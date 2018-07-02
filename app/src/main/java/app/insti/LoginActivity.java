package app.insti;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import app.insti.api.RetrofitInterface;
import app.insti.api.ServiceGenerator;
import app.insti.api.model.LoginResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
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

        final Button loginButton = findViewById(R.id.login_button);
        final EditText usernameEditText = findViewById(R.id.login_username);
        final EditText passwordEditText = findViewById(R.id.login_password);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!progressDialog.isShowing()) {
                    progressDialog.setMessage("Logging In");
                    progressDialog.setCancelable(false);
                    progressDialog.setIndeterminate(true);
                    progressDialog.show();
                }
                login(usernameEditText.getText().toString(), passwordEditText.getText().toString());
            }
        });

        passwordEditText.setImeOptions(EditorInfo.IME_ACTION_DONE);
        passwordEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    loginButton.performClick();
                }
                return false;
            }
        });

        final TextView guestView = findViewById(R.id.login_guest);
        guestView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }

    private void login(final String username, final String password) {
        RetrofitInterface retrofitInterface = ServiceGenerator.createService(RetrofitInterface.class);
        retrofitInterface.passwordLogin(username, password).enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if (response.isSuccessful()) {
                    Log.d(TAG, "Login request successful");
                    session.createLoginSession(username, response.body().getUser(), response.body().getSessionID());
                    Intent i = new Intent(mContext, MainActivity.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(i);
                    progressDialog.dismiss();
                    //Save credentials in AccountManager to keep user logged in
                    //Go to MainActivity
                } else {
                    Toast.makeText(LoginActivity.this, "Authorization Failed!", Toast.LENGTH_LONG).show();
                    progressDialog.dismiss();
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

