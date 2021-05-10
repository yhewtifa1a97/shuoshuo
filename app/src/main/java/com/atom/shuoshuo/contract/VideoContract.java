package com.atom.shuoshuo.contract;

import com.atom.shuoshuo.base.IBasePresenter;
import com.atom.shuoshuo.base.IBaseView;
import com.atom.shuoshuo.bean.duanzi.NeiHanVideo;

import java.util.ArrayList;

public interface VideoContract {

    interface IVideoView extends IBaseView {

        void updateVideoData(ArrayList<NeiHanVideo.DataBean> list);
    }

    interface IVideoPresenter extends IBasePresenter {
        void getVideoData(int page);
    }

}
