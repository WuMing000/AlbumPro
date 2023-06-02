package com.js.photoalbum.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
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

    private Handler handler = new Handler(Looper.myLooper()) {
        @Override
        public void dispatchMessage(@NonNull Message msg) {
            super.dispatchMessage(msg);
            if (msg.what == 0x001) {
                handler.postDelayed(timeRunnable, 1000);
            }
        }
    };

    Runnable timeRunnable = new Runnable() {
        @Override
        public void run() {
            SimpleDateFormat df = new SimpleDateFormat("HH:mm");
            String date = df.format(new Date());
            tvTime.setText(date);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_slide);

        mList = new ArrayList<>();
        rvSlide = findViewById(R.id.rv_slide);
        tvTime = findViewById(R.id.tv_time);

        adapter = new SlideRecyclerViewAdapter(this, mList);
        linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        rvSlide.setLayoutManager(linearLayoutManager);
        rvSlide.setAdapter(adapter);

        PagerSnapHelper pagerSnapHelper = new PagerSnapHelper();
        pagerSnapHelper.attachToRecyclerView(rvSlide);

        handler.sendEmptyMessageAtTime(0x001, 100);

//        rvSlide.addOnScrollListener(onScrollListener);

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

            int childCount = recyclerView.getChildCount();
            Log.e("ccc", childCount + "");

            int[] location = new int[2];
            for (int i = 0; i < childCount; i++) {
                View v = recyclerView.getChildAt(i);
                v.getLocationOnScreen(location);
                int recyclerViewCenterX = recyclerView.getLeft() + recyclerView.getWidth() / 2;
                int itemCenterX = location[0] + v.getWidth() / 2;

//                   ★ 两边的图片缩放比例
                float scale = 0.8f;
//                     ★某个item中心X坐标距recyclerview中心X坐标的偏移量
                int offX = Math.abs(itemCenterX - recyclerViewCenterX);
//                    ★ 在一个item的宽度范围内，item从1缩放至scale，那么改变了（1-scale），从下列公式算出随着offX变化，item的变化缩放百分比

                float percent = offX * (1 - scale) / v.getWidth();
//                   ★  取反哟
                float interpretateScale = 1 - percent;


                v.setScaleX((interpretateScale));
                v.setScaleY((interpretateScale));

            }
//            float midpoint = recyclerView.getWidth() / 2.f;
//            float d0 = 0.f;
//            float d1 = mShrinkDistance * midpoint;
//            float s0 = 1.f;
//            float s1 = 1.f - mShrinkAmount;
//            for (int i = 0; i < recyclerView.getChildCount(); i++) {
//                if (i >= 1) {
//                    View child = recyclerView.getChildAt(i);
//                    float childMidpoint =
//                            (linearLayoutManager.getDecoratedRight(child) + linearLayoutManager.getDecoratedLeft(child)) / 2.f;
//                    float d = Math.min(d1, Math.abs(midpoint - childMidpoint));
//                    float scale = s0 + (s1 - s0) * (d - d0) / (d1 - d0);
//                    child.setScaleX(scale);
//                    child.setScaleY(scale);
//                }
//            }
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