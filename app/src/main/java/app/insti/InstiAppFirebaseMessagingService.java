package app.insti;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

import app.insti.activity.MainActivity;
import app.insti.notifications.NotificationId;

public class InstiAppFirebaseMessagingService extends FirebaseMessagingService {
    String channel;

    @Override
    public void onNewToken(String s) {
        /* For future functionality */
        super.onNewToken(s);
    }

    /** Convert a string to string map to a bundle */
    private Bundle stringMapToBundle(Map<String, String> map) {
        Bundle bundle = new Bundle();
        for (Map.Entry<String, String> entry : map.entrySet()) {
            bundle.putString(entry.getKey(), entry.getValue());
        }
        return bundle;
    }

    /** Get a PendingIntent to open MainActivity from a notification message */
    private PendingIntent getNotificationIntent(RemoteMessage remoteMessage, Integer notificationId) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.putExtra(Constants.MAIN_INTENT_EXTRAS, stringMapToBundle(remoteMessage.getData()));
        return PendingIntent.getActivity(this, notificationId, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        String TAG = "NOTIFICATION";
        channel = getResources().getString(R.string.default_notification_channel_id);

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.wtf(TAG, "Message data payload: " + remoteMessage.getData());
            String isData = remoteMessage.getData().get(Constants.FCM_BUNDLE_IS_DATA);
            if (isData != null && isData.equals("true")) {
                String type = remoteMessage.getData().get(Constants.FCM_BUNDLE_TYPE);
                String action = remoteMessage.getData().get(Constants.FCM_BUNDLE_ACTION);

                if (type.equals(Constants.DATA_TYPE_EVENT) && action.equals(Constants.FCM_BUNDLE_ACTION_STARTING)) {
                    sendEventStartingNotification(remoteMessage);
                }
            } else {
                sendMessageNotification(remoteMessage);
            }
        }

        super.onMessageReceived(remoteMessage);
    }

    /** Ensure key is in data */
    private boolean ensureKeyExists(RemoteMessage remoteMessage, String key) {
        return (remoteMessage.getData().get(key) != null);
    }

    /** Send a event is starting notification */
    private void sendEventStartingNotification(RemoteMessage remoteMessage) {
        if (!ensureKeyExists(remoteMessage, "name")) { return; }

        int notification_id = NotificationId.getID();
        Notification notification = standardNotificationBuilder()
                .setContentTitle(remoteMessage.getData().get("name"))
                .setContentText("Event is about to start")
                .setContentIntent(getNotificationIntent(remoteMessage, notification_id))
                .build();

        /* Show notification */
        showNotification(notification_id, notification);
    }

    /** Send a standard notification from foreground */
    private void sendMessageNotification(RemoteMessage remoteMessage) {
        /* Get data */
        String title = remoteMessage.getNotification().getTitle();
        String body = remoteMessage.getNotification().getBody();
        Integer notification_id;
        try {
            notification_id = Integer.parseInt(remoteMessage.getData().get(Constants.FCM_BUNDLE_NOTIFICATION_ID));
        } catch (NumberFormatException ignored) {
            return;
        }

        /* Check malformed notifications */
        if (title == null || body == null) { return; }

        /* Build notification */
        Notification notification = standardNotificationBuilder()
                .setContentTitle(title)
                .setContentText(body)
                .setContentIntent(getNotificationIntent(remoteMessage, notification_id))
                .build();

        /* Show notification */
        showNotification(notification_id, notification);
    }

    /** Show the notification */
    private void showNotification(int id, Notification notification) {
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(id, notification);
    }

    /** Common builder */
    private NotificationCompat.Builder standardNotificationBuilder() {
        return new NotificationCompat.Builder(this, channel)
                .setSmallIcon(R.drawable.ic_lotusgray)
                .setColor(getResources().getColor(R.color.colorPrimary))
                .setVibrate(new long[]{0, 400})
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
    }
}
