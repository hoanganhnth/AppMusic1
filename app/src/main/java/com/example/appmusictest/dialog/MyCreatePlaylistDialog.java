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

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.example.appmusictest.R;
import com.example.appmusictest.activity.FavoritePlaylistActivity;
import com.example.appmusictest.activity.MainActivity;
import com.example.appmusictest.adapter.PlaylistAdapter;
import com.example.appmusictest.model.Playlist;
import com.example.appmusictest.model.api.ApiResponse;
import com.example.appmusictest.model.api.CreatePlaylistResponse;
import com.example.appmusictest.service.ApiService;
import com.example.appmusictest.service.DataService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyCreatePlaylistDialog {
    private AlertDialog dialog;
    private EditText playlistEt;
    private TextView submitBtn, cancelBtn;

    public MyCreatePlaylistDialog(Context context, PlaylistAdapter adapter) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View view = LayoutInflater.from(context).inflate(R.layout.custom_dialog_add, null);
        playlistEt = view.findViewById(R.id.playlistEt);
        submitBtn = view.findViewById(R.id.submitBtn);
        cancelBtn = view.findViewById(R.id.cancelBtn);
        builder.setView(view);
        builder.setCancelable(true);
        dialog = builder.create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        playlistEt.requestFocus();
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
                createDataServer(context, playlistEt.getText().toString(), adapter);
            }
        });
        cancelBtn.setOnClickListener(v -> dialog.dismiss());
    }

    private void createDataServer(Context context, String title, PlaylistAdapter adapter) {
        DataService dataService = ApiService.getService();
        Call<CreatePlaylistResponse> callback = dataService.createPlaylist(MainActivity.getIdUser(), title, "");
        callback.enqueue(new Callback<CreatePlaylistResponse>() {
            @Override
            public void onResponse(@NonNull Call<CreatePlaylistResponse> call, @NonNull Response<CreatePlaylistResponse> response) {
                if (response.code() == 200) {
                    assert response.body() != null;
                    if (response.body().getErrCode().equals("0")) {
                        FavoritePlaylistActivity.addPlaylist(new Playlist(response.body().getIdPlaylist(), playlistEt.getText().toString(), "", MainActivity.getIdUser()));
                        if (adapter != null) {
                            adapter.notifyItemInserted(FavoritePlaylistActivity.getSize() - 1);
                        }
                    }
                    Toast.makeText(context, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                }
                dialog.dismiss();
            }

            @Override
            public void onFailure(@NonNull Call<CreatePlaylistResponse> call, @NonNull Throwable t) {

            }
        });
    }

    public void show() {
        dialog.show();
    }
}
