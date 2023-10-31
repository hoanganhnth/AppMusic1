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
import com.example.appmusictest.fragment.ListPlayFragment;
import com.example.appmusictest.utilities.TimeFormatterUtility;
import com.example.appmusictest.fragment.DiskFragment;
import com.example.appmusictest.fragment.InfoSongFragment;
import com.example.appmusictest.model.Song;
import com.example.appmusictest.service.MusicPlayerService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;


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
    private DiskFragment diskFragment;
    private InfoSongFragment infoSongFragment;
    private ListPlayFragment listPlayFragment;
    public static MusicPlayerService musicPlayerService;
    public static ArrayList<Song> originalSongs;
    public static ArrayList<Song> currentSongs;
    public static int songPosition = 0;
    public static boolean repeat = false;
    public static String nowPlayingId = "";
    public boolean isFavorite = false;
    private BroadcastReceiver broadcastReceiver;
    public static boolean activeShuffle = false;
    private static final String TAG = "Music_Player_Activity";
    private boolean isShuffled = false;

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
        listPlayFragment = new ListPlayFragment();
        infoSongFragment = new InfoSongFragment();
        fragmentArrayList.add(infoSongFragment);
        fragmentArrayList.add(diskFragment);
        fragmentArrayList.add(listPlayFragment);
        viewPaperPlaylistFragmentAdapter = new ViewPaperPlaylistFragmentAdapter(this, fragmentArrayList);
        viewPager2.setAdapter(viewPaperPlaylistFragmentAdapter);
        viewPager2.setCurrentItem(1);
    }

    private void setViewData() {
        songPosition = getIntent().getIntExtra("index", 0);
        switch (getIntent().getStringExtra("class")) {
            case "SongAdapter":
                initServiceAndPlaylist(SongListActivity.songArrayList,false,true);
                break;
            case "NowPlaying":
                initServiceAndPlaylist(null,false, false);
                break;
            case "SongListActivity":
                initServiceAndPlaylist(SongListActivity.songArrayList, true, true);
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
        shuffleIb.setOnClickListener(v -> {
            if (!activeShuffle) {
                activeShuffle = true;
                shuffleSongs();
                shuffleIb.setColorFilter(ContextCompat.getColor(this,R.color.purple_500));
                Log.d(TAG, "Active shuffle");
            }
            else {
                activeShuffle = false;
                refreshSongs();
                shuffleIb.setColorFilter(ContextCompat.getColor(this,R.color.grayBt));
                Log.d(TAG, "Refresh songs");
            }
            listPlayFragment.onSongChanged();
        });
        listSongIb.setOnClickListener(v -> viewPager2.setCurrentItem(2));
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    if (checkSong()) {
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

        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals("music_control")) {
                    String action = intent.getStringExtra("action");
                    switch (action) {
                        case "play":
                            setDuration();
                            playIb.setImageResource(R.drawable.ic_pause_gray);
                            break;
                        case "prepare":
                            updateLayout();
                            listPlayFragment.onSongChanged();
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
    }

    private void refreshSongs() {
        currentSongs.clear();
        currentSongs.addAll(originalSongs);
        for (int i = 0; i < currentSongs.size(); i++) {
            if (currentSongs.get(i).getId().equals(nowPlayingId)) {
                songPosition = i;
                break;
            }
        }
    }

    private void shuffleSongs() {
        Song song1 = currentSongs.get(0);
        currentSongs.set(0, currentSongs.get(songPosition));
        currentSongs.set(songPosition, song1);
        List<Song> remainingSongs = currentSongs.subList(1, currentSongs.size());
        Collections.shuffle(remainingSongs, new Random());
        songPosition = 0;
        nowPlayingId = currentSongs.get(0).getId();
        Log.d(TAG, "Shuffle list");

    }

    private void setDuration() {
        endDirectionTv.setText(TimeFormatterUtility.formatTime(musicPlayerService.getDuration()));
        seekBar.setMax(musicPlayerService.getDuration());
    }

    public void prevNextSong(boolean b) {
        if (musicPlayerService != null) {
            musicPlayerService.nextSong(b);
        }
    }

    private void initServiceAndPlaylist(ArrayList<Song> playlist, boolean shuffle, boolean isNewSong) {
        if (isNewSong) {
            currentSongs = new ArrayList<>();
            originalSongs = new ArrayList<>();
            currentSongs.addAll(playlist);
            originalSongs.addAll(playlist);
        }
        if (shuffle) {
            Collections.shuffle(currentSongs);
            isShuffled = true;
            Log.d(TAG, "isShuffled is true");
        }
        initLayout();
        Intent intent = new Intent(this, MusicPlayerService.class);
        startService(intent);
        bindService(intent, this, Context.BIND_AUTO_CREATE);
    }

    private void updateLayout() {
        nameSongTv.setText(currentSongs.get(songPosition).getTitle());
        authorSongTv.setText(currentSongs.get(songPosition).getNameAuthor());
        diskFragment.setArtUrl(currentSongs.get(songPosition).getArtUrl());
        diskFragment.updateImage();
        startDirectionTv.setText(R.string.start_direction_title);
        endDirectionTv.setText(R.string.end_direction_title);
        playIb.setImageResource(R.drawable.ic_play);
        seekBar.setProgress(0);
        Log.d(TAG, "update layout");
    }

    private void initLayout() {
        nameSongTv.setText(currentSongs.get(songPosition).getTitle());
        authorSongTv.setText(currentSongs.get(songPosition).getNameAuthor());
        diskFragment.setArtUrl(currentSongs.get(songPosition).getArtUrl());
        if (repeat) {
            repeatIb.setColorFilter(ContextCompat.getColor(getApplicationContext(),R.color.purple_500));
        } else {
            repeatIb.setColorFilter(ContextCompat.getColor(getApplicationContext(),R.color.grayBt));
        }
        if (activeShuffle) {
            shuffleIb.setColorFilter(ContextCompat.getColor(getApplicationContext(),R.color.purple_500));
        } else {
            shuffleIb.setColorFilter(ContextCompat.getColor(getApplicationContext(),R.color.grayBt));
        }
        Log.d(TAG, "init layout");
    }

    private void setLayoutPlaying() {
        if (musicPlayerService.isPlaying()) {
            playIb.setImageResource(R.drawable.ic_pause_gray);
        } else {
            playIb.setImageResource(R.drawable.ic_play);
        }
        setDuration();
        startDirectionTv.setText(TimeFormatterUtility.formatTime(musicPlayerService.getCurrentPosition()));
        seekBar.setProgress(musicPlayerService.getCurrentPosition());
    }

    private void clickPauseOrResume() {
        if (checkSong()) {
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
            return fragments.get(position);
        }

        @Override
        public int getItemCount() {
            return fragments.size();
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
            Log.d(TAG, "Service is connected");
        }
        if (!isShuffled && checkSong()) {
            setLayoutPlaying();
        }
        else {
            musicPlayerService.playMusicFromUrl(currentSongs.get(songPosition).getPathUrl());
            Log.d(TAG, "play new song ");
            if (activeShuffle) {
                shuffleSongs();
            }
        }
    }

    public static boolean checkSong() {
        return currentSongs.get(songPosition).getId().equals(nowPlayingId);
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        musicPlayerService = null;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(getApplicationContext()).unregisterReceiver(broadcastReceiver);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }
}