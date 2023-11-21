package com.example.appmusictest.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.appmusictest.R;
import com.example.appmusictest.activity.AuthorDetailActivity;
import com.example.appmusictest.model.Author;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class AuthorSuggestAdapter extends RecyclerView.Adapter<AuthorSuggestAdapter.ViewHolder> {
    private ArrayList<Author> authors;
    private ArrayList<Author> authorAll;
    private Context context;


    public AuthorSuggestAdapter(ArrayList<Author> authors, Context context) {
        this.authors = authors;
        this.context = context;
    }

    @NonNull
    @Override
    public AuthorSuggestAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_author_suggest, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AuthorSuggestAdapter.ViewHolder holder, int position) {
        holder.titleSuggestTv.setText(authors.get(position).getName());
        Glide.with(context)
                .load(authors.get(position).getArtUrl())
                .placeholder(R.mipmap.music_player_icon_round)
                .into(holder.imgSuggestCiv);
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, AuthorDetailActivity.class);
            Bundle bundle = new Bundle();
            bundle.putParcelable("author", authors.get(position));
            intent.putExtras(bundle);
            context.startActivity(intent);

        });
    }

    @Override
    public int getItemCount() {
        return authors.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        CircleImageView imgSuggestCiv;
        TextView titleSuggestTv;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgSuggestCiv = itemView.findViewById(R.id.imgSuggestCiv);
            titleSuggestTv = itemView.findViewById(R.id.titleSuggestTv);
        }
    }
}
