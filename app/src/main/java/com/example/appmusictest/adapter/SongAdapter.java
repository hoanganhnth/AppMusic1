package com.example.appmusictest.adapter;

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
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.appmusictest.R;
import com.example.appmusictest.activity.MusicPlayerActivity;
import com.example.appmusictest.model.Song;

import java.io.Serializable;
import java.util.ArrayList;

public class SongAdapter extends RecyclerView.Adapter<SongAdapter.ViewHolder> {

    private ArrayList<Song> songArrayList;
    private Context context;
    private static final String TAG = "SongAdapter";

    public SongAdapter(ArrayList<Song> songArrayList) {
        this.songArrayList = songArrayList;
    }

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
                .placeholder(R.mipmap.ic_launcher_round)
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
        holder.moreIv.setOnClickListener(v -> showBottomDialog(v, position));
    }

    private void showBottomDialog(View view, int pos) {
        final Dialog dialog = new Dialog(view.getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.song_bottom_layout);

        LinearLayout addPlaySongsLn = dialog.findViewById(R.id.addPlaySongsLn);
        LinearLayout addPlayNextLn = dialog.findViewById(R.id.addPlayNextLn);
        LinearLayout addFavLn = dialog.findViewById(R.id.addFavLn);

        TextView nameSong = dialog.findViewById(R.id.nameSongTv);
        TextView authorSong = dialog.findViewById(R.id.authorSongTv);
        nameSong.setText(songArrayList.get(pos).getTitle());
        authorSong.setText(songArrayList.get(pos).getNameAuthor());
        ImageView imgIv = dialog.findViewById(R.id.imgDlIv);
        Glide.with(view.getContext())
                        .load(songArrayList.get(pos).getArtUrl())
                                .into(imgIv);
        addPlaySongsLn.setOnClickListener( v -> {
            Toast.makeText(view.getContext(), R.string.add_list_play_notification, Toast.LENGTH_SHORT).show();
            addSongToCurrentSongs(pos);
            dialog.dismiss();
        });

        addPlayNextLn.setOnClickListener( v -> {
            Toast.makeText(view.getContext(), R.string.add_play_next_notification, Toast.LENGTH_SHORT).show();
            addSongToNextSong(pos);
            dialog.dismiss();
        });

        addFavLn.setOnClickListener( v -> {
            Toast.makeText(view.getContext(), R.string.add_favorite_notification, Toast.LENGTH_SHORT).show();
            dialog.dismiss();

        });

        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setGravity(Gravity.BOTTOM);
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

    public class ViewHolder extends RecyclerView.ViewHolder {
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
