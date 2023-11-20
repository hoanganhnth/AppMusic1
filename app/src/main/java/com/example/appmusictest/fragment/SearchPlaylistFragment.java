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

    private ArrayList<Playlist> playlists = new ArrayList<>();

    private RecyclerView playlistRv;
    private static final String TAG = "Playlist_Fragment";
    private PlaylistAdapter playlistAdapter;
    public static TextView noDataTv;

    public void setPlaylists(ArrayList<Playlist> playlists) {
        this.playlists = playlists;
        if (playlistAdapter != null) {
            playlistAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_playlist, container, false);
        initView(view);
//        getData();
        setViewData();
        return view;
    }

    private void setViewData() {
        playlistAdapter = new PlaylistAdapter(playlists,getActivity());
        playlistRv.setAdapter(playlistAdapter);
        if (playlists.size() == 0) {
            noDataTv.setVisibility(View.VISIBLE);
        }
    }

    private void initView(View view) {
        playlistRv = view.findViewById(R.id.playlistRv);
        noDataTv = view.findViewById(R.id.noDataTv);

    }

//    private void getData() {
//        Bundle bundle = getArguments();
//        if (bundle != null) {
//            String query = bundle.getString("query");
//
//            DataService dataService = ApiService.getService();
//            Call<List<Playlist>> callback = dataService.getPlaylistCurrentDay();
//            callback.enqueue(new Callback<List<Playlist>>() {
//                @Override
//                public void onResponse(@NonNull Call<List<Playlist>> call, @NonNull Response<List<Playlist>> response) {
//                    playlists = (ArrayList<Playlist>) response.body();
//                    playlistAdapter = new PlaylistAlbumAdapter<>(playlists,getActivity(), 1);
//                    playlistAdapter.filter(query);
//                    playlistRv.setAdapter(playlistAdapter);
//                }
//
//                @Override
//                public void onFailure(@NonNull Call<List<Playlist>> call, @NonNull Throwable t) {
//                    Log.d(TAG, "Fail get data due to: " + t.getMessage() );
//
//                }
//            });
//        }
//    }
//    public void filter(String query) {
//        if (playlistAdapter != null) {
//            playlistAdapter.filter(query);
//        }
//    }
}