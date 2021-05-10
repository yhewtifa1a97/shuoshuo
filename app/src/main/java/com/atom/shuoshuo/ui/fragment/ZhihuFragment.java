package com.atom.shuoshuo.ui.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.atom.shuoshuo.R;
import com.atom.shuoshuo.adapter.ZhihuAdapter;
import com.atom.shuoshuo.base.AbsBaseFragment;
import com.atom.shuoshuo.bean.zhihu.ZhihuDailyNews;
import com.atom.shuoshuo.contract.ZhihuContract;
import com.atom.shuoshuo.listener.OnRcvScrollListener;
import com.atom.shuoshuo.presenter.ZhihuPresenter;
import com.atom.shuoshuo.utils.AppUtil;
import com.atom.shuoshuo.utils.SnackbarUtil;
import com.atom.shuoshuo.widget.CustomEmptyView;
import com.atom.shuoshuo.widget.DividerItemDecoration;
import com.melnykov.fab.FloatingActionButton;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.util.ArrayList;
import java.util.Calendar;

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
public class ZhihuFragment extends AbsBaseFragment implements ZhihuContract.IZhihuView {


    @BindView(R.id.zhihu_rv)
    RecyclerView mZhihuRv;
    @BindView(R.id.empty_layout)
    CustomEmptyView mEmptyLayout;
    @BindView(R.id.zhihu_refresh)
    SwipeRefreshLayout mZhihuRefresh;
    @BindView(R.id.zhihu_fb)
    FloatingActionButton mZhihuFb;

    //RecycleView是否正在刷新
    private boolean isRefreshing = false;
    private boolean isLoading;

    private int mYear = Calendar.getInstance().get(Calendar.YEAR);
    private int mMonth = Calendar.getInstance().get(Calendar.MONTH);
    private int mDay = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);

    private ZhihuPresenter mZhihuPresenter;

    private ZhihuAdapter mAdapter;

    public static Fragment newInstance() {
        ZhihuFragment fragment = new ZhihuFragment();
        return fragment;
    }


    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_zhihu;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        isPrepared = true;
        mZhihuPresenter = new ZhihuPresenter(this, this.bindToLifecycle());
        lazyLoad();
    }

    @Override
    protected void lazyLoad() {
        if (!isPrepared || !isVisible)
            return;
        showProgressBar();
        initRecyclerView();
        initFButton();
        isPrepared = false;
        mZhihuPresenter.getZhihuData(Calendar.getInstance().getTimeInMillis());
    }

    private void initRecyclerView() {
        mAdapter = new ZhihuAdapter(getContext());
        mZhihuRv.setHasFixedSize(true);
        mZhihuRv.setAdapter(mAdapter);
        mZhihuRv.setLayoutManager(new LinearLayoutManager(getContext()));
        mZhihuRv.setItemAnimator(new DefaultItemAnimator());
        mZhihuRv.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL));
        mZhihuRv.addOnScrollListener(new OnRcvScrollListener() {
            @Override
            public void onBottom() {
                super.onBottom();
                if (!isLoading) {
                    isLoading = true;
                    loadMoreDate();
                }
            }
        });
    }

    private void loadMoreDate() {
//        mAdapter.onLoadStart();
        Calendar c = Calendar.getInstance();
        c.set(mYear, mMonth, --mDay);
        mZhihuPresenter.getZhihuData(c.getTimeInMillis());
    }

    private void initFButton() {
        mZhihuFb.attachToRecyclerView(mZhihuRv);
        mZhihuFb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar now = Calendar.getInstance();
//                now.set(mYear, mMonth, mDay);
                DatePickerDialog dialog = DatePickerDialog.newInstance(new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
                        mYear = year;
                        mMonth = monthOfYear;
                        mDay = dayOfMonth;
                        Calendar temp = Calendar.getInstance();
                        temp.clear();
                        temp.set(year, monthOfYear, dayOfMonth);
                        mZhihuPresenter.getZhihuData(temp.getTimeInMillis());
                    }
                }, now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH));

                dialog.setMaxDate(Calendar.getInstance());
                Calendar minDate = Calendar.getInstance();
                // 2013.5.20是知乎日报api首次上线
                minDate.set(2013, 4, 20);
                dialog.setMinDate(minDate);
                dialog.vibrate(true);
                dialog.show(getActivity().getFragmentManager(), "DatePickerDialog");
            }
        });
    }


    @Override
    public void showProgressBar() {
        mZhihuRefresh.setColorSchemeResources(R.color.colorPrimary);
        mZhihuRefresh.setRefreshing(true);
        mZhihuRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                isRefreshing = true;
                // mAdapter.clearDuanziList();
                mZhihuPresenter.getZhihuData(Calendar.getInstance().getTimeInMillis());
            }
        });
    }

    @Override
    public void hideProgressBar() {
        if (mZhihuRefresh != null)
            mZhihuRefresh.setRefreshing(false);
        isLoading = false;
        isRefreshing = false;
    }

    @Override
    public void showError(String error) {
        initEmptyView();
        mZhihuFb.setVisibility(View.GONE);
    }

    private void initEmptyView() {
        if (!AppUtil.isNetworkConnected()) {
            SnackbarUtil.showMessage(mZhihuRv, getString(R.string.noNetwork));
        } else {
            mZhihuRefresh.setRefreshing(false);
            mEmptyLayout.setVisibility(View.VISIBLE);
            mEmptyLayout.setEmptyImage(R.drawable.ic_error_black_24dp);
            mEmptyLayout.setEmptyText(getString(R.string.loaderror));
            SnackbarUtil.showMessage(mZhihuRv, getString(R.string.noNetwork));
            mEmptyLayout.reload(new CustomEmptyView.ReloadOnClickListener() {
                @Override
                public void reloadClick() {
                    mZhihuPresenter.getZhihuData(Calendar.getInstance().getTimeInMillis());
                }
            });
        }
    }

    @Override
    public void updateZhihuData(ArrayList<ZhihuDailyNews.Question> list) {
        if (mZhihuFb.getVisibility() == View.GONE) {
            mZhihuFb.setVisibility(View.VISIBLE);
        }

        hideEmptyView();
        // 注意addList() 和 onLoadFinish()的调用顺序
        mAdapter.addList(list);
        mAdapter.onLoadFinish();
        isLoading = false;
    }

    private void hideEmptyView() {
        mEmptyLayout.setVisibility(View.GONE);
    }
}
