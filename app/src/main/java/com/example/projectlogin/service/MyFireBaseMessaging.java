package com.example.projectlogin.service;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.example.projectlogin.R;
import com.example.projectlogin.ui.LoginActivity;
import com.example.projectlogin.util.Common;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFireBaseMessaging extends FirebaseMessagingService {


    @Override
    public void onNewToken(@NonNull String fcmToken) {
        super.onNewToken(fcmToken);

        updateFCMTokenToServer(fcmToken);
    }

    private void updateFCMTokenToServer(String fcmToken) {
        Common.FCM_TOKEN = fcmToken;
        Log.i(Common.TAG, "fcmToken => updateFCMTokenToServer : " + fcmToken);
    }


    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        Log.i(Common.TAG, "onMessageReceived : 0 ");

        String title = null;
        String message = null;

        if (remoteMessage.getData().size() > 0) {
            Log.i(Common.TAG, "onMessageReceived : 1 ");

            title = remoteMessage.getData().get("title");
            message = remoteMessage.getData().get("body");
        } else if (remoteMessage.getNotification() != null) {
            title = remoteMessage.getNotification().getTitle();
            message = remoteMessage.getNotification().getBody();
        }

        if (!TextUtils.isEmpty(title) && !TextUtils.isEmpty(message)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                sendNotificationAPI26(MyFireBaseMessaging.this, title, message, LoginActivity.class);
            } else {
                sendNotifications(MyFireBaseMessaging.this, title, message, LoginActivity.class);
            }
        }
    }

    @SuppressLint("InlinedApi")
    public static void sendNotifications(Context context, String title, String message, Class<?> cls) {
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(true)
                .setSound(defaultSoundUri);

        if (cls != null) {
            Intent i = new Intent(context, cls);

            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            PendingIntent notification = PendingIntent.getActivity(context, 0, i, PendingIntent.FLAG_UPDATE_CURRENT);

            builder.setContentIntent(notification);
        }

        builder.setPriority(NotificationManager.IMPORTANCE_HIGH | NotificationManager.IMPORTANCE_MAX);

        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(1, builder.build());

    }

    public static void sendNotificationAPI26(Context context, String title, String message, Class<?> cls) {
        NotificationHelper helper;
        Notification.Builder builder;
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        helper = new NotificationHelper(context);
        builder = helper.getNotification(title, message, defaultSoundUri);

        if (cls != null) {
            Intent i = new Intent(context, cls);

            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            PendingIntent notification = PendingIntent.getActivity(context, 0, i, PendingIntent.FLAG_UPDATE_CURRENT);

            builder.setContentIntent(notification);
        }
        helper.getManager().notify(1, builder.build());
    }
}
