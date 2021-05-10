package com.atom.shuoshuo.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.atom.shuoshuo.R;
import com.atom.shuoshuo.bean.duanzi.NeiHanVideo;
import com.atom.shuoshuo.glide.CircleImageTransform;
import com.atom.shuoshuo.listener.OnLoadMoreLisener;
import com.atom.shuoshuo.utils.SpacingTextView;
import com.atom.shuoshuo.utils.StringUtils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;


import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import fm.jiecao.jcvideoplayer_lib.JCVideoPlayer;
import fm.jiecao.jcvideoplayer_lib.JCVideoPlayerStandard;


/**
 * ============================================================
 * project: shuoshuo
 * package: com.atom.shuoshuo.adapter
 * fileDescribe:
 * user: admin
 * email: 1299854942@qq.com
 * createTime: 2017/8/1  11:06
 * modify:
 * version:: V1.0
 * ============================================================
 **/
public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.VideoViewHolder> implements OnLoadMoreLisener {
    private ArrayList<NeiHanVideo.DataBean> mLists = new ArrayList<>();
    private Context mContext;
    private LayoutInflater mInflater;
    private boolean isLoading;
    private static String coverurl = "http://p2.pstatp.com/large/";
    private RecyclerView mRecyclerView;

    public VideoAdapter(Context context, RecyclerView recyclerView) {
        mContext = context;
        mRecyclerView = recyclerView;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public VideoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new VideoViewHolder(mInflater.inflate(R.layout.fragment_video_item, parent, false));
    }

    @Override
    public void onBindViewHolder(VideoViewHolder holder, int position) {
        final NeiHanVideo.DataBean bean = mLists.get(position);
        final NeiHanVideo.DataBean.GroupBean groupBean = bean.getGroup();
        boolean isSetUp = false;
        if (bean.getGroup() != null)
            isSetUp = holder.mVideoPlayerStandard.setUp(groupBean.getMp4_url(),
                    JCVideoPlayer.SCREEN_LAYOUT_LIST, "");

        if (isSetUp)
            Glide.with(mContext)
                    .load(coverurl + groupBean.getCover_image_uri())
                    .centerCrop()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(holder.mVideoPlayerStandard.thumbImageView);

        holder.user_name.setText(groupBean.getUser().getName());
        if (TextUtils.isEmpty(groupBean.getContent()))
            holder.item_content.setVisibility(View.GONE);
        else
            holder.item_content.setText(groupBean.getContent());
        holder.category_name.setText(groupBean.getCategory_name());
        holder.item_digg.setText(StringUtils.getStr2W(groupBean.getDigg_count()));
        holder.item_bury.setText(StringUtils.getStr2W(groupBean.getBury_count()));
        holder.item_comment.setText(StringUtils.getStr2W(groupBean.getComment_count()));
        holder.item_share_count.setText(StringUtils.getStr2W(groupBean.getShare_count()));
        Glide.with(mContext)
                .load(groupBean.getUser().getAvatar_url())
                .bitmapTransform(new CircleImageTransform(mContext))
                .into(holder.user_avatar);
    }

    @Override
    public int getItemCount() {
        return mLists.size();
    }

    public void addLists(ArrayList<NeiHanVideo.DataBean> list) {
        if (mLists.size() != 0 && list.size() != 0) {
            if (mLists.get(0).getGroup().getMp4_url().equals(list.get(0).getGroup().getMp4_url()))
                return;
        }

        int size = mLists.size();
        if (isLoading) {
            mLists.addAll(list);
            notifyItemRangeInserted(size, list.size());
        } else {
            mLists.addAll(0, list);
            notifyItemRangeInserted(0, list.size());
            mRecyclerView.scrollToPosition(0);
        }
    }

    public void clearDuanziList() {
        mLists.clear();
    }

    @Override
    public void onLoadStart() {
        if (isLoading)
            return;
        isLoading = true;
    }

    @Override
    public void onLoadFinish() {
        if (!isLoading) return;
        isLoading = false;
    }

    private int getLoadingMoreItemPosition() {
        return isLoading ? getItemCount() - 1 : RecyclerView.NO_POSITION;
    }

    static class VideoViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.video_player)
        JCVideoPlayerStandard mVideoPlayerStandard;

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

        View mCardView;

        public VideoViewHolder(View itemView) {
            super(itemView);
            mCardView = itemView;
            ButterKnife.bind(this, itemView);
        }
    }
}
