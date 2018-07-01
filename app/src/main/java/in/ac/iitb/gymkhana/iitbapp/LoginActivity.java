package in.ac.iitb.gymkhana.iitbapp;


import android.annotation.TargetApi;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import net.openid.appauth.AuthorizationException;
import net.openid.appauth.AuthorizationRequest;
import net.openid.appauth.AuthorizationResponse;
import net.openid.appauth.AuthorizationService;
import net.openid.appauth.AuthorizationServiceConfiguration;

import in.ac.iitb.gymkhana.iitbapp.api.RetrofitInterface;
import in.ac.iitb.gymkhana.iitbapp.api.ServiceGenerator;
import in.ac.iitb.gymkhana.iitbapp.api.model.LoginResponse;
import in.ac.iitb.gymkhana.iitbapp.gcm.RegistrationIntentService;
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
    }

    private class WvClient extends WebViewClient
    {
        @Override
        public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError er) {
            handler.proceed();
        }

        @Override
        public boolean shouldOverrideUrlLoading(final WebView view, final String url) {
            /* Capture redirect */
            if (url.startsWith(redirectUri)) {
                /* Show progress dialog */
                progressDialog = new ProgressDialog(LoginActivity.this);
                progressDialog.setMessage("Logging In");
                progressDialog.setCancelable(false);
                progressDialog.setIndeterminate(true);
                progressDialog.show();

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

            /* Load URL */
            view.loadUrl(url);
            return false;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        WebView webview = (WebView)  findViewById(R.id.login_webview);
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

}

