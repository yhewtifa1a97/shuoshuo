package com.atom.shuoshuo.contract;

import com.atom.shuoshuo.base.IBasePresenter;
import com.atom.shuoshuo.base.IBaseView;
import com.atom.shuoshuo.bean.zhihu.ZhihuDailyNews;

import java.util.ArrayList;

public interface ZhihuContract {

    interface IZhihuView extends IBaseView {
        void updateZhihuData(ArrayList<ZhihuDailyNews.Question> list);
    }


    interface IZhihuPresenter extends IBasePresenter {
        void getZhihuData(long data);
    }

}
