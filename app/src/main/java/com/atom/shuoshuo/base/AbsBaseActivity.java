package com.atom.shuoshuo.base;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.trello.rxlifecycle2.components.support.RxAppCompatActivity;

import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * ============================================================
 * project: shuoshuo
 * package: com.atom.shuoshuo.base
 * fileDescribe:
 * user: admin
 * email: 1299854942@qq.com
 * createTime: 2017/7/31  10:19
 * modify:
 * version:: V1.0
 * ============================================================
 **/
public abstract class AbsBaseActivity extends RxAppCompatActivity {

    protected String TAG;
    protected Unbinder mUnbinder;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TAG = this.getClass().getSimpleName();
        preSetContentView();
        setContentView(getResLayoutId());
        mUnbinder = ButterKnife.bind(this);
        //初始化控件
        initViews(savedInstanceState);
    }

    protected void preSetContentView() {

    }

    protected abstract int getResLayoutId();

    protected abstract void initViews(Bundle savedInstanceState);

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mUnbinder != null) {
            mUnbinder.unbind();
            mUnbinder = null;
        }
    }
}
