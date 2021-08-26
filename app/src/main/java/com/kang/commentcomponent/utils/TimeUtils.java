package com.kang.commentcomponent.utils;

import android.annotation.SuppressLint;
import android.text.TextUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class TimeUtils {

    private final static long minute = 60 * 1000;// 1分钟
    private final static long hour = 60 * minute;// 1小时
    private final static long day = 24 * hour;// 1天
    private final static long month = 31 * day;// 月
    private final static long year = 12 * month;// 年

    /**
     * 将毫秒转成秒前、分钟前、小时前、天前、月前、年前输出
     *
     * @param millis
     * @return
     */
    public static String getRecentTimeSpanByNow(final long millis) {
        long now = System.currentTimeMillis();
        long span = now - millis;
        if (span < 1000) {
            return "刚刚";
        } else if (span < TimeConstants.MIN) {
            return String.format(Locale.getDefault(), "%d秒前", span / TimeConstants.SEC);
        } else if (span < TimeConstants.HOUR) {
            return String.format(Locale.getDefault(), "%d分钟前", span / TimeConstants.MIN);
        } else if (span < TimeConstants.DAY) {
            return String.format(Locale.getDefault(), "%d小时前", span / TimeConstants.HOUR);
        } else if (span < TimeConstants.MONTH) {
            return String.format(Locale.getDefault(), "%d天前", span / TimeConstants.DAY);
        } else if (span < TimeConstants.YEAR) {
            return String.format(Locale.getDefault(), "%d月前", span / TimeConstants.MONTH);
        } else if (span > TimeConstants.YEAR) {
            return String.format(Locale.getDefault(), "%d年前", span / TimeConstants.YEAR);
        } else {
            return String.format("%tF", millis);
        }
    }

    /**
     * 将yyyy-MM-dd HH:mm:ss这种格式的时间转成 毫秒
     *
     * @param dateStr
     * @return
     */
    public static long getMillisByFormatTime(String dateStr) {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        long millis = 0;
        try {
            Date date = format.parse(dateStr);
            millis = date.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return millis;
    }

    /**
     * @param dateStr 时间字符串 "2021-08-23 14:51:20"
     * @param format  时间格式，如：yyyy-MM-dd HH:mm:ss
     * @return 超过一年就显示 2021-08-23 14:51:20，超过一天就显示08-23 14:51，否则就显示几个小时前和几分钟前
     */
    public static String getRecentTime(String dateStr, String format) {
        if (TextUtils.isEmpty(format)) {
            return String.valueOf(dateStr);
        }
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat dateFormat = new SimpleDateFormat(format);
        Date date = null;
        try {
            date = dateFormat.parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        assert date != null;

        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat dateFormat_md = new SimpleDateFormat("MM-dd HH:mm");

        String data_md = dateFormat_md.format(date);//08-23

        long diff = new Date().getTime() - date.getTime();
        long r;
        if (diff > year) {
            r = (diff / year);
            return dateStr;
        }
        if (diff > month) {
            r = (diff / month);
            return data_md;
        }
        if (diff > day) {
            r = (diff / day);
            return data_md;
        }
        if (diff > hour) {
            r = (diff / hour);
            return r + "小时前";
        }
        if (diff > minute) {
            r = (diff / minute);
            return r + "分钟前";
        }
        return "1分钟前";
    }
}
