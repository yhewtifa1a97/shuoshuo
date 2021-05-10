package com.atom.shuoshuo.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageView;

import com.atom.shuoshuo.R;
import com.atom.shuoshuo.base.AbsBaseActivity;
import com.atom.shuoshuo.bean.zhihu.ZhihuDailyStory;
import com.atom.shuoshuo.contract.ZhihuDetailContract;
import com.atom.shuoshuo.presenter.ZhihuDetailPresenter;
import com.atom.shuoshuo.utils.SnackbarUtil;
import com.atom.shuoshuo.utils.WebUtils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import butterknife.BindView;

/**
 * ============================================================
 * project: shuoshuo
 * package: com.atom.shuoshuo.ui.activity
 * fileDescribe:
 * user: admin
 * email: 1299854942@qq.com
 * createTime: 2017/7/31  17:08
 * modify:
 * version:: V1.0
 * ============================================================
 **/
public class ZhiHuDetailActivity extends AbsBaseActivity implements ZhihuDetailContract.IZhihuDetailView {

    public static final String EXTRA_ID = "id";
    public static final String TRANSIT_PIC = "picture";


    @BindView(R.id.zhihu_detail_iv)
    ImageView mZhihuDetailIv;
    @BindView(R.id.zhihu_detail_CToolbarLayout)
    CollapsingToolbarLayout mZhihuDetailCToolbarLayout;
    @BindView(R.id.app_bar_layout)
    AppBarLayout mAppBarLayout;
    @BindView(R.id.zhihu_detail_webview)
    WebView mZhihuDetailWebview;
    @BindView(R.id.zhihu_detail_fab)
    FloatingActionButton mZhihuDetailFab;
    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    private ZhihuDetailPresenter mZhihuDetailPresenter;
    private int mId;
    private ZhihuDailyStory mStory;

    @Override
    protected int getResLayoutId() {
        return R.layout.activity_zhihu_detail;
    }


    @Override
    protected void initViews(Bundle savedInstanceState) {
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        ViewCompat.setTransitionName(mZhihuDetailIv, TRANSIT_PIC);
        initWebView();

        mZhihuDetailPresenter = new ZhihuDetailPresenter(this, this.bindToLifecycle());
        mId = getIntent().getIntExtra(EXTRA_ID, 0);
        mZhihuDetailPresenter.getZhihuDetailData(mId);

        mZhihuDetailFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAppBarLayout.setExpanded(false);
            }
        });
    }

    private void initWebView() {
        mZhihuDetailWebview.setScrollbarFadingEnabled(true);

        WebSettings settings = mZhihuDetailWebview.getSettings();
        //js
        settings.setJavaScriptEnabled(true);
        //zoom设置为不能缩放可以防止页面上出现放大和缩小的图标
        settings.setBuiltInZoomControls(false);
        //cache
        settings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        //开启DOM storage API功能
        settings.setDomStorageEnabled(true);
        //开启application Cache功能
        settings.setAppCacheEnabled(true);
        settings.setAppCachePath(getCacheDir().getAbsolutePath() + "/webViewCache");
        //优化
        settings.setBlockNetworkImage(true);
        settings.setLoadWithOverviewMode(true);
        //LayoutAlgorithm
        settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        //webChromeClient
        mZhihuDetailWebview.setWebChromeClient(new WebChromeClient());
    }

    public static Intent newIntent(Context context, int id) {
        Intent intent = new Intent(context, ZhiHuDetailActivity.class);
        intent.putExtra(EXTRA_ID, id);
        return intent;
    }

    @Override
    public void showProgressBar() {

    }

    @Override
    public void hideProgressBar() {

    }

    @Override
    public void showError(String error) {
        SnackbarUtil.showMessage(mZhihuDetailWebview, "加载失败", "重试", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mZhihuDetailPresenter.getZhihuDetailData(mId);
            }
        });
    }

    @Override
    public void updateZhihuDetailData(ZhihuDailyStory story) {
        if (story != null) {
            mStory = story;
            mZhihuDetailWebview.loadDataWithBaseURL("x-data://base", WebUtils.convertResult(story.getBody()), "text/html", "utf-8", null);
            mZhihuDetailWebview.getSettings().setBlockNetworkImage(false);
            Glide.with(this).load(story.getImage())
                    .crossFade().centerCrop()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .placeholder(R.color.mid_grey)
                    .error(R.color.mid_grey)
                    .into(mZhihuDetailIv);
            mToolbar.setTitle(story.getTitle());
        } else {
            assert story != null;
            mZhihuDetailWebview.loadUrl(story.getShare_url());
            Glide.with(this).load(R.drawable.ic_error_black_24dp).into(mZhihuDetailIv);
        }
    }
}
