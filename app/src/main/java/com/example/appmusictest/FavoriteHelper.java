package com.example.appmusictest;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.appmusictest.activity.FavoriteAlbumActivity;
import com.example.appmusictest.activity.FavoriteAuthorActivity;
import com.example.appmusictest.activity.FavoritePlaylistActivity;
import com.example.appmusictest.activity.FavoriteSongActivity;
import com.example.appmusictest.model.Album;
import com.example.appmusictest.model.Author;
import com.example.appmusictest.model.Playlist;
import com.example.appmusictest.model.Song;
import com.example.appmusictest.model.api.ApiResponse;
import com.example.appmusictest.service.ApiService;
import com.example.appmusictest.service.DataService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FavoriteHelper {
    private static final String TAG = "FavoriteHelper";
    public static final boolean TYPE_DELETE = false;
    public static final boolean TYPE_ADD = true;

    public static boolean isInFavSong() {
        return true;
    }

    public static void actionWithFav(Context context, String idUser, String id, boolean actionAdd, int type, Object ob) {
        DataService dataService = ApiService.getService();
        Call<ApiResponse> callback;
        switch (type) {
            case MyApplication.TYPE_PLAYLIST:
                if (actionAdd) {
                    callback = dataService.addFavPlaylist(idUser, id);
                } else {
                    callback = dataService.removeFavPlaylist(idUser, id);
                }
                break;
            case MyApplication.TYPE_ALBUM:
                if (actionAdd) {
                    callback = dataService.addFavAlbum(idUser, id);
                } else {
                    callback = dataService.removeFavAlbum(idUser, id);
                }
                break;
            case MyApplication.TYPE_SONG:
                if (actionAdd) {
                    callback = dataService.addFavSong(idUser, id);

                } else {
                    callback = dataService.removeFavSong(idUser, id);
                }
                break;
            case MyApplication.TYPE_AUTHOR:
                if (actionAdd) {
                    callback = dataService.addFavAuthor(idUser, id);
                } else {
                    callback = dataService.removeFavAuthor(idUser, id);
                }
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + type);
        }
        callback.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse> call, @NonNull Response<ApiResponse> response) {
                if (response.code() == 200) {
                    assert response.body() != null;
                    Toast.makeText(context, response.body().getMessage(), Toast.LENGTH_SHORT).show();

                    if (response.body().getErrCode().equals("0")) {
                        switch (type) {
                            case MyApplication.TYPE_SONG:
                                if (actionAdd) {
                                    FavoriteSongActivity.addSong((Song) ob);
                                } else {
                                    FavoriteSongActivity.removeSong((Song) ob);
                                }
                                break;
                            case MyApplication.TYPE_ALBUM:
                                if (actionAdd) {
                                    FavoriteAlbumActivity.addAlbum((Album) ob);
                                } else {
                                    FavoriteAlbumActivity.removeAlbum((Album) ob);
                                }
                                break;
                            case MyApplication.TYPE_PLAYLIST:
                                if (actionAdd) {
                                    FavoritePlaylistActivity.addPlaylist((Playlist) ob);
                                } else {
                                    FavoritePlaylistActivity.removePlaylist((Playlist) ob);
                                }
                                break;
                            case MyApplication.TYPE_AUTHOR:
                                if (actionAdd) {
                                    FavoriteAuthorActivity.addAuthor((Author) ob);
                                } else {
                                    FavoriteAuthorActivity.removeAuthor((Author) ob);
                                }
                                break;
                        }
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<ApiResponse> call, @NonNull Throwable t) {

                Log.d(TAG, "Not get data from server due to " + t.getMessage());
            }
        });
    }

}
