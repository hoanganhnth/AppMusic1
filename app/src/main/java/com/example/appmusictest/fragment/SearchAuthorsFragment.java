package com.example.appmusictest.fragment;

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

    private ArrayList<Author> authors = new ArrayList<>();
//    private ArrayList<Author> authorsFilter = new ArrayList<>();
    private AuthorAdapter authorAdapter;
    private RecyclerView recyclerView;
    private TextView noDataTv;
    private static final String TAG = "AuthorsFragment";
    private Context context;

    public void setAuthors(ArrayList<Author> authors) {
        this.authors = authors;
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
//        getData();
        setViewData();
        return view;
    }

    private void setViewData() {
        authorAdapter = new AuthorAdapter(authors, getActivity());
        recyclerView.setAdapter(authorAdapter);
        if (authors.size() == 0) {
            noDataTv.setVisibility(View.VISIBLE);
        }
    }

//    private void getData() {
//        Bundle bundle = getArguments();
//        if (bundle != null) {
//            String query = bundle.getString("query");
//            initData();
//
//            filter(query,getContext());
////            getDataServer(query)
//
//        }
//    }

//    private void getDataServer(String query) {
//        DataService dataService = ApiService.getService();
//            Call<List<Playlist>> callback = dataService.getPlaylistCurrentDay();
//            callback.enqueue(new Callback<List<Playlist>>() {
//                @Override
//                public void onResponse(@NonNull Call<List<Playlist>> call, @NonNull Response<List<Playlist>> response) {
//
//
//                }
//
//                @Override
//                public void onFailure(@NonNull Call<List<Playlist>> call, @NonNull Throwable t) {
//                    Log.d(TAG, "Fail get data due to: " + t.getMessage() );
//                    myProgress.dismiss();
//
//                }
//            });
//    }

    private void initData() {
        authors.add(new Author("1", "author1", "https://upload.wikimedia.org/wikipedia/vi/3/32/S%C6%A1n_T%C3%B9ng_M-TP_-_C%C3%B3_ch%E1%BA%AFc_y%C3%AAu_l%C3%A0_%C4%91%C3%A2y.jpg"));
        authors.add(new Author("2", "author2", "https://cdnphoto.dantri.com.vn/ecdPkKw4WCg-NR0Zi2shwRYyUlo=/thumb_w/1020/2022/11/10/micheal-jackson-1668044313441.jpg"));
        authors.add(new Author("3", "author3", "https://cdn.tuoitre.vn/thumb_w/1100/471584752817336320/2023/9/5/jack-messi-1-16938973854241419756685.jpg"));
        authors.add(new Author("4", "author4", "https://images2.thanhnien.vn/528068263637045248/2023/8/30/1-1693385246169701996465.jpg"));
        authors.add(new Author("5", "author5", "https://suckhoedoisong.qltns.mediacdn.vn/thumb_w/640/324455921873985536/2023/9/6/dam-vinh-hung-16939936357631285203697.png"));

    }

//    public void filter(String query, Context context) {
//        myProgress = new MyProgress(context);
//        myProgress.show();
//        String lowerQuery = query.toLowerCase();
//        authorsFilter.clear();
//        if (!lowerQuery.equals("")) {
//            for (int i = 0; i < authors.size(); i++) {
//                if ((authors.get(i)).getName().toLowerCase().contains(lowerQuery)) {
//                        authorsFilter.add(authors.get(i));
//                    }
//            }
//        }
//        if (authorsFilter.isEmpty()) {
//            Log.d(TAG, "not data query");
//            noDataTv.setVisibility(View.VISIBLE);
//        } else {
//            noDataTv.setVisibility(View.GONE);
//            Log.d(TAG, "query :" + lowerQuery);
//        }
//        myProgress.dismiss();
//        authorAdapter = new AuthorAdapter(authorsFilter, getActivity());
//        recyclerView.setAdapter(authorAdapter);
//        authorAdapter.notifyDataSetChanged();
//    }
}