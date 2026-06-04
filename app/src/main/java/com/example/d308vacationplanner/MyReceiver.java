package com.example.d308vacationplanner;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;

public class MyReceiver extends BroadcastReceiver {

    private static int notificationID = 1;
    String channel_id = "vacation_channel";

    @Override
    public void onReceive(Context context, Intent intent) {

        createNotificationChannel(context);

        String message = intent.getStringExtra("message");

        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(context, channel_id)
                        .setSmallIcon(android.R.drawable.ic_dialog_info)
                        .setContentTitle("Vacation Alert")
                        .setContentText(message)
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(notificationID++, builder.build());
    }

    private void createNotificationChannel(Context context) {

        CharSequence name = "Vacation Alerts";
        String description = "Vacation notifications";

        int importance = NotificationManager.IMPORTANCE_DEFAULT;

        NotificationChannel channel =
                new NotificationChannel(channel_id, name, importance);

        channel.setDescription(description);

        NotificationManager notificationManager =
                context.getSystemService(NotificationManager.class);

        notificationManager.createNotificationChannel(channel);
    }
}