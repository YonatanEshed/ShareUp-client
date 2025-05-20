package com.shareup.application.SERVICES;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.shareup.application.R;
import com.shareup.viewmodel.FcmTokenViewModel;

public class FirebaseNotificationService extends FirebaseMessagingService  {

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        Log.d("FirebaseMessaging", "Message received from: " + remoteMessage.getFrom());
        // Handle the incoming message
        if (remoteMessage.getNotification() != null) {
            String title = remoteMessage.getNotification().getTitle();
            String body = remoteMessage.getNotification().getBody();
            Log.d("FirebaseMessaging", "Notification received - Title: " + title + ", Body: " + body);

            showNotification(title, body);
        }

        if (!remoteMessage.getData().isEmpty()) {
            Log.d("FirebaseMessaging", "Data payload: " + remoteMessage.getData());
            // Handle custom data
        }
    }

    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);

        Log.d("FirebaseMessaging", "New token: " + token);

        // Send the token to your server
        sendTokenToServer(token);
    }

    public void sendTokenToServer(String token) {
        FcmTokenViewModel fcmTokenViewModel = new FcmTokenViewModel(getApplication());
        fcmTokenViewModel.setFcmToken(token);
    }

    private void showNotification(String title, String body) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "default")
                .setContentTitle(title)
                .setContentText(body)
                .setSmallIcon(R.drawable.logo)
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("default", "FCM Channel", NotificationManager.IMPORTANCE_HIGH);
            manager.createNotificationChannel(channel);
        }

        manager.notify(0, builder.build());
    }
}
