package com.example.appmusictest.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.example.appmusictest.R;
import com.example.appmusictest.dialog.MyProgress;


public class InfoSongFragment extends Fragment {

    private MyProgress myProgress;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_infor_song, container, false);
        myProgress = new MyProgress(view.getContext());
        myProgress.show();
        myProgress.dismiss();
        return view;
    }
}