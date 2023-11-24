package com.example.appmusictest.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.appmusictest.MyApplication;
import com.example.appmusictest.R;
import com.example.appmusictest.activity.PlaylistAlbumDetailActivity;
import com.example.appmusictest.activity.ShowMorePlaylistActivity;
import com.example.appmusictest.model.Playlist;
import com.google.android.material.imageview.ShapeableImageView;

import java.util.ArrayList;

public class PlaylistSuggestAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private ArrayList<Playlist> arrayList;
    private ArrayList<Playlist> arrayListAll;
    private final Context context;
    private static final String TAG = "PlaylistSuggestAdapter";
    private boolean bigSize;
    private final int VIEW_TYPE_NORMAL = 1;
    private final int VIEW_TYPE_END = 2;
    private final int VIEW_TYPE_BIG = 3;


    public PlaylistSuggestAdapter(ArrayList<Playlist> arrayList, ArrayList<Playlist> arrayListAll, Context context, boolean big) {
        this.arrayList = arrayList;
        this.arrayListAll = arrayListAll;
        this.context = context;
        bigSize = big;
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (bigSize) {
            view = LayoutInflater.from(context).inflate(R.layout.row_playlist_suggest_big, parent, false);
            return new BigViewHolder(view);
        } else if (viewType == VIEW_TYPE_NORMAL) {
            view = LayoutInflater.from(context).inflate(R.layout.row_playlist_suggest, parent, false);
            return new NormalViewHolder(view);
        } else {
            view = LayoutInflater.from(context).inflate(R.layout.row_playlist_album_end, parent, false);
            return new EndViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {


        if (getItemViewType(position) == VIEW_TYPE_NORMAL) {
            Playlist modelPlaylist = arrayList.get(position);
            ((NormalViewHolder) holder).titleSuggestTv.setText(modelPlaylist.getTitle());
            Glide.with(context).load(modelPlaylist.getArtUrl()).placeholder(R.mipmap.music_player_icon).into(((NormalViewHolder) holder).imgSuggestIv);
            holder.itemView.setOnClickListener(v -> {
                Intent intent = new Intent(context, PlaylistAlbumDetailActivity.class);
                Bundle bundle = new Bundle();
                bundle.putParcelable("playlist", modelPlaylist);
                intent.putExtras(bundle);
                context.startActivity(intent);
            });
        } else if (getItemViewType(position) == VIEW_TYPE_END) {
            ((EndViewHolder) holder).showMoreAlbumRl.setOnClickListener(v -> {
                Intent intent = new Intent(context, ShowMorePlaylistActivity.class);
                Bundle bundle = new Bundle();
                bundle.putParcelableArrayList("playlists", arrayListAll);
                intent.putExtras(bundle);
                context.startActivity(intent);
            });
        } else if (getItemViewType(position) == VIEW_TYPE_BIG) {
            Playlist modelPlaylist = arrayList.get(position);
            ((BigViewHolder) holder).titleSuggestTv.setText(modelPlaylist.getTitle());
            Glide.with(context).load(modelPlaylist.getArtUrl()).placeholder(R.mipmap.music_player_icon).into(((BigViewHolder) holder).imgSuggestIv);
            holder.itemView.setOnClickListener(v -> {
                Intent intent = new Intent(context, PlaylistAlbumDetailActivity.class);
                Bundle bundle = new Bundle();
                bundle.putParcelable("playlist", modelPlaylist);
                intent.putExtras(bundle);
                context.startActivity(intent);
            });
        }


    }


    @Override
    public int getItemViewType(int position) {
        if (bigSize) {
            return VIEW_TYPE_BIG;
        } else if (position == arrayList.size() && arrayListAll.size() > MyApplication.NUMBER_SUGGEST) {
            return VIEW_TYPE_END;
        } else {
            return VIEW_TYPE_NORMAL;
        }
    }

    @Override
    public int getItemCount() {
        if (bigSize || arrayListAll.size() <= MyApplication.NUMBER_SUGGEST) {
            return arrayList.size();
        }
        return arrayList.size() + 1;
    }

    public static class NormalViewHolder extends RecyclerView.ViewHolder {
        TextView titleSuggestTv;
        ShapeableImageView imgSuggestIv;

        public NormalViewHolder(@NonNull View itemView) {
            super(itemView);
            titleSuggestTv = itemView.findViewById(R.id.titleSuggestTv);
            imgSuggestIv = itemView.findViewById(R.id.imgSuggestIv);
        }
    }

    public static class EndViewHolder extends RecyclerView.ViewHolder {
        RelativeLayout showMoreAlbumRl;
        ImageView showMoreAlbumIv;

        public EndViewHolder(@NonNull View itemView) {
            super(itemView);
            showMoreAlbumRl = itemView.findViewById(R.id.showMoreAlbumRl);
            showMoreAlbumIv = itemView.findViewById(R.id.showMoreAlbumIv);
        }
    }

    public static class BigViewHolder extends RecyclerView.ViewHolder {
        TextView titleSuggestTv;
        ShapeableImageView imgSuggestIv;

        public BigViewHolder(@NonNull View itemView) {
            super(itemView);
            titleSuggestTv = itemView.findViewById(R.id.titleSuggestTv);
            imgSuggestIv = itemView.findViewById(R.id.imgSuggestIv);
        }
    }
}
