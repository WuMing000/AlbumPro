package com.js.photoalbum.utils;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

public class ToastUtils {

    private static Toast toast = null;
 
    public static void showToast(Context context,String text) {
        Log.e("TAG", toast + "");
        if (toast == null) {
            toast = Toast.makeText(context, text, Toast.LENGTH_SHORT);
        } else {
            toast.cancel();
            toast.setText(text);
            toast.setDuration(Toast.LENGTH_SHORT);
        }
        toast.show();
    }

    public static void cancelToast() {
        if (toast != null) {
            toast.cancel();
        }
    }
}