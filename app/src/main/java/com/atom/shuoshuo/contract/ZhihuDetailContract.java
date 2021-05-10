package com.atom.shuoshuo.contract;

import com.atom.shuoshuo.base.IBasePresenter;
import com.atom.shuoshuo.base.IBaseView;
import com.atom.shuoshuo.bean.zhihu.ZhihuDailyStory;

public interface ZhihuDetailContract {

    interface IZhihuDetailView extends IBaseView {
        void updateZhihuDetailData(ZhihuDailyStory story);
    }


    interface IZhihuDetailPresenter extends IBasePresenter {
        void getZhihuDetailData(int id);
    }

}
