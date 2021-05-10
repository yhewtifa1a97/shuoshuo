package com.atom.shuoshuo.ui.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.animation.LinearInterpolator;

import com.atom.shuoshuo.R;
import com.atom.shuoshuo.adapter.MulitProvider.GifProvider;
import com.atom.shuoshuo.adapter.MulitProvider.GifVideoProvider;
import com.atom.shuoshuo.adapter.MulitProvider.LongImageViewProvider;
import com.atom.shuoshuo.adapter.MulitProvider.MultiImageProvider;
import com.atom.shuoshuo.adapter.MulitProvider.SingleImageViewProvider;
import com.atom.shuoshuo.adapter.PictureAdapter;
import com.atom.shuoshuo.base.AbsBaseFragment;
import com.atom.shuoshuo.bean.picture.Multiimage.MultiImage;
import com.atom.shuoshuo.bean.picture.PictureBean;
import com.atom.shuoshuo.bean.picture.gifvideo.Gif;
import com.atom.shuoshuo.bean.picture.gifvideo.GifVideoBean;
import com.atom.shuoshuo.bean.picture.singleimage.LongImage;
import com.atom.shuoshuo.bean.picture.singleimage.SingleImage;
import com.atom.shuoshuo.contract.PictrueContract;
import com.atom.shuoshuo.listener.OnRcvScrollListener;
import com.atom.shuoshuo.presenter.PicturePresenter;
import com.atom.shuoshuo.utils.AppUtil;
import com.atom.shuoshuo.utils.SnackbarUtil;
import com.melnykov.fab.FloatingActionButton;
import com.nineoldandroids.animation.ObjectAnimator;
import com.nineoldandroids.animation.ValueAnimator;

import java.util.ArrayList;

import butterknife.BindView;
import me.drakeet.multitype.Items;

import static me.drakeet.multitype.MultiTypeAsserts.assertAllRegistered;

/**
 * ============================================================
 * project: shuoshuo
 * package: com.atom.shuoshuo.ui.fragment
 * fileDescribe:
 * user: admin
 * email: 1299854942@qq.com
 * createTime: 2017/7/31  16:12
 * modify:
 * version:: V1.0
 * ============================================================
 **/
public class PictureFragment extends AbsBaseFragment implements PictrueContract.IPictureView {


    @BindView(R.id.picture_recycler)
    RecyclerView mPictureRecycler;
    @BindView(R.id.picture_swipe_refresh_layout)
    SwipeRefreshLayout mPictureSwipeRefreshLayout;
    @BindView(R.id.picture_fb)
    FloatingActionButton mPictureFb;

    private PicturePresenter mPicturePresenter;

    private boolean isRefreshing = false;
    private boolean isLoadingMore = false;

    private ObjectAnimator mAnimator;

    private Items mItems;
    private PictureAdapter mPictureAdapter;

    public static Fragment newInstance() {
        PictureFragment fragment = new PictureFragment();
        return fragment;
    }


    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_picture;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        isPrepared = true;
        mPicturePresenter = new PicturePresenter(this, this.bindToLifecycle());
        lazyLoad();
    }

    @Override
    protected void lazyLoad() {
        if (!isPrepared || !isVisible)
            return;
        mPictureSwipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);
        mPictureSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                isRefreshing = true;
                mPicturePresenter.getPictureData();
            }
        });
        initRecyclerView();
        initFButton();
        isPrepared = false;
        mPicturePresenter.getPictureData();
    }

    private void initRecyclerView() {
        mItems = new Items();
        mPictureAdapter = new PictureAdapter();
        mPictureAdapter.register(Gif.class, new GifProvider());
        mPictureAdapter.register(GifVideoBean.class, new GifVideoProvider());
        mPictureAdapter.register(MultiImage.class, new MultiImageProvider());
        mPictureAdapter.register(LongImage.class, new LongImageViewProvider());
        mPictureAdapter.register(SingleImage.class, new SingleImageViewProvider());
        mPictureAdapter.setItems(mItems);
        mPictureRecycler.setAdapter(mPictureAdapter);
        mPictureRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        mPictureRecycler.setItemAnimator(new DefaultItemAnimator());
        mPictureRecycler.addOnScrollListener(new OnRcvScrollListener() {
            @Override
            public void onBottom() {
                super.onBottom();
                if (!isLoadingMore) {
                    isLoadingMore = true;
                    loadMoreDate();
                }
            }
        });
    }

    private void loadMoreDate() {
        mPicturePresenter.getPictureData();
    }

    private void initFButton() {
        mPictureFb.attachToRecyclerView(mPictureRecycler);
        mPictureFb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isRefreshing || isLoadingMore)
                    return;
                mAnimator = ObjectAnimator.ofFloat(v, "rotation", 0F, 360F);
                mAnimator.setDuration(500);
                mAnimator.setInterpolator(new LinearInterpolator());
                mAnimator.setRepeatCount(ValueAnimator.INFINITE);
                mAnimator.setRepeatMode(ValueAnimator.RESTART);
                mAnimator.start();
                mPictureRecycler.scrollToPosition(0);
                isRefreshing = true;
                mPicturePresenter.getPictureData();
            }
        });
    }


    @Override
    public void showProgressBar() {
        isRefreshing = true;
        mPictureSwipeRefreshLayout.setRefreshing(true);
    }

    @Override
    public void hideProgressBar() {
        if (mAnimator != null)
            mAnimator.cancel();
        if (mPictureSwipeRefreshLayout != null)
            mPictureSwipeRefreshLayout.setRefreshing(false);
        isRefreshing = false;
    }

    @Override
    public void showError(String error) {
        if (!AppUtil.isNetworkConnected()) {
            SnackbarUtil.showMessage(mPictureRecycler, getString(R.string.noNetwork));
        } else {
            mPictureSwipeRefreshLayout.setRefreshing(false);
            SnackbarUtil.showMessage(mPictureFb, error);
        }
    }

    @Override
    public void updatePictureData(ArrayList<PictureBean.DataBeanX.DataBean> list) {
        int size = mPictureAdapter.getItemCount();
        if (isLoadingMore) {
            mItems.addAll(list);
            mPictureAdapter.setItems(mItems);
            mPictureAdapter.notifyItemRangeInserted(size, list.size());
        } else {
            mItems.addAll(0, list);
            mPictureAdapter.setItems(mItems);
            mPictureAdapter.notifyItemRangeInserted(0, list.size());
            mPictureRecycler.scrollToPosition(0);
        }
        assertAllRegistered(mPictureAdapter, mItems);
        isLoadingMore = false;
    }
}
