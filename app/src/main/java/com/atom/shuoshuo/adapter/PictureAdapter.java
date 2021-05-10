package com.atom.shuoshuo.adapter;

import android.support.annotation.NonNull;

import com.atom.shuoshuo.bean.picture.PictureBean;

import me.drakeet.multitype.MultiTypeAdapter;

/**
 * ============================================================
 * project: shuoshuo
 * package: com.atom.shuoshuo.adapter
 * fileDescribe:
 * user: admin
 * email: 1299854942@qq.com
 * createTime: 2017/8/1  11:06
 * modify:
 * version:: V1.0
 * ============================================================
 **/
public class PictureAdapter extends MultiTypeAdapter {

    public PictureAdapter(){
        super();
    }

    @NonNull
    @Override
    public Class onFlattenClass(@NonNull Object item) {
        return ((PictureBean.DataBeanX.DataBean)item).getGroup().getClass();
    }
}
