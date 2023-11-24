package com.example.appmusictest.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.appmusictest.R;
import com.example.appmusictest.adapter.AlbumAdapter;
import com.example.appmusictest.model.Album;

import java.util.ArrayList;

public class SearchAlbumFragment extends Fragment {

    private ArrayList<Album> albums;
    private AlbumAdapter albumAdapter;
    private RecyclerView recyclerView;
    private TextView noDataTv;
    private static final String TAG = "SearchAlbumFragment";

    public void setAlbums(ArrayList<Album> albums) {
        this.albums = albums;
        if (albumAdapter != null) {
            updateUi();
        }
    }
    public int getSize() {
        if (albums == null) albums = new ArrayList<>();
        return albums.size();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_search_album, container, false);
        noDataTv = view.findViewById(R.id.noDataTv);
        recyclerView = view.findViewById(R.id.albumRv);

        setViewData();
        return view;
    }

    private void setViewData() {

        if (albums == null) {
            albums = new ArrayList<>();
        }

        updateUi();
    }

    private void updateUi() {
        albumAdapter = new AlbumAdapter(albums, getActivity());
        recyclerView.setAdapter(albumAdapter);
        if (albums.size() != 0) {
            noDataTv.setVisibility(View.GONE);
        } else {
            noDataTv.setVisibility(View.VISIBLE);
        }
    }

}