package com.atom.shuoshuo.bean.picture.Multiimage;

import com.atom.shuoshuo.bean.picture.PictureContent;
import com.atom.shuoshuo.bean.picture.ThumbImageList;

import java.util.List;

/**
 * ============================================================
 * project: shuoshuo
 * package: com.atom.shuoshuo.bean.picture.gifvideo
 * fileDescribe:
 * user: admin
 * email: 1299854942@qq.com
 * createTime: 2017/7/31  10:12
 * modify:
 * version:: V1.0
 * ============================================================
 **/
public class MultiImage extends PictureContent {

    private List<ThumbImageList> thumb_image_list;
    private List<ThumbImageList> large_image_list;

    public List<ThumbImageList> getThumb_image_list() {
        return thumb_image_list;
    }

    public void setThumb_image_list(List<ThumbImageList> thumb_image_list) {
        this.thumb_image_list = thumb_image_list;
    }

    public List<ThumbImageList> getLarge_image_list() {
        return large_image_list;
    }

    public void setLarge_image_list(List<ThumbImageList> large_image_list) {
        this.large_image_list = large_image_list;
    }

}
