package com.js.photoalbum.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.js.photoalbum.BaseActivity;
import com.js.photoalbum.MyApplication;
import com.js.photoalbum.R;
import com.js.photoalbum.adapter.LargeRecyclerViewAdapter;
import com.js.photoalbum.bean.PhotoBean;
import com.js.photoalbum.utils.CustomUtil;
import com.js.photoalbum.utils.ToastUtils;
import com.js.photoalbum.view.AddPhotoDialog;

import java.util.ArrayList;
import java.util.List;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

@SuppressLint("LongLogTag")
public class ImageLargeActivity extends BaseActivity {

    private static final String TAG = "ImageLargeActivity==============>";

    private RecyclerView recyclerView;
    private LargeRecyclerViewAdapter adapter;
    private List<PhotoBean> mList;
    private List<PhotoBean> slideList;
    private AddPhotoDialog addPhotoDialog;
    boolean isAdd = false;
    private ImageView ivBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_image_large);
        CustomUtil.hideNavigationBar(this);

//        ivLarge = findViewById(R.id.iv_large);
        recyclerView = findViewById(R.id.rv_large);
        mList = new ArrayList<>();
        slideList = new ArrayList<>();
        adapter = new LargeRecyclerViewAdapter(this, mList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recyclerView.setAdapter(adapter);

        PagerSnapHelper pagerSnapHelper = new PagerSnapHelper();
        pagerSnapHelper.attachToRecyclerView(recyclerView);

        ivBack = findViewById(R.id.iv_back);

        slideList = MyApplication.getPhotoList();

        Intent intent = getIntent();
        int position = intent.getIntExtra("position", 0);
        mList.addAll(intent.getParcelableArrayListExtra("mList"));
        recyclerView.scrollToPosition(position);
        adapter.notifyDataSetChanged();

//        Bitmap bitmap = BitmapFactory.decodeFile(imageUrl);
//        ivLarge.setImageBitmap(bitmap);
//        Glide.with(this).asBitmap().load(imageUrl).into(ivLarge);

        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        adapter.setOnLongItemClickListener(new LargeRecyclerViewAdapter.OnLongItemClickListener() {
            @Override
            public void onClick(int position, PhotoBean photoBean) {
                isAdd = false;
                addPhotoDialog = new AddPhotoDialog(ImageLargeActivity.this);
                Window window = addPhotoDialog.getWindow();
                window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                Log.e(TAG, MyApplication.getPhotoList().toString());
                Log.e(TAG, slideList.toString());
                for (PhotoBean bean : MyApplication.getPhotoList()) {
                    if (photoBean.getImgUrl().equals(bean.getImgUrl())) {
                        isAdd = true;
                        break;
                    }
                }
                if (isAdd) {
                    addPhotoDialog.setAddPhotoText("将该图片移出画框");
                } else {
                    addPhotoDialog.setAddPhotoText("将该图片添加画框");
                }
                addPhotoDialog.setImagePhoto(isAdd);
                addPhotoDialog.setAddPhotoOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        addPhotoDialog.dismiss();
                        if (isAdd) {
                            slideList.remove(photoBean);
                            Log.e(TAG, slideList.toString());
                            MyApplication.setPhotoList(slideList);
                            Log.e(TAG, MyApplication.getPhotoList().toString());
                            ToastUtils.showToast(ImageLargeActivity.this, "图片已移出画框");
                        } else {
                            slideList.add(photoBean);
                            MyApplication.setPhotoList(slideList);
                            ToastUtils.showToast(ImageLargeActivity.this, "图片已添加画框");
                        }
                    }
                });
                addPhotoDialog.setCLearOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        addPhotoDialog.dismiss();
                        if (slideList.size() != 0) {
                            slideList.clear();
                            MyApplication.setPhotoList(slideList);
                            ToastUtils.showToast(ImageLargeActivity.this, "已清空画框，请重新添加");
                        } else {
                            ToastUtils.showToast(ImageLargeActivity.this, "画框为空，无法进行操作");
                        }
                    }
                });
                addPhotoDialog.show();
            }
        });

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            //用来标记是否正在向最后一个滑动
            boolean isSlidingToLast = false;
            boolean isSlidingToStart = false;

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                Log.e(TAG, "=====");
                //设置什么布局管理器,就获取什么的布局管理器
                LinearLayoutManager manager = (LinearLayoutManager) recyclerView.getLayoutManager();
                // 当停止滑动时
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    //获取最后一个完全显示的ItemPosition ,角标值
                    int lastVisibleItem = manager.findLastCompletelyVisibleItemPosition();
                    int firstVisibleItem = manager.findFirstVisibleItemPosition();
                    //所有条目,数量值
                    int totalItemCount = manager.getItemCount();

                    // 判断是否滚动到底部，并且是向右滚动
                    if (lastVisibleItem == (totalItemCount - 1) && isSlidingToLast) {
                        //加载更多功能的代码
//                        Toast.makeText(ImageLargeActivity.this, "当前是本分类最后一张图片", Toast.LENGTH_SHORT).show();
                        ToastUtils.showToast(ImageLargeActivity.this, "到底了~~~");
//                        Snackbar.make(ImageLargeActivity.this, recyclerView, "当前是本分类最后一张图片", Snackbar.LENGTH_SHORT).show();
                    } else if (firstVisibleItem == 0 && isSlidingToStart) {
//                        Toast.makeText(ImageLargeActivity.this, "当前是本分类第一张图片", Toast.LENGTH_SHORT).show();
//                        ToastUtils.showToast(ImageLargeActivity.this, "当前是本分类第一张图片");
                    } else {
                        ToastUtils.cancelToast();
                    }
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                //dx用来判断横向滑动方向，dy用来判断纵向滑动方向
                //dx>0:向右滑动,dx<0:向左滑动
                //dy>0:向下滑动,dy<0:向上滑动
                Log.e(TAG, "dx:" + dx + ",dy:" + dy);
                isSlidingToLast = dx > 0;
                isSlidingToStart = dx < 0;
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (addPhotoDialog != null) {
            addPhotoDialog.dismiss();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ToastUtils.cancelToast();
    }
}