package com.example.appmusictest.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.appmusictest.R;
import com.example.appmusictest.utilities.TimeFormatterUtility;
import com.example.appmusictest.fragment.DiskFragment;
import com.example.appmusictest.fragment.InfoSongFragment;
import com.example.appmusictest.model.Song;
import com.example.appmusictest.service.MusicPlayerService;

import java.util.ArrayList;
import java.util.Collections;


public class MusicPlayerActivity extends AppCompatActivity implements ServiceConnection {

    private TextView nameSongTv;
    private TextView authorSongTv;
    public static TextView startDirectionTv;
    private static TextView endDirectionTv;
    private ImageButton backIb;
    private ImageButton menuIb;
    private ImageButton favoriteIb;
    private ImageButton listSongIb;
    private ImageButton shuffleIb;
    private ImageButton previousIb;
    private ImageButton playIb;
    private ImageButton nextIb;
    private ImageButton repeatIb;
    public static SeekBar seekBar;
    private ViewPager2 viewPager2;
    private ViewPaperPlaylistFragmentAdapter viewPaperPlaylistFragmentAdapter;
    private ArrayList<Fragment> fragmentArrayList;
    public static DiskFragment diskFragment;
    private InfoSongFragment infoSongFragment;
    public static MusicPlayerService musicPlayerService;
    public static ArrayList<Song> songs;
    public static int songPosition = 0;
    public static boolean repeat = false;
    public static String nowPlayingId = "";
    public boolean isFavorite = false;
    private BroadcastReceiver broadcastReceiver;
    //    private boolean isReceiverRegistered = false;
    private static final String TAG = "Music_Player_Activity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_player);

        initView();
        initFragment();
        setViewData();

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
        infoSongFragment = new InfoSongFragment();
        fragmentArrayList.add(infoSongFragment);
        fragmentArrayList.add(diskFragment);
        viewPaperPlaylistFragmentAdapter = new ViewPaperPlaylistFragmentAdapter(this, fragmentArrayList);
        viewPager2.setAdapter(viewPaperPlaylistFragmentAdapter);
        viewPager2.setCurrentItem(1);
    }

    private void setViewData() {
        songPosition = getIntent().getIntExtra("index", 0);
        switch (getIntent().getStringExtra("class")) {
            case "SongAdapter":
                initServiceAndPlaylist(SongListActivity.songArrayList,false,false);
                break;
            case "NowPlaying":
                Intent intent = new Intent(this, MusicPlayerService.class);
                bindService(intent, this, Context.BIND_AUTO_CREATE);
                setLayoutPlaying();
                break;
            case "SonsListActivity":
                initServiceAndPlaylist(SongListActivity.songArrayList, true, false);
                break;
        }

        backIb.setOnClickListener(v -> onBackPressed());
        playIb.setOnClickListener( v-> clickPauseOrResume());
        nextIb.setOnClickListener(v -> prevNextSong(true));
        previousIb.setOnClickListener(v -> prevNextSong(false));

        repeatIb.setOnClickListener(v -> {
            if (repeat) {
                repeat = false;
                repeatIb.setColorFilter(ContextCompat.getColor(this,R.color.grayBt));
            }
            else {
                repeat = true;
                repeatIb.setColorFilter(ContextCompat.getColor(this,R.color.purple_500));
            }
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    if (musicPlayerService.isPlaying()) {
                        musicPlayerService.mediaPlayer.seekTo(progress);
                    }
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

//        if (!isReceiverRegistered) {
            broadcastReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    if (intent.getAction().equals("music_control")) {
                        String action = intent.getStringExtra("action");
                        switch (action) {
                            case "next":
                                initLayout();
                                diskFragment.updateImage();
                                break;
                            case "play":
                                setDuration();
                                nowPlayingId = songs.get(songPosition).getId();
                                break;
                            case "pause":
                                playIb.setImageResource(R.drawable.ic_play);
                                break;
                            case "resume":
                                playIb.setImageResource(R.drawable.ic_pause_gray);
                                break;
                        }
                    }
                }
            };
        IntentFilter intentFilter = new IntentFilter("music_control");
        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(broadcastReceiver, intentFilter);
//            isReceiverRegistered = true;
//        }
    }

    private void setDuration() {
        endDirectionTv.setText(TimeFormatterUtility.formatTime(musicPlayerService.getDuration()));
        seekBar.setMax(musicPlayerService.getDuration());
    }

    public void prevNextSong(boolean b) {
        if (musicPlayerService != null) {
            musicPlayerService.nextSong(b);
            initLayout();
            diskFragment.updateImage();
        }
    }

    public static void setSongPosition(boolean b) {
        if (!repeat) {
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
    }

    private void initServiceAndPlaylist(ArrayList<Song> playlist, boolean shuffle, boolean playNext) {
        Intent intent = new Intent(this, MusicPlayerService.class);
        startService(intent);
        bindService(intent, this, Context.BIND_AUTO_CREATE);
        songs = new ArrayList<>();
        songs.addAll(playlist);
        if (shuffle) {
            Collections.shuffle(songs);
        }
        initLayout();
        if (!playNext) {
            // play next music
        }
    }

    private void initLayout() {

        nameSongTv.setText(songs.get(songPosition).getTitle());
        authorSongTv.setText(songs.get(songPosition).getNameAuthor());
        diskFragment.setArtUrl(songs.get(songPosition).getArtUrl());

        if (repeat) {
            repeatIb.setColorFilter(ContextCompat.getColor(getApplicationContext(),R.color.purple_500));
        } else {
            repeatIb.setColorFilter(ContextCompat.getColor(getApplicationContext(),R.color.grayBt));
        }
        playIb.setImageResource(R.drawable.ic_pause_gray);
        startDirectionTv.setText(R.string.start_direction_title);
        endDirectionTv.setText(R.string.end_direction_title);
        seekBar.setProgress(0);
//        Log.d("MUSIC PLAYER ACTIVITY", "set 00:00");
    }

    private void setLayoutPlaying() {
        nameSongTv.setText(songs.get(songPosition).getTitle());
        authorSongTv.setText(songs.get(songPosition).getNameAuthor());
        diskFragment.setArtUrl(songs.get(songPosition).getArtUrl());
        if (repeat) {
            repeatIb.setColorFilter(ContextCompat.getColor(getApplicationContext(),R.color.purple_500));
        } else {
            repeatIb.setColorFilter(ContextCompat.getColor(getApplicationContext(),R.color.grayBt));
        }
    }

    private void clickPauseOrResume() {
        if (nowPlayingId.equals(songs.get(songPosition).getId())) {
            if (musicPlayerService.isPlaying()) {
                musicPlayerService.pauseMusic();
                playIb.setImageResource(R.drawable.ic_play);
                Log.d(TAG, "pause");
            } else {
                musicPlayerService.resumeMusic();
                playIb.setImageResource(R.drawable.ic_pause_gray);
                Log.d(TAG, "resume");
            }
        }
    }

    private class ViewPaperPlaylistFragmentAdapter extends FragmentStateAdapter {

        private ArrayList<Fragment> fragments;

        public ViewPaperPlaylistFragmentAdapter(@NonNull FragmentActivity fragmentActivity, ArrayList<Fragment> fragments) {
            super(fragmentActivity);
            this.fragments = fragments;
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
        }
        if (songs.get(songPosition).getId().equals(nowPlayingId)) {
            Log.d(TAG, "Service is connected");
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
        }

    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
            musicPlayerService = null;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        }

    @Override
    protected void onStop() {
        super.onStop();
        LocalBroadcastManager.getInstance(getApplicationContext()).unregisterReceiver(broadcastReceiver);
    }
}