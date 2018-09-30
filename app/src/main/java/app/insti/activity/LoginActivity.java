package app.insti.activity;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import app.insti.R;
import app.insti.SessionManager;
import app.insti.api.RetrofitInterface;
import app.insti.api.ServiceGenerator;
import app.insti.api.model.LoginResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private final String redirectUri = "https://redirecturi";
    private final String guestUri = "https://guesturi";
    public String authCode = null;
    public String fcmId = null;
    SessionManager session;
    Context mContext = this;
    private boolean loggingIn = false;
    private ProgressDialog progressDialog;

    private RetrofitInterface retrofitInterface;
    public RetrofitInterface getRetrofitInterface() {
        return retrofitInterface;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        session = new SessionManager(mContext);
        if (session.isLoggedIn()) {
            openMainActivity();
        }
        setContentView(R.layout.activity_login);
        progressDialog = new ProgressDialog(LoginActivity.this);
    }

    private void openMainActivity() {
        Intent i = new Intent(mContext, MainActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);
        finish();
    }

    @Override
    protected void onStart() {
        super.onStart();

        ServiceGenerator serviceGenerator = new ServiceGenerator(getApplicationContext());
        this.retrofitInterface = serviceGenerator.getRetrofitInterface();

        WebView webview = (WebView) findViewById(R.id.login_webview);
        webview.getSettings().setJavaScriptEnabled(true);
        webview.getSettings().setDomStorageEnabled(true);
        webview.setWebViewClient(new WvClient());
        webview.loadUrl("file:///android_asset/login.html");

        // Get FCM Id
        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(new OnSuccessListener<InstanceIdResult>() {
            @Override
            public void onSuccess(InstanceIdResult instanceIdResult) {
                fcmId = instanceIdResult.getToken();
            }
        });
    }

    private void login(final String authorizationCode, final String redirectURL) {
        /* This can be null if play services is hung */
        RetrofitInterface retrofitInterface = getRetrofitInterface();
        Call<LoginResponse> call;
        if (fcmId == null) {
            call = retrofitInterface.login(authorizationCode, redirectURL);
        } else {
            call = retrofitInterface.login(authorizationCode, redirectURL, fcmId);
        }

        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if (response.isSuccessful()) {
                    session.createLoginSession(response.body().getUser().getUserName(), response.body().getUser(), response.body().getSessionID());
                    progressDialog.dismiss();
                    openMainActivity();
                    finish();
                } else {
                    Toast.makeText(LoginActivity.this, "Authorization Failed!", Toast.LENGTH_LONG).show();
                    progressDialog.dismiss();
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                //Network Error
            }
        });
    }

    @Deprecated
    private void passLogin(final String username, final String password) {
        if (!progressDialog.isShowing()) {
            progressDialog.setMessage("Logging In");
            progressDialog.setCancelable(false);
            progressDialog.setIndeterminate(true);
            progressDialog.show();
        }

        RetrofitInterface retrofitInterface = getRetrofitInterface();
        Call<LoginResponse> call;

        /* This can be null if play services is hung */
        if (fcmId == null) {
            call = retrofitInterface.passwordLogin(username, password);
        } else {
            call = retrofitInterface.passwordLogin(username, password, fcmId);
        }

        /* Log in the user */
        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                loggingIn = false;
                if (response.isSuccessful()) {
                    Log.d(TAG, "Login request successful");
                    session.createLoginSession(username, response.body().getUser(), response.body().getSessionID());
                    progressDialog.dismiss();
                    openMainActivity();
                    finish();
                } else {
                    Toast.makeText(LoginActivity.this, "Authorization Failed!", Toast.LENGTH_LONG).show();
                    progressDialog.dismiss();
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                loggingIn = false;
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
                loggingIn = true;

                /* Get auth code from query */
                String query = Uri.parse(url).getQuery();
                authCode = query.substring(query.lastIndexOf("=") + 1);
                login(authCode, redirectUri);
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
            if (progressDialog.isShowing() && !loggingIn) {
                progressDialog.dismiss();
            }
        }
    }
}

