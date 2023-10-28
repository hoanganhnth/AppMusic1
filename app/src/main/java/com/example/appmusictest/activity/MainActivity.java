package com.example.appmusictest.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Lifecycle;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;

import com.example.appmusictest.R;
import com.example.appmusictest.fragment.AuthorsFragment;
import com.example.appmusictest.fragment.NowPlayingFragment;
import com.example.appmusictest.fragment.PlaylistFragment;
import com.example.appmusictest.fragment.SongsFragment;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class MainActivity extends AppCompatActivity {

    private TabLayout tabLayout;
    private ViewPager2 viewPager2;
    private ViewPaperMainFragmentAdapter adapter;

    private String[] labelFragment = new String[]{"Playlist", "Author", "Song"};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
        new TabLayoutMediator(tabLayout, viewPager2, (tab, position) -> {
            tab.setText(labelFragment[position]);
        }).attach();
        viewPager2.setCurrentItem(0, false);
    }

    private void init() {
        tabLayout = findViewById(R.id.tabLayout);
        viewPager2 = findViewById(R.id.viewPaper);
        adapter = new ViewPaperMainFragmentAdapter(this);
        viewPager2.setAdapter(adapter);
    }

    private class ViewPaperMainFragmentAdapter extends FragmentStateAdapter {

        public ViewPaperMainFragmentAdapter(@NonNull FragmentActivity fragmentActivity) {
            super(fragmentActivity);
        }

        public ViewPaperMainFragmentAdapter(@NonNull Fragment fragment) {
            super(fragment);
        }

        public ViewPaperMainFragmentAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
            super(fragmentManager, lifecycle);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            switch (position) {
                case 0:
                    return new PlaylistFragment();
                case 1:
                    return new AuthorsFragment();
                case 3:
                    return new SongsFragment();
            }
            return new SongsFragment();
        }

        @Override
        public int getItemCount() {
            return labelFragment.length;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("Main", "main destroy");
        Intent intent = new Intent("your.package.name.MainActivityDestroyed");
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
        }

    @Override
    protected void onResume() {
        super.onResume();
        if (MusicPlayerActivity.musicPlayerService != null) {
            MusicPlayerActivity.musicPlayerService.destroyMain = false;
            Log.d("Main", "main resume");
            if (MusicPlayerActivity.musicPlayerService.isPlaying()) {
                MusicPlayerActivity.musicPlayerService.showNotification(R.drawable.ic_pause_gray);
            } else {
                MusicPlayerActivity.musicPlayerService.showNotification(R.drawable.ic_play);
            }
        }
    }
}