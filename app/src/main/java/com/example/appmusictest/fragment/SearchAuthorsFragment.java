package com.example.appmusictest.fragment;

import static android.util.Log.i;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.appmusictest.R;
import com.example.appmusictest.adapter.AuthorAdapter;
import com.example.appmusictest.dialog.MyProgress;
import com.example.appmusictest.model.Author;
import com.example.appmusictest.model.Playlist;
import com.example.appmusictest.service.ApiService;
import com.example.appmusictest.service.DataService;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchAuthorsFragment extends Fragment {

    private ArrayList<Author> authors ;
    private AuthorAdapter authorAdapter;
    private RecyclerView recyclerView;
    private TextView noDataTv;
    private static final String TAG = "AuthorsFragment";
    private Context context;

    public void setAuthors(ArrayList<Author> authors) {
        this.authors = authors;
        if (authorAdapter != null) {
            updateUi();
        }
    }
    public int getSize() {
        if (authors == null) authors = new ArrayList<>();
        return authors.size();
    }
    @Nullable
    @Override
    public Context getContext() {
        return context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_authors, container, false);
        recyclerView = view.findViewById(R.id.authorRv);
        noDataTv = view.findViewById(R.id.noDataTv);

        setViewData();
        return view;
    }

    private void setViewData() {
        if (authors == null) {
            authors = new ArrayList<>();
        }
        updateUi();

    }

    private void updateUi() {
        authorAdapter = new AuthorAdapter(authors, getActivity());
        recyclerView.setAdapter(authorAdapter);
        if (authors.size() != 0) {
            noDataTv.setVisibility(View.GONE);
        } else {
            noDataTv.setVisibility(View.VISIBLE);
        }
    }


}