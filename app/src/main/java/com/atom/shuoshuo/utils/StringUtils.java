package com.atom.shuoshuo.utils;

/**
 * Created by Administrator on 2016/12/15.
 */

public class StringUtils {

    public static String getUrlPicName(String s) {
        String str = "/guodong.webp";
        int index = s.lastIndexOf("/");
        if (index != -1) {
            str = s.substring(index, s.length());
            return str;
        }
        return str;
    }

    /**
     * 千转万、十万
     *
     * @param s
     * @return
     */
    public static String getStr2W(int s) {
        String str = String.valueOf(s);
        return getStr2W(str);
    }

    /**
     * 千转万、十万
     *
     * @param str
     * @return
     */
    public static String getStr2W(String str) {
        int length = str.length();
        if (length == 5) {
            str = str.substring(0, 1) + "." + str.substring(1, 2) + "万";
            return str;
        } else if (length == 6) {
            str = str.substring(0, 2) + "万";
            return str;
        }
        return str;
    }
}
