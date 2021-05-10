package com.atom.shuoshuo.bean.duanzi;

import java.io.Serializable;

/**
 * ============================================================
 * project: shuoshuo
 * package: com.atom.shuoshuo.bean.duanzi
 * fileDescribe:
 * user: admin
 * email: 1299854942@qq.com
 * createTime: 2017/7/31  09:59
 * modify:
 * version:: V1.0
 * ============================================================
 **/
public class VideoData implements Serializable {

    private NeiHanVideo data;

    public NeiHanVideo getData() {
        return data;
    }

    public void setData(NeiHanVideo data) {
        this.data = data;
    }
}
