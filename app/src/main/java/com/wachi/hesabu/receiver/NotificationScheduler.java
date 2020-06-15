package com.wachi.hesabu.receiver;

import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.core.content.FileProvider;

import com.wachi.hesabu.R;
import com.wachi.hesabu.ui.MainActivity;

import java.io.File;

import static com.wachi.hesabu.ui.ActivityRemider.DAILY_REMINDER_REQUEST_CODE;


public class NotificationScheduler {
    public static void showNotification(Context context, int level) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        assert notificationManager != null;
        notificationManager.cancelAll();
        Intent intent1 = new Intent(context, MainActivity.class);

        String channelId = "channel-01";
        String channelName = "Channel Name";
        int importance = 0;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            importance = NotificationManager.IMPORTANCE_HIGH;
        }

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel mChannel = new NotificationChannel(
                    channelId, channelName, importance);
            notificationManager.createNotificationChannel(mChannel);
        }
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addNextIntent(intent1);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(DAILY_REMINDER_REQUEST_CODE, PendingIntent.FLAG_UPDATE_CURRENT
                | PendingIntent.FLAG_ONE_SHOT);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context, channelId)
                .setSmallIcon(R.mipmap.ic_launcher_round).setContentTitle(context.getResources().getString(R.string.app_name))
                .setContentText(context.getString(R.string.level) + " " + level + " " + context.getString(R.string.level_complete))
                .setAutoCancel(true)
                .setContentIntent(resultPendingIntent);
        notificationManager.notify(123, mBuilder.build());
    }


    public static void showPdfNotification(Activity context, String path) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        assert notificationManager != null;


        String channelId = "channel-01";
        String channelName = "Channel Name";
        int importance = 0;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            importance = NotificationManager.IMPORTANCE_HIGH;
        }

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel mChannel = new NotificationChannel(
                    channelId, channelName, importance);
            notificationManager.createNotificationChannel(mChannel);
        }
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);


        if (!TextUtils.isEmpty(path)) {
            Uri uri_path = FileProvider.getUriForFile(context, context.getApplicationContext().getPackageName() + ".provider", new File(path));

            Intent pdfOpenintent = new Intent(Intent.ACTION_VIEW);
            pdfOpenintent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            pdfOpenintent.setDataAndType(uri_path, "application/pdf");

            try {
                stackBuilder.addNextIntent(pdfOpenintent);
            } catch (ActivityNotFoundException ignored) {
                Toast.makeText(context, context.getString(R.string.no_app_found), Toast.LENGTH_SHORT).show();
            }
        }
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(DAILY_REMINDER_REQUEST_CODE, PendingIntent.FLAG_UPDATE_CURRENT
                | PendingIntent.FLAG_ONE_SHOT);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context, channelId)
                .setSmallIcon(R.mipmap.ic_launcher_round).setContentTitle(context.getResources().getString(R.string.app_name))
                .setContentText(context.getString(R.string.pdf_generate))
                .setAutoCancel(true)
                .setContentIntent(resultPendingIntent);
        notificationManager.notify(345, mBuilder.build());
    }


    static void showReminderNotification(Context context) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Intent intent1 = new Intent(context, MainActivity.class);

        String channelId = "channel-01";
        String channelName = "Channel Name";
        int importance = 0;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            importance = NotificationManager.IMPORTANCE_HIGH;
        }

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel mChannel = new NotificationChannel(
                    channelId, channelName, importance);
            assert notificationManager != null;
            notificationManager.createNotificationChannel(mChannel);
        }
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addNextIntent(intent1);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(DAILY_REMINDER_REQUEST_CODE, PendingIntent.FLAG_UPDATE_CURRENT
                | PendingIntent.FLAG_ONE_SHOT);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context, channelId)
                .setSmallIcon(R.mipmap.ic_launcher_round).setContentTitle(context.getResources().getString(R.string.app_name))
                .setContentText(context.getString(R.string.mathry_reminder))
                .setAutoCancel(true).setContentIntent(resultPendingIntent);
        assert notificationManager != null;
        notificationManager.notify(DAILY_REMINDER_REQUEST_CODE, mBuilder.build());
    }

}
