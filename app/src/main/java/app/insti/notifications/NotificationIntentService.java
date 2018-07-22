package com.iitb.moodindigo.mi2016;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.iitb.moodindigo.mi2016.ServerConnection.GsonModels;

import java.lang.reflect.Type;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class NotificationIntentService extends IntentService {

    private static final String ACTION_START = "ACTION_START";
    private static final String ACTION_DELETE = "ACTION_DELETE";
    private static final String ACTION_NAVIGATE = "ACTION_NAVIGATE";
    private static final String ACTION_NOT_GOING = "ACTION_NOT_GOING";
    private static int NOTIFICATION_ID = 1;
    private SharedPreferences.Editor goingSharedPreferencesEditor;
    private NotificationManager manager;

    public NotificationIntentService() {
        super(NotificationIntentService.class.getSimpleName());
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
        try {
            String action = intent.getAction();
            if (ACTION_START.equals(action)) {
                processStartNotification();
            }
            if (ACTION_DELETE.equals(action)) {
                processDeleteNotification(intent);
            }
            if ("ACTION_NOT_GOING".equals(action)) {
                if (Cache.getGoingEventsList() != null) {
                    GsonModels.Event event = (new Gson().fromJson(intent.getStringExtra("EVENT_JSON"), GsonModels.Event.class));
                    Cache.removeFromGoingList(event);
                    goingSharedPreferencesEditor = this.getSharedPreferences("GOING", Context.MODE_PRIVATE).edit();
                    String goingEventsListJson = (new Gson()).toJson(Cache.getGoingEventsList());
                    goingSharedPreferencesEditor.putString("GOING_LIST", goingEventsListJson);
                    goingSharedPreferencesEditor.apply();
                }
                manager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
                manager.cancel(intent.getIntExtra("NOTIFICATION_ID", -1));
            }
        } finally {
            WakefulBroadcastReceiver.completeWakefulIntent(intent);
        }
    }

    private void processDeleteNotification(Intent intent) {
        // Log something?
    }

    private void processStartNotification() {
        // Do something. For example, fetch fresh data from backend to create a rich notification?

        SharedPreferences goingPreferences = getApplicationContext().getSharedPreferences("GOING", Context.MODE_PRIVATE);
        String goingList = goingPreferences.getString("GOING_LIST", null);
        Type type = new TypeToken<List<GsonModels.Event>>() {
        }.getType();
        List<GsonModels.Event> goingListGson = (new Gson()).fromJson(goingList, type);
        if (goingListGson == null) {
            ;
        } else {
            for (int i = 0; i < goingListGson.size(); i++) {
                GsonModels.Event event = goingListGson.get(i);
                long timediff = getDateDiff(new Date(), event.getDate(), TimeUnit.MINUTES);
                if (timediff <= 30 && timediff > 0) { // Change this to 30*10000 for testing
                    NOTIFICATION_ID = (int) Long.parseLong(event.get_id().substring(6, 11), 16);
                    final NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
                    builder.setContentTitle(event.getTitle())
                            .setAutoCancel(true)
                            .setColor(getResources().getColor(R.color.yellow))
                            .setContentText("Event is about to start in " + getDateDiff(new Date(), event.getDate(), TimeUnit.MINUTES) + ((getDateDiff(new Date(), event.getDate(), TimeUnit.MINUTES) == 1) ? " minute." : " minutes."))
                            .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_event_white_24dp))
                            .setSmallIcon(R.drawable.ic_event_white_24dp);

                    Intent intent = new Intent(this, MainActivity.class);
                    intent.setAction("OPEN_EVENT");
                    String eventJson = (new Gson()).toJson(event);
                    intent.putExtra("EVENT_JSON", eventJson);

                    PendingIntent pendingIntent = PendingIntent.getActivity(this,
                            NOTIFICATION_ID,
                            intent,
                            PendingIntent.FLAG_UPDATE_CURRENT);
                    builder.setContentIntent(pendingIntent);
                    builder.setDeleteIntent(NotificationEventReceiver.getDeleteIntent(this));

                    Intent navigateIntent = new Intent(this, MainActivity.class);
                    navigateIntent.setAction(ACTION_NAVIGATE);
                    navigateIntent.putExtra("EVENT_JSON", eventJson);
                    navigateIntent.putExtra("NOTIFICATION_ID", NOTIFICATION_ID);
                    PendingIntent navigatePendingIntent = PendingIntent.getActivity(this, 0, navigateIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                    builder.addAction(R.drawable.ic_navigation_white_24dp, "Navigate", navigatePendingIntent);

                    Intent notGoingIntent = new Intent(this, NotificationIntentService.class);
                    notGoingIntent.setAction(ACTION_NOT_GOING);
                    notGoingIntent.putExtra("EVENT_JSON", eventJson);
                    notGoingIntent.putExtra("NOTIFICATION_ID", NOTIFICATION_ID);
                    PendingIntent notGoingPendingIntent = PendingIntent.getService(this, 0, notGoingIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                    builder.addAction(R.drawable.ic_close_white_24dp, "Not Going", notGoingPendingIntent);

                    manager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
                    Notification notification = builder.build();
                    notification.defaults |= Notification.DEFAULT_SOUND;
                    notification.defaults |= Notification.DEFAULT_VIBRATE;
                    manager.notify(NOTIFICATION_ID, notification);
                }
            }
        }
    }
}