package com.example.appmusictest.adapter;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.appmusictest.R;
import com.example.appmusictest.activity.FavoritePlaylistActivity;
import com.example.appmusictest.activity.FavoriteSongActivity;
import com.example.appmusictest.activity.MusicPlayerActivity;
import com.example.appmusictest.dialog.MyCreatePlaylistDialog;
import com.example.appmusictest.model.Song;

import java.util.ArrayList;

public class SongAdapter extends RecyclerView.Adapter<SongAdapter.ViewHolder> {

    private ArrayList<Song> songArrayList;
    private Context context;
    private static final String TAG = "SongAdapter";

    public SongAdapter(ArrayList<Song> songArrayList, Context context) {
        this.songArrayList = songArrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public SongAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_songs,parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SongAdapter.ViewHolder holder, int position) {

        holder.nameSong.setText(songArrayList.get(position).getTitle());
        holder.authorSong.setText(songArrayList.get(position).getNameAuthor());

        Glide.with(holder.itemView.getContext())
                .load(songArrayList.get(position).getArtUrl())
                .placeholder(R.mipmap.music_player_icon)
                .into(holder.imgIv);

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(holder.itemView.getContext(), MusicPlayerActivity.class);
            if (songArrayList.get(position).getId().equals(MusicPlayerActivity.nowPlayingId)) {
                intent.putExtra("index", MusicPlayerActivity.songPosition);
                intent.putExtra("class", "NowPlaying");
            } else {
                intent.putExtra("index", position);
                intent.putExtra("class", "SongAdapter");
            }

            holder.itemView.getContext().startActivity(intent);
        });
        holder.moreIv.setOnClickListener(v -> showBottomDialog(context, position));
    }

    private void showBottomDialog(Context context, int pos) {
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.bottom_layout_song);

        LinearLayout addPlaySongsLn = dialog.findViewById(R.id.addPlaySongsLn);
        LinearLayout addPlayNextLn = dialog.findViewById(R.id.addPlayNextLn);
        LinearLayout addFavLn = dialog.findViewById(R.id.addFavLn);
        LinearLayout addPlaylistLn = dialog.findViewById(R.id.addPlaylistLn);
        TextView addFavTv = dialog.findViewById(R.id.addFavTv);
        ImageButton addFavIb = dialog.findViewById(R.id.addFavIb);
        if (FavoriteSongActivity.isInFav(songArrayList.get(pos))) {
            addFavTv.setText(R.string.delete_fav_title);
            addFavIb.setImageResource(R.drawable.ic_in_library);
        }

        TextView nameSong = dialog.findViewById(R.id.nameSongTv);
        TextView authorSong = dialog.findViewById(R.id.authorSongTv);
        nameSong.setText(songArrayList.get(pos).getTitle());
        authorSong.setText(songArrayList.get(pos).getNameAuthor());
        ImageView imgIv = dialog.findViewById(R.id.imgDlIv);
        Glide.with(context)
                        .load(songArrayList.get(pos).getArtUrl())
                                .into(imgIv);
        addPlaySongsLn.setOnClickListener( v -> {
            Toast.makeText(context, R.string.add_list_play_notification, Toast.LENGTH_SHORT).show();
            addSongToCurrentSongs(pos);
            dialog.dismiss();
        });

        addPlayNextLn.setOnClickListener( v -> {
            Toast.makeText(context, R.string.add_play_next_notification, Toast.LENGTH_SHORT).show();
            addSongToNextSong(pos);
            dialog.dismiss();
        });

        addFavLn.setOnClickListener( v -> {
            if (!FavoriteSongActivity.isInFav(songArrayList.get(pos))) {
                Toast.makeText(context, R.string.add_favorite_notification, Toast.LENGTH_SHORT).show();
                FavoriteSongActivity.addSong(songArrayList.get(pos));
            } else {

                if (context.getClass().getSimpleName().equals(FavoriteSongActivity.class.getSimpleName())) {
                    showDeleteDialog(context, pos);
                } else {
                    Toast.makeText(context, R.string.remove_favorite_notification, Toast.LENGTH_SHORT).show();
                    FavoriteSongActivity.removeSong(songArrayList.get(pos));
                }
            }
            dialog.dismiss();

        });
        addPlaylistLn.setOnClickListener(v -> {
            dialog.dismiss();
            showAddPlaylistDialog(context, pos);
        });

        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setGravity(Gravity.BOTTOM);
    }

    private void showAddPlaylistDialog(Context context, int pos) {
        Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.bottom_layout_add_to_playlist);
        RelativeLayout addPlaylistRl = dialog.findViewById(R.id.addPlaylistRl);
        RecyclerView playlistFavRv = dialog.findViewById(R.id.playlistFavRv);
        PlaylistAddAdapter playlistAdapter = new PlaylistAddAdapter(FavoritePlaylistActivity.getFavPlaylists(), context);
        playlistFavRv.setAdapter(playlistAdapter);
        addPlaylistRl.setOnClickListener(v -> {
            dialog.dismiss();
            showCreatePlaylistDialog(context);
        });
        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setGravity(Gravity.BOTTOM);
    }

    private void showCreatePlaylistDialog(Context context) {
        MyCreatePlaylistDialog myCreatePlaylistDialog = new MyCreatePlaylistDialog(context);
        myCreatePlaylistDialog.show();
    }

    private void showDeleteDialog(Context context, int pos) {
        AlertDialog dialog;
        TextView titleDialogDeleteTv,contentDialogTv,submitBtn,cancelBtn;
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View viewDialog = LayoutInflater.from(context).inflate(R.layout.custom_dialog_delete,null);
        titleDialogDeleteTv = viewDialog.findViewById(R.id.titleDialogDeleteTv);
        contentDialogTv = viewDialog.findViewById(R.id.contentDialogTv);
        submitBtn = viewDialog.findViewById(R.id.submitBtn);
        cancelBtn = viewDialog.findViewById(R.id.cancelBtn);

        titleDialogDeleteTv.setText(songArrayList.get(pos).getTitle());

        contentDialogTv.setText(R.string.delete_song_title);

        builder.setView(viewDialog);
        builder.setCancelable(true);
        dialog = builder.create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();

        submitBtn.setOnClickListener(v -> {
            Toast.makeText(context, R.string.remove_favorite_notification, Toast.LENGTH_SHORT).show();
            FavoriteSongActivity.removeSong(songArrayList.get(pos));
            notifyItemRemoved(pos);
            notifyItemRangeChanged(pos, FavoriteSongActivity.getSize());
            dialog.dismiss();
        });
        cancelBtn.setOnClickListener(v -> dialog.dismiss());

    }

    private void addSongToNextSong(int pos) {
        if (MusicPlayerActivity.originalSongs != null) {
            MusicPlayerActivity.currentSongs.add(MusicPlayerActivity.songPosition + 1, songArrayList.get(pos));
            MusicPlayerActivity.originalSongs.add(MusicPlayerActivity.originalPos() + 1, songArrayList.get(pos));
            Log.d(TAG, "add Song To NextSong");
        }
    }

    private void addSongToCurrentSongs(int pos) {
        if (MusicPlayerActivity.originalSongs != null) {
            MusicPlayerActivity.currentSongs.add(songArrayList.get(pos));
            MusicPlayerActivity.originalSongs.add(songArrayList.get(pos));
            Log.d(TAG, "add Song To CurrentSongs");
        }
    }

    @Override
    public int getItemCount() {
        return songArrayList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView nameSong, authorSong;
        private ImageView moreIv;
        private ImageView imgIv;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nameSong = itemView.findViewById(R.id.nameSongTv);
            authorSong = itemView.findViewById(R.id.authorSongTv);
            moreIv = itemView.findViewById(R.id.moreIv);
            imgIv = itemView.findViewById(R.id.imgRowIv);
        }
    }
}
