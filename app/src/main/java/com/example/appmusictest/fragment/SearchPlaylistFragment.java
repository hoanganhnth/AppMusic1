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
import java.util.ArrayList;



public class SearchPlaylistFragment extends Fragment {

    private ArrayList<Playlist> playlists;

    private RecyclerView playlistRv;
    private static final String TAG = "Playlist_Fragment";
    private PlaylistAdapter playlistAdapter;
    public static TextView noDataTv;

    public void setPlaylists(ArrayList<Playlist> playlists) {
        this.playlists = playlists;
        updateUI();
    }

    public int getSize() {
        if (playlists == null) playlists = new ArrayList<>();
        return playlists.size();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_playlist, container, false);
        initView(view);
        setViewData();
        return view;
    }

    private void setViewData() {
        if (playlists == null) {
            playlists = new ArrayList<>();
        }
    }
    private void initView(View view) {
        playlistRv = view.findViewById(R.id.playlistRv);
        noDataTv = view.findViewById(R.id.noDataTv);

    }
    private void updateUI() {
        playlistAdapter = new PlaylistAdapter(playlists,getActivity());
        playlistRv.setAdapter(playlistAdapter);
        if (playlists.size() == 0 ) {
            noDataTv.setVisibility(View.VISIBLE);
        } else {
            noDataTv.setVisibility(View.GONE);
        }
    }
}