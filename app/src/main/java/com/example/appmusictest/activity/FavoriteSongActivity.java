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

    private static ArrayList<Song> favSongs = new ArrayList<>();
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
        setViewData();
    }

    private void getDataServer() {
        DataService dataService = ApiService.getService();
        // name api
        Call<List<Song>> callback = dataService.getSongByPlaylist("1");
        callback.enqueue(new Callback<List<Song>>() {
            @Override
            public void onResponse(@NonNull Call<List<Song>> call, @NonNull Response<List<Song>> response) {

                favSongs = (ArrayList<Song>) response.body();
                songAdapter.notifyDataSetChanged();
                updateUi();
                myProgress.dismiss();
            }

            @Override
            public void onFailure(@NonNull Call<List<Song>> call, @NonNull Throwable t) {
                Log.d(TAG, "Fail to get data from server due to:" + t.getMessage() );
                myProgress.dismiss();
            }
        });
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