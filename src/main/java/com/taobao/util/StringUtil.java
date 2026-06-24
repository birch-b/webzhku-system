package com.taobao.util;

/**
 * 字符串工具类
 */
public class StringUtil {

    /** 判断字符串是否为空 */
    public static boolean isEmpty(String s) {
        return s == null || s.trim().isEmpty();
    }

    /** 判断字符串是否非空 */
    public static boolean isNotEmpty(String s) {
        return !isEmpty(s);
    }

    /** 安全返回字符串（null转为空串） */
    public static String defaultIfEmpty(String s, String defaultVal) {
        return isEmpty(s) ? defaultVal : s;
    }

    /** 截断字符串（防XSS） */
    public static String escape(String s) {
        if (s == null) return null;
        return s.replaceAll("<", "&lt;").replaceAll(">", "&gt;");
    }
}
