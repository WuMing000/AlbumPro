package com.js.photoalbum;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import com.js.photoalbum.utils.CustomUtil;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;

@SuppressLint("LongLogTag")
public class BaseActivity extends Activity {

    private static final String TAG = "BaseActivity==============>";

    private static final List<Activity> activityList = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");
        CustomUtil.hideNavigationBar(this);
        activityList.add(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d(TAG, "onRestart");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
        activityList.remove(this);
    }

    public static void finishAll() {
        for(Activity activity : activityList) {
            activity.finish();
        }

        activityList.clear();
    }

    public static void exit() {
        finishAll();
    }

}
