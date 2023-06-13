package com.js.photoalbum.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.net.Uri;
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
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.facebook.drawee.view.SimpleDraweeView;
import com.js.photoalbum.MyApplication;
import com.js.photoalbum.R;
import com.js.photoalbum.activity.ImageLargeActivity;
import com.js.photoalbum.bean.PhotoBean;
import com.js.photoalbum.utils.ToastUtils;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

@SuppressLint("LongLogTag")
public class PhotoRecyclerViewAdapter extends RecyclerView.Adapter<PhotoRecyclerViewAdapter.MyViewHolder> {

    private final static String TAG = "PhotoRecyclerViewAdapter===========>";

    private Context mContext;
    private List<PhotoBean> mList;

    private OnItemClickListener onItemClickListener;
    private OnItemLongClickListener onItemLongClickListener;

    public PhotoRecyclerViewAdapter(Context mContext, List<PhotoBean> mList) {
        this.mContext = mContext;
        this.mList = mList;
    }

    @NonNull
    @Override
    public PhotoRecyclerViewAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        Log.e(TAG, "wuwuwuwuwu");
        View view;
        Configuration mConfiguration = mContext.getResources().getConfiguration(); //获取设置的配置信息
        int ori = mConfiguration.orientation; //获取屏幕方向
        if (ori == Configuration.ORIENTATION_LANDSCAPE) {
            view = LayoutInflater.from(mContext).inflate(R.layout.item_photo, parent, false);
        } else {
            view = LayoutInflater.from(mContext).inflate(R.layout.item_photo_portrait, parent, false);
        }
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        int width = mContext.getResources().getDisplayMetrics().widthPixels;
        params.width = width / 2;
        view.setLayoutParams(params);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PhotoRecyclerViewAdapter.MyViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Log.e(TAG, mList.size() + "");
        String s = mList.get(position).getImgUrl();
        String imgAuthor = mList.get(position).getImgAuthor();
        String imgName = mList.get(position).getImgName();
        String newName = "";
        if (imgName.length() != 0) {
            newName = imgName.substring(0, imgName.length() - 4);
        }

//        Glide.with(mContext).load(s).into(holder.ivPhoto);
//        Bitmap bitmap = BitmapFactory.decodeFile(s);
//        Log.e("TAG", bitmap.getWidth() +  "," + bitmap.getHeight() + "");
//        holder.ivPhoto.setImageBitmap(bitmap);

//        Bitmap bitmap = ImageScalingUtil.compressBitmapFromPath(s, 600, 300);
//        RequestOptions options = new RequestOptions()
//                .priority(Priority.HIGH) //优先级
//                .transform(new GlideRoundTransformation(8)); //圆角
//        Glide.with(mContext).load(s).apply(RequestOptions.bitmapTransform(new RoundedCorners(15))).placeholder(R.mipmap.loading_image).into(holder.ivPhoto);

        holder.tvImageName.setText(newName);
        holder.tvImageAuthor.setText(imgAuthor);

        holder.ivRightSlide.setVisibility(View.GONE);
        for (PhotoBean photoBean : MyApplication.getPhotoList()) {
            if (s.equals(photoBean.getImgUrl())) {
                holder.ivRightSlide.setVisibility(View.VISIBLE);
            }
        }
//        Glide.with(mContext).load(s).apply(RequestOptions.bitmapTransform(new RoundedCorners(15))).placeholder(R.mipmap.loading_image).into(new CustomTarget<Drawable>() {
//            @Override
//            public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
//                holder.ivPhoto.setImageDrawable(resource);
//            }
//
//            @Override
//            public void onLoadCleared(@Nullable Drawable placeholder) {
//
//            }
//        });
        Uri uri;
        if (s.contains("storage")) {
            uri = Uri.parse("file://" + s);
        } else {
            uri = Uri.parse(s);
        }
        holder.ivPhoto.setImageURI(uri);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClickListener.onClick(position);
            }
        });

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                onItemLongClickListener.onClick(mList.get(position), holder.ivRightSlide);
                return true;
            }
        });
//        Glide.with(mContext).load(s).placeholder(R.mipmap.loading_image).into(holder.ivPhoto);
    }

    @Override
    public int getItemCount() {
        return mList == null ? 0 : mList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private SimpleDraweeView ivPhoto;
        private CardView cvImage;
        private ImageView ivRightSlide;
        private TextView tvImageName;
        private TextView tvImageAuthor;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            cvImage = itemView.findViewById(R.id.cv_image);
            ivPhoto = itemView.findViewById(R.id.iv_photo);
            ivRightSlide = itemView.findViewById(R.id.iv_right_slide);
            tvImageName = itemView.findViewById(R.id.tv_image_name);
            tvImageAuthor = itemView.findViewById(R.id.tv_image_author);
        }
    }

    public void setOnItemClickListener(OnItemClickListener itemClickListener) {
        this.onItemClickListener = itemClickListener;
    }

    public interface OnItemClickListener {
        void onClick(int position);
    }

    public void setOnItemLongClickListener(OnItemLongClickListener itemLongClickListener) {
        this.onItemLongClickListener = itemLongClickListener;
    }

    public interface OnItemLongClickListener {
        void onClick(PhotoBean photoBean, ImageView imageView);
    }
}
