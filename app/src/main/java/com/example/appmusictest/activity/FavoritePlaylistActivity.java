package com.example.appmusictest.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.appmusictest.R;
import com.example.appmusictest.adapter.PlaylistAdapter;
import com.example.appmusictest.dialog.MyCreatePlaylistDialog;
import com.example.appmusictest.dialog.MyProgress;
import com.example.appmusictest.model.Playlist;

import java.util.ArrayList;
import java.util.Iterator;

public class FavoritePlaylistActivity extends AppCompatActivity {

    private static ArrayList<Playlist> favPlaylists;
    private PlaylistAdapter playlistAdapter;
    private RelativeLayout addPlaylistRl;
    private RecyclerView playlistFavRv;
    private TextView numberPlaylistTv;
    private ImageButton backIb;
    private MyProgress myProgress;
    private final static String TAG = "Favorite_Playlist_Ac";

    public static ArrayList<Playlist> getFavPlaylists() {
        return favPlaylists;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite_playlist);
        myProgress = new MyProgress(this);
        myProgress.show();
        initView();
        getData();
        setViewData();
    }

    private void getData() {
        if (getIntent() != null) {
            favPlaylists = getIntent().getParcelableArrayListExtra("favPlaylists");
            if (favPlaylists == null) favPlaylists = new ArrayList<>();
        }
    }

    private void initView() {
        numberPlaylistTv = findViewById(R.id.numberPlaylistTv);
        backIb = findViewById(R.id.backIb);
        playlistFavRv = findViewById(R.id.playlistFavRv);
        addPlaylistRl = findViewById(R.id.addPlaylistRl);
    }

    private void setViewData() {
        playlistAdapter = new PlaylistAdapter(favPlaylists, this);
        playlistFavRv.setAdapter(playlistAdapter);

        if (!favPlaylists.isEmpty()) {
            numberPlaylistTv.setVisibility(View.VISIBLE);
            numberPlaylistTv.setText("Tất cả " + favPlaylists.size() + " playlist");
        }

        backIb.setOnClickListener(v -> {
            onBackPressed();
        });

        addPlaylistRl.setOnClickListener(v -> {
            showDialogAddPlaylist();
        });
        myProgress.dismiss();
    }

    private void showDialogAddPlaylist() {
        MyCreatePlaylistDialog myCreatePlaylistDialog = new MyCreatePlaylistDialog(this, playlistAdapter);
        myCreatePlaylistDialog.show();
    }

    public static void addPlaylist(Playlist playlist) {
        favPlaylists.add(playlist);

        Log.d(TAG, "added to favorite");
    }

    public static void removePlaylist(Playlist playlist) {
        Iterator<Playlist> iterator = favPlaylists.iterator();
        while (iterator.hasNext()) {
            Playlist obj = iterator.next();
            if (obj.getId().equals(playlist.getId())) {
                iterator.remove();
                break;
            }
        }
        Log.d(TAG, "remove from favorite");
    }

    public static boolean isInFav(Playlist playlist) {
//        for (Playlist playlist1 : favPlaylists) {
//            if (playlist1.getId().equals(playlist.getId())) {
//                return true;
//            }
//        }
        return false;
    }

    public void updateUi() {
        if (!favPlaylists.isEmpty()) {
            numberPlaylistTv.setText("Tất cả " + favPlaylists.size() + " playlist");
        }
    }
    public static int getSize() {
        return favPlaylists.size();
    }

}