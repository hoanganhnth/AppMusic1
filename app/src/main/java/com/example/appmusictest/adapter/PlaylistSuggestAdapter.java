package com.example.appmusictest.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.appmusictest.R;
import com.example.appmusictest.activity.SongListActivity;
import com.example.appmusictest.model.Playlist;

import java.util.ArrayList;

public class PlaylistSuggestAdapter extends RecyclerView.Adapter<PlaylistSuggestAdapter.ViewHolder> {
    private ArrayList<? extends Playlist> playlistArrayList;
    private final Context context;
    private static final String TAG = "Playlist_Adapter";
    private boolean bigSize;


    public PlaylistSuggestAdapter(ArrayList<? extends Playlist> playlistArrayList, Context context, boolean big) {
        this.playlistArrayList = playlistArrayList;
        this.context = context;
        bigSize = big;
    }

    @NonNull
    @Override
    public PlaylistSuggestAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (bigSize) {
            view = LayoutInflater.from(context).inflate(R.layout.row_playlist_suggest_big, parent, false);
        } else {
            view = LayoutInflater.from(context).inflate(R.layout.row_playlist_suggest, parent, false);
        }
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {


        holder.titleSuggestTv.setText(playlistArrayList.get(position).getTitle());
        Glide.with(context)
                .load(playlistArrayList.get(position).getArtUrl())
                .placeholder(R.mipmap.ic_launcher_round)
                .into(holder.imgSuggestIv);
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, SongListActivity.class);
            Bundle bundle = new Bundle();
            bundle.putParcelable("playlist", playlistArrayList.get(position));
            intent.putExtras(bundle);
            context.startActivity(intent);
        });
    }



    @Override
    public int getItemCount() {
        return playlistArrayList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView titleSuggestTv;
        ImageView imgSuggestIv;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            titleSuggestTv = itemView.findViewById(R.id.titleSuggestTv);
            imgSuggestIv = itemView.findViewById(R.id.imgSuggestIv);
        }
    }
}
