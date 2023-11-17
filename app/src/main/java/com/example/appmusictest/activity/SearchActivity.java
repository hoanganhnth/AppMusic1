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
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.appmusictest.R;
import com.example.appmusictest.adapter.PlaylistAlbumAdapter;
import com.example.appmusictest.dialog.MyProgress;
import com.example.appmusictest.fragment.SearchAlbumFragment;
import com.example.appmusictest.fragment.SearchAuthorsFragment;
import com.example.appmusictest.fragment.SearchPlaylistFragment;
import com.example.appmusictest.fragment.SearchSongFragment;
import com.example.appmusictest.model.Album;
import com.example.appmusictest.model.Author;
import com.example.appmusictest.model.Playlist;
import com.example.appmusictest.model.Song;
import com.example.appmusictest.service.ApiService;
import com.example.appmusictest.service.DataService;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchActivity extends AppCompatActivity {

    private TabLayout tabLayout;
    private ViewPager2 viewPager2;
    private ImageButton backIb;
    private EditText searchEt;
    private static final String TAG = "Search_activity";
    private final String[] labelFragment = new String[]{"Playlist", "Author", "Album", "Song"};
    private ArrayList<Fragment> fragmentArrayList;
    private SearchPlaylistFragment searchPlaylistFragment;
    private ArrayList<Song> songs = new ArrayList<>();
    private ArrayList<Playlist> playlists = new ArrayList<>();
    private ArrayList<Author> authors = new ArrayList<>();
    private ArrayList<Album> albums = new ArrayList<>();
    private SearchAuthorsFragment searchAuthorsFragment;
    private SearchAlbumFragment searchAlbumFragment;
    private SearchSongFragment searchSongFragment;
    private String query;
    private MyProgress myProgress;
    private int numberSuccess = 0;

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

        searchPlaylistFragment = new SearchPlaylistFragment();
        searchAuthorsFragment = new SearchAuthorsFragment();
        searchAlbumFragment = new SearchAlbumFragment();
        searchSongFragment = new SearchSongFragment();
        getDataServer(query);
//        Bundle bundle = new Bundle();
//        bundle.putString("query", query);
//        searchPlaylistFragment.setArguments(bundle);
//        searchAuthorsFragment.setArguments(bundle);
//        searchAlbumFragment.setArguments(bundle);
//        searchSongFragment.setArguments(bundle);

        fragmentArrayList.add(searchPlaylistFragment);
        fragmentArrayList.add(searchAuthorsFragment);
        fragmentArrayList.add(searchAlbumFragment);
        fragmentArrayList.add(searchSongFragment);

        ViewPaperMainFragmentAdapter adapter = new ViewPaperMainFragmentAdapter(this,fragmentArrayList);
        viewPager2.setAdapter(adapter);

        new TabLayoutMediator(tabLayout, viewPager2, (tab, position)
                -> tab.setText(labelFragment[position])).attach();
        viewPager2.setCurrentItem(1, false);

        searchEt.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH || event.getAction() == KeyEvent.KEYCODE_ENTER) {
                    String query = searchEt.getText().toString().trim();
                    getDataServer(query);
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

    private void getDataServer(String query) {
        myProgress = new MyProgress(this);
        myProgress.show();
        getDataSearchSong(query);
        getDataSearchPlaylist(query);
        getDataSearchAlbum(query);
        getDataSearchAuthor(query);
    }

    private void getDataSearchAuthor(String query) {
        authors.clear();
        authors.add(new Author("1", "author1", "https://upload.wikimedia.org/wikipedia/vi/3/32/S%C6%A1n_T%C3%B9ng_M-TP_-_C%C3%B3_ch%E1%BA%AFc_y%C3%AAu_l%C3%A0_%C4%91%C3%A2y.jpg"));
        authors.add(new Author("2", "author2", "https://cdnphoto.dantri.com.vn/ecdPkKw4WCg-NR0Zi2shwRYyUlo=/thumb_w/1020/2022/11/10/micheal-jackson-1668044313441.jpg"));
        authors.add(new Author("3", "author3", "https://cdn.tuoitre.vn/thumb_w/1100/471584752817336320/2023/9/5/jack-messi-1-16938973854241419756685.jpg"));
        authors.add(new Author("4", "author4", "https://images2.thanhnien.vn/528068263637045248/2023/8/30/1-1693385246169701996465.jpg"));
        authors.add(new Author("5", "author5", "https://suckhoedoisong.qltns.mediacdn.vn/thumb_w/640/324455921873985536/2023/9/6/dam-vinh-hung-16939936357631285203697.png"));
        searchAuthorsFragment.setAuthors(authors);
        checkSuccessAll();

    }

    private void getDataSearchAlbum(String query) {
        albums.clear();
        albums.add(new Album("1","test1", "https://imgmusic.com/uploads/album/cover/2/ZPP_Pop_Rock_Inspirational_vol1_highrez.jpg"));
        albums.add(new Album("2","test2", "https://imgmusic.com/uploads/album/cover/197/poprockinsp_vol2_ZPP067.jpg"));
        albums.add(new Album("3","test3", "https://imgmusic.com/uploads/album/cover/78/IndieInspirational_Vol2.jpg"));
        albums.add(new Album("4","test4", "https://imgmusic.com/uploads/album/cover/66/EmotionalBuilds_Vol1-update.jpg"));
        albums.add(new Album("5","test5", "https://imgmusic.com/uploads/album/cover/11/IndieReflections_Vol1_highrez.jpg"));

        searchAlbumFragment.setAlbums(albums);
        checkSuccessAll();
    }

    private void getDataSearchPlaylist(String query) {
        DataService dataService = ApiService.getService();
        Call<List<Playlist>> callback = dataService.getPlaylistCurrentDay();
        callback.enqueue(new Callback<List<Playlist>>() {
            @Override
            public void onResponse(@NonNull Call<List<Playlist>> call, @NonNull Response<List<Playlist>> response) {
                playlists = (ArrayList<Playlist>) response.body();
                searchPlaylistFragment.setPlaylists(playlists);
                checkSuccessAll();
            }

            @Override
            public void onFailure(@NonNull Call<List<Playlist>> call, @NonNull Throwable t) {
                Log.d(TAG, "Fail get data due to: " + t.getMessage() );
                myProgress.dismiss();

            }
        });

    }

    private void getDataSearchSong(String query) {
        DataService dataService = ApiService.getService();
        Call<List<Song>> callback = dataService.getSongByPlaylist("1");
        callback.enqueue(new Callback<List<Song>>() {
            @Override
            public void onResponse(@NonNull Call<List<Song>> call, @NonNull Response<List<Song>> response) {
                songs = (ArrayList<Song>) response.body();
                searchSongFragment.setSongs(songs);
                checkSuccessAll();

            }

            @Override
            public void onFailure(@NonNull Call<List<Song>> call, @NonNull Throwable t) {
                Log.d(TAG, "Fail get data due to: " + t.getMessage());
                myProgress.dismiss();
            }
        });
    }

    private void checkSuccessAll() {
        numberSuccess ++;
        if (numberSuccess == 4) {
            myProgress.dismiss();
            numberSuccess = 0;
        }
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