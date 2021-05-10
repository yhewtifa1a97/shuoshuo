package com.atom.shuoshuo;

import android.app.Application;
import android.content.Context;

/**
 * ============================================================
 * project: shuoshuo
 * package: com.atom.shuoshuo
 * fileDescribe:
 * user: admin
 * email: 1299854942@qq.com
 * createTime: 2017/7/29  15:19
 * modify:
 * version:: V1.0
 * ============================================================
 **/
public class ShuoShuoApp extends Application{

    private static Context AppContext;

    public static Context getAppContext(){
        return AppContext;
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        this.AppContext = base;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }
}
