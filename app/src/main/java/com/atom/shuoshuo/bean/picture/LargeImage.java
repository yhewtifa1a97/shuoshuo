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
 * createTime: 2017/7/31  10:03
 * modify:
 * version:: V1.0
 * ============================================================
 **/
public class LargeImage implements Serializable {

    private List<UrlList> url_list;

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
