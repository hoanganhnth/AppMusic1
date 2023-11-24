package com.example.appmusictest.model.api;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CreatePlaylistResponse {
    @SerializedName("errCode")
    @Expose
    private String errCode;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("idPlaylist")
    @Expose
    private String idPlaylist;

    public String getErrCode() {
        return errCode;
    }

    public void setErrCode(String errCode) {
        this.errCode = errCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getIdPlaylist() {
        return idPlaylist;
    }

    public void setIdPlaylist(String idPlaylist) {
        this.idPlaylist = idPlaylist;
    }
}
