package com.example.appmusictest.service;

import static com.example.appmusictest.MyApplication.CHANNEL_ID;

import static com.example.appmusictest.activity.MusicPlayerActivity.nowPlayingId;
import static com.example.appmusictest.activity.MusicPlayerActivity.repeat;
import static com.example.appmusictest.activity.MusicPlayerActivity.seekBar;
import static com.example.appmusictest.activity.MusicPlayerActivity.songPosition;
import static com.example.appmusictest.activity.MusicPlayerActivity.currentSongs;
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
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.appmusictest.MyApplication;
import com.example.appmusictest.NotificationReceiver;
import com.example.appmusictest.R;
import com.example.appmusictest.activity.MainActivity;
import com.example.appmusictest.activity.SearchActivity;
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

    public MediaPlayer getMediaPlayer() {
        return mediaPlayer;
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
        Log.d(TAG, "On create");
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
        Log.d(TAG, "stop service");
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
        Log.d(TAG, "service destroy");
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
                .setSmallIcon(R.drawable.ic_music)
                .setContentTitle(MusicPlayerActivity.currentSongs.get(MusicPlayerActivity.songPosition).getTitle())
                .setContentText(MusicPlayerActivity.currentSongs.get(MusicPlayerActivity.songPosition).getNameAuthor())
                .setStyle(new androidx.media.app.NotificationCompat.MediaStyle().setMediaSession(mediaSessionCompat.getSessionToken()))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setOnlyAlertOnce(true)
                .addAction(R.drawable.ic_previous, "Previous", prevPendingIntent)
                .addAction(playPauseBtn, "Play", playPendingIntent)
                .addAction(R.drawable.ic_next, "Next", nextPendingIntent)
                .addAction(exit, "Exit", exitPendingIntent)
                .build();
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
            float playbackSpeed;
            if (isPlaying) {
                playbackSpeed = 1.0f;
            } else {
                playbackSpeed = 0.0f;
            }

            mediaSessionCompat.setMetadata(new MediaMetadataCompat.Builder()
                    .putLong(MediaMetadataCompat.METADATA_KEY_DURATION, getDuration()) // Đặt tổng thời gian
                    .build());
            PlaybackStateCompat.Builder playbackStateBuilder = new PlaybackStateCompat.Builder()
                    .setState(PlaybackStateCompat.STATE_PLAYING, getCurrentPosition(), playbackSpeed) // Ví dụ: STATE_PLAYING và vị trí hiện tại
                    .setActions(PlaybackStateCompat.ACTION_PLAY | PlaybackStateCompat.ACTION_PAUSE | PlaybackStateCompat.ACTION_SEEK_TO);

            mediaSessionCompat.setPlaybackState(playbackStateBuilder.build());
            mediaSessionCompat.setCallback(new MediaSessionCompat.Callback() {

                @Override
                public boolean onMediaButtonEvent(Intent mediaButtonEvent) {
                    if (isPlaying) {
                        // Tạm dừng nhạc
                        pauseMusic();
                        showNotification(R.drawable.ic_play);
                    } else {
                        // Phát nhạc
                        resumeMusic();
                        showNotification(R.drawable.ic_pause_gray);
                    }
                    return super.onMediaButtonEvent(mediaButtonEvent);
                }

                @Override
                public void onSeekTo(long pos) {
                    super.onSeekTo(pos);
                    mediaPlayer.seekTo((int) pos);
                    PlaybackStateCompat.Builder playbackStateBuilder = new PlaybackStateCompat.Builder()
                            .setState(PlaybackStateCompat.STATE_PLAYING, mediaPlayer.getCurrentPosition(), playbackSpeed)
                            .setActions(PlaybackStateCompat.ACTION_SEEK_TO);
                    mediaSessionCompat.setPlaybackState(playbackStateBuilder.build());
                }
            });

        }

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
                    Intent intent = new Intent("music_control");
                    intent.putExtra("action", "prepare");
                    LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
//                    showNotification(R.drawable.ic_play);
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
            nowPlayingId = currentSongs.get(songPosition).getId();
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
            if (MusicPlayerActivity.checkSong()) {
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
        setSongPosition(b);
        playMusicFromUrl(currentSongs.get(songPosition).getPathUrl());
        Log.d(TAG, "Next song");
    }

    private void setSongPosition(boolean b) {
        if (!repeat) {
            if (b) {
                if (songPosition == currentSongs.size() - 1) {
                    songPosition = 0;
                } else {
                    songPosition++;
                }
            } else {
                if (songPosition == 0) {
                    songPosition = currentSongs.size() - 1;
                } else {
                    songPosition --;
                }
            }
        }
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
