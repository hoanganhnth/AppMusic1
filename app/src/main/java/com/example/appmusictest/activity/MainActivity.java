package com.example.appmusictest.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Bundle;

import com.example.appmusictest.R;
import com.example.appmusictest.fragment.AuthorsFragment;
import com.example.appmusictest.fragment.PlaylistFragment;
import com.example.appmusictest.fragment.SongsFragment;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class MainActivity extends AppCompatActivity {

    private TabLayout tabLayout;
    private ViewPager2 viewPager2;

    private ViewPaperMainFragmentAdapter adapter;

    private String[] labelFragment = new String[]{"Songs", "Playlist", "Author"};
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
                    return new SongsFragment();
                case 1:
                    return new PlaylistFragment();
                case 2:
                    return new AuthorsFragment();
            }
            return new SongsFragment();
        }

        @Override
        public int getItemCount() {
            return labelFragment.length;
        }
    }
}