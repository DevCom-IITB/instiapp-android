package app.insti.activity;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.browser.customtabs.CustomTabsIntent;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import app.insti.Constants;
import app.insti.R;
import app.insti.SessionManager;
import app.insti.api.EmptyCallback;
import app.insti.api.RetrofitInterface;
import app.insti.api.ServiceGenerator;
import app.insti.api.response.LoginResponse;
import retrofit2.Call;
import retrofit2.Response;


public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";
    private final String redirectUri = "https://www.insti.app/login-android.html";
    private final String guestUri = "https://guesturi";
    private final String loginUri = "https://loginuri";
    public String authCode = null;
    public String fcmId = null;
    private SessionManager session;
    private Context mContext = this;
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
        } else {
            setContentView(R.layout.activity_login);
            progressDialog = new ProgressDialog(LoginActivity.this);
        }
    }

    private void openMainActivity() {
        Intent i = new Intent(mContext, MainActivity.class);

        /* Pass FCM data if available */
        Intent myIntent = getIntent();
        if (myIntent.getExtras() != null) {
            i.putExtra(Constants.MAIN_INTENT_EXTRAS, myIntent.getExtras());
        }

        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);
        finish();
    }

    @Override
    protected void onStart() {
        super.onStart();

        // Initialize
        ServiceGenerator serviceGenerator = new ServiceGenerator(getApplicationContext());
        this.retrofitInterface = serviceGenerator.getRetrofitInterface();

        // Get FCM Id
        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(new OnSuccessListener<InstanceIdResult>() {
            @Override
            public void onSuccess(InstanceIdResult instanceIdResult) {
                fcmId = instanceIdResult.getToken();
            }
        });

        // Login if intent is present
        String action = getIntent().getAction();
        String data = getIntent().getDataString();
        if (Intent.ACTION_VIEW.equals(action) && data != null) {
            Uri query = Uri.parse(data);
            authCode = query.getQueryParameter("code");

            if (authCode != null) {
                /* Show progress dialog */
                progressDialog.setMessage("Logging In");
                progressDialog.setCancelable(false);
                progressDialog.setIndeterminate(true);
                if (!progressDialog.isShowing()) {
                    progressDialog.show();
                }
                /* Perform the login */
                login(authCode, redirectUri);
            } else {
                Toast.makeText(this, "Login Failed!", Toast.LENGTH_SHORT).show();
            }
        }

        // Setup web view placeholder
        WebView webview = findViewById(R.id.login_webview);
        webview.setWebViewClient(new WvClient());
        webview.loadUrl("file:///android_asset/login.html");
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

        call.enqueue(new EmptyCallback<LoginResponse>() {
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
        });
    }

    private class WvClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(final WebView view, final String url) {
            /* Actual login button */
            if (url.startsWith(loginUri)) {
                CustomTabsIntent customTabsIntent = new CustomTabsIntent.Builder()
                        .addDefaultShareMenuItem()
                        .setToolbarColor(LoginActivity.this.getResources()
                                .getColor(R.color.colorPrimary))
                        .setShowTitle(true)
                        .build();
                customTabsIntent.launchUrl(LoginActivity.this, Uri.parse("https://gymkhana.iitb.ac.in/sso/oauth/authorize/?client_id=vR1pU7wXWyve1rUkg0fMS6StL1Kr6paoSmRIiLXJ&response_type=code&scope=basic%20profile%20picture%20sex%20ldap%20phone%20insti_address%20program%20secondary_emails&redirect_uri=https://www.insti.app/login-android.html&approval_prompt=force"));
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
}

