package com.example.appmusictest.service;

import com.example.appmusictest.model.Playlist;
import com.example.appmusictest.model.Song;
import com.example.appmusictest.model.api.AlbumsResponse;
import com.example.appmusictest.model.api.ApiResponse;
import com.example.appmusictest.model.api.AuthorsResponse;
import com.example.appmusictest.model.api.CreatePlaylistResponse;
import com.example.appmusictest.model.api.GenreResponse;
import com.example.appmusictest.model.api.LoginResponse;
import com.example.appmusictest.model.api.PlaylistsResponse;
import com.example.appmusictest.model.api.RegisterResponse;
import com.example.appmusictest.model.api.SearchResponse;
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

    @GET("/albums")
    Call<AlbumsResponse> getAllAlbums();

    @GET("/randomauthors")
    Call<AuthorsResponse> getAuthorRandom();

    @GET("/randomalbums")
    Call<AlbumsResponse> getAlbumRandom();

    @FormUrlEncoded
    @POST("/listsong")
    Call<List<Song>> getSongByPlaylist(@Field("idPlaylist") String idPlaylist);


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
    @POST("/removefavplaylist")
    Call<ApiResponse> removeFavPlaylist(@Field("idUser") String idUser, @Field("idPlaylist") String idPlaylist);

    @FormUrlEncoded
    @POST("/addfavauthor")
    Call<ApiResponse> addFavAuthor(@Field("idUser") String idUser, @Field("idAuthor") String idAuthor);

    @FormUrlEncoded
    @POST("/removefavauthor")
    Call<ApiResponse> removeFavAuthor(@Field("idUser") String idUser, @Field("idAuthor") String idAuthor);

    @FormUrlEncoded
    @POST("/getalbumsongs")
    Call<SongsResponse> getAlbumSongs(@Field("idAlbum") String idAlbum);

    @FormUrlEncoded
    @POST("/getplaylistsongs")
    Call<SongsResponse> getPlaylistSongs(@Field("idPlaylist") String idPlaylist);

    @FormUrlEncoded
    @POST("/getauthorsongs")
    Call<SongsResponse> getAuthorSongs(@Field("idAuthor") String idAuthor);

    @FormUrlEncoded
    @POST("/getauthoralbums")
    Call<AlbumsResponse> getAuthorAlbums(@Field("idAuthor") String idAuthor);

    @FormUrlEncoded
    @POST("/createplaylist")
    Call<CreatePlaylistResponse> createPlaylist(@Field("idUser") String idUser, @Field("title") String title, @Field("artUrl") String artUrl);

    @FormUrlEncoded
    @POST("/search")
    Call<SearchResponse> search(@Field("text") String text);

    @FormUrlEncoded
    @POST("/addsongtoplaylist")
    Call<ApiResponse> addSongToPlaylist(@Field("idUser") String idUser, @Field("idSong") String idSong, @Field("idPlaylist") String idPlaylist);

    @FormUrlEncoded
    @POST("/getsongauthors")
    Call<AuthorsResponse> getSongAuthor(@Field("idSong") String idSong);

    @FormUrlEncoded
    @POST("/getgenre")
    Call<GenreResponse> getGenreSong(@Field("idSong") String idSong);



}
