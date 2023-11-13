package com.example.appmusictest.service;

import com.example.appmusictest.model.Playlist;
import com.example.appmusictest.model.Song;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface DataService {
    @GET("/currentday")
    Call<List<Playlist>> getPlaylistCurrentDay();

    @FormUrlEncoded
    @POST("/listsong")
    Call<List<Song>> getSongByPlaylist(@Field("idPlaylist") String idPlaylist);
}
