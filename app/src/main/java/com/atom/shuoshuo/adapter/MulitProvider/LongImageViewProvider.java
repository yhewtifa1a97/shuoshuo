package com.atom.shuoshuo.adapter.MulitProvider;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.atom.shuoshuo.R;
import com.atom.shuoshuo.bean.picture.singleimage.LongImage;
import com.atom.shuoshuo.ui.activity.LongPictureActivity;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * ============================================================
 * project: shuoshuo
 * package: com.atom.shuoshuo.adapter.MulitProvider
 * fileDescribe:
 * user: admin
 * email: 1299854942@qq.com
 * createTime: 2017/8/1  14:30
 * modify:
 * version:: V1.0
 * ============================================================
 **/
public class LongImageViewProvider extends PictureFrameProvider<LongImage, LongImageViewProvider.ViewHolder> {


    @Override
    protected ContentHolder onCreateContentViewHolder(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent) {
        View view = inflater.inflate(R.layout.fragment_picture_long_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    protected void onBindContentViewHolder(@NonNull final LongImageViewProvider.ViewHolder holder, @NonNull LongImage content) {
        final String url = content.getMiddle_image().getUrl_list().get(0).getUrl();

        Glide.with(holder.mImageView.getContext())
                .load(url)
                .asBitmap()
                .placeholder(R.drawable.ic_image_black_24dp)
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                        holder.mImageView.setImageBitmap(Bitmap.createBitmap(resource, 0, 0, resource.getWidth(), 300));
                    }
                });

        holder.mImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LongPictureActivity.startActivity(holder.mImageView.getContext(),
                        url);
            }
        });

        holder.mLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LongPictureActivity.startActivity(holder.mImageView.getContext(), url);
            }
        });
    }

    static class ViewHolder extends ContentHolder {

        @BindView(R.id.fragment_picture_item_iv)
        ImageView mImageView;

        @BindView(R.id.fragment_picture_item_ll)
        LinearLayout mLinearLayout;

        ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
