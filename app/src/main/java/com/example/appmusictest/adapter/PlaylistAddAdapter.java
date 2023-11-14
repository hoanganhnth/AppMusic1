package com.example.appmusictest.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.appmusictest.R;
import com.example.appmusictest.model.Playlist;
import com.google.android.material.imageview.ShapeableImageView;

import java.util.ArrayList;

public class PlaylistAddAdapter extends RecyclerView.Adapter<PlaylistAddAdapter.ViewHolder> {

    private ArrayList<Playlist> playlistArrayList;
    private Context context;

    public PlaylistAddAdapter(ArrayList<Playlist> playlistArrayList, Context context) {
        this.playlistArrayList = playlistArrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public PlaylistAddAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_playlist, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PlaylistAddAdapter.ViewHolder holder, int position) {
        holder.playlistTv.setText(playlistArrayList.get(position).getTitle());
        Glide.with(context)
                .load(playlistArrayList.get(position).getArtUrl())
                .placeholder(R.mipmap.music_player_icon)
                .into(holder.playlistIv);

        holder.itemView.setOnClickListener(v -> {

        });
        holder.playlistFvIb.setVisibility(View.GONE);
    }

    @Override
    public int getItemCount() {
        return playlistArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView playlistTv;
        ShapeableImageView playlistIv;
        ImageButton playlistFvIb;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            playlistIv = itemView.findViewById(R.id.rowIv);
            playlistTv = itemView.findViewById(R.id.rowTv);
            playlistFvIb = itemView.findViewById(R.id.rowFvIb);
        }
    }
}
