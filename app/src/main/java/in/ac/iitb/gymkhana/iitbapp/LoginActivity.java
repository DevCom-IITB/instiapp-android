package in.ac.iitb.gymkhana.iitbapp;


import android.annotation.TargetApi;
import android.app.PendingIntent;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.gson.annotations.SerializedName;

import net.openid.appauth.AuthorizationException;
import net.openid.appauth.AuthorizationRequest;
import net.openid.appauth.AuthorizationResponse;
import net.openid.appauth.AuthorizationService;
import net.openid.appauth.AuthorizationServiceConfiguration;

import in.ac.iitb.gymkhana.iitbapp.api.RetrofitInterface;
import in.ac.iitb.gymkhana.iitbapp.api.ServiceGenerator;
import in.ac.iitb.gymkhana.iitbapp.api.model.LoginRequest;
import in.ac.iitb.gymkhana.iitbapp.api.model.LoginResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";
    private final String clientId = "pFcDDWtUUfzlAX2ibriV25lm1J2m92O5ynfT4SYk";
    private final String clientSecret = "k56GXiN1qB4Dt7CnTVWjuwLJyWntNulitWOkL7Wddr6JHPiHqIZgSfgUplO6neTqumVr32zA14XgQmkuoC8y6y9jnaQT9tKDsq4jQklRb8MQNQglQ1H4YrmqOwPfaNyO";
    private final Uri redirectUri = Uri.parse("https://redirecturi");
    private final Uri mAuthEndpoint = Uri.parse("http://gymkhana.iitb.ac.in/sso/oauth/authorize/");
    private final Uri mTokenEndpoint = Uri.parse("http://gymkhana.iitb.ac.in/sso/oauth/token/");
    private AuthorizationService mAuthService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mAuthService = new AuthorizationService(this);

        Button ldapLogin = (Button) findViewById(R.id.ldap_login);
        ldapLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "Initiating auth");

                AuthorizationServiceConfiguration config =
                        new AuthorizationServiceConfiguration(mAuthEndpoint, mTokenEndpoint);

                makeAuthRequest(config);
            }
        });
        Button guestLogin = (Button) findViewById(R.id.guest_login);
        guestLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO This is for debug purposes, change once SSO is implemented
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

    }

    @Override
    protected void onNewIntent(Intent intent) {

        checkIntent(intent);
    }

    private void checkIntent(@Nullable Intent intent) {
        if (intent != null) {
            Log.d(TAG, "Intent Received");
            String action = intent.getAction();
            switch (action) {
                case "HANDLE_AUTHORIZATION_RESPONSE": {
                    handleAuthorizationResponse(intent);

                }
                break;
                default:
                    Log.d(TAG, intent.getAction());

            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        checkIntent(getIntent());
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "In Resume");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mAuthService.dispose();
    }

    private void handleAuthorizationResponse(@NonNull Intent intent) {
        AuthorizationResponse response = AuthorizationResponse.fromIntent(intent);
        AuthorizationException error = AuthorizationException.fromIntent(intent);

        if (response != null) {
            Log.d(TAG, "Received AuthorizationResponse: " + "AuthCode: " + response.authorizationCode);
            Toast.makeText(this,
                    "AuthCode: " + response.authorizationCode, Toast.LENGTH_SHORT)
                    .show();

//            TODO: Replace gcmId
            login(response.authorizationCode, "xyz");
        } else {
            Log.i(TAG, "Authorization failed: " + error.getMessage());
            Toast.makeText(this,
                    "Authorization failed", Toast.LENGTH_LONG)
                    .show();
        }
    }

    private void makeAuthRequest(
            @NonNull AuthorizationServiceConfiguration serviceConfig) {

        AuthorizationRequest authRequest = new AuthorizationRequest.Builder(
                serviceConfig,
                clientId,
                "code",
                redirectUri)
                .setScope("profile")
                .build();

        Log.d(TAG, "Making auth request");
        String action = "HANDLE_AUTHORIZATION_RESPONSE";
        Intent postAuthorizationIntent = new Intent(action);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, authRequest.hashCode(), postAuthorizationIntent, 0);

        mAuthService.performAuthorizationRequest(
                authRequest,
                pendingIntent,
                mAuthService.createCustomTabsIntentBuilder()
                        .setToolbarColor(getCustomTabColor())
                        .build());
    }

    //Todo: Change the color of Chrome custom tabs based on app theme color
    @TargetApi(Build.VERSION_CODES.M)
    @SuppressWarnings("deprecation")
    private int getCustomTabColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return getColor(R.color.colorPrimaryDark);
        } else {
            return getResources().getColor(R.color.colorPrimaryDark);
        }
    }

    private void login(String authorizationCode, String gcmId) {
        LoginRequest loginRequest = new LoginRequest(authorizationCode, gcmId);
        RetrofitInterface retrofitInterface = ServiceGenerator.createService(RetrofitInterface.class);
        retrofitInterface.login(loginRequest).enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if (response.isSuccessful()) {
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
}

