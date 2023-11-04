package com.example.appmusictest.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.appmusictest.R;
import com.example.appmusictest.adapter.PlaylistAdapter;
import com.example.appmusictest.model.Playlist;
import com.example.appmusictest.service.ApiService;
import com.example.appmusictest.service.DataService;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class PlaylistFragment extends Fragment {

    private ArrayList<Playlist> playlists;
    private RecyclerView playlistRv;
    private static final String TAG = "Playlist_Fragment";
    private PlaylistAdapter playlistAdapter;
    private boolean isFilter = false;
    public static TextView noDataTv;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_playlist, container, false);

        initView(view);
        getData();
        return view;
    }

    private void initView(View view) {
        playlistRv = view.findViewById(R.id.playlistRv);
        noDataTv = view.findViewById(R.id.noDataTv);
    }

    private void getData() {
        Bundle bundle = getArguments();
        if (bundle != null) {
            String query = bundle.getString("query");

            DataService dataService = ApiService.getService();
            Call<List<Playlist>> callback = dataService.getPlaylistCurrentDay();
            callback.enqueue(new Callback<List<Playlist>>() {
                @Override
                public void onResponse(@NonNull Call<List<Playlist>> call, @NonNull Response<List<Playlist>> response) {

                    playlists = (ArrayList<Playlist>) response.body();
                    playlistAdapter = new PlaylistAdapter(playlists,getActivity());
                    playlistAdapter.filter(query);
                    playlistRv.setAdapter(playlistAdapter);
                }

                @Override
                public void onFailure(@NonNull Call<List<Playlist>> call, @NonNull Throwable t) {
                    Log.d(TAG, "Fail get data due to: " + t.getMessage() );

                }
            });
        }
    }
    public void filter(String query) {
        if (playlistAdapter != null) {
            playlistAdapter.filter(query);
        }
    }
}