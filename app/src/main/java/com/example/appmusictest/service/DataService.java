package com.example.appmusictest.service;

import com.example.appmusictest.model.Playlist;
import com.example.appmusictest.model.Song;
import com.example.appmusictest.model.api.LoginRequest;
import com.example.appmusictest.model.api.LoginResponse;
import com.example.appmusictest.model.api.RegisterRequest;
import com.example.appmusictest.model.api.RegisterResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface DataService {
//    @GET("/currentday")
//    Call<List<Playlist>> getPlaylistCurrentDay();
//
//    @FormUrlEncoded
//    @POST("/listsong")
//    Call<List<Song>> getSongByPlaylist(@Field("idPlaylist") String idPlaylist);
    @GET("playlistforcurrentday.php")
    Call<List<Playlist>> getPlaylistCurrentDay();

    @FormUrlEncoded
    @POST("listsong.php")
    Call<List<Song>> getSongByPlaylist(@Field("idPlaylist") String idPlaylist);

    @FormUrlEncoded
    @POST("/register'")
    Call<RegisterResponse> performUserSignUp(@Body RegisterRequest registerRequest);

    @FormUrlEncoded
    @POST("/login'")
    Call<LoginResponse> performUserLoginIn(@Body LoginRequest loginRequest);



}
