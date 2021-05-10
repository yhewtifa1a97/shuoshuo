package com.atom.shuoshuo.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.atom.shuoshuo.R;
import com.atom.shuoshuo.base.AbsBaseActivity;

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

    public static void startActivity(Context context) {
        Intent intent = new Intent(context, MyBlogActivity.class);
        context.startActivity(intent);
    }


    @Override
    protected int getResLayoutId() {
        return R.layout.activity_blog;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {

    }

}
