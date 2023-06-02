package com.js.photoalbum.activity;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.js.photoalbum.BaseActivity;
import com.js.photoalbum.MyApplication;
import com.js.photoalbum.R;
import com.js.photoalbum.adapter.PhotoRecyclerViewAdapter;
import com.js.photoalbum.bean.PhotoBean;
import com.js.photoalbum.manager.CenterZoomLayoutManager;
import com.js.photoalbum.manager.HorizontalDecoration;
import com.js.photoalbum.utils.CustomUtil;
import com.js.photoalbum.utils.ToastUtils;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import jp.wasabeef.glide.transformations.BlurTransformation;

@SuppressLint("LongLogTag")
public class MainActivity extends BaseActivity {

    private static final String TAG = "MainActivity=============>";

    private RecyclerView rvPhoto;
    private PhotoRecyclerViewAdapter adapter;
    private List<PhotoBean> mList;
    private List<PhotoBean> localList;
    private List<PhotoBean> slideList;

    private Button btnNature, btnLocal, btnSlide, btnBack;
    private ImageView ivGaussBlur;

    private final float mShrinkAmount = 0.55f;
    private final float mShrinkDistance = 0.9f;

    private boolean isScroll = true;

    private CenterZoomLayoutManager centerZoomLayoutManager;
    private LinearLayoutManager linearLayoutManager;

    private int currentPosition;

    private String currentAlbum;
    AlertDialog dialog;

    private Handler handler = new Handler(Looper.myLooper()) {
        @Override
        public void dispatchMessage(@NonNull Message msg) {
            super.dispatchMessage(msg);
            if (msg.what == 0x001) {
                Bundle bundle = (Bundle) msg.obj;
                String path = bundle.getString("path");
                String name = bundle.getString("name");
                String author = bundle.getString("author");
//                Bitmap bitmap = BitmapFactory.decodeFile(path);
//                if (bitmap.getWidth() == 1920 && bitmap.getHeight() == 1080) {
                mList.add(new PhotoBean(path, name, author));
                localList.add(new PhotoBean(path, name, author));
                adapter.notifyDataSetChanged();
//                rvPhoto.smoothScrollToPosition(0);
//                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mList = new ArrayList<>();
        localList = new ArrayList<>();
        slideList = new ArrayList<>();
        rvPhoto = findViewById(R.id.rv_photo);
        btnNature = findViewById(R.id.btn_nature);
        btnLocal = findViewById(R.id.btn_local);
        ivGaussBlur = findViewById(R.id.iv_gauss_blur);
        btnBack = findViewById(R.id.btn_back);
        btnSlide = findViewById(R.id.btn_slide);

        slideList = MyApplication.getPhotoList();

        adapter = new PhotoRecyclerViewAdapter(this, mList);
        centerZoomLayoutManager = new CenterZoomLayoutManager(this, RecyclerView.HORIZONTAL, false);
        linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        rvPhoto.setLayoutManager(centerZoomLayoutManager);

        LinearSnapHelper mLinearSnapHelper = new LinearSnapHelper();//让recyclerview的item居中的方法
        mLinearSnapHelper.attachToRecyclerView(rvPhoto);//将该类绑定到相应的recyclerview上
//        mLinearSnapHelper.calculateScrollDistance(100, 100);
        rvPhoto.addItemDecoration(new HorizontalDecoration(0));
        rvPhoto.setAdapter(adapter);

        initListener();
        initList();

        currentAlbum = btnLocal.getText().toString();

    }

    private void initList() {

        new Thread(){
            @Override
            public void run() {
                super.run();
                Cursor cursor = getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, null, null, null, null);
                while (cursor.moveToNext()) {
                    //获取图片的名称
                    @SuppressLint("Range") String name = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME));
                    @SuppressLint("Range") String author = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.AUTHOR));
                    // 获取图片的绝对路径
                    int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                    String path = cursor.getString(column_index);
                    Log.i("GetImagesPath", "GetImagesPath: name = "+name+"  path = "+ path);
//                    mList.add(path);
//                    adapter.notifyDataSetChanged();
                    Message message = new Message();
                    message.what = 0x001;
                    Bundle bundle = new Bundle();
                    bundle.putString("path", path);
                    bundle.putString("name", name);
                    bundle.putString("author", author);
                    message.obj = bundle;
                    handler.sendMessageAtTime(message, 100);
                }
                cursor.close();
            }
        }.start();

    }

    private void initListener() {

        rvPhoto.addOnScrollListener(mOnScrollListener);
        adapter.setOnItemClickListener(new PhotoRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onClick(int position) {
                Log.e(TAG, position + "");
                if (position == currentPosition) {
//                    String imageUrl = mList.get(position);
                    Intent intent = new Intent(MainActivity.this, ImageLargeActivity.class);
                    intent.putExtra("position", position);
                    intent.putParcelableArrayListExtra("mList", (ArrayList<PhotoBean>) mList);
                    startActivity(intent);
                } else if (position > currentPosition) {
                    rvPhoto.smoothScrollBy(650, 0);
                } else {
                    rvPhoto.smoothScrollBy(-650, 0);
                }
            }
        });

        btnNature.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ToastUtils.cancelToast();
                if (btnNature.getText().toString().equals(currentAlbum)) {
                    ToastUtils.showToast(MainActivity.this, "正在操作该分类");
//                    Toast.makeText(MainActivity.this, "正在操作该分类", Toast.LENGTH_SHORT).show();
                } else {
                    currentAlbum = btnNature.getText().toString();
                    mList.clear();
                    mList.add(new PhotoBean("http://img.netbian.com/file/20130316/68888e99d665f2b8dba45e065c60ca42.jpg", "", ""));
                    mList.add(new PhotoBean("http://img.netbian.com/file/20150417/31bdff0d6c694b93ba462ffb21e8da4b.jpg", "", ""));
                    mList.add(new PhotoBean("http://img.netbian.com/file/2023/0518/225517rRWjH.jpg", "", ""));
                    mList.add(new PhotoBean("http://img.netbian.com/file/2023/0527/234811tmIC3.jpg", "", ""));
                    mList.add(new PhotoBean("http://img.netbian.com/file/2016/0108/86d01043b9b088bc0f833b6167a54528.jpg", "", ""));
//                mList.add(Contact.SERVER_URL + "1.jpg");
                    adapter.notifyDataSetChanged();
                    if (currentPosition == 0) {
                        rvPhoto.smoothScrollToPosition(0);
                        rvPhoto.smoothScrollBy(-480 * mList.size() * mList.size(), 0);
                    } else {
                        rvPhoto.smoothScrollBy(-480 * mList.size() * mList.size(), 0);
                    }
                }
            }
        });

        btnLocal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ToastUtils.cancelToast();
                if (btnLocal.getText().toString().equals(currentAlbum)) {
                    ToastUtils.showToast(MainActivity.this, "正在操作该分类");
//                    Toast.makeText(MainActivity.this, "正在操作该分类", Toast.LENGTH_SHORT).show();
                } else {
                    currentAlbum = btnLocal.getText().toString();
//                    adapter.notifyDataSetChanged();
                    mList.clear();
                    if (localList.size() != 0) {
                        mList.addAll(localList);
                    }
                    adapter.notifyDataSetChanged();
                    if (currentPosition == 0) {
                        rvPhoto.smoothScrollToPosition(0);
                        rvPhoto.smoothScrollBy(-480 * mList.size() * mList.size(), 0);
                    } else {
                        rvPhoto.smoothScrollBy(-480 * mList.size() * mList.size(), 0);
                    }
//                    initList();
                }
//                adapter.notifyDataSetChanged();
            }
        });

        adapter.setOnItemLongClickListener(new PhotoRecyclerViewAdapter.OnItemLongClickListener() {
            @Override
            public void onClick(PhotoBean photoBean, ImageView imageView) {
                boolean isAdd = false;
                Log.e(TAG, MyApplication.getPhotoList().toString());
                Log.e(TAG, slideList.toString());
                for (PhotoBean bean : MyApplication.getPhotoList()) {
                    if (photoBean.getImgUrl().equals(bean.getImgUrl())) {
                        isAdd = true;
                        dialog = new AlertDialog.Builder(MainActivity.this)
                                .setMessage("该图片已添加到幻灯片，是否移除？")
                                .setCancelable(false)
                                .setPositiveButton("是", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                        slideList.remove(photoBean);
                                        Log.e(TAG, slideList.toString());
                                        MyApplication.setPhotoList(slideList);
                                        Log.e(TAG, MyApplication.getPhotoList().toString());
                                        imageView.setVisibility(View.GONE);
                                    }
                                })
                                .setNegativeButton("否", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                }).show();
                    }
                }
                if (!isAdd) {
                    dialog = new AlertDialog.Builder(MainActivity.this)
                            .setMessage("是否将该图片添加到幻灯片")
                            .setCancelable(false)
                            .setPositiveButton("是", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    slideList.add(photoBean);
                                    MyApplication.setPhotoList(slideList);
                                    imageView.setVisibility(View.VISIBLE);
                                }
                            })
                            .setNegativeButton("否", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            }).show();
                }
            }
        });

        btnSlide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (slideList.size() == 0) {
                    ToastUtils.showToast(MainActivity.this, "请长按图片添加图片到幻灯片");
                } else {
                    Intent intent = new Intent(MainActivity.this, SlideActivity.class);
//                    intent.putParcelableArrayListExtra("slideList", (ArrayList<PhotoBean>) slideList);
                    startActivity(intent);
                }
            }
        });

    }

    private RecyclerView.OnScrollListener mOnScrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {

            Log.e(TAG, "================onScroll");
            int firstVisibleItemPosition = centerZoomLayoutManager.findFirstVisibleItemPosition();
            int lastVisibleItemPosition = centerZoomLayoutManager.findLastVisibleItemPosition();
            if (lastVisibleItemPosition - firstVisibleItemPosition == 1 && firstVisibleItemPosition == 0) {
                currentPosition = firstVisibleItemPosition;
            } else if (lastVisibleItemPosition - firstVisibleItemPosition == 1 && lastVisibleItemPosition == centerZoomLayoutManager.getItemCount() - 1) {
                currentPosition = lastVisibleItemPosition;
            } else {
                currentPosition = (lastVisibleItemPosition + firstVisibleItemPosition) / 2;
            }

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (currentPosition != -1) {
                        Glide.with(MyApplication.getContext()).load(mList.get(currentPosition).getImgUrl()).skipMemoryCache(false).dontAnimate().apply(RequestOptions.bitmapTransform(new BlurTransformation(25, 3))).into(new CustomTarget<Drawable>() {
                            @Override
                            public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                                ivGaussBlur.setImageDrawable(resource);
                            }

                            @Override
                            public void onLoadCleared(@Nullable Drawable placeholder) {

                            }
                        });
                    }
                }
            });

            Log.e(TAG, firstVisibleItemPosition + "," + lastVisibleItemPosition + "," + currentPosition);
            float midpoint = recyclerView.getWidth() / 2.f;
            float d0 = 0.f;
            float d1 = mShrinkDistance * midpoint;
            float s0 = 1.f;
            float s1 = 1.f - mShrinkAmount;
            for (int i = 0; i < recyclerView.getChildCount(); i++) {
                if (i >= 1 && isScroll) {
                    View child = recyclerView.getChildAt(i);
                    float childMidpoint =
                            (centerZoomLayoutManager.getDecoratedRight(child) + centerZoomLayoutManager.getDecoratedLeft(child)) / 2.f;
                    float d = Math.min(d1, Math.abs(midpoint - childMidpoint));
                    float scale = s0 + (s1 - s0) * (d - d0) / (d1 - d0);
                    child.setScaleX(scale);
                    child.setScaleY(scale);
                    isScroll = false;
                }
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mOnScrollListener != null) {
            rvPhoto.removeOnScrollListener(mOnScrollListener);
        }
        if (dialog != null) {
            dialog.dismiss();
        }
    }
}