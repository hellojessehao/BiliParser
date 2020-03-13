package com.android.jesse.biliparser.utils;

import android.util.Log;

/**
 * @Description: 日志打印类
 * @author: zhangshihao
 * @date: 2020/3/12
 */
public class LogUtils {

    private static final String TAG = "zsh";

    public static void d(String msg){
        Log.d(TAG,msg);
    }

    public static void i(String msg){
        Log.i(TAG,msg);
    }

    public static void e(String msg){
        Log.e(TAG,msg);
    }

    public static void v(String msg){
        Log.v(TAG,msg);
    }

}
