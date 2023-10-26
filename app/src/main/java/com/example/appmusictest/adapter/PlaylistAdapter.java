package com.example.appmusictest.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.appmusictest.R;
import com.example.appmusictest.activity.SongListActivity;
import com.example.appmusictest.fragment.PlaylistFragment;
import com.example.appmusictest.model.Playlist;

import java.util.ArrayList;

public class PlaylistAdapter extends RecyclerView.Adapter<PlaylistAdapter.ViewHolder> {
    private ArrayList<Playlist> playlistArrayList;
    private Context context;


    public PlaylistAdapter(ArrayList<Playlist> playlistArrayList, Context context) {
        this.playlistArrayList = playlistArrayList;
        this.context = context;

    }

    @NonNull
    @Override
    public PlaylistAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_playlist, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PlaylistAdapter.ViewHolder holder, int position) {

        holder.playlistTv.setText(playlistArrayList.get(position).getTitle());
        Glide.with(context)
                .load(playlistArrayList.get(position).getArtUrl())
                .placeholder(R.mipmap.ic_launcher_round)
                .into(holder.playlistIv);
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, SongListActivity.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable("playlist", playlistArrayList.get(position));
            intent.putExtras(bundle);
            context.startActivity(intent);

        });
    }

    @Override
    public int getItemCount() {
        if (playlistArrayList != null) {
            return playlistArrayList.size();
        }
        return 0;

    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView playlistTv;
        ImageView playlistIv;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            playlistIv = itemView.findViewById(R.id.playlistIv);
            playlistTv = itemView.findViewById(R.id.playlistTv);
        }
    }
}
