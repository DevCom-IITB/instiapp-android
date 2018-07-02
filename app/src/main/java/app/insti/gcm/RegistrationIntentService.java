package app.insti.gcm;


import android.annotation.TargetApi;
import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;

import app.insti.Constants;


@TargetApi(Build.VERSION_CODES.CUPCAKE)
public class RegistrationIntentService extends IntentService {
    private static final String TAG = "RegIntentService";


    @RequiresApi(api = Build.VERSION_CODES.CUPCAKE)
    public RegistrationIntentService() {
        super(TAG);
    }

    @RequiresApi(api = Build.VERSION_CODES.GINGERBREAD)
    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        String token = null;
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        try {
            InstanceID instanceID = InstanceID.getInstance(this);
            token = instanceID.getToken("306601329049",
                    GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
            Log.i(TAG, "GCM Registration Token: " + token);
            Toast.makeText(this, "GCM Registration Token: " + token, Toast.LENGTH_SHORT).show();


            sharedPreferences.edit().putBoolean(Constants.SENT_TOKEN_TO_SERVER, true).apply();

        } catch (Exception e) {
            Log.d(TAG, "Failed to complete token refresh", e);
            sharedPreferences.edit().putBoolean(Constants.SENT_TOKEN_TO_SERVER, false).apply();
        }

        //Notify UI that registration is complete
        Intent registrationComplete = new Intent(Constants.REGISTRATION_COMPLETE);
        registrationComplete.putExtra("Token", token);
        LocalBroadcastManager.getInstance(this).sendBroadcast(registrationComplete);
    }


}
