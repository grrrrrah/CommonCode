package com.lijangop.sdk.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * @Author lijiangop
 * @CreateTime 2019/7/10 14:50
 */
public class DateUtils {

    /**
     * 转时分秒
     *
     * @param ms 毫秒
     * @return
     */
    public static String formatTime(long ms) {
        int ss = 1000;
        int mi = ss * 60;
        int hh = mi * 60;
        int dd = hh * 24;

        long day = ms / dd;
        long hour = (ms - day * dd) / hh;
        long minute = (ms - day * dd - hour * hh) / mi;
        long second = (ms - day * dd - hour * hh - minute * mi) / ss;
        long milliSecond = ms - day * dd - hour * hh - minute * mi - second * ss;

        String strDay = day < 10 ? "0" + day : "" + day; //天
        String strHour = hour < 10 ? "0" + hour : "" + hour;//小时
        String strMinute = minute < 10 ? "0" + minute : "" + minute;//分钟
        String strSecond = second < 10 ? "0" + second : "" + second;//秒
        //        String strMilliSecond = milliSecond < 10 ? "00" + milliSecond : milliSecond < 100 ? "0" + milliSecond : "" + milliSecond;
        return strMinute + "分" + strSecond + "秒" + milliSecond + "毫秒";
    }

    /**
     * 转时分秒
     *
     * @param ms 毫秒
     * @return
     */
    public static long[] formatTime_timePicker(long ms) {
        long arr[] = new long[3];
        int ss = 1000;
        int mi = ss * 60;
        int hh = mi * 60;
        int dd = hh * 24;

        long day = ms / dd;
        long hour = (ms - day * dd) / hh;
        long minute = (ms - day * dd - hour * hh) / mi;
        long second = (ms - day * dd - hour * hh - minute * mi) / ss;
        long milliSecond = ms - day * dd - hour * hh - minute * mi - second * ss;

        String strDay = day < 10 ? "0" + day : "" + day; //天
        String strHour = hour < 10 ? "0" + hour : "" + hour;//小时
        String strMinute = minute < 10 ? "0" + minute : "" + minute;//分钟
        String strSecond = second < 10 ? "0" + second : "" + second;//秒
        //        String strMilliSecond = milliSecond < 10 ? "0" + milliSecond : "" + milliSecond;///毫秒
        //        strMilliSecond = milliSecond < 100 ? "0" + strMilliSecond : "" + strMilliSecond;
        arr[0] = minute;
        arr[1] = second;
        arr[2] = milliSecond;
        return arr;
    }

    /**
     * 格式化时间显示 00：00：000
     *
     * @param millisecond
     * @return
     */
    public static String formatTime_music(long millisecond) {
        if (millisecond == 0) {
            return "00:00:000";
        } else {
            long second = millisecond / 1000;
            long m = second / 60;
            long s = second % 60;
            long ms = millisecond - s * 1000 - m * 60 * 1000;
            if (m >= 60) {
                long hour = m / 60;
                long minute = m % 60;
                //                return hour + ":" + (minute > 9 ? minute : "0" + minute) + ":" + (s > 9 ? s : "0" + s);
                return hour + ":" + (minute > 9 ? minute : "0" + minute) + ":" + (s > 9 ? s : "0" + s) + ":" + (ms > 9 ? (ms > 99 ? "" + ms : "0" + ms) : "00" + ms);
            } else {
                //                return (m > 9 ? m : "0" + m) + ":" + (s > 9 ? s : "0" + s);
                return (m > 9 ? m : "0" + m) + ":" + (s > 9 ? s : "0" + s) + ":" + (ms > 9 ? (ms > 99 ? "" + ms : "0" + ms) : "00" + ms);
            }
        }
    }

    /**
     * 格式化时间显示 分秒毫秒
     *
     * @param millisecond
     * @return
     */
    public static String formatTime_music2(int millisecond) {
        if (millisecond == 0) {
            return "00分00秒000毫秒";
        } else {
            int second = millisecond / 1000;
            int m = second / 60;
            int s = second % 60;
            int ms = millisecond - s * 1000 - m * 60 * 1000;
            if (m >= 60) {
                int hour = m / 60;
                int minute = m % 60;
                //                return hour + ":" + (minute > 9 ? minute : "0" + minute) + ":" + (s > 9 ? s : "0" + s);
                return hour + "时" + (minute > 9 ? minute : "0" + minute) + "分" + (s > 9 ? s : "0" + s) + "秒" + (ms > 9 ? (ms > 99 ? "" + ms + "毫秒" : "0" + ms + "毫秒") : "00" + ms + "毫秒");
            } else {
                //                return (m > 9 ? m : "0" + m) + ":" + (s > 9 ? s : "0" + s);
                return (m > 9 ? m : "0" + m) + "分" + (s > 9 ? s : "0" + s) + "秒" + (ms > 9 ? (ms > 99 ? "" + ms + "毫秒" : "0" + ms + "毫秒") : "00" + ms + "毫秒");
            }
        }
    }

    /**
     * 格式化时间显示 00：00
     *
     * @param millisecond
     * @return
     */
    public static String formatTime_music3(int millisecond) {
        if (millisecond == 0) {
            return "00:00";
        } else {
            int second = millisecond / 1000;
            int m = second / 60;
            int s = second % 60;
            if (m >= 60) {
                int hour = m / 60;
                int minute = m % 60;
                return hour + ":" + (minute > 9 ? minute : "0" + minute) + ":" + (s > 9 ? s : "0" + s);
            } else {
                return (m > 9 ? m : "0" + m) + ":" + (s > 9 ? s : "0" + s);
            }
        }
    }

    public static String getNameByTime(long currentTimeMillis) {
        return new SimpleDateFormat("yyyy年MM月dd日HH时mm分ss秒").format(new Date(currentTimeMillis));
    }

    /**
     * 获取年份
     *
     * @param currentTimeMillis
     * @return
     */
    public static String getYear(long currentTimeMillis) {
        String result = "";
        String date = new SimpleDateFormat("yyyy年MM月dd日HH时mm分ss秒").format(new Date(currentTimeMillis));
        if (date.contains("年")) {
            String[] split = date.split("年");
            result = split[0];
        }
        return result;
    }

    /**
     * 时间字符串(HH:mm:ss.SSS) 转换成毫秒(milliseconds)
     *
     * @param ms 毫秒
     * @return
     */
    public static long formatTimeWithString(String subString) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
            Date date = sdf.parse("1970-01-01 " + subString);
            return date.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }
}
