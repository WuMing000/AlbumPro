package com.js.photoalbum.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;

import com.js.photoalbum.MyApplication;
import com.js.photoalbum.activity.MainActivity;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class CheckPermissionUtil {

    static String[] permissions = new String[]{
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    private static boolean isAllGet = true;

    //点击按钮，访问如下方法
    public static void checkPermissions(Activity activity){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            for (String permission : permissions) {
                int j = ContextCompat.checkSelfPermission(MyApplication.getContext(), permission);
                // 权限是否已经 授权 GRANTED---授权  DINIED---拒绝
                if (j != PackageManager.PERMISSION_GRANTED) {
                    // 如果没有授予该权限，就去提示用户请求
                    startRequestPermission(activity);
                    isAllGet = false;
                }
            }
            if (isAllGet) {
                //获取权限成功,跳转
                Intent intent = new Intent(activity, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                activity.startActivity(intent);
                activity.finish();
            }
        }
    }

    private static void startRequestPermission(Activity activity) {
        ActivityCompat.requestPermissions(activity, permissions, 321);
    }
}
