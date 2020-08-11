package com.xagent.dyin.utils;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;

public class DataTimeUtil
{
    // Long millisecond = Instant.now().toEpochMilli();  // 精确到毫秒
// Long second = Instant.now().getEpochSecond();// 精确到秒

    // Long类型的时间戳转成字符串
    public static String convertTimeToString(Long time){
        DateTimeFormatter ftf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return ftf.format(LocalDateTime.ofInstant(Instant.ofEpochMilli(time), ZoneId.systemDefault()));  // ZoneId.of("Asia/Shanghai")
    }

    // 将字符串时间转成Long类型的时间戳
    public static Long convertTimeToLong(String time) {
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
}
