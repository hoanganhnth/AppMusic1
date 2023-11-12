package com.example.appmusictest.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.appmusictest.R;
import com.example.appmusictest.activity.PlaylistAlbumDetailActivity;
import com.example.appmusictest.model.Album;
import com.example.appmusictest.model.Playlist;

import java.util.ArrayList;

public class PlaylistAlbumSuggestAdapter<T> extends RecyclerView.Adapter<PlaylistAlbumSuggestAdapter.ViewHolder> {
    private ArrayList<? extends T> arrayList;
    private final Context context;
    private static final String TAG = "PlaylistAlbum_Adapter";
    private boolean bigSize;

    public PlaylistAlbumSuggestAdapter(ArrayList<? extends T> arrayList, Context context, boolean big ) {
        this.arrayList = arrayList;
        this.context = context;
        bigSize = big;
    }



    @NonNull
    @Override
    public PlaylistAlbumSuggestAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
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


        if (arrayList.get(position) instanceof Playlist) {
            Playlist modelPlaylist = (Playlist) arrayList.get(position);
            holder.titleSuggestTv.setText(modelPlaylist.getTitle());
            Glide.with(context)
                    .load(modelPlaylist.getArtUrl())
                    .placeholder(R.mipmap.music_player_icon)
                    .into(holder.imgSuggestIv);
            holder.itemView.setOnClickListener(v -> {
                Intent intent = new Intent(context, PlaylistAlbumDetailActivity.class);
                Bundle bundle = new Bundle();
                bundle.putParcelable("playlist", modelPlaylist);
                intent.putExtras(bundle);
                context.startActivity(intent);
            });
        } else {
            Album modelAlbum = (Album) arrayList.get(position);
            holder.titleSuggestTv.setText(modelAlbum.getTitle());
            Glide.with(context)
                    .load(modelAlbum.getArtUrl())
                    .placeholder(R.mipmap.music_player_icon)
                    .into(holder.imgSuggestIv);
            holder.itemView.setOnClickListener(v -> {
                Intent intent = new Intent(context, PlaylistAlbumDetailActivity.class);
                Bundle bundle = new Bundle();
                bundle.putParcelable("album", modelAlbum);
                intent.putExtras(bundle);
                context.startActivity(intent);
            });
        }

    }



    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView titleSuggestTv;
        ImageView imgSuggestIv;
        RelativeLayout showMoreAlbumRl;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            titleSuggestTv = itemView.findViewById(R.id.titleSuggestTv);
            imgSuggestIv = itemView.findViewById(R.id.imgSuggestIv);
            showMoreAlbumRl = itemView.findViewById(R.id.showMoreAlbumRl);
        }
    }
}
