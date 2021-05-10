package com.atom.shuoshuo.bean.picture;

import java.io.Serializable;
import java.util.List;

/**
 * ============================================================
 * project: shuoshuo
 * package: com.atom.shuoshuo.bean.picture
 * fileDescribe:
 * user: admin
 * email: 1299854942@qq.com
 * createTime: 2017/7/31  10:04
 * modify:
 * version:: V1.0
 * ============================================================
 **/
public class MiddleImage implements Serializable {

    private int width;
    private int r_height;
    private int r_width;
    private String uri;
    private int height;
    private List<UrlList> url_list;

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getR_height() {
        return r_height;
    }

    public void setR_height(int r_height) {
        this.r_height = r_height;
    }

    public int getR_width() {
        return r_width;
    }

    public void setR_width(int r_width) {
        this.r_width = r_width;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public List<UrlList> getUrl_list() {
        return url_list;
    }

    public void setUrl_list(List<UrlList> url_list) {
        this.url_list = url_list;
    }

    public static class UrlList {

        private String url;

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }
    }
}
