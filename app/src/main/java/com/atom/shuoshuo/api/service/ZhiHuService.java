package com.atom.shuoshuo.api.service;

import com.atom.shuoshuo.bean.zhihu.ZhihuDailyNews;
import com.atom.shuoshuo.bean.zhihu.ZhihuDailyStory;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * ============================================================
 * project: shuoshuo
 * package: com.atom.shuoshuo.api.service
 * fileDescribe:
 * user: admin
 * email: 1299854942@qq.com
 * createTime: 2017/7/31  16:56
 * modify:
 * version:: V1.0
 * ============================================================
 **/
public interface ZhiHuService {

    @GET("api/4/news/before/{date}")
    Observable<ZhihuDailyNews> getZhiHuData(@Path("date") String date);

    @GET("api/4/news/{id}")
    Observable<ZhihuDailyStory> getZhiHuStory(@Path("id") int id);
}
