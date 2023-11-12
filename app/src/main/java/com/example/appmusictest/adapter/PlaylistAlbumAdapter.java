package com.example.appmusictest.adapter;

import static com.example.appmusictest.MyApplication.TYPE_ALBUM;
import static com.example.appmusictest.MyApplication.TYPE_PLAYLIST;
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
import com.example.appmusictest.MyApplication;
import com.example.appmusictest.R;
import com.example.appmusictest.activity.FavoriteAlbumActivity;
import com.example.appmusictest.activity.FavoritePlaylistActivity;
import com.example.appmusictest.activity.PlaylistAlbumDetailActivity;
import com.example.appmusictest.model.Album;
import com.example.appmusictest.model.Playlist;

import java.util.ArrayList;

public class PlaylistAlbumAdapter<T> extends RecyclerView.Adapter<PlaylistAlbumAdapter.ViewHolder> {
    private ArrayList<T> arrayList;
    private ArrayList<T> arrayListFilter;
    private Context context;
    private static final String TAG = "Playlist_Adapter";
    private boolean isFilter = false;
    private int type;

    public PlaylistAlbumAdapter(ArrayList<T> arrayList, Context context, int t) {
        this.arrayList = arrayList;
        this.context = context;
        arrayListFilter = new ArrayList<>();
        type = t;
    }

    @NonNull
    @Override
    public PlaylistAlbumAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_playlist, parent, false);
        Log.d(TAG, "ViewHolder");
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PlaylistAlbumAdapter.ViewHolder holder, int position) {

        if (type == TYPE_PLAYLIST) {
            Playlist model;
            if (isFilter)  {
                model = (Playlist) arrayListFilter.get(position);
            } else {
                model = (Playlist) arrayList.get(position);
            }
            String activity = context.getClass().getSimpleName();

            holder.rowTv.setText(model.getTitle());
            Glide.with(context)
                    .load(model.getArtUrl())
                    .placeholder(R.mipmap.music_player_icon)
                    .into(holder.rowIv);

            holder.itemView.setOnClickListener(v -> {
                Intent intent = new Intent(context, PlaylistAlbumDetailActivity.class);
                Bundle bundle = new Bundle();
                bundle.putParcelable("playlist", model);
                intent.putExtras(bundle);
                context.startActivity(intent);
            });

            if (FavoritePlaylistActivity.isInFav(model)) {
                holder.rowFvIb.setImageResource(R.drawable.ic_favorite_purple);
            }
            holder.rowFvIb.setOnClickListener(v -> {
                if (!FavoritePlaylistActivity.isInFav(model)) {
                    FavoritePlaylistActivity.addPlaylist(model);
                    holder.rowFvIb.setImageResource(R.drawable.ic_favorite_purple);
                    Toast.makeText(v.getContext(), R.string.add_favorite_notification, Toast.LENGTH_SHORT).show();
                } else if (activity.equals(FavoritePlaylistActivity.class.getSimpleName())){
                    showDialog(position, model, null);
                } else {
                    holder.rowFvIb.setImageResource(R.drawable.ic_favorite_gray);
                    FavoritePlaylistActivity.removePlaylist(model);
                    Toast.makeText(v.getContext(), R.string.remove_favorite_notification, Toast.LENGTH_SHORT).show();
                }
            });
        } else if (type == TYPE_ALBUM) {
            Album model;
            if (isFilter)  {
                model = (Album) arrayListFilter.get(position);
            } else {
                model = (Album) arrayList.get(position);
            }
            String activity = context.getClass().getSimpleName();

            holder.rowTv.setText(model.getTitle());
            Glide.with(context)
                    .load(model.getArtUrl())
                    .placeholder(R.mipmap.music_player_icon)
                    .into(holder.rowIv);

            holder.itemView.setOnClickListener(v -> {
                Intent intent = new Intent(context, PlaylistAlbumDetailActivity.class);
                Bundle bundle = new Bundle();
                bundle.putParcelable("album", model);
                intent.putExtras(bundle);
                context.startActivity(intent);
            });

            if (FavoriteAlbumActivity.isInFav(model)) {
                holder.rowFvIb.setImageResource(R.drawable.ic_favorite_purple);
            }
            holder.rowFvIb.setOnClickListener(v -> {
                if (!FavoriteAlbumActivity.isInFav(model)) {
                    FavoriteAlbumActivity.addAlbum(model);
                    holder.rowFvIb.setImageResource(R.drawable.ic_favorite_purple);
                    Toast.makeText(v.getContext(), R.string.add_favorite_notification, Toast.LENGTH_SHORT).show();
                } else if (activity.equals(FavoriteAlbumActivity.class.getSimpleName())){
                    showDialog(position,null, model);
                } else {
                    holder.rowFvIb.setImageResource(R.drawable.ic_favorite_gray);
                    FavoriteAlbumActivity.removeAlbum(model);
                    Toast.makeText(v.getContext(), R.string.remove_favorite_notification, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void showDialog(int pos, Playlist playlist,Album album) {
        AlertDialog dialog;
        TextView titleDialogDeleteTv,contentDialogTv,submitBtn,cancelBtn;
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View view = LayoutInflater.from(context).inflate(R.layout.custom_dialog_delete,null);

        titleDialogDeleteTv = view.findViewById(R.id.titleDialogDeleteTv);
        contentDialogTv = view.findViewById(R.id.contentDialogTv);
        submitBtn = view.findViewById(R.id.submitBtn);
        cancelBtn = view.findViewById(R.id.cancelBtn);

        if (type == TYPE_ALBUM) {
            titleDialogDeleteTv.setText(album.getTitle());
        } else  if (type == TYPE_PLAYLIST) {
            titleDialogDeleteTv.setText(playlist.getTitle());
        }

        contentDialogTv.setText(R.string.delete_playlist_title);

        builder.setView(view);
        builder.setCancelable(true);
        dialog = builder.create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();


        submitBtn.setOnClickListener(v -> {
            if (type == TYPE_PLAYLIST) {
                FavoritePlaylistActivity.removePlaylist(playlist);
            } else if (type == TYPE_ALBUM) {
                FavoriteAlbumActivity.removeAlbum(album);
            }

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
            return arrayListFilter.size();
        }
        return arrayList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView rowTv;
        ImageView rowIv;
        ImageButton rowFvIb;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            rowIv = itemView.findViewById(R.id.rowIv);
            rowTv = itemView.findViewById(R.id.rowTv);
            rowFvIb = itemView.findViewById(R.id.rowFvIb);
        }
    }

    public void filter(String query) {

        if (!isFilter) {
            isFilter = true;
        }
        String lowerQuery = query.toLowerCase();
        arrayListFilter.clear();
        if (!lowerQuery.equals("")) {
//            for (Playlist item : playlistArrayList) {
//                if (item.getTitle().toLowerCase().contains(lowerQuery)) {
//                    playlistArrayListFilter.add(item);
//                }
//            }
            for (int i = 0; i < arrayList.size(); i++) {
                if (type == TYPE_PLAYLIST) {
                    if (((Playlist) arrayList.get(i)).getTitle().toLowerCase().contains(lowerQuery)) {
                        arrayListFilter.add(arrayList.get(i));
                    }
                } else if (type == TYPE_ALBUM) {
                    if (((Album) arrayList.get(i)).getTitle().toLowerCase().contains(lowerQuery)) {
                        arrayListFilter.add(arrayList.get(i));
                    }
                }
            }
        }
        if (arrayListFilter.isEmpty()) {
            Log.d(TAG, "not data query");
            noDataTv.setVisibility(View.VISIBLE);
        } else {
            noDataTv.setVisibility(View.GONE);
            Log.d(TAG, "query :" + lowerQuery);
        }
        notifyDataSetChanged();
    }
}
