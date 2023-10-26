package com.example.appmusictest.service;

import static com.example.appmusictest.MyApplication.CHANNEL_ID;
import static com.example.appmusictest.activity.MusicPlayerActivity.endDirectionTv;

import static com.example.appmusictest.activity.MusicPlayerActivity.nowPlayingId;
import static com.example.appmusictest.activity.MusicPlayerActivity.seekBar;
import static com.example.appmusictest.activity.MusicPlayerActivity.songPosition;
import static com.example.appmusictest.activity.MusicPlayerActivity.songs;
import static com.example.appmusictest.activity.MusicPlayerActivity.startDirectionTv;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
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

import com.bumptech.glide.Glide;
import com.example.appmusictest.MyApplication;
import com.example.appmusictest.NotificationReceiver;
import com.example.appmusictest.R;
import com.example.appmusictest.activity.MainActivity;
import com.example.appmusictest.activity.MusicPlayerActivity;
import com.example.appmusictest.fragment.NowPlayingFragment;
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
    private final Handler seekBarUpdateHandler = new Handler();

    public boolean isPlaying() {
        return isPlaying;
    }

    private boolean isPlaying = false;

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
    }

    public void stopService() {
        stopSelf();
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (disposable != null && !disposable.isDisposed()) {
            disposable.dispose();
        }
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
        seekBarUpdateHandler.removeCallbacks(updateSeekbar);

    }

    public void showNotification(int playPauseBtn) {
        Intent intent = new Intent(getBaseContext(), MainActivity.class);
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

        Intent exitIntent = new Intent(getBaseContext(), NotificationReceiver.class).setAction(MyApplication.EXIT);
        PendingIntent exitPendingIntent = PendingIntent.getBroadcast(getBaseContext(), 0, exitIntent, flag);


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
                .addAction(R.drawable.ic_exit_gray, "Exit", exitPendingIntent)
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
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        result -> {
                            // Xử lý khi việc phát nhạc đã được chuẩn bị sẵn
                        },
                        error -> {
                            // Xử lý khi có lỗi xảy ra
                        }
                );
        mediaPlayer.setOnPreparedListener(mp -> {
            mediaPlayer.start();
            isPlaying = true;
            setDuration();
            nowPlayingId = songs.get(songPosition).getId();
            updateSeekbar.run();
            Intent intent = new Intent("music_control");
            intent.putExtra("action", "play");
            LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
            showNotification(R.drawable.ic_pause_gray);

        });
    }


    private void setDuration() {
        endDirectionTv.setText(TimeFormatterUtility.formatTime(getDuration()));
        seekBar.setMax(getDuration());
    }

    public int getDuration() {
        return mediaPlayer.getDuration();
    }

    public Runnable updateSeekbar = new Runnable() {
        @Override
        public void run() {
            if (isPlaying) {
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
        }
    }

    public void resumeMusic() {
        if (!mediaPlayer.isPlaying()) {
            mediaPlayer.start();
            isPlaying = true;
            showNotification(R.drawable.ic_pause_gray);
        }
    }

    public void stopMusic() {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
        }
    }

    public void nextSong(Boolean b) {
        MusicPlayerActivity.setSongPosition(b);
        playMusicFromUrl(songs.get(songPosition).getPathUrl());

    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        MusicPlayerActivity.prevNextSong(true);
        Intent intent = new Intent("music_control");
        intent.putExtra("action", "next");
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
        Log.e("MUSIC SERVICE", "da hoan thanh bai hat");
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        mediaSessionCompat = new MediaSessionCompat(getBaseContext(), "My Music");
        return binder;
    }


}
