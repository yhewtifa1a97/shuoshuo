package com.atom.shuoshuo.utils;

/**
 * Created by Administrator on 2016/10/13.
 */

public class WebUtils {

    private WebUtils() {
    }

    public static String convertResult(String preReuslt) {
        preReuslt = preReuslt.replace("<div class=\"img-place-holder\">", "");
        preReuslt = preReuslt.replace("<div class=\"headline\">", "");

        /**   在api中，css的地址是以一个数组的形式给出，这里需要设置
         *   in fact,in api,css addresses are given as an array
         *   api中还有js的部分，这里不再解析js
         *   javascript is included,but here I don't use it
         *   不再选择加载网络css，而是加载本地assets文件夹中的css
         *   use the css file from local assets folder,not from network
         **/
        String css = "<link rel=\"stylesheet\" href=\"file:///android_asset/zhihu_detail.css\" type=\"text/css\">";

        // 根据主题的不同确定不同的加载内容
        // load content judging by different theme
        String theme = "<body className=\"\" onload=\"onLoaded()\">";
//        if (App.getThemeValue() == Resources.Theme.NIGHT_THEME) {
//            theme = "<body className=\"\" onload=\"onLoaded()\" class=\"night\">";
//        }

        return new StringBuilder()
                .append("<!DOCTYPE html>\n")
                .append("<html lang=\"en\" xmlns=\"http://www.w3.org/1999/xhtml\">\n")
                .append("<head>\n")
                .append("\t<meta charset=\"utf-8\" />")
                .append(css).append("\n</head>\n")
                .append(theme).append(preReuslt)
                .append("</body></html>")
                .toString();
    }
}
