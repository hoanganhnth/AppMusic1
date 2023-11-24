package com.example.appmusictest;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

public class MyApplication extends Application {
    public static final String CHANNEL_ID = "channel";
    public static final String PLAY = "play";
    public static final String NEXT = "next";
    public static final String PREVIOUS = "previous";
    public static final String EXIT = "exit";
    public static final int TYPE_PLAYLIST = 1;
    public static final int TYPE_ALBUM = 2;
    public static final int TYPE_SONG = 3;
    public static final int TYPE_AUTHOR = 4;
    public static final int NUMBER_SUGGEST = 4;


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
