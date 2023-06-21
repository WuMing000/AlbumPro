package com.js.photoalbum.bean;

import androidx.annotation.NonNull;

public class BottomBean {

    private int bottomId;
    private String bottomName;

    public BottomBean() {
    }

    public BottomBean(int bottomId, String bottomName) {
        this.bottomId = bottomId;
        this.bottomName = bottomName;
    }

    public int getBottomId() {
        return bottomId;
    }

    public void setBottomId(int bottomId) {
        this.bottomId = bottomId;
    }

    public String getBottomName() {
        return bottomName;
    }

    public void setBottomName(String bottomName) {
        this.bottomName = bottomName;
    }

    @NonNull
    @Override
    public String toString() {
        return "BottomBean{" +
                "bottomId=" + bottomId +
                ", bottomName='" + bottomName + '\'' +
                '}';
    }
}
