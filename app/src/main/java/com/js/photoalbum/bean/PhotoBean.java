package com.js.photoalbum.bean;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Objects;

import androidx.annotation.NonNull;

public class PhotoBean implements Parcelable {

    private String imgUrl;
    private String imgName;
    private String imgAuthor;

    public PhotoBean() {
    }

    public PhotoBean(String imgUrl, String imgName, String imgAuthor) {
        this.imgUrl = imgUrl;
        this.imgName = imgName;
        this.imgAuthor = imgAuthor;
    }

    protected PhotoBean(Parcel in) {
        imgUrl = in.readString();
        imgName = in.readString();
        imgAuthor = in.readString();
    }

    public static final Creator<PhotoBean> CREATOR = new Creator<PhotoBean>() {
        @Override
        public PhotoBean createFromParcel(Parcel in) {
            return new PhotoBean(in);
        }

        @Override
        public PhotoBean[] newArray(int size) {
            return new PhotoBean[size];
        }
    };

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getImgName() {
        return imgName;
    }

    public void setImgName(String imgName) {
        this.imgName = imgName;
    }

    public String getImgAuthor() {
        return imgAuthor;
    }

    public void setImgAuthor(String imgAuthor) {
        this.imgAuthor = imgAuthor;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(imgUrl);
        dest.writeString(imgName);
        dest.writeString(imgAuthor);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PhotoBean photoBean = (PhotoBean) o;
        return Objects.equals(imgUrl, photoBean.imgUrl) && Objects.equals(imgName, photoBean.imgName) && Objects.equals(imgAuthor, photoBean.imgAuthor);
    }

    @Override
    public int hashCode() {
        return Objects.hash(imgUrl, imgName, imgAuthor);
    }

    @NonNull
    @Override
    public String toString() {
        return "PhotoBean{" +
                "imgUrl='" + imgUrl + '\'' +
                ", imgName='" + imgName + '\'' +
                ", imgAuthor='" + imgAuthor + '\'' +
                '}';
    }
}
