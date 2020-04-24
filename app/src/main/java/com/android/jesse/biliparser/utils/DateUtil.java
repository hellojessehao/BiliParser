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

    /**
     * 获取格式化后的日期字符串
     * @param mills 日期毫秒数
     * @param formatRule 格式化规则
     * @return 格式化后的日期字符串
     */
    public static String getFormatedDate(long mills,String formatRule){
        String formatedDate = "";
        Date date = new Date(mills);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(formatRule);
        formatedDate = simpleDateFormat.format(date);
        return formatedDate;
    }

}
