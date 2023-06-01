package com.js.photoalbum.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.js.photoalbum.R;
import com.js.photoalbum.activity.ImageLargeActivity;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

public class PhotoRecyclerViewAdapter extends RecyclerView.Adapter<PhotoRecyclerViewAdapter.MyViewHolder> {

    private Context mContext;
    private List<String> mList;

    private OnItemClickListener onItemClickListener;

    public PhotoRecyclerViewAdapter(Context mContext, List<String> mList) {
        this.mContext = mContext;
        this.mList = mList;
    }

    @NonNull
    @Override
    public PhotoRecyclerViewAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_photo, parent, false);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        int width = mContext.getResources().getDisplayMetrics().widthPixels;
        params.width = width / 2;
        view.setLayoutParams(params);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PhotoRecyclerViewAdapter.MyViewHolder holder, @SuppressLint("RecyclerView") int position) {
        String s = mList.get(position);
//        Glide.with(mContext).load(s).into(holder.ivPhoto);
//        Bitmap bitmap = BitmapFactory.decodeFile(s);
//        Log.e("TAG", bitmap.getWidth() +  "," + bitmap.getHeight() + "");
//        holder.ivPhoto.setImageBitmap(bitmap);

//        Bitmap bitmap = ImageScalingUtil.compressBitmapFromPath(s, 600, 300);
//        RequestOptions options = new RequestOptions()
//                .priority(Priority.HIGH) //优先级
//                .transform(new GlideRoundTransformation(8)); //圆角
        Glide.with(mContext).load(s).apply(RequestOptions.bitmapTransform(new RoundedCorners(15))).placeholder(R.mipmap.loading_image).into(holder.ivPhoto);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClickListener.onClick(position);
            }
        });
//        Glide.with(mContext).load(s).placeholder(R.mipmap.loading_image).into(holder.ivPhoto);
    }

    @Override
    public int getItemCount() {
        return mList == null ? 0 : mList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private ImageView ivPhoto;
        private CardView cvImage;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            cvImage = itemView.findViewById(R.id.cv_image);
            ivPhoto = itemView.findViewById(R.id.iv_photo);
        }
    }

    public void setOnItemClickListener(OnItemClickListener itemClickListener) {
        this.onItemClickListener = itemClickListener;
    }

    public interface OnItemClickListener {
        void onClick(int position);
    }
}
