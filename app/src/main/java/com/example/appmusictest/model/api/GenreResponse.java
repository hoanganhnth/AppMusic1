package com.example.appmusictest.model.api;

import com.example.appmusictest.model.Author;
import com.example.appmusictest.model.Genre;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class GenreResponse {
    @SerializedName("errCode")
    @Expose
    private String errCode;

    @SerializedName("genre")
    @Expose
    private String genres;

    public String getErrCode() {
        return errCode;
    }

    public String getGenres() {
        return genres;
    }

    public void setGenres(String genres) {
        this.genres = genres;
    }

    public void setErrCode(String errCode) {
        this.errCode = errCode;
    }
}
