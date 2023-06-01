package com.js.photoalbum;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.js.photoalbum.activity.ImageLargeActivity;
import com.js.photoalbum.adapter.PhotoRecyclerViewAdapter;
import com.js.photoalbum.manager.CenterZoomLayoutManager;
import com.js.photoalbum.manager.Contact;
import com.js.photoalbum.manager.HorizontalDecoration;
import com.js.photoalbum.utils.CustomUtil;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

@SuppressLint("LongLogTag")
public class MainActivity extends Activity {

    private static final String TAG = "MainActivity=============>";

    private RecyclerView rvPhoto;
    private PhotoRecyclerViewAdapter adapter;
    private List<String> mList;

    private Button btnNature, btnLocal;

    private final float mShrinkAmount = 0.55f;
    private final float mShrinkDistance = 0.9f;

    private boolean isScroll = true;

    private CenterZoomLayoutManager centerZoomLayoutManager;

    private int currentPosition;

    private String currentAlbum;

    private Handler handler = new Handler(Looper.myLooper()) {
        @Override
        public void dispatchMessage(@NonNull Message msg) {
            super.dispatchMessage(msg);
            if (msg.what == 0x001) {
                String path = (String) msg.obj;
//                Bitmap bitmap = BitmapFactory.decodeFile(path);
//                if (bitmap.getWidth() == 1920 && bitmap.getHeight() == 1080) {
                mList.add(path);
                adapter.notifyDataSetChanged();
                rvPhoto.smoothScrollToPosition(0);
//                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        CustomUtil.hideNavigationBar(this);

        mList = new ArrayList<>();
        rvPhoto = findViewById(R.id.rv_photo);
        btnNature = findViewById(R.id.btn_nature);
        btnLocal = findViewById(R.id.btn_local);

        adapter = new PhotoRecyclerViewAdapter(this, mList);
        centerZoomLayoutManager = new CenterZoomLayoutManager(this, RecyclerView.HORIZONTAL, false);
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
                    // 获取图片的绝对路径
                    int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                    String path = cursor.getString(column_index);
                    Log.i("GetImagesPath", "GetImagesPath: name = "+name+"  path = "+ path);
//                    mList.add(path);
//                    adapter.notifyDataSetChanged();
                    Message message = new Message();
                    message.what = 0x001;
                    message.obj = path;
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
                    String imageUrl = mList.get(position);
                    Intent intent = new Intent(MainActivity.this, ImageLargeActivity.class);
                    intent.putExtra("position", position);
                    intent.putStringArrayListExtra("mList", (ArrayList<String>) mList);
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
                if (btnNature.getText().toString().equals(currentAlbum)) {
                    Toast.makeText(MainActivity.this, "正在操作该分类", Toast.LENGTH_SHORT).show();
                } else {
                    currentAlbum = btnNature.getText().toString();
                    mList.clear();
                    mList.add("http://img.netbian.com/file/20130316/68888e99d665f2b8dba45e065c60ca42.jpg");
                    mList.add("http://img.netbian.com/file/20150417/31bdff0d6c694b93ba462ffb21e8da4b.jpg");
                    mList.add("http://img.netbian.com/file/2023/0518/225517rRWjH.jpg");
                    mList.add("http://img.netbian.com/file/2023/0527/234811tmIC3.jpg");
                    mList.add("http://img.netbian.com/file/2016/0108/86d01043b9b088bc0f833b6167a54528.jpg");
//                mList.add(Contact.SERVER_URL + "1.jpg");
                    adapter.notifyDataSetChanged();
                    rvPhoto.smoothScrollToPosition(0);
                }
            }
        });

        btnLocal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (btnLocal.getText().toString().equals(currentAlbum)) {
                    Toast.makeText(MainActivity.this, "正在操作该分类", Toast.LENGTH_SHORT).show();
                } else {
                    currentAlbum = btnLocal.getText().toString();
//                    adapter.notifyDataSetChanged();
                    mList.clear();
                    initList();
                }
//                adapter.notifyDataSetChanged();
            }
        });

    }

    private RecyclerView.OnScrollListener mOnScrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            int firstVisibleItemPosition = centerZoomLayoutManager.findFirstVisibleItemPosition();
            int lastVisibleItemPosition = centerZoomLayoutManager.findLastVisibleItemPosition();
            if (lastVisibleItemPosition - firstVisibleItemPosition == 1 && firstVisibleItemPosition == 0) {
                currentPosition = firstVisibleItemPosition;
            } else if (lastVisibleItemPosition - firstVisibleItemPosition == 1 && lastVisibleItemPosition == centerZoomLayoutManager.getItemCount() - 1) {
                currentPosition = lastVisibleItemPosition;
            } else {
                currentPosition = (lastVisibleItemPosition + firstVisibleItemPosition) / 2;
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
    protected void onDestroy() {
        super.onDestroy();
        if (mOnScrollListener != null) {
            rvPhoto.removeOnScrollListener(mOnScrollListener);
        }
    }
}