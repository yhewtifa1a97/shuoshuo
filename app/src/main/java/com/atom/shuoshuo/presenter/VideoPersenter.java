package com.atom.shuoshuo.presenter;

import com.atom.shuoshuo.api.ApiHelper;
import com.atom.shuoshuo.api.service.DuanZiService;
import com.atom.shuoshuo.bean.duanzi.NeiHanVideo;
import com.atom.shuoshuo.bean.duanzi.VideoData;
import com.atom.shuoshuo.contract.VideoContract;
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
 * createTime: 2017/8/2  10:26
 * modify:
 * version:: V1.0
 * ============================================================
 **/
public class VideoPersenter implements VideoContract.IVideoPresenter {

    private VideoContract.IVideoView mIVideoView;
    private LifecycleTransformer<VideoData> bind;

    public VideoPersenter(VideoContract.IVideoView mIVideoView, LifecycleTransformer bind) {
        this.mIVideoView = mIVideoView;
        this.bind = bind;

    }

    @Override
    public void getVideoData(int page) {
        mIVideoView.showProgressBar();
        ApiHelper.getInstance().getService(DuanZiService.class, ApiHelper.DUANZI_BASE_URL)
                .getVideoData(page)
                .compose(bind)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<VideoData>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NonNull VideoData videoData) {
                        mIVideoView.hideProgressBar();
                        ArrayList<NeiHanVideo.DataBean> list = new ArrayList<>();
                        for (NeiHanVideo.DataBean bean : videoData.getData().getData()) {
                            if (bean.getType() == 1)
                                list.add(bean);
                        }
                        mIVideoView.updateVideoData(list);
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        mIVideoView.hideProgressBar();
                        mIVideoView.showError(e.getMessage());

                    }

                    @Override
                    public void onComplete() {

                    }
                });

    }
}
