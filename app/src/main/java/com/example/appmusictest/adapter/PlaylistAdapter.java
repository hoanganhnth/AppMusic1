package com.example.appmusictest.adapter;

import static com.example.appmusictest.fragment.PlaylistFragment.noDataTv;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.appmusictest.R;
import com.example.appmusictest.activity.FavoritePlaylistActivity;
import com.example.appmusictest.activity.PlaylistDetailActivity;
import com.example.appmusictest.model.Playlist;

import java.util.ArrayList;

public class PlaylistAdapter extends RecyclerView.Adapter<PlaylistAdapter.ViewHolder> {
    private ArrayList<Playlist> playlistArrayList;
    private ArrayList<Playlist> playlistArrayListFilter;
    private Context context;
    private static final String TAG = "Playlist_Adapter";
    private boolean isFilter = false;

    public PlaylistAdapter(ArrayList<Playlist> playlistArrayList, Context context) {
        this.playlistArrayList = playlistArrayList;
        this.context = context;
        playlistArrayListFilter = new ArrayList<>();
        Log.d(TAG, "construct");

    }

    @NonNull
    @Override
    public PlaylistAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_playlist, parent, false);
        Log.d(TAG, "ViewHolder");
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PlaylistAdapter.ViewHolder holder, int position) {

        if (isFilter) {
            holder.playlistTv.setText(playlistArrayListFilter.get(position).getTitle());
            Glide.with(context)
                    .load(playlistArrayListFilter.get(position).getArtUrl())
                    .placeholder(R.mipmap.music_player_icon)
                    .into(holder.playlistIv);
            holder.itemView.setOnClickListener(v -> {
                Intent intent = new Intent(context, PlaylistDetailActivity.class);
                Bundle bundle = new Bundle();
                bundle.putParcelable("playlist", playlistArrayListFilter.get(position));
                intent.putExtras(bundle);
                context.startActivity(intent);

            });
            if (FavoritePlaylistActivity.isInFav(playlistArrayListFilter.get(position))) {
                holder.playlistFvIb.setImageResource(R.drawable.ic_favorite_purple);
            }
            holder.playlistFvIb.setOnClickListener(v -> {
                if (FavoritePlaylistActivity.isInFav(playlistArrayListFilter.get(position))) {
                    showDialog(position, holder, true);
                } else {
                    FavoritePlaylistActivity.addPlaylist(playlistArrayListFilter.get(position));
                    holder.playlistFvIb.setImageResource(R.drawable.ic_favorite_purple);
                    Toast.makeText(v.getContext(), R.string.add_favorite_notification, Toast.LENGTH_SHORT).show();
                }
            });

            Log.d(TAG, "filter");
        } else {
            holder.playlistTv.setText(playlistArrayList.get(position).getTitle());
            Glide.with(context)
                    .load(playlistArrayList.get(position).getArtUrl())
                    .placeholder(R.mipmap.music_player_icon)
                    .into(holder.playlistIv);
            holder.itemView.setOnClickListener(v -> {
                Intent intent = new Intent(context, PlaylistDetailActivity.class);
                Bundle bundle = new Bundle();
                bundle.putParcelable("playlist", playlistArrayList.get(position));
                intent.putExtras(bundle);
                context.startActivity(intent);
            });
            if (FavoritePlaylistActivity.isInFav(playlistArrayList.get(position))) {
                holder.playlistFvIb.setImageResource(R.drawable.ic_favorite_purple);
            }
            holder.playlistFvIb.setOnClickListener(v -> {
                showDialog(position, holder, false);

            });
            Log.d(TAG, "not filter");
        }
    }

    private void showDialog(int pos, ViewHolder holder, boolean isFilter) {
        AlertDialog dialog;
        TextView titleDialogDeleteTv,contentDialogTv,submitBtn,cancelBtn;
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View view = LayoutInflater.from(context).inflate(R.layout.custom_delete_dialog,null);
        titleDialogDeleteTv = view.findViewById(R.id.titleDialogDeleteTv);
        contentDialogTv = view.findViewById(R.id.contentDialogTv);
        submitBtn = view.findViewById(R.id.submitBtn);
        cancelBtn = view.findViewById(R.id.cancelBtn);
        if (isFilter) {
            titleDialogDeleteTv.setText(playlistArrayListFilter.get(pos).getTitle());
        } else {
            titleDialogDeleteTv.setText(playlistArrayList.get(pos).getTitle());
        }
        contentDialogTv.setText(R.string.delete_playlist_title);

        builder.setView(view);
        builder.setCancelable(true);
        dialog = builder.create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();

        submitBtn.setOnClickListener(v -> {
            if (!isFilter) {
                FavoritePlaylistActivity.removePlaylist(playlistArrayList.get(pos));
                notifyItemRemoved(pos);
                notifyItemRangeChanged(pos, FavoritePlaylistActivity.getSize());
            } else {
                holder.playlistFvIb.setImageResource(R.drawable.ic_favorite_gray);
                FavoritePlaylistActivity.removePlaylist(playlistArrayListFilter.get(pos));
            }
            dialog.dismiss();
            Toast.makeText(v.getContext(), R.string.remove_favorite_notification, Toast.LENGTH_SHORT).show();
        });
        cancelBtn.setOnClickListener(v -> dialog.dismiss());
    }

    @Override
    public int getItemCount() {
        Log.d(TAG, "getItemCount");
        if (isFilter) {
            return playlistArrayListFilter.size();
        }
        return playlistArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView playlistTv;
        ImageView playlistIv;
        ImageButton playlistFvIb;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            playlistIv = itemView.findViewById(R.id.playlistIv);
            playlistTv = itemView.findViewById(R.id.playlistTv);
            playlistFvIb = itemView.findViewById(R.id.playlistFvIb);
        }
    }

    public void filter(String query) {
        if (!isFilter) {
            isFilter = true;
        }
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
