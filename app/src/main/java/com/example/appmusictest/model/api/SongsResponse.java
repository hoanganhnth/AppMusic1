package com.example.appmusictest.model.api;

import com.example.appmusictest.model.Song;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class SongsResponse {
    @SerializedName("errCode")
    @Expose
    private String errCode;

    @SerializedName("songs")
    @Expose
    private ArrayList<Song> songs;

    public SongsResponse(String errCode, ArrayList<Song> songs) {
        this.errCode = errCode;
        this.songs = songs;
    }

    public String getErrCode() {
        return errCode;
    }

    public void setErrCode(String errCode) {
        this.errCode = errCode;
    }

    public ArrayList<Song> getSongs() {
        return songs;
    }

    public void setSongs(ArrayList<Song> songs) {
        this.songs = songs;
    }
}
