package com.hzy.restaurant.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class TimeUtils {
    /**
     * 年月日 时分秒
     */
    public static String getyyyyMMddHHmmssTime(long time) {
        SimpleDateFormat simpleDateFormat = (SimpleDateFormat) SimpleDateFormat.getInstance();
        simpleDateFormat.applyPattern("yyyy-MM-dd HH:mm:ss");
        return simpleDateFormat.format(new Date(time));
    }

    /**
     * 时分
     */
    public static String getHHmmTime(long time) {
        SimpleDateFormat simpleDateFormat = (SimpleDateFormat) SimpleDateFormat.getInstance();
        simpleDateFormat.applyPattern("HH:mm");
        return simpleDateFormat.format(new Date(time));
    }

    /**
     * 时分
     */
    public static Date getHHmm2Date(String time) {
        SimpleDateFormat simpleDateFormat = (SimpleDateFormat) SimpleDateFormat.getInstance();
        simpleDateFormat.applyPattern("HH:mm");
        Date newTime;
        try {
            newTime = simpleDateFormat.parse(time);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        return newTime;
    }


    public static String plusHours (int hour, String time) {
        SimpleDateFormat simpleDateFormat = (SimpleDateFormat) SimpleDateFormat.getInstance();
        simpleDateFormat.applyPattern("HH:mm");
        String newTime;
        try {
            Date date = simpleDateFormat.parse(time);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            calendar.add(Calendar.HOUR, hour);
            newTime = simpleDateFormat.format(calendar.getTime());
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        return newTime;
    }

    public static String plusMinus(int minus, String time) {
        SimpleDateFormat simpleDateFormat = (SimpleDateFormat) SimpleDateFormat.getInstance();
        simpleDateFormat.applyPattern("HH:mm");
        String newTime;
        try {
            Date date = simpleDateFormat.parse(time);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            calendar.add(Calendar.MINUTE, minus);
            newTime = simpleDateFormat.format(calendar.getTime());
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        return newTime;
    }

    /**
     * 时
     */
    public static String getHHTime(long time) {
        SimpleDateFormat simpleDateFormat = (SimpleDateFormat) SimpleDateFormat.getInstance();
        simpleDateFormat.applyPattern("HH");
        return simpleDateFormat.format(new Date(time));
    }
    /**
     * 分
     */
    public static String getmmTime(long time) {
        SimpleDateFormat simpleDateFormat = (SimpleDateFormat) SimpleDateFormat.getInstance();
        simpleDateFormat.applyPattern("mm");
        return simpleDateFormat.format(new Date(time));
    }

    /**
     * 年月日
     */
    public static String getyyyyMMddTime(long time) {
        SimpleDateFormat simpleDateFormat = (SimpleDateFormat) SimpleDateFormat.getInstance();
        simpleDateFormat.applyPattern("yyyy-MM-dd");
        return simpleDateFormat.format(new Date(time));
    }

    /**
     * 年
     */
    public static String getYyyy(long time) {
        SimpleDateFormat simpleDateFormat = (SimpleDateFormat) SimpleDateFormat.getInstance();
        simpleDateFormat.applyPattern("yyyy");
        return simpleDateFormat.format(new Date(time));
    }

    /**
     * 年月
     */
    public static String getYyyyMM(long time) {
        SimpleDateFormat simpleDateFormat = (SimpleDateFormat) SimpleDateFormat.getInstance();
        simpleDateFormat.applyPattern("yyyy_MM");
        return simpleDateFormat.format(new Date(time));
    }

    /**
     * 年月-日历弹框
     */
    public static String getYyyyMM2Dialog(long time) {
        SimpleDateFormat simpleDateFormat = (SimpleDateFormat) SimpleDateFormat.getInstance();
        simpleDateFormat.applyPattern("yyyyMM");
        return simpleDateFormat.format(new Date(time));
    }

    /**
     * 年月
     */
    public static String getyyyyMMTime(long time) {
        SimpleDateFormat simpleDateFormat = (SimpleDateFormat) SimpleDateFormat.getInstance();
        simpleDateFormat.applyPattern("yyyy-MM");
        return simpleDateFormat.format(new Date(time));
    }

    /**
     * 年月
     */
    public static String getCNyyyyMMTime(long time) {
        SimpleDateFormat simpleDateFormat = (SimpleDateFormat) SimpleDateFormat.getInstance();
        simpleDateFormat.applyPattern("yyyy年MM月");
        return simpleDateFormat.format(new Date(time));
    }

    /**
     * 月 中文
     */
    public static String getMMTimeForCN(long time) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM", Locale.CHINESE);
        return simpleDateFormat.format(new Date(time));
    }

    /**
     * 年
     */
    public static String getyyyyTimeForCN(long time) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy", Locale.CHINESE);
        return simpleDateFormat.format(new Date(time));
    }

    /**
     * 年月日 中文
     */
    public static String getyyyyMMddTimeForCN(long time) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINESE);
        return simpleDateFormat.format(new Date(time));
    }

    /**
     * 日
     */
    public static String getddTime(long time) {
        SimpleDateFormat simpleDateFormat = (SimpleDateFormat) SimpleDateFormat.getInstance();
        simpleDateFormat.applyPattern("dd");
        return simpleDateFormat.format(new Date(time));
    }

    /**
     * 日
     */
    public static String getdTime(long time) {
        SimpleDateFormat simpleDateFormat = (SimpleDateFormat) SimpleDateFormat.getInstance();
        simpleDateFormat.applyPattern("d");
        return simpleDateFormat.format(new Date(time));
    }

    /**
     * 秒
     */
    public static String getssTime(long time) {
        SimpleDateFormat simpleDateFormat = (SimpleDateFormat) SimpleDateFormat.getInstance();
        simpleDateFormat.applyPattern("ss");
        return simpleDateFormat.format(new Date(time));
    }

    /**
     * 月日 时分
     */
    public static String getMMddHHmmTime(long time) {
        SimpleDateFormat simpleDateFormat = (SimpleDateFormat) SimpleDateFormat.getInstance();
        simpleDateFormat.applyPattern("MM月dd日 HH:mm");
        return simpleDateFormat.format(new Date(time));
    }

    /**
     * 月日
     */
    public static String getMMddTime(long time) {
        SimpleDateFormat simpleDateFormat = (SimpleDateFormat) SimpleDateFormat.getInstance();
        simpleDateFormat.applyPattern("MM月dd日");
        return simpleDateFormat.format(new Date(time));
    }

    //毫秒换成00:00:00
    public static String getCountTimeByLong(long finishTime) {
        int totalTime = (int) (finishTime / 1000);//秒
        int hour = 0, minute = 0, second = 0;

        if (3600 <= totalTime) {
            hour = totalTime / 3600;
            totalTime = totalTime - 3600 * hour;
        }
        if (60 <= totalTime) {
            minute = totalTime / 60;
            totalTime = totalTime - 60 * minute;
        }
        if (0 <= totalTime) {
            second = totalTime;
        }
        StringBuilder sb = new StringBuilder();

        if (hour < 10) {
            sb.append("0").append(hour).append(":");
        } else {
            sb.append(hour).append(":");
        }
        if (minute < 10) {
            sb.append("0").append(minute).append(":");
        } else {
            sb.append(minute).append(":");
        }
        if (second < 10) {
            sb.append("0").append(second);
        } else {
            sb.append(second);
        }
        return sb.toString();

    }

    public static long getLong(String time) {
        SimpleDateFormat simpleDateFormat = (SimpleDateFormat) SimpleDateFormat.getInstance();
        simpleDateFormat.applyPattern("yyyy-MM-dd");
        long s = 0;
        try {
            Date date = simpleDateFormat.parse(time);
            if (date != null) {
                s = date.getTime();
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return s;
    }


    /**
     * 月日
     */
    public static String getYyyyMMddTimeEN(long time) {
        SimpleDateFormat simpleDateFormat = (SimpleDateFormat) SimpleDateFormat.getInstance();
        simpleDateFormat.applyPattern("yyyy-MM-dd");
        return simpleDateFormat.format(new Date(time));
    }

    public static long getLongFormString(String time) {
        SimpleDateFormat simpleDateFormat = (SimpleDateFormat) SimpleDateFormat.getInstance();
        simpleDateFormat.applyPattern("yyyy-MM-dd HH:mm");
        long s = 0;
        try {
            Date date = simpleDateFormat.parse(time);
            if (date != null) {
                s = date.getTime();
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return s;
    }
}
