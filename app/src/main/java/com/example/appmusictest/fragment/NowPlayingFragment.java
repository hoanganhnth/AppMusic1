package com.example.appmusictest.fragment;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.appmusictest.R;
import com.example.appmusictest.activity.MusicPlayerActivity;

import de.hdodenhof.circleimageview.CircleImageView;


public class NowPlayingFragment extends Fragment {


    public static TextView nameSongTv;
    public static TextView authorSongTv;
    private DiskFragment imgPlFragment;
    public CircleImageView circleImageView;
    private ImageButton pausePlIb,nextPlIb;
    ObjectAnimator objectAnimator;
    private BroadcastReceiver broadcastReceiver;
    IntentFilter intentFilter;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_now_playing, container, false);
        nameSongTv = view.findViewById(R.id.nameSongTv);
        authorSongTv = view.findViewById(R.id.authorSongTv);
        pausePlIb = view.findViewById(R.id.pausePlIb);
        nextPlIb = view.findViewById(R.id.nextPlIb);
        circleImageView = view.findViewById(R.id.imgPlayPlCiv);
        nameSongTv.setSelected(true);
//        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
//        transaction.replace(R.id.)
////        imgPlFragment = new DiskFragment();
//        objectAnimator = ObjectAnimator.ofFloat(circleImageView, "rotation", 0f,360f);
//        objectAnimator.setDuration(10000);
//        objectAnimator.setRepeatCount(ValueAnimator.INFINITE);
//        objectAnimator.setRepeatMode(ValueAnimator.RESTART);
//        objectAnimator.setInterpolator(new LinearInterpolator());
//        objectAnimator.start();
        view.setVisibility(View.INVISIBLE);

        pausePlIb.setOnClickListener( v -> {
            if (MusicPlayerActivity.musicPlayerService.isPlaying()) {
                MusicPlayerActivity.musicPlayerService.pauseMusic();
                pausePlIb.setImageResource(R.drawable.ic_play);
            } else {
                MusicPlayerActivity.musicPlayerService.resumeMusic();
                pausePlIb.setImageResource(R.drawable.ic_pause_gray);
            }
        });

        nextPlIb.setOnClickListener(v -> {
            MusicPlayerActivity.musicPlayerService.nextSong(true);
            setViewData();
        });

        view.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), MusicPlayerActivity.class);
            intent.putExtra("index", MusicPlayerActivity.songPosition);
            intent.putExtra("class", "NowPlaying");
            startActivity(intent);
        });

        return view;

    }


    @Override
    public void onResume() {
        super.onResume();
        if (MusicPlayerActivity.musicPlayerService != null) {
            this.getView().setVisibility(View.VISIBLE);
            setViewData();
            if (MusicPlayerActivity.musicPlayerService.isPlaying()) {
                pausePlIb.setImageResource(R.drawable.ic_pause_gray);
            } else {
                pausePlIb.setImageResource(R.drawable.ic_play);
            }
//            MusicPlayerActivity.musicPlayerService.mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
//                @Override
//                public void onCompletion(MediaPlayer mp) {
//                    setViewData();
//                }
//            });
            broadcastReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    if (intent.getAction().equals("music_control")) {
                        String action = intent.getStringExtra("action");

                        if (action.equals("next")) {
                            setViewData();
                        } else if (action.equals("play")) {
                            pausePlIb.setImageResource(R.drawable.ic_pause_gray);
                        }
                    }
                }
            };
            intentFilter = new IntentFilter("music_control");
            LocalBroadcastManager.getInstance(requireContext()).registerReceiver(broadcastReceiver, intentFilter);

        }
    }


    public void setViewData() {
        if (getView() != null) {
            Glide.with(getView())
                    .load(MusicPlayerActivity.songs.get(MusicPlayerActivity.songPosition).getArtUrl())
                    .placeholder(R.mipmap.ic_launcher_round)
                    .into(circleImageView);
            nameSongTv.setText(MusicPlayerActivity.songs.get(MusicPlayerActivity.songPosition).getTitle());
            authorSongTv.setText(MusicPlayerActivity.songs.get(MusicPlayerActivity.songPosition).getNameAuthor());
        }

//        imgPlFragment.setArtUrl(MusicPlayerActivity.songs.get(MusicPlayerActivity.songPosition).getArtUrl());


    }



}