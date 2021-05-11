package com.atom.shuoshuo.ui.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.webkit.WebView;

import com.atom.shuoshuo.R;
import com.atom.shuoshuo.base.AbsBaseActivity;

import butterknife.BindView;

/**
 * ============================================================
 * project: shuoshuo
 * package: com.atom.shuoshuo.ui.activity
 * fileDescribe:
 * user: admin
 * email: 1299854942@qq.com
 * createTime: 2017/7/31  16:02
 * modify:
 * version:: V1.0
 * ============================================================
 **/
public class MyBlogActivity extends AbsBaseActivity {

    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.wv_blog)
    WebView mWvBlog;

    public static void startActivity(Context context) {
        Intent intent = new Intent(context, MyBlogActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected int getResLayoutId() {
        return R.layout.activity_blog;
    }

    @Override
    @SuppressLint("SetJavaScriptEnabled")
    protected void initViews(Bundle savedInstanceState) {
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        mWvBlog.getSettings().setJavaScriptEnabled(true);
        mWvBlog.loadUrl("http://www.aiwuol.xyz");
    }
}
