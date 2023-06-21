package com.js.photoalbum.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.js.photoalbum.R;
import com.js.photoalbum.bean.BottomBean;
import com.js.photoalbum.view.CircleImageView;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class BottomRecyclerViewAdapter extends RecyclerView.Adapter<BottomRecyclerViewAdapter.MyViewHolder> {

    private final Context mContext;
    private final List<BottomBean> mList;

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
            holder.ivName.setStrokeColor(mContext.getResources().getColor(R.color.white));
            holder.tvName.setTextColor(mContext.getResources().getColor(R.color.white));
            holder.tvName.getPaint().setFakeBoldText(true);
            holder.tvName.setTextSize(16);
            holder.ivName.setTranslationY(-10);
        } else {
            holder.ivName.setStrokeWidth(0);
            holder.tvName.setTextColor(mContext.getResources().getColor(R.color.light_white));
            holder.tvName.getPaint().setFakeBoldText(false);
            holder.tvName.setTextSize(14);
            holder.ivName.setTranslationY(0);
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

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        private final CircleImageView ivName;
        private final TextView tvName;

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
