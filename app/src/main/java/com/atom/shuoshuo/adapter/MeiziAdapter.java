package com.atom.shuoshuo.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.atom.shuoshuo.R;
import com.atom.shuoshuo.bean.meizi.Meizi;
import com.atom.shuoshuo.listener.OnLoadMoreLisener;
import com.atom.shuoshuo.ui.activity.MainActivity;
import com.atom.shuoshuo.ui.activity.MeiziActivity;
import com.atom.shuoshuo.widget.SquareCenterImageView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.nineoldandroids.animation.AnimatorInflater;
import com.nineoldandroids.animation.AnimatorSet;

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

public class MeiziAdapter extends RecyclerView.Adapter<MeiziAdapter.MeiziViewHolder> implements OnLoadMoreLisener {
    private ArrayList<Meizi> mMeiziLists = new ArrayList<>();
    private Context mContext;
    private LayoutInflater mInflater;

    private boolean isLoading;

    public MeiziAdapter(Context context) {
        mContext = context;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public MeiziViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MeiziViewHolder(mInflater.inflate(R.layout.fragment_meizi_item, parent, false));
    }

    @Override
    public void onBindViewHolder(final MeiziViewHolder holder, final int position) {
        holder.imageView.setPivotX(0.0f);
        holder.imageView.setPivotY(0.0f);

        Glide.with(mContext)
                .load(mMeiziLists.get(position).url)
                .dontAnimate()
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(R.drawable.ic_image_black_24dp)
                .into(holder.imageView);

        AnimatorSet set = (AnimatorSet) AnimatorInflater.loadAnimator(mContext, R.animator.zoom_in);
        set.setTarget(holder.imageView);
        set.start();

        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int[] location = new int[2];
                view.getLocationOnScreen(location);

                 MeiziActivity.newIntent(mContext, mMeiziLists.get(position).url,
                        location[0], location[1] + 50, view.getWidth(), view.getHeight());
                ((MainActivity) mContext).overridePendingTransition(0, 0);
            }
        });
    }

    public void addLists(ArrayList<Meizi> list) {
        if (list.size() != 0 && mMeiziLists.size() != 0) {
            if (list.get(0).url.equals(mMeiziLists.get(0).url))
                return;
        }

        int size = mMeiziLists.size();
        if (isLoading) {
            mMeiziLists.addAll(list);
            notifyItemRangeInserted(size, list.size());
        } else {
            mMeiziLists.addAll(0, list);
            notifyItemRangeInserted(0, list.size());
        }
//        notifyDataSetChanged();
    }

    public void clearMeiziList() {
        mMeiziLists.clear();
    }

    @Override
    public int getItemCount() {
        return mMeiziLists.size();
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

    static class MeiziViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.meizi_iv)
        SquareCenterImageView imageView;

        View card;

        public MeiziViewHolder(View itemView) {
            super(itemView);
            card = itemView;
            ButterKnife.bind(this, itemView);
        }
    }
}