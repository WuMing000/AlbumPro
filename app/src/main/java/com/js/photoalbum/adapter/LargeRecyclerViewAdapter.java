package com.js.photoalbum.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.github.chrisbanes.photoview.PhotoView;
import com.js.photoalbum.R;
import com.js.photoalbum.view.ZoomImageView;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

public class LargeRecyclerViewAdapter extends RecyclerView.Adapter<LargeRecyclerViewAdapter.MyViewHolder> {

    private Context mContext;
    private List<String> mList;

    public LargeRecyclerViewAdapter(Context mContext, List<String> mList) {
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
        String s = mList.get(position);
        Glide.with(mContext).asBitmap().load(s).placeholder(R.mipmap.loading_image).into(holder.ivLarge);
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
