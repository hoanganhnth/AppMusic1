package com.example.appmusictest.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.appmusictest.R;
import com.example.appmusictest.model.Author;

public class AuthorDetailActivity extends AppCompatActivity {

    private TextView nameAuthorTv, followTv, buttonShuffleTv;
    private ImageButton backIb;
    private ImageView imgBgIv;
    private Author author;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_author_detail);
        initView();
        getDataIntent();
        setViewData();
    }

    private void setViewData() {
        nameAuthorTv.setText(author.getName());
        backIb.setOnClickListener(v -> onBackPressed());
        Glide.with(this)
                .load(author.getUrlArt())
                .into(imgBgIv);
    }

    private void initView() {
        nameAuthorTv = findViewById(R.id.nameAuthorTv);
        followTv = findViewById(R.id.followTv);
        buttonShuffleTv = findViewById(R.id.buttonShuffleTv);
        backIb = findViewById(R.id.backIb);
        imgBgIv = findViewById(R.id.imgBgIv);
    }

    private void getDataIntent() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            author = bundle.getParcelable("author");
        }
    }
}