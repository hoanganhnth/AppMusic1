package com.example.appmusictest.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.appmusictest.R;
import com.example.appmusictest.adapter.SongAdapter;
import com.example.appmusictest.model.Author;
import com.example.appmusictest.model.Song;
import com.example.appmusictest.service.ApiService;
import com.example.appmusictest.service.DataService;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AuthorDetailActivity extends AppCompatActivity {

    private TextView nameAuthorTv, followTv, buttonShuffleTv;
    private ImageButton backIb;
    private ImageView imgBgIv;
    private Author author;
    private RecyclerView listSongRv;
    private SongAdapter songAdapter;
    private static ArrayList<Song> songArrayList;
    private static final String TAG = "Author_Detail_Acti";

    public static ArrayList<Song> getSongArrayList() {
        return songArrayList;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_author_detail);
        initView();
        getDataIntent();
        setViewData();

    }

    private void setViewData() {
        nameAuthorTv.setText(author.getName());
        backIb.setOnClickListener(v -> onBackPressed());
        Glide.with(this)
                .load(author.getUrlArt())
                .into(imgBgIv);
        followTv.setOnClickListener(v -> {
            if (FavoriteAuthorActivity.isInFav(author)) {
                FavoriteAuthorActivity.removeAuthor(author);
                followTv.setText(R.string.followAuthorTitle);
            } else {
                FavoriteAuthorActivity.addAuthor(author);
                followTv.setText(R.string.followedAuthorTitle);
            }
        });
        buttonShuffleTv.setOnClickListener(v -> {
            Intent intent = new Intent(this, MusicPlayerActivity.class);
            intent.putExtra("class","AuthorDetailActivity");
            intent.putExtra("index", 0);
            startActivity(intent);
        });
    }

    private void initView() {
        nameAuthorTv = findViewById(R.id.nameAuthorTv);
        followTv = findViewById(R.id.followTv);
        buttonShuffleTv = findViewById(R.id.buttonShuffleTv);
        backIb = findViewById(R.id.backIb);
        imgBgIv = findViewById(R.id.imgBgIv);
        listSongRv = findViewById(R.id.listSongRv);
    }

    private void getDataIntent() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            author = bundle.getParcelable("author");
        }
        getDataFromServer(author.getId());
    }

    private void getDataFromServer(String idAuthor) {
        DataService dataService = ApiService.getService();
        Call<List<Song>> callback = dataService.getSongByPlaylist(idAuthor);
        songArrayList = new ArrayList<>();
        callback.enqueue(new Callback<List<Song>>() {
            @Override
            public void onResponse(@NonNull Call<List<Song>> call, @NonNull Response<List<Song>> response) {
                songArrayList = (ArrayList<Song>) response.body();
                songAdapter = new SongAdapter(songArrayList, AuthorDetailActivity.this);
                listSongRv.setAdapter(songAdapter);

            }

            @Override
            public void onFailure(@NonNull Call<List<Song>> call, @NonNull Throwable t) {
                Log.d(TAG, "Fail to get data from server due to:" + t.getMessage() );
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (FavoriteAuthorActivity.isInFav(author)) {
            followTv.setText(R.string.followedAuthorTitle);
        } else {
            followTv.setText(R.string.followAuthorTitle);
        }
    }
}