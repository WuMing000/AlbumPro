package com.js.photoalbum.activity;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.js.photoalbum.BaseActivity;
import com.js.photoalbum.MyApplication;
import com.js.photoalbum.R;
import com.js.photoalbum.adapter.BottomRecyclerViewAdapter;
import com.js.photoalbum.adapter.PhotoRecyclerViewAdapter;
import com.js.photoalbum.bean.BottomBean;
import com.js.photoalbum.bean.DownBean;
import com.js.photoalbum.bean.DownProgressBean;
import com.js.photoalbum.bean.PhotoBean;
import com.js.photoalbum.bean.PhotoServerBean;
import com.js.photoalbum.bean.SlideTypeBean;
import com.js.photoalbum.manager.CenterZoomLayoutManager;
import com.js.photoalbum.manager.Contact;
import com.js.photoalbum.manager.HorizontalDecoration;
import com.js.photoalbum.utils.CustomUtil;
import com.js.photoalbum.utils.ToastUtils;
import com.js.photoalbum.view.AddPhotoDialog;
import com.js.photoalbum.view.SlideDialog;
import com.js.photoalbum.view.UpdateDialog;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
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

    private static final int GET_PHOTO = 0x002;
    private static final int ADAPTER_CHANGED = 0x003;
    private static final int UPDATE_VERSION_DIFFERENT = 0x004;
    private static final int UPDATE_VERSION_SAME = 0x005;
    private static final int NETWORK_NO_CONNECT = 0x006;
    private static final int DOWNLOAD_ERROR = 0x007;
    private static final int DOWNLOADING_PROGRESS = 0x008;

    private RecyclerView rvPhoto;
    private PhotoRecyclerViewAdapter adapter;
    private List<PhotoBean> mList;
    private List<PhotoBean> localList;
    private List<PhotoBean> natureList;
    private List<PhotoBean> naturePortraitList;
    private List<PhotoBean> girlList;
    private List<PhotoBean> girlPortraitList;
    private List<PhotoBean> plantList;
    private List<PhotoBean> plantPortraitList;
    private List<PhotoBean> scenicList;
    private List<PhotoBean> scenicPortraitList;
    private List<PhotoBean> customList;
    private List<PhotoBean> customPortraitList;
    private List<PhotoBean> skyList;
    private List<PhotoBean> skyPortraitList;
    private List<PhotoBean> cartoonList;
    private List<PhotoBean> cartoonPortraitList;
    private List<PhotoBean> carList;
    private List<PhotoBean> carPortraitList;
    private List<PhotoBean> paintList;
    private List<PhotoBean> paintPortraitList;
    private List<PhotoBean> slideList;

//    private LinearLayout llSlide;
    private Button btnSlide, btnSlideSetting;
    private ImageView ivGaussBlur, ivBack;

    private RecyclerView rvBottom;
    private BottomRecyclerViewAdapter bottomRecyclerViewAdapter;
    private List<BottomBean> bottomBeanList;

    private final float mShrinkAmount = 0.55f;
    private final float mShrinkDistance = 0.9f;

    private boolean isScroll = true;

    private CenterZoomLayoutManager centerZoomLayoutManager;
    private LinearLayoutManager linearLayoutManager;

    private int currentPosition;

    private String currentAlbum;
    private AlertDialog dialog;

    private ObjectAnimator animator;
    private UpdateDialog updateDialog;
    private AddPhotoDialog addPhotoDialog;
    private SlideDialog slideDialog;
    boolean isAdd = false;

    private int currentBottomPosition;

    private boolean isRunStart = false;

//    private int slideSpeed;

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
//                    if (!CustomUtil.isNetworkAvailable(MainActivity.this)) {
//                        mList.addAll(localList);
//                        adapter.notifyDataSetChanged();
//                    }
                    adapter.notifyDataSetChanged();
//                rvPhoto.smoothScrollToPosition(0);
//                }
                    break;
                case GET_PHOTO:
                    Bundle data = (Bundle) msg.obj;
                    String text = data.getString("text");
                    String url = data.getString("url");
                    Log.e(TAG, "text:" + text + ",url:" + url);
                    ArrayList<PhotoServerBean> list = new Gson().fromJson(text, new TypeToken<List<PhotoServerBean>>() {
                    }.getType());
                    Log.e(TAG, "=========" + list.toString());
//                    mList.clear();
                    new Thread() {
                        @Override
                        public void run() {
                            super.run();
                            if ("nature".equals(list.get(0).getPhotoType())) {
                                for (int i = 0; i < list.get(0).getPhotoUrl().split(",").length; i++) {
                                    mList.add(new PhotoBean(list.get(0).getPhotoUrl().split(",")[i], "", ""));
                                    natureList.add(new PhotoBean(list.get(0).getPhotoUrl().split(",")[i], "", ""));
                                    handler.sendEmptyMessageAtTime(ADAPTER_CHANGED, 100);
                                }
                            } else if ("girl".equals(list.get(0).getPhotoType())) {
                                for (int i = 0; i < list.get(0).getPhotoUrl().split(",").length; i++) {
//                                    mList.add(new PhotoBean(list.get(0).getPhotoUrl().split(",")[i], "", ""));
                                    girlList.add(new PhotoBean(list.get(0).getPhotoUrl().split(",")[i], "", ""));
                                    handler.sendEmptyMessageAtTime(ADAPTER_CHANGED, 100);
                                }
                            } else if ("plant".equals(list.get(0).getPhotoType())) {
                                for (int i = 0; i < list.get(0).getPhotoUrl().split(",").length; i++) {
//                                    mList.add(new PhotoBean(list.get(0).getPhotoUrl().split(",")[i], "", ""));
                                    plantList.add(new PhotoBean(list.get(0).getPhotoUrl().split(",")[i], "", ""));
                                    handler.sendEmptyMessageAtTime(ADAPTER_CHANGED, 100);
                                }
                            } else if ("scenic".equals(list.get(0).getPhotoType())) {
                                for (int i = 0; i < list.get(0).getPhotoUrl().split(",").length; i++) {
//                                    mList.add(new PhotoBean(list.get(0).getPhotoUrl().split(",")[i], "", ""));
                                    scenicList.add(new PhotoBean(list.get(0).getPhotoUrl().split(",")[i], "", ""));
                                    handler.sendEmptyMessageAtTime(ADAPTER_CHANGED, 100);
                                }
                            } else if ("custom".equals(list.get(0).getPhotoType())) {
                                for (int i = 0; i < list.get(0).getPhotoUrl().split(",").length; i++) {
//                                    mList.add(new PhotoBean(list.get(0).getPhotoUrl().split(",")[i], "", ""));
                                    customList.add(new PhotoBean(list.get(0).getPhotoUrl().split(",")[i], "", ""));
                                    handler.sendEmptyMessageAtTime(ADAPTER_CHANGED, 100);
                                }
                            } else if ("sky".equals(list.get(0).getPhotoType())) {
                                for (int i = 0; i < list.get(0).getPhotoUrl().split(",").length; i++) {
//                                    mList.add(new PhotoBean(list.get(0).getPhotoUrl().split(",")[i], "", ""));
                                    skyList.add(new PhotoBean(list.get(0).getPhotoUrl().split(",")[i], "", ""));
                                    handler.sendEmptyMessageAtTime(ADAPTER_CHANGED, 100);
                                }
                            } else if ("cartoon".equals(list.get(0).getPhotoType())) {
                                for (int i = 0; i < list.get(0).getPhotoUrl().split(",").length; i++) {
//                                    mList.add(new PhotoBean(list.get(0).getPhotoUrl().split(",")[i], "", ""));
                                    cartoonList.add(new PhotoBean(list.get(0).getPhotoUrl().split(",")[i], "", ""));
                                    handler.sendEmptyMessageAtTime(ADAPTER_CHANGED, 100);
                                }
                            } else if ("car".equals(list.get(0).getPhotoType())) {
                                for (int i = 0; i < list.get(0).getPhotoUrl().split(",").length; i++) {
//                                    mList.add(new PhotoBean(list.get(0).getPhotoUrl().split(",")[i], "", ""));
                                    carList.add(new PhotoBean(list.get(0).getPhotoUrl().split(",")[i], "", ""));
                                    handler.sendEmptyMessageAtTime(ADAPTER_CHANGED, 100);
                                }
                            } else if ("paint".equals(list.get(0).getPhotoType())) {
                                for (int i = 0; i < list.get(0).getPhotoUrl().split(",").length; i++) {
//                                    mList.add(new PhotoBean(list.get(0).getPhotoUrl().split(",")[i], "", ""));
                                    paintList.add(new PhotoBean(list.get(0).getPhotoUrl().split(",")[i], "", ""));
                                    handler.sendEmptyMessageAtTime(ADAPTER_CHANGED, 100);
                                }
                            }
                        }
                    }.start();
                    break;
                case ADAPTER_CHANGED:
                    adapter.notifyDataSetChanged();
                    break;
                case UPDATE_VERSION_DIFFERENT:
                    Log.e(TAG, "版本号不一致");
                    updateDialog = new UpdateDialog(MainActivity.this);
                    updateDialog.setMessage("相册有新版本！！！");
                    updateDialog.setTitleVisible(View.GONE);
                    updateDialog.setExitOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            updateDialog.dismiss();
                        }
                    });
                    updateDialog.setUpdateOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            updateDialog.setProgressVisible(View.VISIBLE);
                            updateDialog.setButtonVisible(View.GONE);
                            new Thread() {
                                @Override
                                public void run() {
                                    super.run();
                                    String serverFile = CustomUtil.getServerFile(Contact.SERVER_URL + ":8080/test/js_project/album/Update.txt");
                                    Log.e(TAG, "serverFile:" + serverFile);
                                    if (serverFile.length() == 0) {
                                        handler.sendEmptyMessageAtTime(NETWORK_NO_CONNECT, 100);
                                    } else {
                                        Message message = new Message();
                                        message.obj = serverFile;
                                        message.what = DOWNLOADING_PROGRESS;
                                        handler.sendMessageAtTime(message, 100);
                                    }
                                }
                            }.start();
                        }
                    });
                    updateDialog.setCancelable(false);
                    updateDialog.show();
                    break;
                case UPDATE_VERSION_SAME:
                    Log.e(TAG, "版本号一致");
                    break;
                case NETWORK_NO_CONNECT:
                    ToastUtils.showToast(MainActivity.this, "网络未连接，请先连接网络");
                    break;
                case DOWNLOAD_ERROR:
                    Toast.makeText(MainActivity.this, "下载异常，已取消下载", Toast.LENGTH_SHORT).show();
                    break;
                case DOWNLOADING_PROGRESS:
                    String downUrl = (String) msg.obj;
                    DownBean downBean = CustomUtil.updateAPK(downUrl);
                    Timer timer = new Timer();
                    timer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            DownProgressBean downProgressBean = CustomUtil.updateProgress(downBean.getDownloadId(), timer);
                            Log.e(TAG, downProgressBean.getProgress());
                            try {
                                float progress = Float.parseFloat(downProgressBean.getProgress());
                                if (progress == 100.00) {
                                    updateDialog.dismiss();
                                }
                                updateDialog.setPbProgress((int) progress);
                                updateDialog.setTvProgress(downProgressBean.getProgress());
                            } catch (Exception e) {
                                e.printStackTrace();
                                updateDialog.dismiss();
                                timer.cancel();
                                DownloadManager manager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
                                manager.remove(downProgressBean.getDownloadId());
                                handler.sendEmptyMessageAtTime(DOWNLOAD_ERROR, 100);
                            }
                        }
                    }, 0, 1000);
                    break;
            }
        }
    };

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        Log.e(TAG, "onConfigurationChanged:position:" + currentBottomPosition);
        Configuration mConfiguration = getResources().getConfiguration(); //获取设置的配置信息
        int ori = mConfiguration.orientation; //获取屏幕方向
        if (ori == Configuration.ORIENTATION_LANDSCAPE) {
            if (currentBottomPosition == 0) {
                mList.clear();
                mList.addAll(natureList);
            } else if (currentBottomPosition == 1) {
                mList.clear();
                mList.addAll(paintList);
            } else if (currentBottomPosition == 2) {
                mList.clear();
                mList.addAll(plantList);
            } else if (currentBottomPosition == 3) {
                mList.clear();
                mList.addAll(skyList);
            } else if (currentBottomPosition == 4) {
                mList.clear();
                mList.addAll(carList);
            }
        } else {
            if (currentBottomPosition == 0) {
                mList.clear();
                mList.addAll(naturePortraitList);
            } else if (currentBottomPosition == 1) {
                mList.clear();
                mList.addAll(paintPortraitList);
            } else if (currentBottomPosition == 2) {
                mList.clear();
                mList.addAll(plantPortraitList);
            } else if (currentBottomPosition == 3) {
                mList.clear();
                mList.addAll(skyPortraitList);
            } else if (currentBottomPosition == 4) {
                mList.clear();
                mList.addAll(carPortraitList);
            }
        }
        rvPhoto.setOnFlingListener(null);
        LinearSnapHelper mLinearSnapHelper = new LinearSnapHelper();//让recyclerview的item居中的方法
        mLinearSnapHelper.attachToRecyclerView(rvPhoto);//将该类绑定到相应的recyclerview上
//        mLinearSnapHelper.calculateScrollDistance(100, 100);
        rvPhoto.addItemDecoration(new HorizontalDecoration(0));
//        adapter = new PhotoRecyclerViewAdapter(MainActivity.this, mList);
        rvPhoto.setAdapter(adapter);
        new Thread() {
            @Override
            public void run() {
                super.run();
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                rvPhoto.smoothScrollBy(1, 0);
            }
        }.start();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mList = new ArrayList<>();
        localList = new ArrayList<>();
        natureList = new ArrayList<>();
        girlList = new ArrayList<>();
        plantList = new ArrayList<>();
        scenicList = new ArrayList<>();
        customList = new ArrayList<>();
        skyList = new ArrayList<>();
        cartoonList = new ArrayList<>();
        carList = new ArrayList<>();
        paintList = new ArrayList<>();
        slideList = new ArrayList<>();
        bottomBeanList = new ArrayList<>();

        naturePortraitList = new ArrayList<>();
        girlPortraitList = new ArrayList<>();
        plantPortraitList = new ArrayList<>();
        scenicPortraitList = new ArrayList<>();
        customPortraitList = new ArrayList<>();
        skyPortraitList = new ArrayList<>();
        cartoonPortraitList = new ArrayList<>();
        carPortraitList = new ArrayList<>();
        paintPortraitList = new ArrayList<>();

        rvPhoto = findViewById(R.id.rv_photo);
        ivGaussBlur = findViewById(R.id.iv_gauss_blur);
        ivBack = findViewById(R.id.iv_back);
        btnSlide = findViewById(R.id.btn_slide);
        btnSlideSetting = findViewById(R.id.btn_slide_setting);
//        btnClearSlide = findViewById(R.id.btn_clear_slide);
//        btnStartSlide = findViewById(R.id.btn_start_slide);
//        btnSettingSlide = findViewById(R.id.btn_setting_slide);
//        llSlide = findViewById(R.id.ll_slide);
        rvBottom = findViewById(R.id.rv_bottom);

//        ivGaussBlur.setImageResource(R.mipmap.nature_default);

        adapter = new PhotoRecyclerViewAdapter(this, mList);
        centerZoomLayoutManager = new CenterZoomLayoutManager(this, RecyclerView.HORIZONTAL, false);
        linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        rvPhoto.setLayoutManager(centerZoomLayoutManager);

        bottomRecyclerViewAdapter = new BottomRecyclerViewAdapter(this, bottomBeanList);
        rvBottom.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));
        rvBottom.setAdapter(bottomRecyclerViewAdapter);

        LinearSnapHelper mLinearSnapHelper = new LinearSnapHelper();//让recyclerview的item居中的方法
        mLinearSnapHelper.attachToRecyclerView(rvPhoto);//将该类绑定到相应的recyclerview上
//        mLinearSnapHelper.calculateScrollDistance(100, 100);
        rvPhoto.addItemDecoration(new HorizontalDecoration(0));
        rvPhoto.setAdapter(adapter);
        rvPhoto.addOnScrollListener(mOnScrollListener);

        currentAlbum = "自然风景";
        currentPosition = 0;

        initListener();

        new Thread() {
            @Override
            public void run() {
                super.run();
                initList();
            }
        }.start();

        new Thread() {
            @Override
            public void run() {
                super.run();
                String serverFile = CustomUtil.getServerFile(Contact.SERVER_URL + ":8080/test/js_project/album/Version.txt");
                if (serverFile.length() == 0) {
                    handler.sendEmptyMessageAtTime(NETWORK_NO_CONNECT, 100);
                    return;
                }
                String localVersionName = CustomUtil.getLocalVersionName();
                if (localVersionName.equals(serverFile)) {
                    handler.sendEmptyMessageAtTime(UPDATE_VERSION_SAME, 100);
                } else {
                    handler.sendEmptyMessageAtTime(UPDATE_VERSION_DIFFERENT, 100);
                }
            }
        }.start();

    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.e(TAG, "onStart");
        isRunStart = true;
//        rvPhoto.smoothScrollBy(-1, 0);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!isRunStart) {
            adapter.notifyDataSetChanged();
            rvPhoto.smoothScrollBy(1, 0);
        }
        slideList = MyApplication.getPhotoList();
    }

    private void initList() {

//        float curTranslationX = llSlide.getTranslationX();
//        animator = ObjectAnimator.ofFloat(llSlide, "translationX", 200f, curTranslationX);
//        animator.setDuration(500);

        bottomBeanList.add(new BottomBean(R.drawable.nature_circle, "自然风景"));
        bottomBeanList.add(new BottomBean(R.drawable.paint_circle, "世界名画"));
        bottomBeanList.add(new BottomBean(R.drawable.plant_circle, "护眼绿色"));
//        bottomBeanList.add(new BottomBean(R.drawable.scenic_circle, "名胜古迹"));
//        bottomBeanList.add(new BottomBean(R.drawable.custom_circle, "风土人情"));
        bottomBeanList.add(new BottomBean(R.drawable.sky_circle, "璀璨星空"));
//        bottomBeanList.add(new BottomBean(R.drawable.cartoon_circle, "热血动漫"));
        bottomBeanList.add(new BottomBean(R.drawable.car_circle, "时尚汽车"));
//        bottomBeanList.add(new BottomBean(R.drawable.paint_circle, "世界名画"));
        bottomBeanList.add(new BottomBean(R.drawable.local_circle, "本地相册"));
        bottomRecyclerViewAdapter.notifyDataSetChanged();

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

//        if (CustomUtil.isNetworkAvailable(this)) {
//            new Thread() {
//                @Override
//                public void run() {
//                    super.run();
//                    getAPPData(Contact.SERVER_URL + ":" + Contact.SERVER_PORT + "/" + Contact.GET_NATURE_PHOTO);
//                    getAPPData(Contact.SERVER_URL + ":" + Contact.SERVER_PORT + "/" + Contact.GET_GIRL_PHOTO);
//                    getAPPData(Contact.SERVER_URL + ":" + Contact.SERVER_PORT + "/" + Contact.GET_PLANT_PHOTO);
//                    getAPPData(Contact.SERVER_URL + ":" + Contact.SERVER_PORT + "/" + Contact.GET_SCENIC_PHOTO);
//                    getAPPData(Contact.SERVER_URL + ":" + Contact.SERVER_PORT + "/" + Contact.GET_CUSTOM_PHOTO);
//                    getAPPData(Contact.SERVER_URL + ":" + Contact.SERVER_PORT + "/" + Contact.GET_SKY_PHOTO);
//                    getAPPData(Contact.SERVER_URL + ":" + Contact.SERVER_PORT + "/" + Contact.GET_CARTOON_PHOTO);
//                    getAPPData(Contact.SERVER_URL + ":" + Contact.SERVER_PORT + "/" + Contact.GET_CAR_PHOTO);
//                    getAPPData(Contact.SERVER_URL + ":" + Contact.SERVER_PORT + "/" + Contact.GET_PAINT_PHOTO);
//                }
//            }.start();
//        } else {
//            bottomBeanList.add(new BottomBean(R.drawable.local_circle, "本地相册"));
//            bottomRecyclerViewAdapter.notifyDataSetChanged();
//        }

        //nature
        natureList.add(new PhotoBean(idToUri(MainActivity.this, R.drawable.nature_1).toString(), "", ""));
        natureList.add(new PhotoBean(idToUri(MainActivity.this, R.drawable.nature_2).toString(), "", ""));
        natureList.add(new PhotoBean(idToUri(MainActivity.this, R.drawable.nature_3).toString(), "", ""));
        natureList.add(new PhotoBean(idToUri(MainActivity.this, R.drawable.nature_4).toString(), "", ""));
        natureList.add(new PhotoBean(idToUri(MainActivity.this, R.drawable.nature_5).toString(), "", ""));
        natureList.add(new PhotoBean(idToUri(MainActivity.this, R.drawable.nature_6).toString(), "", ""));
        natureList.add(new PhotoBean(idToUri(MainActivity.this, R.drawable.nature_7).toString(), "", ""));
        natureList.add(new PhotoBean(idToUri(MainActivity.this, R.drawable.nature_8).toString(), "", ""));
        natureList.add(new PhotoBean(idToUri(MainActivity.this, R.drawable.nature_9).toString(), "", ""));
        natureList.add(new PhotoBean(idToUri(MainActivity.this, R.drawable.nature_10).toString(), "", ""));
        natureList.add(new PhotoBean(idToUri(MainActivity.this, R.drawable.nature_11).toString(), "", ""));
        natureList.add(new PhotoBean(idToUri(MainActivity.this, R.drawable.nature_12).toString(), "", ""));
        natureList.add(new PhotoBean(idToUri(MainActivity.this, R.drawable.nature_13).toString(), "", ""));
        natureList.add(new PhotoBean(idToUri(MainActivity.this, R.drawable.nature_14).toString(), "", ""));
        natureList.add(new PhotoBean(idToUri(MainActivity.this, R.drawable.nature_15).toString(), "", ""));
        natureList.add(new PhotoBean(idToUri(MainActivity.this, R.drawable.nature_16).toString(), "", ""));
        natureList.add(new PhotoBean(idToUri(MainActivity.this, R.drawable.nature_17).toString(), "", ""));
        natureList.add(new PhotoBean(idToUri(MainActivity.this, R.drawable.nature_18).toString(), "", ""));
        natureList.add(new PhotoBean(idToUri(MainActivity.this, R.drawable.nature_19).toString(), "", ""));
        natureList.add(new PhotoBean(idToUri(MainActivity.this, R.drawable.nature_20).toString(), "", ""));
        natureList.add(new PhotoBean(idToUri(MainActivity.this, R.drawable.nature_21).toString(), "", ""));
        natureList.add(new PhotoBean(idToUri(MainActivity.this, R.drawable.nature_22).toString(), "", ""));
        natureList.add(new PhotoBean(idToUri(MainActivity.this, R.drawable.nature_23).toString(), "", ""));
        natureList.add(new PhotoBean(idToUri(MainActivity.this, R.drawable.nature_24).toString(), "", ""));
        natureList.add(new PhotoBean(idToUri(MainActivity.this, R.drawable.nature_25).toString(), "", ""));

        //nature-portrait
        naturePortraitList.add(new PhotoBean(idToUri(MainActivity.this, R.drawable.nature_portrait_1).toString(), "", ""));
        naturePortraitList.add(new PhotoBean(idToUri(MainActivity.this, R.drawable.nature_portrait_2).toString(), "", ""));
        naturePortraitList.add(new PhotoBean(idToUri(MainActivity.this, R.drawable.nature_portrait_3).toString(), "", ""));
        naturePortraitList.add(new PhotoBean(idToUri(MainActivity.this, R.drawable.nature_portrait_4).toString(), "", ""));
        naturePortraitList.add(new PhotoBean(idToUri(MainActivity.this, R.drawable.nature_portrait_5).toString(), "", ""));
        naturePortraitList.add(new PhotoBean(idToUri(MainActivity.this, R.drawable.nature_portrait_6).toString(), "", ""));
        naturePortraitList.add(new PhotoBean(idToUri(MainActivity.this, R.drawable.nature_portrait_7).toString(), "", ""));
        naturePortraitList.add(new PhotoBean(idToUri(MainActivity.this, R.drawable.nature_portrait_8).toString(), "", ""));
        naturePortraitList.add(new PhotoBean(idToUri(MainActivity.this, R.drawable.nature_portrait_9).toString(), "", ""));
        naturePortraitList.add(new PhotoBean(idToUri(MainActivity.this, R.drawable.nature_portrait_10).toString(), "", ""));

        //girl
//        girlList.add(new PhotoBean(idToUri(MainActivity.this, R.drawable.girl_1).toString(), "", ""));
//        girlList.add(new PhotoBean(idToUri(MainActivity.this, R.drawable.girl_2).toString(), "", ""));
//        girlList.add(new PhotoBean(idToUri(MainActivity.this, R.drawable.girl_3).toString(), "", ""));
//        girlList.add(new PhotoBean(idToUri(MainActivity.this, R.drawable.girl_4).toString(), "", ""));
//        girlList.add(new PhotoBean(idToUri(MainActivity.this, R.drawable.girl_5).toString(), "", ""));
//        girlList.add(new PhotoBean(idToUri(MainActivity.this, R.drawable.girl_6).toString(), "", ""));
//        girlList.add(new PhotoBean(idToUri(MainActivity.this, R.drawable.girl_7).toString(), "", ""));
//        girlList.add(new PhotoBean(idToUri(MainActivity.this, R.drawable.girl_8).toString(), "", ""));
//        girlList.add(new PhotoBean(idToUri(MainActivity.this, R.drawable.girl_9).toString(), "", ""));
//        girlList.add(new PhotoBean(idToUri(MainActivity.this, R.drawable.girl_10).toString(), "", ""));
//        girlList.add(new PhotoBean(idToUri(MainActivity.this, R.drawable.girl_11).toString(), "", ""));
//        girlList.add(new PhotoBean(idToUri(MainActivity.this, R.drawable.girl_12).toString(), "", ""));
//        girlList.add(new PhotoBean(idToUri(MainActivity.this, R.drawable.girl_13).toString(), "", ""));
//        girlList.add(new PhotoBean(idToUri(MainActivity.this, R.drawable.girl_14).toString(), "", ""));
//        girlList.add(new PhotoBean(idToUri(MainActivity.this, R.drawable.girl_15).toString(), "", ""));
//        girlList.add(new PhotoBean(idToUri(MainActivity.this, R.drawable.girl_16).toString(), "", ""));
//        girlList.add(new PhotoBean(idToUri(MainActivity.this, R.drawable.girl_17).toString(), "", ""));
//        girlList.add(new PhotoBean(idToUri(MainActivity.this, R.drawable.girl_18).toString(), "", ""));
//        girlList.add(new PhotoBean(idToUri(MainActivity.this, R.drawable.girl_19).toString(), "", ""));
//        girlList.add(new PhotoBean(idToUri(MainActivity.this, R.drawable.girl_20).toString(), "", ""));
//        girlList.add(new PhotoBean(idToUri(MainActivity.this, R.drawable.girl_21).toString(), "", ""));
//        girlList.add(new PhotoBean(idToUri(MainActivity.this, R.drawable.girl_22).toString(), "", ""));
//        girlList.add(new PhotoBean(idToUri(MainActivity.this, R.drawable.girl_23).toString(), "", ""));
//        girlList.add(new PhotoBean(idToUri(MainActivity.this, R.drawable.girl_24).toString(), "", ""));
//        girlList.add(new PhotoBean(idToUri(MainActivity.this, R.drawable.girl_25).toString(), "", ""));

        //paint
        paintList.add(new PhotoBean(idToUri(MainActivity.this, R.drawable.paint_1).toString(), "", ""));
        paintList.add(new PhotoBean(idToUri(MainActivity.this, R.drawable.paint_2).toString(), "", ""));
        paintList.add(new PhotoBean(idToUri(MainActivity.this, R.drawable.paint_3).toString(), "", ""));
        paintList.add(new PhotoBean(idToUri(MainActivity.this, R.drawable.paint_4).toString(), "", ""));
        paintList.add(new PhotoBean(idToUri(MainActivity.this, R.drawable.paint_5).toString(), "", ""));
        paintList.add(new PhotoBean(idToUri(MainActivity.this, R.drawable.paint_6).toString(), "", ""));
        paintList.add(new PhotoBean(idToUri(MainActivity.this, R.drawable.paint_7).toString(), "", ""));
        paintList.add(new PhotoBean(idToUri(MainActivity.this, R.drawable.paint_8).toString(), "", ""));
        paintList.add(new PhotoBean(idToUri(MainActivity.this, R.drawable.paint_9).toString(), "", ""));
        paintList.add(new PhotoBean(idToUri(MainActivity.this, R.drawable.paint_10).toString(), "", ""));
        paintList.add(new PhotoBean(idToUri(MainActivity.this, R.drawable.paint_11).toString(), "", ""));
        paintList.add(new PhotoBean(idToUri(MainActivity.this, R.drawable.paint_12).toString(), "", ""));
        paintList.add(new PhotoBean(idToUri(MainActivity.this, R.drawable.paint_13).toString(), "", ""));
        paintList.add(new PhotoBean(idToUri(MainActivity.this, R.drawable.paint_14).toString(), "", ""));
        paintList.add(new PhotoBean(idToUri(MainActivity.this, R.drawable.paint_15).toString(), "", ""));
        paintList.add(new PhotoBean(idToUri(MainActivity.this, R.drawable.paint_16).toString(), "", ""));
        paintList.add(new PhotoBean(idToUri(MainActivity.this, R.drawable.paint_17).toString(), "", ""));
        paintList.add(new PhotoBean(idToUri(MainActivity.this, R.drawable.paint_18).toString(), "", ""));
        paintList.add(new PhotoBean(idToUri(MainActivity.this, R.drawable.paint_19).toString(), "", ""));
        paintList.add(new PhotoBean(idToUri(MainActivity.this, R.drawable.paint_20).toString(), "", ""));
        paintList.add(new PhotoBean(idToUri(MainActivity.this, R.drawable.paint_21).toString(), "", ""));
        paintList.add(new PhotoBean(idToUri(MainActivity.this, R.drawable.paint_22).toString(), "", ""));
        paintList.add(new PhotoBean(idToUri(MainActivity.this, R.drawable.paint_23).toString(), "", ""));
        paintList.add(new PhotoBean(idToUri(MainActivity.this, R.drawable.paint_24).toString(), "", ""));
        paintList.add(new PhotoBean(idToUri(MainActivity.this, R.drawable.paint_25).toString(), "", ""));

        //paint-portrait
        paintPortraitList.add(new PhotoBean(idToUri(MainActivity.this, R.drawable.paint_portrait_1).toString(), "", ""));
        paintPortraitList.add(new PhotoBean(idToUri(MainActivity.this, R.drawable.paint_portrait_2).toString(), "", ""));
        paintPortraitList.add(new PhotoBean(idToUri(MainActivity.this, R.drawable.paint_portrait_3).toString(), "", ""));
        paintPortraitList.add(new PhotoBean(idToUri(MainActivity.this, R.drawable.paint_portrait_4).toString(), "", ""));
        paintPortraitList.add(new PhotoBean(idToUri(MainActivity.this, R.drawable.paint_portrait_5).toString(), "", ""));
        paintPortraitList.add(new PhotoBean(idToUri(MainActivity.this, R.drawable.paint_portrait_6).toString(), "", ""));
        paintPortraitList.add(new PhotoBean(idToUri(MainActivity.this, R.drawable.paint_portrait_7).toString(), "", ""));
        paintPortraitList.add(new PhotoBean(idToUri(MainActivity.this, R.drawable.paint_portrait_8).toString(), "", ""));
        paintPortraitList.add(new PhotoBean(idToUri(MainActivity.this, R.drawable.paint_portrait_9).toString(), "", ""));
        paintPortraitList.add(new PhotoBean(idToUri(MainActivity.this, R.drawable.paint_portrait_10).toString(), "", ""));

        //green
        plantList.add(new PhotoBean(idToUri(MainActivity.this, R.drawable.green_1).toString(), "", ""));
        plantList.add(new PhotoBean(idToUri(MainActivity.this, R.drawable.green_2).toString(), "", ""));
        plantList.add(new PhotoBean(idToUri(MainActivity.this, R.drawable.green_3).toString(), "", ""));
        plantList.add(new PhotoBean(idToUri(MainActivity.this, R.drawable.green_4).toString(), "", ""));
        plantList.add(new PhotoBean(idToUri(MainActivity.this, R.drawable.green_5).toString(), "", ""));
        plantList.add(new PhotoBean(idToUri(MainActivity.this, R.drawable.green_6).toString(), "", ""));
        plantList.add(new PhotoBean(idToUri(MainActivity.this, R.drawable.green_7).toString(), "", ""));
        plantList.add(new PhotoBean(idToUri(MainActivity.this, R.drawable.green_8).toString(), "", ""));
        plantList.add(new PhotoBean(idToUri(MainActivity.this, R.drawable.green_9).toString(), "", ""));
        plantList.add(new PhotoBean(idToUri(MainActivity.this, R.drawable.green_10).toString(), "", ""));
        plantList.add(new PhotoBean(idToUri(MainActivity.this, R.drawable.green_11).toString(), "", ""));
        plantList.add(new PhotoBean(idToUri(MainActivity.this, R.drawable.green_12).toString(), "", ""));
        plantList.add(new PhotoBean(idToUri(MainActivity.this, R.drawable.green_13).toString(), "", ""));
        plantList.add(new PhotoBean(idToUri(MainActivity.this, R.drawable.green_14).toString(), "", ""));
        plantList.add(new PhotoBean(idToUri(MainActivity.this, R.drawable.green_15).toString(), "", ""));
        plantList.add(new PhotoBean(idToUri(MainActivity.this, R.drawable.green_16).toString(), "", ""));
        plantList.add(new PhotoBean(idToUri(MainActivity.this, R.drawable.green_17).toString(), "", ""));
        plantList.add(new PhotoBean(idToUri(MainActivity.this, R.drawable.green_18).toString(), "", ""));
        plantList.add(new PhotoBean(idToUri(MainActivity.this, R.drawable.green_19).toString(), "", ""));
        plantList.add(new PhotoBean(idToUri(MainActivity.this, R.drawable.green_20).toString(), "", ""));
        plantList.add(new PhotoBean(idToUri(MainActivity.this, R.drawable.green_21).toString(), "", ""));
        plantList.add(new PhotoBean(idToUri(MainActivity.this, R.drawable.green_22).toString(), "", ""));
        plantList.add(new PhotoBean(idToUri(MainActivity.this, R.drawable.green_23).toString(), "", ""));
        plantList.add(new PhotoBean(idToUri(MainActivity.this, R.drawable.green_24).toString(), "", ""));
        plantList.add(new PhotoBean(idToUri(MainActivity.this, R.drawable.green_25).toString(), "", ""));

        //green-portrait
        plantPortraitList.add(new PhotoBean(idToUri(MainActivity.this, R.drawable.green_portrait_1).toString(), "", ""));
        plantPortraitList.add(new PhotoBean(idToUri(MainActivity.this, R.drawable.green_portrait_2).toString(), "", ""));
        plantPortraitList.add(new PhotoBean(idToUri(MainActivity.this, R.drawable.green_portrait_3).toString(), "", ""));
        plantPortraitList.add(new PhotoBean(idToUri(MainActivity.this, R.drawable.green_portrait_4).toString(), "", ""));
        plantPortraitList.add(new PhotoBean(idToUri(MainActivity.this, R.drawable.green_portrait_5).toString(), "", ""));
        plantPortraitList.add(new PhotoBean(idToUri(MainActivity.this, R.drawable.green_portrait_6).toString(), "", ""));
        plantPortraitList.add(new PhotoBean(idToUri(MainActivity.this, R.drawable.green_portrait_7).toString(), "", ""));
        plantPortraitList.add(new PhotoBean(idToUri(MainActivity.this, R.drawable.green_portrait_8).toString(), "", ""));
        plantPortraitList.add(new PhotoBean(idToUri(MainActivity.this, R.drawable.green_portrait_9).toString(), "", ""));
        plantPortraitList.add(new PhotoBean(idToUri(MainActivity.this, R.drawable.green_portrait_10).toString(), "", ""));


        //sky
        skyList.add(new PhotoBean(idToUri(MainActivity.this, R.drawable.sky_1).toString(), "", ""));
        skyList.add(new PhotoBean(idToUri(MainActivity.this, R.drawable.sky_2).toString(), "", ""));
        skyList.add(new PhotoBean(idToUri(MainActivity.this, R.drawable.sky_3).toString(), "", ""));
        skyList.add(new PhotoBean(idToUri(MainActivity.this, R.drawable.sky_4).toString(), "", ""));
        skyList.add(new PhotoBean(idToUri(MainActivity.this, R.drawable.sky_5).toString(), "", ""));
        skyList.add(new PhotoBean(idToUri(MainActivity.this, R.drawable.sky_6).toString(), "", ""));
        skyList.add(new PhotoBean(idToUri(MainActivity.this, R.drawable.sky_7).toString(), "", ""));
        skyList.add(new PhotoBean(idToUri(MainActivity.this, R.drawable.sky_8).toString(), "", ""));
        skyList.add(new PhotoBean(idToUri(MainActivity.this, R.drawable.sky_9).toString(), "", ""));
        skyList.add(new PhotoBean(idToUri(MainActivity.this, R.drawable.sky_10).toString(), "", ""));
        skyList.add(new PhotoBean(idToUri(MainActivity.this, R.drawable.sky_11).toString(), "", ""));
        skyList.add(new PhotoBean(idToUri(MainActivity.this, R.drawable.sky_12).toString(), "", ""));
        skyList.add(new PhotoBean(idToUri(MainActivity.this, R.drawable.sky_13).toString(), "", ""));
        skyList.add(new PhotoBean(idToUri(MainActivity.this, R.drawable.sky_14).toString(), "", ""));
        skyList.add(new PhotoBean(idToUri(MainActivity.this, R.drawable.sky_15).toString(), "", ""));
        skyList.add(new PhotoBean(idToUri(MainActivity.this, R.drawable.sky_16).toString(), "", ""));
        skyList.add(new PhotoBean(idToUri(MainActivity.this, R.drawable.sky_17).toString(), "", ""));
        skyList.add(new PhotoBean(idToUri(MainActivity.this, R.drawable.sky_18).toString(), "", ""));
        skyList.add(new PhotoBean(idToUri(MainActivity.this, R.drawable.sky_19).toString(), "", ""));
        skyList.add(new PhotoBean(idToUri(MainActivity.this, R.drawable.sky_20).toString(), "", ""));
        skyList.add(new PhotoBean(idToUri(MainActivity.this, R.drawable.sky_21).toString(), "", ""));
        skyList.add(new PhotoBean(idToUri(MainActivity.this, R.drawable.sky_22).toString(), "", ""));
        skyList.add(new PhotoBean(idToUri(MainActivity.this, R.drawable.sky_23).toString(), "", ""));
        skyList.add(new PhotoBean(idToUri(MainActivity.this, R.drawable.sky_24).toString(), "", ""));
        skyList.add(new PhotoBean(idToUri(MainActivity.this, R.drawable.sky_25).toString(), "", ""));

        //sky-portrait
        skyPortraitList.add(new PhotoBean(idToUri(MainActivity.this, R.drawable.sky_portrait_1).toString(), "", ""));
        skyPortraitList.add(new PhotoBean(idToUri(MainActivity.this, R.drawable.sky_portrait_2).toString(), "", ""));
        skyPortraitList.add(new PhotoBean(idToUri(MainActivity.this, R.drawable.sky_portrait_3).toString(), "", ""));
        skyPortraitList.add(new PhotoBean(idToUri(MainActivity.this, R.drawable.sky_portrait_4).toString(), "", ""));
        skyPortraitList.add(new PhotoBean(idToUri(MainActivity.this, R.drawable.sky_portrait_5).toString(), "", ""));
        skyPortraitList.add(new PhotoBean(idToUri(MainActivity.this, R.drawable.sky_portrait_6).toString(), "", ""));
        skyPortraitList.add(new PhotoBean(idToUri(MainActivity.this, R.drawable.sky_portrait_7).toString(), "", ""));
        skyPortraitList.add(new PhotoBean(idToUri(MainActivity.this, R.drawable.sky_portrait_8).toString(), "", ""));
        skyPortraitList.add(new PhotoBean(idToUri(MainActivity.this, R.drawable.sky_portrait_9).toString(), "", ""));
        skyPortraitList.add(new PhotoBean(idToUri(MainActivity.this, R.drawable.sky_portrait_10).toString(), "", ""));

        //car
        carList.add(new PhotoBean(idToUri(MainActivity.this, R.drawable.car_1).toString(), "", ""));
        carList.add(new PhotoBean(idToUri(MainActivity.this, R.drawable.car_2).toString(), "", ""));
        carList.add(new PhotoBean(idToUri(MainActivity.this, R.drawable.car_3).toString(), "", ""));
        carList.add(new PhotoBean(idToUri(MainActivity.this, R.drawable.car_4).toString(), "", ""));
        carList.add(new PhotoBean(idToUri(MainActivity.this, R.drawable.car_5).toString(), "", ""));
        carList.add(new PhotoBean(idToUri(MainActivity.this, R.drawable.car_6).toString(), "", ""));
        carList.add(new PhotoBean(idToUri(MainActivity.this, R.drawable.car_7).toString(), "", ""));
        carList.add(new PhotoBean(idToUri(MainActivity.this, R.drawable.car_8).toString(), "", ""));
        carList.add(new PhotoBean(idToUri(MainActivity.this, R.drawable.car_9).toString(), "", ""));
        carList.add(new PhotoBean(idToUri(MainActivity.this, R.drawable.car_10).toString(), "", ""));
        carList.add(new PhotoBean(idToUri(MainActivity.this, R.drawable.car_11).toString(), "", ""));
        carList.add(new PhotoBean(idToUri(MainActivity.this, R.drawable.car_12).toString(), "", ""));
        carList.add(new PhotoBean(idToUri(MainActivity.this, R.drawable.car_13).toString(), "", ""));
        carList.add(new PhotoBean(idToUri(MainActivity.this, R.drawable.car_14).toString(), "", ""));
        carList.add(new PhotoBean(idToUri(MainActivity.this, R.drawable.car_15).toString(), "", ""));
        carList.add(new PhotoBean(idToUri(MainActivity.this, R.drawable.car_16).toString(), "", ""));
        carList.add(new PhotoBean(idToUri(MainActivity.this, R.drawable.car_17).toString(), "", ""));
        carList.add(new PhotoBean(idToUri(MainActivity.this, R.drawable.car_18).toString(), "", ""));
        carList.add(new PhotoBean(idToUri(MainActivity.this, R.drawable.car_19).toString(), "", ""));
        carList.add(new PhotoBean(idToUri(MainActivity.this, R.drawable.car_20).toString(), "", ""));
        carList.add(new PhotoBean(idToUri(MainActivity.this, R.drawable.car_21).toString(), "", ""));
        carList.add(new PhotoBean(idToUri(MainActivity.this, R.drawable.car_22).toString(), "", ""));
        carList.add(new PhotoBean(idToUri(MainActivity.this, R.drawable.car_23).toString(), "", ""));
        carList.add(new PhotoBean(idToUri(MainActivity.this, R.drawable.car_24).toString(), "", ""));
        carList.add(new PhotoBean(idToUri(MainActivity.this, R.drawable.car_25).toString(), "", ""));

        //car-portrait
        carPortraitList.add(new PhotoBean(idToUri(MainActivity.this, R.drawable.car_portrait_1).toString(), "", ""));
        carPortraitList.add(new PhotoBean(idToUri(MainActivity.this, R.drawable.car_portrait_2).toString(), "", ""));
        carPortraitList.add(new PhotoBean(idToUri(MainActivity.this, R.drawable.car_portrait_3).toString(), "", ""));
        carPortraitList.add(new PhotoBean(idToUri(MainActivity.this, R.drawable.car_portrait_4).toString(), "", ""));
        carPortraitList.add(new PhotoBean(idToUri(MainActivity.this, R.drawable.car_portrait_5).toString(), "", ""));
        carPortraitList.add(new PhotoBean(idToUri(MainActivity.this, R.drawable.car_portrait_6).toString(), "", ""));
        carPortraitList.add(new PhotoBean(idToUri(MainActivity.this, R.drawable.car_portrait_7).toString(), "", ""));
        carPortraitList.add(new PhotoBean(idToUri(MainActivity.this, R.drawable.car_portrait_8).toString(), "", ""));
        carPortraitList.add(new PhotoBean(idToUri(MainActivity.this, R.drawable.car_portrait_9).toString(), "", ""));
        carPortraitList.add(new PhotoBean(idToUri(MainActivity.this, R.drawable.car_portrait_10).toString(), "", ""));

        Configuration mConfiguration = getResources().getConfiguration();
        int ori = mConfiguration.orientation; //获取屏幕方向
        if (ori == Configuration.ORIENTATION_LANDSCAPE) {
            mList.addAll(natureList);
        } else {
            mList.addAll(naturePortraitList);
        }
        handler.sendEmptyMessageAtTime(ADAPTER_CHANGED, 100);

    }

    public static final String RESOURCE = "android.resource://";

    public static Uri idToUri(Context context, int resourceId) {
        return Uri.parse(RESOURCE + context.getPackageName() + "/" + resourceId);
    }

    private void initListener() {
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

        bottomRecyclerViewAdapter.setOnItemClickListener(new BottomRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onClick(int position) {
                Configuration mConfiguration = getResources().getConfiguration(); //获取设置的配置信息
                int ori = mConfiguration.orientation; //获取屏幕方向
                currentBottomPosition = position;
                rvPhoto.smoothScrollToPosition(0);
                if ("自然风景".equals(bottomBeanList.get(position).getBottomName())) {
                    ToastUtils.cancelToast();
                    if (bottomBeanList.get(position).getBottomName().equals(currentAlbum)) {
                        ToastUtils.showToast(MainActivity.this, "正在操作该分类");
//                    Toast.makeText(MainActivity.this, "正在操作该分类", Toast.LENGTH_SHORT).show();
                    } else {
                        currentAlbum = bottomBeanList.get(position).getBottomName();
                        mList.clear();
//                    mList.add(new PhotoBean("http://img.netbian.com/file/20130316/68888e99d665f2b8dba45e065c60ca42.jpg", "", ""));
//                    mList.add(new PhotoBean("http://img.netbian.com/file/20150417/31bdff0d6c694b93ba462ffb21e8da4b.jpg", "", ""));
//                    mList.add(new PhotoBean("http://img.netbian.com/file/2023/0518/225517rRWjH.jpg", "", ""));
//                    mList.add(new PhotoBean("http://img.netbian.com/file/2023/0527/234811tmIC3.jpg", "", ""));
//                    mList.add(new PhotoBean("http://img.netbian.com/file/2016/0108/86d01043b9b088bc0f833b6167a54528.jpg", "", ""));
//                mList.add(Contact.SERVER_URL + "1.jpg");
                        if (natureList.size() != 0) {
                            if (ori == Configuration.ORIENTATION_LANDSCAPE) {
                                mList.addAll(natureList);
                            } else {
                                mList.addAll(naturePortraitList);
                            }
                        }
                        adapter.notifyDataSetChanged();
//                    getAPPData(Contact.SERVER_URL + ":" + Contact.SERVER_PORT + "/" + Contact.GET_PHOTO);
                        if (currentPosition == 0) {
                            rvPhoto.smoothScrollToPosition(0);
                            rvPhoto.smoothScrollBy(-480 * mList.size() * mList.size(), 0);
                        } else {
                            rvPhoto.smoothScrollBy(-480 * mList.size() * mList.size(), 0);
                        }
                    }
                } else if ("养眼美女".equals(bottomBeanList.get(position).getBottomName())) {
                    ToastUtils.cancelToast();
                    if (bottomBeanList.get(position).getBottomName().equals(currentAlbum)) {
                        ToastUtils.showToast(MainActivity.this, "正在操作该分类");
//                    Toast.makeText(MainActivity.this, "正在操作该分类", Toast.LENGTH_SHORT).show();
                    } else {
                        currentAlbum = bottomBeanList.get(position).getBottomName();
                        mList.clear();
//                    mList.add(new PhotoBean("http://img.netbian.com/file/20130316/68888e99d665f2b8dba45e065c60ca42.jpg", "", ""));
//                    mList.add(new PhotoBean("http://img.netbian.com/file/20150417/31bdff0d6c694b93ba462ffb21e8da4b.jpg", "", ""));
//                    mList.add(new PhotoBean("http://img.netbian.com/file/2023/0518/225517rRWjH.jpg", "", ""));
//                    mList.add(new PhotoBean("http://img.netbian.com/file/2023/0527/234811tmIC3.jpg", "", ""));
//                    mList.add(new PhotoBean("http://img.netbian.com/file/2016/0108/86d01043b9b088bc0f833b6167a54528.jpg", "", ""));
//                mList.add(Contact.SERVER_URL + "1.jpg");
                        if (girlList.size() != 0) {
                            mList.addAll(girlList);
                        }
                        adapter.notifyDataSetChanged();
//                    getAPPData(Contact.SERVER_URL + ":" + Contact.SERVER_PORT + "/" + Contact.GET_PHOTO);
                        if (currentPosition == 0) {
                            rvPhoto.smoothScrollToPosition(0);
                            rvPhoto.smoothScrollBy(-480 * mList.size() * mList.size(), 0);
                        } else {
                            rvPhoto.smoothScrollBy(-480 * mList.size() * mList.size(), 0);
                        }
                    }
                } else if ("护眼绿色".equals(bottomBeanList.get(position).getBottomName())) {
                    ToastUtils.cancelToast();
                    if (bottomBeanList.get(position).getBottomName().equals(currentAlbum)) {
                        ToastUtils.showToast(MainActivity.this, "正在操作该分类");
//                    Toast.makeText(MainActivity.this, "正在操作该分类", Toast.LENGTH_SHORT).show();
                    } else {
                        currentAlbum = bottomBeanList.get(position).getBottomName();
                        mList.clear();
//                    mList.add(new PhotoBean("http://img.netbian.com/file/20130316/68888e99d665f2b8dba45e065c60ca42.jpg", "", ""));
//                    mList.add(new PhotoBean("http://img.netbian.com/file/20150417/31bdff0d6c694b93ba462ffb21e8da4b.jpg", "", ""));
//                    mList.add(new PhotoBean("http://img.netbian.com/file/2023/0518/225517rRWjH.jpg", "", ""));
//                    mList.add(new PhotoBean("http://img.netbian.com/file/2023/0527/234811tmIC3.jpg", "", ""));
//                    mList.add(new PhotoBean("http://img.netbian.com/file/2016/0108/86d01043b9b088bc0f833b6167a54528.jpg", "", ""));
//                mList.add(Contact.SERVER_URL + "1.jpg");
                        if (plantList.size() != 0) {
                            if (ori == Configuration.ORIENTATION_LANDSCAPE) {
                                mList.addAll(plantList);
                            } else {
                                mList.addAll(plantPortraitList);
                            }
                        }
                        adapter.notifyDataSetChanged();
//                    getAPPData(Contact.SERVER_URL + ":" + Contact.SERVER_PORT + "/" + Contact.GET_PHOTO);
                        if (currentPosition == 0) {
                            rvPhoto.smoothScrollToPosition(0);
                            rvPhoto.smoothScrollBy(-480 * mList.size() * mList.size(), 0);
                        } else {
                            rvPhoto.smoothScrollBy(-480 * mList.size() * mList.size(), 0);
                        }
                    }
                }
//                else if ("名胜古迹".equals(bottomBeanList.get(position).getBottomName())) {
//                    ToastUtils.cancelToast();
//                    if (bottomBeanList.get(position).getBottomName().equals(currentAlbum)) {
//                        ToastUtils.showToast(MainActivity.this, "正在操作该分类");
////                    Toast.makeText(MainActivity.this, "正在操作该分类", Toast.LENGTH_SHORT).show();
//                    } else {
//                        currentAlbum = bottomBeanList.get(position).getBottomName();
//                        mList.clear();
////                    mList.add(new PhotoBean("http://img.netbian.com/file/20130316/68888e99d665f2b8dba45e065c60ca42.jpg", "", ""));
////                    mList.add(new PhotoBean("http://img.netbian.com/file/20150417/31bdff0d6c694b93ba462ffb21e8da4b.jpg", "", ""));
////                    mList.add(new PhotoBean("http://img.netbian.com/file/2023/0518/225517rRWjH.jpg", "", ""));
////                    mList.add(new PhotoBean("http://img.netbian.com/file/2023/0527/234811tmIC3.jpg", "", ""));
////                    mList.add(new PhotoBean("http://img.netbian.com/file/2016/0108/86d01043b9b088bc0f833b6167a54528.jpg", "", ""));
////                mList.add(Contact.SERVER_URL + "1.jpg");
//                        if (scenicList.size() != 0) {
//                            mList.addAll(scenicList);
//                        }
//                        adapter.notifyDataSetChanged();
////                    getAPPData(Contact.SERVER_URL + ":" + Contact.SERVER_PORT + "/" + Contact.GET_PHOTO);
//                        if (currentPosition == 0) {
//                            rvPhoto.smoothScrollToPosition(0);
//                            rvPhoto.smoothScrollBy(-480 * mList.size() * mList.size(), 0);
//                        } else {
//                            rvPhoto.smoothScrollBy(-480 * mList.size() * mList.size(), 0);
//                        }
//                    }
//                }
//                else if ("风土人情".equals(bottomBeanList.get(position).getBottomName())) {
//                    ToastUtils.cancelToast();
//                    if (bottomBeanList.get(position).getBottomName().equals(currentAlbum)) {
//                        ToastUtils.showToast(MainActivity.this, "正在操作该分类");
////                    Toast.makeText(MainActivity.this, "正在操作该分类", Toast.LENGTH_SHORT).show();
//                    } else {
//                        currentAlbum = bottomBeanList.get(position).getBottomName();
//                        mList.clear();
////                    mList.add(new PhotoBean("http://img.netbian.com/file/20130316/68888e99d665f2b8dba45e065c60ca42.jpg", "", ""));
////                    mList.add(new PhotoBean("http://img.netbian.com/file/20150417/31bdff0d6c694b93ba462ffb21e8da4b.jpg", "", ""));
////                    mList.add(new PhotoBean("http://img.netbian.com/file/2023/0518/225517rRWjH.jpg", "", ""));
////                    mList.add(new PhotoBean("http://img.netbian.com/file/2023/0527/234811tmIC3.jpg", "", ""));
////                    mList.add(new PhotoBean("http://img.netbian.com/file/2016/0108/86d01043b9b088bc0f833b6167a54528.jpg", "", ""));
////                mList.add(Contact.SERVER_URL + "1.jpg");
//                        if (customList.size() != 0) {
//                            mList.addAll(customList);
//                        }
//                        adapter.notifyDataSetChanged();
////                    getAPPData(Contact.SERVER_URL + ":" + Contact.SERVER_PORT + "/" + Contact.GET_PHOTO);
//                        if (currentPosition == 0) {
//                            rvPhoto.smoothScrollToPosition(0);
//                            rvPhoto.smoothScrollBy(-480 * mList.size() * mList.size(), 0);
//                        } else {
//                            rvPhoto.smoothScrollBy(-480 * mList.size() * mList.size(), 0);
//                        }
//                    }
//                }
                else if ("璀璨星空".equals(bottomBeanList.get(position).getBottomName())) {
                    ToastUtils.cancelToast();
                    if (bottomBeanList.get(position).getBottomName().equals(currentAlbum)) {
                        ToastUtils.showToast(MainActivity.this, "正在操作该分类");
//                    Toast.makeText(MainActivity.this, "正在操作该分类", Toast.LENGTH_SHORT).show();
                    } else {
                        currentAlbum = bottomBeanList.get(position).getBottomName();
                        mList.clear();
//                    mList.add(new PhotoBean("http://img.netbian.com/file/20130316/68888e99d665f2b8dba45e065c60ca42.jpg", "", ""));
//                    mList.add(new PhotoBean("http://img.netbian.com/file/20150417/31bdff0d6c694b93ba462ffb21e8da4b.jpg", "", ""));
//                    mList.add(new PhotoBean("http://img.netbian.com/file/2023/0518/225517rRWjH.jpg", "", ""));
//                    mList.add(new PhotoBean("http://img.netbian.com/file/2023/0527/234811tmIC3.jpg", "", ""));
//                    mList.add(new PhotoBean("http://img.netbian.com/file/2016/0108/86d01043b9b088bc0f833b6167a54528.jpg", "", ""));
//                mList.add(Contact.SERVER_URL + "1.jpg");
                        if (skyList.size() != 0) {
                            if (ori == Configuration.ORIENTATION_LANDSCAPE) {
                                mList.addAll(skyList);
                            } else {
                                mList.addAll(skyPortraitList);
                            }
                        }
                        adapter.notifyDataSetChanged();
//                    getAPPData(Contact.SERVER_URL + ":" + Contact.SERVER_PORT + "/" + Contact.GET_PHOTO);
                        if (currentPosition == 0) {
                            rvPhoto.smoothScrollToPosition(0);
                            rvPhoto.smoothScrollBy(-480 * mList.size() * mList.size(), 0);
                        } else {
                            rvPhoto.smoothScrollBy(-480 * mList.size() * mList.size(), 0);
                        }
                    }
                } else if ("热血动漫".equals(bottomBeanList.get(position).getBottomName())) {
                    ToastUtils.cancelToast();
                    if (bottomBeanList.get(position).getBottomName().equals(currentAlbum)) {
                        ToastUtils.showToast(MainActivity.this, "正在操作该分类");
//                    Toast.makeText(MainActivity.this, "正在操作该分类", Toast.LENGTH_SHORT).show();
                    } else {
                        currentAlbum = bottomBeanList.get(position).getBottomName();
                        mList.clear();
//                    mList.add(new PhotoBean("http://img.netbian.com/file/20130316/68888e99d665f2b8dba45e065c60ca42.jpg", "", ""));
//                    mList.add(new PhotoBean("http://img.netbian.com/file/20150417/31bdff0d6c694b93ba462ffb21e8da4b.jpg", "", ""));
//                    mList.add(new PhotoBean("http://img.netbian.com/file/2023/0518/225517rRWjH.jpg", "", ""));
//                    mList.add(new PhotoBean("http://img.netbian.com/file/2023/0527/234811tmIC3.jpg", "", ""));
//                    mList.add(new PhotoBean("http://img.netbian.com/file/2016/0108/86d01043b9b088bc0f833b6167a54528.jpg", "", ""));
//                mList.add(Contact.SERVER_URL + "1.jpg");
                        if (cartoonList.size() != 0) {
                            if (ori == Configuration.ORIENTATION_LANDSCAPE) {
                                mList.addAll(cartoonList);
                            } else {
                                mList.addAll(cartoonPortraitList);
                            }
                        }
                        adapter.notifyDataSetChanged();
//                    getAPPData(Contact.SERVER_URL + ":" + Contact.SERVER_PORT + "/" + Contact.GET_PHOTO);
                        if (currentPosition == 0) {
                            rvPhoto.smoothScrollToPosition(0);
                            rvPhoto.smoothScrollBy(-480 * mList.size() * mList.size(), 0);
                        } else {
                            rvPhoto.smoothScrollBy(-480 * mList.size() * mList.size(), 0);
                        }
                    }
                } else if ("时尚汽车".equals(bottomBeanList.get(position).getBottomName())) {
                    ToastUtils.cancelToast();
                    if (bottomBeanList.get(position).getBottomName().equals(currentAlbum)) {
                        ToastUtils.showToast(MainActivity.this, "正在操作该分类");
//                    Toast.makeText(MainActivity.this, "正在操作该分类", Toast.LENGTH_SHORT).show();
                    } else {
                        currentAlbum = bottomBeanList.get(position).getBottomName();
                        mList.clear();
//                    mList.add(new PhotoBean("http://img.netbian.com/file/20130316/68888e99d665f2b8dba45e065c60ca42.jpg", "", ""));
//                    mList.add(new PhotoBean("http://img.netbian.com/file/20150417/31bdff0d6c694b93ba462ffb21e8da4b.jpg", "", ""));
//                    mList.add(new PhotoBean("http://img.netbian.com/file/2023/0518/225517rRWjH.jpg", "", ""));
//                    mList.add(new PhotoBean("http://img.netbian.com/file/2023/0527/234811tmIC3.jpg", "", ""));
//                    mList.add(new PhotoBean("http://img.netbian.com/file/2016/0108/86d01043b9b088bc0f833b6167a54528.jpg", "", ""));
//                mList.add(Contact.SERVER_URL + "1.jpg");
                        if (carList.size() != 0) {
                            if (ori == Configuration.ORIENTATION_LANDSCAPE) {
                                mList.addAll(carList);
                            } else {
                                mList.addAll(carPortraitList);
                            }
                        }
                        adapter.notifyDataSetChanged();
//                    getAPPData(Contact.SERVER_URL + ":" + Contact.SERVER_PORT + "/" + Contact.GET_PHOTO);
                        if (currentPosition == 0) {
                            rvPhoto.smoothScrollToPosition(0);
                            rvPhoto.smoothScrollBy(-480 * mList.size() * mList.size(), 0);
                        } else {
                            rvPhoto.smoothScrollBy(-480 * mList.size() * mList.size(), 0);
                        }
                    }
                } else if ("世界名画".equals(bottomBeanList.get(position).getBottomName())) {
                    ToastUtils.cancelToast();
                    if (bottomBeanList.get(position).getBottomName().equals(currentAlbum)) {
                        ToastUtils.showToast(MainActivity.this, "正在操作该分类");
//                    Toast.makeText(MainActivity.this, "正在操作该分类", Toast.LENGTH_SHORT).show();
                    } else {
                        currentAlbum = bottomBeanList.get(position).getBottomName();
                        mList.clear();
//                    mList.add(new PhotoBean("http://img.netbian.com/file/20130316/68888e99d665f2b8dba45e065c60ca42.jpg", "", ""));
//                    mList.add(new PhotoBean("http://img.netbian.com/file/20150417/31bdff0d6c694b93ba462ffb21e8da4b.jpg", "", ""));
//                    mList.add(new PhotoBean("http://img.netbian.com/file/2023/0518/225517rRWjH.jpg", "", ""));
//                    mList.add(new PhotoBean("http://img.netbian.com/file/2023/0527/234811tmIC3.jpg", "", ""));
//                    mList.add(new PhotoBean("http://img.netbian.com/file/2016/0108/86d01043b9b088bc0f833b6167a54528.jpg", "", ""));
//                mList.add(Contact.SERVER_URL + "1.jpg");
                        if (paintList.size() != 0) {
                            if (ori == Configuration.ORIENTATION_LANDSCAPE) {
                                mList.addAll(paintList);
                            } else {
                                mList.addAll(paintPortraitList);
                            }
                        }
                        adapter.notifyDataSetChanged();
//                    getAPPData(Contact.SERVER_URL + ":" + Contact.SERVER_PORT + "/" + Contact.GET_PHOTO);
                        if (currentPosition == 0) {
                            rvPhoto.smoothScrollToPosition(0);
                            rvPhoto.smoothScrollBy(-480 * mList.size() * mList.size(), 0);
                        } else {
                            rvPhoto.smoothScrollBy(-480 * mList.size() * mList.size(), 0);
                        }
                    }
                } else if ("本地相册".equals(bottomBeanList.get(position).getBottomName())) {
                    ToastUtils.cancelToast();
                    if (bottomBeanList.get(position).getBottomName().equals(currentAlbum)) {
                        ToastUtils.showToast(MainActivity.this, "正在操作该分类");
//                    Toast.makeText(MainActivity.this, "正在操作该分类", Toast.LENGTH_SHORT).show();
                    } else {
                        currentAlbum = bottomBeanList.get(position).getBottomName();
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
            }
        });

        bottomRecyclerViewAdapter.setOnLongItemClickListener(new BottomRecyclerViewAdapter.OnLongItemClickListener() {
            @Override
            public void onClick(int position) {
//                Toast.makeText(MainActivity.this, "你长按了" + position, Toast.LENGTH_SHORT).show();
            }
        });

        adapter.setOnItemLongClickListener(new PhotoRecyclerViewAdapter.OnItemLongClickListener() {
            @Override
            public void onClick(PhotoBean photoBean, ImageView imageView) {
                isAdd = false;
                addPhotoDialog = new AddPhotoDialog(MainActivity.this);
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
                            imageView.setVisibility(View.GONE);
                        } else {
                            slideList.add(photoBean);
                            MyApplication.setPhotoList(slideList);
                            imageView.setVisibility(View.VISIBLE);
                        }
                    }
                });
                addPhotoDialog.setCLearOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        addPhotoDialog.dismiss();
                        if (slideList.size() != 0) {
                            slideList.clear();
                            adapter.notifyDataSetChanged();
                            rvPhoto.smoothScrollBy(-10, 0);
                            MyApplication.setPhotoList(slideList);
                            ToastUtils.showToast(MainActivity.this, "已清空画框，请重新添加");
                        } else {
                            ToastUtils.showToast(MainActivity.this, "画框为空，无法进行操作");
                        }
                    }
                });
                addPhotoDialog.show();

            }
        });

        btnSlide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if (llSlide.getVisibility() == View.VISIBLE) {
//                    llSlide.setVisibility(View.GONE);
//                } else {
//                    llSlide.setVisibility(View.VISIBLE);
//                    animator.start();
//                }
                if (slideList.size() == 0) {
                    ToastUtils.showToast(MainActivity.this, "请长按图片添加到画框");
                } else {
                    Intent intent = new Intent(MainActivity.this, SlideActivity.class);
//                    intent.putExtra("slideSpeed", slideSpeed);
//                    intent.putParcelableArrayListExtra("slideList", (ArrayList<PhotoBean>) slideList);
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_in_from_bottom, 0);
                }
            }
        });

//        btnStartSlide.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                llSlide.setVisibility(View.GONE);
//                if (slideList.size() == 0) {
//                    ToastUtils.showToast(MainActivity.this, "请长按图片添加到画框");
//                } else {
//                    Intent intent = new Intent(MainActivity.this, SlideActivity.class);
////                    intent.putExtra("slideSpeed", slideSpeed);
////                    intent.putParcelableArrayListExtra("slideList", (ArrayList<PhotoBean>) slideList);
//                    startActivity(intent);
//                    overridePendingTransition(R.anim.slide_in_from_bottom, 0);
//                }
//            }
//        });

        btnSlideSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                llSlide.setVisibility(View.GONE);
                slideDialog = new SlideDialog(MainActivity.this);
                Window window = slideDialog.getWindow();
                window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                slideDialog.setConfirmOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String editSpeed = slideDialog.getEditSpeed();
                        SlideTypeBean slideType = slideDialog.getSlideType();
                        MyApplication.setSlideType(slideType);
                        Log.e(TAG, slideType.getSlideId() + "," + slideType.getSlideType());
                        if (editSpeed == null) {
                            Log.d(TAG, "edittext is null!");
                        } else {
                            slideDialog.dismiss();
                            Log.e(TAG, editSpeed);
                            MyApplication.setSlideSpeed(editSpeed);
//                            slideSpeed = Integer.parseInt(editSpeed);
                        }
                    }
                });
                slideDialog.setCancelOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        slideDialog.dismiss();
                    }
                });
//                slideDialog.setCancelable(false);
                slideDialog.show();
            }
        });

//        btnClearSlide.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                llSlide.setVisibility(View.GONE);
//                if (slideList.size() != 0) {
//                    slideList.clear();
//                    adapter.notifyDataSetChanged();
//                    rvPhoto.smoothScrollBy(-10, 0);
//                    MyApplication.setPhotoList(slideList);
//                    ToastUtils.showToast(MainActivity.this, "已清空画框，请重新添加");
//                } else {
//                    ToastUtils.showToast(MainActivity.this, "画框为空，无法进行操作");
//                }
//            }
//        });

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
        adapter.notifyDataSetChanged();
        rvPhoto.smoothScrollBy(-10, 0);
//        initList();
    }

    @Override
    protected void onPause() {
        super.onPause();
        isRunStart = false;
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.e(TAG, "onStop");
        if (dialog != null) {
            dialog.dismiss();
        }
        if (slideDialog != null) {
            slideDialog.dismiss();
        }
        if (addPhotoDialog != null) {
            addPhotoDialog.dismiss();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.e(TAG, "onDestroy");
//        if (mList.size() != 0) {
//            mList.clear();
//            isScroll = true;
//        }
        if (mOnScrollListener != null) {
            rvPhoto.removeOnScrollListener(mOnScrollListener);
        }
        if (updateDialog != null) {
            updateDialog.dismiss();
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
                        message.what = GET_PHOTO;
                        message.obj = bundle;
                        handler.sendMessageAtTime(message, 100);
                    }
                });
            }
        }.start();
    }

//    /**
//     * 使editText点击外部时候失去焦点
//     *
//     * @param ev 触屏事件
//     * @return 事件是否被消费
//     */
//    @Override
//    public boolean dispatchTouchEvent(MotionEvent ev) {
//        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
//            if (!CustomUtil.isTouchPointInView(llSlide, (int) ev.getX(), (int) ev.getY())) {
//                llSlide.setVisibility(View.GONE);
//            }
//            return super.dispatchTouchEvent(ev);
//        }
//        // 必不可少，否则所有的组件都不会有TouchEvent了
//        return getWindow().superDispatchTouchEvent(ev) || onTouchEvent(ev);
//    }
}