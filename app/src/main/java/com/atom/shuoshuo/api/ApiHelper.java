package com.atom.shuoshuo.api;

/**
 * ============================================================
 * project: shuoshuo
 * package: com.atom.shuoshuo.Api
 * fileDescribe:
 * user: admin
 * email: 1299854942@qq.com
 * createTime: 2017/7/31  09:24
 * modify:
 * version:: V1.0
 * ============================================================
 **/
public class ApiHelper {
    public static final String MEIZI_BASE_URL = "http://gank.io/";
    public static final String TOUTIAO_BASE_URL = "http://c.m.163.com/";
    public static final String ZHIHU_BASE_URL = "http://news-at.zhihu.com/";
    public static final String DUANZI_BASE_URL = "http://ic.snssdk.com/";
    public static final String QIUBAI_BASE_URL = "http://m2.qiushibaike.com/";

    private ApiHelper() {
    }

    private volatile static ApiHelper instance;

    public static ApiHelper getInstance() {
        if (null == instance) {
            synchronized (ApiHelper.class) {
                if (null == instance) {
                    instance = new ApiHelper();
                }
            }
        }
        return instance;
    }


    public <T> T getService(Class<T> cls, String baseUrl) {
        return RetrofitHelper.getInstance().createService(cls, baseUrl);
    }
}
