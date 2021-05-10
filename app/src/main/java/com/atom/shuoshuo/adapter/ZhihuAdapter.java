package com.atom.shuoshuo.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.atom.shuoshuo.R;
import com.atom.shuoshuo.bean.zhihu.ZhihuDailyNews;
import com.atom.shuoshuo.listener.OnLoadMoreListener;
import com.atom.shuoshuo.ui.activity.MainActivity;
import com.atom.shuoshuo.ui.activity.ZhiHuDetailActivity;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;
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

public class ZhihuAdapter extends RecyclerView.Adapter<ZhihuAdapter.ZhihuViewHolder> implements OnLoadMoreListener {

    private Context mContext;
    private ArrayList<ZhihuDailyNews.Question> mLists = new ArrayList<>();
    private LayoutInflater mInflater;
    private boolean isLoading;

    public ZhihuAdapter(Context context) {
        this.mContext = context;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public ZhihuViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.fragment_zhihu_item, parent, false);
        return new ZhihuViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ZhihuViewHolder holder, int position) {
        final ZhihuDailyNews.Question zhihu = mLists.get(position);
        holder.mTextView.setText(zhihu.getTitle());
        Glide.with(mContext)
                .load(zhihu.getImages().get(0))
                .bitmapTransform(new RoundedCornersTransformation(mContext, 10, 0))
                .placeholder(R.drawable.ic_image_black_24dp)
                .error(R.drawable.ic_broken_image_black_24dp)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.mImageView);

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = ZhiHuDetailActivity.newIntent(mContext, zhihu.getId());
                ActivityOptionsCompat optionsCompat = ActivityOptionsCompat
                        .makeSceneTransitionAnimation((MainActivity) mContext, holder.mImageView, ZhiHuDetailActivity.TRANSIT_PIC);
                try {
                    ActivityCompat.startActivity((MainActivity) mContext, intent, optionsCompat.toBundle());
                } catch (Exception e) {
                    e.printStackTrace();
                    mContext.startActivity(intent);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mLists.size();
    }

    public void addList(ArrayList<ZhihuDailyNews.Question> list) {
        if (mLists.size() != 0 && list.size() != 0) {
            if (mLists.get(0).getTitle().equals(list.get(0).getTitle()))
                return;
        }

        if (isLoading)
            mLists.addAll(list);
        else
            mLists.addAll(0, list);
        notifyDataSetChanged();
    }

    public void clearList() {
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
        if (!isLoading)
            return;
        isLoading = false;
    }

    private int getLoadingMoreItemPosition() {
        return isLoading ? getItemCount() - 1 : 0;
    }


    static class ZhihuViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.zhihu_item_iv)
        ImageView mImageView;

        @BindView(R.id.zhihu_item_tv_title)
        TextView mTextView;

        View mView;

        public ZhihuViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            ButterKnife.bind(this, itemView);
        }
    }
}
