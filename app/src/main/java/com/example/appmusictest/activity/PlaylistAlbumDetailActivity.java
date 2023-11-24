package com.example.appmusictest.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.appmusictest.FavoriteHelper;
import com.example.appmusictest.MyApplication;
import com.example.appmusictest.R;
import com.example.appmusictest.adapter.SongAdapter;
import com.example.appmusictest.dialog.MyProgress;
import com.example.appmusictest.model.Album;
import com.example.appmusictest.model.Playlist;
import com.example.appmusictest.model.Song;
import com.example.appmusictest.model.api.SongsResponse;
import com.example.appmusictest.service.ApiService;
import com.example.appmusictest.service.DataService;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.play.integrity.internal.m;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PlaylistAlbumDetailActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    public static ArrayList<Song> songArrayList;
    private Playlist playlist;
    private Album album;
    private ImageButton backIb, favoriteIb, menuIb;
    private TextView shuffleBtn, titlePlIv, numberSongTv;
    private ShapeableImageView imgPlIv;
    private int type;
    private MyProgress myProgress;
    private static final String TAG = "Pl_Al_Detail_Activity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playlist_album_detail);
        myProgress = new MyProgress(this);
        myProgress.show();
        initView();
        getDataIntent();
        setViewData();

    }

    private void setViewData() {
        if (type == MyApplication.TYPE_PLAYLIST) {
            setViewDataPlaylist();
        } else if (type == MyApplication.TYPE_ALBUM) {
            setViewDataAlbum();
        }
        shuffleBtn.setOnClickListener(v -> {
            Intent intent = new Intent(this, MusicPlayerActivity.class);
            intent.putExtra("class","PlaylistAlbumDetailActivity");
            intent.putExtra("index", 0);
            startActivity(intent);
        });
        backIb.setOnClickListener(v -> onBackPressed());
        menuIb.setOnClickListener(v -> showDialog());
    }

    private void setViewDataAlbum() {

        titlePlIv.setText(album.getTitle());
        Glide.with(this)
                .load(album.getArtUrl())
                .placeholder(R.mipmap.music_player_icon)
                .into(imgPlIv);
        favoriteIb.setOnClickListener(v -> {
            if (FavoriteAlbumActivity.isInFav(album)) {
                FavoriteHelper.actionWithFav(this, MainActivity.getIdUser(),album.getId(), FavoriteHelper.TYPE_DELETE, MyApplication.TYPE_ALBUM, album);
                favoriteIb.setImageResource(R.drawable.ic_favorite_gray);
            } else {
                favoriteIb.setImageResource(R.drawable.ic_favorite_purple);
                FavoriteHelper.actionWithFav(this, MainActivity.getIdUser(),album.getId(), FavoriteHelper.TYPE_ADD, MyApplication.TYPE_ALBUM, album);

            }
        });

        Log.d(TAG, "ALBUM SETVIEW");
    }

    private void setViewDataPlaylist() {
        titlePlIv.setText(playlist.getTitle());
        Glide.with(this)
                .load(playlist.getArtUrl())
                .placeholder(R.mipmap.music_player_icon)
                .into(imgPlIv);
        favoriteIb.setOnClickListener(v -> {
            if (FavoritePlaylistActivity.isInFav(playlist)) {
                if (playlist.getIdUser().equals(MainActivity.getIdUser())) {
                    showDialogDelete(playlist);
                } else {
                    FavoriteHelper.actionWithFav(this,MainActivity.getIdUser(),playlist.getId(), FavoriteHelper.TYPE_DELETE, MyApplication.TYPE_PLAYLIST, playlist);
                    favoriteIb.setImageResource(R.drawable.ic_favorite_gray);
                }
            } else {
                FavoriteHelper.actionWithFav(this,MainActivity.getIdUser(),playlist.getId(), FavoriteHelper.TYPE_ADD, MyApplication.TYPE_PLAYLIST, playlist);
                favoriteIb.setImageResource(R.drawable.ic_favorite_purple);
            }
        });

        Log.d(TAG, "playlist SETVIEW");
    }
    private void showDialogDelete(Playlist playlist) {
        AlertDialog dialog;
        TextView titleDialogDeleteTv,contentDialogTv,submitBtn,cancelBtn;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.custom_dialog_delete,null);

        titleDialogDeleteTv = view.findViewById(R.id.titleDialogDeleteTv);
        contentDialogTv = view.findViewById(R.id.contentDialogTv);
        submitBtn = view.findViewById(R.id.submitBtn);
        cancelBtn = view.findViewById(R.id.cancelBtn);


        titleDialogDeleteTv.setText(playlist.getTitle());
        contentDialogTv.setText(R.string.delete_playlist_title);

        builder.setView(view);
        builder.setCancelable(true);
        dialog = builder.create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();


        submitBtn.setOnClickListener(v -> {

            FavoriteHelper.actionWithFav(this, MainActivity.getIdUser(),playlist.getId(), FavoriteHelper.TYPE_DELETE, MyApplication.TYPE_PLAYLIST, playlist);
            FavoritePlaylistActivity.removePlaylist(playlist);
            dialog.dismiss();
            onBackPressed();
        });
        cancelBtn.setOnClickListener(v -> dialog.dismiss());
    }
    private void showDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.bottom_layout_detail_playlist);

        LinearLayout searchLn = dialog.findViewById(R.id.searchLn);
        LinearLayout addPlaySongsLn = dialog.findViewById(R.id.addPlaySongsLn);
        LinearLayout addFavLn = dialog.findViewById(R.id.addFavLn);
        TextView namePlaylist = dialog.findViewById(R.id.namePlTv);
        ImageButton addFavIb = dialog.findViewById(R.id.addFavIb);
        TextView addFavTv = dialog.findViewById(R.id.addFavTv);
        ShapeableImageView imgIv = dialog.findViewById(R.id.imgPlIv);

        if (type == MyApplication.TYPE_PLAYLIST) {
            namePlaylist.setText(playlist.getTitle());
            Glide.with(this)
                    .load(playlist.getArtUrl())
                    .placeholder(R.mipmap.music_player_icon)
                    .into(imgIv);
            if (FavoritePlaylistActivity.isInFav(playlist)) {
                addFavTv.setText(R.string.delete_fav_title);
                addFavIb.setImageResource(R.drawable.ic_in_library);
                favoriteIb.setImageResource(R.drawable.ic_favorite_purple);
                Log.d(TAG , "favorite contain");
            }
            addFavLn.setOnClickListener(v -> {
                if (!FavoritePlaylistActivity.isInFav(playlist)) {
                    FavoriteHelper.actionWithFav(this,MainActivity.getIdUser(),playlist.getId(), FavoriteHelper.TYPE_ADD, MyApplication.TYPE_PLAYLIST, playlist);
                    favoriteIb.setImageResource(R.drawable.ic_favorite_purple);
                } else {
                    if (playlist.getIdUser().equals(MainActivity.getIdUser())) {
                        showDialogDelete(playlist);
                    } else {
                        FavoriteHelper.actionWithFav(this,MainActivity.getIdUser(),playlist.getId(), FavoriteHelper.TYPE_DELETE, MyApplication.TYPE_PLAYLIST, playlist);
                        favoriteIb.setImageResource(R.drawable.ic_favorite_gray);
                    }
                }
                dialog.dismiss();
            });
        } else if (type == MyApplication.TYPE_ALBUM) {
            namePlaylist.setText(album.getTitle());
            Glide.with(this)
                    .load(album.getArtUrl())
                    .placeholder(R.mipmap.music_player_icon_round)
                    .into(imgIv);
            if (FavoriteAlbumActivity.isInFav(album)) {
                addFavTv.setText(R.string.delete_fav_title);
                addFavIb.setImageResource(R.drawable.ic_in_library);
                favoriteIb.setImageResource(R.drawable.ic_favorite_purple);
                Log.d(TAG , "favorite contain");
            }
            addFavLn.setOnClickListener(v -> {
                if (!FavoriteAlbumActivity.isInFav(album)) {
                    FavoriteHelper.actionWithFav(this, MainActivity.getIdUser(),album.getId(), FavoriteHelper.TYPE_ADD, MyApplication.TYPE_ALBUM, album);
                    favoriteIb.setImageResource(R.drawable.ic_favorite_purple);
                } else {
                    FavoriteHelper.actionWithFav(this, MainActivity.getIdUser(),album.getId(), FavoriteHelper.TYPE_DELETE, MyApplication.TYPE_ALBUM, album);

                    favoriteIb.setImageResource(R.drawable.ic_favorite_gray);
                }
                dialog.dismiss();
            });
        }

        searchLn.setOnClickListener(v -> {
            Toast.makeText(this, "Tính năng đang trong quá trình phát triển", Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        });
        addPlaySongsLn.setOnClickListener(v -> {
            Toast.makeText(this, R.string.add_list_play_notification, Toast.LENGTH_SHORT).show();
            MusicPlayerActivity.currentSongs.addAll(songArrayList);
            dialog.dismiss();
        });


        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setGravity(Gravity.BOTTOM);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private void initView() {
        songArrayList = new ArrayList<>();
        recyclerView = findViewById(R.id.listsongRv);
        backIb = findViewById(R.id.backIb);
        favoriteIb = findViewById(R.id.favoriteIb);
        menuIb = findViewById(R.id.moreIv);
        shuffleBtn = findViewById(R.id.buttonShuffleTv);
        numberSongTv = findViewById(R.id.numberSongTv);
        imgPlIv = findViewById(R.id.imgPlIv);
        titlePlIv = findViewById(R.id.titlePlTv);
    }

    private void getDataIntent() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            playlist = bundle.getParcelable("playlist");
            album = bundle.getParcelable("album");
        }
        getDataFromServer();
    }

    private void getDataFromServer() {
        if (playlist != null) {
            type = MyApplication.TYPE_PLAYLIST;
            getData(playlist.getId(), type);
        } else if (album != null) {
            type = MyApplication.TYPE_ALBUM;
            getData(album.getId(), type);

        }
    }

    private void getData(String id, int type) {
        DataService dataService = ApiService.getService();
        Call<SongsResponse> callback;
        if (type == MyApplication.TYPE_ALBUM) {
             callback = dataService.getAlbumSongs(id);
        } else {
             callback = dataService.getPlaylistSongs(id);
        }
        callback.enqueue(new Callback<SongsResponse>() {
            @Override
            public void onResponse(@NonNull Call<SongsResponse> call, @NonNull Response<SongsResponse> response) {
                if (response.code() == 200) {
                    if (response.body().getErrCode().equals("0")) {
                        songArrayList = response.body().getSongs();
                        recyclerView.setAdapter(new SongAdapter(songArrayList, PlaylistAlbumDetailActivity.this));
                        numberSongTv.setText(songArrayList.size() + " " + getString(R.string.playlist_title));
                        if (songArrayList.isEmpty()) {
                            shuffleBtn.setVisibility(View.GONE);
                        } else {
                            shuffleBtn.setVisibility(View.VISIBLE);
                        }
                        myProgress.dismiss();
                    }
                }

            }

            @Override
            public void onFailure(@NonNull Call<SongsResponse> call, @NonNull Throwable t) {
                Log.d(TAG, "Fail to get data from server due to:" + t.getMessage() );
                myProgress.dismiss();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateUi();
    }

    private void updateUi() {
        if (type == MyApplication.TYPE_PLAYLIST) {
            if (FavoritePlaylistActivity.isInFav(playlist)) {
                favoriteIb.setImageResource(R.drawable.ic_favorite_purple);
            } else {
                favoriteIb.setImageResource(R.drawable.ic_favorite_gray);
            }
        } else if (type == MyApplication.TYPE_ALBUM) {
            if (FavoriteAlbumActivity.isInFav(album)) {
                favoriteIb.setImageResource(R.drawable.ic_favorite_purple);
            } else {
                favoriteIb.setImageResource(R.drawable.ic_favorite_gray);
            }
        }

        if (songArrayList.isEmpty()) {
            shuffleBtn.setVisibility(View.GONE);
        } else {
            shuffleBtn.setVisibility(View.VISIBLE);
        }
    }
}