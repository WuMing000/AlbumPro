package com.js.photoalbum;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.js.photoalbum.bean.PhotoBean;
import com.js.photoalbum.utils.PhotoListDataSaveUtils;

import java.util.List;

public class MyApplication extends Application {

    private static Context mContext;
    private static PhotoListDataSaveUtils photoListDataSaveUtils;
    private static PhotoListDataSaveUtils slideDataSaveUtils;

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
        return slideDataSaveUtils.getDataString("slideSpeed");
    }

    public static void setSlideSpeed(String slideSpeed) {
        slideDataSaveUtils.setDataString("slideSpeed", slideSpeed);
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
        mContext = getApplicationContext();
        photoListDataSaveUtils = new PhotoListDataSaveUtils(mContext, "photo_list_data");
        slideDataSaveUtils = new PhotoListDataSaveUtils(mContext, "slide_list_data");
    }
}
