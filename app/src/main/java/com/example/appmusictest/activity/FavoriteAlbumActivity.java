package com.example.appmusictest.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.appmusictest.R;
import com.example.appmusictest.adapter.AlbumAdapter;
import com.example.appmusictest.dialog.MyProgress;
import com.example.appmusictest.model.Album;


import java.util.ArrayList;
import java.util.Iterator;

public class FavoriteAlbumActivity extends AppCompatActivity {

    private static ArrayList<Album> favAlbum;
    private AlbumAdapter adapter;
    private RecyclerView albumFavRv;
    private TextView numberAlbumTv;
    private ImageButton backIb;
    private final static String TAG = "Favorite_Album_Ac";
    private MyProgress myProgress;

    public static ArrayList<Album> getFavAlbum() {
        return favAlbum;
    }

    public static void setFavAlbum(ArrayList<Album> favAlbum) {
        FavoriteAlbumActivity.favAlbum = favAlbum;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favforite_album);
        myProgress = new MyProgress(this);
        myProgress.show();
        initView();
//        getData();
        setViewData();
    }

    private void getData() {
        if (getIntent() != null) {
            favAlbum = getIntent().getParcelableArrayListExtra("favAlbums");
            if (favAlbum == null) favAlbum = new ArrayList<>();
        }
    }

    private void setViewData() {
        adapter = new AlbumAdapter(favAlbum, this);
        albumFavRv.setAdapter(adapter);

        if (!favAlbum.isEmpty()) {
            numberAlbumTv.setVisibility(View.VISIBLE);
            numberAlbumTv.setText("Tất cả " + favAlbum.size() + " album");
        }
        backIb.setOnClickListener(v -> {
            onBackPressed();
        });
        myProgress.dismiss();
    }

    private void initView() {
        albumFavRv = findViewById(R.id.albumFavRv);
        numberAlbumTv = findViewById(R.id.numberAlbumTv);
        backIb = findViewById(R.id.backIb);
    }

    public static void addAlbum(Album album) {
        favAlbum.add(album);
        Log.d(TAG, "added to favorite");
    }

    public static void removeAlbum(Album album) {
        Iterator<Album> iterator = favAlbum.iterator();
        while (iterator.hasNext()) {
            Album obj = iterator.next();
            if (obj.getId().equals(album.getId())) {
                iterator.remove();
                break;
            }
        }
        Log.d(TAG, "remove from favorite");
    }

    public static boolean isInFav(Album album) {
        for (Album album1 : favAlbum) {
            if (album1.getId().equals(album.getId())) {
                return true;
            }
        }
        return false;
    }

    public static int getSize() {
        if (favAlbum == null) favAlbum = new ArrayList<>();
        return favAlbum.size();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateUi();
    }

    private void updateUi() {
        if (!favAlbum.isEmpty()) {
            numberAlbumTv.setText("Tất cả " + favAlbum.size() + " playlist");
        }
        adapter.notifyDataSetChanged();
    }
}