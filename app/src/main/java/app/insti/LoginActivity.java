package app.insti;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
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
import com.google.firebase.iid.FirebaseInstanceId;

import app.insti.api.RetrofitInterface;
import app.insti.api.ServiceGenerator;
import app.insti.api.model.LoginResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    public static final String USERNAME_ERROR = "Username cannot be blank";
    public static final String PASSWORD_ERROR = "Password cannot be blank";
    SessionManager session;
    Context mContext = this;
    private ProgressDialog progressDialog;

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

        final Button loginButton = findViewById(R.id.login_button);
        final EditText usernameEditText = findViewById(R.id.login_username);
        final EditText passwordEditText = findViewById(R.id.login_password);
        final TextInputLayout usernameLayout = findViewById(R.id.login_username_layout);
        final TextInputLayout passwordLayout = findViewById(R.id.login_password_layout);

        usernameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (usernameEditText.getText().toString().equals("")) {
                    usernameLayout.setError(USERNAME_ERROR);
                } else {
                    usernameLayout.setError(null);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        passwordEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (passwordEditText.getText().toString().equals("")) {
                    passwordLayout.setError(PASSWORD_ERROR);
                } else {
                    passwordLayout.setError(null);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = usernameEditText.getText().toString();
                String password = passwordEditText.getText().toString();
                passwordEditText.setText("");
                passwordLayout.setError(null);
                if (username.equals("")) {
                    usernameLayout.setError(USERNAME_ERROR);
                    return;
                }
                if (password.equals("")) {
                    passwordLayout.setError(PASSWORD_ERROR);
                    return;
                }
                login(username, password);
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
                openMainActivity();
            }
        });
    }

    private void login(final String username, final String password) {
        if (!progressDialog.isShowing()) {
            progressDialog.setMessage("Logging In");
            progressDialog.setCancelable(false);
            progressDialog.setIndeterminate(true);
            progressDialog.show();
        }

        RetrofitInterface retrofitInterface = ServiceGenerator.createService(RetrofitInterface.class);
        Call<LoginResponse> call;

        /* This can be null if play services is hung */
        if (FirebaseInstanceId.getInstance().getToken() == null) {
            call = retrofitInterface.passwordLogin(username, password);
        } else {
            call = retrofitInterface.passwordLogin(username, password, FirebaseInstanceId.getInstance().getToken());
        }

        /* Log in the user */
        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if (response.isSuccessful()) {
                    Log.d(TAG, "Login request successful");
                    session.createLoginSession(username, response.body().getUser(), response.body().getSessionID());
                    progressDialog.dismiss();
                    openMainActivity();
                    finish();
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

