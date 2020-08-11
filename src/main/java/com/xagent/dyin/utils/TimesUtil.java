package com.xagent.dyin.utils;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.Calendar;
import java.util.Date;

public class TimesUtil
{
    // Long millisecond = Instant.now().toEpochMilli();  // 精确到毫秒
    // Long second = Instant.now().getEpochSecond();// 精确到秒

    // long类型的时间戳转成字符串
    public static String convertTimeToString(long time){
        DateTimeFormatter ftf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return ftf.format(LocalDateTime.ofInstant(Instant.ofEpochMilli(time), ZoneId.systemDefault()));  // ZoneId.of("Asia/Shanghai")
    }

    public static String convertTimeToDayString(long time){
        DateTimeFormatter ftf = DateTimeFormatter.ofPattern("MM_dd");
        return ftf.format(LocalDateTime.ofInstant(Instant.ofEpochMilli(time), ZoneId.systemDefault()));  // ZoneId.of("Asia/Shanghai")
    }

    // 将字符串时间转成long类型的时间戳
    public static long convertTimeToLong(String time) {
        DateTimeFormatter ftf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime parse = LocalDateTime.parse("2018-05-29 13:52:50", ftf);
        return LocalDateTime.from(parse).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }

    // 本月第一天
    public static LocalDate firstDayOfThisMonth() {
        LocalDate today = LocalDate.now();
        return today.with(TemporalAdjusters.firstDayOfMonth());
    }

    // 取本月第一天的开始时间
    public static LocalDateTime startOfThisMonth() {
        return LocalDateTime.of(firstDayOfThisMonth(), LocalTime.MIN);
    }
    // 本月最后一天
    public static LocalDate lastDayOfThisMonth() {
        LocalDate today = LocalDate.now();
        return today.with(TemporalAdjusters.lastDayOfMonth());
    }
    // 取本月最后一天的结束时间
    public static LocalDateTime endOfThisMonth() {
        return LocalDateTime.of(lastDayOfThisMonth(), LocalTime.MAX);
    }

    public static long zeroTimeOfCentainDay(long millSec)
    {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(millSec);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTimeInMillis();
    }
    public static long endTimeOfCentainDay(long millSec)
    {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(millSec);
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.SECOND, 59);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.MILLISECOND, 999);
        return cal.getTimeInMillis();
    }

    public static long zeroTimOfToday()
    {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTimeInMillis();
    }
    public static long endTimOfToday()
    {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        cal.set(Calendar.MILLISECOND, 999);
        return cal.getTimeInMillis();
    }

    public static boolean isInSameDay(long ta, long tb)
    {
        Calendar cal_a = Calendar.getInstance();
        cal_a.setTimeInMillis(ta);

        Calendar cal_b = Calendar.getInstance();
        cal_b.setTimeInMillis(tb);

        return cal_a.get(Calendar.DAY_OF_YEAR) == cal_b.get(Calendar.DAY_OF_YEAR);
    }

    public static Date getBeginTimeOfMonth(int year, int month) {
        YearMonth yearMonth = YearMonth.of(year, month);
        LocalDate localDate = yearMonth.atDay(1);
        LocalDateTime startOfDay = localDate.atStartOfDay();
        ZonedDateTime zonedDateTime = startOfDay.atZone(ZoneId.of("Asia/Shanghai"));

        return Date.from(zonedDateTime.toInstant());
    }

    public static Date getEndTimeOfMonth(int year, int month) {
        YearMonth yearMonth = YearMonth.of(year, month);
        LocalDate endOfMonth = yearMonth.atEndOfMonth();
        LocalDateTime localDateTime = endOfMonth.atTime(23, 59, 59, 999);
        ZonedDateTime zonedDateTime = localDateTime.atZone(ZoneId.of("Asia/Shanghai"));
        return Date.from(zonedDateTime.toInstant());
    }

    public static String secondToString(long second)
    {
        if (second <= 0)
        {
            return "不足1分钟";
        }

        String str = "";
        long day = second/3600/24;
        long hour = second/3600 % 24;
        long minute = second/60 % 60;
        long scnd = second%60;

        if (day > 0)
        {
            str += day + "天";
        }
        if (hour > 0)
        {
            str += hour + "小时";
        }
        if (minute > 0)
        {
            str += minute + "分钟";
        }
        if (scnd > 0)
        {
            str += scnd + "秒";
        }

        return str;
    }
}
