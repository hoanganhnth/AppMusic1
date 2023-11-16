package com.example.appmusictest.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.appmusictest.R;
import com.example.appmusictest.fragment.AuthorsFragment;
import com.example.appmusictest.fragment.PlaylistFragment;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;

public class SearchActivity extends AppCompatActivity {

    private TabLayout tabLayout;
    private ViewPager2 viewPager2;
    private ImageButton backIb;
    private EditText searchEt;
    private static final String TAG = "Search_activity";
    private final String[] labelFragment = new String[]{"Playlist", "Author"};
    private ArrayList<Fragment> fragmentArrayList;
    private PlaylistFragment playlistFragment;
    private AuthorsFragment authorsFragment;
    private String query;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        searchEt = findViewById(R.id.searchEt);
        tabLayout = findViewById(R.id.tabLayout);
        viewPager2 = findViewById(R.id.viewPaper);
        backIb = findViewById(R.id.backIb);


        query = getIntent().getStringExtra("query");
        searchEt.setText(query);

        fragmentArrayList = new ArrayList<>();

        playlistFragment = new PlaylistFragment();
        Bundle bundle = new Bundle();
        bundle.putString("query", query);
        playlistFragment.setArguments(bundle);

        authorsFragment = new AuthorsFragment();

        fragmentArrayList.add(playlistFragment);
        fragmentArrayList.add(authorsFragment);

        ViewPaperMainFragmentAdapter adapter = new ViewPaperMainFragmentAdapter(this,fragmentArrayList);
        viewPager2.setAdapter(adapter);

        new TabLayoutMediator(tabLayout, viewPager2, (tab, position)
                -> tab.setText(labelFragment[position])).attach();
        viewPager2.setCurrentItem(0, false);

        searchEt.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH || event.getAction() == KeyEvent.KEYCODE_ENTER) {
                    String query = searchEt.getText().toString().trim();
                    playlistFragment.filter(query);
//                    searchEt.clearFocus();
                    InputMethodManager in = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    in.hideSoftInputFromWindow(searchEt.getApplicationWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS); return true;
                }
                Log.d(TAG, "NOT enter");
                return false;
            }
        });
        backIb.setOnClickListener(V -> {
            onBackPressed();
        });

    }



    private class ViewPaperMainFragmentAdapter extends FragmentStateAdapter {

        private ArrayList<Fragment> arrayList;
        public ViewPaperMainFragmentAdapter(@NonNull FragmentActivity fragmentActivity, ArrayList<Fragment> fragmentArrayList) {
            super(fragmentActivity);
            arrayList = fragmentArrayList;
        }



        @NonNull
        @Override
        public Fragment createFragment(int position) {
            return arrayList.get(position);
        }

        @Override
        public int getItemCount() {
            return arrayList.size();
        }
    }


    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (v instanceof EditText) {
                Rect outRect = new Rect();
                v.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int) event.getRawX(), (int) event.getRawY())) {
                    v.clearFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        }
        return super.dispatchTouchEvent(event);
    }
}