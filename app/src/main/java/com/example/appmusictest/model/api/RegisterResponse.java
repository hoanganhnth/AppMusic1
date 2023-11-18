package com.example.appmusictest.model.api;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class RegisterResponse {
    @SerializedName("errCode")
    @Expose
    private String errCode;
    @SerializedName("message")
    @Expose
    private String message;

    public RegisterResponse(String errCode, String message) {
        this.errCode = errCode;
        this.message = message;
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
