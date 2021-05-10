package com.atom.shuoshuo.api;

import com.atom.shuoshuo.common.AppConfig;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * ============================================================
 * project: shuoshuo
 * package: com.atom.shuoshuo.Api
 * fileDescribe:
 * user: admin
 * email: 1299854942@qq.com
 * createTime: 2017/7/31  09:30
 * modify:
 * version:: V1.0
 * ============================================================
 **/
public class RetrofitHelper {

    private static final int CONNECTTIMEOUT = 20;
    private static final int READTIMEOUT = 20;
    private static final int WRITETIMEOUT = 20;

    private static OkHttpClient mHttpClient = null;
    private static Retrofit mRetrofit = null;

    private RetrofitHelper() {
    }

    private volatile static RetrofitHelper instance;

    public static RetrofitHelper getInstance() {
        if (null == instance) {
            synchronized (RetrofitHelper.class) {
                if (null == instance) {
                    instance = new RetrofitHelper();
                }
            }
        }
        return instance;
    }

    public static OkHttpClient getHttpClient() {
        if (mHttpClient != null) {
            return mHttpClient;
        }
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        if (AppConfig.DEGUB) {
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        } else {
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.NONE);
        }
        mHttpClient = new OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .retryOnConnectionFailure(true)
                .connectTimeout(CONNECTTIMEOUT, TimeUnit.SECONDS)
                .readTimeout(READTIMEOUT, TimeUnit.SECONDS)
                .writeTimeout(WRITETIMEOUT, TimeUnit.SECONDS)
                .build();
        return mHttpClient;
    }

    private static Retrofit getRetrofit(String baseUrl) {
        mRetrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(getHttpClient())
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
        return mRetrofit;
    }


    public <T> T createService(Class<T> cls, String baseUrl) {
        return getRetrofit(baseUrl).create(cls);
    }
}
