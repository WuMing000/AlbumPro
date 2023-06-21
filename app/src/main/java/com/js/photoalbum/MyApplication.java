package com.js.photoalbum;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.js.photoalbum.bean.PhotoBean;
import com.js.photoalbum.bean.SlideTypeBean;
import com.js.photoalbum.utils.PhotoListDataSaveUtils;

import java.util.List;

public class MyApplication extends Application {

    @SuppressLint("StaticFieldLeak")
    private static Context mContext;
    private static PhotoListDataSaveUtils photoListDataSaveUtils;
    private static PhotoListDataSaveUtils slideSpeedSaveUtils;
    private static PhotoListDataSaveUtils slideTypeSaveUtils;

    private static List<PhotoBean> photoList;

    public static List<PhotoBean> getPhotoList() {
        return photoListDataSaveUtils.getDataList("photoList");
    }

    public static void setPhotoList(List<PhotoBean> photoList) {
        MyApplication.photoList = photoList;
        Log.e("TAG", MyApplication.photoList.toString());
        photoListDataSaveUtils.setDataList("photoList", MyApplication.photoList);
    }

    public static String getSlideSpeed() {
        return slideSpeedSaveUtils.getDataString("slideSpeed");
    }

    public static void setSlideSpeed(String slideSpeed) {
        slideSpeedSaveUtils.setDataString("slideSpeed", slideSpeed);
    }

    public static SlideTypeBean getSlideType() {
        return slideTypeSaveUtils.getDataBean("slideType", "slideId");
    }

    public static void setSlideType(SlideTypeBean slideTypeBean) {
        slideTypeSaveUtils.setDataBean("slideType", "slideId", slideTypeBean);
    }

    public static Context getContext() {
        return mContext;
    }

    public static void setContext(Context mContext) {
        MyApplication.mContext = mContext;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Fresco.initialize(this);
        mContext = getApplicationContext();
        photoListDataSaveUtils = new PhotoListDataSaveUtils(mContext, "photo_list_data");
        slideSpeedSaveUtils = new PhotoListDataSaveUtils(mContext, "slide_speed_data");
        slideTypeSaveUtils = new PhotoListDataSaveUtils(mContext, "slide_type_data");
    }
}
