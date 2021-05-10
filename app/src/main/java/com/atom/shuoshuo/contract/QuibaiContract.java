package com.atom.shuoshuo.contract;

import com.atom.shuoshuo.base.IBasePresenter;
import com.atom.shuoshuo.base.IBaseView;
import com.atom.shuoshuo.bean.duanzi.NeiHanDuanZi;
import com.atom.shuoshuo.bean.qiubai.QiuShiBaiKe;

import java.util.ArrayList;

public interface QuibaiContract {

    interface IQuibaiView extends IBaseView {
        void updateQiubaiData(ArrayList<QiuShiBaiKe.Item> list);
    }

    interface IQuibaiPresenter extends IBasePresenter {
        void getQuibaiData(int page);
    }

}
