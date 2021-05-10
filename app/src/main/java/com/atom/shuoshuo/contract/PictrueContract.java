package com.atom.shuoshuo.contract;

import com.atom.shuoshuo.base.IBasePresenter;
import com.atom.shuoshuo.base.IBaseView;
import com.atom.shuoshuo.bean.picture.PictureBean;

import java.util.ArrayList;

public interface PictrueContract {

    interface IPictureView extends IBaseView {
        void updatePictureData(ArrayList<PictureBean.DataBeanX.DataBean> list);
    }

    interface IPicturePresenter extends IBasePresenter {
        void getPictureData();
    }

}
