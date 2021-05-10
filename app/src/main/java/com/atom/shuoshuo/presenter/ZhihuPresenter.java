package com.atom.shuoshuo.presenter;

import com.atom.shuoshuo.api.ApiHelper;
import com.atom.shuoshuo.api.service.ZhiHuService;
import com.atom.shuoshuo.bean.zhihu.ZhihuDailyNews;
import com.atom.shuoshuo.contract.ZhihuContract;
import com.atom.shuoshuo.utils.DateTimeHelper;
import com.google.gson.Gson;
import com.trello.rxlifecycle2.LifecycleTransformer;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * ============================================================
 * project: shuoshuo
 * package: com.atom.shuoshuo.presenter
 * fileDescribe:
 * user: admin
 * email: 1299854942@qq.com
 * createTime: 2017/7/31  16:54
 * modify:
 * version:: V1.0
 * ============================================================
 **/
public class ZhihuPresenter implements ZhihuContract.IZhihuPresenter {

    private ZhihuContract.IZhihuView mZhihuView;
    private Gson gson;
    private LifecycleTransformer<ZhihuDailyNews> bind;

    public ZhihuPresenter(ZhihuContract.IZhihuView mZhihuView, LifecycleTransformer bind) {
        this.mZhihuView = mZhihuView;
        gson = new Gson();
        this.bind = bind;
    }

    @Override
    public void getZhihuData(long data) {
        mZhihuView.showProgressBar();
        ApiHelper.getInstance().getService(ZhiHuService.class, ApiHelper.ZHIHU_BASE_URL)
                .getZhiHuData(DateTimeHelper.formatZhihuDailyDate(data))
                .compose(bind)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ZhihuDailyNews>() {
                    @Override
                    public void onComplete() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        mZhihuView.hideProgressBar();
                        mZhihuView.showError(e.getMessage());
                    }

                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(ZhihuDailyNews data) {
                        mZhihuView.hideProgressBar();
                        mZhihuView.updateZhihuData(data.getStories());
                    }
                });
    }
}
