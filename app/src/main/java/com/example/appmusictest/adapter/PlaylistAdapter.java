package com.example.appmusictest.adapter;



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
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.appmusictest.FavoriteHelper;
import com.example.appmusictest.MyApplication;
import com.example.appmusictest.R;
import com.example.appmusictest.activity.FavoritePlaylistActivity;
import com.example.appmusictest.activity.MainActivity;
import com.example.appmusictest.activity.PlaylistAlbumDetailActivity;
import com.example.appmusictest.dialog.MyProgress;
import com.example.appmusictest.model.Album;
import com.example.appmusictest.model.Playlist;
import com.google.android.material.imageview.ShapeableImageView;

import java.util.ArrayList;

public class PlaylistAdapter extends RecyclerView.Adapter<PlaylistAdapter.ViewHolder> {
    private ArrayList<Playlist> arrayList;
    private Context context;
    private static final String TAG = "Playlist_Adapter";
    private boolean isFilter = false;
    private MyProgress myProgress;

    public PlaylistAdapter(ArrayList<Playlist> arrayList, Context context) {
        this.arrayList = arrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public PlaylistAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_playlist, parent, false);
        Log.d(TAG, "ViewHolder");
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Playlist model;
        model = arrayList.get(position);

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
                FavoriteHelper.actionWithFav(context, MainActivity.getIdUser(),model.getId(), FavoriteHelper.TYPE_ADD, MyApplication.TYPE_PLAYLIST, model);
                holder.rowFvIb.setImageResource(R.drawable.ic_favorite_purple);
            } else if (activity.equals(FavoritePlaylistActivity.class.getSimpleName())){
                showDialog(position, model);
            } else {
                holder.rowFvIb.setImageResource(R.drawable.ic_favorite_gray);
                FavoriteHelper.actionWithFav(context, MainActivity.getIdUser(),model.getId(), FavoriteHelper.TYPE_DELETE, MyApplication.TYPE_PLAYLIST, model);
            }
        });
    }

    private void showDialog(int pos, Playlist playlist) {
        AlertDialog dialog;
        TextView titleDialogDeleteTv,contentDialogTv,submitBtn,cancelBtn;
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View view = LayoutInflater.from(context).inflate(R.layout.custom_dialog_delete,null);

        titleDialogDeleteTv = view.findViewById(R.id.titleDialogDeleteTv);
        contentDialogTv = view.findViewById(R.id.contentDialogTv);
        submitBtn = view.findViewById(R.id.submitBtn);
        cancelBtn = view.findViewById(R.id.cancelBtn);


        titleDialogDeleteTv.setText(playlist.getTitle());
        contentDialogTv.setText(R.string.delete_playlist_title);

        builder.setView(view);
        builder.setCancelable(true);
        dialog = builder.create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();


        submitBtn.setOnClickListener(v -> {

            FavoriteHelper.actionWithFav(context, MainActivity.getIdUser(),playlist.getId(), FavoriteHelper.TYPE_DELETE, MyApplication.TYPE_PLAYLIST, playlist);

            arrayList.remove(pos);
            notifyItemRemoved(pos);
            notifyItemRangeChanged(pos, arrayList.size() - pos);
            dialog.dismiss();
        });
        cancelBtn.setOnClickListener(v -> dialog.dismiss());
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView rowTv;
        ShapeableImageView rowIv;
        ImageButton rowFvIb;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            rowIv = itemView.findViewById(R.id.rowIv);
            rowTv = itemView.findViewById(R.id.rowTv);
            rowFvIb = itemView.findViewById(R.id.rowFvIb);
        }
    }

//    public void filter(String query) {
//        myProgress = new MyProgress(context);
//        myProgress.show();
//        if (!isFilter) {
//            isFilter = true;
//        }
//        String lowerQuery = query.toLowerCase();
//        arrayListFilter.clear();
//        if (!lowerQuery.equals("")) {
////            for (Playlist item : playlistArrayList) {
////                if (item.getTitle().toLowerCase().contains(lowerQuery)) {
////                    playlistArrayListFilter.add(item);
////                }
////            }
//            for (int i = 0; i < arrayList.size(); i++) {
//                if (type == TYPE_PLAYLIST) {
//                    if (((Playlist) arrayList.get(i)).getTitle().toLowerCase().contains(lowerQuery)) {
//                        arrayListFilter.add(arrayList.get(i));
//                    }
//                } else if (type == TYPE_ALBUM) {
//                    if (((Album) arrayList.get(i)).getTitle().toLowerCase().contains(lowerQuery)) {
//                        arrayListFilter.add(arrayList.get(i));
//                    }
//                }
//            }
//        }
//        if (arrayListFilter.isEmpty()) {
//            Log.d(TAG, "not data query");
//            noDataTv.setVisibility(View.VISIBLE);
//        } else {
//            noDataTv.setVisibility(View.GONE);
//            Log.d(TAG, "query :" + lowerQuery);
//        }
//        myProgress.dismiss();
//        notifyDataSetChanged();
//    }
}
