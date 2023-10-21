package com.example.appmusictest.fragment;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;

import com.bumptech.glide.Glide;
import com.example.appmusictest.R;

import de.hdodenhof.circleimageview.CircleImageView;

public class DiskFragment extends Fragment {

    public static CircleImageView circleImageView;
    ObjectAnimator objectAnimator;
    private String artUrl;

    public DiskFragment() {
    }

    public DiskFragment(String artUrl) {
        this.artUrl = artUrl;

    }

    public String getArtUrl() {
        return artUrl;
    }

    public void setArtUrl(String artUrl) {
        this.artUrl = artUrl;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_disk, container, false);
        circleImageView = view.findViewById(R.id.imgPlayCiv);
        objectAnimator = ObjectAnimator.ofFloat(circleImageView, "rotation", 0f,360f);
        objectAnimator.setDuration(10000);
        objectAnimator.setRepeatCount(ValueAnimator.INFINITE);
        objectAnimator.setRepeatMode(ValueAnimator.RESTART);
        objectAnimator.setInterpolator(new LinearInterpolator());
        circleImageView = view.findViewById(R.id.imgPlayCiv);
//        setImageView(artUrl);
        Glide.with(view)
                .load(artUrl)
                .into(circleImageView);
        objectAnimator.start();
        return view;
    }

    public void updateImage() {
        if (circleImageView != null) {
            Glide.with(this)
                    .load(artUrl)
                    .into(circleImageView);
        }
    }
}