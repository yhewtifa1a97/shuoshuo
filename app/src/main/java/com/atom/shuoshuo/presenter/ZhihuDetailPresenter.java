package com.atom.shuoshuo.presenter;

import com.atom.shuoshuo.api.ApiHelper;
import com.atom.shuoshuo.api.service.ZhiHuService;
import com.atom.shuoshuo.bean.zhihu.ZhihuDailyStory;
import com.atom.shuoshuo.contract.ZhihuDetailContract;
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
public class ZhihuDetailPresenter implements ZhihuDetailContract.IZhihuDetailPresenter {

    private ZhihuDetailContract.IZhihuDetailView mIZhihuDetailView;
    private LifecycleTransformer<ZhihuDailyStory> bind;

    public ZhihuDetailPresenter(ZhihuDetailContract.IZhihuDetailView mIZhihuDetailView, LifecycleTransformer bind) {
        this.mIZhihuDetailView = mIZhihuDetailView;
        this.bind = bind;
    }

    @Override
    public void getZhihuDetailData(int id) {
        mIZhihuDetailView.showProgressBar();
        ApiHelper.getInstance().getService(ZhiHuService.class,ApiHelper.ZHIHU_BASE_URL)
                .getZhiHuStory(id)
                .compose(bind)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ZhihuDailyStory>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NonNull ZhihuDailyStory zhihuDailyStory) {
                        mIZhihuDetailView.hideProgressBar();
                        mIZhihuDetailView.updateZhihuDetailData(zhihuDailyStory);
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        mIZhihuDetailView.hideProgressBar();
                        mIZhihuDetailView.showError(e.getMessage());
                    }

                    @Override
                    public void onComplete() {

                    }
                });


    }


}
