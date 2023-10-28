package com.example.appmusictest;

import static com.example.appmusictest.activity.MusicPlayerActivity.nowPlayingId;
import static com.example.appmusictest.activity.MusicPlayerActivity.songPosition;
import static com.example.appmusictest.activity.MusicPlayerActivity.songs;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.bumptech.glide.Glide;
import com.example.appmusictest.activity.MusicPlayerActivity;
import com.example.appmusictest.fragment.NowPlayingFragment;

public class NotificationReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        if (intent != null && intent.getAction() != null) {
            String action = intent.getAction();

            if (MyApplication.PREVIOUS.equals(action)) {
                if (MusicPlayerActivity.songs.size() > 1) {
                    prevNextSong(false, context);
                }
            } else if (MyApplication.PLAY.equals(action)) {
                if (nowPlayingId.equals(songs.get(songPosition).getId())) {
                    if (MusicPlayerActivity.musicPlayerService.isPlaying()) {
                        pauseMusic();
                    } else {
                        playMusic();
                    }
                }

            } else if (MyApplication.NEXT.equals(action)) {
                if (MusicPlayerActivity.songs.size() > 1) {
                    prevNextSong(true, context);
                }
            } else if (MyApplication.EXIT.equals(action)) {
                exitApplication(context);
            }
        }
    }

    private void exitApplication(Context context) {
        if (MusicPlayerActivity.musicPlayerService != null) {
            MusicPlayerActivity.musicPlayerService.mediaPlayer.release();
            MusicPlayerActivity.musicPlayerService.mediaPlayer = null;
            MusicPlayerActivity.musicPlayerService.stopForeground(true);
            MusicPlayerActivity.musicPlayerService.stopService();
            MusicPlayerActivity.musicPlayerService = null;
        }
    }

    private void prevNextSong(boolean b, Context context) {
        MusicPlayerActivity.musicPlayerService.nextSong(b);
        Log.d("Notification Receiver", "next:" + b);
        MusicPlayerActivity.nameSongTv.setText(songs.get(songPosition).getTitle());
        MusicPlayerActivity.authorSongTv.setText(songs.get(songPosition).getNameAuthor());
        MusicPlayerActivity.diskFragment.setArtUrl(songs.get(songPosition).getArtUrl());
        MusicPlayerActivity.diskFragment.updateImage();

        NowPlayingFragment.nameSongTv.setText(songs.get(songPosition).getTitle());
        NowPlayingFragment.authorSongTv.setText(songs.get(songPosition).getNameAuthor());
        Glide.with(context)
                .load(songs.get(songPosition).getArtUrl())
                .placeholder(R.mipmap.ic_launcher_round)
                .into(NowPlayingFragment.circleImageView);
    }

    private void playMusic() {
        MusicPlayerActivity.musicPlayerService.resumeMusic();
        MusicPlayerActivity.playIb.setImageResource(R.drawable.ic_pause_gray);
        NowPlayingFragment.pausePlIb.setImageResource(R.drawable.ic_pause_gray);
        Log.d("Notification Receiver", "play");
    }

    private void pauseMusic() {
        MusicPlayerActivity.musicPlayerService.pauseMusic();
        MusicPlayerActivity.playIb.setImageResource(R.drawable.ic_play);
        NowPlayingFragment.pausePlIb.setImageResource(R.drawable.ic_play);
        Log.d("Notification Receiver", "pause");
    }
}
