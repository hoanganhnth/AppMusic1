package com.example.appmusictest.fragment;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.appmusictest.R;
import com.example.appmusictest.activity.MusicPlayerActivity;
import com.example.appmusictest.adapter.ListPlayAdapter;

public class ListPlayFragment extends Fragment {


    private RecyclerView listPlayRv;
    private static final String TAG = "ListPlayFragment";
    private ListPlayAdapter listPlayAdapter;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_list_play, container, false);

        listPlayRv = view.findViewById(R.id.listPlayRv);
        listPlayAdapter = new ListPlayAdapter(MusicPlayerActivity.currentSongs);
        listPlayRv.setAdapter(listPlayAdapter);
        listPlayAdapter.setCurrentlyPlayingPosition(MusicPlayerActivity.songPosition);
        listPlayAdapter.notifyDataSetChanged();

        Log.d(TAG, "on create view");
        return view;
    }

    public void onSongChanged() {
        if (getView() != null) {
            listPlayAdapter.setCurrentlyPlayingPosition(MusicPlayerActivity.songPosition);
            listPlayAdapter.notifyDataSetChanged();
        }
    }
}