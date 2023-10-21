package com.example.appmusictest;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

import java.text.SimpleDateFormat;

public class ApplicationClass extends Application {
    public static String CHANNEL_ID = "channel";
    public static String PLAY = "play";
    public static String NEXT = "next";
    public static String PREVIOUS = "previous";
    public static String EXIT = "exit";

    @Override
    public void onCreate() {
        super.onCreate();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID, "Now Playing Song", NotificationManager.IMPORTANCE_HIGH);
            notificationChannel.setDescription("This is a important for showing song!");
            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(notificationChannel);
        }
    }
}
