package com.atom.shuoshuo.adapter.MulitProvider;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.atom.shuoshuo.R;
import com.atom.shuoshuo.bean.picture.PictureBean;
import com.atom.shuoshuo.bean.picture.PictureContent;
import com.atom.shuoshuo.glide.CircleImageTransform;
import com.atom.shuoshuo.utils.SpacingTextView;
import com.atom.shuoshuo.utils.StringUtils;
import com.bumptech.glide.Glide;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.drakeet.multitype.ItemViewProvider;

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
public abstract class PictureFrameProvider<Content extends PictureContent, SubViewHolder extends ContentHolder>
        extends ItemViewProvider<PictureBean.DataBeanX.DataBean, PictureFrameProvider.FrameHolder> {

    protected abstract ContentHolder onCreateContentViewHolder(
            @NonNull LayoutInflater inflater, @NonNull ViewGroup parent);

    protected abstract void onBindContentViewHolder(
            @NonNull SubViewHolder holder, @NonNull Content content);

    @NonNull
    @Override
    protected FrameHolder onCreateViewHolder(
            @NonNull LayoutInflater inflater, @NonNull ViewGroup parent) {
        View root = inflater.inflate(R.layout.fragment_picture_item, parent, false);
        ContentHolder subViewHolder = onCreateContentViewHolder(inflater, parent);
        return new FrameHolder(root, subViewHolder);
    }


    @SuppressWarnings("unchecked")
    @Override
    protected void onBindViewHolder(@NonNull final FrameHolder holder, @NonNull final PictureBean.DataBeanX.DataBean bean) {

        holder.user_name.setText(bean.getGroup().getUser().getName());
        if (TextUtils.isEmpty(bean.getGroup().getContent())) {
            setViewGone(holder.item_content);
        } else {
            holder.item_content.setText(bean.getGroup().getContent());
        }
        holder.category_name.setText(bean.getGroup().getCategory_name());
        holder.item_digg.setText(StringUtils.getStr2W(bean.getGroup().getDigg_count()));
        holder.item_bury.setText(StringUtils.getStr2W(bean.getGroup().getBury_count()));
        holder.item_comment.setText(StringUtils.getStr2W(bean.getGroup().getComment_count()));
        holder.item_share_count.setText(StringUtils.getStr2W(bean.getGroup().getShare_count()));
        Glide.with(holder.itemView.getContext())
                .load(bean.getGroup().getUser().getAvatar_url())
                .bitmapTransform(new CircleImageTransform(holder.itemView.getContext()))
                .into(holder.user_avatar);

        final PictureContent pictureContent = bean.getGroup();
        onBindContentViewHolder((SubViewHolder) holder.subViewHolder, (Content) pictureContent);
    }

    private void setViewGone(View view) {
        view.setVisibility(View.GONE);
    }

    static class FrameHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.picture_item_user_avatar)
        ImageView user_avatar;

        @BindView(R.id.picture_item_user_name)
        TextView user_name;

        @BindView(R.id.picture_item_content)
        SpacingTextView item_content;

        @BindView(R.id.picture_item_category_name)
        TextView category_name;

        @BindView(R.id.picture_item_digg)
        TextView item_digg;

        @BindView(R.id.picture_item_share_count)
        TextView item_share_count;

        @BindView(R.id.picture_item_share)
        RelativeLayout item_share;

        @BindView(R.id.picture_item_bury)
        TextView item_bury;

        @BindView(R.id.picture_item_comment)
        TextView item_comment;

        @BindView(R.id.picture_container)
        FrameLayout container;

        private ContentHolder subViewHolder;

        public FrameHolder(View itemView, final ContentHolder subViewHolder) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            container.addView(subViewHolder.itemView);
            this.subViewHolder = subViewHolder;
        }
    }

}
