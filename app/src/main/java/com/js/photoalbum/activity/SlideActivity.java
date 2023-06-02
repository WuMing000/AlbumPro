package com.js.photoalbum.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;

import com.js.photoalbum.BaseActivity;
import com.js.photoalbum.MyApplication;
import com.js.photoalbum.R;
import com.js.photoalbum.adapter.SlideRecyclerViewAdapter;
import com.js.photoalbum.bean.PhotoBean;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

@SuppressLint("LongLogTag")
public class SlideActivity extends BaseActivity {

    private static final String TAG = "SlideActivity===========>";

    private RecyclerView rvSlide;
    private SlideRecyclerViewAdapter adapter;
    private List<PhotoBean> mList;

    private float downY, moveY;

    private ScheduledExecutorService scheduledExecutorService;
    private LinearLayoutManager linearLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_slide);

        mList = new ArrayList<>();
        rvSlide = findViewById(R.id.rv_slide);
        adapter = new SlideRecyclerViewAdapter(this, mList);
        linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        rvSlide.setLayoutManager(linearLayoutManager);
        rvSlide.setAdapter(adapter);

        PagerSnapHelper pagerSnapHelper = new PagerSnapHelper();
        pagerSnapHelper.attachToRecyclerView(rvSlide);

//        Intent intent = getIntent();
//        ArrayList<PhotoBean> slideList = intent.getParcelableArrayListExtra("slideList");
        mList.addAll(MyApplication.getPhotoList());
        adapter.notifyDataSetChanged();
        Log.e(TAG, mList.toString());

    }

    @Override
    protected void onResume() {
        super.onResume();
        scheduledExecutorService = Executors.newScheduledThreadPool(1);
        scheduledExecutorService.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                rvSlide.smoothScrollToPosition(linearLayoutManager.findFirstVisibleItemPosition() + 1);
            }
        }, 5000, 5000, TimeUnit.MILLISECONDS);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {

        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            downY = ev.getRawY();
        } else if (ev.getAction() == MotionEvent.ACTION_MOVE) {
            moveY = ev.getRawY();
            float v = Math.abs(downY - moveY);
            Log.e(TAG, v + "");
            if (v > 100) {
                finish();
            }
        }

        return getWindow().superDispatchTouchEvent(ev) || onTouchEvent(ev);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (scheduledExecutorService != null) {
            scheduledExecutorService.shutdown();
        }
    }
}