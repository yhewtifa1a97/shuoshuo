package com.atom.shuoshuo.presenter;

import com.atom.shuoshuo.api.ApiHelper;
import com.atom.shuoshuo.api.service.GankService;
import com.atom.shuoshuo.api.service.ZhiHuService;
import com.atom.shuoshuo.bean.meizi.Meizi;
import com.atom.shuoshuo.bean.meizi.MeiziList;
import com.atom.shuoshuo.bean.zhihu.ZhihuDailyNews;
import com.atom.shuoshuo.contract.MeiziContract;
import com.atom.shuoshuo.contract.MeiziContract;
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
public class MeiziPresenter implements MeiziContract.IMeiziPresenter {

    private MeiziContract.IMeiziView mIMeiziView;
    private LifecycleTransformer<MeiziList> bind;

    public MeiziPresenter(MeiziContract.IMeiziView mIMeiziView, LifecycleTransformer bind) {
        this.mIMeiziView = mIMeiziView;
        this.bind = bind;
    }



    @Override
    public void getMeiziData(int page) {
        mIMeiziView.showProgressBar();
        ApiHelper.getInstance().getService(GankService.class, ApiHelper.MEIZI_BASE_URL)
                .getMeizhiData(page)
                .compose(bind)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<MeiziList>() {
                    @Override
                    public void onError(Throwable e) {
                        mIMeiziView.hideProgressBar();
                        mIMeiziView.showError(e.getMessage());
                    }

                    @Override
                    public void onComplete() {

                    }

                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(MeiziList meiziList) {
                        mIMeiziView.hideProgressBar();
                        mIMeiziView.updateMeiziData(meiziList.getResults());
                    }
                });
    }
}
