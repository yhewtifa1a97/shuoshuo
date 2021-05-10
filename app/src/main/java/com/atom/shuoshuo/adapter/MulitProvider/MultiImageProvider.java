package com.atom.shuoshuo.adapter.MulitProvider;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.atom.shuoshuo.R;
import com.atom.shuoshuo.bean.picture.Multiimage.MultiImage;
import com.atom.shuoshuo.bean.picture.ThumbImageList;
import com.atom.shuoshuo.bean.picture.gifvideo.Gif;
import com.atom.shuoshuo.ui.activity.MultiGifActivity;
import com.atom.shuoshuo.ui.activity.MultiPictureActivity;
import com.atom.shuoshuo.widget.ninegridview.NineGridImageView;
import com.atom.shuoshuo.widget.ninegridview.NineGridImageViewAdapter;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * ============================================================
 * project: shuoshuo
 * package: com.atom.shuoshuo.adapter.MulitProvider
 * fileDescribe:
 * user: admin
 * email: 1299854942@qq.com
 * createTime: 2017/8/1  14:28
 * modify:
 * version:: V1.0
 * ============================================================
 **/
public class MultiImageProvider extends PictureFrameProvider<MultiImage, MultiImageProvider.ViewHolder> {

    @Override
    protected ContentHolder onCreateContentViewHolder(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent) {
        View view = inflater.inflate(R.layout.fragment_picture_multi_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    protected void onBindContentViewHolder(@NonNull MultiImageProvider.ViewHolder holder, @NonNull MultiImage content) {

        NineGridImageViewAdapter<ThumbImageList> mAdapter = new NineGridImageViewAdapter<ThumbImageList>() {

            @Override
            protected void onDisplayImage(Context context, ImageView imageView, ThumbImageList s) {
                Glide.with(context)
                        .load(s.getUrl())
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .placeholder(R.drawable.ic_image_black_24dp)
                        .into(imageView);
            }

            @Override
            protected ImageView generateImageView(Context context) {
                return super.generateImageView(context);
            }

            @Override
            protected void onItemImageClick(Context context, int index, List<ThumbImageList> list) {
                ArrayList<String> listUrl = new ArrayList<>();
                for (ThumbImageList thumbImageList : list) {
                    listUrl.add(thumbImageList.getUrl());
                }

                if (list.get(index).is_gif()) {
                    MultiGifActivity.startActivity(context, index, listUrl,
                            list.get(index).getWidth(), list.get(index).getHeight());
                    return;
                }

                MultiPictureActivity.startActivity(context, index,  listUrl);
            }
        };
        holder.mNineGridImageView.setAdapter(mAdapter);
        holder.mNineGridImageView.setImagesData(content.getThumb_image_list(), content.getLarge_image_list());
    }


    static class ViewHolder extends ContentHolder {

        @BindView(R.id.picture_multi_nine)
        NineGridImageView mNineGridImageView;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
