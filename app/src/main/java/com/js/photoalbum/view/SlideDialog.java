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
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.js.photoalbum.MyApplication;
import com.js.photoalbum.R;
import com.js.photoalbum.bean.SlideTypeBean;
import com.js.photoalbum.utils.CustomUtil;
import com.js.photoalbum.utils.ToastUtils;

import java.util.List;

import androidx.annotation.NonNull;

@SuppressLint("UseCompatLoadingForDrawables")
public class SlideDialog extends Dialog {

    private final static String TAG = "SlideDialog===========>";
    private final static int MAX_SPEED = 120000;

    private EditText etSpeed;
    private RadioGroup rgType;
    private RadioButton rbSmooth, rbReduce;
    private Button btnConfirm, btnCancel;
    private String slideType;
    private int slideId;

    public SlideDialog(@NonNull Context context) {
        super(context, R.style.dialog_soft_input);
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_slide, null);
        etSpeed = view.findViewById(R.id.et_speed);
        rgType = view.findViewById(R.id.rg_type);
        rbSmooth = view.findViewById(R.id.rb_smooth);
        rbReduce = view.findViewById(R.id.rb_reduce);
        btnConfirm = view.findViewById(R.id.btn_confirm);
        btnCancel = view.findViewById(R.id.btn_cancel);

        if (MyApplication.getSlideType().getSlideType() == null) {
            slideType = "平滑";
            slideId = R.id.rb_smooth;
        } else {
            slideType = MyApplication.getSlideType().getSlideType();
            slideId = MyApplication.getSlideType().getSlideId();
            rgType.check(slideId);
        }

        etSpeed.setText(MyApplication.getSlideSpeed());

        rgType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                Log.e("TAG", checkedId + "");
                if (checkedId == rbSmooth.getId()) {
                    slideType = rbSmooth.getText().toString();
                    slideId = checkedId;
                } else if (checkedId == rbReduce.getId()) {
                    slideType = rbReduce.getText().toString();
                    slideId = checkedId;
                }
            }
        });

        etSpeed.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (etSpeed.getText().toString().length() == 0 || Integer.parseInt(etSpeed.getText().toString()) < 1000) {
                    return;
                } else {
                    etSpeed.setBackground(MyApplication.getContext().getResources().getDrawable(R.drawable.selector_edittext_bg, null));
                }
                if (Integer.parseInt(etSpeed.getText().toString()) > MAX_SPEED) {
                    etSpeed.setText("" + MAX_SPEED);
                    etSpeed.setSelection(etSpeed.getText().length());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

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
        if (etSpeed.getText().toString().length() == 0) {
            etSpeed.setBackground(MyApplication.getContext().getResources().getDrawable(R.drawable.selector_edittext_null_bg, null));
            ToastUtils.showToast(MyApplication.getContext(), "幻灯片播放速度输入为空");
            return null;
        }else if (Integer.parseInt(etSpeed.getText().toString()) < 1000) {
            etSpeed.setBackground(MyApplication.getContext().getResources().getDrawable(R.drawable.selector_edittext_null_bg, null));
            ToastUtils.showToast(MyApplication.getContext(), "幻灯片播放速度小于1000ms");
            return null;
        }
        return etSpeed.getText().toString().trim();
    }

    public SlideTypeBean getSlideType() {
        return new SlideTypeBean(slideId, slideType);
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
            ToastUtils.cancelToast();
            View v = getCurrentFocus();
            if (CustomUtil.isTouchPointInView(btnConfirm, (int) ev.getX(), (int) ev.getY()) &&
                    (etSpeed.getText().toString().length() == 0 || Integer.parseInt(etSpeed.getText().toString()) < 1000)) {
                Log.e(TAG, "isOn btnConfirm");
                etSpeed.setFocusable(true);
                etSpeed.setFocusableInTouchMode(true);
                etSpeed.requestFocus();
                InputMethodManager inputManager = (InputMethodManager) etSpeed.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.showSoftInput(etSpeed, 0);
                return super.dispatchTouchEvent(ev);
            }
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
