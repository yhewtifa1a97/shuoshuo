package com.atom.shuoshuo.presenter;

import android.util.Log;

import com.atom.shuoshuo.api.ApiHelper;
import com.atom.shuoshuo.api.RetrofitHelper;
import com.atom.shuoshuo.api.service.DuanZiService;
import com.atom.shuoshuo.bean.picture.GsonProvider;
import com.atom.shuoshuo.bean.picture.PictureBean;
import com.atom.shuoshuo.contract.PictrueContract;
import com.trello.rxlifecycle2.LifecycleTransformer;

import java.util.ArrayList;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

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
public class PicturePresenter implements PictrueContract.IPicturePresenter {

    private PictrueContract.IPictureView mIPictureView;
    private LifecycleTransformer<PictureBean> bind;
    private Retrofit mRetrofit;

    public PicturePresenter(PictrueContract.IPictureView mIPictureView, LifecycleTransformer bind) {
        this.mIPictureView = mIPictureView;
        this.bind = bind;
        mRetrofit = new Retrofit.Builder()
                .baseUrl(ApiHelper.DUANZI_BASE_URL)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(GsonProvider.gson))
                .client(RetrofitHelper.getHttpClient())
                .build();
    }

    @Override
    public void getPictureData() {
        mIPictureView.showProgressBar();
        mRetrofit.create(DuanZiService.class).getPictureBean()
                .compose(bind)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<PictureBean>() {
                    @Override
                    public void onComplete() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        mIPictureView.hideProgressBar();
                        mIPictureView.showError(e.getMessage());
                        Log.e("Test", "onError: " + e.getMessage());
                    }

                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(PictureBean list) {
                        mIPictureView.hideProgressBar();
                        ArrayList<PictureBean.DataBeanX.DataBean> datas = new ArrayList<>();
                        for (PictureBean.DataBeanX.DataBean data : list.getData().getData()) {
                            if (data.getType() == 1)
                                datas.add(data);
                        }
                        mIPictureView.updatePictureData(datas);
                    }
                });
    }
}
