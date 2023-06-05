package com.js.photoalbum.activity;

import android.animation.ObjectAnimator;
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
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.js.photoalbum.BaseActivity;
import com.js.photoalbum.MyApplication;
import com.js.photoalbum.R;
import com.js.photoalbum.adapter.PhotoRecyclerViewAdapter;
import com.js.photoalbum.bean.PhotoBean;
import com.js.photoalbum.bean.PhotoServerBean;
import com.js.photoalbum.manager.CenterZoomLayoutManager;
import com.js.photoalbum.manager.Contact;
import com.js.photoalbum.manager.HorizontalDecoration;
import com.js.photoalbum.utils.ToastUtils;
import com.js.photoalbum.view.CircleImageView;
import com.js.photoalbum.view.SlideDialog;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import jp.wasabeef.glide.transformations.BlurTransformation;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

@SuppressLint("LongLogTag")
public class MainActivity extends BaseActivity {

    private static final String TAG = "MainActivity=============>";

    private static final int GET_NATURE_PHOTO = 0x002;
    private static final int ADAPTER_CHANGED = 0x003;

    private RecyclerView rvPhoto;
    private PhotoRecyclerViewAdapter adapter;
    private List<PhotoBean> mList;
    private List<PhotoBean> localList;
    private List<PhotoBean> natureList;
    private List<PhotoBean> slideList;

    private LinearLayout llSlide;
    private Button btnSlide, btnClearSlide, btnStartSlide, btnSettingSlide;
    private CircleImageView btnNature, btnLocal;
    private ImageView ivGaussBlur, ivBack;
    private TextView tvNature, tvLocal;

    private final float mShrinkAmount = 0.55f;
    private final float mShrinkDistance = 0.9f;

    private boolean isScroll = true;

    private CenterZoomLayoutManager centerZoomLayoutManager;
    private LinearLayoutManager linearLayoutManager;

    private int currentPosition;

    private String currentAlbum;
    private AlertDialog dialog;

    private ObjectAnimator animator;

    private Handler handler = new Handler(Looper.myLooper()) {
        @Override
        public void dispatchMessage(@NonNull Message msg) {
            super.dispatchMessage(msg);
            switch (msg.what) {
                case 0x001:
                    Bundle bundle = (Bundle) msg.obj;
                    String path = bundle.getString("path");
                    String name = bundle.getString("name");
                    String author = bundle.getString("author");
//                Bitmap bitmap = BitmapFactory.decodeFile(path);
//                if (bitmap.getWidth() == 1920 && bitmap.getHeight() == 1080) {

//                    mList.add(new PhotoBean(path, name, author));
                    localList.add(new PhotoBean(path, name, author));
                    adapter.notifyDataSetChanged();
//                rvPhoto.smoothScrollToPosition(0);
//                }
                    break;
                case GET_NATURE_PHOTO:
                    Bundle data = (Bundle) msg.obj;
                    String text = data.getString("text");
                    String url = data.getString("url");
                    Log.e(TAG, "text:" + text + ",url:" + url);
                    ArrayList<PhotoServerBean> list = new Gson().fromJson(text, new TypeToken<List<PhotoServerBean>>() {
                    }.getType());
                    new Thread() {
                        @Override
                        public void run() {
                            super.run();
                            for (int i = 0; i < list.get(0).getPhotoUrl().split(",").length; i++) {
                                mList.add(new PhotoBean(list.get(0).getPhotoUrl().split(",")[i], "", ""));
                                natureList.add(new PhotoBean(list.get(0).getPhotoUrl().split(",")[i], "", ""));
                                handler.sendEmptyMessageAtTime(ADAPTER_CHANGED, 100);
                            }
                        }
                    }.start();
                    break;
                case ADAPTER_CHANGED:
                    adapter.notifyDataSetChanged();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mList = new ArrayList<>();
        localList = new ArrayList<>();
        natureList = new ArrayList<>();
        slideList = new ArrayList<>();
        rvPhoto = findViewById(R.id.rv_photo);
        btnNature = findViewById(R.id.btn_nature);
        btnLocal = findViewById(R.id.btn_local);
        ivGaussBlur = findViewById(R.id.iv_gauss_blur);
        ivBack = findViewById(R.id.iv_back);
        btnSlide = findViewById(R.id.btn_slide);
        btnClearSlide = findViewById(R.id.btn_clear_slide);
        btnStartSlide = findViewById(R.id.btn_start_slide);
        btnSettingSlide = findViewById(R.id.btn_setting_slide);
        llSlide = findViewById(R.id.ll_slide);
        tvNature = findViewById(R.id.tv_nature);
        tvLocal = findViewById(R.id.tv_local);

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
        currentAlbum = tvNature.getText().toString();

        float curTranslationX = llSlide.getTranslationX();
        animator = ObjectAnimator.ofFloat(llSlide, "translationX", 200f, curTranslationX);
        animator.setDuration(500);

    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.e(TAG, "onStart");
//        rvPhoto.smoothScrollBy(-1, 0);
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

        getAPPData(Contact.SERVER_URL + ":" + Contact.SERVER_PORT + "/" + Contact.GET_NATURE_PHOTO);

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
                if (tvNature.getText().toString().equals(currentAlbum)) {
                    ToastUtils.showToast(MainActivity.this, "正在操作该分类");
//                    Toast.makeText(MainActivity.this, "正在操作该分类", Toast.LENGTH_SHORT).show();
                } else {
                    currentAlbum = tvNature.getText().toString();
                    mList.clear();
//                    mList.add(new PhotoBean("http://img.netbian.com/file/20130316/68888e99d665f2b8dba45e065c60ca42.jpg", "", ""));
//                    mList.add(new PhotoBean("http://img.netbian.com/file/20150417/31bdff0d6c694b93ba462ffb21e8da4b.jpg", "", ""));
//                    mList.add(new PhotoBean("http://img.netbian.com/file/2023/0518/225517rRWjH.jpg", "", ""));
//                    mList.add(new PhotoBean("http://img.netbian.com/file/2023/0527/234811tmIC3.jpg", "", ""));
//                    mList.add(new PhotoBean("http://img.netbian.com/file/2016/0108/86d01043b9b088bc0f833b6167a54528.jpg", "", ""));
//                mList.add(Contact.SERVER_URL + "1.jpg");
                    if (natureList.size() != 0) {
                        mList.addAll(natureList);
                    }
                    adapter.notifyDataSetChanged();
//                    getAPPData(Contact.SERVER_URL + ":" + Contact.SERVER_PORT + "/" + Contact.GET_NATURE_PHOTO);
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
                if (tvLocal.getText().toString().equals(currentAlbum)) {
                    ToastUtils.showToast(MainActivity.this, "正在操作该分类");
//                    Toast.makeText(MainActivity.this, "正在操作该分类", Toast.LENGTH_SHORT).show();
                } else {
                    currentAlbum = tvLocal.getText().toString();
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
                if (llSlide.getVisibility() == View.VISIBLE) {
                    llSlide.setVisibility(View.GONE);
                } else {
                    llSlide.setVisibility(View.VISIBLE);
                    animator.start();
                }
            }
        });

        btnStartSlide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                llSlide.setVisibility(View.GONE);
                if (slideList.size() == 0) {
                    ToastUtils.showToast(MainActivity.this, "请长按图片添加图片到幻灯片");
                } else {
                    Intent intent = new Intent(MainActivity.this, SlideActivity.class);
//                    intent.putParcelableArrayListExtra("slideList", (ArrayList<PhotoBean>) slideList);
                    startActivity(intent);
                }
            }
        });

        btnSettingSlide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                llSlide.setVisibility(View.GONE);
                SlideDialog slideDialog = new SlideDialog(MainActivity.this);
                slideDialog.setConfirmOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String editSpeed = slideDialog.getEditSpeed();
                        String typeId = slideDialog.getTypeText();
                        if (Integer.parseInt(editSpeed) > 20000) {
                            ToastUtils.showToast(MainActivity.this, "超过最大限制，请重新输入！");
                            slideDialog.setEditSpeed();
                        } else {
                            slideDialog.dismiss();
                        }
                        Log.e(TAG, typeId + "");
                        Log.e(TAG, editSpeed);
                    }
                });
                slideDialog.setCancelOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        slideDialog.dismiss();
                    }
                });
                slideDialog.setCancelable(false);
                slideDialog.show();
            }
        });

        btnClearSlide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                llSlide.setVisibility(View.GONE);
                if (slideList.size() != 0) {
                    slideList.clear();
                    adapter.notifyDataSetChanged();
                    MyApplication.setPhotoList(slideList);
                    ToastUtils.showToast(MainActivity.this, "已清空幻灯片，请重新添加");
                }
            }
        });

        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exit();
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
    protected void onRestart() {
        super.onRestart();
        Log.e(TAG, "onRestart");
//        initList();
    }

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

    private void getAPPData(String url) {
        new Thread() {
            @Override
            public void run() {
                super.run();
                //1.创建OkHttpClient对象
                OkHttpClient okHttpClient = new OkHttpClient().newBuilder().connectTimeout(120000, TimeUnit.MILLISECONDS).readTimeout(120000, TimeUnit.MILLISECONDS).build();
                //2.创建Request对象，设置一个url地址,设置请求方式。
                Request request = new Request.Builder().url(url).method("GET",null).build();
                //3.创建一个call对象,参数就是Request请求对象
                Call call = okHttpClient.newCall(request);
                //4.请求加入调度，重写回调方法
                call.enqueue(new Callback() {
                    //请求失败执行的方法
                    @Override
                    public void onFailure(Call call, IOException e) {
                        e.printStackTrace();
                        getAPPData(url);
                        Log.e("TAG", "服务器异常，请求数据失败");
//                        handler.sendEmptyMessageAtTime(0x015, 100);
                    }
                    //请求成功执行的方法
                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
//                        Log.e("TAG", response.body().string());
                        String text = response.body().string();
                        //ArrayList<APPHomeBean> list = new Gson().fromJson(text, new TypeToken<List<APPHomeBean>>() {}.getType());
                        Log.e("TAG", text);
                        Message message = new Message();
                        Bundle bundle = new Bundle();
                        bundle.putString("text", text);
                        bundle.putString("url", url);
                        message.what = GET_NATURE_PHOTO;
                        message.obj = bundle;
                        handler.sendMessageAtTime(message, 100);
                    }
                });
            }
        }.start();
    }

}