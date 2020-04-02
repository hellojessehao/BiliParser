package com.android.jesse.biliparser.utils;

import android.app.Activity;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.List;

/**
 * @Description: 通用工具类
 * @author: zhangshihao
 * @date: 2020/3/12
 */
public class Utils {

    private static final String TAG = Utils.class.getSimpleName();

    public static String translateChinese2Utf8(String chineseWord) {
        try {
            return URLEncoder.encode(chineseWord, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 设置沉浸式状态栏
     */
    public static void setDeepStatusBar(Activity activity) {
        ScreenManager.getInstance().setStatusBar(true, activity);
    }

    /**
     * 设置沉浸式状态栏，状态栏字体为黑色
     */
    public static void setDarkStatusBar(Activity activity) {
        ScreenManager.getInstance().setDeepStatusBar(true, activity);
    }

    //判断列表是否为空
    public static boolean isListEmpty(List list) {
        if (list != null && list.size() > 0) {
            return false;
        }
        return true;
    }

}
