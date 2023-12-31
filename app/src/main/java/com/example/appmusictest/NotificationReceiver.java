package com.example.appmusictest;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import com.example.appmusictest.activity.MusicPlayerActivity;


public class NotificationReceiver extends BroadcastReceiver {

    private static final String TAG = "Notification_Receiver";
    @Override
    public void onReceive(Context context, Intent intent) {

        if (intent != null && intent.getAction() != null) {
            String action = intent.getAction();

            if (MyApplication.PREVIOUS.equals(action)) {
                if (MusicPlayerActivity.currentSongs.size() > 1) {
                    prevNextSong(false);
                }
            } else if (MyApplication.PLAY.equals(action)) {
                if (MusicPlayerActivity.checkSong()) {
                    if (MusicPlayerActivity.musicPlayerService.isPlaying()) {
                        pauseMusic();
                    } else {
                        playMusic();
                    }
                }
            } else if (MyApplication.NEXT.equals(action)) {
                if (MusicPlayerActivity.currentSongs.size() > 1) {
                    prevNextSong(true);
                }
            } else if (MyApplication.EXIT.equals(action)) {
                exitApplication(context);
            }
        }
    }

    public static void exitApplication(Context context) {
        if (MusicPlayerActivity.musicPlayerService != null) {
            MusicPlayerActivity.musicPlayerService.mediaPlayer.release();
            MusicPlayerActivity.musicPlayerService.mediaPlayer = null;
            MusicPlayerActivity.musicPlayerService.stopForeground(true);
            MusicPlayerActivity.musicPlayerService.stopService();
            MusicPlayerActivity.musicPlayerService = null;
            Log.d(TAG, "Stop service");
        }
    }

    private void prevNextSong(boolean b) {
        MusicPlayerActivity.musicPlayerService.nextSong(b);
        Log.d(TAG, "next: " + b);
}

    private void playMusic() {
        MusicPlayerActivity.musicPlayerService.resumeMusic();
        Log.d(TAG, "play");
    }

    private void pauseMusic() {
        MusicPlayerActivity.musicPlayerService.pauseMusic();
        Log.d(TAG, "pause");
    }
}
