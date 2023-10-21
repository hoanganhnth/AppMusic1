package com.example.appmusictest.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
    }

    private void getData() {
        DataService dataService = ApiService.getService();
        Call<List<Playlist>> callback = dataService.getPlaylistCurrentDay();
        callback.enqueue(new Callback<List<Playlist>>() {
            @Override
            public void onResponse(Call<List<Playlist>> call, Response<List<Playlist>> response) {

                playlists = (ArrayList<Playlist>) response.body();
                playlistRv.setAdapter(new PlaylistAdapter(playlists,getActivity(),getChildFragmentManager()));
            }

            @Override
            public void onFailure(Call<List<Playlist>> call, Throwable t) {
                Log.d("PlF", "fail get data due to: " + t.getMessage() );

            }
        });

    }
}