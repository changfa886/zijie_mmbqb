package com.xagent.dyin.utils;

import java.time.*;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PublicMethod
{
    public static boolean isDigital(String str)
    {
        Pattern pattern = Pattern.compile("[0-9]*");
        Matcher isNum = pattern.matcher(str);
        if (!isNum.matches())
        {
            return false;
        }
        return true;
    }

    public static String randomDigital(int len)
    {
        Random random = new Random();
        String result = "";
        for (int i = 0; i < len; i++)
        {
            result += random.nextInt(10);
        }
        return result;
    }

    public static boolean isMobilePhone(String mobiles)
    {
        Pattern p = Pattern.compile("^(13|14|15|16|17|18|19)\\d{9}$");
        Matcher m = p.matcher(mobiles);
        return m.matches();
    }

    public static boolean isLetterDigital(String str)
    {
        Pattern p = Pattern.compile("^[a-zA-Z0-9]+$");
        //Pattern p = Pattern.compile("^\\w+$");  // \w 是字母数字下划线
        Matcher m = p.matcher(str);
        return m.matches();
    }

    public static boolean isValidUsename(String username)
    {
        //字母、数字、下划线组成,以字母开头,长度在 6~15
        Pattern p = Pattern.compile("^[a-zA-Z][a-zA-Z0-9_]{5,14}$");
        Matcher m = p.matcher(username);
        return m.matches();
    }

    public static boolean isValidPasswd(String passwd)
    {
        //长度 6~15
        int length = passwd.length();
        if (length < 6 || length > 15)
        {
            return false;
        }
        return true;
    }

    public static boolean isChineseName(String name)
    {
        Pattern pattern = Pattern.compile("^([\u4E00-\uFA29]|[\uE7C7-\uE7F3]){2,5}$");
        Matcher matcher = pattern.matcher(name);
        if (matcher.find())
        {
            return true;
        }
        return false;
    }
    public static boolean isChineseString(String name)
    {
        Pattern pattern = Pattern.compile("^([\u4E00-\uFA29]|[\uE7C7-\uE7F3])+$");
        Matcher matcher = pattern.matcher(name);
        if (matcher.find())
        {
            return true;
        }
        return false;
    }

    // 是否包含中文
    public static boolean isContainChinese(String str)
    {
        Pattern p = Pattern.compile("[\u4e00-\u9fa5]");
        Matcher m = p.matcher(str);
        if (m.find())
        {
            return true;
        }
        return false;
    }

    // 只允许中文和字母数字
    public static boolean isNickname(String name)
    {
        Pattern pattern = Pattern.compile("^([\u4E00-\uFA29]|[\uE7C7-\uE7F3]|[a-z]|[A-Z]|[0-9])+$");
        Matcher matcher = pattern.matcher(name);
        if (matcher.find())
        {
            return true;
        }
        return false;
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


    // 是否在指定时间范围
    public static boolean isTormbTime()
    {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());

        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        //int minute = calendar.get(Calendar.MINUTE);
        int week = calendar.get(Calendar.DAY_OF_WEEK);

        // 工作日 09：00-18：00
        if (week == Calendar.SUNDAY || week == Calendar.SATURDAY)
        {
            return false;
        }
        if (hour < 9 || hour >= 17)
        {
            return false;
        }
        return true;
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


    public static String hideMiddleName(String src)
    {
        if (src==null || src.isEmpty())
        {
            return "";
        }
        String sRet = src;
        int len = src.length();
        if (len == 2)
        {
            sRet = "*" + src.substring(1);
        }
        else if (len > 2)
        {
            sRet = src.substring(0, 1);
            for (int i = 1; i < (len - 1); i++)
            {
                sRet += "*";
            }
            sRet += src.substring(len - 1);
        }

        return sRet;
    }
    public static String hideMobile(String mobile)
    {
        return mobile.substring(0,3)+"****"+mobile.substring(7);
    }

    public static String fenToYuan(int amount)
    {
        if (amount == 0)
        {
            return "0";
        }

        int flag = 0;
        String amString = amount + "";
        if (amString.charAt(0) == '-')
        {
            flag = 1;
            amString = amString.substring(1);
        }

        StringBuffer result = new StringBuffer();
        if (amString.length() == 1)
        {
            result.append("0.0").append(amString);
        }
        else if (amString.length() == 2)
        {
            result.append("0.").append(amString);
        }
        else
        {
            result.append(amString.substring(0, amString.length() - 2)).append(".").append(amString.substring(amString.length() - 2));
        }

        if (flag == 1)
        {
            return "-" + result.toString();
        }
        else
        {
            return result.toString();
        }
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

    // 巨坑 禁止使用
    public static boolean isHttpUrl(String content)
    {
        Pattern pattern = Pattern.compile("^([hH][tT]{2}[pP]:/*|[hH][tT]{2}[pP][sS]:/*|[fF][tT][pP]:/*)(([A-Za-z0-9-~]+).)+([A-Za-z0-9-~\\/])+(\\?{0,1}(([A-Za-z0-9-~]+\\={0,1})([A-Za-z0-9-~]*)\\&{0,1})*)$");
        Matcher matcher = pattern.matcher(content);
        if (matcher.find())
        {
            return true;
        }
        return false;
    }
}
