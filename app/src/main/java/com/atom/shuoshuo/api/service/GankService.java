package com.atom.shuoshuo.api.service;


import com.atom.shuoshuo.bean.meizi.MeiziList;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * ============================================================
 * project: shuoshuo
 * package: com.atom.shuoshuo.Api.service
 * fileDescribe:
 * user: admin
 * email: 1299854942@qq.com
 * createTime: 2017/7/31  09:51
 * modify:
 * version:: V1.0
 * ============================================================
 **/

public interface GankService {

    @GET("api/data/福利/20/{page}")
    Observable<MeiziList> getMeizhiData(@Path("page") int page);

//    @GET("api/data/休息视频/10/{page}")
//    Observable<VedioData> getVedioData(@Path("page") int page);
}
