package com.example.appmusictest.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.appmusictest.R;
import com.example.appmusictest.adapter.PlaylistSuggestAdapter;
import com.example.appmusictest.model.Playlist;

import java.util.ArrayList;

public class ShowMorePlaylistActivity extends AppCompatActivity {

    private TextView titleMoreTv;
    private ImageButton backIb;
    private RecyclerView showMoreRv;
    private PlaylistSuggestAdapter playlistSuggestAdapter;
    private ArrayList<? extends Playlist> playlistArrayList;
    private static final String TAG = "Show More Playlist";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_more_playlist);

        initView();
        getDataIntent();
        setViewData();

        backIb.setOnClickListener(v -> onBackPressed());
    }

    private void getDataIntent() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            Log.d(TAG, "bundle != null" );
            playlistArrayList = bundle.getParcelableArrayList("playlists");
            if (playlistArrayList != null) {
                playlistSuggestAdapter = new PlaylistSuggestAdapter(playlistArrayList, this, true);
                showMoreRv.setLayoutManager(new GridLayoutManager(this, 2));
                showMoreRv.setAdapter(playlistSuggestAdapter);
                Log.d(TAG, "SIZE :" + playlistArrayList.size());
            }
        }


    }

    private void setViewData() {

    }

    private void initView() {
        titleMoreTv = findViewById(R.id.titleTv);
        backIb = findViewById(R.id.backIb);
        showMoreRv = findViewById(R.id.showMorePlaylistRv);
    }

    @Override
    protected void onResume() {
        super.onResume();
        titleMoreTv.setText(R.string.hot_playlist_title);
    }
}