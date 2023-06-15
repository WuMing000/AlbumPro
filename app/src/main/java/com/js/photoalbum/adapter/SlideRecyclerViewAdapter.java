package com.js.photoalbum.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.facebook.drawee.view.SimpleDraweeView;
import com.js.photoalbum.R;
import com.js.photoalbum.activity.MainActivity;
import com.js.photoalbum.bean.PhotoBean;
import com.js.photoalbum.utils.CustomUtil;
import com.js.photoalbum.utils.ToastUtils;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

public class SlideRecyclerViewAdapter extends RecyclerView.Adapter<SlideRecyclerViewAdapter.MyViewHolder> {

    private Context mContext;
    private List<PhotoBean> mList;

    public SlideRecyclerViewAdapter(Context mContext, List<PhotoBean> mList) {
        this.mContext = mContext;
        this.mList = mList;
    }

    @NonNull
    @Override
    public SlideRecyclerViewAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_slide, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SlideRecyclerViewAdapter.MyViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Configuration mConfiguration = mContext.getResources().getConfiguration(); //获取设置的配置信息
        int ori = mConfiguration.orientation; //获取屏幕方向
        if (ori == Configuration.ORIENTATION_LANDSCAPE) {
            holder.ivSlide.setScaleType(ImageView.ScaleType.FIT_XY);
        } else {
            holder.ivSlide.setScaleType(ImageView.ScaleType.CENTER_CROP);
        }
        if (mList.size() == 0) {
//            ToastUtils.showToast(mContext, "幻灯片为空，请先添加幻灯片（长按图片可添加）");
//            Intent intent = new Intent(mContext, MainActivity.class);
//            mContext.startActivity(intent);
        } else {
            String s = mList.get(position % mList.size()).getImgUrl();
//            Glide.with(mContext).load(s).diskCacheStrategy(DiskCacheStrategy.ALL).placeholder(R.mipmap.loading_image).into(new CustomTarget<Drawable>() {
//                @Override
//                public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
//                    holder.ivSlide.setImageDrawable(resource);
//                }
//
//                @Override
//                public void onLoadCleared(@Nullable Drawable placeholder) {
//
//                }
//            });

//            Glide.with(mContext).load(s).diskCacheStrategy(DiskCacheStrategy.ALL).placeholder(holder.ivSlide.getDrawable()).into(holder.ivSlide);
            Uri uri;
            if (s.contains("storage")) {
                uri = Uri.parse("file://" + s);
            } else {
                uri = Uri.parse(s);
            }
            holder.ivSlide.setImageURI(uri);
        }
    }

    @Override
    public int getItemCount() {
        return Integer.MAX_VALUE;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private SimpleDraweeView ivSlide;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            ivSlide = itemView.findViewById(R.id.iv_slide);
        }
    }
}
