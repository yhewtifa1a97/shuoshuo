package com.atom.shuoshuo.ui.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.animation.LinearInterpolator;

import com.atom.shuoshuo.R;
import com.atom.shuoshuo.adapter.MeiziAdapter;
import com.atom.shuoshuo.base.AbsBaseFragment;
import com.atom.shuoshuo.bean.meizi.Meizi;
import com.atom.shuoshuo.contract.MeiziContract;
import com.atom.shuoshuo.listener.OnRcvScrollListener;
import com.atom.shuoshuo.presenter.MeiziPresenter;
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
public class MeiziFragment extends AbsBaseFragment implements MeiziContract.IMeiziView {


    @BindView(R.id.meizi_recycler)
    RecyclerView mMeiziRecycler;
    @BindView(R.id.empty_layout)
    CustomEmptyView mEmptyLayout;
    @BindView(R.id.meizi_swipe_refresh_layout)
    SwipeRefreshLayout mMeiziSwipeRefreshLayout;
    @BindView(R.id.meizi_fb)
    FloatingActionButton mMeiziFb;

    private boolean isRefreshing = false;
    private boolean isLoading = false;
    private ObjectAnimator mAnimator;

    private MeiziPresenter mMeiziPresenter;
    private MeiziAdapter mAdapter;
    private int page = 1;

    public static Fragment newInstance() {
        MeiziFragment fragment = new MeiziFragment();
        return fragment;
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_meizi;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        mMeiziPresenter = new MeiziPresenter(this, this.bindToLifecycle());
        initFButton();
        initRecyclerView();
        isPrepared = true;
        mMeiziSwipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);
        lazyLoad();
    }

    private void initFButton() {
        mMeiziFb.attachToRecyclerView(mMeiziRecycler);
        mMeiziFb.setOnClickListener(new View.OnClickListener() {
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
                mMeiziRecycler.scrollToPosition(0);
                isRefreshing = true;
                mMeiziPresenter.getMeiziData(1);
            }
        });
    }

    private void initRecyclerView() {
        mAdapter = new MeiziAdapter(getContext());
        mMeiziRecycler.setHasFixedSize(true);
        mMeiziRecycler.setAdapter(mAdapter);
        mMeiziRecycler.setLayoutManager(new GridLayoutManager(getContext(), 3));
        mMeiziRecycler.setItemAnimator(new DefaultItemAnimator());
        mMeiziRecycler.addOnScrollListener(new OnRcvScrollListener() {
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

    @Override
    protected void lazyLoad() {
        isPrepared = false;
        mMeiziPresenter.getMeiziData(1);
    }


    private void loadMoreDate() {
        mAdapter.onLoadStart();
        mMeiziPresenter.getMeiziData(page);
    }

    @Override
    public void showProgressBar() {
        isRefreshing = true;
        mMeiziSwipeRefreshLayout.setRefreshing(true);
        mMeiziSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                isRefreshing = true;
                //                mAdapter.clearMeiziList();
                mMeiziPresenter.getMeiziData(1);
            }
        });
    }

    @Override
    public void hideProgressBar() {
        if (mAnimator != null)
            mAnimator.cancel();
        if (mMeiziSwipeRefreshLayout != null)
            mMeiziSwipeRefreshLayout.setRefreshing(false);
        isLoading = false;
        isRefreshing = false;
    }

    @Override
    public void showError(String error) {
        initEmptyView();
    }


    @Override
    public void updateMeiziData(ArrayList<Meizi> list) {
        hideEmptyView();
        mAdapter.addLists(list);
        mAdapter.onLoadFinish();
        isLoading = false;
    }

    public void hideEmptyView() {
        mEmptyLayout.setVisibility(View.GONE);
    }

    public void initEmptyView() {
        mMeiziSwipeRefreshLayout.setRefreshing(false);
        SnackbarUtil.showMessage(mMeiziRecycler, getString(R.string.noNetwork));
    }
}
