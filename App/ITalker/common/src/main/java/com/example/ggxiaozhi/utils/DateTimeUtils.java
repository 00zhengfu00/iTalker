package com.example.ggxiaozhi.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * 工程名 ： ITalker
 * 包名   ： com.example.ggxiaozhi.utils
 * 作者名 ： 志先生_
 * 日期   ： 2017/12
 * 功能   ：时间转换工具类
 */

public class DateTimeUtils {
    private static final SimpleDateFormat FORMATYEAR = new SimpleDateFormat("yy-MM-dd", Locale.ENGLISH);
    private static final SimpleDateFormat FORMATHOUR = new SimpleDateFormat("HH:mm", Locale.ENGLISH);

    /**
     * 得到一个简单的时间日期
     *
     * @param date 时间
     * @return yy-MM-dd
     */
    public static String getSimpleDate(Date date) {
        return FORMATYEAR.format(date);
    }

    /**
     * 得到一个简单的时间日期
     *
     * @param date 时间
     * @return yy-MM-dd HH:mm
     */
    public static String getSimpleDateHour(Date date) {
        return FORMATHOUR.format(date);
    }
}
