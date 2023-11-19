package com.example.appmusictest.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Song implements Parcelable {
    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("nameAuthor")
    @Expose
    private String nameAuthor;
    @SerializedName("idAlbum")
    @Expose
    private String idAlbum;
    @SerializedName("idCategory")
    @Expose
    private String idCategory;
    @SerializedName("idPlaylist")
    @Expose
    private String idPlaylist;
    @SerializedName("artUrl")
    @Expose
    private String artUrl;
    @SerializedName("pathUrl")
    @Expose
    private String pathUrl;

    public Song(String id, String title, String nameAuthor, String idAlbum, String idCategory, String idPlaylist, String artUrl, String pathUrl) {
        this.id = id;
        this.title = title;
        this.nameAuthor = nameAuthor;
        this.idAlbum = idAlbum;
        this.idCategory = idCategory;
        this.idPlaylist = idPlaylist;
        this.artUrl = artUrl;
        this.pathUrl = pathUrl;
    }

    public Song() {
    }

    protected Song(Parcel in) {
        id = in.readString();
        title = in.readString();
        nameAuthor = in.readString();
        idAlbum = in.readString();
        idCategory = in.readString();
        idPlaylist = in.readString();
        artUrl = in.readString();
        pathUrl = in.readString();
    }

    public static final Creator<Song> CREATOR = new Creator<Song>() {
        @Override
        public Song createFromParcel(Parcel in) {
            return new Song(in);
        }

        @Override
        public Song[] newArray(int size) {
            return new Song[size];
        }
    };

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getNameAuthor() {
        return nameAuthor;
    }

    public void setNameAuthor(String nameAuthor) {
        this.nameAuthor = nameAuthor;
    }

    public String getIdAlbum() {
        return idAlbum;
    }

    public void setIdAlbum(String idAlbum) {
        this.idAlbum = idAlbum;
    }

    public String getIdCategory() {
        return idCategory;
    }

    public void setIdCategory(String idCategory) {
        this.idCategory = idCategory;
    }

    public String getIdPlaylist() {
        return idPlaylist;
    }

    public void setIdPlaylist(String idPlaylist) {
        this.idPlaylist = idPlaylist;
    }

    public String getArtUrl() {
        return artUrl;
    }

    public void setArtUrl(String artUrl) {
        this.artUrl = artUrl;
    }

    public String getPathUrl() {
        return pathUrl;
    }

    public void setPathUrl(String pathUrl) {
        this.pathUrl = pathUrl;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(title);
        dest.writeString(nameAuthor);
        dest.writeString(idAlbum);
        dest.writeString(idCategory);
        dest.writeString(idPlaylist);
        dest.writeString(artUrl);
        dest.writeString(pathUrl);
    }
}
