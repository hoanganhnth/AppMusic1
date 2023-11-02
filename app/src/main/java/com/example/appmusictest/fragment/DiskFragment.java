package com.example.appmusictest.fragment;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;

import com.bumptech.glide.Glide;
import com.example.appmusictest.R;
import com.example.appmusictest.activity.MusicPlayerActivity;

import de.hdodenhof.circleimageview.CircleImageView;

public class DiskFragment extends Fragment {

    private CircleImageView circleImageView;
    ObjectAnimator objectAnimator;
    private String artUrl;
    private BroadcastReceiver broadcastReceiver;
    private IntentFilter intentFilter;

    private static final String TAG = "Disk_fragment";
    public DiskFragment() {
    }

    public DiskFragment(String artUrl) {
        this.artUrl = artUrl;

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
        Glide.with(view)
                .load(artUrl)
                .placeholder(R.mipmap.ic_launcher_round)
                .into(circleImageView);
        objectAnimator.start();
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals("music_control")) {
                    String action = intent.getStringExtra("action");
                    switch (action) {
                        case "resume":
                            objectAnimator.resume();
                            Log.d(TAG, "resume");
                            break;
                        case "pause":
                            objectAnimator.pause();
                            Log.d(TAG, "pause");
                            break;
                        case "play":
                            objectAnimator.resume();
                            Log.d(TAG, "play");
                            break;
                    }
                }
            }
        };
        intentFilter = new IntentFilter("music_control");
        LocalBroadcastManager.getInstance(requireContext()).registerReceiver(broadcastReceiver, intentFilter);

        Log.d(TAG, "On create view");
        return view;
    }

    public void updateImage() {
        if (circleImageView != null && getView() != null) {
            Glide.with(getView())
                    .load(artUrl)
                    .into(circleImageView);
            Log.d(TAG, "show img ");
        } else {
            if (circleImageView == null) Log.d(TAG, "circleImg is null");
            if (getView() == null) Log.d(TAG, "get view is null");
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(requireContext()).unregisterReceiver(broadcastReceiver);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (MusicPlayerActivity.musicPlayerService == null) {
            objectAnimator.pause();
        } else {
            if (MusicPlayerActivity.checkSong() && !MusicPlayerActivity.musicPlayerService.isPlaying()) {
                objectAnimator.pause();
            }
        }
    }
}