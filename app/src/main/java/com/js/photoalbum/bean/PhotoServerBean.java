package com.js.photoalbum.bean;

import com.google.gson.annotations.SerializedName;

import androidx.annotation.NonNull;

public class PhotoServerBean {

    @SerializedName("id")
    private int id;
    @SerializedName("photoType")
    private String photoType;
    @SerializedName("photoUrl")
    private String photoUrl;

    public PhotoServerBean() {
    }

    public PhotoServerBean(int id, String photoType, String photoUrl) {
        this.id = id;
        this.photoType = photoType;
        this.photoUrl = photoUrl;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPhotoType() {
        return photoType;
    }

    public void setPhotoType(String photoType) {
        this.photoType = photoType;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    @NonNull
    @Override
    public String toString() {
        return "PhotoServerBean{" +
                "id=" + id +
                ", photoType='" + photoType + '\'' +
                ", photoUrl='" + photoUrl + '\'' +
                '}';
    }
}
