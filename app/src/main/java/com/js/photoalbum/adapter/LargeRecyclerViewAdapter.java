package com.js.photoalbum.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.github.chrisbanes.photoview.PhotoView;
import com.js.photoalbum.R;
import com.js.photoalbum.bean.PhotoBean;
import com.js.photoalbum.view.ZoomImageView;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

public class LargeRecyclerViewAdapter extends RecyclerView.Adapter<LargeRecyclerViewAdapter.MyViewHolder> {

    private Context mContext;
    private List<PhotoBean> mList;

    public LargeRecyclerViewAdapter(Context mContext, List<PhotoBean> mList) {
        this.mContext = mContext;
        this.mList = mList;
    }

    @NonNull
    @Override
    public LargeRecyclerViewAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_large, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LargeRecyclerViewAdapter.MyViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Configuration mConfiguration = mContext.getResources().getConfiguration(); //获取设置的配置信息
        int ori = mConfiguration.orientation; //获取屏幕方向
        if (ori == Configuration.ORIENTATION_LANDSCAPE) {
            holder.ivLarge.setScaleType(ImageView.ScaleType.FIT_XY);
        } else {
            holder.ivLarge.setScaleType(ImageView.ScaleType.FIT_CENTER);
        }
        String s = mList.get(position).getImgUrl();
        Glide.with(mContext).load(s).diskCacheStrategy(DiskCacheStrategy.ALL).placeholder(R.mipmap.loading_image).into(new CustomTarget<Drawable>() {
            @Override
            public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                holder.ivLarge.setImageDrawable(resource);
            }

            @Override
            public void onLoadCleared(@Nullable Drawable placeholder) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private PhotoView ivLarge;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            ivLarge = itemView.findViewById(R.id.iv_large);
        }
    }
}
