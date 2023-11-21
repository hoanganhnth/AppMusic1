package com.example.appmusictest.model.api;

import com.example.appmusictest.model.Album;
import com.example.appmusictest.model.Author;
import com.example.appmusictest.model.Playlist;
import com.example.appmusictest.model.Song;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class Result {
    @SerializedName("songs")
    @Expose
    private ArrayList<Song> songs;
    @SerializedName("playlists")
    @Expose
    private ArrayList<Playlist> playlists;
    @SerializedName("albums")
    @Expose
    private ArrayList<Album> albums;
    @SerializedName("authors")
    @Expose
    private ArrayList<Author> authors;

    public ArrayList<Song> getSongs() {
        return songs;
    }

    public void setSongs(ArrayList<Song> songs) {
        this.songs = songs;
    }

    public ArrayList<Playlist> getPlaylists() {
        return playlists;
    }

    public void setPlaylists(ArrayList<Playlist> playlists) {
        this.playlists = playlists;
    }

    public ArrayList<Album> getAlbums() {
        return albums;
    }

    public void setAlbums(ArrayList<Album> albums) {
        this.albums = albums;
    }

    public ArrayList<Author> getAuthors() {
        return authors;
    }

    public void setAuthors(ArrayList<Author> authors) {
        this.authors = authors;
    }
}
