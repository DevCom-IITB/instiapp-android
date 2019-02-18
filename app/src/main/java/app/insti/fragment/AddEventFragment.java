package app.insti.fragment;

import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.google.gson.Gson;

import app.insti.Constants;
import app.insti.R;
import app.insti.Utils;
import app.insti.api.EmptyCallback;
import app.insti.api.RetrofitInterface;
import app.insti.api.model.Body;
import app.insti.api.model.Event;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class AddEventFragment extends BaseFragment {
    public ValueCallback<Uri[]> uploadMessage;
    private ProgressDialog progressDialog;
    private String query = "";

    public AddEventFragment() {
        // Required empty public constructor
    }

    public AddEventFragment withDate(String date) {
        query += "&date=" + date;
        return this;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        container.removeAllViews();
        View view = inflater.inflate(R.layout.fragment_add_event, container, false);

        /* Show progress dialog */
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Loading");
        progressDialog.setCancelable(false);
        progressDialog.show();

        String host = "insti.app";
        Toolbar toolbar = getActivity().findViewById(R.id.toolbar);
        toolbar.setTitle(getArguments().containsKey("id") ? "Update Event" : "Add Event");

        if (savedInstanceState == null) {
            WebView webView = view.findViewById(R.id.add_event_webview);
            webView.getSettings().setBuiltInZoomControls(true);
            webView.getSettings().setDisplayZoomControls(false);
            webView.getSettings().setAllowFileAccess(true);
            WebSettings webSettings = webView.getSettings();
            webSettings.setJavaScriptEnabled(true);
            webSettings.setDomStorageEnabled(true);

            webView.setWebChromeClient(new MyWebChromeClient());
            webView.setWebViewClient(new MyWebViewClient());

            CookieManager cookieManager = CookieManager.getInstance();
            String cookieString = Utils.getSessionIDHeader();
            cookieManager.setCookie(host, cookieString);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
                CookieManager.getInstance().flush();
            } else {
                CookieSyncManager.getInstance().sync();
            }

            String url = "https://" + host + "/add-event?sandbox=true";
            if (getArguments().containsKey("id")) {
                url = "https://" + host + "/edit-event/" + getArguments().getString("id") + "?sandbox=true";
            } else if (getArguments().containsKey("bodyId")) {
                url = "https://" + host + "/edit-body/" + getArguments().getString("bodyId") + "?sandbox=true";
                toolbar.setTitle("Update Organization");
            }

            url += query;

            webView.loadUrl(url);

            webView.setOnTouchListener(new View.OnTouchListener() {
                float m_downX;

                public boolean onTouch(View v, MotionEvent event) {

                    if (event.getPointerCount() > 1) {
                        //Multi touch detected
                        return true;
                    }

                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN: {
                            // save the x
                            m_downX = event.getX();
                            break;
                        }
                        case MotionEvent.ACTION_MOVE:
                        case MotionEvent.ACTION_CANCEL:
                        case MotionEvent.ACTION_UP: {
                            // set x so that it doesn't move
                            event.setLocation(m_downX, event.getY());
                            break;
                        }

                    }
                    return false;
                }
            });
        }

        return view;
    }

    private void openEvent(Event event) {
        Utils.eventCache.updateCache(event);
        String eventJson = new Gson().toJson(event);
        Bundle bundle = getArguments();
        if (bundle == null)
            bundle = new Bundle();
        bundle.putString(Constants.EVENT_JSON, eventJson);
        EventFragment eventFragment = new EventFragment();
        eventFragment.setArguments(bundle);
        FragmentManager manager = getActivity().getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.setCustomAnimations(R.anim.slide_in_up, R.anim.fade_out, R.anim.fade_in, R.anim.slide_out_down);
        transaction.replace(R.id.framelayout_for_fragment, eventFragment, eventFragment.getTag());
        transaction.addToBackStack(eventFragment.getTag()).commit();
    }

    private void openBody(Body body) {
        Utils.bodyCache.invalidateCache(body);
        BodyFragment bodyFragment = BodyFragment.newInstance(body);
        FragmentManager manager = getActivity().getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.setCustomAnimations(R.anim.slide_in_up, R.anim.fade_out, R.anim.fade_in, R.anim.slide_out_down);
        transaction.replace(R.id.framelayout_for_fragment, bodyFragment, bodyFragment.getTag());
        transaction.addToBackStack(bodyFragment.getTag()).commit();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 101) {
            if (uploadMessage == null) return;
            uploadMessage.onReceiveValue(WebChromeClient.FileChooserParams.parseResult(resultCode, data));
            uploadMessage = null;
        }
    }

    public class MyWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            /* Check URL */
            if (url.contains("/event/")) {
                url = url.substring(url.lastIndexOf("/") + 1);

                RetrofitInterface retrofitInterface = Utils.getRetrofitInterface();
                retrofitInterface.getEvent(Utils.getSessionIDHeader(), url).enqueue(new Callback<Event>() {
                    @Override
                    public void onResponse(Call<Event> call, Response<Event> response) {
                        if (response.isSuccessful()) {
                            openEvent(response.body());
                        }
                    }

                    @Override
                    public void onFailure(Call<Event> call, Throwable t) {
                    }
                });

                return true;
            } else if (url.contains("/org/")) {
                url = url.substring(url.lastIndexOf("/") + 1);

                RetrofitInterface retrofitInterface = Utils.getRetrofitInterface();
                retrofitInterface.getBody(Utils.getSessionIDHeader(), url).enqueue(new EmptyCallback<Body>() {
                    @Override
                    public void onResponse(Call<Body> call, Response<Body> response) {
                        if (response.isSuccessful()) {
                            openBody(response.body());
                        }
                    }
                });

                return true;
            }
            // return true; //Indicates WebView to NOT load the url;
            return false; //Allow WebView to load url
        }
    }

    public class MyWebChromeClient extends WebChromeClient {

        @Override
        public void onProgressChanged(WebView view, int progress) {
            if (progress < 100) {
                progressDialog.show();
            }
            if (progress == 100) {
                progressDialog.dismiss();
            }
        }

        @Override
        public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams) {
            // make sure there is no existing message
            if (uploadMessage != null) {
                uploadMessage.onReceiveValue(null);
                uploadMessage = null;
            }

            uploadMessage = filePathCallback;

            Intent intent = fileChooserParams.createIntent();
            try {
                startActivityForResult(intent, 101);
            } catch (ActivityNotFoundException e) {
                uploadMessage = null;
                Toast.makeText(getContext(), "Cannot open file chooser", Toast.LENGTH_LONG).show();
                return false;
            }

            return true;
        }
    }
}
