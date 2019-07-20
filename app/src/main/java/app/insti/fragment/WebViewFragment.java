package app.insti.fragment;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SurfaceView;
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

import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.MultiProcessor;
import com.google.android.gms.vision.Tracker;
import com.google.gson.Gson;

import app.insti.Constants;
import app.insti.R;
import app.insti.Utils;
import app.insti.activity.MainActivity;
import app.insti.api.EmptyCallback;
import app.insti.api.RetrofitInterface;
import app.insti.api.model.Body;
import app.insti.api.model.Event;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import java.io.IOException;


public class WebViewFragment extends BaseFragment {
    public ValueCallback<Uri[]> uploadMessage;
    private ProgressDialog progressDialog;
    private String query = "";

    public WebViewFragment() {
        // Required empty public constructor
    }

    private final String host = "insti.app";
    private CameraSource mCameraSource;

    public WebViewFragment withDate(String date) {
        query += "&date=" + date;
        return this;
    }

    private String chooseUrl(Bundle args) {
        setTitle("InstiApp");

        // Construct basic URL
        String url = "https://" + host;

        // Check for type
        if (!args.containsKey(Constants.WV_TYPE)) {
            return url;
        }

        // Check for arguments
        final String type = args.getString(Constants.WV_TYPE);
        String ID = args.getString(Constants.WV_ID);
        if (ID == null) { ID = ""; }

        switch (type) {
            case Constants.WV_TYPE_ADD_EVENT:
                url += "/add-event/";
                setTitle("Add Event");
                break;

            case Constants.WV_TYPE_UPDATE_EVENT:
                url += "/edit-event/" + ID;
                setTitle("Update Event");
                break;

            case Constants.WV_TYPE_UPDATE_BODY:
                url += "/edit-body/" + ID;
                setTitle("Update Organization");
                break;

            case Constants.WV_TYPE_ACHIEVEMENTS:
                initQRButton();
                url += "/achievements";
                setTitle("Achievements");
                break;

            case Constants.WV_TYPE_NEW_OFFERED_ACHIEVEMENT:
                initQRButton();
                url += "/achievement-new/" + ID;
                setTitle("Achievements");
                break;

            case Constants.WV_TYPE_URL:
                return args.getString(Constants.WV_URL);
        }

        return url + "?sandbox=true";
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onPause() {
        if (mCameraSource != null) {
            SurfaceView surfaceView = getView().findViewById(R.id.qr_camera_surfaceview);
            mCameraSource.stop();
            surfaceView.setVisibility(View.GONE);
        }
        super.onPause();
    }

    @Override
    public void onStop() {
        if (mCameraSource != null) {
            mCameraSource.release();
        }
        super.onStop();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        container.removeAllViews();
        View view = inflater.inflate(R.layout.fragment_webview, container, false);

        /* Show progress dialog */
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Loading");
        progressDialog.setCancelable(false);
        progressDialog.show();

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

            webView.loadUrl(chooseUrl(getArguments()) + query);

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

    private boolean isValidQR(String code) {
        return code.startsWith("https://" + host);
    }

    private void handleQR(String code) {
        if (getView() == null || getActivity() == null) return;

        WebView webView = getView().findViewById(R.id.add_event_webview);
        code += code.contains("?") ? "&" : "?";
        webView.loadUrl(code + "sandbox=true");
    }

    private void setTitle(String title) {
        Toolbar toolbar = getActivity().findViewById(R.id.toolbar);
        if (toolbar != null) {
            toolbar.setTitle(title);
        }
    }

    private void initQRButton() {
        setHasOptionsMenu(true);
    }

    class BarcodeTrackerFactory implements MultiProcessor.Factory<Barcode> {
        @Override
        public Tracker<Barcode> create(Barcode barcode) {
            return new MyBarcodeTracker();
        }
    }

    class MyBarcodeTracker extends Tracker<Barcode> {
        @Override
        public void onUpdate(Detector.Detections<Barcode> detectionResults, Barcode barcode) {
            if (isValidQR(barcode.displayValue)) {
                getActivity().runOnUiThread(new Runnable() {
                    public void run() {
                        mCameraSource.stop();
                        SurfaceView surfaceView = getView().findViewById(R.id.qr_camera_surfaceview);
                        surfaceView.setVisibility(View.GONE);
                        handleQR(barcode.displayValue);
                    }
                });
            }
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.qr_scan_menu, menu);
        MenuItem item = menu.findItem(R.id.action_qr_scan);
        item.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                BarcodeDetector barcodeDetector = new BarcodeDetector.Builder(getContext())
                        .setBarcodeFormats(256).build();
                BarcodeTrackerFactory barcodeFactory = new BarcodeTrackerFactory();
                barcodeDetector.setProcessor(
                        new MultiProcessor.Builder<>(barcodeFactory).build());

                mCameraSource = new CameraSource.Builder(getContext(), barcodeDetector)
                        .setFacing(CameraSource.CAMERA_FACING_BACK)
                        .setRequestedPreviewSize(720, 1280)
                        .setAutoFocusEnabled(true)
                        .build();
                SurfaceView surfaceView = getView().findViewById(R.id.qr_camera_surfaceview);
                if (surfaceView == null) {
                    return false;
                }

                if (ActivityCompat.checkSelfPermission(
                        getActivity().getApplicationContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {

                    Toast.makeText(getContext(), "Please grant camera permission!", Toast.LENGTH_SHORT).show();
                    ActivityCompat.requestPermissions(getActivity(),
                            new String[]{Manifest.permission.CAMERA}, 787);

                    return false;
                }

                surfaceView.setVisibility(View.VISIBLE);

                /* Give surface view time to become visible */
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (ActivityCompat.checkSelfPermission(
                                getActivity().getApplicationContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                            return;
                        }

                        try {
                            mCameraSource.start(surfaceView.getHolder());
                        } catch (IOException ignored) {
                            surfaceView.setVisibility(View.GONE);
                        }
                    }
                }, 500);

                return false;
            }
        });
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
