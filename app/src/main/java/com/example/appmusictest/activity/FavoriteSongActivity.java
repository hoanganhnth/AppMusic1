package com.example.appmusictest.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.appmusictest.R;
import com.example.appmusictest.adapter.SongAdapter;
import com.example.appmusictest.dialog.MyProgress;
import com.example.appmusictest.model.Playlist;
import com.example.appmusictest.model.Song;
import com.example.appmusictest.service.ApiService;
import com.example.appmusictest.service.DataService;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FavoriteSongActivity extends AppCompatActivity {

    private static ArrayList<Song> favSongs;
    private SongAdapter songAdapter;
    private RecyclerView songFavRv;
    private TextView numberSongTv,buttonShuffleTv;
    private ImageButton backIb;
    private MyProgress myProgress;
    private final static String TAG = "Favorite_Song_Activity";

    public static ArrayList<Song> getFavSongs() {
        return favSongs;
    }

    public static void setFavSongs(ArrayList<Song> favSongs) {
        FavoriteSongActivity.favSongs = favSongs;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite_song);

        myProgress = new MyProgress(this);
        myProgress.show();
        initView();
//        getData();
        setViewData();
    }

    private void getData() {
        if (getIntent() != null) {
            favSongs = getIntent().getParcelableArrayListExtra("favSongs");
            if (favSongs == null) favSongs = new ArrayList<>();
        }
    }

    private void setViewData() {
        songAdapter = new SongAdapter(favSongs, this);
        songFavRv.setAdapter(songAdapter);
        updateUi();
        backIb.setOnClickListener(v -> {
            onBackPressed();
        });
        buttonShuffleTv.setOnClickListener(v -> {
            Intent intent = new Intent(this, MusicPlayerActivity.class);
            intent.putExtra("class","FavoriteSongActivity");
            intent.putExtra("index", 0);
            startActivity(intent);
        });
        myProgress.dismiss();

    }

    public void updateUi() {
        if (!favSongs.isEmpty()) {
            buttonShuffleTv.setVisibility(View.VISIBLE);
            numberSongTv.setVisibility(View.VISIBLE);
            numberSongTv.setText("Tất cả " + favSongs.size() + " bài hát");
        }
    }


    private void initView() {
        songFavRv = findViewById(R.id.songFavRv);
        numberSongTv = findViewById(R.id.numberSongTv);
        backIb = findViewById(R.id.backIb);
        buttonShuffleTv = findViewById(R.id.buttonShuffleTv);
    }

    public static int getSize() {
        if (favSongs == null) favSongs = new ArrayList<>();
        return favSongs.size();
    }

    public static void addSong(Song song) {
        favSongs.add(song);
        Log.d(TAG, "size: " + getSize());
        Log.d(TAG, "added to favorite");
    }

    public static void removeSong(Song song) {
        Iterator<Song> iterator = favSongs.iterator();
        while (iterator.hasNext()) {
            Song obj = iterator.next();
            if (obj.getId().equals(song.getId())) {
                iterator.remove();
                break;
            }
        }
        Log.d(TAG, "remove from favorite");
    }
    public static boolean isInFav(Song song) {
        for (Song song1 : favSongs) {
            if (song1.getId().equals(song.getId())) {
                return true;
            }
        }
        return false;
    }
}