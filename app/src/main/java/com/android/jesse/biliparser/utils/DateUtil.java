package com.android.jesse.biliparser.utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @Description: 时间处理类
 * @author: zhangshihao
 * @date: 2020/4/14
 */
public class DateUtil {

    private static final String TAG = DateUtil.class.getSimpleName();

    /**
     *
     * @param rule 例：yyyy-MM-dd HH:mm:ss
     * @return 按照rule格式化后的当前时间字符串
     */
    public static String getTime(String rule){
        String formatDate = "";
        Date date = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat(rule);
        formatDate = dateFormat.format(date);
        return formatDate;
    }

    /**
     * @return 默认格式的当前时间
     */
    public static String getDefaultTime(){
        return getTime("yyyy-MM-dd HH:mm:ss");
    }

}
