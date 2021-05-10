package com.atom.shuoshuo.contract;

import com.atom.shuoshuo.base.IBasePresenter;
import com.atom.shuoshuo.base.IBaseView;
import com.atom.shuoshuo.bean.duanzi.NeiHanDuanZi;

import java.util.ArrayList;

public interface DuanziContract {

    interface IDuanziView extends IBaseView {
        void updateDuanziData(ArrayList<NeiHanDuanZi.Data> list);
    }

    interface IDuanziPresenter extends IBasePresenter {
        void getDuanziData(int page);
    }

}
