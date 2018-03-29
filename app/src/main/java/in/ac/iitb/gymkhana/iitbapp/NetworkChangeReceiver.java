package in.ac.iitb.gymkhana.iitbapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import in.ac.iitb.gymkhana.iitbapp.api.RetrofitInterface;
import in.ac.iitb.gymkhana.iitbapp.api.ServiceGenerator;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static in.ac.iitb.gymkhana.iitbapp.SessionManager.SESSION_ID;

public class NetworkChangeReceiver extends BroadcastReceiver {


        @Override
        public void onReceive(final Context context, final Intent intent) {
            final SessionManager sessionManager = new SessionManager(context);
            if (!sessionManager.pref.getBoolean(sessionManager.IF_UPDATED, false)) {
                RetrofitInterface retrofitInterface = ServiceGenerator.createService(RetrofitInterface.class);
                final String sessionId = sessionManager.pref.getString(sessionManager.SESSION_ID, null);
                final String eventId = sessionManager.pref.getString(sessionManager.EVENT_ID, null);
                final int finalStatus = sessionManager.pref.getInt(sessionManager.STATUS, 0);
                retrofitInterface.updateUserEventStatus(sessionId, eventId, finalStatus).enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        if (response.isSuccessful()) {
                            sessionManager.setUserEventStatus(sessionId, eventId, finalStatus);
                            sessionManager.setIfUpdated(true);
                            Toast.makeText(context, "UserEventStatus Updated", Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        sessionManager.setUserEventStatus(sessionId, eventId, finalStatus);
                        sessionManager.setIfUpdated(false);
                        Toast.makeText(context, "Network Error", Toast.LENGTH_LONG).show();
                    }
                });
            }
        }
    }
