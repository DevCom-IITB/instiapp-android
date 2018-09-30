package app.insti;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.squareup.picasso.Picasso;

import java.io.IOException;
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
        channel = getResources().getString(R.string.default_notification_channel_id);

        // Check for empty data notifications
        if (remoteMessage.getData().size() > 0) {
            sendRichNotification(remoteMessage);
        }

        super.onMessageReceived(remoteMessage);
    }

    /** Ensure key is in data */
    private boolean ensureKeyExists(RemoteMessage remoteMessage, String key) {
        return (remoteMessage.getData().get(key) != null);
    }

    /** Send a rich notification with image support */
    private void sendRichNotification(RemoteMessage remoteMessage) {
        if (!ensureKeyExists(remoteMessage, Constants.FCM_BUNDLE_TITLE) ||
            !ensureKeyExists(remoteMessage, Constants.FCM_BUNDLE_VERB)) { return; }

        final String message = remoteMessage.getData().get(Constants.FCM_BUNDLE_VERB);

        int notification_id = NotificationId.getID();
        showBitmapNotification(
                this,
                remoteMessage.getData().get(Constants.FCM_BUNDLE_IMAGE),
                remoteMessage.getData().get(Constants.FCM_BUNDLE_LARGE_ICON),
                notification_id,
                standardNotificationBuilder()
                    .setContentTitle(remoteMessage.getData().get(Constants.FCM_BUNDLE_TITLE))
                    .setContentText(message)
                    .setContentIntent(getNotificationIntent(remoteMessage, notification_id)),
                message
        );
    }

    /** Show the notification */
    private static void showNotification(Context context, int id, Notification notification) {
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(id, notification);
    }

    /** Common builder */
    private NotificationCompat.Builder standardNotificationBuilder() {
        Uri soundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        return new NotificationCompat.Builder(this, channel)
                .setSmallIcon(R.drawable.ic_lotusgray)
                .setColor(getResources().getColor(R.color.colorPrimary))
                .setVibrate(new long[]{0, 200})
                .setSound(soundUri)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
    }

    /** Gets a bitmap from a URL asynchronously and shows notification */
    public static void showBitmapNotification(
            final Context context, final String imageUrl, final String largeIconUrl,
            final int notification_id, final NotificationCompat.Builder builder, final String content){

        new AsyncTask<Void, Void, Bitmap[]>() {
            @Override
            protected Bitmap[] doInBackground(Void... params) {
                try {
                    Bitmap image = null;
                    if (imageUrl != null) {
                        image = Picasso.get().load(imageUrl).get();
                    }
                    Bitmap largeIcon = null;
                    if (largeIconUrl != null) {
                         largeIcon = getCroppedBitmap(
                                 Picasso.get().load(Constants.resizeImageUrl(largeIconUrl, 200)).get(), 200);
                    }
                    return new Bitmap[]{image, largeIcon};
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Bitmap[] bitmaps) {
                // Check if we loaded big image
                if (bitmaps != null && bitmaps[0] != null) {
                    builder.setStyle(
                            new NotificationCompat.BigPictureStyle()
                                    .bigPicture(bitmaps[0])
                                    .setSummaryText(content)
                    );
                }

                // Check if we loaded large icon
                if (bitmaps != null && bitmaps[1] != null) {
                    builder.setLargeIcon(bitmaps[1]);
                }

                showNotification(context, notification_id, builder.build());
                super.onPostExecute(bitmaps);
            }
        }.execute();
    }

    /** Get circular center cropped bitmap */
    public static Bitmap getCroppedBitmap(Bitmap bmp, int radius) {
        Bitmap sbmp;

        if (bmp.getWidth() != radius || bmp.getHeight() != radius) {
            float smallest = Math.min(bmp.getWidth(), bmp.getHeight());
            float factor = smallest / radius;
            sbmp = Bitmap.createScaledBitmap(bmp, (int)(bmp.getWidth() / factor), (int)(bmp.getHeight() / factor), false);
        } else {
            sbmp = bmp;
        }

        Bitmap output = Bitmap.createBitmap(radius, radius,
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xffa19774;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, radius, radius);
        final Rect destRect = new Rect(
                (sbmp.getWidth() - radius) / 2,
                (sbmp.getHeight() - radius) / 2,
                radius + (sbmp.getWidth() - radius) / 2,
                radius + (sbmp.getHeight() - radius) / 2);

        paint.setAntiAlias(true);
        paint.setFilterBitmap(true);
        paint.setDither(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(Color.parseColor("#BAB399"));
        canvas.drawCircle(radius / 2,
                radius / 2, radius / 2, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(sbmp, destRect, rect, paint);

        return output;
    }
}
