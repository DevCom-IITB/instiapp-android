package in.ac.iitb.gymkhana.iitbapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;

import in.ac.iitb.gymkhana.iitbapp.data.User;

public class SessionManager {
    private static final String PREF_NAME = "LoggedInPref";
    private static final String IS_LOGGED_IN = "IsLoggedIn";
    private static final String GCM_ID = "GcmId";
    public static final String CURRENT_USER = "current_user";
    SharedPreferences pref;
    Editor editor;
    Context context;
    int PRIVATE_MODE = 0;

    public SessionManager(Context context) {
        this.context = context;
        pref = context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public void checkLogin() {
        if (!this.isLoggedIn()) {
            Intent i = new Intent(context, LoginActivity.class);
            // Closing all the Activities
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            // Add new Flag to start new Activity
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            // Staring Login Activity
            context.startActivity(i);
        }
    }

    public void createLoginSession(String gcmId, User currentUser) {
        Log.d("SessionManager", "GcmId being stored");
        editor.putBoolean(IS_LOGGED_IN, true);
        editor.putString(GCM_ID, gcmId);
        editor.putString(CURRENT_USER, currentUser.toString());
        editor.commit();
    }

    public boolean isLoggedIn() {
        return pref.getBoolean(IS_LOGGED_IN, false);
    }
}
