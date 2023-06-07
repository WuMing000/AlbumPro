package com.js.photoalbum.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.js.photoalbum.MyApplication;
import com.js.photoalbum.R;
import com.js.photoalbum.bean.BottomBean;
import com.js.photoalbum.bean.PhotoBean;
import com.js.photoalbum.view.CircleImageView;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

public class BottomRecyclerViewAdapter extends RecyclerView.Adapter<BottomRecyclerViewAdapter.MyViewHolder> {

    private Context mContext;
    private List<BottomBean> mList;

    private OnItemClickListener onItemClickListener;
    private OnLongItemClickListener onLongItemClickListener;

    private int savePosition = 0;

    public BottomRecyclerViewAdapter(Context mContext, List<BottomBean> mList) {
        this.mContext = mContext;
        this.mList = mList;
    }

    @NonNull
    @Override
    public BottomRecyclerViewAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_bottom, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BottomRecyclerViewAdapter.MyViewHolder holder, @SuppressLint("RecyclerView") int position) {

        holder.tvName.setText(mList.get(position).getBottomName());
        holder.ivName.setImageResource(mList.get(position).getBottomId());

        if (savePosition == position) {
            holder.ivName.setStrokeWidth(6);
            holder.ivName.setStrokeColor(mContext.getResources().getColor(R.color.purple_500));
            holder.tvName.setTextColor(mContext.getResources().getColor(R.color.purple_500));
        } else {
            holder.ivName.setStrokeWidth(0);
            holder.tvName.setTextColor(mContext.getResources().getColor(R.color.white));
        }

        Log.e("Bottom", savePosition + "");

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 如果当前位置和之前选中的位置不同，则更新选中位置，并刷新RecyclerView
                if (position != savePosition) {
                    savePosition = position;
                    notifyDataSetChanged();
                }
                onItemClickListener.onClick(position);
            }
        });

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                onLongItemClickListener.onClick(position);
                return true;
            }
        });

    }

    @Override
    public int getItemCount() {
        return mList == null ? 0 : mList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private CircleImageView ivName;
        private TextView tvName;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            ivName = itemView.findViewById(R.id.iv_name);
            tvName = itemView.findViewById(R.id.tv_name);

        }
    }

    public void setOnItemClickListener(OnItemClickListener itemClickListener) {
        this.onItemClickListener = itemClickListener;
    }

    public interface OnItemClickListener {
        void onClick(int position);
    }

    public void setOnLongItemClickListener(OnLongItemClickListener longItemClickListener) {
        this.onLongItemClickListener = longItemClickListener;
    }

    public interface OnLongItemClickListener {
        void onClick(int position);
    }
}