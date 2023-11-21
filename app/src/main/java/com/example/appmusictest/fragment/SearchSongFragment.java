package com.example.appmusictest.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.appmusictest.R;
import com.example.appmusictest.adapter.SongAdapter;

import com.example.appmusictest.model.Song;


import java.util.ArrayList;


public class SearchSongFragment extends Fragment {

    private ArrayList<Song> songs ;
    private SongAdapter songAdapter;
    private RecyclerView recyclerView;
    private TextView noDataTv;
    private static final String TAG = "SearchAlbumFragment";

    public void setSongs(ArrayList<Song> songs) {
        this.songs = songs;
        if (songAdapter != null) {
            updateUi();
        }
    }
    public int getSize() {
        if (songs == null) songs = new ArrayList<>();
        return songs.size();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_song_search, container, false);
        recyclerView = view.findViewById(R.id.songRv);
        noDataTv = view.findViewById(R.id.noDataTv);
        setViewData();

        return view;
    }

    private void setViewData() {
        if (songs == null) {
            songs = new ArrayList<>();
        }
        updateUi();
    }
    private void updateUi() {
        songAdapter = new SongAdapter(songs, getActivity());
        recyclerView.setAdapter(songAdapter);
        if (songs.size() != 0) {
            noDataTv.setVisibility(View.GONE);
        } else {
            noDataTv.setVisibility(View.VISIBLE);
        }
    }
}