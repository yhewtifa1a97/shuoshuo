package com.atom.shuoshuo.bean.picture;

import com.atom.shuoshuo.bean.picture.gifvideo.GifVideo;

import java.io.Serializable;

/**
 * ============================================================
 * project: shuoshuo
 * package: com.atom.shuoshuo.bean.picture
 * fileDescribe:
 * user: admin
 * email: 1299854942@qq.com
 * createTime: 2017/7/31  10:01
 * modify:
 * version:: V1.0
 * ============================================================
 **/
public class PictureContent implements Serializable {
    private String text;
    private long id;
    private String share_url;
    private String content;
    private int comment_count;
    private int share_count;
    private String category_name;
    private int bury_count;
    private boolean is_anonymous;
    private int repin_count;
    private boolean is_neihan_hot;
    private int digg_count;
    private long group_id;
    private int is_multi_image;
    private int is_gif;
    private int media_type;
    private User user;
    private GifVideo gifvideo;
    private MiddleImage middle_image;
    private LargeImage large_image;

    public LargeImage getLarge_image() {
        return large_image;
    }

    public void setLarge_image(LargeImage large_image) {
        this.large_image = large_image;
    }

    public MiddleImage getMiddle_image() {
        return middle_image;
    }

    public void setMiddle_image(MiddleImage middle_image) {
        this.middle_image = middle_image;
    }

    public GifVideo getGifvideo() {
        return gifvideo;
    }

    public void setGifvideo(GifVideo gifvideo) {
        this.gifvideo = gifvideo;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getShare_url() {
        return share_url;
    }

    public void setShare_url(String share_url) {
        this.share_url = share_url;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getComment_count() {
        return comment_count;
    }

    public void setComment_count(int comment_count) {
        this.comment_count = comment_count;
    }

    public int getShare_count() {
        return share_count;
    }

    public void setShare_count(int share_count) {
        this.share_count = share_count;
    }

    public String getCategory_name() {
        return category_name;
    }

    public void setCategory_name(String category_name) {
        this.category_name = category_name;
    }

    public int getBury_count() {
        return bury_count;
    }

    public void setBury_count(int bury_count) {
        this.bury_count = bury_count;
    }

    public boolean is_anonymous() {
        return is_anonymous;
    }

    public void setIs_anonymous(boolean is_anonymous) {
        this.is_anonymous = is_anonymous;
    }

    public int getRepin_count() {
        return repin_count;
    }

    public void setRepin_count(int repin_count) {
        this.repin_count = repin_count;
    }

    public boolean is_neihan_hot() {
        return is_neihan_hot;
    }

    public void setIs_neihan_hot(boolean is_neihan_hot) {
        this.is_neihan_hot = is_neihan_hot;
    }

    public int getDigg_count() {
        return digg_count;
    }

    public void setDigg_count(int digg_count) {
        this.digg_count = digg_count;
    }

    public long getGroup_id() {
        return group_id;
    }

    public void setGroup_id(long group_id) {
        this.group_id = group_id;
    }

    public int getIs_multi_image() {
        return is_multi_image;
    }

    public void setIs_multi_image(int is_multi_image) {
        this.is_multi_image = is_multi_image;
    }

    public int getIs_gif() {
        return is_gif;
    }

    public void setIs_gif(int is_gif) {
        this.is_gif = is_gif;
    }

    public int getMedia_type() {
        return media_type;
    }

    public void setMedia_type(int media_type) {
        this.media_type = media_type;
    }
}
