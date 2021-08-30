package com.mei.hui.util;

import org.apache.commons.lang3.time.DateFormatUtils;

import java.lang.management.ManagementFactory;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

/**
 * 时间工具类
 * 
 * @author ruoyi
 */
public class DateUtils extends org.apache.commons.lang3.time.DateUtils {
    public static String YYYY = "yyyy";

    public static String YYYY_MM = "yyyy-MM";

    public static String YYYY_MM_DD = "yyyy-MM-dd";

    public static String YYYYMMDDHHMMSS = "yyyyMMddHHmmss";

    public static String YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm:ss";
    
    private static String[] parsePatterns = {
            "yyyy-MM-dd", "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd HH:mm", "yyyy-MM", 
            "yyyy/MM/dd", "yyyy/MM/dd HH:mm:ss", "yyyy/MM/dd HH:mm", "yyyy/MM",
            "yyyy.MM.dd", "yyyy.MM.dd HH:mm:ss", "yyyy.MM.dd HH:mm", "yyyy.MM"};

    /**
     * 获取当前Date型日期
     * 
     * @return Date() 当前日期
     */
    public static Date getNowDate()
    {
        return new Date();
    }

    /**
     * 获取当前日期, 默认格式为yyyy-MM-dd
     * 
     * @return String
     */
    public static String getDate()
    {
        return dateTimeNow(YYYY_MM_DD);
    }

    public static final String getTime()
    {
        return dateTimeNow(YYYY_MM_DD_HH_MM_SS);
    }

    public static final String dateTimeNow()
    {
        return dateTimeNow(YYYYMMDDHHMMSS);
    }

    public static final String dateTimeNow(final String format)
    {
        return parseDateToStr(format, new Date());
    }

    public static final String dateTime(final Date date)
    {
        return parseDateToStr(YYYY_MM_DD, date);
    }

    public static final String parseDateToStr(final String format, final Date date)
    {
        return new SimpleDateFormat(format).format(date);
    }

    public static final Date dateTime(final String format, final String ts)
    {
        try
        {
            return new SimpleDateFormat(format).parse(ts);
        }
        catch (ParseException e)
        {
            throw new RuntimeException(e);
        }
    }

    /**
     * 日期路径 即年/月/日 如2018/08/08
     */
    public static final String datePath()
    {
        Date now = new Date();
        return DateFormatUtils.format(now, "yyyy/MM/dd");
    }

    /**
     * 日期路径 即年/月/日 如20180808
     */
    public static final String dateTime()
    {
        Date now = new Date();
        return DateFormatUtils.format(now, "yyyyMMdd");
    }

    /**
     * 日期型字符串转化为日期 格式
     */
    public static Date parseDate(Object str)
    {
        if (str == null)
        {
            return null;
        }
        try
        {
            return parseDate(str.toString(), parsePatterns);
        }
        catch (Exception e)
        {
            return null;
        }
    }
    
    /**
     * 获取服务器启动时间
     */
    public static Date getServerStartDate()
    {
        long time = ManagementFactory.getRuntimeMXBean().getStartTime();
        return new Date(time);
    }

    /**
     * 获取昨天的日期
     * @return
     */
    public static String getYesterDayDateYmd()
    {
        Calendar cal=Calendar.getInstance();
        cal.add(Calendar.DATE,-1);
        Date d=cal.getTime();
        return parseDateToStr(YYYY_MM_DD, d);
    }

    /**
    * 获取当天的开始时间
    *
    * @description
    * @author shangbin
    * @date 2021/6/9 11:22
    * @param []
    * @return java.util.Date
    * @version v1.0.0
    */
    public static Date getBeginOfDayDate() {
        return getDayStartTime(new Date());
    }

    /**
    * 获取当天的结束时间
    *
    * @description
    * @author shangbin
    * @date 2021/6/16 15:51
    * @param []
    * @return java.util.Date
    * @version v1.0.0
    */
    public static Date getEndOfDayDate() {
        return getDayEndTime(new Date());
    }

    /**
    * 获取某个日期的开始时间
    *
    * @description
    * @author shangbin
    * @date 2021/6/9 11:21
    * @param [d]
    * @return java.sql.Timestamp
    * @version v1.0.0
    */
    public static Timestamp getDayStartTime(Date d) {
        Calendar calendar = Calendar.getInstance();
        if (null != d) {
            calendar.setTime(d);
        }
        calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),    calendar.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return new Timestamp(calendar.getTimeInMillis());
    }

    /**
    * 获取某个日期的结束时间
    *
    * @description
    * @author shangbin
    * @date 2021/6/16 15:52
    * @param [d]
    * @return java.sql.Timestamp
    * @version v1.0.0
    */
    public static Timestamp getDayEndTime(Date d) {
        Calendar calendar = Calendar.getInstance();
        if (null != d) {
            calendar.setTime(d);
        }
        calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),    calendar.get(Calendar.DAY_OF_MONTH), 23, 59, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        return new Timestamp(calendar.getTimeInMillis());
    }

    /**
     * 计算两个时间差
     */
    public static String getDatePoor(Date endDate, Date nowDate)
    {
        long nd = 1000 * 24 * 60 * 60;
        long nh = 1000 * 60 * 60;
        long nm = 1000 * 60;
        // long ns = 1000;
        // 获得两个时间的毫秒时间差异
        long diff = endDate.getTime() - nowDate.getTime();
        // 计算差多少天
        long day = diff / nd;
        // 计算差多少小时
        long hour = diff % nd / nh;
        // 计算差多少分钟
        long min = diff % nd % nh / nm;
        // 计算差多少秒//输出结果
        // long sec = diff % nd % nh % nm / ns;
        return day + "天" + hour + "小时" + min + "分钟";
    }

    /**
    * 获取昨天的开始时间
    *
    * @description
    * @author shangbin
    * @date 2021/6/16 15:50
    * @param []
    * @return java.util.Date
    * @version v1.0.0
    */
    public static Date getBeginYesterdayDate() {
        Calendar cal = new GregorianCalendar();
        cal.setTime(getBeginOfDayDate());
        cal.add(Calendar.DAY_OF_MONTH, -1);
        return cal.getTime();
    }

    /**
    * 获取昨天的结束时间
    *
    * @description
    * @author shangbin
    * @date 2021/6/16 15:50
    * @param []
    * @return java.util.Date
    * @version v1.0.0
    */
    public static Date getEndYesterdayDate() {
        Calendar cal = new GregorianCalendar();
        cal.setTime(getEndOfDayDate());
        cal.add(Calendar.DAY_OF_MONTH, -1);
        return cal.getTime();
    }

    /**
    * 获取当前年份
    *
    * @description
    * @author shangbin
    * @date 2021/8/4 19:33
    * @param []
    * @return java.lang.Integer
    * @version v1.4.1
    */
    public static Integer getNowYear() {
        Date date = new Date();
        GregorianCalendar gc = (GregorianCalendar) Calendar.getInstance();
        gc.setTime(date);
        return Integer.valueOf(gc.get(1));
    }

    /**
    * 获取当前月份
    *
    * @description
    * @author shangbin
    * @date 2021/8/4 19:33
    * @param []
    * @return int
    * @version v1.4.1
    */
    public static int getNowMonth() {
        Date date = new Date();
        GregorianCalendar gc = (GregorianCalendar) Calendar.getInstance();
        gc.setTime(date);
        return gc.get(2) + 1;
    }

    /**
    * 获取本月的结束时间
    *
    * @description
    * @author shangbin
    * @date 2021/8/4 19:32
    * @param []
    * @return java.util.Date
    * @version v1.4.1
    */
    public static Date getEndDayOfMonth() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(getNowYear(), getNowMonth() - 1, 1);
        int day = calendar.getActualMaximum(5);
        calendar.set(getNowYear(), getNowMonth() - 1, day);
        return getDayEndTime(calendar.getTime());
    }

    /**
     * 根据入参年、月获取指定月的结束时间
     * @return
     */
    public static Date getAssignEndDayOfMonth(Integer year,Integer month) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month - 1, 1);
        int day = calendar.getActualMaximum(5);
        calendar.set(year, month - 1, day);
        return getDayEndTime(calendar.getTime());
    }

    public static void main(String[] args) {
//        System.out.println(getYesterDayDateYmd());

        //获取系统当前时间Date类型，需要将字符串类型转成时间
        /*SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        //设置为东八区
        sdf.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));
        Date date = new Date();
        String dateStr = sdf.format(date);
        System.out.println(dateStr);
        System.out.println(new Date());
        System.out.println(LocalDateTime.now());*/

//        System.out.println(getBeginYesterdayDate());
//        System.out.println(getBeginOfDayDate());
//        System.out.println(getYesterDayDateYmd());
//        System.out.println(getEndDayOfMonth());
//        System.out.println(getAssignEndDayOfMonth(2021,7));
//        String date = "2021-07-31 23:59:59.999";
//        System.out.println(getAssignEndDayOfMonth(Integer.valueOf(date.substring(0,4)),Integer.valueOf(date.substring(5,7))));
//        String yesterDayDateYmd = getYesterDayDateYmd();
//        System.out.println(yesterDayDateYmd.substring(0,4)+ "--------" +yesterDayDateYmd.substring(5,7)+ "--------" +yesterDayDateYmd.substring(8,10));
//        System.out.println(yesterDayDateYmd.substring(0,10));

//        System.out.println(getEndYesterdayDate());
//        System.out.println(getYesterDayDateYmd()+" 23:59:59");

//        System.out.println(getBeginOfDayDate());// 2021-08-20 00:00:00.0
//        System.out.println(getEndOfDayDate());// 2021-08-20 23:59:59.999
//        System.out.println(getDate());//2021-08-20
//        System.out.println(LocalDateTime.now());//2021-08-20T14:53:57.306

//        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
//        LocalDate localDateTime = LocalDate.parse(DateUtils.getDate(), dateTimeFormatter);
//        System.out.println(localDateTime);

//        System.out.println((new Date()).getTime());// 1629797079401
//        System.out.println(new Date());// Tue Aug 24 17:24:04 CST 2021

        //获取秒数
//        Long second = LocalDateTime.now().toEpochSecond(ZoneOffset.of("+8"));
//        System.out.println(second);//1629797079
        System.out.println(getEndYesterdayDate().getTime()/1000);
        //获取毫秒数
//        Long milliSecond = LocalDateTime.now().toInstant(ZoneOffset.of("+8")).toEpochMilli();
//        System.out.println(milliSecond);// 1629797079500

        // 时间戳转成datetime
        String beginDate = "1630252650";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String sd = sdf.format(new Date(Long.parseLong(beginDate) * 1000)); // 时间戳转换日期
        System.out.println(sd);

    }

}
