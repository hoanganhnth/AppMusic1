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
import android.widget.Toast;

import com.example.appmusictest.R;
import com.example.appmusictest.SessionManager;
import com.example.appmusictest.adapter.AlbumSuggestAdapter;
import com.example.appmusictest.adapter.AuthorSuggestAdapter;
import com.example.appmusictest.adapter.PlaylistSuggestAdapter;
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
    private TextView numberSongTv,numberPlaylistTv,numberAlbumTv,numberAuthorTv;
    private ImageButton logOutIb;
    private ScrollView viewSv;
    private ArrayList<Playlist> playlists, favPlaylist,showPlaylist;
    private ArrayList<Album> albums, favAlbums, showAlbum;
    private ArrayList<Author> authors, favAuthors, showAuthor;
    private ArrayList<Song> favSongs;
    private RecyclerView playlistSgRv,albumSgRv, authorSgRv;
    private static final String TAG = "Main_Activity";
    private AlbumSuggestAdapter albumSuggestAdapter;
    private PlaylistSuggestAdapter playlistSuggestAdapter;
    private AuthorSuggestAdapter authorSuggestAdapter;
    private SessionManager sessionManager;
    private MyProgress myProgress;
    private boolean getDataSuccess = false;
    private static String idUser = "";
    private int numberSuggest = 4;

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
        numberPlaylistTv = findViewById(R.id.numberPlaylistTv);
        albumSgRv = findViewById(R.id.albumSuggestRv);
        viewSv = findViewById(R.id.viewSv);
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
        getDataFavAuthor();
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
                            numberAuthorTv.setText("");
                        } else {
                            numberAuthorTv.setText(String.valueOf(favAuthors.size()));
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
        DataService dataService = ApiService.getService();
        Call<AuthorsResponse> callback = dataService.getFavAuthor(idUser);
        callback.enqueue(new Callback<AuthorsResponse>() {
            @Override
            public void onResponse(@NonNull Call<AuthorsResponse> call, @NonNull Response<AuthorsResponse> response) {
                if (response.code() == 200) {
                    if (response.body().getErrCode().equals("0")) {
                        authors = response.body().getAuthors();
                        showAuthor = new ArrayList<>(authors.subList(0,  Math.min(authors.size(), numberSuggest)));
                        authorSuggestAdapter = new AuthorSuggestAdapter(authors, MainActivity.this);
                        authorSgRv.setAdapter(authorSuggestAdapter);
                        myProgress.dismiss();
                    }
                }

            }

            @Override
            public void onFailure(@NonNull Call<AuthorsResponse> call, @NonNull Throwable t) {
                Log.d(TAG, "Fail get data due to: " + t.getMessage());
                myProgress.dismiss();

            }
        });


    }

    private void getDataAlbum() {
        albums = new ArrayList<>();
        DataService dataService = ApiService.getService();
        Call<AlbumsResponse> callback = dataService.getAllAlbums();
        callback.enqueue(new Callback<AlbumsResponse>() {
            @Override
            public void onResponse(@NonNull Call<AlbumsResponse> call, @NonNull Response<AlbumsResponse> response) {
                if (response.code() == 200) {
                    if (response.body().getErrCode().equals("0")) {
                        albums = response.body().getAlbums();
                        showAlbum = new ArrayList<>(albums.subList(0,  Math.min(albums.size(), numberSuggest)));
                        albumSuggestAdapter = new AlbumSuggestAdapter(showAlbum, albums, MainActivity.this, false);
                        albumSgRv.setAdapter(albumSuggestAdapter);
                        myProgress.dismiss();
                    }
                }

            }

            @Override
            public void onFailure(@NonNull Call<AlbumsResponse> call, @NonNull Throwable t) {
                Log.d(TAG, "Fail get data due to: " + t.getMessage() );
                myProgress.dismiss();

            }
        });


    }

    private void getDataPlaylist() {
        DataService dataService = ApiService.getService();
        Call<List<Playlist>> callback = dataService.getPlaylistCurrentDay();
        callback.enqueue(new Callback<List<Playlist>>() {
            @Override
            public void onResponse(@NonNull Call<List<Playlist>> call, @NonNull Response<List<Playlist>> response) {

                playlists = (ArrayList<Playlist>) response.body();
                assert playlists != null;
                showPlaylist = new ArrayList<>(playlists.subList(0,  Math.min(playlists.size(), numberSuggest)));
                playlistSuggestAdapter = new PlaylistSuggestAdapter(showPlaylist,playlists, MainActivity.this, false);
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