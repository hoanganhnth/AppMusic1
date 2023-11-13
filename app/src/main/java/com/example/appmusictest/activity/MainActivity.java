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
import android.widget.ScrollView;
import android.widget.TextView;

import com.example.appmusictest.R;
import com.example.appmusictest.SessionManager;
import com.example.appmusictest.adapter.AuthorSuggestAdapter;
import com.example.appmusictest.adapter.PlaylistAlbumSuggestAdapter;
import com.example.appmusictest.model.Album;
import com.example.appmusictest.model.Author;
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
    private RelativeLayout showMorePlaylistRl,showMoreAlbumRl;
    private TextView numberSongTv,numberPlaylistTv,numberAlbumTv;
    private ImageButton logOutIb,showMoreIb,showMoreAlbumIb;
    private ScrollView viewSv;
    private ArrayList<Playlist> playlists;
    private ArrayList<Album> albums;
    private ArrayList<Author> authors;
    private RecyclerView playlistSgRv,albumSgRv, authorSgRv;
    private static final String TAG = "Main_Activity";
    private PlaylistAlbumSuggestAdapter<Album> albumSuggestAdapter;
    private PlaylistAlbumSuggestAdapter<Playlist> playlistSuggestAdapter;
    private AuthorSuggestAdapter authorSuggestAdapter;
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

        showMoreAlbumIb.setOnClickListener( v-> {
            Intent intent = new Intent(this, ShowMorePlaylistActivity.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable("albums", albums);
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
        showMorePlaylistRl = findViewById(R.id.showMorePlaylistRl);
        numberPlaylistTv = findViewById(R.id.numberPlaylistTv);
        albumSgRv = findViewById(R.id.albumSuggestRv);
        showMoreAlbumRl = findViewById(R.id.showMoreAlbumRl);
        viewSv = findViewById(R.id.viewSv);
        showMoreAlbumIb = findViewById(R.id.showMoreAlbumIb);
        numberAlbumTv = findViewById(R.id.numberAlbumTv);
        authorSgRv = findViewById(R.id.authorSuggestRv);
    }

    private void getData() {
        getDataPlaylist();
        getDataAlbum();
        getDataAuthor();

    }

    private void getDataAuthor() {
        authors = new ArrayList<>();
        authors.add(new Author("1", "author1", "https://upload.wikimedia.org/wikipedia/vi/3/32/S%C6%A1n_T%C3%B9ng_M-TP_-_C%C3%B3_ch%E1%BA%AFc_y%C3%AAu_l%C3%A0_%C4%91%C3%A2y.jpg"));
        authors.add(new Author("2", "author2", "https://cdnphoto.dantri.com.vn/ecdPkKw4WCg-NR0Zi2shwRYyUlo=/thumb_w/1020/2022/11/10/micheal-jackson-1668044313441.jpg"));
        authors.add(new Author("3", "author3", "https://cdn.tuoitre.vn/thumb_w/1100/471584752817336320/2023/9/5/jack-messi-1-16938973854241419756685.jpg"));
        authors.add(new Author("4", "author4", "https://images2.thanhnien.vn/528068263637045248/2023/8/30/1-1693385246169701996465.jpg"));
        authors.add(new Author("5", "author5", "https://suckhoedoisong.qltns.mediacdn.vn/thumb_w/640/324455921873985536/2023/9/6/dam-vinh-hung-16939936357631285203697.png"));
        authorSuggestAdapter = new AuthorSuggestAdapter(authors, this);
        authorSgRv.setAdapter(authorSuggestAdapter);

    }

    private void getDataAlbum() {
        albums = new ArrayList<>();
        albums.add(new Album("1","test1", "https://imgmusic.com/uploads/album/cover/2/ZPP_Pop_Rock_Inspirational_vol1_highrez.jpg"));
        albums.add(new Album("2","test2", "https://imgmusic.com/uploads/album/cover/197/poprockinsp_vol2_ZPP067.jpg"));
        albums.add(new Album("3","test3", "https://imgmusic.com/uploads/album/cover/78/IndieInspirational_Vol2.jpg"));
        albums.add(new Album("4","test4", "https://imgmusic.com/uploads/album/cover/66/EmotionalBuilds_Vol1-update.jpg"));
        albums.add(new Album("5","test5", "https://imgmusic.com/uploads/album/cover/11/IndieReflections_Vol1_highrez.jpg"));
        albumSuggestAdapter = new PlaylistAlbumSuggestAdapter<>(albums, this, false);
        albumSgRv.setAdapter(albumSuggestAdapter);

        if (albums.size() >= 5) {
            showMoreAlbumRl.setVisibility(View.VISIBLE);
        }
        Log.d(TAG, "album size "  + albums.size());
    }

    private void getDataPlaylist() {
        DataService dataService = ApiService.getService();
        Call<List<Playlist>> callback = dataService.getPlaylistCurrentDay();
        callback.enqueue(new Callback<List<Playlist>>() {
            @Override
            public void onResponse(@NonNull Call<List<Playlist>> call, @NonNull Response<List<Playlist>> response) {

                playlists = (ArrayList<Playlist>) response.body();
                assert playlists != null;
                if (playlists.size() >= 3) {
                    showMorePlaylistRl.setVisibility(View.VISIBLE);
                }
                playlistSuggestAdapter = new PlaylistAlbumSuggestAdapter<>(playlists, MainActivity.this, false);
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

        if (FavoriteAlbumActivity.getSize() == 0) {
            numberAlbumTv.setText("");
        } else {
            numberAlbumTv.setText(String.valueOf(FavoriteAlbumActivity.getSize()));
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