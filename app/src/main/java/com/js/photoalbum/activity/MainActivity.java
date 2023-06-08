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

    private RecyclerView rvPhoto;
    private PhotoRecyclerViewAdapter adapter;
    private List<PhotoBean> mList;
    private List<PhotoBean> localList;
    private List<PhotoBean> natureList;
    private List<PhotoBean> girlList;
    private List<PhotoBean> plantList;
    private List<PhotoBean> scenicList;
    private List<PhotoBean> customList;
    private List<PhotoBean> skyList;
    private List<PhotoBean> cartoonList;
    private List<PhotoBean> carList;
    private List<PhotoBean> paintList;
    private List<PhotoBean> slideList;

    private LinearLayout llSlide;
    private Button btnSlide, btnClearSlide, btnStartSlide, btnSettingSlide;
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
                    if (!CustomUtil.isNetworkAvailable(MainActivity.this)) {
                        mList.addAll(localList);
                        adapter.notifyDataSetChanged();
                    }
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
                            CustomUtil.killAppProcess();
                        }
                    });
                    updateDialog.setUpdateOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            updateDialog.setProgressVisible(View.VISIBLE);
                            updateDialog.setButtonVisible(View.GONE);
                            DownBean downBean = CustomUtil.updateAPK(Contact.SERVER_URL + ":8080/test/js_project/album/js_photoalbum.apk");
                            Timer timer = new Timer();
                            timer.schedule(new TimerTask() {
                                @Override
                                public void run() {
                                    DownProgressBean downProgressBean = CustomUtil.updateProgress(downBean.getDownloadId(), timer);
                                    Log.e(TAG, downProgressBean.getProgress());
                                    float progress = Float.parseFloat(downProgressBean.getProgress());
                                    if (progress == 100.00) {
                                        updateDialog.dismiss();
                                    }
                                    updateDialog.setPbProgress((int) progress);
                                    updateDialog.setTvProgress(downProgressBean.getProgress());
                                }
                            }, 0, 1000);
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
        rvPhoto = findViewById(R.id.rv_photo);
        ivGaussBlur = findViewById(R.id.iv_gauss_blur);
        ivBack = findViewById(R.id.iv_back);
        btnSlide = findViewById(R.id.btn_slide);
        btnClearSlide = findViewById(R.id.btn_clear_slide);
        btnStartSlide = findViewById(R.id.btn_start_slide);
        btnSettingSlide = findViewById(R.id.btn_setting_slide);
        llSlide = findViewById(R.id.ll_slide);
        rvBottom = findViewById(R.id.rv_bottom);

        ivGaussBlur.setImageResource(R.mipmap.nature_default);

        slideList = MyApplication.getPhotoList();

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

        currentAlbum = "自然风景";

        initListener();
        initList();

        float curTranslationX = llSlide.getTranslationX();
        animator = ObjectAnimator.ofFloat(llSlide, "translationX", 200f, curTranslationX);
        animator.setDuration(500);

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

        if (CustomUtil.isNetworkAvailable(this)) {
            bottomBeanList.add(new BottomBean(R.drawable.nature_circle, "自然风景"));
            bottomBeanList.add(new BottomBean(R.drawable.girl_circle, "养眼美女"));
            bottomBeanList.add(new BottomBean(R.drawable.plant_circle, "护眼绿色"));
//        bottomBeanList.add(new BottomBean(R.drawable.scenic_circle, "名胜古迹"));
//        bottomBeanList.add(new BottomBean(R.drawable.custom_circle, "风土人情"));
            bottomBeanList.add(new BottomBean(R.drawable.sky_circle, "璀璨星空"));
            bottomBeanList.add(new BottomBean(R.drawable.cartoon_circle, "热血动漫"));
            bottomBeanList.add(new BottomBean(R.drawable.car_circle, "时尚汽车"));
            bottomBeanList.add(new BottomBean(R.drawable.paint_circle, "世界名画"));
            bottomBeanList.add(new BottomBean(R.drawable.local_circle, "本地相册"));
            bottomRecyclerViewAdapter.notifyDataSetChanged();
            new Thread() {
                @Override
                public void run() {
                    super.run();
                    getAPPData(Contact.SERVER_URL + ":" + Contact.SERVER_PORT + "/" + Contact.GET_NATURE_PHOTO);
                    getAPPData(Contact.SERVER_URL + ":" + Contact.SERVER_PORT + "/" + Contact.GET_GIRL_PHOTO);
                    getAPPData(Contact.SERVER_URL + ":" + Contact.SERVER_PORT + "/" + Contact.GET_PLANT_PHOTO);
                    getAPPData(Contact.SERVER_URL + ":" + Contact.SERVER_PORT + "/" + Contact.GET_SCENIC_PHOTO);
                    getAPPData(Contact.SERVER_URL + ":" + Contact.SERVER_PORT + "/" + Contact.GET_CUSTOM_PHOTO);
                    getAPPData(Contact.SERVER_URL + ":" + Contact.SERVER_PORT + "/" + Contact.GET_SKY_PHOTO);
                    getAPPData(Contact.SERVER_URL + ":" + Contact.SERVER_PORT + "/" + Contact.GET_CARTOON_PHOTO);
                    getAPPData(Contact.SERVER_URL + ":" + Contact.SERVER_PORT + "/" + Contact.GET_CAR_PHOTO);
                    getAPPData(Contact.SERVER_URL + ":" + Contact.SERVER_PORT + "/" + Contact.GET_PAINT_PHOTO);
                }
            }.start();
        } else {
            bottomBeanList.add(new BottomBean(R.drawable.local_circle, "本地相册"));
            bottomRecyclerViewAdapter.notifyDataSetChanged();
        }

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

        bottomRecyclerViewAdapter.setOnItemClickListener(new BottomRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onClick(int position) {
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
                            mList.addAll(natureList);
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
                            mList.addAll(plantList);
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
                            mList.addAll(skyList);
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
                            mList.addAll(cartoonList);
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
                            mList.addAll(carList);
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
                            mList.addAll(paintList);
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
                boolean isAdd = false;
                Log.e(TAG, MyApplication.getPhotoList().toString());
                Log.e(TAG, slideList.toString());
                for (PhotoBean bean : MyApplication.getPhotoList()) {
                    if (photoBean.getImgUrl().equals(bean.getImgUrl())) {
                        isAdd = true;
                        dialog = new AlertDialog.Builder(MainActivity.this)
                                .setMessage("该图片已添加到画框，是否移除？")
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
                            .setMessage("是否将该图片添加到画框")
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

        btnSettingSlide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                llSlide.setVisibility(View.GONE);
                SlideDialog slideDialog = new SlideDialog(MainActivity.this);
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
                    rvPhoto.smoothScrollBy(-10, 0);
                    MyApplication.setPhotoList(slideList);
                    ToastUtils.showToast(MainActivity.this, "已清空画框，请重新添加");
                } else {
                    ToastUtils.showToast(MainActivity.this, "画框为空，无法进行操作");
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

//            runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//
//                }
//            });
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
    protected void onDestroy() {
        super.onDestroy();
        if (mOnScrollListener != null) {
            rvPhoto.removeOnScrollListener(mOnScrollListener);
        }
        if (dialog != null) {
            dialog.dismiss();
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

}