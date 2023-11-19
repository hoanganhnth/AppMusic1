package com.example.appmusictest.service;

import com.example.appmusictest.model.Playlist;
import com.example.appmusictest.model.Song;
import com.example.appmusictest.model.api.AlbumsResponse;
import com.example.appmusictest.model.api.ApiResponse;
import com.example.appmusictest.model.api.AuthorsResponse;
import com.example.appmusictest.model.api.LoginResponse;
import com.example.appmusictest.model.api.PlaylistsResponse;
import com.example.appmusictest.model.api.RegisterResponse;
import com.example.appmusictest.model.api.SongsResponse;

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
//    @GET("playlistforcurrentday.php")
//    Call<List<Playlist>> getPlaylistCurrentDay();
//
//    @FormUrlEncoded
//    @POST("listsong.php")
//    Call<List<Song>> getSongByPlaylist(@Field("idPlaylist") String idPlaylist);

//    @POST("/register'")
//    Call<RegisterResponse> performUserSignUp(@Body RegisterRequest registerRequest);
//
//    @POST("/login'")
//    Call<LoginResponse> performUserLoginIn(@Body LoginRequest loginRequest);

    @FormUrlEncoded
    @POST("/register")
    Call<RegisterResponse> performUserSignUp(@Field("username") String username, @Field("email") String email, @Field("password") String password);

    @FormUrlEncoded
    @POST("/login")
    Call<LoginResponse> performUserLoginIn(@Field("email") String email, @Field("password") String password);

    @FormUrlEncoded
    @POST("/getfavsongs")
    Call<SongsResponse> getFavSong(@Field("idUser") String idUser);

    @FormUrlEncoded
    @POST("/getfavalbums")
    Call<AlbumsResponse> getFavAlbum(@Field("idUser") String idUser);

    @FormUrlEncoded
    @POST("/getfavplaylists")
    Call<PlaylistsResponse> getFavPlaylist(@Field("idUser") String idUser);

    @FormUrlEncoded
    @POST("/getfavauthors")
    Call<AuthorsResponse> getFavAuthor(@Field("idUser") String idUser);

    @FormUrlEncoded
    @POST("/addfavsong")
    Call<ApiResponse> addFavSong(@Field("idUser") String idUser, @Field("idSong") String idSong);

    @FormUrlEncoded
    @POST("/removefavsong")
    Call<ApiResponse> removeFavSong(@Field("idUser") String idUser, @Field("idSong") String idSong);

    @FormUrlEncoded
    @POST("/addfavalbum")
    Call<ApiResponse> addFavAlbum(@Field("idUser") String idUser, @Field("idAlbum") String idAlbum);

    @FormUrlEncoded
    @POST("/removefavalbum")
    Call<ApiResponse> removeFavAlbum(@Field("idUser") String idUser, @Field("idAlbum") String idAlbum);

    @FormUrlEncoded
    @POST("/addfavplaylist")
    Call<ApiResponse> addFavPlaylist(@Field("idUser") String idUser, @Field("idPlaylist") String idPlaylist);

    @FormUrlEncoded
    @POST("/removefavalbum")
    Call<ApiResponse> removeFavAuthor(@Field("idUser") String idUser, @Field("idPlaylist") String idPlaylist);

    @FormUrlEncoded
    @POST("/addfavauthor")
    Call<ApiResponse> addFavAuthor(@Field("idUser") String idUser, @Field("idAuthor") String idAuthor);

    @FormUrlEncoded
    @POST("/removefavauthor")
    Call<ApiResponse> removeFavPlaylist(@Field("idUser") String idUser, @Field("idAuthor") String idAuthor);


}
