package com.example.appmusictest.service;

import static com.example.appmusictest.ApplicationClass.CHANNEL_ID;
import static com.example.appmusictest.activity.MusicPlayerActivity.endDirectionTv;

import static com.example.appmusictest.activity.MusicPlayerActivity.nowPlayingId;
import static com.example.appmusictest.activity.MusicPlayerActivity.prevNextSong;
import static com.example.appmusictest.activity.MusicPlayerActivity.seekBar;
import static com.example.appmusictest.activity.MusicPlayerActivity.songPosition;
import static com.example.appmusictest.activity.MusicPlayerActivity.songs;
import static com.example.appmusictest.activity.MusicPlayerActivity.startDirectionTv;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.media.session.MediaSessionCompat;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.example.appmusictest.R;
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
    }

    public void showNotification() {
        Notification builder = new NotificationCompat.Builder(getBaseContext(), CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_play)
                .setContentTitle(MusicPlayerActivity.songs.get(MusicPlayerActivity.songPosition).getTitle())
                .setContentText(MusicPlayerActivity.songs.get(MusicPlayerActivity.songPosition).getNameAuthor())
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setOnlyAlertOnce(true)
                .addAction(R.drawable.ic_previous, "Previous", null)
                .addAction(R.drawable.ic_play, "Play", null)
                .addAction(R.drawable.ic_next, "Next", null)
                .addAction(R.drawable.ic_exit_gray, "Exit", null)
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
            // Bắt đầu phát nhạc sau khi chuẩn bị xong
            mediaPlayer.setOnErrorListener((mp1, what, extra) -> {
                Log.e("MediaPlayer Error", "what: " + what + ", extra: " + extra);
                return false; // Trả về true nếu bạn đã xử lý lỗi, ngược lại trả về false.
            });
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
            if (isPlaying()) {
                seekBar.setProgress(getCurrentPosition());
                startDirectionTv.setText(TimeFormatterUtility.formatTime(getCurrentPosition()));
            }
            seekBarUpdateHandler.postDelayed(this, 100);
        }
    };

    public int getCurrentPosition() {
        return mediaPlayer.getCurrentPosition();

    }

    public void pauseMusic() {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            isPlaying = false;
        }
    }

    public void resumeMusic() {
        if (!mediaPlayer.isPlaying()) {
            mediaPlayer.start();
            isPlaying = true;
        }
    }

    public void stopMusic() {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
        }
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        prevNextSong(true);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }
}
