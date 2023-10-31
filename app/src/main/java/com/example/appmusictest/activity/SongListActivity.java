package com.example.appmusictest.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.appmusictest.R;
import com.example.appmusictest.adapter.SongAdapter;
import com.example.appmusictest.model.Playlist;
import com.example.appmusictest.model.Song;
import com.example.appmusictest.service.ApiService;
import com.example.appmusictest.service.DataService;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SongListActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    public static ArrayList<Song> songArrayList;
    private Playlist playlist;
    private ImageButton backIb, favoriteIb, menuIb;
    private TextView shuffleBtn, titlePlIv, numberSongTv;
    private ImageView imgPlIv;
    private static final String TAG = "Song_List_Activity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_song_list);
        initView();
        getDataIntent();
        getDataFromServer(playlist.getId());
        setViewData();

    }

    private void setViewData() {
        titlePlIv.setText(playlist.getTitle());
        Glide.with(this)
                .load(playlist.getArtUrl())
                .placeholder(R.mipmap.ic_launcher_round)
                .into(imgPlIv);
        backIb.setOnClickListener(v -> onBackPressed());

        shuffleBtn.setOnClickListener(v -> {
            Intent intent = new Intent(this, MusicPlayerActivity.class);
            intent.putExtra("class","SongListActivity");
            intent.putExtra("index", 0);
            startActivity(intent);
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private void initView() {
        songArrayList = new ArrayList<>();
        recyclerView = findViewById(R.id.listsongRv);
        backIb = findViewById(R.id.backIb);
        favoriteIb = findViewById(R.id.favoriteIb);
        menuIb = findViewById(R.id.moreIv);
        shuffleBtn = findViewById(R.id.buttonShuffleTv);
        numberSongTv = findViewById(R.id.numberSongTv);
        imgPlIv = findViewById(R.id.imgPlIv);
        titlePlIv = findViewById(R.id.titlePlTv);
    }

    private void getDataIntent() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            playlist = (Playlist) bundle.getSerializable("playlist");
        }
    }

    private void getDataFromServer(String idPlaylist) {
        DataService dataService = ApiService.getService();
        Call<List<Song>> callback = dataService.getSongByPlaylist(idPlaylist);
        callback.enqueue(new Callback<List<Song>>() {
            @Override
            public void onResponse(@NonNull Call<List<Song>> call, @NonNull Response<List<Song>> response) {

                songArrayList = (ArrayList<Song>) response.body();
                recyclerView.setAdapter(new SongAdapter(songArrayList));
                numberSongTv.setText(songArrayList.size() + " " + getString(R.string.playlist_title));
            }

            @Override
            public void onFailure(@NonNull Call<List<Song>> call, @NonNull Throwable t) {
                Log.d(TAG, "Fail to get data from server due to:" + t.getMessage() );
            }
        });
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

}