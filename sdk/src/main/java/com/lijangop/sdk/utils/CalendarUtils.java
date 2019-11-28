package com.lijangop.sdk.utils;

import android.annotation.SuppressLint;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * @Author lijiangop
 * @CreateTime 2019/11/6 10:32
 */
public class CalendarUtils {

    /**
     * 获取两个时间的天数差
     *
     * @param firstAddData
     * @param strCurrentDate
     * @return
     */
    public static int get2DateDiffInDay(String firstAddData, String strCurrentDate) {
        try {
            @SuppressLint("SimpleDateFormat")
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy_MM_dd");

            Calendar frontCalendar = Calendar.getInstance();
            frontCalendar.setTime(simpleDateFormat.parse(firstAddData));
            int frontDay = frontCalendar.get(Calendar.DAY_OF_YEAR);
            int frontYear = frontCalendar.get(Calendar.YEAR);

            Calendar currentCalendar = Calendar.getInstance();
            currentCalendar.setTime(simpleDateFormat.parse(strCurrentDate));
            int currentDay = currentCalendar.get(Calendar.DAY_OF_YEAR);
            int currentYear = currentCalendar.get(Calendar.YEAR);

            if (frontYear != currentYear) {//同一年
                int timeDistance = 0;
                for (int i = frontYear; i < currentYear; i++) {
                    if (i % 4 == 0 && i % 100 != 0 || i % 400 == 0) {   //闰年
                        timeDistance += 366;
                    } else {//不是闰年
                        timeDistance += 365;
                    }
                }
                return timeDistance + (currentDay - frontDay);
            } else {//不同年
                return currentDay - frontDay;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * 获取当前的时间
     *
     * @return yyyy_MM_dd 格式的日期时间
     */
    public static String getCurrentDate() {
        return getCurrentDate(0);
    }

    /**
     * 获取相对于当前+dayDiffer天后的时间
     *
     * @param dayDiffer 天数差(正数为以后;复数为以前)
     * @return yyyy_MM_dd 格式的日期时间
     */
    public static String getCurrentDate(int dayDiffer) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, dayDiffer);
        return calendar.get(Calendar.YEAR) + "_" + (calendar.get(Calendar.MONTH) + 1) + "_" + calendar.get(Calendar.DAY_OF_MONTH);
    }

    /**
     * 获取MPAndroidChart的格式化x轴坐标值
     *
     * @param value
     * @return
     */
    public static String getFormatAxisValue(String value) {
        String result;
        int dateDiffInDay = get2DateDiffInDay(value, getCurrentDate());
        if (dateDiffInDay > 0) {//大于0是前面的时间
            if (dateDiffInDay == 1) {
                result = "昨\n\n天";
            } else if (dateDiffInDay == 2) {
                result = "前\n\n天";
            } else {
                result = dateDiffInDay + "\n天\n前";
            }
        } else if (dateDiffInDay == 0) {
            result = "今\n\n天";
        } else {
            if (dateDiffInDay == -1) {
                result = "明\n\n天";
            } else if (dateDiffInDay == -2) {
                result = "后\n\n天";
            } else {
                result = Math.abs(dateDiffInDay) + "\n天\n后";
            }
        }
        return result;
    }

    /**
     * 获取当前时间(返回是签到所需时间格式)
     *
     * @return
     */
    public static String getCurrentDateInSigninFormat() {
        Calendar calendar = Calendar.getInstance();
        return calendar.get(Calendar.YEAR) + "." + (calendar.get(Calendar.MONTH) + 1) + "." + calendar.get(Calendar.DAY_OF_MONTH);
    }
}
