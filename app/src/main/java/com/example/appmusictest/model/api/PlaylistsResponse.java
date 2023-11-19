package com.example.appmusictest.model.api;

import com.example.appmusictest.model.Playlist;
import com.example.appmusictest.model.Song;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class PlaylistsResponse {
    @SerializedName("errCode")
    @Expose
    private String errCode;

    @SerializedName("playlists")
    @Expose
    private ArrayList<Playlist> playlists;

    public String getErrCode() {
        return errCode;
    }

    public void setErrCode(String errCode) {
        this.errCode = errCode;
    }

    public ArrayList<Playlist> getPlaylists() {
        return playlists;
    }

    public void setPlaylists(ArrayList<Playlist> playlists) {
        this.playlists = playlists;
    }
}
