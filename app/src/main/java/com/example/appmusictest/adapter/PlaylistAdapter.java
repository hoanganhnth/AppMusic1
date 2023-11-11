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
import com.example.appmusictest.model.Album;
import com.example.appmusictest.model.Playlist;

import java.util.ArrayList;

public class PlaylistAdapter extends RecyclerView.Adapter<PlaylistAdapter.ViewHolder> {
    private ArrayList<Playlist> playlistArrayList;
    private ArrayList<Playlist> playlistArrayListFilter;
    private ArrayList<Album> albumArrayListFilter;
    private Context context;
    private static final String TAG = "Playlist_Adapter";
    private boolean isFilter = false;
    private boolean isPlaylist = false;

    public PlaylistAdapter(ArrayList<Playlist> playlistArrayList, Context context) {
        this.playlistArrayList = playlistArrayList;
        this.context = context;
        playlistArrayListFilter = new ArrayList<>();

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

        Playlist model;
        if (isFilter)  {
            model = playlistArrayListFilter.get(position);
        } else {
            model = playlistArrayList.get(position);
        }
        String activity = context.getClass().getSimpleName();

        holder.playlistTv.setText(model.getTitle());
        Glide.with(context)
                .load(model.getArtUrl())
                .placeholder(R.mipmap.music_player_icon)
                .into(holder.playlistIv);

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, PlaylistDetailActivity.class);
            Bundle bundle = new Bundle();
            bundle.putParcelable("playlist", model);
            intent.putExtras(bundle);
            context.startActivity(intent);
        });

        if (FavoritePlaylistActivity.isInFav(model)) {
            holder.playlistFvIb.setImageResource(R.drawable.ic_favorite_purple);
        }
        holder.playlistFvIb.setOnClickListener(v -> {
            if (!FavoritePlaylistActivity.isInFav(model)) {
                FavoritePlaylistActivity.addPlaylist(model);
                holder.playlistFvIb.setImageResource(R.drawable.ic_favorite_purple);
                Toast.makeText(v.getContext(), R.string.add_favorite_notification, Toast.LENGTH_SHORT).show();
            } else if (activity.equals(FavoritePlaylistActivity.class.getSimpleName())){
                showDialog(position, model);
            } else {
                holder.playlistFvIb.setImageResource(R.drawable.ic_favorite_gray);
                FavoritePlaylistActivity.removePlaylist(model);
                Toast.makeText(v.getContext(), R.string.remove_favorite_notification, Toast.LENGTH_SHORT).show();
            }
        });


    }

    private void showDialog(int pos, Playlist model) {
        AlertDialog dialog;
        TextView titleDialogDeleteTv,contentDialogTv,submitBtn,cancelBtn;
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View view = LayoutInflater.from(context).inflate(R.layout.custom_dialog_delete,null);

        titleDialogDeleteTv = view.findViewById(R.id.titleDialogDeleteTv);
        contentDialogTv = view.findViewById(R.id.contentDialogTv);
        submitBtn = view.findViewById(R.id.submitBtn);
        cancelBtn = view.findViewById(R.id.cancelBtn);

        titleDialogDeleteTv.setText(model.getTitle());
        contentDialogTv.setText(R.string.delete_playlist_title);

        builder.setView(view);
        builder.setCancelable(true);
        dialog = builder.create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();


        submitBtn.setOnClickListener(v -> {
            FavoritePlaylistActivity.removePlaylist(model);
            notifyItemRemoved(pos);
            notifyItemRangeChanged(pos, FavoritePlaylistActivity.getSize());
            dialog.dismiss();
            Toast.makeText(v.getContext(), R.string.remove_favorite_notification, Toast.LENGTH_SHORT).show();
        });
        cancelBtn.setOnClickListener(v -> dialog.dismiss());
    }

    @Override
    public int getItemCount() {
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
            playlistIv = itemView.findViewById(R.id.rowIv);
            playlistTv = itemView.findViewById(R.id.rowTv);
            playlistFvIb = itemView.findViewById(R.id.rowFvIb);
        }
    }

    public void filter(String query) {

        if (!isFilter) {
            isFilter = true;
        }
        String lowerQuery = query.toLowerCase();
        playlistArrayListFilter.clear();
        if (!lowerQuery.equals("")) {
            for (Playlist item : playlistArrayList) {
                if (item.getTitle().toLowerCase().contains(lowerQuery)) {
                    playlistArrayListFilter.add(item);
                }
            }
        }
        if (playlistArrayListFilter.isEmpty()) {
            Log.d(TAG, "not data query");
            noDataTv.setVisibility(View.VISIBLE);
        } else {
            noDataTv.setVisibility(View.GONE);
            Log.d(TAG, "query :" + lowerQuery);
        }
        notifyDataSetChanged();
    }
}
