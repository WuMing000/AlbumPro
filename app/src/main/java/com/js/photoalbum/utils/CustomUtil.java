package com.js.photoalbum.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.Dialog;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.js.photoalbum.MyApplication;
import com.js.photoalbum.bean.DownBean;
import com.js.photoalbum.bean.DownProgressBean;

import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Timer;

import androidx.core.content.FileProvider;

public class CustomUtil {

    private static final String TAG = "CustomUtil============>";

    /**
     * 隐藏底部底部导航栏
     */
    public static void hideNavigationBar(Activity activity) {

        Window window;
        window = activity.getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            params.systemUiVisibility = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
            window.setAttributes(params);


            int uiFlags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR; // hide nav bar; // hide status bar

            uiFlags |= 0x00001000;    //SYSTEM_UI_FLAG_IMMERSIVE_STICKY: hide navigation bars - compatibility: building API level is lower than 19, use magic number directly for higher API target level

            activity.getWindow().getDecorView().setSystemUiVisibility(uiFlags);
        }
    }

    public static void killAppProcess()
    {
        //注意：不能先杀掉主进程，否则逻辑代码无法继续执行，需先杀掉相关进程最后杀掉主进程
        ActivityManager mActivityManager = (ActivityManager) MyApplication.getContext().getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> mList = mActivityManager.getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo runningAppProcessInfo : mList)
        {
            if (runningAppProcessInfo.pid != android.os.Process.myPid())
            {
                android.os.Process.killProcess(runningAppProcessInfo.pid);
            }
        }
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(0);
    }

    /**
     * 判断视图v是否应该隐藏输入软键盘，若v不是输入框，返回false
     *
     * @param v     视图
     * @param event 屏幕事件
     * @return 视图v是否应该隐藏输入软键盘，若v不是输入框，返回false
     */
    public static boolean isShouldHideInput(View v, MotionEvent event) {
        if (v != null && (v instanceof EditText)) {
            int[] leftTop = {0, 0};
            //获取输入框当前的location位置
            v.getLocationInWindow(leftTop);
            int left = leftTop[0];
            int top = leftTop[1];
            int bottom = top + v.getHeight();
            int right = left + v.getWidth();
            return !(event.getX() > left && event.getX() < right
                    && event.getY() > top && event.getY() < bottom);
        }
        return false;
    }

    //隐藏软键盘
    public static void hideKeyBoard(Dialog dialog) {
        InputMethodManager imm = (InputMethodManager) MyApplication.getContext().getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = dialog.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(MyApplication.getContext());
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static boolean isTouchPointInView(@NotNull View view, int x, int y) {
        Log.e(TAG, "x:" + x + ",y:" + y);
        int[] location = new int[2];
        view.getLocationInWindow(location);
        int left = location[0];
        int top = location[1];
        int right = left + view.getWidth();
        int bottom = top + view.getHeight();
        Log.e(TAG, "left:" + left + ",right:" + right + ",top:" + top + ",bottom:" + bottom);
        if (top <= y) {
            if (bottom >= y && x >= left && x <= right) {
                return true;
            }
        }
        return false;
    }

    public static String getServerFile(String path) {
        //获取网络数据
        //01.定义获取网络的数据的路径
//        String path="http://114.132.220.67:8080/test/js_project/store/Version.txt";
//        StringBuilder stringBuffer = null;
        String str = "";
        try {
            //2.实例化url
            URL url = new URL(path);
            //3.获取连接属性
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            //4.设置请求方式
            conn.setRequestMethod("GET");
            //以及请求时间
            conn.setConnectTimeout(5000);
            //5.获取响应码
            int code = conn.getResponseCode();
            if (200 == code) {
                //6.获取返回的数据json
                InputStream is = conn.getInputStream();
                //7.测试（删除-注释）
                //缓冲字符流
                BufferedReader br = new BufferedReader(new InputStreamReader(is));
//                stringBuffer = new StringBuilder();
                if ((str = br.readLine()) != null) {
//                    stringBuffer.append(str);
                    Log.i("tt", str);
                    return str;
                }
//                Log.i("tt", stringBuffer.toString());
                //8.解析
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return str;
    }

    /**
     * 获取本地软件版本号名称
     */
    public static String getLocalVersionName() {
        String localVersion = "";
        try {
            PackageInfo packageInfo = MyApplication.getContext().getApplicationContext()
                    .getPackageManager()
                    .getPackageInfo(MyApplication.getContext().getPackageName(), 0);
            localVersion = packageInfo.versionName;
            Log.d(TAG, "本软件的版本名：" + localVersion);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return localVersion;
    }

    public static DownBean updateAPK(String url) {

        DownloadManager manager = (DownloadManager) MyApplication.getContext().getSystemService(Context.DOWNLOAD_SERVICE);
        /*
         * 1. 封装下载请求
         */
        // 创建下载请求
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_HIDDEN);

        Log.e(TAG, MyApplication.getContext().getExternalFilesDir(null).getAbsolutePath());
        Log.e(TAG, url.substring(url.lastIndexOf("/") + 1));
        File saveFile = new File(MyApplication.getContext().getExternalFilesDir(null), "com.js.photoalbum");
        request.setDestinationUri(Uri.fromFile(saveFile));

        if (saveFile.exists()) {
            saveFile.delete();
            Log.e(TAG, "删除");
        }

        long downloadId = manager.enqueue(request);

        return new DownBean(downloadId);
    }

    public static DownProgressBean updateProgress(long downloadId, Timer timer) {
        DownProgressBean downProgressBean = new DownProgressBean();
        DownloadManager manager = (DownloadManager) MyApplication.getContext().getSystemService(Context.DOWNLOAD_SERVICE);
        // 创建一个查询对象
        DownloadManager.Query query = new DownloadManager.Query();
        // 根据 下载ID 过滤结果
        query.setFilterById(downloadId);
        // 还可以根据状态过滤结果
        // query.setFilterByStatus(DownloadManager.STATUS_SUCCESSFUL);
        // 执行查询, 返回一个 Cursor (相当于查询数据库)
        Cursor cursor = manager.query(query);
        if (!cursor.moveToFirst()) {
            cursor.close();
            return downProgressBean;
        }
        // 下载ID
        @SuppressLint("Range") long id = cursor.getLong(cursor.getColumnIndex(DownloadManager.COLUMN_ID));
        // 下载请求的状态
        @SuppressLint("Range") int status = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS));
        // 下载文件在本地保存的路径（Android 7.0 以后 COLUMN_LOCAL_FILENAME 字段被弃用, 需要用 COLUMN_LOCAL_URI 字段来获取本地文件路径的 Uri）
        @SuppressLint("Range") String localFilename = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI));
        // 已下载的字节大小
        @SuppressLint("Range") long downloadedSoFar = cursor.getLong(cursor.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR));
        // 下载文件的总字节大小
        @SuppressLint("Range") long totalSize = cursor.getLong(cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES)) == -1 ? 1 : cursor.getLong(cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES));
        cursor.close();
//        System.out.println("下载进度: " + downloadedSoFar  + "/" + totalSize);
        DecimalFormat decimalFormat = new DecimalFormat( "##0.00 ");
        String dd = decimalFormat.format(downloadedSoFar * 1.0f / totalSize * 100);
//        Log.e(TAG, downloadedSoFar * 1.0f / totalSize * 100 + "");
        Log.e(TAG, dd);
        downProgressBean = new DownProgressBean(downloadId, dd);
        if (status == DownloadManager.STATUS_SUCCESSFUL) {
            File installFile = null;
            File saveFile = new File(localFilename.substring(7));
            if (saveFile.exists()) {
                installFile = renameFile(localFilename.substring(7), localFilename.substring(7) + ".apk");
            }
            Log.e(TAG, installFile.getAbsolutePath());
//            System.out.println("下载成功, 打开文件, 文件路径: " + localFilename);
            installAPK(MyApplication.getContext(), installFile);
            timer.cancel();
        }

        return downProgressBean;
    }

    /**
     * oldPath 和 newPath必须是新旧文件的绝对路径
     */
    private static File renameFile(String oldPath, String newPath) {
        if (TextUtils.isEmpty(oldPath)) {
            return null;
        }

        if (TextUtils.isEmpty(newPath)) {
            return null;
        }
        File oldFile = new File(oldPath);
        File newFile = new File(newPath);
        boolean b = oldFile.renameTo(newFile);
        File file2 = new File(newPath);
        return file2;
    }

    /**
     * 安装APK内容
     */
    public static void installAPK(Context mContext, File apkName) {
        try {
            if (!apkName.exists()) {
                Log.e("TAG", "app not exists!");
                return;
            }
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);//安装完成后打开新版本
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION); // 给目标应用一个临时授权
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {//判断版本大于等于7.0
                Log.e("TAG", "11111111111111");
                //如果SDK版本>=24，即：Build.VERSION.SDK_INT >= 24，使用FileProvider兼容安装apk
                String packageName = mContext.getApplicationContext().getPackageName();
                String authority = new StringBuilder(packageName).append(".fileprovider").toString();
                Uri apkUri = FileProvider.getUriForFile(mContext, authority, apkName);
                intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
            } else {
                intent.setDataAndType(Uri.fromFile(apkName), "application/vnd.android.package-archive");
            }
            mContext.startActivity(intent);
//            android.os.Process.killProcess(android.os.Process.myPid());//安装完之后会提示”完成” “打开”。

        } catch (Exception e) {
            e.printStackTrace();
            Log.e("TAG", e.toString());
        }
    }

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm == null) {
        } else {
            //如果仅仅是用来判断网络连接
            // 则可以使用 cm.getActiveNetworkInfo().isAvailable();
            NetworkInfo[] info = cm.getAllNetworkInfo();
            if (info != null) {
                for (NetworkInfo networkInfo : info) {
                    if (networkInfo.getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

}
