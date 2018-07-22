package com.iitb.moodindigo.mi2016;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public final class NotificationServiceStarterReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        NotificationEventReceiver.setupAlarm(context);
    }
}