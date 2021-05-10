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
import com.atom.shuoshuo.adapter.VideoAdapter;
import com.atom.shuoshuo.base.AbsBaseFragment;
import com.atom.shuoshuo.bean.duanzi.NeiHanVideo;
import com.atom.shuoshuo.contract.VideoContract;
import com.atom.shuoshuo.listener.OnRcvScrollListener;
import com.atom.shuoshuo.presenter.VideoPersenter;
import com.atom.shuoshuo.utils.AppUtil;
import com.atom.shuoshuo.utils.SnackbarUtil;
import com.atom.shuoshuo.widget.CustomEmptyView;
import com.melnykov.fab.FloatingActionButton;
import com.nineoldandroids.animation.ObjectAnimator;
import com.nineoldandroids.animation.ValueAnimator;

import java.util.ArrayList;

import butterknife.BindView;

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
public class VideoFragment extends AbsBaseFragment implements VideoContract.IVideoView {


    @BindView(R.id.duanzi_recycler)
    RecyclerView mDuanziRecycler;
    @BindView(R.id.empty_layout)
    CustomEmptyView mEmptyLayout;
    @BindView(R.id.duanzi_swipe_refresh_layout)
    SwipeRefreshLayout mDuanziSwipeRefreshLayout;
    @BindView(R.id.duanzi_fb)
    FloatingActionButton mDuanziFb;

    private VideoPersenter mVideoPersenter;
    private VideoAdapter mVideoAdapter;

    private boolean isRefreshing = false;
    private boolean isLoading = false;
    private ObjectAnimator mAnimator;
    private int page = 1;

    public static Fragment newInstance() {
        VideoFragment fragment = new VideoFragment();
        return fragment;
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_video;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        initRecyclerView();
        initFButton();
        isPrepared = true;
        mVideoPersenter = new VideoPersenter(this, this.bindToLifecycle());
        lazyLoad();
    }

    @Override
    protected void lazyLoad() {
        if (!isPrepared || !isVisible)
            return;
        isPrepared = false;
        mVideoPersenter.getVideoData(1);
    }


    private void initFButton() {
        mDuanziFb.attachToRecyclerView(mDuanziRecycler);
        mDuanziFb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isRefreshing || isLoading)
                    return;
                mAnimator = ObjectAnimator.ofFloat(v, "rotation", 0F, 360F);
                mAnimator.setDuration(500);
                mAnimator.setInterpolator(new LinearInterpolator());
                mAnimator.setRepeatCount(ValueAnimator.INFINITE);
                mAnimator.setRepeatMode(ValueAnimator.RESTART);
                mAnimator.start();
                mDuanziRecycler.scrollToPosition(0);
                isRefreshing = true;
                mVideoPersenter.getVideoData(1);
            }
        });
    }

    private void initRecyclerView() {
        mVideoAdapter = new VideoAdapter(getContext(), mDuanziRecycler);
        mDuanziRecycler.setAdapter(mVideoAdapter);
        mDuanziRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        mDuanziRecycler.setItemAnimator(new DefaultItemAnimator());
        mDuanziRecycler.addOnScrollListener(new OnRcvScrollListener() {
            @Override
            public void onBottom() {
                super.onBottom();
                if (!isLoading) {
                    isLoading = true;
                    page++;
                    loadMoreDate();
                }
            }
        });
    }

    private void loadMoreDate() {
        mVideoAdapter.onLoadStart();
        mVideoPersenter.getVideoData(page);
    }


    @Override
    public void showProgressBar() {
        isRefreshing = true;
        mDuanziSwipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);
        mDuanziSwipeRefreshLayout.setRefreshing(true);
        mDuanziSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                isRefreshing = true;
                mVideoPersenter.getVideoData(1);
            }
        });
    }

    @Override
    public void hideProgressBar() {
        if (mAnimator != null)
            mAnimator.cancel();
        if (mDuanziSwipeRefreshLayout != null)
            mDuanziSwipeRefreshLayout.setRefreshing(false);
        isLoading = false;
        isRefreshing = false;
    }

    @Override
    public void showError(String error) {
        initEmptyView();
    }

    public void hideEmptyView() {
        mEmptyLayout.setVisibility(View.GONE);
    }

    public void initEmptyView() {
        if (!AppUtil.isNetworkConnected()) {
            SnackbarUtil.showMessage(mDuanziRecycler, getString(R.string.noNetwork));
        } else {
            mDuanziSwipeRefreshLayout.setRefreshing(false);
            mEmptyLayout.setVisibility(View.VISIBLE);
            mEmptyLayout.setEmptyImage(R.drawable.ic_broken_image_black_24dp);
            mEmptyLayout.setEmptyText(getString(R.string.loaderror));
            SnackbarUtil.showMessage(mDuanziRecycler, getString(R.string.noNetwork));
            mEmptyLayout.reload(new CustomEmptyView.ReloadOnClickListener() {
                @Override
                public void reloadClick() {
                    mVideoPersenter.getVideoData(1);
                }
            });
        }
    }

    @Override
    public void updateVideoData(ArrayList<NeiHanVideo.DataBean> list) {
        hideEmptyView();
        // 注意addList() 和 onLoadFinish()的调用顺序
        mVideoAdapter.addLists(list);
        mVideoAdapter.onLoadFinish();
        isLoading = false;
    }
}
