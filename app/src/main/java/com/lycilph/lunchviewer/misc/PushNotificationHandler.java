package com.lycilph.lunchviewer.misc;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.lycilph.lunchviewer.Services.DownloadService;
import com.lycilph.lunchviewer.activities.MainActivity;
import com.microsoft.windowsazure.notifications.NotificationsHandler;

public class PushNotificationHandler extends NotificationsHandler {
    private static final String TAG = "PushNotificationHandler";
    private static final String NEW_DATA_MESSAGE = "NewData";

    @Override
    public void onRegistered(Context context, String gcmRegistrationId)
    {
        Log.i(TAG, "Registered push notification handler");

        super.onRegistered(context, gcmRegistrationId);
        MainActivity mainActivity = (MainActivity) context;
        mainActivity.getAzureService().registerForPush(gcmRegistrationId);
    }

    @Override
    public void onReceive(Context context, Bundle bundle) {
        Log.i(TAG, "Received push notification");

        String msg = bundle.getString("message");
        if (msg.equals(NEW_DATA_MESSAGE)) {
            Log.i(TAG, "Got 'new data' message");
            Intent intent = new Intent(context, DownloadService.class);
            context.startService(intent);
        } else {
            Log.e(TAG, "Got unknown message");
        }
    }
}
