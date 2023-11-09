package com.example.appmusictest.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;

import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.appmusictest.R;
import com.example.appmusictest.SessionManager;
import com.example.appmusictest.adapter.PlaylistSuggestAdapter;
import com.example.appmusictest.model.Playlist;
import com.example.appmusictest.service.ApiService;
import com.example.appmusictest.service.DataService;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private EditText searchEt;
    private RelativeLayout songFavRl, playlistFavRl, albumFavRl, authorFavRl;
    private RelativeLayout showMoreRl;
    private TextView numberSongTv,numberPlaylistTv;
    private ImageButton logOutIb,showMoreIb;
    private ArrayList<Playlist> playlists;
    private RecyclerView playlistSgRv;
    private static final String TAG = "Main_Activity";
    private PlaylistSuggestAdapter playlistSuggestAdapter;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sessionManager = new SessionManager(getApplicationContext());
        initView();
        getData();
        setViewData();
    }

    private void setViewData() {


        searchEt.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH || event.getAction() == KeyEvent.KEYCODE_ENTER) {
                String query = searchEt.getText().toString().trim();
                if (!query.equals("")) {
                    Intent intent = new Intent(MainActivity.this, SearchActivity.class);
                    intent.putExtra("query", query);
                    startActivity(intent);
                    searchEt.clearFocus();
                }
                return true;
            }
            Log.d(TAG, "NOT enter");
            return false;
        });
        songFavRl.setOnClickListener(v -> startActivity(new Intent(this, FavoriteSongActivity.class)));
        playlistFavRl.setOnClickListener(v -> startActivity(new Intent(this, FavoritePlaylistActivity.class)));
        albumFavRl.setOnClickListener(v -> startActivity(new Intent(this, FavoriteAlbumActivity.class)));
        authorFavRl.setOnClickListener(v -> startActivity(new Intent(this, FavoriteAuthorActivity.class)));

        logOutIb.setOnClickListener(v -> sessionManager.logOutSession());

        showMoreIb.setOnClickListener(v -> {
            Intent intent = new Intent(this, ShowMorePlaylistActivity.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable("playlists", playlists);
            intent.putExtras(bundle);
            startActivity(intent);
        });


    }

    private void initView() {
        searchEt = findViewById(R.id.searchEt);
        playlistSgRv = findViewById(R.id.playlistSuggestRv);
        songFavRl = findViewById(R.id.songFavRl);
        playlistFavRl = findViewById(R.id.playlistFavRl);
        albumFavRl = findViewById(R.id.albumFavRl);
        authorFavRl = findViewById(R.id.authorFavRl);
        numberSongTv = findViewById(R.id.numberSongTv);
        logOutIb = findViewById(R.id.logOutIb);
        showMoreIb = findViewById(R.id.showMoreIb);
        showMoreRl = findViewById(R.id.showMoreRl);
        numberPlaylistTv = findViewById(R.id.numberPlaylistTv);
    }

    private void getData() {
        DataService dataService = ApiService.getService();
        Call<List<Playlist>> callback = dataService.getPlaylistCurrentDay();
        callback.enqueue(new Callback<List<Playlist>>() {
            @Override
            public void onResponse(@NonNull Call<List<Playlist>> call, @NonNull Response<List<Playlist>> response) {

                playlists = (ArrayList<Playlist>) response.body();
                assert playlists != null;
                if (playlists.size() >= 3) {
                    showMoreRl.setVisibility(View.VISIBLE);
                }
                playlistSuggestAdapter = new PlaylistSuggestAdapter(playlists, MainActivity.this, false);
                playlistSgRv.setAdapter(playlistSuggestAdapter);
            }

            @Override
            public void onFailure(@NonNull Call<List<Playlist>> call, @NonNull Throwable t) {
                Log.d(TAG, "Fail get data due to: " + t.getMessage() );

            }
        });

    }


    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (v instanceof EditText) {
                Rect outRect = new Rect();
                v.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int) event.getRawX(), (int) event.getRawY())) {
                    v.clearFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        }
        return super.dispatchTouchEvent(event);
    }
    @Override
    protected void onResume() {
        super.onResume();

        updateUi();
        if (MusicPlayerActivity.musicPlayerService != null) {
            MusicPlayerActivity.musicPlayerService.destroyMain = false;
            Log.d(TAG, "main resume");
            if (MusicPlayerActivity.musicPlayerService.isPlaying()) {
                MusicPlayerActivity.musicPlayerService.showNotification(R.drawable.ic_pause_gray);
            } else {
                MusicPlayerActivity.musicPlayerService.showNotification(R.drawable.ic_play);
            }
        }
    }

    private void updateUi() {
        searchEt.setText("");
        if (FavoriteSongActivity.getSize() == 0) {
            numberSongTv.setText("");
        } else {
            numberSongTv.setText(String.valueOf(FavoriteSongActivity.getSize()));
        }

        if (FavoritePlaylistActivity.getSize() == 0) {
            numberPlaylistTv.setText("");
        } else {
            numberPlaylistTv.setText(String.valueOf(FavoritePlaylistActivity.getSize()));
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "main destroy");
        Intent intent = new Intent("your.package.name.MainActivityDestroyed");
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }
}