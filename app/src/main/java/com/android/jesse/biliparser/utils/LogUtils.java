package com.android.jesse.biliparser.utils;

import android.util.Log;

import com.orhanobut.logger.Logger;

/**
 * @Description: 日志打印类
 * @author: zhangshihao
 * @date: 2020/3/12
 */
public class LogUtils {

    private static final String TAG = "zsh";

    public static void d(String msg){
        Logger.d(TAG+" "+msg);
    }

    public static void i(String msg){
        Logger.i(TAG+" "+msg);
    }

    public static void e(String msg){
        Logger.e(TAG+" "+msg);
    }

    public static void v(String msg){
        Logger.v(TAG+" "+msg);
    }

}
