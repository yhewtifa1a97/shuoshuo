package com.atom.shuoshuo.adapter.MulitProvider;

import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.atom.shuoshuo.R;
import com.atom.shuoshuo.bean.picture.singleimage.SingleImage;
import com.atom.shuoshuo.ui.activity.MultiPictureActivity;
import com.atom.shuoshuo.widget.ResizableImageView;
import com.bumptech.glide.Glide;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * ============================================================
 * project: shuoshuo
 * package: com.atom.shuoshuo.adapter.MulitProvider
 * fileDescribe:
 * user: admin
 * email: 1299854942@qq.com
 * createTime: 2017/8/1  14:31
 * modify:
 * version:: V1.0
 * ============================================================
 **/
public class SingleImageViewProvider extends PictureFrameProvider<SingleImage, SingleImageViewProvider.ViewHolder> {

    @Override
    protected ContentHolder onCreateContentViewHolder(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent) {
        View view = inflater.inflate(R.layout.fragment_picture_single_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    protected void onBindContentViewHolder(@NonNull final SingleImageViewProvider.ViewHolder holder, @NonNull SingleImage content) {

        final String url = content.getMiddle_image().getUrl_list().get(0).getUrl();

        holder.mResizableImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<String> list = new ArrayList<>();
                list.add(url);
                MultiPictureActivity.startActivity(holder.mResizableImageView.getContext(), 0, list);
            }
        });

        Glide.with(holder.mResizableImageView.getContext())
                .load(url)
                .placeholder(R.drawable.ic_image_black_24dp)
                .into(holder.mResizableImageView);
    }

    static class ViewHolder extends ContentHolder {

        @BindView(R.id.fragment_picture_item_iv)
        ResizableImageView mResizableImageView;

        ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
