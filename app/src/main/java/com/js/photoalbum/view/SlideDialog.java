package com.js.photoalbum.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.js.photoalbum.MyApplication;
import com.js.photoalbum.R;
import com.js.photoalbum.utils.CustomUtil;
import com.js.photoalbum.utils.ToastUtils;

import androidx.annotation.NonNull;

public class SlideDialog extends Dialog {

    private EditText etSpeed;
    private RadioGroup rgType;
    private RadioButton rbFadeIn, rbCube;
    private Button btnConfirm, btnCancel;
    private String typeText = "淡入";

    public SlideDialog(@NonNull Context context) {
        super(context, R.style.dialog_soft_input);
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_slide, null);
        etSpeed = view.findViewById(R.id.et_speed);
        rgType = view.findViewById(R.id.rg_type);
        rbFadeIn = view.findViewById(R.id.rb_fade_in);
        rbCube = view.findViewById(R.id.rb_cube);
        btnConfirm = view.findViewById(R.id.btn_confirm);
        btnCancel = view.findViewById(R.id.btn_cancel);
        rgType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                Log.e("TAG", checkedId + "");
                if (checkedId == rbFadeIn.getId()) {
                    typeText = rbFadeIn.getText().toString();
                } else if (checkedId == rbCube.getId()) {
                    typeText = rbCube.getText().toString();
                }
            }
        });
        setContentView(view);
    }

    public void setConfirmOnClickListener(View.OnClickListener onClickListener) {
        btnConfirm.setOnClickListener(onClickListener);
    }

    public void setCancelOnClickListener(View.OnClickListener onClickListener) {
        btnCancel.setOnClickListener(onClickListener);
    }

    public String getEditSpeed () {
        return etSpeed.getText().toString().trim();
    }

    public String getTypeText() {
        return typeText;
    }

    public void setEditSpeed () {
        etSpeed.setText("");
        etSpeed.setFocusable(true);
        etSpeed.setFocusableInTouchMode(true);
    }

    /**
     * 使editText点击外部时候失去焦点
     *
     * @param ev 触屏事件
     * @return 事件是否被消费
     */
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (CustomUtil.isShouldHideInput(v, ev)) {
                //点击editText控件外部
                InputMethodManager imm = (InputMethodManager) MyApplication.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    assert v != null;
                    //软键盘工具类关闭软键盘
                    CustomUtil.hideKeyBoard(this);
                    //使输入框失去焦点
                    v.clearFocus();
                }
            }
            return super.dispatchTouchEvent(ev);
        }
        // 必不可少，否则所有的组件都不会有TouchEvent了
        return getWindow().superDispatchTouchEvent(ev) || onTouchEvent(ev);
    }

}
