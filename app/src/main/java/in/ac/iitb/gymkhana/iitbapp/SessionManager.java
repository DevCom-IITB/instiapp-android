package in.ac.iitb.gymkhana.iitbapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;

import in.ac.iitb.gymkhana.iitbapp.data.User;

public class SessionManager {
    SharedPreferences pref;
    Editor editor;
    Context context;
    int PRIVATE_MODE = 0;

    public SessionManager(Context context) {
        this.context = context;
        pref = context.getSharedPreferences(Constants.PREF_NAME, PRIVATE_MODE);
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

    public void createLoginSession(String gcmId, User currentUser, String sessionID) {
        Log.d("SessionManager", "GcmId being stored");
        editor = pref.edit();
        editor.putBoolean(Constants.IS_LOGGED_IN, true);
        editor.putString(Constants.GCM_ID, gcmId);
        editor.putString(Constants.CURRENT_USER, currentUser.toString());
        editor.putString(Constants.SESSION_ID, sessionID);
        editor.commit();
    }

    public String getSessionID() {
        return pref.getString(Constants.SESSION_ID, "");
    }

    public boolean isLoggedIn() {
        return pref.getBoolean(Constants.IS_LOGGED_IN, false);
    }

    public void logout() {
        editor = pref.edit();
        editor.clear().commit();
    }
}
