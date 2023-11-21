package com.example.appmusictest.model.api;

import com.example.appmusictest.model.Album;
import com.example.appmusictest.model.Author;
import com.example.appmusictest.model.Playlist;
import com.example.appmusictest.model.Song;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class SearchResponse {
    @SerializedName("errCode")
    @Expose
    private String errCode;

    @SerializedName("result")
    @Expose
    private Result result;

    public String getErrCode() {
        return errCode;
    }

    public Result getResult() {
        return result;
    }

    public void setResult(Result result) {
        this.result = result;
    }

    public void setErrCode(String errCode) {
        this.errCode = errCode;
    }


}
