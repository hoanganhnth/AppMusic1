package com.example.appmusictest.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;

import com.example.appmusictest.R;
import com.example.appmusictest.adapter.AuthorAdapter;
import com.example.appmusictest.dialog.MyProgress;
import com.example.appmusictest.model.Album;
import com.example.appmusictest.model.Author;

import java.util.ArrayList;
import java.util.Iterator;

public class FavoriteAuthorActivity extends AppCompatActivity {

    private static ArrayList<Author> favAuthor;
    private RecyclerView authorFavRv;
    private ImageButton backIb;
    private AuthorAdapter adapter;
    private final static String TAG = "Favorite_Album_Ac";
    private MyProgress myProgress;


    public static ArrayList<Author> getFavAuthor() {
        return favAuthor;
    }

    public static void setFavAuthor(ArrayList<Author> favAuthor) {
        FavoriteAuthorActivity.favAuthor = favAuthor;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite_author);
        myProgress = new MyProgress(this);
        myProgress.show();
        initView();
//        getData();
        setViewData();
    }

    private void getData() {
        if (getIntent() != null) {
            favAuthor = getIntent().getParcelableArrayListExtra("favAuthors");
            if (favAuthor == null) favAuthor = new ArrayList<>();
        }
    }

    private void setViewData() {
        adapter = new AuthorAdapter(favAuthor, this);
        authorFavRv.setAdapter(adapter);
        backIb.setOnClickListener(v -> onBackPressed());
        myProgress.dismiss();
    }

    private void initView() {
        authorFavRv = findViewById(R.id.authorFavRv);
        backIb = findViewById(R.id.backIb);
    }

    public static void addAuthor(Author author) {
        favAuthor.add(author);
        Log.d(TAG, "added to favorite");
    }
    public static void removeAuthor(Author author) {
        Iterator<Author> iterator = favAuthor.iterator();
        while (iterator.hasNext()) {
            Author obj = iterator.next();
            if (obj.getId().equals(author.getId())) {
                iterator.remove();
                break;
            }
        }
        Log.d(TAG, "remove from favorite");
    }
    public static boolean isInFav(Author author) {
        for (Author author1 : favAuthor) {
            if (author.getId().equals(author1.getId())) {
                return true;
            }
        }
        return false;
    }

    public static int getSize() {
        if (favAuthor == null) favAuthor = new ArrayList<>();
        return favAuthor.size();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateUi();
    }

    private void updateUi() {
        adapter.notifyDataSetChanged();
    }
}