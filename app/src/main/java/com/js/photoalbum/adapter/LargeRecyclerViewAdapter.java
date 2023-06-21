package com.js.photoalbum.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.github.chrisbanes.photoview.PhotoView;
import com.js.photoalbum.R;
import com.js.photoalbum.bean.PhotoBean;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

public class LargeRecyclerViewAdapter extends RecyclerView.Adapter<LargeRecyclerViewAdapter.MyViewHolder> {

    private final Context mContext;
    private final List<PhotoBean> mList;
    private OnLongItemClickListener onLongItemClickListener;

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
        String s = mList.get(position).getImgUrl();
        Glide.with(mContext).load(s).skipMemoryCache(false).dontAnimate().into(new CustomTarget<Drawable>() {
            @Override
            public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                holder.ivLarge.setImageDrawable(resource);
            }

            @Override
            public void onLoadCleared(@Nullable Drawable placeholder) {

            }
        });
//        Glide.with(mContext).load(s).diskCacheStrategy(DiskCacheStrategy.NONE).placeholder(holder.ivLarge.getDrawable()).into(holder.ivLarge);

        holder.ivLarge.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                onLongItemClickListener.onClick(position, mList.get(position));
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        private final PhotoView ivLarge;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            ivLarge = itemView.findViewById(R.id.iv_large);
        }
    }

    public void setOnLongItemClickListener(OnLongItemClickListener longItemClickListener) {
        this.onLongItemClickListener = longItemClickListener;
    }

    public interface OnLongItemClickListener {
        void onClick(int position, PhotoBean photoBean);
    }
}
