package com.example.appmusictest.adapter;

import static com.example.appmusictest.fragment.PlaylistFragment.noDataTv;

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
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.appmusictest.R;
import com.example.appmusictest.activity.SongListActivity;
import com.example.appmusictest.model.Playlist;

import java.util.ArrayList;

public class PlaylistAdapter extends RecyclerView.Adapter<PlaylistAdapter.ViewHolder> {
    private ArrayList<Playlist> playlistArrayList;
    private ArrayList<Playlist> playlistArrayListFilter;
    private Context context;
    private static final String TAG = "Playlist_Adapter";

    public PlaylistAdapter(ArrayList<Playlist> playlistArrayList, Context context) {
        this.playlistArrayList = playlistArrayList;
        this.context = context;
        playlistArrayListFilter = new ArrayList<>();
    }

    @NonNull
    @Override
    public PlaylistAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_playlist, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PlaylistAdapter.ViewHolder holder, int position) {


        holder.playlistTv.setText(playlistArrayListFilter.get(position).getTitle());
        Glide.with(context)
                .load(playlistArrayListFilter.get(position).getArtUrl())
                .placeholder(R.mipmap.ic_launcher_round)
                .into(holder.playlistIv);
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, SongListActivity.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable("playlist", playlistArrayListFilter.get(position));
            intent.putExtras(bundle);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return playlistArrayListFilter.size();
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

    public void filter(String query) {
        query = query.toLowerCase();
        playlistArrayListFilter.clear();
        if (!query.equals("")) {
            for (Playlist item : playlistArrayList) {
                if (item.getTitle().toLowerCase().contains(query)) {
                    playlistArrayListFilter.add(item);
                }
            }
        }
        if (playlistArrayListFilter.isEmpty()) {
            Log.d(TAG, "not data query");
            noDataTv.setVisibility(View.VISIBLE);
        } else {
            noDataTv.setVisibility(View.GONE);
            Log.d(TAG, "query :" + query);
        }
        notifyDataSetChanged();
    }
}
