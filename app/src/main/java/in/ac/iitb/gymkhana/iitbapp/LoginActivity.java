package in.ac.iitb.gymkhana.iitbapp;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.webkit.SslErrorHandler;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import in.ac.iitb.gymkhana.iitbapp.api.RetrofitInterface;
import in.ac.iitb.gymkhana.iitbapp.api.ServiceGenerator;
import in.ac.iitb.gymkhana.iitbapp.api.model.LoginResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private final String redirectUri = "https://redirecturi";
    private final String guestUri = "https://guesturi";
    public String authCode = null;
    SessionManager session;
    Context mContext = this;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        session = new SessionManager(mContext);
        setContentView(R.layout.activity_login);
        progressDialog = new ProgressDialog(LoginActivity.this);
    }

    @Override
    protected void onStart() {
        super.onStart();

        WebView webview = (WebView) findViewById(R.id.login_webview);
        webview.getSettings().setJavaScriptEnabled(true);
        webview.getSettings().setDomStorageEnabled(true);
        webview.setWebViewClient(new WvClient());
        webview.loadUrl("file:///android_asset/login.html");
    }

    private void login(String authorizationCode, final String redirectURI, String gcmID) {
        RetrofitInterface retrofitInterface = ServiceGenerator.createService(RetrofitInterface.class);
        retrofitInterface.login(authorizationCode, redirectURI, gcmID).enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if (response.isSuccessful()) {
                    Log.d(TAG, "Login request successful");
                    session.createLoginSession(redirectURI, response.body().getUser(), response.body().getSessionID());
                    Intent i = new Intent(mContext, MainActivity.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(i);
                    progressDialog.dismiss();
                    //Save credentials in AccountManager to keep user logged in
                    //Go to MainActivity
                }
                //Server error
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                //Network Error
            }
        });
    }

    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST)
                        .show();
            } else {
                Log.i(TAG, "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }

    private class WvClient extends WebViewClient {
        @Override
        public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError er) {
            handler.proceed();
        }

        @Override
        public boolean shouldOverrideUrlLoading(final WebView view, final String url) {
            /* Capture redirect */
            if (url.startsWith(redirectUri)) {
                /* Show progress dialog */
                progressDialog.setMessage("Logging In");
                progressDialog.setCancelable(false);
                progressDialog.setIndeterminate(true);
                if (!progressDialog.isShowing()) {
                    progressDialog.show();
                }

                /* Get auth code from query */
                String query = Uri.parse(url).getQuery();
                authCode = query.substring(query.lastIndexOf("=") + 1);
                login(authCode, redirectUri, authCode);
                return true;
            }

            /* Guest Login */
            if (url.startsWith(guestUri)) {
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
                return true;
            }

            if (!progressDialog.isShowing()) {
                progressDialog.setMessage("Loading");
                progressDialog.setCancelable(false);
                progressDialog.setIndeterminate(true);
                progressDialog.show();
            }
            /* Load URL */
            view.loadUrl(url);
            return false;
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            if (progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
        }
    }

}

