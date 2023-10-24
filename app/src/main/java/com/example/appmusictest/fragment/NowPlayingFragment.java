package com.example.appmusictest.fragment;

import static com.example.appmusictest.activity.MusicPlayerActivity.nowPlayingId;
import static com.example.appmusictest.activity.MusicPlayerActivity.songPosition;
import static com.example.appmusictest.activity.MusicPlayerActivity.songs;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
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
    private boolean isReceiverRegistered = false;
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
        Log.d("Now playing fragment", "createview");
        return view;


    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("Now playing fragment", "create");
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

            if (!isReceiverRegistered) {
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
                isReceiverRegistered = true;
            }

        }
        Log.d("Now playing fragment", "resume");
//        Log.d("Now playing fragment", nameSongTv.getText().toString());
    }


    public void setViewData() {
        if (getView() != null) {
            nameSongTv.setText(songs.get(songPosition).getTitle());
            authorSongTv.setText(songs.get(songPosition).getNameAuthor());
            Glide.with(getView())
                    .load(songs.get(songPosition).getArtUrl())
                    .placeholder(R.mipmap.ic_launcher_round)
                    .into(circleImageView);

             }
    }
}