package com.example.appmusictest.activity;

import static com.example.appmusictest.MyApplication.TYPE_AUTHOR;

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
import com.example.appmusictest.FavoriteHelper;
import com.example.appmusictest.R;
import com.example.appmusictest.adapter.AlbumAdapter;
import com.example.appmusictest.adapter.SongAdapter;
import com.example.appmusictest.dialog.MyProgress;
import com.example.appmusictest.model.Album;
import com.example.appmusictest.model.Author;
import com.example.appmusictest.model.Song;
import com.example.appmusictest.model.api.AlbumsResponse;
import com.example.appmusictest.model.api.SongsResponse;
import com.example.appmusictest.service.ApiService;
import com.example.appmusictest.service.DataService;

import java.util.ArrayList;


import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AuthorDetailActivity extends AppCompatActivity {

    private TextView nameAuthorTv, followTv, buttonShuffleTv,listAlbumTv;
    private ImageButton backIb;
    private ImageView imgBgIv;
    private Author author;
    private RecyclerView listSongRv,listAlbumRv;
    private SongAdapter songAdapter;
    private AlbumAdapter albumAdapter;
    private static ArrayList<Song> songArrayList;
    private ArrayList<Album> albumArrayList;
    private static final String TAG = "Author_Detail_Acti";
    private MyProgress myProgress;
    public static ArrayList<Song> getSongArrayList() {
        return songArrayList;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_author_detail);
        myProgress = new MyProgress(this);
        myProgress.show();
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
                FavoriteHelper.actionWithFav(this, MainActivity.getIdUser(),author.getId(), FavoriteHelper.TYPE_DELETE, TYPE_AUTHOR);
                followTv.setText(R.string.followAuthorTitle);
            } else {
                FavoriteHelper.actionWithFav(this, MainActivity.getIdUser(),author.getId(), FavoriteHelper.TYPE_ADD, TYPE_AUTHOR);
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
        listAlbumTv = findViewById(R.id.listAlbumTv);
        nameAuthorTv = findViewById(R.id.nameAuthorTv);
        followTv = findViewById(R.id.followTv);
        buttonShuffleTv = findViewById(R.id.buttonShuffleTv);
        backIb = findViewById(R.id.backIb);
        imgBgIv = findViewById(R.id.imgBgIv);
        listSongRv = findViewById(R.id.listSongRv);
        listAlbumRv = findViewById(R.id.listAlbumRv);
    }

    private void getDataIntent() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            author = bundle.getParcelable("author");
            getDataFromServer(author.getId());
        }
    }

    private void getDataFromServer(String idAuthor) {
        getDataListSong(idAuthor);
        getDataListAlbum(idAuthor);

    }

    private void getDataListAlbum(String idAuthor) {
        DataService dataService = ApiService.getService();
        Call<AlbumsResponse> callback = dataService.getAuthorAlbums(idAuthor);
        albumArrayList = new ArrayList<>();
        callback.enqueue(new Callback<AlbumsResponse>() {
            @Override
            public void onResponse(@NonNull Call<AlbumsResponse> call, @NonNull Response<AlbumsResponse> response) {
                if (response.code() == 200) {
                    assert response.body() != null;
                    if (response.body().getErrCode().equals("0")) {
                        albumArrayList = response.body().getAlbums();
                        albumAdapter = new AlbumAdapter(albumArrayList, AuthorDetailActivity.this);
                        listAlbumRv.setAdapter(albumAdapter);
                        if (albumArrayList.isEmpty()) {
                            listAlbumTv.setVisibility(View.GONE);
                        } else {
                            listAlbumTv.setVisibility(View.VISIBLE);
                        }
                    }
                }
                myProgress.dismiss();
            }

            @Override
            public void onFailure(@NonNull Call<AlbumsResponse> call, @NonNull Throwable t) {
                Log.d(TAG, "Fail to get data from server due to:" + t.getMessage() );
                myProgress.dismiss();
            }
        });
    }

    private void getDataListSong(String idAuthor) {
        DataService dataService = ApiService.getService();
        Call<SongsResponse> callback = dataService.getAuthorSongs(idAuthor);
        songArrayList = new ArrayList<>();
        callback.enqueue(new Callback<SongsResponse>() {
            @Override
            public void onResponse(@NonNull Call<SongsResponse> call, @NonNull Response<SongsResponse> response) {
                if (response.code() == 200) {
                    assert response.body() != null;
                    if (response.body().getErrCode().equals("0")) {
                        songArrayList = response.body().getSongs();
                        songAdapter = new SongAdapter(songArrayList, AuthorDetailActivity.this);
                        listSongRv.setAdapter(songAdapter);
                        if (songArrayList.isEmpty()) {
                            buttonShuffleTv.setVisibility(View.GONE);
                        }
                    }
                }
                myProgress.dismiss();
            }

            @Override
            public void onFailure(@NonNull Call<SongsResponse> call, @NonNull Throwable t) {
                Log.d(TAG, "Fail to get data from server due to:" + t.getMessage() );
                myProgress.dismiss();
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