package app.insti.notifications;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import app.insti.Constants;
import app.insti.MainActivity;
import app.insti.R;
import app.insti.SessionManager;
import app.insti.api.RetrofitInterface;
import app.insti.api.ServiceGenerator;
import app.insti.data.Event;
import app.insti.data.User;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NotificationIntentService extends IntentService {

    private static final String ACTION_START = "ACTION_START";
    private static final String ACTION_DELETE = "ACTION_DELETE";
    private static final String ACTION_NAVIGATE = "ACTION_NAVIGATE";
    private static final String ACTION_NOT_GOING = "ACTION_NOT_GOING";
    public static final String ACTION_OPEN_EVENT = "ACTION_OPEN_EVENT";
    private static int NOTIFICATION_ID = 1;
    private NotificationManager manager;

    public NotificationIntentService() {
        super(NotificationIntentService.class.getSimpleName());
    }

    @Override
    public void onCreate() {
        super.onCreate();
        startForeground(1, new Notification());
    }

    public static Intent createIntentStartNotificationService(Context context) {
        Intent intent = new Intent(context, NotificationIntentService.class);
        intent.setAction(ACTION_START);
        return intent;
    }

    public static Intent createIntentDeleteNotification(Context context) {
        Intent intent = new Intent(context, NotificationIntentService.class);
        intent.setAction(ACTION_DELETE);
        return intent;
    }

    public static long getDateDiff(Date date1, Date date2, TimeUnit timeUnit) {
        long diffInMillies = date2.getTime() - date1.getTime();
        return timeUnit.convert(diffInMillies, TimeUnit.MILLISECONDS);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d(getClass().getSimpleName(), "onHandleIntent, started handling a notification event");
        String action = intent.getAction();
        if (ACTION_START.equals(action)) {
            processStartNotification();
        }
        if (ACTION_DELETE.equals(action)) {
            processDeleteNotification(intent);
        }
        if (ACTION_NOT_GOING.equals(action)) {
            String eventID = intent.getStringExtra(Constants.EVENT_ID);
            String sessionID = intent.getStringExtra(Constants.SESSION_ID);

            RetrofitInterface retrofitInterface = ServiceGenerator.createService(RetrofitInterface.class);
            retrofitInterface.updateUserEventStatus("sessionid=" + sessionID, eventID, Constants.STATUS_NOT_GOING).enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {

                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {

                }
            });
            manager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
            manager.cancel(intent.getIntExtra("NOTIFICATION_ID", -1));
        }
        if (ACTION_NAVIGATE.equals(action)) {
            manager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
            manager.cancel(intent.getIntExtra("NOTIFICATION_ID", -1));
            double latitude = intent.getDoubleExtra(Constants.EVENT_LATITUDE, 0);
            double longitude = intent.getDoubleExtra(Constants.EVENT_LONGITUDE, 0);
            Uri gmmIntentUri = Uri.parse("google.navigation:q=" + latitude + "," + longitude + "&mode=w");
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
            startActivity(mapIntent);
        }
    }

    private void processDeleteNotification(Intent intent) {
        // Log something?
    }

    private void processStartNotification() {
        SessionManager sessionManager = new SessionManager(this);
        String userID = sessionManager.getUserID();
        final String sessionID = sessionManager.getSessionID();

        RetrofitInterface retrofitInterface = ServiceGenerator.createService(RetrofitInterface.class);
        retrofitInterface.getUser("sessionid=" + sessionID, userID).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful()) {
                    User user = response.body();
                    List<Event> goingEventList = user.getUserGoingEvents();
                    if (goingEventList != null) {
                        for (Event event : goingEventList) {
                            long timediff = getDateDiff(new Date(), event.getEventStartTime(), TimeUnit.MINUTES);
                            if (timediff <= 30 && timediff > 0) { // Change this to 30*10000 for testing
                                NOTIFICATION_ID = event.getEventID().hashCode();
                                final NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext());
                                builder.setContentTitle(event.getEventName())
                                        .setAutoCancel(true)
                                        .setColor(getResources().getColor(R.color.colorAccent))
                                        .setContentText("Event is about to start in " + getDateDiff(new Date(), event.getEventStartTime(), TimeUnit.MINUTES) + ((getDateDiff(new Date(), event.getEventStartTime(), TimeUnit.MINUTES) == 1) ? " minute." : " minutes."))
                                        .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.lotus_white))
                                        .setSmallIcon(R.drawable.lotus_white);

                                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                intent.setAction(ACTION_OPEN_EVENT);
                                intent.putExtra(Constants.SESSION_ID, sessionID);
                                intent.putExtra(Constants.EVENT_JSON, event.toString());

                                PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(),
                                        NOTIFICATION_ID,
                                        intent,
                                        PendingIntent.FLAG_UPDATE_CURRENT);
                                builder.setContentIntent(pendingIntent);
                                builder.setDeleteIntent(NotificationEventReceiver.getDeleteIntent(getApplicationContext()));

                                Intent navigateIntent = new Intent(getApplicationContext(), NotificationIntentService.class);
                                navigateIntent.setAction(ACTION_NAVIGATE);
                                navigateIntent.putExtra(Constants.EVENT_ID, event.getEventID());
                                navigateIntent.putExtra(Constants.EVENT_LATITUDE, event.getEventVenues().get(0).getVenueLatitude());
                                navigateIntent.putExtra(Constants.EVENT_LONGITUDE, event.getEventVenues().get(0).getVenueLongitude());
                                navigateIntent.putExtra("NOTIFICATION_ID", NOTIFICATION_ID);
                                PendingIntent navigatePendingIntent = PendingIntent.getService(getApplicationContext(), 0, navigateIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                                builder.addAction(R.drawable.baseline_navigation_white_24, "Navigate", navigatePendingIntent);

                                Intent notGoingIntent = new Intent(getApplicationContext(), NotificationIntentService.class);
                                notGoingIntent.setAction(ACTION_NOT_GOING);
                                notGoingIntent.putExtra(Constants.SESSION_ID, sessionID);
                                notGoingIntent.putExtra(Constants.EVENT_ID, event.getEventID());
                                notGoingIntent.putExtra("NOTIFICATION_ID", NOTIFICATION_ID);
                                PendingIntent notGoingPendingIntent = PendingIntent.getService(getApplicationContext(), 0, notGoingIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                                builder.addAction(R.drawable.baseline_close_white_24, "Not Going", notGoingPendingIntent);

                                manager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
                                Notification notification = builder.build();
                                notification.defaults |= Notification.DEFAULT_SOUND;
                                notification.defaults |= Notification.DEFAULT_VIBRATE;
                                manager.notify(NOTIFICATION_ID, notification);
                            }

                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {

            }
        });
    }
}