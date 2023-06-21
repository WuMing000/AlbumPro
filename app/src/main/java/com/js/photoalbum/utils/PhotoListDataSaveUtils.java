package com.js.photoalbum.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.js.photoalbum.bean.PhotoBean;
import com.js.photoalbum.bean.SlideTypeBean;

import java.util.ArrayList;
import java.util.List;

public class PhotoListDataSaveUtils {
    private final SharedPreferences preferences;
    private final SharedPreferences.Editor editor;

    public PhotoListDataSaveUtils(Context mContext, String preferenceName) {
        preferences = mContext.getSharedPreferences(preferenceName, Context.MODE_PRIVATE);
        editor = preferences.edit();
    }

    /**
     * 保存普通String
     */
    public void setDataString(String key, String value) {
        if (value == null) {
            return;
        }
        Gson gson = new Gson();
        //转换成json数据，再保存
        String strJson = gson.toJson(value);
        editor.clear();
        editor.putString(key, strJson);
        editor.commit();
    }

    /**
     * 获取普通的String
     */
    public String getDataString(String key) {
        String value = "";
        String strJson = preferences.getString(key, null);
        if (null == strJson) {
            return value;
        }
        Gson gson = new Gson();
        value = gson.fromJson(strJson, new TypeToken<String>() {
        }.getType());
        return value;
    }

    /**
     * 保存对象
     */
    public void setDataBean(String typeName, String idName, SlideTypeBean value) {
        if (value == null) {
            return;
        }
        editor.clear();
        editor.putString(typeName, value.getSlideType());
        editor.putInt(idName, value.getSlideId());
        editor.commit();
    }

    /**
     * 获取对象
     */
    public SlideTypeBean getDataBean(String typeName, String idName) {
        String slideType = preferences.getString(typeName, null);
        int slideId = preferences.getInt(idName, 0);
        return new SlideTypeBean(slideId, slideType);
    }

    /**
     * 保存List
     */
    public void setDataList(String tag, List<PhotoBean> dataList) {
        if (null == dataList)
            return;

        Gson gson = new Gson();
        //转换成json数据，再保存
        String strJson = gson.toJson(dataList);
        editor.clear();
        editor.putString(tag, strJson);
        editor.commit();
    }

    /**
     * 获取List
     */
    public List<PhotoBean> getDataList(String tag) {
        List<PhotoBean> dataList = new ArrayList();
        String strJson = preferences.getString(tag, null);
        if (null == strJson) {
            return dataList;
        }
        Gson gson = new Gson();
        dataList = gson.fromJson(strJson, new TypeToken<List<PhotoBean>>() {
        }.getType());
        return dataList;
    }
}
