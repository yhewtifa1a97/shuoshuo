package com.atom.shuoshuo.presenter;

import com.atom.shuoshuo.api.ApiHelper;
import com.atom.shuoshuo.api.service.QiuBaiService;
import com.atom.shuoshuo.bean.qiubai.QiuShiBaiKe;
import com.atom.shuoshuo.contract.QuibaiContract;
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
 * createTime: 2017/8/2  09:49
 * modify:
 * version:: V1.0
 * ============================================================
 **/
public class QiubaiPersenter implements QuibaiContract.IQuibaiPresenter {

    private QuibaiContract.IQuibaiView mIQuibaiView;
    private LifecycleTransformer<QiuShiBaiKe> bind;

    public QiubaiPersenter(QuibaiContract.IQuibaiView mIQuibaiView, LifecycleTransformer bind) {
        this.mIQuibaiView = mIQuibaiView;
        this.bind = bind;
    }

    @Override
    public void getQuibaiData(int page) {
        mIQuibaiView.showProgressBar();
        ApiHelper.getInstance().getService(QiuBaiService.class, ApiHelper.QIUBAI_BASE_URL)
                .getQiuBaiData(page)
                .compose(bind)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<QiuShiBaiKe>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NonNull QiuShiBaiKe qiuShiBaiKe) {
                        mIQuibaiView.hideProgressBar();
                        ArrayList<QiuShiBaiKe.Item> list = new ArrayList<>();
                        for (QiuShiBaiKe.Item item : qiuShiBaiKe.getItems()) {
                            list.add(item);
                        }
                        mIQuibaiView.updateQiubaiData(list);
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        mIQuibaiView.hideProgressBar();
                        mIQuibaiView.showError(e.getMessage());
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }
}
