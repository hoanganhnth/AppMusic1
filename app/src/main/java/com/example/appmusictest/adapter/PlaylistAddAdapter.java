package com.example.appmusictest.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.appmusictest.R;
import com.example.appmusictest.activity.MainActivity;
import com.example.appmusictest.dialog.MyProgress;
import com.example.appmusictest.model.Playlist;
import com.example.appmusictest.model.api.ApiResponse;
import com.example.appmusictest.service.ApiService;
import com.example.appmusictest.service.DataService;
import com.google.android.material.imageview.ShapeableImageView;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PlaylistAddAdapter extends RecyclerView.Adapter<PlaylistAddAdapter.ViewHolder> {

    private ArrayList<Playlist> playlistArrayList;
    private Context context;
    private String idSong;
    private static final String TAG = "PlaylistAddAdapter";

    public PlaylistAddAdapter(ArrayList<Playlist> playlistArrayList, Context context, String idSong) {
        this.playlistArrayList = playlistArrayList;
        this.context = context;
        this.idSong = idSong;
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
            DataService dataService = ApiService.getService();
            Call<ApiResponse> callback = dataService.addSongToPlaylist(MainActivity.getIdUser(), idSong, playlistArrayList.get(position).getId());
            callback.enqueue(new Callback<ApiResponse>() {
                @Override
                public void onResponse(@NonNull Call<ApiResponse> call, @NonNull Response<ApiResponse> response) {

                    assert response.body() != null;
                    Toast.makeText(context, response.body().getMessage(), Toast.LENGTH_SHORT).show();

                }

                @Override
                public void onFailure(@NonNull Call<ApiResponse> call, @NonNull Throwable t) {
                    Log.d(TAG, "Fail get data due to: " + t.getMessage());
                }
            });
        });
        holder.itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.dialog_color));
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
