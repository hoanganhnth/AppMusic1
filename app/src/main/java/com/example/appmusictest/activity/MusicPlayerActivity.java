package com.example.appmusictest.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.audiofx.AudioEffect;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.appmusictest.FavoriteHelper;
import com.example.appmusictest.MyApplication;
import com.example.appmusictest.NotificationReceiver;
import com.example.appmusictest.R;
import com.example.appmusictest.adapter.AuthorAdapter;
import com.example.appmusictest.adapter.PlaylistAddAdapter;
import com.example.appmusictest.dialog.MyCreatePlaylistDialog;
import com.example.appmusictest.fragment.ListPlayFragment;
import com.example.appmusictest.model.Author;
import com.example.appmusictest.model.Playlist;
import com.example.appmusictest.model.api.AuthorsResponse;
import com.example.appmusictest.service.ApiService;
import com.example.appmusictest.service.DataService;
import com.example.appmusictest.utilities.TimeFormatterUtility;
import com.example.appmusictest.fragment.DiskFragment;
import com.example.appmusictest.fragment.InfoSongFragment;
import com.example.appmusictest.model.Song;
import com.example.appmusictest.service.MusicPlayerService;
import com.google.android.material.imageview.ShapeableImageView;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


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
    private static int originalPos = 0;
    private static boolean isSetTimer = false;


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
        songPosition = getIntent().getExtras().getInt("index");
        switch (getIntent().getExtras().getString("class")) {
            case "SongAdapter":
                initServiceAndPlaylist(getIntent().getExtras().getParcelableArrayList("songs"),false,true);
                break;
            case "NowPlaying":
                initServiceAndPlaylist(null,false, false);
                break;
            case "PlaylistAlbumDetailActivity":
                initServiceAndPlaylist(PlaylistAlbumDetailActivity.songArrayList, true, true);
                break;
            case "FavoriteSongActivity":
                initServiceAndPlaylist(FavoriteSongActivity.getFavSongs(), false, true);
                break;
            case "ShuffleFavoriteSongActivity":
                initServiceAndPlaylist(FavoriteSongActivity.getFavSongs(), true, true);
                break;
            case "AuthorDetailActivity":
                initServiceAndPlaylist(AuthorDetailActivity.getSongArrayList(), true, true);
                break;
        }
        infoSongFragment.setSong(currentSongs.get(songPosition));
        backIb.setOnClickListener(v -> onBackPressed());
        playIb.setOnClickListener( v-> clickPauseOrResume());
        nextIb.setOnClickListener(v -> prevNextSong(true));
        previousIb.setOnClickListener(v -> prevNextSong(false));

        repeatIb.setOnClickListener(v -> {
            if (repeat) {
                repeat = false;
                repeatIb.setColorFilter(ContextCompat.getColor(this,R.color.action_bar));
            }
            else {
                repeat = true;
                repeatIb.setColorFilter(ContextCompat.getColor(this,R.color.fav_playlist_color));
            }
        });
        shuffleIb.setOnClickListener(v -> {
            if (!activeShuffle) {
                activeShuffle = true;
                shuffleSongs();
                shuffleIb.setColorFilter(ContextCompat.getColor(this,R.color.fav_playlist_color));
                Log.d(TAG, "Active shuffle");
            }
            else {
                activeShuffle = false;
                refreshSongs();
                shuffleIb.setColorFilter(ContextCompat.getColor(this,R.color.action_bar));
                Log.d(TAG, "Refresh songs");
            }
            listPlayFragment.onSongChanged();
        });
        listSongIb.setOnClickListener(v -> viewPager2.setCurrentItem(2));

        menuIb.setOnClickListener(v -> {
            showDialog();
        });

        if (FavoriteSongActivity.isInFav(currentSongs.get(songPosition))) {
            favoriteIb.setImageResource(R.drawable.ic_favorite_purple);
        }
        favoriteIb.setOnClickListener(v -> {
            if (!FavoriteSongActivity.isInFav(currentSongs.get(songPosition))) {
                FavoriteHelper.actionWithFav(this,MainActivity.getIdUser(),currentSongs.get(songPosition).getId(), FavoriteHelper.TYPE_ADD, MyApplication.TYPE_SONG, currentSongs.get(songPosition));
                favoriteIb.setImageResource(R.drawable.ic_favorite_purple);
            } else {
                FavoriteHelper.actionWithFav(this,MainActivity.getIdUser(),currentSongs.get(songPosition).getId(), FavoriteHelper.TYPE_DELETE, MyApplication.TYPE_SONG, currentSongs.get(songPosition));
                favoriteIb.setImageResource(R.drawable.ic_favorite_gray);
            }
        });
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

    private void showDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.bottom_layout_music_player);

        LinearLayout setTimeLn = dialog.findViewById(R.id.setTimeLn);
        LinearLayout addPlaylistLn = dialog.findViewById(R.id.addPlaylistLn);
        LinearLayout setEqualizerLn = dialog.findViewById(R.id.setEqualizerLn);
        LinearLayout addFavoriteLn = dialog.findViewById(R.id.addFavoriteLn);
        LinearLayout seeAuthorLn = dialog.findViewById(R.id.seeAuthorLn);
        TextView setTimeTv = dialog.findViewById(R.id.setTimeTv);
        TextView nameSong = dialog.findViewById(R.id.nameSongTv);
        TextView addFavTv = dialog.findViewById(R.id.addFavTv);
        TextView authorSong = dialog.findViewById(R.id.authorSongTv);
        ImageView setTimeIb = dialog.findViewById(R.id.setTimeIb);
        ImageView addFavIb = dialog.findViewById(R.id.addFavIb);

        nameSong.setText(currentSongs.get(songPosition).getTitle());
        authorSong.setText(currentSongs.get(songPosition).getNameAuthor());
        ShapeableImageView imgIv = dialog.findViewById(R.id.imgDlIv);
        if (isSetTimer) {
            setTimeIb.setColorFilter(ContextCompat.getColor(this,R.color.fav_playlist_color));
            setTimeTv.setTextColor(ContextCompat.getColor(this,R.color.fav_playlist_color));
        }
        if (FavoriteSongActivity.isInFav(currentSongs.get(songPosition))) {
            addFavTv.setText(R.string.delete_fav_title);
            addFavIb.setImageResource(R.drawable.ic_in_library);
        }
        Glide.with(this)
                .load(currentSongs.get(songPosition).getArtUrl())
                .into(imgIv);
        setTimeLn.setOnClickListener( v -> {
            dialog.dismiss();
            showDialogTimer();
        });
        setEqualizerLn.setOnClickListener( v -> {
            try {
                Intent eqIntent = new Intent(AudioEffect.ACTION_DISPLAY_AUDIO_EFFECT_CONTROL_PANEL);
                eqIntent.putExtra(AudioEffect.EXTRA_AUDIO_SESSION, musicPlayerService.getMediaPlayer().getAudioSessionId());
                eqIntent.putExtra(AudioEffect.EXTRA_PACKAGE_NAME, getBaseContext().getPackageName());
                eqIntent.putExtra(AudioEffect.EXTRA_CONTENT_TYPE, AudioEffect.CONTENT_TYPE_MUSIC);
                startActivityForResult(eqIntent, 13);
            } catch (Exception e) {
                Toast.makeText(this, "Equalizer Feature not Supported!!", Toast.LENGTH_SHORT).show();
            }


            dialog.dismiss();
        });
        addFavoriteLn.setOnClickListener( v -> {
            if (!FavoriteSongActivity.isInFav(currentSongs.get(songPosition))) {
                FavoriteHelper.actionWithFav(this,MainActivity.getIdUser(),currentSongs.get(songPosition).getId(), FavoriteHelper.TYPE_ADD, MyApplication.TYPE_SONG, currentSongs.get(songPosition));
            } else {
                FavoriteHelper.actionWithFav(this,MainActivity.getIdUser(),currentSongs.get(songPosition).getId(), FavoriteHelper.TYPE_DELETE, MyApplication.TYPE_SONG, currentSongs.get(songPosition));
            }
            dialog.dismiss();
        });

        seeAuthorLn.setOnClickListener( v -> {
            getDataAuthor();
            dialog.dismiss();
        });

        addPlaylistLn.setOnClickListener(v -> {
            showAddPlaylistDialog();
            dialog.dismiss();
        });

        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setGravity(Gravity.BOTTOM);
    }

    private void getDataAuthor() {
        DataService dataService = ApiService.getService();
        Call<AuthorsResponse> callback = dataService.getSongAuthor(currentSongs.get(songPosition).getId());
        callback.enqueue(new Callback<AuthorsResponse>() {
            @Override
            public void onResponse(@NonNull Call<AuthorsResponse> call, @NonNull Response<AuthorsResponse> response) {
                if (response.code() == 200) {
                    if (response.body().getErrCode().equals("0")) {
                        ArrayList<Author> authors = response.body().getAuthors();
                        if (authors.size() == 1) {
                            Intent intent = new Intent(MusicPlayerActivity.this, AuthorDetailActivity.class);
                            Bundle bundle = new Bundle();
                            bundle.putParcelable("author", authors.get(0));
                            intent.putExtras(bundle);
                            startActivity(intent);
                        } else if (authors.size() > 1) {
                            showAuthorsDialog(authors);
                        }
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<AuthorsResponse> call, @NonNull Throwable t) {
                Log.d(TAG, "Fail to get data from server due to:" + t.getMessage() );
            }
        });
    }

    private void showAuthorsDialog(ArrayList<Author> authors) {
        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.bottom_layout_authors);
        RecyclerView authorsRv = dialog.findViewById(R.id.authorsRv);
        AuthorAdapter authorAdapter = new AuthorAdapter(authors, this);
        authorsRv.setAdapter(authorAdapter);
        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setGravity(Gravity.BOTTOM);
    }

    private void showAddPlaylistDialog() {
        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.bottom_layout_add_to_playlist);
        RelativeLayout addPlaylistRl = dialog.findViewById(R.id.addPlaylistRl);
        RecyclerView playlistFavRv = dialog.findViewById(R.id.playlistFavRv);
        PlaylistAddAdapter playlistAdapter = new PlaylistAddAdapter(FavoritePlaylistActivity.getPlaylistByUser(), this, currentSongs.get(songPosition).getId());
        playlistFavRv.setAdapter(playlistAdapter);
        addPlaylistRl.setOnClickListener(v -> {
            dialog.dismiss();
            showCreatePlaylistDialog(this);
        });
        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setGravity(Gravity.BOTTOM);
    }

    private void showCreatePlaylistDialog(Context context) {
        MyCreatePlaylistDialog myCreatePlaylistDialog = new MyCreatePlaylistDialog(context, null);
        myCreatePlaylistDialog.show();
    }

    private void showDialogTimer() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.bottom_layout_timer);
        LinearLayout statusTv = dialog.findViewById(R.id.statusTimerLn);
        TextView timeRemainingTV = dialog.findViewById(R.id.timeRemainingTv);
        TextView setQuarterfTv = dialog.findViewById(R.id.setQuarterTv);
        TextView setHalfTv = dialog.findViewById(R.id.setHalfTv);
        TextView setOneTv = dialog.findViewById(R.id.setOneTv);

        if (isSetTimer) {
            statusTv.setVisibility(View.VISIBLE);
//            timeRemainingTV.setText("(Còn lại " + "15" + " phút)");
            timeRemainingTV.setVisibility(View.GONE);
            statusTv.setOnClickListener(v -> {
                isSetTimer = false;
                dialog.dismiss();
                Toast.makeText(this, "Đã tắt hẹn giờ", Toast.LENGTH_SHORT).show();
            });
        } else {
            statusTv.setVisibility(View.GONE);
        }

        setQuarterfTv.setOnClickListener(v -> {
            Toast.makeText(this, "Âm nhạc sẽ tắt sau 15 phút", Toast.LENGTH_SHORT).show();
            isSetTimer = true;
            initTimer(1);
            dialog.dismiss();
        });
        setHalfTv.setOnClickListener(v -> {
            Toast.makeText(this, "Âm nhạc sẽ tắt sau 30 phút", Toast.LENGTH_SHORT).show();
            isSetTimer = true;
            initTimer(30);
            dialog.dismiss();
        });
        setOneTv.setOnClickListener(v -> {
            Toast.makeText(this, "Âm nhạc sẽ tắt sau 1 giờ", Toast.LENGTH_SHORT).show();
            isSetTimer = true;
            initTimer(60);
            dialog.dismiss();
        });


        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setGravity(Gravity.BOTTOM);
    }


    private void initTimer(int sec) {
        new Thread(() -> {
            try {
                Thread.sleep(sec * 60000L);
                if (isSetTimer) {
                    NotificationReceiver.exitApplication(this);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
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
            repeatIb.setColorFilter(ContextCompat.getColor(getApplicationContext(),R.color.fav_playlist_color));
        } else {
            repeatIb.setColorFilter(ContextCompat.getColor(getApplicationContext(),R.color.action_bar));
        }
        if (activeShuffle) {
            shuffleIb.setColorFilter(ContextCompat.getColor(getApplicationContext(),R.color.fav_playlist_color));
        } else {
            shuffleIb.setColorFilter(ContextCompat.getColor(getApplicationContext(),R.color.action_bar));
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
        Log.d(TAG, "set layoutPlaying");
    }

    private void clickPauseOrResume() {
        if (musicPlayerService == null) {
            Intent intent = new Intent(this, MusicPlayerService.class);
            startService(intent);
            bindService(intent, this, Context.BIND_AUTO_CREATE);
        } else {
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
    @Override
    public void onServiceDisconnected(ComponentName name) {
        musicPlayerService = null;
        playIb.setImageResource(R.drawable.ic_play);
        Log.d(TAG, "onServiceDisconnected");
    }

    public static boolean checkSong() {
        return currentSongs.get(songPosition).getId().equals(nowPlayingId);
    }

    public static int originalPos() {
        for (int i = 0; i < originalSongs.size(); i ++) {
            if (currentSongs.get(songPosition).getId().equals(originalSongs.get(i).getId())) {
                originalPos = i;
                break;
            }
        }
        return originalPos;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 13 || resultCode == RESULT_OK) {
        }
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