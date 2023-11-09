package com.example.appmusictest.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.appmusictest.R;
import com.example.appmusictest.adapter.PlaylistAdapter;
import com.example.appmusictest.model.Playlist;

import java.util.ArrayList;
import java.util.Iterator;

public class FavoritePlaylistActivity extends AppCompatActivity {

    private static ArrayList<Playlist> favPlaylists = new ArrayList<>();
    private PlaylistAdapter playlistAdapter;
    private RelativeLayout addPlaylistRl;
    private RecyclerView playlistFavRv;
    private TextView numberPlaylistTv;
    private ImageButton backIb;
    private final static String TAG = "Favorite_Playlist_Ac";

    public static ArrayList<Playlist> getFavPlaylists() {
        return favPlaylists;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite_playlist);
        initView();
        setViewData();
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
    }

    private void showDialogAddPlaylist() {
        AlertDialog dialog;
        EditText playlistEt;
        TextView submitBtn,cancelBtn;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.custom_add_dialog,null);
        playlistEt = view.findViewById(R.id.playlistEt);
        submitBtn = view.findViewById(R.id.submitBtn);
        cancelBtn = view.findViewById(R.id.cancelBtn);
        builder.setView(view);
        builder.setCancelable(true);
        dialog = builder.create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
        submitBtn.setBackground(ContextCompat.getDrawable(FavoritePlaylistActivity.this, R.drawable.shape_button4));
        playlistEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().isEmpty()) {
                    submitBtn.setBackground(ContextCompat.getDrawable(FavoritePlaylistActivity.this, R.drawable.shape_button4));
                } else {
                    submitBtn.setBackground(ContextCompat.getDrawable(FavoritePlaylistActivity.this, R.drawable.shape_button3));
                }
            }
        });

        submitBtn.setOnClickListener(v -> {
            if (!playlistEt.getText().toString().isEmpty()) {
                FavoritePlaylistActivity.addPlaylist(new Playlist("4", playlistEt.getText().toString(),""));
                dialog.dismiss();
                Toast.makeText(this, "Đã tạo playlist", Toast.LENGTH_SHORT).show();
            }
        });
        cancelBtn.setOnClickListener(v -> dialog.dismiss());
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
            }
        }
        Log.d(TAG, "remove from favorite");
    }

    public static boolean isInFav(Playlist playlist) {
        for (Playlist playlist1 : favPlaylists) {
            if (playlist1.getId().equals(playlist.getId())) {
                return true;
            }
        }
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