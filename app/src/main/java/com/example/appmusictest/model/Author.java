package com.example.appmusictest.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class Author implements Parcelable {
    private String id;
    private String name;
    private String urlArt;

    public Author(String id, String name, String urlArt) {
        this.id = id;
        this.name = name;
        this.urlArt = urlArt;
    }

    protected Author(Parcel in) {
        id = in.readString();
        name = in.readString();
        urlArt = in.readString();
    }

    public static final Creator<Author> CREATOR = new Creator<Author>() {
        @Override
        public Author createFromParcel(Parcel in) {
            return new Author(in);
        }

        @Override
        public Author[] newArray(int size) {
            return new Author[size];
        }
    };

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrlArt() {
        return urlArt;
    }

    public void setUrlArt(String urlArt) {
        this.urlArt = urlArt;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(name);
        dest.writeString(urlArt);
    }
}
