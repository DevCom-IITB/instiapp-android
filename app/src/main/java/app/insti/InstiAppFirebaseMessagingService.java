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

public class InstiAppFirebaseMessagingService extends FirebaseMessagingService {
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
    private PendingIntent getNotificationIntent(RemoteMessage remoteMessage) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.putExtra(Constants.MAIN_INTENT_EXTRAS, stringMapToBundle(remoteMessage.getData()));
        return PendingIntent.getActivity(this, 0, intent, 0);
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        String TAG = "NOTIFICATION";
        String channel = getResources().getString(R.string.default_notification_channel_id);

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.wtf(TAG, "Message data payload: " + remoteMessage.getData());
            String isData = remoteMessage.getData().get("is_data");
            if (isData != null && isData.equals("true")) {
                // TODO: Implement this
            } else {
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
                Notification notification = new NotificationCompat.Builder(this, channel)
                        .setSmallIcon(R.drawable.ic_lotusgray)
                        .setColor(getResources().getColor(R.color.colorPrimary))
                        .setContentTitle(title)
                        .setContentText(body)
                        .setContentIntent(getNotificationIntent(remoteMessage))
                        .setVibrate(new long[]{0, 400})
                        .setAutoCancel(true)
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .build();

                /* Show notification */
                NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
                notificationManager.notify(notification_id, notification);
            }
        }

        super.onMessageReceived(remoteMessage);
    }
}
