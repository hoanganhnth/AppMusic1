package com.example.appmusictest.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;

import android.graphics.Rect;
import android.os.Bundle;
import android.os.Parcelable;
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
import com.example.appmusictest.dialog.MyProgress;
import com.example.appmusictest.model.Album;
import com.example.appmusictest.model.Author;
import com.example.appmusictest.model.Playlist;
import com.example.appmusictest.model.Song;
import com.example.appmusictest.model.api.AlbumsResponse;
import com.example.appmusictest.model.api.AuthorsResponse;
import com.example.appmusictest.model.api.PlaylistsResponse;
import com.example.appmusictest.model.api.SongsResponse;
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
    private TextView numberSongTv,numberPlaylistTv,numberAlbumTv,numberAuthorTv;
    private ImageButton logOutIb,showMoreIb,showMoreAlbumIb;
    private ScrollView viewSv;
    private ArrayList<Playlist> playlists, favPlaylist;
    private ArrayList<Album> albums, favAlbums;
    private ArrayList<Author> authors, favAuthors;
    private ArrayList<Song> favSongs;
    private RecyclerView playlistSgRv,albumSgRv, authorSgRv;
    private static final String TAG = "Main_Activity";
    private PlaylistAlbumSuggestAdapter<Album> albumSuggestAdapter;
    private PlaylistAlbumSuggestAdapter<Playlist> playlistSuggestAdapter;
    private AuthorSuggestAdapter authorSuggestAdapter;
    private SessionManager sessionManager;
    private MyProgress myProgress;
    private boolean getDataSuccess = false;
    private static String idUser = "";

    public static String getIdUser() {
        return idUser;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sessionManager = new SessionManager(getApplicationContext());
        idUser = sessionManager.getIdUser();
        myProgress = new MyProgress(this);
        myProgress.show();
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
        songFavRl.setOnClickListener(v -> {
            Intent intent = new Intent(this, FavoriteSongActivity.class);
            Bundle bundle = new Bundle();
            bundle.putParcelableArrayList("favSongs", favSongs);
            intent.putExtras(bundle);
            startActivity(intent);
        });
        playlistFavRl.setOnClickListener(v -> {
            Intent intent = new Intent(this, FavoritePlaylistActivity.class);
            Bundle bundle = new Bundle();
            bundle.putParcelableArrayList("favPlaylists", favPlaylist);
            intent.putExtras(bundle);
            startActivity(intent);
        });
        albumFavRl.setOnClickListener(v -> {
            Intent intent = new Intent(this, FavoriteAlbumActivity.class);
            Bundle bundle = new Bundle();
            bundle.putParcelableArrayList("favAlbums", favAlbums);
            intent.putExtras(bundle);
            startActivity(intent);
        });
        authorFavRl.setOnClickListener(v -> {
            Intent intent = new Intent(this, FavoriteAuthorActivity.class);
            Bundle bundle = new Bundle();
            bundle.putParcelableArrayList("favAuthors", favAuthors);
            intent.putExtras(bundle);
            startActivity(intent);
        });

        logOutIb.setOnClickListener(v -> sessionManager.logOutSession());
        showMoreIb.setOnClickListener(v -> {
            Intent intent = new Intent(this, ShowMorePlaylistActivity.class);
            Bundle bundle = new Bundle();
            bundle.putParcelableArrayList("playlists", playlists);
            intent.putExtras(bundle);
            startActivity(intent);
        });

        showMoreAlbumIb.setOnClickListener( v-> {
            Intent intent = new Intent(this, ShowMorePlaylistActivity.class);
            Bundle bundle = new Bundle();
            bundle.putParcelableArrayList("albums", albums);
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
        numberAuthorTv = findViewById(R.id.numberAuthorTv);
    }

    private void getData() {
        getDataPlaylist();
        getDataAlbum();
        getDataAuthor();

    }

    private void getDataFav() {
        getDataFavSong();
        getDataFavPlaylist();
        getDataFavAlbum();
//        getDataFavAuthor();
    }

    private void getDataFavAlbum() {
        DataService dataService = ApiService.getService();
        // name api
        Call<AlbumsResponse> callback = dataService.getFavAlbum(idUser);
        callback.enqueue(new Callback<AlbumsResponse>() {
            @Override
            public void onResponse(@NonNull Call<AlbumsResponse> call, @NonNull Response<AlbumsResponse> response) {
                if (response.code() == 200) {
                    assert response.body() != null;
                    if (response.body().getErrCode().equals("0")) {
                        favAlbums = response.body().getAlbums();
                        if (favAlbums.isEmpty()) {
                            numberAlbumTv.setText("");
                        } else {
                            numberAlbumTv.setText(String.valueOf(favAlbums.size()));
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

    private void getDataFavAuthor() {
        DataService dataService = ApiService.getService();
        Call<AuthorsResponse> callback = dataService.getFavAuthor(idUser);
        callback.enqueue(new Callback<AuthorsResponse>() {
            @Override
            public void onResponse(@NonNull Call<AuthorsResponse> call, @NonNull Response<AuthorsResponse> response) {
                if (response.code() == 200) {
                    assert response.body() != null;
                    if (response.body().getErrCode().equals("0")) {
                        favAuthors = response.body().getAuthors();
                        if (favAuthors.isEmpty()) {
                            numberPlaylistTv.setText("");
                        } else {
                            numberPlaylistTv.setText(String.valueOf(favAuthors.size()));
                        }
                    }
                }
                myProgress.dismiss();
            }

            @Override
            public void onFailure(@NonNull Call<AuthorsResponse> call, @NonNull Throwable t) {
                Log.d(TAG, "Fail to get data from server due to:" + t.getMessage() );
                myProgress.dismiss();
            }
        });
    }

    private void getDataFavPlaylist() {
        DataService dataService = ApiService.getService();
        Call<PlaylistsResponse> callback = dataService.getFavPlaylist(idUser);
        callback.enqueue(new Callback<PlaylistsResponse>() {
            @Override
            public void onResponse(@NonNull Call<PlaylistsResponse> call, @NonNull Response<PlaylistsResponse> response) {
                if (response.code() == 200) {
                    assert response.body() != null;
                    if (response.body().getErrCode().equals("0")) {
                        favPlaylist = response.body().getPlaylists();
                        if (favPlaylist.isEmpty()) {
                            numberPlaylistTv.setText("");
                        } else {
                            numberPlaylistTv.setText(String.valueOf(favPlaylist.size()));
                        }
                    }
                }
                myProgress.dismiss();
            }

            @Override
            public void onFailure(@NonNull Call<PlaylistsResponse> call, @NonNull Throwable t) {
                Log.d(TAG, "Fail to get data from server due to:" + t.getMessage() );
                myProgress.dismiss();
            }
        });
    }

    private void getDataFavSong() {
        DataService dataService = ApiService.getService();
        // name api
        Call<SongsResponse> callback = dataService.getFavSong(idUser);
        callback.enqueue(new Callback<SongsResponse>() {
            @Override
            public void onResponse(@NonNull Call<SongsResponse> call, @NonNull Response<SongsResponse> response) {
                if (response.code() == 200) {
                    assert response.body() != null;
                    if (response.body().getErrCode().equals("0")) {
                        favSongs = response.body().getSongs();
                        if (favSongs.isEmpty()) {
                            numberSongTv.setText("");
                        } else {
                            numberSongTv.setText(String.valueOf(favSongs.size()));
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
                getDataSuccess = true;
                if (getDataSuccess) myProgress.dismiss();
            }

            @Override
            public void onFailure(@NonNull Call<List<Playlist>> call, @NonNull Throwable t) {
                Log.d(TAG, "Fail get data due to: " + t.getMessage() );
                myProgress.dismiss();

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
        updateUiFav();
    }

    private void updateUiFav() {
        getDataFav();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "main destroy");
        Intent intent = new Intent("your.package.name.MainActivityDestroyed");
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }
}