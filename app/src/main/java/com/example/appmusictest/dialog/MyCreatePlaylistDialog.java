package com.example.appmusictest.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import com.example.appmusictest.R;
import com.example.appmusictest.activity.FavoritePlaylistActivity;
import com.example.appmusictest.adapter.PlaylistAlbumAdapter;
import com.example.appmusictest.model.Playlist;

public class MyCreatePlaylistDialog {
    private AlertDialog dialog;
    private EditText playlistEt;
    private TextView submitBtn,cancelBtn;
    public MyCreatePlaylistDialog(Context context, PlaylistAlbumAdapter adapter) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View view = LayoutInflater.from(context).inflate(R.layout.custom_dialog_add,null);
        playlistEt = view.findViewById(R.id.playlistEt);
        submitBtn = view.findViewById(R.id.submitBtn);
        cancelBtn = view.findViewById(R.id.cancelBtn);
        builder.setView(view);
        builder.setCancelable(true);
        dialog = builder.create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        submitBtn.setBackground(ContextCompat.getDrawable(context, R.drawable.shape_button4));
        playlistEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().isEmpty()) {
                    submitBtn.setBackground(ContextCompat.getDrawable(context, R.drawable.shape_button4));
                } else {
                    submitBtn.setBackground(ContextCompat.getDrawable(context, R.drawable.shape_button3));
                }
            }
        });

        submitBtn.setOnClickListener(v -> {
            if (!playlistEt.getText().toString().isEmpty()) {
                FavoritePlaylistActivity.addPlaylist(new Playlist("4", playlistEt.getText().toString(),""));
                dialog.dismiss();
                if (adapter != null) {
                    adapter.notifyDataSetChanged();
                }

                Toast.makeText(context, "Đã tạo playlist", Toast.LENGTH_SHORT).show();
            }
        });
        cancelBtn.setOnClickListener(v -> dialog.dismiss());
    }

    public void show() {
        dialog.show();
    }
}
