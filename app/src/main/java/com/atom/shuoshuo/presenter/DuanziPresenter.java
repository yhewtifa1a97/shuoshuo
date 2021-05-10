package com.atom.shuoshuo.presenter;

import com.atom.shuoshuo.api.ApiHelper;
import com.atom.shuoshuo.api.service.DuanZiService;
import com.atom.shuoshuo.bean.duanzi.NeiHanDuanZi;
import com.atom.shuoshuo.contract.DuanziContract;
import com.trello.rxlifecycle2.LifecycleTransformer;

import java.util.ArrayList;

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
public class DuanziPresenter implements DuanziContract.IDuanziPresenter {

    private DuanziContract.IDuanziView mIDuanziView;
    private LifecycleTransformer<NeiHanDuanZi> bind;

    public DuanziPresenter(DuanziContract.IDuanziView mIDuanziView, LifecycleTransformer bind) {
        this.mIDuanziView = mIDuanziView;
        this.bind = bind;
    }

    @Override
    public void getDuanziData(int page) {
        mIDuanziView.showProgressBar();
        ApiHelper.getInstance().getService(DuanZiService.class, ApiHelper.DUANZI_BASE_URL)
                .getDuanZiData(page)
                .compose(bind)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<NeiHanDuanZi>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NonNull NeiHanDuanZi neiHanDuanZi) {
                        mIDuanziView.hideProgressBar();
                        ArrayList<NeiHanDuanZi.Data> datas = new ArrayList<>();
                        for (NeiHanDuanZi.Data data : neiHanDuanZi.getData().getData()) {
                            if (data.getAd() == null)
                                datas.add(data);
                        }
                        mIDuanziView.updateDuanziData(datas);
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        mIDuanziView.hideProgressBar();
                        mIDuanziView.showError(e.getMessage());
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }
}
