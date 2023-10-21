package com.example.appmusictest.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.appmusictest.R;
import com.example.appmusictest.utilities.TimeFormatterUtility;
import com.example.appmusictest.fragment.DiskFragment;
import com.example.appmusictest.fragment.InforSongFragment;
import com.example.appmusictest.model.Song;
import com.example.appmusictest.service.MusicPlayerService;

import java.util.ArrayList;


public class MusicPlayerActivity extends AppCompatActivity implements ServiceConnection {

    public static TextView nameSongTv;
    public static TextView authorSongTv;
    public static TextView startDirectionTv;
    public static TextView endDirectionTv;
    private ImageButton backIb;
    private ImageButton menuIb;
    private ImageButton favoriteIb;
    private ImageButton listSongIb;
    private ImageButton shuffleIb;
    private ImageButton previousIb;
    private static ImageButton playIb;
    private ImageButton nextIb;
    private ImageButton repeatIb;
    public static SeekBar seekBar;
    private ViewPager2 viewPager2;
    private ViewPaperPlaylistFragmentAdapter viewPaperPlaylistFragmentAdapter;
    private ArrayList<Fragment> fragmentArrayList;
    private static DiskFragment diskFragment;
    private InforSongFragment inforSongFragment;
    public static MusicPlayerService musicPlayerService;
    public static ArrayList<Song> songs;
    public static int songPosition = 0;
    public boolean repeat = false;
    public static String nowPlayingId = "";
    public boolean isFavorite = false;
    private int direction = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_player);

        initView();
        initFragment();
        setViewData();
//        evenClick();

    }

    private void initView() {
        nameSongTv = findViewById(R.id.nameSongTv);
        authorSongTv = findViewById(R.id.authorSongTv);
        startDirectionTv = findViewById(R.id.startDirection);
        endDirectionTv = findViewById(R.id.endDirection);
        backIb = findViewById(R.id.backIb);
        menuIb = findViewById(R.id.menuIb);
        favoriteIb = findViewById(R.id.favoriteIb);
        listSongIb = findViewById(R.id.listSongIb);
        shuffleIb = findViewById(R.id.shuffleIb);
        previousIb = findViewById(R.id.previousIb);
        playIb = findViewById(R.id.playIb);
        nextIb = findViewById(R.id.nextIb);
        repeatIb = findViewById(R.id.repeatIb);
        seekBar = findViewById(R.id.seeBar);
        viewPager2 = findViewById(R.id.playMusicVp2);
    }

    private void initFragment() {
        fragmentArrayList = new ArrayList<>();
        diskFragment = new DiskFragment();
        inforSongFragment = new InforSongFragment();
        fragmentArrayList.add(inforSongFragment);
        fragmentArrayList.add(diskFragment);
        viewPaperPlaylistFragmentAdapter = new ViewPaperPlaylistFragmentAdapter(this, fragmentArrayList);
        viewPager2.setAdapter(viewPaperPlaylistFragmentAdapter);
        viewPager2.setCurrentItem(1);
    }

    private void setViewData() {
        songPosition = getIntent().getIntExtra("index", 0);
        Log.d("MUSIC PLAYER ACTIVITY", getIntent().getStringExtra("class"));
        switch (getIntent().getStringExtra("class")) {
            case "SongAdapter":
                initServiceAndPlaylist(SongListActivity.songArrayList,false,false);
                break;
            case "NowPlaying":
                Intent intent = new Intent(this, MusicPlayerService.class);
                bindService(intent, this, Context.BIND_AUTO_CREATE);
                setLayoutPlaying();
                break;
        }

        backIb.setOnClickListener(v -> {
            onBackPressed();
        });
        playIb.setOnClickListener( v-> {
            clickPauseOrResume();
        });
        nextIb.setOnClickListener(v -> {
            prevNextSong(true);

        });
        previousIb.setOnClickListener(v -> {
            prevNextSong(false);
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    musicPlayerService.mediaPlayer.seekTo(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });


    }


    public static void prevNextSong(boolean b) {
        setSongPosition(b);
        setLayout();
        diskFragment.updateImage();
        if (musicPlayerService != null) {
            musicPlayerService.playMusicFromUrl(songs.get(songPosition).getPathUrl());
        }
    }

    private static void setSongPosition(boolean b) {
        if (b) {
            if (songPosition == songs.size() - 1) {
                songPosition = 0;
            } else {
                songPosition++;
            }
        } else {
            if (songPosition == 0) {
                songPosition = songs.size() - 1;
            } else {
                songPosition --;
            }
        }
    }

    private void initServiceAndPlaylist(ArrayList<Song> playlist, boolean shuffle, boolean playNext) {
        Intent intent = new Intent(this, MusicPlayerService.class);
        startService(intent);
        bindService(intent, this, Context.BIND_AUTO_CREATE);
        songs = new ArrayList();
        songs.addAll(playlist);
        if (shuffle) {
            // shuffe music
        }
        setLayout();
        if (!playNext) {
            // play next music
        }
    }

    private static void setLayout() {
        nameSongTv.setText(songs.get(songPosition).getTitle());
        authorSongTv.setText(songs.get(songPosition).getNameAuthor());
        diskFragment.setArtUrl(songs.get(songPosition).getArtUrl());
//        diskFragment.setImageView(songs.get(songPosition).getArtUrl());
        playIb.setImageResource(R.drawable.ic_pause_gray);
        startDirectionTv.setText("00:00");
        endDirectionTv.setText("Loading...");
        seekBar.setProgress(0);
        Log.d("MUSIC PLAYER ACTIVITY", "set 00:00");
    }

    private void setLayoutPlaying() {
        nameSongTv.setText(songs.get(songPosition).getTitle());
        authorSongTv.setText(songs.get(songPosition).getNameAuthor());
        diskFragment.setArtUrl(songs.get(songPosition).getArtUrl());
    }

    private void clickPauseOrResume() {
        if (nowPlayingId.equals(songs.get(songPosition).getId())) {
            if (musicPlayerService.isPlaying()) {
                musicPlayerService.pauseMusic();
                playIb.setImageResource(R.drawable.ic_play);
                Log.d("MUSIC PLAYER ACTIVITY", "dung chs");
            } else {
                musicPlayerService.resumeMusic();
                playIb.setImageResource(R.drawable.ic_pause_gray);
                Log.d("MUSIC PLAYER ACTIVITY", "chs tiep");
            }
        }
    }




    private class ViewPaperPlaylistFragmentAdapter extends FragmentStateAdapter {

        private ArrayList<Fragment> fragments;

        public ViewPaperPlaylistFragmentAdapter(@NonNull FragmentActivity fragmentActivity, ArrayList<Fragment> fragments) {
            super(fragmentActivity);
            this.fragments = fragments;
        }

        public ViewPaperPlaylistFragmentAdapter(@NonNull Fragment fragment) {
            super(fragment);
        }

        public ViewPaperPlaylistFragmentAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
            super(fragmentManager, lifecycle);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            return fragmentArrayList.get(position);
        }

        @Override
        public int getItemCount() {
            return fragmentArrayList.size();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        if (musicPlayerService == null) {
            MusicPlayerService.LocalBinder binder = (MusicPlayerService.LocalBinder) service;
            musicPlayerService = binder.getService();

            if (musicPlayerService != null) {

            }
        }
        if (songs.get(songPosition).getId().equals(nowPlayingId)) {
            Log.d("MUSIC PLAYER ACTIVITY", "musicPlayerService is not null");
            if (musicPlayerService.isPlaying()) {
                playIb.setImageResource(R.drawable.ic_pause_gray);
            } else {
                playIb.setImageResource(R.drawable.ic_play);
            }
            startDirectionTv.setText(TimeFormatterUtility.formatTime(musicPlayerService.getCurrentPosition()));
            endDirectionTv.setText(TimeFormatterUtility.formatTime(musicPlayerService.getDuration()));
            seekBar.setMax(musicPlayerService.getDuration());
            seekBar.setProgress(musicPlayerService.getCurrentPosition());
        }
        else {
            musicPlayerService.playMusicFromUrl(songs.get(songPosition).getPathUrl());
//            musicPlayerService.showNotification();
//            musicPlayerService.updateSeekbar.run();
        }

    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
            musicPlayerService = null;
    }


    public static void timeSong(int direction) {
            endDirectionTv.setText(TimeFormatterUtility.formatTime(direction));
            seekBar.setMax(direction);

    }



}