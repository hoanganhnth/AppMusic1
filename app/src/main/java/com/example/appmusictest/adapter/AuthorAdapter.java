package com.example.appmusictest.adapter;

import static com.example.appmusictest.MyApplication.TYPE_AUTHOR;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.appmusictest.FavoriteHelper;
import com.example.appmusictest.R;
import com.example.appmusictest.activity.AuthorDetailActivity;
import com.example.appmusictest.activity.FavoriteAuthorActivity;
import com.example.appmusictest.activity.MainActivity;
import com.example.appmusictest.model.Author;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class AuthorAdapter extends RecyclerView.Adapter<AuthorAdapter.ViewHolder> {

    private ArrayList<Author> arrayList;
    private Context context;

    public AuthorAdapter(ArrayList<Author> arrayList, Context context) {
        this.arrayList = arrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public AuthorAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_author, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Author model = arrayList.get(position);
        holder.authorTv.setText(model.getName());
        Glide.with(context)
                .load(model.getArtUrl())
                .placeholder(R.mipmap.music_player_icon_round)
                .into(holder.authorIv);

        holder.itemView.setOnClickListener(v -> {

        });
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, AuthorDetailActivity.class);
            Bundle bundle = new Bundle();
            bundle.putParcelable("author", model);
            intent.putExtras(bundle);
            context.startActivity(intent);
        });
        String activity = context.getClass().getSimpleName();
        if (FavoriteAuthorActivity.isInFav(model)) {
            holder.authorFvIb.setImageResource(R.drawable.ic_favorite_purple);
        }
        holder.authorFvIb.setOnClickListener(v -> {
            if (!FavoriteAuthorActivity.isInFav(model)) {

                FavoriteHelper.actionWithFav(context, MainActivity.getIdUser(),model.getId(), FavoriteHelper.TYPE_ADD, TYPE_AUTHOR, model);
                holder.authorFvIb.setImageResource(R.drawable.ic_favorite_purple);
            } else if (activity.equals(FavoriteAuthorActivity.class.getSimpleName())){
                showDialog(position, model);
            } else {
                holder.authorFvIb.setImageResource(R.drawable.ic_favorite_gray);
                FavoriteHelper.actionWithFav(context, MainActivity.getIdUser(),model.getId(), FavoriteHelper.TYPE_DELETE, TYPE_AUTHOR, model);
            }
        });
    }
    private void showDialog(int pos, Author author) {
        AlertDialog dialog;
        TextView titleDialogDeleteTv,contentDialogTv,submitBtn,cancelBtn;
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View view = LayoutInflater.from(context).inflate(R.layout.custom_dialog_delete,null);

        titleDialogDeleteTv = view.findViewById(R.id.titleDialogDeleteTv);
        contentDialogTv = view.findViewById(R.id.contentDialogTv);
        submitBtn = view.findViewById(R.id.submitBtn);
        cancelBtn = view.findViewById(R.id.cancelBtn);


        titleDialogDeleteTv.setText(author.getName());


        contentDialogTv.setText(R.string.delete_author_title);

        builder.setView(view);
        builder.setCancelable(true);
        dialog = builder.create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();


        submitBtn.setOnClickListener(v -> {

            FavoriteHelper.actionWithFav(context, MainActivity.getIdUser(),author.getId(), FavoriteHelper.TYPE_DELETE, TYPE_AUTHOR, author);
            arrayList.remove(pos);
            notifyItemRemoved(pos);
            notifyItemRangeChanged(pos, arrayList.size() - pos) ;
            dialog.dismiss();
        });
        cancelBtn.setOnClickListener(v -> dialog.dismiss());
    }




    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView authorTv;
        CircleImageView authorIv;
        ImageButton authorFvIb;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            authorIv = itemView.findViewById(R.id.authorIv);
            authorTv = itemView.findViewById(R.id.rowTv);
            authorFvIb = itemView.findViewById(R.id.rowFvIb);
        }
    }
}
