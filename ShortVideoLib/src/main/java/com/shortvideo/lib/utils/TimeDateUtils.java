package com.shortvideo.lib.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class TimeDateUtils {

    /**
     * 24小时制转化成12小时制
     */
    public static String timeFormatStr(long t) {
        Calendar calendarTime = Calendar.getInstance();
        calendarTime.setTimeInMillis(t);
        String tempStr = formatDate(t, "hh:mm");
        int hour = calendarTime.get(Calendar.HOUR_OF_DAY);
        if (hour > 11) {
            tempStr = tempStr + " PM";
        } else {
            tempStr = tempStr + " AM";
        }
        return tempStr;
    }

    public static String formatDate(long time, String fromat) {
        SimpleDateFormat sdf = new SimpleDateFormat(fromat, Locale.CHINESE);
        String temp = time + "";
        if (temp.length() == 10)
            time = time * 1000;
        return sdf.format(new Date(time));
    }

    public static String formatDate(Date time, String fromat) {
        SimpleDateFormat sdf = new SimpleDateFormat(fromat, Locale.CHINESE);
        return sdf.format(time);
    }

    /**
     * 获取近几个月的日期
     *
     * @param month
     * @return
     */
    public static long getMonthDate(int month) {
        Calendar calendarTime = Calendar.getInstance();
        calendarTime.setTimeInMillis(getCurTimeLong());
        calendarTime.add(Calendar.MONTH, month);
        return Math.max(calendarTime.getTime().getTime(), getSixMonthDay());
    }

    /**
     * 获取近几天的日期
     *
     * @param day
     * @return
     */
    public static long getDayDate(int day) {
        Calendar calendarTime = Calendar.getInstance();
        calendarTime.setTimeInMillis(getCurTimeLong());
        calendarTime.add(Calendar.DAY_OF_MONTH, day);
        return Math.max(calendarTime.getTime().getTime(), getSixMonthDay());
    }

    /**
     * 获取某天的近几天的日期
     *
     * @param date
     * @param day
     * @return
     */
    public static long getWhichDayDate(Date date, int day) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DAY_OF_MONTH, day);
        return Math.max(calendar.getTime().getTime(), getSixMonthDay());
    }

    /**
     * 获取首尾时间中间的日期
     */
    public static List<Long> getAllDays(long begin, long end) {
        if ((begin + "").length() == 13)
            begin = begin / 1000;
        if ((end + "").length() == 13)
            end = end / 1000;
        List<Long> allDate = new ArrayList();
        allDate.add(begin);
        Calendar calBegin = Calendar.getInstance();
        // 使用给定的 Date 设置此 Calendar 的时间
        calBegin.setTimeInMillis(begin * 1000);
        Calendar calEnd = Calendar.getInstance();
        // 使用给定的 Date 设置此 Calendar 的时间
        calEnd.setTimeInMillis(end * 1000);
        // 测试此日期是否在指定日期之后
        while (new Date(end * 1000).after(calBegin.getTime())) {
            // 根据日历的规则，为给定的日历字段添加或减去指定的时间量
            calBegin.add(Calendar.DAY_OF_MONTH, 1);
            allDate.add(calBegin.getTime().getTime() / 1000);
        }
        return allDate;
    }

    /**
     * 获取系统时间戳
     *
     * @return
     */
    public static long getCurTimeLong() {
        return System.currentTimeMillis();
    }

    /**
     * 获取当前时间
     *
     * @param pattern
     * @return
     */
    public static String getCurDate(String pattern) {
        SimpleDateFormat sDateFormat = new SimpleDateFormat(pattern, Locale.CHINESE);
        return sDateFormat.format(new Date());
    }

    /**
     * 时间戳转换成字符窜
     *
     * @param milSecond
     * @param pattern
     * @return
     */
    public static String getDateToString(long milSecond, String pattern) {
        Date date = new Date(milSecond);
        SimpleDateFormat format = new SimpleDateFormat(pattern, Locale.CHINESE);
        return format.format(date);
    }

    /**
     * 将字符串转为时间戳
     *
     * @param dateString
     * @param pattern
     * @return
     */
    public static long getStringToDate(String dateString, String pattern) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(pattern, Locale.CHINESE);
        Date date = new Date();
        try {
            date = dateFormat.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date != null ? date.getTime() : 0;
    }


    /**
     * 将字符串时间转成时间戳
     *
     * @param serverTime 字符串
     * @param format     解析格式
     * @return
     */
    public static Date parseServerTime(String serverTime, String format) {
        if (format == null || format.isEmpty())
            format = "yyyy-MM-dd HH:mm:ss";
        SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.CHINESE);
        sdf.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
        Date date = null;
        try {
            date = sdf.parse(serverTime);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return date;
    }

    /**
     * 获取某个日期前后N天的日期
     *
     * @param beginDate
     * @param distanceDay 前后几天 如获取前7天日期则传-7即可；如果后7天则传7
     * @param format      日期格式，默认"yyyy-MM-dd"
     * @return
     */
    public static String getOldDateByDay(Date beginDate, int distanceDay, String format) {
        if (format == null || format.isEmpty())
            format = "yyyy-MM-dd";
        SimpleDateFormat dft = new SimpleDateFormat(format, Locale.CHINESE);
        Calendar date = Calendar.getInstance();
        date.setTime(beginDate);
        date.set(Calendar.DATE, date.get(Calendar.DATE) + distanceDay);
        Date endDate = null;
        try {
            endDate = dft.parse(dft.format(date.getTime()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dft.format(endDate);
    }


    public static int getDateDays(long firstData, long lastData) {
        if (firstData > 0 && lastData > 0)
            return (int) (lastData - firstData) / (24 * 60 * 60);
        return 0;
    }

    /**
     * 获取2021年6月1号的时间戳
     *
     * @return
     */
    private static long getSixMonthDay() {
        return getStringToDate("2021-06-01", "yyyy-MM-dd");
    }
}
