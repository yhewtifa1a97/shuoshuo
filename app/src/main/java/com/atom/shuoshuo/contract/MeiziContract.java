package com.atom.shuoshuo.contract;

import com.atom.shuoshuo.base.IBasePresenter;
import com.atom.shuoshuo.base.IBaseView;
import com.atom.shuoshuo.bean.meizi.Meizi;

import java.util.ArrayList;

public interface MeiziContract {

    interface IMeiziView extends IBaseView {
        void updateMeiziData(ArrayList<Meizi> list);
    }

    interface IMeiziPresenter extends IBasePresenter {
        void getMeiziData(int page);
    }

}
