package com.atom.shuoshuo.utils;

import android.annotation.TargetApi;
import android.os.AsyncTask;
import android.os.Build;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 用于替换系统自带的AsynTask，使用自己的多线程池，执行一些比较复杂的工作，比如select photos，这里用的是缓存线程池，也可以用和cpu数相等的定长线程池以提高性能
 */
public abstract class AlxMultiTask<Params, Progress, Result> extends AsyncTask<Params, Progress, Result> {

    public static final int CPU_COUNT = Runtime.getRuntime().availableProcessors();
    private static final int CORE_POOL_SIZE = CPU_COUNT + 1;
    private static final int MAXIMUM_POOL_SIZE = CPU_COUNT + 3;
    private static final int KEEP_ALIVE = 10;
    private static final BlockingQueue<Runnable> sPoolWorkQueue = new LinkedBlockingQueue<Runnable>(128);
    public static Executor mTHREAD_POOL_EXECUTOR = null;
    private static final ThreadFactory sThreadFactory = new ThreadFactory() {
        private final AtomicInteger mCount = new AtomicInteger(1);

        public Thread newThread(Runnable r) {
            return new Thread(r, "AlxAsyncTask #" + mCount.getAndIncrement());
        }
    };

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public void executeDependSDK(Params... params) {
        if (mTHREAD_POOL_EXECUTOR == null) initThreadPool();
        super.executeOnExecutor(mTHREAD_POOL_EXECUTOR, params);
    }


    /**
     * 初始化线程池
     */
    public static void initThreadPool() {
        mTHREAD_POOL_EXECUTOR = new ThreadPoolExecutor(CORE_POOL_SIZE, MAXIMUM_POOL_SIZE, KEEP_ALIVE, TimeUnit.SECONDS, sPoolWorkQueue, sThreadFactory);
    }

}

