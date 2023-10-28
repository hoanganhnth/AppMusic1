package com.example.appmusictest.service;

import static com.example.appmusictest.MyApplication.CHANNEL_ID;

import static com.example.appmusictest.activity.MusicPlayerActivity.nowPlayingId;
import static com.example.appmusictest.activity.MusicPlayerActivity.seekBar;
import static com.example.appmusictest.activity.MusicPlayerActivity.songPosition;
import static com.example.appmusictest.activity.MusicPlayerActivity.songs;
import static com.example.appmusictest.activity.MusicPlayerActivity.startDirectionTv;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.media.session.MediaSessionCompat;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.appmusictest.MyApplication;
import com.example.appmusictest.NotificationReceiver;
import com.example.appmusictest.R;
import com.example.appmusictest.activity.MainActivity;
import com.example.appmusictest.activity.MusicPlayerActivity;
import com.example.appmusictest.utilities.TimeFormatterUtility;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableOnSubscribe;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class MusicPlayerService extends Service implements MediaPlayer.OnCompletionListener{
    public MediaPlayer mediaPlayer;
    private Disposable disposable;
    private final IBinder binder = new LocalBinder();
    private MediaSessionCompat mediaSessionCompat;
    private BroadcastReceiver onDestroyReceiver;
    private final Handler seekBarUpdateHandler = new Handler();
    public boolean destroyMain = false;
    private boolean isPlaying = false;
    private static final String TAG = "Music_Player_Service";

    public boolean isPlaying() {
        return isPlaying;
    }

    public class LocalBinder extends Binder {
        public MusicPlayerService getService() {
            return MusicPlayerService.this;
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mediaPlayer = new MediaPlayer();
        checkMainActivityDestroy();

    }

    private void checkMainActivityDestroy() {
        onDestroyReceiver = new BroadcastReceiver() {



            @Override
            public void onReceive(Context context, Intent intent) {
                destroyMain = true;
                if (isPlaying) {
                    showNotification(R.drawable.ic_pause_gray);
                } else {
                    showNotification(R.drawable.ic_play);
                }
            }
        };
        LocalBroadcastManager.getInstance(this).registerReceiver(onDestroyReceiver, new IntentFilter("your.package.name.MainActivityDestroyed"));
    }

    public void stopService() {
        stopSelf();
    }

    @Override
    public void onDestroy() {
        if (disposable != null && !disposable.isDisposed()) {
            disposable.dispose();
        }
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
        seekBarUpdateHandler.removeCallbacks(updateSeekbar);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(onDestroyReceiver);
        super.onDestroy();
    }

    public void showNotification(int playPauseBtn) {
        Log.d(TAG, "show notification");
        Intent intent = new Intent(getBaseContext(), MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        int flag;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            flag = PendingIntent.FLAG_IMMUTABLE;
        } else {
            flag = PendingIntent.FLAG_UPDATE_CURRENT;
        }
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, intent, flag);

        Intent playIntent = new Intent(getBaseContext(), NotificationReceiver.class).setAction(MyApplication.PLAY);
        PendingIntent playPendingIntent = PendingIntent.getBroadcast(getBaseContext(), 0, playIntent, flag);

        Intent nextIntent = new Intent(getBaseContext(), NotificationReceiver.class).setAction(MyApplication.NEXT);
        PendingIntent nextPendingIntent = PendingIntent.getBroadcast(getBaseContext(), 0, nextIntent, flag);

        Intent prevIntent = new Intent(getBaseContext(), NotificationReceiver.class).setAction(MyApplication.PREVIOUS);
        PendingIntent prevPendingIntent = PendingIntent.getBroadcast(getBaseContext(), 0, prevIntent, flag);
        PendingIntent exitPendingIntent;
        int exit;
        if (destroyMain) {
            Intent exitIntent = new Intent(getBaseContext(), NotificationReceiver.class).setAction(MyApplication.EXIT);
            exitPendingIntent = PendingIntent.getBroadcast(getBaseContext(), 0, exitIntent, flag);
            exit = R.drawable.ic_exit_gray;
        } else {
            exitPendingIntent = null;
            exit = 0;
        }

        Notification builder = new NotificationCompat.Builder(getBaseContext(), CHANNEL_ID)
                .setContentIntent(contentIntent)
                .setSmallIcon(R.drawable.ic_music_cyan)
                .setContentTitle(MusicPlayerActivity.songs.get(MusicPlayerActivity.songPosition).getTitle())
                .setContentText(MusicPlayerActivity.songs.get(MusicPlayerActivity.songPosition).getNameAuthor())
                .setStyle(new androidx.media.app.NotificationCompat.MediaStyle().setMediaSession(mediaSessionCompat.getSessionToken()))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setOnlyAlertOnce(true)
                .addAction(R.drawable.ic_previous, "Previous", prevPendingIntent)
                .addAction(playPauseBtn, "Play", playPendingIntent)
                .addAction(R.drawable.ic_next, "Next", nextPendingIntent)
                .addAction(exit, "Exit", exitPendingIntent)
                .build();
        startForeground(13,builder);
    }

    public void playMusicFromUrl(String musicUrl) {

        if (mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
            isPlaying = false;
            seekBarUpdateHandler.removeCallbacks(updateSeekbar);
        }
        if (disposable != null && !disposable.isDisposed()) {
            disposable.dispose();
        }

        disposable = Observable.create((ObservableOnSubscribe<Boolean>) emitter -> {

                    mediaPlayer.reset();
                    mediaPlayer.setDataSource(musicUrl);
                    mediaPlayer.prepareAsync();
                    mediaPlayer.setOnCompletionListener(this);
                    showNotification(R.drawable.ic_play);
                    Log.d(TAG, "Prepare music");
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        result -> {

                        },
                        error -> {

                        }
                );
        mediaPlayer.setOnPreparedListener(mp -> {
            mediaPlayer.start();
            isPlaying = true;
            updateSeekbar.run();

            Intent intent = new Intent("music_control");
            intent.putExtra("action", "play");
            LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
            showNotification(R.drawable.ic_pause_gray);
            Log.d(TAG, "Start music");

        });
    }

    public int getDuration() {
        if (mediaPlayer == null) return 0;
        return mediaPlayer.getDuration();
    }

    public Runnable updateSeekbar = new Runnable() {
        @Override
        public void run() {
            if (isPlaying && nowPlayingId.equals(songs.get(songPosition).getId())) {
                seekBar.setProgress(getCurrentPosition());
                startDirectionTv.setText(TimeFormatterUtility.formatTime(getCurrentPosition()));
            }
            seekBarUpdateHandler.postDelayed(this, 100);
        }
    };

    public int getCurrentPosition() {
        if (mediaPlayer != null) {
            return mediaPlayer.getCurrentPosition();
        }
        return 0;
    }

    public void pauseMusic() {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            isPlaying = false;
            showNotification(R.drawable.ic_play);
            Intent intent = new Intent("music_control");
            intent.putExtra("action", "pause");
            LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
            Log.d("MUSIC SERVICE", "Pause music");
        }
    }

    public void resumeMusic() {
        if (!mediaPlayer.isPlaying()) {
            mediaPlayer.start();
            isPlaying = true;
            showNotification(R.drawable.ic_pause_gray);
            Intent intent = new Intent("music_control");
            intent.putExtra("action", "resume");
            LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
            Log.d("MUSIC SERVICE", "Resume music");
        }
    }

    public void nextSong(Boolean b) {
        MusicPlayerActivity.setSongPosition(b);
        playMusicFromUrl(songs.get(songPosition).getPathUrl());
        Intent intent = new Intent("music_control");
        intent.putExtra("action", "next");
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
        Log.d(TAG, "Next song");
    }

    @Override
    public void onCompletion(MediaPlayer mp) {

        Log.d(TAG, "Complete song");
        nextSong(true);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        mediaSessionCompat = new MediaSessionCompat(getBaseContext(), "My Music");
        return binder;
    }
}
