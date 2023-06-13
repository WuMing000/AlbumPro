package com.js.photoalbum.view;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.js.photoalbum.MyApplication;
import com.js.photoalbum.R;
import com.js.photoalbum.bean.SlideTypeBean;
import com.js.photoalbum.utils.CustomUtil;
import com.js.photoalbum.utils.ToastUtils;

import androidx.annotation.NonNull;

@SuppressLint("UseCompatLoadingForDrawables")
public class AddPhotoDialog extends Dialog {

    private final static String TAG = "AddPhotoDialog===========>";

    private Button btnAddPhoto, btnClear;
    private LinearLayout llAddPhoto, llClearSlide;
    private ImageView ivAddPhoto;

    public AddPhotoDialog(@NonNull Context context) {
        super(context, R.style.dialog_soft_input);
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_add_photo, null);

        btnAddPhoto = view.findViewById(R.id.btn_add_photo);
        btnClear = view.findViewById(R.id.btn_clear_slide);
        ivAddPhoto = view.findViewById(R.id.iv_add_photo);
        llAddPhoto = view.findViewById(R.id.ll_add_photo);
        llClearSlide = view.findViewById(R.id.ll_clear_slide);

        setContentView(view);
    }

    public void setImagePhoto(boolean isAdd) {
        if (isAdd) {
            ivAddPhoto.setImageResource(R.mipmap.icon_shift_out);
        } else {
            ivAddPhoto.setImageResource(R.mipmap.icon_add);
        }
    }

    public void setAddPhotoText(String text) {
        btnAddPhoto.setText(text);
    }

    public void setAddPhotoOnClickListener(View.OnClickListener onClickListener) {
        llAddPhoto.setOnClickListener(onClickListener);
    }

    public void setCLearOnClickListener(View.OnClickListener onClickListener) {
        llClearSlide.setOnClickListener(onClickListener);
    }

}
