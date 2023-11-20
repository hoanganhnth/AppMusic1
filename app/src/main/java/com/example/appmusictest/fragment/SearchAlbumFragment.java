package com.example.appmusictest.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.appmusictest.MyApplication;
import com.example.appmusictest.R;
import com.example.appmusictest.adapter.AlbumAdapter;
import com.example.appmusictest.adapter.AuthorAdapter;
import com.example.appmusictest.adapter.PlaylistAdapter;
import com.example.appmusictest.dialog.MyProgress;
import com.example.appmusictest.model.Album;
import com.example.appmusictest.model.Author;

import java.util.ArrayList;

public class SearchAlbumFragment extends Fragment {

    private ArrayList<Album> albums = new ArrayList<>();
    private AlbumAdapter albumAdapter;
    private RecyclerView recyclerView;
    private TextView noDataTv;
    private static final String TAG = "SearchAlbumFragment";

    public void setAlbums(ArrayList<Album> albums) {
        this.albums = albums;
        if (albumAdapter != null) {
            albumAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_album_search, container, false);
        noDataTv = view.findViewById(R.id.noDataTv);
        recyclerView = view.findViewById(R.id.albumRv);

//        getData();
        setViewData();
        return view;
    }

    private void setViewData() {
        albumAdapter = new AlbumAdapter(albums, getActivity());
        recyclerView.setAdapter(albumAdapter);
        if (albums.size() == 0) {
            noDataTv.setVisibility(View.VISIBLE);
        }
    }

    private void getData() {
        Bundle bundle = getArguments();
        if (bundle != null) {
            String query = bundle.getString("query");
            initData();

//            filter(query);

        }
    }

//    public void filter(String query) {
//        myProgress = new MyProgress(getContext());
//        myProgress.show();
//        String lowerQuery = query.toLowerCase();
//        albumFilter.clear();
//        if (!lowerQuery.equals("")) {
//            for (int i = 0; i < albums.size(); i++) {
//                if ((albums.get(i)).getTitle().toLowerCase().contains(lowerQuery)) {
//                    albumFilter.add(albums.get(i));
//                }
//            }
//        }
//        if (albumFilter.isEmpty()) {
//            Log.d(TAG, "not data query");
//            noDataTv.setVisibility(View.VISIBLE);
//        } else {
//            noDataTv.setVisibility(View.GONE);
//            Log.d(TAG, "query :" + lowerQuery);
//        }
//        myProgress.dismiss();
//        albumAdapter = new PlaylistAlbumAdapter<>(albumFilter, getActivity(), MyApplication.TYPE_ALBUM);
//        recyclerView.setAdapter(albumAdapter);
//        albumAdapter.notifyDataSetChanged();
//    }

    private void initData() {
        albums = new ArrayList<>();
//        albumFilter = new ArrayList<>();
        albums.add(new Album("1","test1", "https://imgmusic.com/uploads/album/cover/2/ZPP_Pop_Rock_Inspirational_vol1_highrez.jpg"));
        albums.add(new Album("2","test2", "https://imgmusic.com/uploads/album/cover/197/poprockinsp_vol2_ZPP067.jpg"));
        albums.add(new Album("3","test3", "https://imgmusic.com/uploads/album/cover/78/IndieInspirational_Vol2.jpg"));
        albums.add(new Album("4","test4", "https://imgmusic.com/uploads/album/cover/66/EmotionalBuilds_Vol1-update.jpg"));
        albums.add(new Album("5","test5", "https://imgmusic.com/uploads/album/cover/11/IndieReflections_Vol1_highrez.jpg"));
    }
}