package com.example.appmusictest.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

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
