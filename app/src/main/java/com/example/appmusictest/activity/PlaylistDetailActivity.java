package com.example.appmusictest.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

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

public class PlaylistDetailActivity extends AppCompatActivity {

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
        setContentView(R.layout.activity_playlist_detail);
        initView();
        getDataIntent();
        getDataFromServer(playlist.getId());
        setViewData();

    }

    private void setViewData() {
        titlePlIv.setText(playlist.getTitle());
        Glide.with(this)
                .load(playlist.getArtUrl())
                .placeholder(R.mipmap.music_player_icon)
                .into(imgPlIv);
        backIb.setOnClickListener(v -> onBackPressed());

        if (songArrayList.isEmpty()) {
            shuffleBtn.setVisibility(View.GONE);
        }
        shuffleBtn.setOnClickListener(v -> {
            Intent intent = new Intent(this, MusicPlayerActivity.class);
            intent.putExtra("class","SongListActivity");
            intent.putExtra("index", 0);
            startActivity(intent);
        });
        menuIb.setOnClickListener(v -> showDialog());

        favoriteIb.setOnClickListener(v -> {
            if (FavoritePlaylistActivity.isInFav(playlist)) {
                FavoritePlaylistActivity.removePlaylist(playlist);
                favoriteIb.setImageResource(R.drawable.ic_favorite_gray);
                Toast.makeText(this, R.string.remove_favorite_notification, Toast.LENGTH_SHORT).show();
            } else {
                FavoritePlaylistActivity.addPlaylist(playlist);
                favoriteIb.setImageResource(R.drawable.ic_favorite_purple);
                Toast.makeText(this, R.string.add_favorite_notification, Toast.LENGTH_SHORT).show();
            }
        });


    }

    private void showDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.song_list_bottom_layout);

        LinearLayout searchLn = dialog.findViewById(R.id.searchLn);
        LinearLayout addPlaySongsLn = dialog.findViewById(R.id.addPlaySongsLn);
        LinearLayout addFavLn = dialog.findViewById(R.id.addFavLn);
        TextView namePlaylist = dialog.findViewById(R.id.namePlTv);
        ImageButton addFavIb = dialog.findViewById(R.id.addFavIb);
        TextView addFavTv = dialog.findViewById(R.id.addFavTv);
        namePlaylist.setText(playlist.getTitle());
        ImageView imgIv = dialog.findViewById(R.id.imgPlIv);
        Glide.with(this)
                .load(playlist.getArtUrl())
                .placeholder(R.mipmap.ic_launcher_round)
                .into(imgIv);

        if (FavoritePlaylistActivity.isInFav(playlist)) {
            addFavTv.setText(R.string.delete_fav_title);
            addFavIb.setImageResource(R.drawable.ic_in_library);
            favoriteIb.setImageResource(R.drawable.ic_favorite_purple);
            Log.d(TAG , "favorite contain");
        }
        searchLn.setOnClickListener(v -> {
            Toast.makeText(this, "Tính năng đang trong quá trình phát triển", Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        });
        addPlaySongsLn.setOnClickListener(v -> {
            Toast.makeText(this, "Tính năng đang trong quá trình phát triển", Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        });
        addFavLn.setOnClickListener(v -> {
            if (!FavoritePlaylistActivity.isInFav(playlist)) {
                Toast.makeText(this, R.string.add_favorite_notification, Toast.LENGTH_SHORT).show();
                FavoritePlaylistActivity.addPlaylist(playlist);
                favoriteIb.setImageResource(R.drawable.ic_favorite_purple);
            } else {
                Toast.makeText(this, R.string.remove_favorite_notification, Toast.LENGTH_SHORT).show();
                FavoritePlaylistActivity.removePlaylist(playlist);
                favoriteIb.setImageResource(R.drawable.ic_favorite_gray);
            }
            dialog.dismiss();
        });

        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setGravity(Gravity.BOTTOM);
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
            playlist = bundle.getParcelable("playlist");
        }
    }

    private void getDataFromServer(String idPlaylist) {
        DataService dataService = ApiService.getService();
        Call<List<Song>> callback = dataService.getSongByPlaylist(idPlaylist);
        callback.enqueue(new Callback<List<Song>>() {
            @Override
            public void onResponse(@NonNull Call<List<Song>> call, @NonNull Response<List<Song>> response) {

                songArrayList = (ArrayList<Song>) response.body();
                recyclerView.setAdapter(new SongAdapter(songArrayList, PlaylistDetailActivity.this));
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

    @Override
    protected void onResume() {
        super.onResume();
        updateUi();
    }

    private void updateUi() {
        if (FavoritePlaylistActivity.isInFav(playlist)) {
            favoriteIb.setImageResource(R.drawable.ic_favorite_purple);
        } else {
            favoriteIb.setImageResource(R.drawable.ic_favorite_gray);
        }
    }
}