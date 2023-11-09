package com.example.appmusictest.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.appmusictest.R;
import com.example.appmusictest.adapter.SongAdapter;
import com.example.appmusictest.model.Playlist;
import com.example.appmusictest.model.Song;

import java.util.ArrayList;
import java.util.Iterator;

public class FavoriteSongActivity extends AppCompatActivity {

    private static ArrayList<Song> favSongs = new ArrayList<>();
    private SongAdapter songAdapter;
    private RecyclerView songFavRv;
    private TextView numberSongTv;
    private ImageButton backIb;
    private final static String TAG = "Favorite_Song_Activity";

    public static ArrayList<Song> getFavSongs() {
        return favSongs;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite_song);

        initView();
        setViewData();
    }

    private void setViewData() {

        songAdapter = new SongAdapter(favSongs);
        songFavRv.setAdapter(songAdapter);

        if (!favSongs.isEmpty()) {
            numberSongTv.setVisibility(View.VISIBLE);
            numberSongTv.setText("Tất cả " + favSongs.size() + " bài hát");
        }

        backIb.setOnClickListener(v -> {
            onBackPressed();
        });
    }

    private void initView() {
        songFavRv = findViewById(R.id.songFavRv);
        numberSongTv = findViewById(R.id.numberSongTv);
        backIb = findViewById(R.id.backIb);
    }

    public static void addSong(Song song) {
        favSongs.add(song);
        Log.d(TAG, "added to favorite");
    }

    public static void removeSong(Song song) {
        Iterator<Song> iterator = favSongs.iterator();
        while (iterator.hasNext()) {
            Song obj = iterator.next();
            if (obj.getId().equals(song.getId())) {
                iterator.remove();
            }
        }
        Log.d(TAG, "remove from favorite");
    }

    public static int getSize() {
        return favSongs.size();
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