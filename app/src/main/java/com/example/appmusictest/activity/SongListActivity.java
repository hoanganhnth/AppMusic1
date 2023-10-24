package com.example.appmusictest.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
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
import com.example.appmusictest.fragment.NowPlayingFragment;
import com.example.appmusictest.model.Playlist;
import com.example.appmusictest.model.Song;
import com.example.appmusictest.service.ApiService;
import com.example.appmusictest.service.DataService;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

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
    private TextView shuffleTv, titlePlIv, numberSongTv;
    private ImageView imgPlIv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_song_list);
        initView();
//        if (savedInstanceState == null) {
//            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
//            NowPlayingFragment yourFragment = new NowPlayingFragment(); // Tạo một đối tượng Fragment
//            transaction.replace(R.id.playingFragment, yourFragment);
//            transaction.addToBackStack(null); // Thêm Fragment vào back stack
//            transaction.commit();
//        }

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
        backIb.setOnClickListener(v -> {
            onBackPressed();
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        startActivity(new Intent(this, MainActivity.class));
    }

    private void initView() {
        songArrayList = new ArrayList<>();
        recyclerView = findViewById(R.id.listsongRv);
        backIb = findViewById(R.id.backIb);
        favoriteIb = findViewById(R.id.favoriteIb);
        menuIb = findViewById(R.id.moreIv);
        shuffleTv = findViewById(R.id.buttonShuffleTv);
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
            public void onResponse(Call<List<Song>> call, Response<List<Song>> response) {

                songArrayList = (ArrayList<Song>) response.body();
                recyclerView.setAdapter(new SongAdapter(songArrayList));
                numberSongTv.setText(songArrayList.size() + " bài hát bởi Music App");
//                if (response.isSuccessful()) {
//                    // Chuyển đổi phản hồi thành dữ liệu JSON đẹp hơn bằng Gson
//                    Gson gson = new GsonBuilder().setPrettyPrinting().create();
//                    String jsonResponse = gson.toJson(response.body());
//
//                    // In dữ liệu JSON ra Logcat
//                    Log.d("JSON Response", jsonResponse);
//
//                    // Xử lý dữ liệu JSON dưới đây
//                } else {
//                    // Xử lý lỗi nếu có
//                    Log.e("API Error", "Error: " + response.code());
//                }
            }

            @Override
            public void onFailure(Call<List<Song>> call, Throwable t) {
                Log.d("SongListA", "fail to get data from server due to:" + t.getMessage() );
            }
        });
    }

}