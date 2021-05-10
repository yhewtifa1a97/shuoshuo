package com.atom.shuoshuo.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

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
public class AboutMeActivity extends AbsBaseActivity {

    public static void startActivity(Context context) {
        Intent intent = new Intent(context, AboutMeActivity.class);
        context.startActivity(intent);
    }


    @Override
    protected int getResLayoutId() {
        return 0;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {

    }


}
