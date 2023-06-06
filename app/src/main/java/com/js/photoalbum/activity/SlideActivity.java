package com.js.photoalbum.activity;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
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

@SuppressLint("LongLogTag")
public class SlideActivity extends BaseActivity {

    private static final String TAG = "SlideActivity===========>";

    private RecyclerView rvSlide;
    private TextView tvTime;
    private SlideRecyclerViewAdapter adapter;
    private List<PhotoBean> mList;

    private float downY, moveY;

    private ScheduledExecutorService scheduledExecutorService;
    private LinearLayoutManager linearLayoutManager;

    private int slideSpeed;
    private String slideType;

    private Handler handler = new Handler(Looper.myLooper()) {
        @Override
        public void dispatchMessage(@NonNull Message msg) {
            super.dispatchMessage(msg);
//            if (msg.what == 0x001) {
//                handler.postDelayed(timeRunnable, 1000);
//            }
        }
    };

//    Runnable timeRunnable = new Runnable() {
//        @Override
//        public void run() {
//            SimpleDateFormat df = new SimpleDateFormat("HH:mm");
//            String date = df.format(new Date());
//            tvTime.setText(date);
////            handler.sendEmptyMessageAtTime(0x001, 100);
//        }
//    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_slide);

        mList = new ArrayList<>();
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

        adapter = new SlideRecyclerViewAdapter(this, mList);
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

//        Intent intent = getIntent();
//        ArrayList<PhotoBean> slideList = intent.getParcelableArrayListExtra("slideList");
        mList.addAll(MyApplication.getPhotoList());
        adapter.notifyDataSetChanged();
        Log.e(TAG, mList.toString());

    }

    private final float mShrinkAmount = 0.55f;
    private final float mShrinkDistance = 0.9f;
    RecyclerView.OnScrollListener onScrollListener = new RecyclerView.OnScrollListener() {

        @Override
        public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);


//            int childCount = recyclerView.getChildCount();
////            Log.e("ccc", childCount + "");
////
//            int[] location = new int[2];
//            for (int i = 0; i < childCount; i++) {
//                View v = recyclerView.getChildAt(i);
//                v.getLocationOnScreen(location);
//                int recyclerViewCenterX = recyclerView.getLeft() + recyclerView.getWidth() / 2;
//                int itemCenterX = location[0] + v.getWidth() / 2;
//
////                   ★ 两边的图片缩放比例
//                float scale = 0.5f;
////                     ★某个item中心X坐标距recyclerview中心X坐标的偏移量
//                int offX = Math.abs(itemCenterX - recyclerViewCenterX);
////                    ★ 在一个item的宽度范围内，item从1缩放至scale，那么改变了（1-scale），从下列公式算出随着offX变化，item的变化缩放百分比
//
//                float percent = offX * (1 - scale) / v.getWidth();
////                   ★  取反哟
//                float interpretateScale = 1 - percent;
////
////
//                v.setScaleX((interpretateScale));
//                v.setScaleY((interpretateScale));
////
//            }

//            int childCount = recyclerView.getChildCount();
//            for (int i = 0; i < childCount; i++) {
//                View child = recyclerView.getChildAt(i);
//                int left = child.getLeft();//距屏幕左边距
//                float i1 = left * 1f / child.getWidth();//随着滑动左边距和控件宽度的比例变化
//                float degree = (30 * i1) * 1f;//旋转角度ÿ
//                child.setScaleX(degree);
//            }

            if ("缩小".equals(slideType)) {
                float midpoint = recyclerView.getWidth() / 2.f;
                float d0 = 0.f;
                float d1 = mShrinkDistance * midpoint;
                float s0 = 1.f;
                float s1 = 1.f - mShrinkAmount;
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

        registerUpdateTimeReceiver();
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
        if (mTimeUpdateReceiver != null) {
            unregisterReceiver(mTimeUpdateReceiver);
        }
    }
}