package com.lycilph.lunchviewer;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.microsoft.windowsazure.notifications.NotificationsHandler;

public class PushNotificationHandler extends NotificationsHandler {
    public static final String TAG = "PushNotificationHandler";
    public static final int NOTIFICATION_ID = 1;
    private NotificationManager notificationManager;
    private Context ctx;

    @Override
    public void onRegistered(Context context, String gcmRegistrationId)
    {
        super.onRegistered(context, gcmRegistrationId);
        MainActivity mainActivity = (MainActivity) context;
        mainActivity.registerForPush(gcmRegistrationId);
        Log.i(TAG, "Notification handler registered");
    }

    @Override
    public void onReceive(Context context, Bundle bundle) {
        ctx = context;
        String msg = bundle.getString("message");
        sendNotification(msg);
        Log.i(TAG, "Got message: " + msg);
    }

    private void sendNotification(String msg) {
        notificationManager = (NotificationManager) ctx.getSystemService(Context.NOTIFICATION_SERVICE);

        PendingIntent contentIntent = PendingIntent.getActivity(ctx, 0, new Intent(ctx, MainActivity.class), 0);

        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(ctx)
                        .setSmallIcon(R.drawable.ic_launcher)
                        .setContentTitle("Lunch Viewer Notification test")
                        .setStyle(new NotificationCompat.BigTextStyle().bigText(msg))
                        .setContentText(msg);

        builder.setContentIntent(contentIntent);
        notificationManager.notify(NOTIFICATION_ID, builder.build());
    }
}
