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
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
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
    private static final String CLIENT_ID = "vR1pU7wXWyve1rUkg0fMS6StL1Kr6paoSmRIiLXJ";
    private final Uri redirectUri = Uri.parse("https://redirecturi");
    private final Uri mAuthEndpoint = Uri.parse("https://temp-iitb.radialapps.com/content/login.html");
    private final Uri mTokenEndpoint = Uri.parse("http://gymkhana.iitb.ac.in/sso/oauth/token/");
    public String authCode = null;
    SessionManager session;
    Context mContext = this;
    private AuthorizationService mAuthService;
    private BroadcastReceiver mRegistrationBroadcastReceiver;
    private boolean isReceiverRegistered;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        session = new SessionManager(mContext);
        setContentView(R.layout.activity_login);
        mAuthService = new AuthorizationService(this);
        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
                boolean sentToken = sharedPreferences.getBoolean(Constants.SENT_TOKEN_TO_SERVER, false);
                if (sentToken) {
                    String token = intent.getStringExtra("Token");
                    Log.d(TAG, "Going to login with :" + authCode + "\n" + token);
                    //************
                    //TODO Remove following 6 lines after the server is hosted
                    String gcmRegId = token;
//                    session.createLoginSession(gcmRegId);
//                    Intent i = new Intent(mContext, MainActivity.class);
//                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                    startActivity(i);


                    //**************
                    login(authCode, redirectUri.toString(), gcmRegId);

                } else {

                }
            }
        };
        registerReceiver();

        Button ldapLogin = (Button) findViewById(R.id.ldap_login);
        ldapLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "Initiating auth");

                AuthorizationServiceConfiguration config = new AuthorizationServiceConfiguration(mAuthEndpoint, mTokenEndpoint);

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
            if (action != null) {
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
    }

    @Override
    protected void onStart() {
        super.onStart();
        checkIntent(getIntent());
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver();
        Log.d(TAG, "On Resume");
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        isReceiverRegistered = false;
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mAuthService.dispose();
    }

    private void handleAuthorizationResponse(@NonNull Intent intent) {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Logging In");
        progressDialog.setCancelable(false);
        progressDialog.setIndeterminate(true);
        progressDialog.show();
        AuthorizationResponse response = AuthorizationResponse.fromIntent(intent);
        AuthorizationException error = AuthorizationException.fromIntent(intent);

        if (response != null) {
            authCode = response.authorizationCode;
            Log.d(TAG, "Received AuthorizationResponse: " + "AuthCode: " + authCode);
            if (checkPlayServices()) {
                Intent registerIntent = new Intent(this, RegistrationIntentService.class);
                startService(registerIntent);
            }

//

        } else {
            Log.i(TAG, "Authorization failed: " + error.getMessage());
            Toast.makeText(this,
                    "Authorization failed", Toast.LENGTH_LONG)
                    .show();
        }
    }

    private void makeAuthRequest(@NonNull AuthorizationServiceConfiguration serviceConfig) {

        AuthorizationRequest authRequest = new AuthorizationRequest.Builder(
                serviceConfig,
                CLIENT_ID,
                "code",
                redirectUri)
                .setScope("basic profile picture sex ldap phone insti_address program secondary_emails")
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

    //TODO: Change the color of Chrome custom tabs based on app theme color
    @TargetApi(Build.VERSION_CODES.M)
    @SuppressWarnings("deprecation")
    private int getCustomTabColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return getColor(R.color.colorPrimaryDark);
        } else {
            return getResources().getColor(R.color.colorPrimaryDark);
        }
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

    private void registerReceiver() {
        if (!isReceiverRegistered) {
            LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                    new IntentFilter(Constants.REGISTRATION_COMPLETE));
            isReceiverRegistered = true;
        }
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

