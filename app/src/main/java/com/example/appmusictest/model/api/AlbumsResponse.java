package com.example.appmusictest.model.api;

import com.example.appmusictest.model.Album;
import com.example.appmusictest.model.Song;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class AlbumsResponse {
    @SerializedName("errCode")
    @Expose
    private String errCode;

    @SerializedName("albums")
    @Expose
    private ArrayList<Album> albums;

    public String getErrCode() {
        return errCode;
    }

    public void setErrCode(String errCode) {
        this.errCode = errCode;
    }


    public ArrayList<Album> getAlbums() {
        return albums;
    }

    public void setAlbums(ArrayList<Album> albums) {
        this.albums = albums;
    }
}
