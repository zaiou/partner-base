package com.zaiou.common.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.locale.converters.DateLocaleConverter;
import org.apache.commons.lang.time.DateFormatUtils;

import java.text.*;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

/**
 * @Description:
 * @auther: LB 2018/9/19 20:00
 * @modify: LB 2018/9/19 20:00
 */
@Slf4j
public class DateUtils extends DateFormatUtils {


    public static final String format1 = "yyyyMMdd";

    public static final String format2 = "yyyy-MM-dd HH:mm:ss";

    public static final String format3 = "yyyyMMddHHmmss";

    public static final String format4 = "yyyy-MM-dd HH:mm";

    public static final String format5 = "yyyy-MM-dd";

    public static final String format6 = "HH:mm:ss.SSS";

    public static final String format7 = "yyyy.MM.dd";

    public static final String format8 = "yyyy.MM.dd HH:mm:ss";

    public static final String format9 = "dd-MM-yyyy HH:mm:ss";

    public static final String ddMMyyyy = "ddMMyyyy";

    public static final String yyMMdd = "yyMMdd";

    public static final String HHmmss = "HHmmss";

    /**
     * 日期-中文格式
     */
    public static final String formatCN = "yyyy年MM月dd日";

    /**
     * 日期-英文格式
     */
    public static final String formatEN = "dd MM yyyy";

    /**
     * 日期-巴文格式
     */
    public static final String formatUR = "dd-MM-yyyy";

    /**
     * 日期-中文格式
     */
    public static final String timeCN = "HH时mm分ss秒";

    /**
     * 日期-英文格式
     */
    public static final String timeEN = "HH:mm:ss";

    /**
     * 日期-巴文格式
     */
    public static final String timeUR = "HH:mm:ss";

    public static final String formatURTime = "dd-MM-yyyy HH:mm:ss";

    /**
     * 1毫秒
     */
    public static final Long MS = 1L;
    /**
     * 1秒
     */
    public static final Long SECOND = 1000 * MS;
    /**
     * 1分钟
     */
    public static final Long MINUTE = 60 * SECOND;
    /**
     * 1小时
     */
    public static final Long HOUR = 60 * MINUTE;
    /**
     * 1天
     */
    public static final Long DAY = 24 * HOUR;
    /**
     * 1年(闰年)
     */
    public static final Long YEAR_LEAP = 366 * DAY;
    /**
     * 1年(平年)
     */
    public static final Long YEAR_AVERAGE = 365 * DAY;
    /**
     * 1年(默认为平年)
     */
    public static final Long YEAR = 1 * YEAR_AVERAGE;

    /**
     * 日期格式转换
     * eg=> fromFormat:yyyyMMdd ---> toFormat:yyyy-MM-dd
     * @param date fromFormat格式的时间字符串
     * @param fromFormat
     * @param toFormat
     * @return
     */
    public static String format(String date, String fromFormat, String toFormat) {
        try {
            SimpleDateFormat sdf1 = new SimpleDateFormat(fromFormat);
            SimpleDateFormat sdf2 = new SimpleDateFormat(toFormat);
            return sdf2.format(sdf1.parse(date));
        } catch (ParseException e) {
            return null;
        }
    }

    /**
     * 巴方日期格式dd-MM-yyyy转换成数据库格式
     * eg=>  dd-MM-yyyy ---> yyyyMMdd
     * @param date
     * @return
     */
    public static String formatPageDateToSystem(String date) {

        SimpleDateFormat sdfFormatUR = new SimpleDateFormat(formatUR);
        SimpleDateFormat sdfFormatPage = new SimpleDateFormat(format1);
        String pageDate = null;
        try {
            pageDate = sdfFormatPage.format(sdfFormatUR.parse(date));
        } catch (ParseException e) {
            return null;
        }
        return pageDate;
    }


    /**
     * 根据语言标志格式化日期
     *  eg=> dd-MM-yyyy ---> yyyyMMdd
     * @param date 日期
     * @param dateFormat 参数date日期的格式
     * @param language 语言类型 0 中文 1 英文 2 巴文 默认英文 3 数据库存储日期类型 4页面日期时间类型
     */
    public static String formatDateByLanguage(String date, String dateFormat, String language) {
        if (org.apache.commons.lang.StringUtils.isBlank(date)) {
            return "";
        }
        // 日期格式
        String format = "";

        // 语音环境
        Locale locale = null;

        switch (language) {
            case "0":// 中文
                format = formatCN;
                break;
            case "1":// 英文
                format = formatEN;
                break;
            case "2":// 巴文
                format = formatUR;
                break;
            case "3":// format1系统格式
                format = format1;
                break;
            case "4":// format9页面时间格式
                format = format9;
                break;
            default:// 默认英文
                format = formatEN;
                locale = Locale.ENGLISH;
                break;
        }
        String formatDate = "";
        try {
            // 格式化日期
            SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
            SimpleDateFormat toFormat;
            if (null != locale) {
                toFormat = new SimpleDateFormat(format, locale);
            } else {
                toFormat = new SimpleDateFormat(format);
            }

            formatDate = toFormat.format(sdf.parse(date));
        } catch (ParseException e) {
            formatDate = date;
        }
        return formatDate;
    }

    /**
     * 根据语言标志格式化时间
     * eg=> HH时mm分ss秒 ---> HHmmss
     * @param time 时间
     * @param timeFormat 参数time时间的格式
     * @param language 语言类型 0 中文 1 英文 2 巴文 默认英文 3 数据库存储时间类型
     */
    public static String formatTimeByLanguage(String time, String timeFormat, String language) {

        // 日期格式
        String format = "";

        // 语音环境
        Locale locale = null;

        switch (language) {
            case "0":// 中文
                format = timeCN;
                break;
            case "2":// 巴文
                format = timeUR;
                break;
            case "3":// 数据库存储时间类型
                format = HHmmss;
                break;
            default:// 默认英文
                format = timeEN;
                locale = Locale.ENGLISH;
                break;
        }

        String formatTime = "";
        try {
            // 格式化日期
            SimpleDateFormat sdf;
            sdf = new SimpleDateFormat(timeFormat);
            SimpleDateFormat toFormat;
            if (null != locale) {
                toFormat = new SimpleDateFormat(format, locale);
            } else {
                toFormat = new SimpleDateFormat(format);
            }
            formatTime = toFormat.format(sdf.parse(time));
        } catch (ParseException e) {
            formatTime = time;
        }

        return formatTime;
    }

    /**
     * 1970年1月1日0时0分0秒到现在的毫秒数格式化
     * eg=> 1543646970000 ---> 14时49分30秒
     * @param cur_millis  1970年1月1日0时0分0秒到现在的毫秒数
     * @param strformat 需要格式化成的时间格式
     * @return
     */
    public static String getCurDate(long cur_millis, String strformat) {
        return format(cur_millis, strformat);
    }

    /**
     *  获取当前时间
     *  eg=> 2018-12-01 14:53:05
     * @return
     */
    public static String getDate() {
        String d = new SimpleDateFormat(format2).format(Calendar.getInstance().getTime());
        return d;
    }

    /**
     * 获取某天月前或者后的多少个月的日期，根据preN 是正数还是负数
     * @Title: getPreTimeMoth
     * @param d 某天日期
     * @param preN 月前或月后多少月
     * @return
     */
    public static Date getPreTimeMoth(Date d, int preN) {
        Calendar instance = Calendar.getInstance();
        instance.setTime(d);
        instance.add(Calendar.MONTH, preN);
        return instance.getTime();
    }


    /**
     * 获取前N天的日期
     * eg=> yyyyMMdd
     * @param preN 数字天数
     * @return
     */
    public static String getPreDate(int preN) {
        Calendar instance = Calendar.getInstance();
        instance.add(Calendar.DAY_OF_MONTH, -preN);
        String d = new SimpleDateFormat(format1).format(instance.getTime());
        return d;
    }

    /**
     * 获取目标日期的前N天的日期
     * @param date yyyyMMdd
     * @param dateFormat 获取的日期类型
     * @param preN
     * @return
     */
    public static String getPreDate(String date, String dateFormat, int preN) {
        DateFormat df = new SimpleDateFormat(dateFormat);
        Calendar instance = Calendar.getInstance();
        try {
            instance.setTime(df.parse(date));
        } catch (ParseException e) {
            return getPreDate(preN);
        }
        instance.add(Calendar.DAY_OF_MONTH, -preN);
        String d = new SimpleDateFormat(format1).format(instance.getTime());
        return d;
    }


    /**
     * 获取目标日期的前N天的时间
     * @param date  yyyy-MM-dd HH:mm:ss
     * @param dateFormat  获取的日期类型
     * @param preN
     * @return
     */
    public static String getPreTime(String date, String dateFormat, int preN) {
        if (StringUtils.isEmpty(dateFormat))
            dateFormat = "yyyy-MM-dd HH:mm:ss";
        DateFormat df = new SimpleDateFormat(dateFormat);
        Calendar instance = Calendar.getInstance();
        try {
            instance.setTime(df.parse(date));
        } catch (ParseException e) {
            return getPreDate(preN);
        }
        instance.add(Calendar.DAY_OF_MONTH, -preN);
        String d = new SimpleDateFormat(dateFormat).format(instance.getTime());
        return d;
    }

    /**
     * @Description: 获取n月后的日期
     * @auther: LB 2018/7/24 22:00
     * @modify: LB 2018/7/24 22:00
     * @param: term 期限
     * @Param： type 1：年 2:月
     * @return:
     * @see:
     */
    public static Date getAddMonthDate(Date date, int term, int type) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.add(type, term);
        return cal.getTime();
    }

    /**
     * 获取当前时间
     * @param pattern 获取格式化日期类型
     * @return
     */
    public static String getCurDate(String pattern) {
        if (StringUtils.isEmpty(pattern)) {
            pattern = format5;
        }
        String d = new SimpleDateFormat(pattern).format(Calendar.getInstance().getTime());
        return d;
    }


    /**
     * 比较两个日期的大小
     * eg=> date1>date2 返回 1
     *      date1<date2 返回 -1
     *      date1=date2 返回 0
     * @param date1
     * @param date2
     * @param format  date1/date2的日期格式
     * @return
     */
    public static int compareDate(String date1, String date2, String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        long d1 = 0;
        long d2 = 0;
        try {
            d1 = sdf.parse(date1).getTime();
            d2 = sdf.parse(date2).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (d1 < d2)
            return -1;
        if (d1 == d2) {
            return 0;
        }
        return 1;
    }

    /**
     * 验证输入的是否是日历上的日期
     * eg=> 是 返回0
     *      不是 返回1
     * @param date
     * @return
     */
    public static String dateIsR(String date) {
        int year = Integer.parseInt(date.substring(0, 4));
        int mon = Integer.parseInt(date.substring(4, 6));
        int day = Integer.parseInt(date.substring(6, 8));
        String M2 = "28";
        if (year % 4 == 0) {
            M2 = "29";
        }
        String[] Mday = new String[] { "31", M2, "31", "30", "31", "30", "31", "31", "30", "31", "30", "31" };
        if (mon > 12 || mon == 0) {
            return "1";

        }
        int tDay = Integer.parseInt(Mday[(mon - 1)]);
        if (day > tDay || day == 0) {
            return "0";
        }
        return "";

    }

    /**
     * 修改日期
     * @param baseDate 基础日期
     * @param field 修改日期字段 -年,月,日  - 1:年 2：月 3：周 4：周 5：天
     * @param amount 数量
     * @return
     */
    public static String dateModify(String baseDate, int field, int amount) {
        StringBuffer sb = new StringBuffer();

        SimpleDateFormat sdf = new SimpleDateFormat(format1); // 将字符串格式化
        Date dt = sdf.parse(baseDate, new ParsePosition(0)); // 由格式化后的字符串产生一个Date对象

        Calendar c = Calendar.getInstance(); // 初始化一个Calendar
        c.setTime(dt); // 设置基准日期
        c.add(field, amount); // 你要加减的日期
        Date dt1 = c.getTime(); // 从Calendar对象获得基准日期增减后的日期
        sb = sdf.format(dt1, sb, new FieldPosition(0)); // 得到结果字符串

        return sb.toString();
    }

    /**
     *  返回 date1 是否大于 date2
     *
     * @param dfs 例如 "yyyyMMdd HHmmss"
     * @param date1
     * @param date2
     * @return
     */
    public static boolean dateCompare(String dfs, String date1, String date2) {
        DateFormat df = new SimpleDateFormat(dfs);
        // 获取Calendar实例
        Calendar currentTime = Calendar.getInstance();
        Calendar compareTime = Calendar.getInstance();
        try {
            // 把字符串转成日期类型
            currentTime.setTime(df.parse(date1));
            compareTime.setTime(df.parse(date2));
        } catch (ParseException e) {
            log.error("日期比较异常={}",e.getMessage());
        }
        // 利用Calendar的方法比较大小
        return currentTime.compareTo(compareTime) > 0;
    }

    /**
     * 计算两个日期差
     * @param dfs 时间日期格式 如"yyyyMMdd HHmmss"
     * @param date1
     * @param date2
     * @param unit 计算结果返回的单位，默认为毫秒
     * @return date1 - date2
     */
    public static long dateDifferent(String dfs, String date1, String date2, Long unit) {
        DateFormat df = new SimpleDateFormat(dfs);
        // 获取Calendar实例
        Calendar currentTime = Calendar.getInstance();
        Calendar compareTime = Calendar.getInstance();
        try {
            // 把字符串转成日期类型
            currentTime.setTime(df.parse(date1));
            compareTime.setTime(df.parse(date2));

            Long l1 = currentTime.getTime().getTime();
            Long l2 = compareTime.getTime().getTime();

            Double result = l1.doubleValue() - l2.doubleValue();
            if (unit == null || unit.longValue() - 0L == 0) {
                unit = MS;
            }
            result = result / unit.doubleValue();
            return result.longValue();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0L;
    }

    /**
     * 时间字符串转化为时间类
     * @param dateStr 时间字符串
     * @param pattern dateStr对应的时间格式
     * @return
     */
    public static Date parseDate(String dateStr, String pattern) {
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        Date d = null;
        try {
            d = sdf.parse(dateStr);
        } catch (ParseException e) {
            log.error("时间字符串转化为时间类，转化异常={}",e.getMessage());
        }
        return d;
    }

    /**
     * 时间类转化为时间字读串
     * @param date
     * @param pattern 获取的时间字符串格式
     * @return
     */
    public static String DateToStr(Date date, String pattern) {
        if (StringUtils.isEmpty(pattern))
            pattern = "yyyy-MM-dd HH:mm:ss";
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        String str = sdf.format(date);
        return str;
    }


    /**
     * 当前时间格式字符串转化成其他时间格式字符串
     * eg=> 2018-12-01 12:12:12 ---> 20181201121212
     * @param date
     * @param originalFormat date时间格式字符串
     * @param toFormat 需要转换的时间格式字符串
     * @return
     */
    public static String format1Date(String date, String originalFormat, String toFormat) {

        SimpleDateFormat from = new SimpleDateFormat(originalFormat);
        SimpleDateFormat to = new SimpleDateFormat(toFormat);
        String result = "";
        try {
            Date tmpDate = from.parse(date);
            result = to.format(tmpDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return result;

    }

    /**
     *  通过时间秒毫秒数判断两个时间的间隔 date2-date1 单位：天
     * @param date1
     * @param date1Format date1对应的时间格式
     * @param date2
     * @param date2Format date2对应的时间格式
     * @return
     */
    public static int differentDays(String date1, String date1Format, String date2, String date2Format) {

        SimpleDateFormat sdf1 = new SimpleDateFormat(date1Format);
        SimpleDateFormat sdf2 = new SimpleDateFormat(date2Format);

        int days = 0;
        try {
            long t1 = sdf1.parse(date1).getTime();
            long t2 = sdf2.parse(date2).getTime();

            days = (int) ((t2 - t1) / (1000 * 3600 * 24));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return days;
    }

    /**
     * 获取当前时间距离当日24点剩余秒数
     * @return
     */
    public static int differentDays() {
        LocalDateTime midnight = LocalDateTime.now().plusDays(1).withHour(0).withMinute(0).withSecond(0).withNano(0);
        long seconds = ChronoUnit.SECONDS.between(LocalDateTime.now(), midnight);
        int sec = new Long(seconds).intValue();
        return sec;
    }

    /**
     * 获取目标日期是周几
     * eg=> Wed
     * @param dateStr 时间字符串
     * @param pattern dateStr时间字符串的格式
     * @return 当前日期是星期几
     */
    public static String getWeekOfDate(String dateStr, String pattern) {
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        try {
            Date dt = sdf.parse(dateStr);
            return getWeekOfDate(dt);
        } catch (ParseException e) {
            return "";
        }
    }

    /**
     * 获取目标日期是周几
     * eg=> Wed
     * @param dt
     * @return
     */
    public static String getWeekOfDate(Date dt) {
        String[] weekDays = { "Sun", "Mon", "Tues", "Wed", "Thurs", "Fri", "Sat" };
        Calendar cal = Calendar.getInstance();
        cal.setTime(dt);
        int w = cal.get(Calendar.DAY_OF_WEEK) - 1;
        if (w < 0)
            w = 0;
        return weekDays[w];
    }

    /***
     * 获取2个日期之间的所有日期 包含 startDate和endDate
     * eg=>  [20181201, 20181202, 20181203, 20181204, 20181205]
     * @param startDate
     * @param endDate
     * @param pattern startDate和endDate时间格式
     * @return
     */
    public static List<String> getEveryDay(String startDate, String endDate, String pattern) {
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        List<String> dates = new ArrayList<String>();

        try {
            Date start = sdf.parse(startDate);
            Date end = sdf.parse(endDate);
            Calendar gregorianCalendar = new GregorianCalendar();
            gregorianCalendar.setTime(start);
            dates.add(startDate);
            while (gregorianCalendar.getTime().compareTo(end) < 0) {
                // 加1天
                gregorianCalendar.add(Calendar.DAY_OF_MONTH, 1);
                Date theDate = gregorianCalendar.getTime();
                dates.add(sdf.format(theDate));
            }
            return dates;
        } catch (ParseException e) {
            return dates;
        }
    }

    /**
     * 获取当前卡槽的值
     **/
    public static final int getSlotIndex() {
        return Calendar.getInstance().get(Calendar.MINUTE) * 60 + Calendar.getInstance().get(Calendar.SECOND);
    }

    public static void main(String[] args) {
        log.info(String.valueOf(getSlotIndex()));
    }

    public static String formatDateByFormat(java.util.Date date, String format) {
        String result = "";
        if (date != null) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat(format);
                result = sdf.format(date);
            } catch (Exception ex) {
                return "";
            }
        }
        return result;
    }

    /**
     * 获取当天前n天和后n天零点的时间
     *
     * @param n
     *            负数的时候代表获取前面的日期，比如昨天，正数的时候获取后面的日期，比如明天
     */
    public static Date getZeroDate(int n, Date date) {
        Calendar cal = Calendar.getInstance();
        if (date != null) {
            cal.setTime(date);
        }
        cal.add(Calendar.DATE, n);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

    /**
     * 获取当天零点时间
     */
    public static Date getCurZeroDate(Date date) {
        return getZeroDate(0, date);
    }

    /**
     * 获取昨天零点时间
     */
    public static Date getYesterdayZeroDate(Date date) {
        return getZeroDate(-1, date);
    }

    /**
     * 获取明天零点时间
     */
    public static Date getTomorrowZeroDate(Date date) {
        return getZeroDate(1, date);
    }

    /**
     * 判断输入时间是否是月初第一天
     */
    public static boolean isFirtDayOnCurMoth(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int curDay = cal.get(Calendar.DAY_OF_MONTH);
        if (curDay == 1) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 获取前n月或者后n月的月初第一天零点的时间
     */
    public static Date getMothFirstDayZeroDate(int n, Date date) {
        Calendar cal = Calendar.getInstance();
        if (date != null) {
            cal.setTime(date);
        }
        cal.add(Calendar.MONTH, n);
        cal.set(Calendar.DAY_OF_MONTH, 1);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

    /**
     * 获取上月月初第一天零点的时间
     */
    public static Date getLastMothFirstDayZeroDate(Date date) {
        return getMothFirstDayZeroDate(-1, date);
    }

    /**
     * 获取本月月初第一天零点的时间
     */
    public static Date getCurMothFirstDayZeroDate(Date date) {
        return getMothFirstDayZeroDate(0, date);
    }

    /**
     * 判断是否是季度初第一天
     */
    public static boolean isQuarterFirstDay(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int dayOfMoth = cal.get(Calendar.DAY_OF_MONTH);
        int moth = cal.get(Calendar.MONTH) + 1;
        if (dayOfMoth == 1) {
            if (moth == 1 || moth == 4 || moth == 7 || moth == 10) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    /**
     * 判断是否是年初第一天
     */
    public static boolean isYearFirstDay(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int dayOfMoth = cal.get(Calendar.DAY_OF_MONTH);
        int moth = cal.get(Calendar.MONTH) + 1;
        if (dayOfMoth == 1) {
            if (moth == 1) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    /**
     * 获取当前日期季度初零点的时间
     */
    public static Date getQuarterFirstZeroDay(Date date) {
        Calendar cal = Calendar.getInstance();
        if (date != null) {
            cal.setTime(date);
        }
        int month = cal.get(Calendar.MONTH) + 1;
        cal.set(Calendar.DAY_OF_MONTH, 1);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        if (month >= 1 && month <= 3) {
            cal.set(Calendar.MONTH, 0);
            return cal.getTime();
        }
        if (month >= 4 && month <= 6) {
            cal.set(Calendar.MONTH, 3);
            return cal.getTime();
        }
        if (month >= 7 && month <= 9) {
            cal.set(Calendar.MONTH, 6);
            return cal.getTime();
        }
        if (month >= 10 && month <= 12) {
            cal.set(Calendar.MONTH, 9);
            return cal.getTime();
        }
        return null;
    }

    /**
     * 获取当前日期上季度初零点的时间
     */
    public static Date getLastQuarterFirstZeroDay(Date date) {
        Date curDate = getQuarterFirstZeroDay(date);
        Calendar cal = Calendar.getInstance();
        cal.setTime(curDate);
        cal.add(Calendar.MONTH, -3);
        return cal.getTime();
    }

    /**
     * 获取当前日期n年年初的时间
     */
    public static Date getYearFirstZeroDay(int n, Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.YEAR, n);
        cal.set(Calendar.MONTH, 0);
        cal.set(Calendar.DAY_OF_MONTH, 1);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

    /**
     * 获取当前日期去年年年初的时间
     */
    public static Date getLastYearFirstZeroDay(Date date) {
        return getYearFirstZeroDay(-1, date);
    }

    /**
     * 获取当前日期今年年年初的时间
     */
    public static Date getCurYearFirstZeroDay(Date date) {
        return getYearFirstZeroDay(0, date);
    }

    private final static SimpleDateFormat sdfReportDayFormat = new SimpleDateFormat("yyyyMMdd");
    private final static SimpleDateFormat sdfReportMonthFormat = new SimpleDateFormat("yyyyMM");
    private final static SimpleDateFormat sdfReportYearFormat = new SimpleDateFormat("yyyy");

    /**
     * 获取报表字符串日期
     */
    public static String getReportDayStr(Date date) {
        return sdfReportDayFormat.format(getYesterdayZeroDate(date));
    }

    public static String getReportMonthStr(Date date) {
        if (isFirtDayOnCurMoth(date)) {
            return sdfReportMonthFormat.format(getYesterdayZeroDate(date));
        } else {
            return sdfReportMonthFormat.format(date);
        }
    }

    public static String getReportQuarterStr(Date date) {
        Date quaDate = null;
        if (isQuarterFirstDay(date)) {
            quaDate = getLastQuarterFirstZeroDay(date);
        } else {
            quaDate = getQuarterFirstZeroDay(date);
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(quaDate);
        int month = cal.get(Calendar.MONTH) + 1;
        if (month == 1) {
            return sdfReportYearFormat.format(cal.getTime()) + "13";
        }
        if (month == 4) {
            return sdfReportYearFormat.format(cal.getTime()) + "46";
        }
        if (month == 7) {
            return sdfReportYearFormat.format(cal.getTime()) + "79";
        }
        if (month == 10) {
            return sdfReportYearFormat.format(cal.getTime()) + "02";
        }
        return null;
    }

    public static String getReportYearStr(Date date) {
        if (isYearFirstDay(date)) {
            return sdfReportYearFormat.format(getLastYearFirstZeroDay(date));
        } else {
            return sdfReportYearFormat.format(date);
        }
    }

    /**
     * 获取前n天日期集合
     */
    public static List<Date> getBackDateList(int n, Date date) {
        List<Date> listDates = new ArrayList<Date>();
        if (n > 0) {
            for (int i = 1; i <= n; i++) {
                listDates.add(getZeroDate(i, date));
            }
        } else {
            n = -n;
            for (int i = 1; i <= n; i++) {
                listDates.add(getZeroDate(-i, date));
            }
        }
        return listDates;
    }

    public static List<Date> getTwoDateDates(Date date1, Date date2){
        List<Date> dates = new ArrayList<Date>();
        if(date1 == null){
            dates.add(getCurZeroDate(date2));
            return dates;
        }
        Date dateZero1 = getCurZeroDate(date1);
        Date dateZero2 = getCurZeroDate(date2);
        if(dateZero1.getTime() >= dateZero2.getTime()){
            return dates;
        }
        int count = (int)((dateZero2.getTime() - dateZero1.getTime())/(24*60*60*1000));
        for(int i=1; i<=count; i++){
            dates.add(getZeroDate(i, dateZero1));
        }
        return dates;
    }

    public static int apartMonth(Date date) {
        Date d1 = getCurMothFirstDayZeroDate(date);
        Date d2 = getCurZeroDate(date);
        return (int) ((d2.getTime() - d1.getTime()) / (1000 * 3600 * 24));
    }

    public static int apartQuarter(Date date) {
        Date d1 = getQuarterFirstZeroDay(date);
        Date d2 = getCurZeroDate(date);
        return (int) ((d2.getTime() - d1.getTime()) / (1000 * 3600 * 24));
    }

    public static int apartYear(Date date) {
        Date d1 = getCurYearFirstZeroDay(date);
        Date d2 = getCurZeroDate(date);
        return (int) ((d2.getTime() - d1.getTime()) / (1000 * 3600 * 24));
    }

    public static String dateToStrWithFormat5(Date date) {
        return new SimpleDateFormat(format5).format(date);
    }

    public static Date strToDateWithFormat5(String str) {
        try {
            return new SimpleDateFormat(format5).parse(str);
        } catch (ParseException e) {
            return null;
        }
    }

    public static Date strToDateWithFormatCN(String str) {
        try {
            return new SimpleDateFormat(formatCN).parse(str);
        } catch (ParseException e) {
            return null;
        }
    }

    public static String dateToStrWithFormat1(Date date) {

        return new SimpleDateFormat(format1).format(date);
    }

    public static Date strToDateWithFormat1(String str) {
        try {
            return new SimpleDateFormat(format1).parse(str);
        } catch (ParseException e) {
            return null;
        }
    }

    public static String dateToStrWithFormat2(Date date) {
        return new SimpleDateFormat(format2).format(date);
    }

    public static Date strToDateWithFormat2(String str) {
        try {
            return new SimpleDateFormat(format2).parse(str);
        } catch (ParseException e) {
            return null;
        }
    }

    public static Date getLastMonthSameDay(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.MONTH, -1);
        return cal.getTime();
    }

    public static Date getLastYearEndZeroDay(Date date) {
        Date firstZeroDay = getCurYearFirstZeroDay(date);
        Calendar cal = Calendar.getInstance();
        cal.setTime(firstZeroDay);
        cal.add(Calendar.DAY_OF_MONTH, -1);
        return cal.getTime();
    }

    public static Date getHourZeroDate(int n, Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.HOUR_OF_DAY, n);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

    /**
     *
     * @Title: calcCurrTimeAndIdCardDirrDay
     * @Description: 计算身份证离当前日期相差多少天数
     * @author: liushuangxi 2018年7月19日
     * @modify: liushuangxi 2018年7月19日
     * @param idCard
     * @return
     * @throws Exception
     */
    public static int calcCurrTimeAndIdCardDirrDay(String idCard) throws Exception {
        String ymd = idCard.substring(6, 14);
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
        Date date = format.parse(ymd);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        Calendar calendar2 = Calendar.getInstance();
        calendar2.setTime(new Date());
        long time1 = calendar.getTimeInMillis();
        long time2 = calendar2.getTimeInMillis();
        long between_days = (time2 - time1) / (1000 * 3600 * 24);
        int day = Integer.parseInt(String.valueOf(between_days));
        return day;
    }

    /**
     *
     * @Title: calcAgeAndloanMaxAgeDirrMonth
     * @Description: 计算身份证的时间离最大贷款年龄相差多少月
     * @author: liushuangxi 2018年7月19日
     * @modify: liushuangxi 2018年7月19日
     * @param idCard
     * @param loadMaxAge
     * @return
     * @throws Exception
     */
    public static int calcAgeAndloanMaxAgeDirrMonth(String idCard, int loadMaxAge) throws Exception {
        // 获取身份证时间
        String ymd = idCard.substring(6, 14);
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
        Date date = format.parse(ymd);
        Calendar idCardDate = Calendar.getInstance();
        idCardDate.setTime(date);
        // 将身份证时间加上loadMaxAge（60）年
        Calendar end = Calendar.getInstance();
        end.setTime(date);
        end.add(Calendar.YEAR, loadMaxAge);
        long time1 = idCardDate.getTimeInMillis();
        long time2 = end.getTimeInMillis();
        long between_days = (time2 - time1) / (1000 * 3600 * 24);
        // 计算身份证时间+60年以后与身份证时间相差的天数
        int day = Integer.parseInt(String.valueOf(between_days));
        // 计算身份证与当前时间相差的天数
        int idCardDirrDay = calcCurrTimeAndIdCardDirrDay(idCard);
        // 获取系统当前时间当前月的最大天数
        Calendar currDate = Calendar.getInstance();
        int maxDate = currDate.getActualMaximum(Calendar.DATE);
        // 计算身份证的时间离最大贷款年龄相差多少月
        int month = (day - idCardDirrDay) / maxDate;
        return month;

    }

    /**
     * 根据指定的日期及前后天数，取日期，整正数为后天数，负正数为前天数，即num为7时则指定日期七天后的日期，-7时为取指定日期的前七天日期
     *
     * @param date
     *            指定的日期
     * @param num
     *            正或负数
     * @return 指定num数后的日期
     */
    public static Date getDate(Date date, int num) {
        if (num == 0)
            return date;
        Calendar cal = Calendar.getInstance(java.util.Locale.CHINA);
        cal.setTime(date);
        cal.set(Calendar.DATE, cal.get(Calendar.DATE) + num);
        return cal.getTime();
    }

    public static String dateToString(Date date, String pattern) {
        if (StringUtils.isEmpty(pattern)) {
            pattern = "yyyy-MM-dd HH:mm:ss";
        }
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        return sdf.format(date);
    }

    /**
     * 获取n年之前或者之后的零点日期
     *
     * @param n
     * @param date
     * @return
     */
    public static Date getBeforeOrAfterYearZeroDate(int n, Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.YEAR, n);
        return getCurZeroDate(cal.getTime());
    }

    /**
     *
     * @Title: getYesterDateYYYYMMDD
     * @Description: 获取date - 1天的日期
     * @author: liushuangxi 2018年7月20日
     * @modify: liushuangxi 2018年7月20日
     * @param date
     * @return
     */
    public static String getYesterDateYYYYMMDD(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DATE, -1);
        return DateUtils.dateToStrWithFormat1(cal.getTime());
    }

    /**
     *
     * @Title: getYesterDateYYYYMMDDHHMMSS
     * @Description: 获取昨日最后时间
     * @author: liushuangxi 2018年9月2日
     * @modify: liushuangxi 2018年9月2日
     * @param date
     * @return
     * @throws ParseException
     */
    public static Date getYesterDateYYYYMMDDHHMMSS(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DATE, -1);
        String y = cal.get(Calendar.YEAR) + "";
        String m = (cal.get(Calendar.MONTH) + 1) + "";
        String d = cal.get(Calendar.DATE) + "";
        SimpleDateFormat format = new SimpleDateFormat(DateUtils.format2);
        String dateStr = y + "-" + m + "-" + d + " 23:59:59";
        try {
            return format.parse(dateStr);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return date;
    }

    /**
     *
     * @Title: getTransDateYYYYMMDDHHMMSS
     * @Description: 获取传递进来的数据最后时间
     * @author: liushuangxi 2018年9月2日
     * @modify: liushuangxi 2018年9月2日
     * @param date yyyyMMdd
     * @return
     */
    public static Date getTransDateYYYYMMDDHHMMSS(String date){
        String dateStr = date + "235959";
        SimpleDateFormat format = new SimpleDateFormat(DateUtils.format3);
        try {
            return format.parse(dateStr);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new Date();
    }

    /**
     *
     * @Title: isNow
     * @Description: 判断是否是今日
     * @author: liushuangxi 2018年8月23日
     * @modify: liushuangxi 2018年8月23日
     * @param date
     * @return
     */
    public static boolean isNow(Date date) {
        if (null == date) {
            return false;
        }
        SimpleDateFormat sdf = new SimpleDateFormat(DateUtils.format1);
        Date toDate = new Date();
        String date1Str = sdf.format(date);
        String date2Str = sdf.format(toDate);
        if (date1Str.equals(date2Str)) {
            return true;
        }
        return false;
    }

    /**
     * 通过DateLocaleConverter实现字符串转date
     *
     * @param dateStr
     * @param patten
     * @return java.util.Date
     * @author ggx
     * @date 2018/8/22
     */
    public static Date parseDate2(String dateStr, String patten) {
        DateLocaleConverter dcl = new DateLocaleConverter();
        return (Date) dcl.convert(dateStr, patten);
    }

    /**
     * 通过DateLocaleConverter比较大小 -1 date1小于date2 0 相等 1 date1大于date2
     *
     * @param date1
     * @param date2
     * @param patten
     * @return int
     * @author ggx
     * @date 2018/8/23
     */
    public static int compareDate2(String date1, String date2, String patten) {
        DateLocaleConverter dcl = new DateLocaleConverter();
        long d1 = ((Date) dcl.convert(date1, patten)).getTime();
        long d2 = ((Date) dcl.convert(date2, patten)).getTime();
        if (d1 < d2) {
            return -1;
        }
        if (d1 == d2) {
            return 0;
        }
        return 1;
    }

    /**
     * yyyyMMdd转换成yyyy/MM/dd
     *
     * @author 束庆乐
     * @param 20180829
     * @return
     */
    public static String formatDateToExcelDay(String date) {
        SimpleDateFormat sdfFormatUR = new SimpleDateFormat(format1);
        SimpleDateFormat sdfFormatPage = new SimpleDateFormat("yyyy年MM月dd日");
        String pageDate = null;
        try {
            pageDate = sdfFormatPage.format(sdfFormatUR.parse(date));
        } catch (ParseException e) {
            return null;
        }
        return pageDate;
    }

    /**
     * yyyyMM转换成yyyy年MM月
     *
     * @author 束庆乐
     * @param 20180830
     * @return
     */
    public static String formatDateToExcelMonth(String date) {
        SimpleDateFormat sdfFormatUR = new SimpleDateFormat("yyyyMM");
        SimpleDateFormat sdfFormatPage = new SimpleDateFormat("yyyy年MM月");
        String pageDate = null;
        try {
            pageDate = sdfFormatPage.format(sdfFormatUR.parse(date));
        } catch (ParseException e) {
            return null;
        }
        return pageDate;
    }

    /**
     * yyyyyyyy年
     *
     * @author 束庆乐
     * @param 20180830
     * @return
     */
    public static String formatDateToExcelYear(String date) {
        SimpleDateFormat sdfFormatUR = new SimpleDateFormat("yyyy");
        SimpleDateFormat sdfFormatPage = new SimpleDateFormat("yyyy年");
        String pageDate = null;
        try {
            pageDate = sdfFormatPage.format(sdfFormatUR.parse(date));
        } catch (ParseException e) {
            return null;
        }
        return pageDate;
    }

    public static Date formatStrToDate(String dateStr, String format){
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        try {
            return sdf.parse(dateStr);
        } catch (ParseException e) {
            return null;
        }
    }

}
