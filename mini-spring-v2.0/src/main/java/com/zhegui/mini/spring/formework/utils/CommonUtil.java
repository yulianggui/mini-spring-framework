package com.zhegui.mini.spring.formework.utils;

/**
 * create by zhegui on 2018/12/17
 */
public class CommonUtil {

    public static String lowerFirstCase(String str){
        char[] chars = str.toCharArray();
        chars[0] += 32;
        return String.valueOf(chars);
    }
}
