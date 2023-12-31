package com.example.appmusictest.model.api;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class LoginResponse {

    @SerializedName("errCode")
    @Expose
    private String errCode;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("idUser")
    @Expose
    private String idUser;
    @SerializedName("username")
    @Expose
    private String username;

    public LoginResponse(String errCode, String message) {
        this.errCode = errCode;
        this.message = message;
    }

    public String getIdUser() {
        return idUser;
    }

    public void setIdUser(String idUser) {
        this.idUser = idUser;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

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
}
