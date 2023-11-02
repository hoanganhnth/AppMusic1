package com.example.appmusictest.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.appmusictest.R;
import com.example.appmusictest.activity.MusicPlayerActivity;
import com.example.appmusictest.model.Song;

import java.util.ArrayList;

public class ListPlayAdapter extends RecyclerView.Adapter<ListPlayAdapter.ViewHolder> {

    private ArrayList<Song> currentSongs;
    private int positionPlaying;
    private static final String TAG = "ListPlayAdapter";

    public ListPlayAdapter(ArrayList<Song> currentSongs) {
        this.currentSongs = currentSongs;
    }

    public void setCurrentlyPlayingPosition(int newPosition) {
        positionPlaying = newPosition;
        notifyDataSetChanged();
    }
    @NonNull
    @Override
    public ListPlayAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_songs, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ListPlayAdapter.ViewHolder holder, int position) {
        holder.nameSongTv.setText(currentSongs.get(position).getTitle());
        holder.authorSongTv.setText(currentSongs.get(position).getNameAuthor());
        Glide.with(holder.itemView.getContext())
                .load(currentSongs.get(position).getArtUrl())
                .into(holder.imgIV);
        if (position == positionPlaying) {
            holder.itemView.setBackground(ContextCompat.getDrawable(holder.itemView.getContext(),R.drawable.shape_button2));
            Log.d(TAG, "song number " + position + " ís playing");
        } else {
            holder.itemView.setBackground(null);
        }
        holder.itemView.setOnClickListener(v -> {
            if (MusicPlayerActivity.musicPlayerService != null)  {
                MusicPlayerActivity.musicPlayerService.playMusicFromUrl(currentSongs.get(position).getPathUrl());
                MusicPlayerActivity.songPosition = holder.getAdapterPosition();
                setCurrentlyPlayingPosition(holder.getAdapterPosition());
            }
        });
    }

    @Override
    public int getItemCount() {
        return currentSongs.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView nameSongTv, authorSongTv;
        private ImageView imgIV;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nameSongTv = itemView.findViewById(R.id.nameSongTv);
            authorSongTv = itemView.findViewById(R.id.authorSongTv);
            imgIV = itemView.findViewById(R.id.imgRowIv);
        }
    }
}
