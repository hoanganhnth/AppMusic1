package com.example.appmusictest.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.appmusictest.R;
import com.example.appmusictest.activity.AuthorDetailActivity;
import com.example.appmusictest.activity.MusicPlayerActivity;
import com.example.appmusictest.dialog.MyProgress;
import com.example.appmusictest.model.Author;
import com.example.appmusictest.model.Genre;
import com.example.appmusictest.model.Song;
import com.example.appmusictest.model.api.AuthorsResponse;
import com.example.appmusictest.model.api.GenreResponse;
import com.example.appmusictest.service.ApiService;
import com.example.appmusictest.service.DataService;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class InfoSongFragment extends Fragment {

    private MyProgress myProgress;
    private Song song;
    private ArrayList<Author> authors;
//    private Genre genres;
    private String genres;

    private static final String TAG = "InfoSongFragment";
    private TextView nameSongTitle,albumSongTv, authorSongTv,categorySongTv;
    private View view;

    public void setSong(Song song) {
        this.song = song;
    }
    public void setAuthor(ArrayList<Author> authors) {
        this.authors = authors;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_infor_song, container, false);
        myProgress = new MyProgress(view.getContext());
        myProgress.show();
        nameSongTitle = view.findViewById(R.id.nameSongTitle);
        albumSongTv = view.findViewById(R.id.albumSongTv);
        authorSongTv = view.findViewById(R.id.authorSongTv);
        categorySongTv = view.findViewById(R.id.categorySongTv);

        getDataService();
        nameSongTitle.setText(song.getTitle());
        return view;
    }

    private void getDataService() {
        getDataAuthor();
        getDataGenre();
    }

    private void getDataGenre() {
        DataService dataService = ApiService.getService();
        Call<GenreResponse> callback = dataService.getGenreSong(song.getId());
        callback.enqueue(new Callback<GenreResponse>() {
            @Override
            public void onResponse(@NonNull Call<GenreResponse> call, @NonNull Response<GenreResponse> response) {
                if (response.code() == 200) {
                    if (response.body().getErrCode().equals("0")) {
                        genres = response.body().getGenres();
                        StringBuilder names = new StringBuilder();
//                        for (Genre genre : genres) {
//                            names.append(genre.getName()).append(", ");
//                        }
//                        if (names.length() > 0) {
//                            names.delete(names.length() - 2, names.length());
//                        }
                        Log.d(TAG, "genre is " + genres);
                        categorySongTv.setText(genres);
                    }
//                    Toast.makeText(getContext(), response.body().getErrCode(), Toast.LENGTH_SHORT).show();
                }
                myProgress.dismiss();
            }

            @Override
            public void onFailure(@NonNull Call<GenreResponse> call, @NonNull Throwable t) {
                Log.d(TAG, "Fail to get data from server due to:" + t.getMessage() );
                myProgress.dismiss();
            }
        });
    }

    private void getDataAuthor() {

        DataService dataService = ApiService.getService();
        Call<AuthorsResponse> callback = dataService.getSongAuthor(song.getId());
        callback.enqueue(new Callback<AuthorsResponse>() {
            @Override
            public void onResponse(@NonNull Call<AuthorsResponse> call, @NonNull Response<AuthorsResponse> response) {
                if (response.code() == 200) {
                    if (response.body().getErrCode().equals("0")) {
                        authors = response.body().getAuthors();
                        StringBuilder names = new StringBuilder();
                        for (Author author : authors) {
                            names.append(author.getName()).append(", ");
                        }
                        if (names.length() > 0) {
                            names.delete(names.length() - 2, names.length());
                        }
                        authorSongTv.setText(names.toString());
                    }
                }
                myProgress.dismiss();
            }

            @Override
            public void onFailure(@NonNull Call<AuthorsResponse> call, @NonNull Throwable t) {
                Log.d(TAG, "Fail to get data from server due to:" + t.getMessage() );
                myProgress.dismiss();
            }
        });
    }
}