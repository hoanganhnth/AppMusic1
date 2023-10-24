package com.example.appmusictest.fragment;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.appmusictest.R;
import com.example.appmusictest.model.Song;
import com.example.appmusictest.adapter.SongAdapter;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;


public class SongsFragment extends Fragment {

    private RecyclerView songRv;
    private ArrayList<Song> arrayList;
    private FirebaseAuth firebaseAuth;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_songs, container, false);
        initData();


        songRv = view.findViewById(R.id.songsRv);
        songRv.setAdapter(new SongAdapter(arrayList));
        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    private void initData() {

        arrayList = new ArrayList<>();
        arrayList.add(new Song("","Bình yên những phút giây", "Sơn Tùng", "", "", "", "", ""));
        arrayList.add(new Song("","Chúng ta không thuộc về nhau", "Sơn Tùng", "","","", "", "" ));
        arrayList.add(new Song("","Hãy trao cho anh", "Sơn Tùng", "","", "", "", ""));
        arrayList.add(new Song("","Remember me", "Sơn Tùng", "","", "", "", ""));
        arrayList.add(new Song("","Remember me", "Sơn Tùng", "","", "", "", ""));
        arrayList.add(new Song("","Remember me", "Sơn Tùng", "","", "", "", ""));
        arrayList.add(new Song("","Remember me", "Sơn Tùng", "","", "", "", ""));
        arrayList.add(new Song("","Remember me", "Sơn Tùng", "","", "", "", ""));
        arrayList.add(new Song("","Remember me", "Sơn Tùng", "","", "", "", ""));
        arrayList.add(new Song("","Remember me", "Sơn Tùng", "","", "", "", ""));


    }
}