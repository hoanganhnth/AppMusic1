package com.example.appmusictest.fragment;

import static com.example.appmusictest.activity.MusicPlayerActivity.nowPlayingId;
import static com.example.appmusictest.activity.MusicPlayerActivity.songPosition;
import static com.example.appmusictest.activity.MusicPlayerActivity.songs;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.appmusictest.R;
import com.example.appmusictest.activity.MusicPlayerActivity;

import de.hdodenhof.circleimageview.CircleImageView;


public class NowPlayingFragment extends Fragment {


    private TextView nameSongTv;
    private TextView authorSongTv;
    private CircleImageView circleImageView;
    private ImageButton pausePlIb,nextPlIb;
    private BroadcastReceiver broadcastReceiver;
    IntentFilter intentFilter;
    private static final String TAG = "Now_Playing_Fragment";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_now_playing, container, false);
        initView(view);
        view.setVisibility(View.INVISIBLE);

        pausePlIb.setOnClickListener( v -> {
            if (nowPlayingId.equals(songs.get(songPosition).getId())) {
                if (MusicPlayerActivity.musicPlayerService.isPlaying()) {
                    MusicPlayerActivity.musicPlayerService.pauseMusic();
                    pausePlIb.setImageResource(R.drawable.ic_play);
                } else {
                    MusicPlayerActivity.musicPlayerService.resumeMusic();
                    pausePlIb.setImageResource(R.drawable.ic_pause_gray);
                }
            }
        });

        nextPlIb.setOnClickListener(v -> {
            MusicPlayerActivity.musicPlayerService.nextSong(true);
            setViewData();
        });

        view.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), MusicPlayerActivity.class);
            intent.putExtra("index", songPosition);
            intent.putExtra("class", "NowPlaying");
            startActivity(intent);
        });
        Log.d(TAG, "createView");
        return view;


    }

    private void initView(View view) {
        nameSongTv = view.findViewById(R.id.nameSongTv);
        authorSongTv = view.findViewById(R.id.authorSongTv);
        pausePlIb = view.findViewById(R.id.pausePlIb);
        nextPlIb = view.findViewById(R.id.nextPlIb);
        circleImageView = view.findViewById(R.id.imgPlayPlCiv);
        nameSongTv.setSelected(true);
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "create");
    }

    @Override
    public void onResume() {
        super.onResume();
        if (MusicPlayerActivity.musicPlayerService != null && getView() != null) {
            initView(getView());
            getView().setVisibility(View.VISIBLE);
            setViewData();
            if (MusicPlayerActivity.musicPlayerService.isPlaying()) {
                pausePlIb.setImageResource(R.drawable.ic_pause_gray);
            } else {
                pausePlIb.setImageResource(R.drawable.ic_play);
            }

            broadcastReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    if (intent.getAction().equals("music_control")) {
                        String action = intent.getStringExtra("action");

                        switch (action) {
                            case "next":
                                setViewData();
                                break;
                            case "play":
                            case "resume":
                                pausePlIb.setImageResource(R.drawable.ic_pause_gray);
                                break;
                            case "pause":
                                pausePlIb.setImageResource(R.drawable.ic_play);
                                break;
                        }
                    }
                }
            };
            intentFilter = new IntentFilter("music_control");
            LocalBroadcastManager.getInstance(requireContext()).registerReceiver(broadcastReceiver, intentFilter);
            Log.d(TAG, "show fragment");
        }
        Log.d(TAG, "resume");
    }

    public void setViewData() {
        if (getView() != null) {
            nameSongTv.setText(songs.get(songPosition).getTitle());
            authorSongTv.setText(songs.get(songPosition).getNameAuthor());
            Glide.with(getView())
                    .load(songs.get(songPosition).getArtUrl())
                    .into(circleImageView);
        }
        Log.d(TAG, "setView");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(requireContext()).unregisterReceiver(broadcastReceiver);
    }
}