package com.example.andr2app.notifications;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

import com.example.andr2app.R;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import javax.annotation.Nonnull;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    public MyFirebaseMessagingService() {
    }

    String title, message;
    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage){
        super.onMessageReceived(remoteMessage);

        title = remoteMessage.getData().get("title");
        message = remoteMessage.getData().get("message");

        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(getApplicationContext())
                .setSmallIcon(R.drawable.ic_mail_white_36dp)
                .setContentTitle(title)
                        .setStyle(new NotificationCompat.BigTextStyle()
                                .bigText(message))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setVibrate(new long[] { 0, 500, 1000, 1000, 1000 });
        NotificationManager manager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(0,builder.build());
    }

}
