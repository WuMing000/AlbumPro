package com.js.photoalbum.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.js.photoalbum.R;
import com.js.photoalbum.bean.PhotoBean;

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
        String s = mList.get(position % mList.size()).getImgUrl();
        Glide.with(mContext).load(s).diskCacheStrategy(DiskCacheStrategy.ALL).placeholder(R.mipmap.loading_image).into(new CustomTarget<Drawable>() {
            @Override
            public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                holder.ivSlide.setImageDrawable(resource);
            }

            @Override
            public void onLoadCleared(@Nullable Drawable placeholder) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return Integer.MAX_VALUE;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private ImageView ivSlide;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            ivSlide = itemView.findViewById(R.id.iv_slide);
        }
    }
}
