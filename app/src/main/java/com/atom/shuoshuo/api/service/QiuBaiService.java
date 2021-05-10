package com.atom.shuoshuo.api.service;

import com.atom.shuoshuo.bean.qiubai.QiuShiBaiKe;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;

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


public interface QiuBaiService {

    @GET("article/list/text")
    Observable<QiuShiBaiKe> getQiuBaiData(@Query("page") int page);
}
