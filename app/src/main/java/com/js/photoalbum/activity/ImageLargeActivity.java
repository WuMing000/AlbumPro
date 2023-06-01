package com.js.photoalbum.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.js.photoalbum.R;
import com.js.photoalbum.adapter.LargeRecyclerViewAdapter;
import com.js.photoalbum.utils.CustomUtil;
import com.js.photoalbum.view.ZoomImageView;

import java.util.ArrayList;
import java.util.List;

public class ImageLargeActivity extends Activity {

//    private ZoomImageView ivLarge;
    private RecyclerView recyclerView;
    private LargeRecyclerViewAdapter adapter;
    private List<String> mList;
    private Button btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_image_large);
        CustomUtil.hideNavigationBar(this);

//        ivLarge = findViewById(R.id.iv_large);
        recyclerView = findViewById(R.id.rv_large);
        mList = new ArrayList<>();
        adapter = new LargeRecyclerViewAdapter(this, mList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recyclerView.setAdapter(adapter);

        PagerSnapHelper pagerSnapHelper = new PagerSnapHelper();
        pagerSnapHelper.attachToRecyclerView(recyclerView);

        btnBack = findViewById(R.id.btn_back);

        Intent intent = getIntent();
        int position = intent.getIntExtra("position", 0);
        mList.addAll(intent.getStringArrayListExtra("mList"));
        recyclerView.scrollToPosition(position);
        adapter.notifyDataSetChanged();

//        Bitmap bitmap = BitmapFactory.decodeFile(imageUrl);
//        ivLarge.setImageBitmap(bitmap);
//        Glide.with(this).asBitmap().load(imageUrl).into(ivLarge);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}