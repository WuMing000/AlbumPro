package com.js.photoalbum.bean;

import androidx.annotation.NonNull;

public class DownProgressBean {

    private long downloadId;
    private String progress;

    public DownProgressBean() {
    }

    public DownProgressBean(long downloadId, String progress) {
        this.downloadId = downloadId;
        this.progress = progress;
    }

    public long getDownloadId() {
        return downloadId;
    }

    public void setDownloadId(long downloadId) {
        this.downloadId = downloadId;
    }

    public String getProgress() {
        return progress;
    }

    public void setProgress(String progress) {
        this.progress = progress;
    }

    @NonNull
    @Override
    public String toString() {
        return "DownProgressBean{" +
                "downloadId=" + downloadId +
                ", progress='" + progress + '\'' +
                '}';
    }
}
