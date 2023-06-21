package com.js.photoalbum.activity;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.js.photoalbum.BaseActivity;
import com.js.photoalbum.MyApplication;
import com.js.photoalbum.R;
import com.js.photoalbum.adapter.SlideRecyclerViewAdapter;
import com.js.photoalbum.bean.PhotoBean;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

@SuppressLint({"LongLogTag", "NotifyDataSetChanged", "SimpleDateFormat"})
public class SlideActivity extends BaseActivity {

    private static final String TAG = "SlideActivity===========>";

    private RecyclerView rvSlide;
    private TextView tvTime;

    private ScheduledExecutorService scheduledExecutorService;
    private LinearLayoutManager linearLayoutManager;

    private int slideSpeed;
    private String slideType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_slide);

        List<PhotoBean> mList = new ArrayList<>();
        rvSlide = findViewById(R.id.rv_slide);
        tvTime = findViewById(R.id.tv_time);

        if (MyApplication.getSlideSpeed().length() == 0) {
            slideSpeed = 10000;
        } else {
            slideSpeed = Integer.parseInt(MyApplication.getSlideSpeed());
        }

        slideType = MyApplication.getSlideType().getSlideType();
        if (slideType == null) {
            slideType = "平滑";
        }
        Log.e(TAG, "slideType:" + slideType);

        Log.e(TAG, "slideSpeed:" + slideSpeed);
//        slideSpeed = getIntent().getIntExtra("slideSpeed", 10000);

        SlideRecyclerViewAdapter adapter = new SlideRecyclerViewAdapter(this, mList);
        linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        rvSlide.setLayoutManager(linearLayoutManager);
        rvSlide.setAdapter(adapter);

        PagerSnapHelper pagerSnapHelper = new PagerSnapHelper();
        pagerSnapHelper.attachToRecyclerView(rvSlide);

        SimpleDateFormat df = new SimpleDateFormat("HH:mm");
        String date = df.format(new Date());
        tvTime.setText(date);
//        handler.sendEmptyMessageAtTime(0x001, 100);

        rvSlide.addOnScrollListener(onScrollListener);

        mList.addAll(MyApplication.getPhotoList());
        adapter.notifyDataSetChanged();
        Log.e(TAG, mList.toString());

        registerUpdateTimeReceiver();

    }

//    private final float mShrinkAmount = 0.55f;
//    private final float mShrinkDistance = 0.9f;
    RecyclerView.OnScrollListener onScrollListener = new RecyclerView.OnScrollListener() {

        @Override
        public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);

            if ("缩小".equals(slideType)) {
                float midpoint = recyclerView.getWidth() / 2.f;
                float d0 = 0.f;
                float d1 = 0.9f * midpoint;
                float s0 = 1.f;
                float s1 = 1.f - 0.55f;
                for (int i = 0; i < recyclerView.getChildCount(); i++) {
                    View child = recyclerView.getChildAt(i);
                    float childMidpoint = (linearLayoutManager.getDecoratedRight(child) + linearLayoutManager.getDecoratedLeft(child)) / 2.f;
                    float d = Math.min(d1, Math.abs(midpoint - childMidpoint));
                    float scale = s0 + (s1 - s0) * (d - d0) / (d1 - d0);
                    child.setScaleX(scale);
                    child.setScaleY(scale);

                }
            }
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        scheduledExecutorService = Executors.newScheduledThreadPool(1);
        scheduledExecutorService.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                rvSlide.smoothScrollToPosition(linearLayoutManager.findFirstVisibleItemPosition() + 1);
            }
        }, slideSpeed, slideSpeed, TimeUnit.MILLISECONDS);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    /**
     * interval update time
     */
    private void registerUpdateTimeReceiver() {
        //register time update
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_TIME_TICK);
        filter.addAction(Intent.ACTION_TIME_CHANGED);
        registerReceiver(mTimeUpdateReceiver, filter);
    }

    /**
     * broad receive time update
     */
    BroadcastReceiver mTimeUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent == null) {
                return;
            }
            String action = intent.getAction();
            if (action == null || action.isEmpty()) {
                return;
            }

            if (action.equals(Intent.ACTION_TIME_TICK)) {
                //system every 1 min send broadcast
                SimpleDateFormat df = new SimpleDateFormat("HH:mm");
                String date = df.format(new Date());
                tvTime.setText(date);
            }
        }
    };

    private boolean isMoveDown = false;
    private float downY;
    private float moveY;
    private float downX;

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {

        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            isMoveDown = false;
            downY = ev.getRawY();
            downX = ev.getRawX();
        } else if (ev.getAction() == MotionEvent.ACTION_MOVE) {
            moveY = ev.getRawY();
            float moveX = ev.getRawX();
            float abs = Math.abs(downX - moveX);
            float v = downY - moveY;
            Log.e(TAG, v + "");
            if (v < 0 || abs > 100) {
                isMoveDown = true;
            }
        } else if (ev.getAction() == MotionEvent.ACTION_UP && !isMoveDown) {
            float v = downY - moveY;
            Log.e(TAG, v + "");
            if (v > 100) {
                finish();
                overridePendingTransition(0, R.anim.slide_out_from_bottom);
                return getWindow().superDispatchTouchEvent(ev) || onTouchEvent(ev);
            }
            finish();
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mTimeUpdateReceiver != null) {
            unregisterReceiver(mTimeUpdateReceiver);
        }
    }
}