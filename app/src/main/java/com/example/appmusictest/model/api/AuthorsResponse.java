package com.example.appmusictest.model.api;

import com.example.appmusictest.model.Author;
import com.example.appmusictest.model.Song;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class AuthorsResponse {
    @SerializedName("errCode")
    @Expose
    private String errCode;

    @SerializedName("authors")
    @Expose
    private ArrayList<Author> authors;

    public String getErrCode() {
        return errCode;
    }

    public void setErrCode(String errCode) {
        this.errCode = errCode;
    }

    public ArrayList<Author> getAuthors() {
        return authors;
    }

    public void setAuthors(ArrayList<Author> authors) {
        this.authors = authors;
    }
}
