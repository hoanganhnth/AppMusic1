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

import com.example.appmusictest.MyApplication;
import com.example.appmusictest.R;
import com.example.appmusictest.adapter.PlaylistAlbumAdapter;
import com.example.appmusictest.adapter.SongAdapter;
import com.example.appmusictest.dialog.MyProgress;
import com.example.appmusictest.model.Album;
import com.example.appmusictest.model.Playlist;
import com.example.appmusictest.model.Song;
import com.example.appmusictest.service.ApiService;
import com.example.appmusictest.service.DataService;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchSongFragment extends Fragment {

    private ArrayList<Song> songs = new ArrayList<>();
//    private ArrayList<Song> songsFilter;
    private SongAdapter songAdapter;
    private RecyclerView recyclerView;
    private TextView noDataTv;
    private static final String TAG = "SearchAlbumFragment";

    public void setSongs(ArrayList<Song> songs) {
        this.songs = songs;
        if (songAdapter != null) {
            songAdapter.notifyDataSetChanged();
        }
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
        songAdapter = new SongAdapter(songs, getActivity());
        recyclerView.setAdapter(songAdapter);
        if (songs.size() == 0) {
            noDataTv.setVisibility(View.VISIBLE);
        }
    }

//    private void getData() {
//        songs = new ArrayList<>();
//        songsFilter = new ArrayList<>();
//        Bundle bundle = getArguments();
//        if (bundle != null) {
//            String query = bundle.getString("query");
//
//
//            getDataServer(query);
//
//
//        }
//    }

//    private void getDataServer(String query) {
//        DataService dataService = ApiService.getService();
//        Call<List<Song>> callback = dataService.getSongByPlaylist("1");
//        callback.enqueue(new Callback<List<Song>>() {
//            @Override
//            public void onResponse(@NonNull Call<List<Song>> call, @NonNull Response<List<Song>> response) {
//                songs = (ArrayList<Song>) response.body();
//
//                filter(query);
//
//            }
//
//            @Override
//            public void onFailure(@NonNull Call<List<Song>> call, @NonNull Throwable t) {
//                Log.d(TAG, "Fail get data due to: " + t.getMessage());
//                myProgress.dismiss();
//
//            }
//        });
//    }

//    public void filter(String query) {
//        myProgress = new MyProgress(getContext());
//        myProgress.show();
//        String lowerQuery = query.toLowerCase();
//        songsFilter.clear();
//        if (!lowerQuery.equals("")) {
//            for (int i = 0; i < songs.size(); i++) {
//                if ((songs.get(i)).getTitle().toLowerCase().contains(lowerQuery)) {
//                    songsFilter.add(songs.get(i));
//                }
//            }
//        }
//        if (songsFilter.isEmpty()) {
//            Log.d(TAG, "not data query");
//            noDataTv.setVisibility(View.VISIBLE);
//        } else {
//            noDataTv.setVisibility(View.GONE);
//            Log.d(TAG, "query :" + lowerQuery);
//        }
//        myProgress.dismiss();
//        songAdapter = new SongAdapter(songsFilter, getActivity());
//        recyclerView.setAdapter(songAdapter);
//        songAdapter.notifyDataSetChanged();
//    }
}