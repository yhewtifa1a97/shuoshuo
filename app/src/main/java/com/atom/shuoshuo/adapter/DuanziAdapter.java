package com.atom.shuoshuo.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.atom.shuoshuo.R;
import com.atom.shuoshuo.bean.duanzi.NeiHanDuanZi;
import com.atom.shuoshuo.glide.CircleImageTransform;
import com.atom.shuoshuo.listener.OnLoadMoreLisener;
import com.atom.shuoshuo.utils.SpacingTextView;
import com.atom.shuoshuo.utils.StringUtils;
import com.atom.shuoshuo.widget.TextDrawable;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * ============================================================
 * project: shuoshuo
 * package: com.atom.shuoshuo
 * fileDescribe:
 * user: admin
 * email: 1299854942@qq.com
 * createTime: 2017/7/31  16:05
 * modify:
 * version:: V1.0
 * ============================================================
 **/
public class DuanziAdapter extends RecyclerView.Adapter<DuanziAdapter.DuanziViewHolder> implements OnLoadMoreLisener {

    private ArrayList<NeiHanDuanZi.Data> mDuanziLists = new ArrayList<>();
    private Context mContext;
    private LayoutInflater mInflater;
    private boolean isLoading;
    private RecyclerView mRecyclerView;

    public DuanziAdapter(Context context, RecyclerView recyclerView) {
        mContext = context;
        mRecyclerView = recyclerView;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public DuanziViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new DuanziViewHolder(mInflater.inflate(R.layout.fragment_duanzi_item, parent, false));
    }

    @Override
    public void onBindViewHolder(DuanziViewHolder holder, int position) {
        final NeiHanDuanZi.Data duanzi = mDuanziLists.get(position);
        final NeiHanDuanZi.Group groupBean = duanzi.getGroup();
        ArrayList<NeiHanDuanZi.Comment> comments = duanzi.getComments();
        displayTopAndBottom(holder, duanzi, groupBean);
        dispalyShenping(holder, duanzi, groupBean, comments);
    }

    private void dispalyShenping(DuanziViewHolder holder, NeiHanDuanZi.Data duanzi, NeiHanDuanZi.Group groupBean, ArrayList<NeiHanDuanZi.Comment> comments) {
        int size = comments.size();
        if (size > 0) {
            holder.item_shenping.setVisibility(View.VISIBLE);
            if (size == 1) {
                diaplayShenPingOne(holder, duanzi, groupBean, comments);
            } else if (size == 2) {
                diaplayShenPingAll(holder, duanzi, groupBean, comments);
            }
        }
    }

    private void diaplayShenPingAll(DuanziViewHolder holder, final NeiHanDuanZi.Data duanzi, final NeiHanDuanZi.Group groupBean, ArrayList<NeiHanDuanZi.Comment> comments) {
        diaplayShenPingOne(holder, duanzi, groupBean, comments);
        diaplayShenPingTwo(holder, duanzi, groupBean, comments);
    }

    private void diaplayShenPingOne(DuanziViewHolder holder, final NeiHanDuanZi.Data duanzi, final NeiHanDuanZi.Group groupBean, ArrayList<NeiHanDuanZi.Comment> comments) {
        NeiHanDuanZi.Comment comment = comments.get(0);
        holder.item_shenping_one.setVisibility(View.VISIBLE);
        holder.item_shenping_one_user_name.setText(comment.getUser_name());
        holder.item_shenping_one_digg.setText(StringUtils.getStr2W(comment.getDigg_count()));
        holder.item_shenping_one_content.setText(comment.getText());
        Glide.with(mContext).load(comment.getAvatar_url())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .bitmapTransform(new CircleImageTransform(mContext))
                .into(holder.item_shenping_one_user_avatar);
    }

    private void diaplayShenPingTwo(DuanziViewHolder holder, final NeiHanDuanZi.Data duanzi, final NeiHanDuanZi.Group groupBean, ArrayList<NeiHanDuanZi.Comment> comments) {
        NeiHanDuanZi.Comment comment = comments.get(1);
        holder.item_shenping_two.setVisibility(View.VISIBLE);
        holder.item_shenping_two_user_name.setText(comment.getUser_name());
        holder.item_shenping_two_digg.setText(StringUtils.getStr2W(comment.getDigg_count()));
        holder.item_shenping_two_content.setText(comment.getText());
        Glide.with(mContext).load(comment.getAvatar_url())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .bitmapTransform(new CircleImageTransform(mContext))
                .into(holder.item_shenping_two_user_avatar);
    }

    private void displayTopAndBottom(DuanziViewHolder holder, final NeiHanDuanZi.Data duanzi, final NeiHanDuanZi.Group groupBean) {
        if (groupBean.getUser() == null) {
            holder.user_name.setText("匿名用户");
            holder.user_avatar.setVisibility(View.GONE);
        } else {
            holder.user_name.setText(groupBean.getUser().getName());
            Glide.with(mContext)
                    .load(groupBean.getUser().getAvatar_url())
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .bitmapTransform(new CircleImageTransform(mContext))
                    .into(holder.user_avatar);
        }

        holder.item_content.setText(groupBean.getContent());
        holder.category_name.setText(groupBean.getCategory_name());
        holder.item_digg.setText(StringUtils.getStr2W(groupBean.getDigg_count()));
        holder.item_bury.setText(StringUtils.getStr2W(groupBean.getBury_count()));
        holder.item_comment.setText(StringUtils.getStr2W(groupBean.getComment_count()));
        holder.item_share_count.setText(StringUtils.getStr2W(groupBean.getShare_count()));
    }

    @Override
    public int getItemCount() {
        return mDuanziLists.size();
    }

    public void addLists(ArrayList<NeiHanDuanZi.Data> list) {
        if (mDuanziLists.size() != 0 && list.size() != 0) {
            if (mDuanziLists.get(0).getGroup().getText().equals(list.get(0).getGroup().getText()))
                return;
        }

        int size = mDuanziLists.size();
        if (isLoading) {
            mDuanziLists.addAll(list);
            notifyItemRangeInserted(size, list.size());
        } else {
            mDuanziLists.addAll(0, list);
            notifyItemRangeInserted(0, list.size());
            mRecyclerView.scrollToPosition(0);
        }
    }

    public void clearDuanziList() {
        mDuanziLists.clear();
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

    static class DuanziViewHolder extends RecyclerView.ViewHolder {
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

        @BindView(R.id.shenping_rl)
        RelativeLayout item_shenping;

        @BindView(R.id.shenping_one)
        RelativeLayout item_shenping_one;

        @BindView(R.id.shenping_one_user_avatar)
        ImageView item_shenping_one_user_avatar;

        @BindView(R.id.shenping_one_user_name)
        TextView item_shenping_one_user_name;

        @BindView(R.id.shenping_one_digg)
        TextDrawable item_shenping_one_digg;

        @BindView(R.id.shenping_one_content)
        TextView item_shenping_one_content;

        @BindView(R.id.shenping_one_share)
        ImageView item_shenping_one_share;

        @BindView(R.id.shenping_two)
        RelativeLayout item_shenping_two;

        @BindView(R.id.shenping_two_user_avatar)
        ImageView item_shenping_two_user_avatar;

        @BindView(R.id.shenping_two_user_name)
        TextView item_shenping_two_user_name;

        @BindView(R.id.shenping_two_digg)
        TextDrawable item_shenping_two_digg;

        @BindView(R.id.shenping_two_content)
        TextView item_shenping_two_content;

        @BindView(R.id.shenping_two_share)
        ImageView item_shenping_two_share;

        View view;

        public DuanziViewHolder(View itemView) {
            super(itemView);
            view = itemView;
            ButterKnife.bind(this, itemView);
        }
    }
}
