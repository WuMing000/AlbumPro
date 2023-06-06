package com.js.photoalbum.bean;

public class SlideTypeBean {

    private int slideId;
    private String slideType;

    public SlideTypeBean() {
    }

    public SlideTypeBean(int slideId, String slideType) {
        this.slideId = slideId;
        this.slideType = slideType;
    }

    public int getSlideId() {
        return slideId;
    }

    public void setSlideId(int slideId) {
        this.slideId = slideId;
    }

    public String getSlideType() {
        return slideType;
    }

    public void setSlideType(String slideType) {
        this.slideType = slideType;
    }

    @Override
    public String toString() {
        return "SlideTypeBean{" +
                "slideId=" + slideId +
                ", slideType='" + slideType + '\'' +
                '}';
    }
}
