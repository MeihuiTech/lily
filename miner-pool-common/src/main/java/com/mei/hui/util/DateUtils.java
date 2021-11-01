package com.mei.hui.util;

import com.mei.hui.util.html.DateFormatEnum;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.http.util.Args;

import java.lang.management.ManagementFactory;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
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

    public static String YYYY_MM_DD = "yyyy-MM-dd";

    public static String YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm:ss";
    
    private static String[] parsePatterns = {
            "yyyy-MM-dd", "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd HH:mm", "yyyy-MM", 
            "yyyy/MM/dd", "yyyy/MM/dd HH:mm:ss", "yyyy/MM/dd HH:mm", "yyyy/MM",
            "yyyy.MM.dd", "yyyy.MM.dd HH:mm:ss", "yyyy.MM.dd HH:mm", "yyyy.MM"};


    /**
     * 获取秒级时间戳
     * @param localDateTime
     * @return
     */
    public static long localDateTimeToSecond(LocalDateTime localDateTime){
        ZoneId zone = ZoneId.systemDefault();
        Instant instant = localDateTime.atZone(zone).toInstant();
        return instant.getEpochSecond();
    }
    /**
     * String转LocalDateTime
     * @param string
     * @param format
     * @return
     */
    public static LocalDateTime stringToLocalDateTime(String string, DateFormatEnum format){
        if(string == null){
            return null;
        }
        DateTimeFormatter timeDtf = DateTimeFormatter.ofPattern(format.getFormat());
        LocalDateTime localDateTime = LocalDateTime.parse(string, timeDtf);
        return localDateTime;
    }

    /**
     * LocalDateTime 转 String
     * @param localDateTime
     * @param format
     * @return
     */
    public static String localDateTimeToString(LocalDateTime localDateTime, DateFormatEnum format){
        if(localDateTime == null){
            return null;
        }
        DateTimeFormatter timeDtf = DateTimeFormatter.ofPattern(format.getFormat());
        return localDateTime.format(timeDtf);
    }

    /**
     * String转LocalDate
     * @param string
     * @param format
     * @return
     */
    public static LocalDate stringToLocalDate(String string,DateFormatEnum format){
        if(string == null){
            return null;
        }
        DateTimeFormatter timeDtf = DateTimeFormatter.ofPattern(format.getFormat());
        LocalDate localDate = LocalDate.parse(string, timeDtf);
        return localDate;
    }

    /**
     * LocalDateTime 转 String
     * @param localDate
     * @param format
     * @return
     */
    public static String localDateToString(LocalDate localDate, DateFormatEnum format){
        if(localDate == null){
            return null;
        }
        DateTimeFormatter timeDtf = DateTimeFormatter.ofPattern(format.getFormat());
        return localDate.format(timeDtf);
    }

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

    public static final String dateTimeNow(final String format)
    {
        return parseDateToStr(format, new Date());
    }

    public static final String parseDateToStr(final String format, final Date date)
    {
        return new SimpleDateFormat(format).format(date);
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
    * 获取昨天的开始时间，返回时间类型
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
     * 获取昨天的开始时间，返回字符串类型，格式为：yyyy-MM-dd HH:mm:ss
     * @return
     */
    public static String getBeginYesterdayDateStr() {
        Calendar cal = new GregorianCalendar();
        cal.setTime(getBeginOfDayDate());
        cal.add(Calendar.DAY_OF_MONTH, -1);
        return parseDateToStr(YYYY_MM_DD_HH_MM_SS, cal.getTime());
    }

    /**
    * 获取昨天的结束时间，返回时间类型
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
     * 获取昨天的结束时间，返回字符串类型，格式为：yyyy-MM-dd HH:mm:ss
     * @return
     */
    public static String getEndYesterdayDateStr() {
        Calendar cal = new GregorianCalendar();
        cal.setTime(getEndOfDayDate());
        cal.add(Calendar.DAY_OF_MONTH, -1);
        return parseDateToStr(YYYY_MM_DD_HH_MM_SS, cal.getTime());
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


    //------------------------------------       LocalDateTime              ---------------------------------------

    /**
     * LocalDateTime获取当前时间的秒级时间戳
     * @return
     */
    public static Long lDTNowTimestamp(){
        //获取秒数
        Long second = LocalDateTime.now().toEpochSecond(ZoneOffset.of("+8"));
        return second;
    }

    /**
     * LocalDateTime获取几个小时前或者几个小时后的秒级时间戳
     * @return
     */
    public static Long lDTBeforeOrAfterHourTimestamp(Integer hour){
        //获取秒数
        Long second = LocalDateTime.now().plusHours(hour).toEpochSecond(ZoneOffset.of("+8"));
        return second;
    }

    /**
     * LocalDateTime获取当前时间YYYY_MM_DD_HH_MM_SS格式的字符串
     * @return
     */
    public static String lDTLocalDateTimeNow(){
        LocalDateTime localDateTime = LocalDateTime.now();
        String  format = lDTToStringFormat( localDateTime, YYYY_MM_DD_HH_MM_SS);
        return format;
    }

    /**
     * 格式化入参localDateTime类型日期，返回字符串格式为：yyyy-MM-dd HH:mm:ss
     * @param localDateTime
     * @return
     */
    public static String lDTLocalDateTimeFormatYMDHMS(LocalDateTime localDateTime){
        String  format = lDTToStringFormat( localDateTime, YYYY_MM_DD_HH_MM_SS);
        return format;
    }

    /**
     * 格式化入参localDateTime类型日期，返回字符串格式为：yyyy-MM-dd
     * @param localDateTime
     * @return
     */
    public static String lDTLocalDateTimeFormatYMD(LocalDateTime localDateTime){
        String  format = lDTToStringFormat( localDateTime, YYYY_MM_DD);
        return format;
    }

    /**
     * LocalDateTime格式化localDateTime时间为YYYY_MM_DD_HH_MM_SS，返回字符串类型
     * @param localDateTime  时间
     * @return
     */
    public static String lDTToStringFormatYMDHMS(LocalDateTime localDateTime){
        // 指定模式
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(YYYY_MM_DD_HH_MM_SS);
        // 将 LocalDateTime 格式化为字符串
        String format = localDateTime.format(dateTimeFormatter);
        // 2020/02/03 14/38/54
        return format;
    }

    /**
     * LocalDateTime根据入参要格式化成的样式 格式化localDateTime时间，返回字符串类型
     * @param localDateTime  时间
     * @param formatStr   要格式化成的样式
     * @return
     */
    public static String lDTToStringFormat(LocalDateTime localDateTime,String formatStr){
        // 指定模式
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(formatStr);
        // 将 LocalDateTime 格式化为字符串
        String format = localDateTime.format(dateTimeFormatter);
        // 2020/02/03 14/38/54
        return format;
    }

    /**
     * LocalDateTime：把YMDHMS格式的字符串转成LocalDateTime类型
     * @param str
     * @return
     */
    public static LocalDateTime lDTStringToLocalDateTimeYMDHMS(String str){
        DateTimeFormatter df = DateTimeFormatter.ofPattern(YYYY_MM_DD_HH_MM_SS);
        LocalDateTime ldt = LocalDateTime.parse(str,df);
        return ldt;
    }

    /**
     * LocalDateTime：把YMD格式的字符串转成LocalDate类型
     * @param str
     * @return
     */
    public static LocalDate lDTStringToLocalDateYMD(String str){
        DateTimeFormatter df = DateTimeFormatter.ofPattern(YYYY_MM_DD);
        LocalDate ldt = LocalDate.parse(str,df);
        return ldt;
    }

    /**
     * LocalDateTime获取今天上一个时间点,如现在为：2021-08-30 15:30:00,上一个整点时间点为：2021-08-30 15:00:00
     * @return
     */
    public static LocalDateTime lDTBeforeLocalDateTimeHourDate(){
        LocalDateTime localDateTime =LocalDateTime.now().withMinute(0).withSecond(0).withNano(0);
        return localDateTime;
    }

    /**
     * LocalDateTime获取今天上一个时间点,如现在为：2021-08-30 15:30:00,上一个整点时间点为：2021-08-30 15:00:00
     * @return
     */
    public static String lDTBeforeLocalDateTimeHour(){
        LocalDateTime localDateTime =LocalDateTime.now().withMinute(0).withSecond(0).withNano(0);
        String format = lDTToStringFormat(localDateTime, YYYY_MM_DD_HH_MM_SS);
        return format;
    }

    /**
     * LocalDateTime获取昨天的上一个时间点,如现在为2021-08-30 15:30:00,上一个整点时间点为，2021-08-29 15:00:00
     * @return
     */
    public static LocalDateTime lDTYesterdayBeforeLocalDateTimeHourDate(){
        LocalDateTime localDateTime =LocalDateTime.now().withMinute(0).withSecond(0).withNano(0).plusDays(-1);
        return localDateTime;
    }

    /**
     * LocalDateTime获取昨天的上一个时间点,如现在为2021-08-30 15:30:00,上一个整点时间点为，2021-08-29 15:00:00
     * @return
     */
    public static String lDTYesterdayBeforeLocalDateTimeHour(){
        LocalDateTime localDateTime =LocalDateTime.now().withMinute(0).withSecond(0).withNano(0).plusDays(-1);
        String format = lDTToStringFormat(localDateTime, YYYY_MM_DD_HH_MM_SS);
        return format;
    }

    /**
     * LocalDateTime获取昨天的上上一个时间点,如现在为2021-08-30 15:30:00,上上一个整点时间点为，2021-08-29 14:00:00
     * @return
     */
    public static String lDTYesterdayBeforeBeforeLocalDateTimeHourDate(){
        LocalDateTime localDateTime =LocalDateTime.now().withMinute(0).withSecond(0).withNano(0).plusDays(-1).plusHours(-1);
        String format = lDTToStringFormat(localDateTime, YYYY_MM_DD_HH_MM_SS);
        return format;
    }

    /**
     * LocalDateTime获取今天的上上一个时间点,如现在为2021-08-30 15:30:00,上上一个整点时间点为，2021-08-30 14:00:00
     * @return
     */
    public static LocalDateTime lDTBeforeBeforeLocalDateTimeHourDate(){
        LocalDateTime localDateTime =LocalDateTime.now().withMinute(0).withSecond(0).withNano(0).plusHours(-1);
        return localDateTime;
    }

    /**
     * LocalDateTime获取今天的上上一个时间点,如现在为2021-08-30 15:30:00,上上一个整点时间点为，2021-08-30 14:00:00
     * @return
     */
    public static String lDTBeforeBeforeLocalDateTimeHour(){
        LocalDateTime localDateTime =LocalDateTime.now().withMinute(0).withSecond(0).withNano(0).plusHours(-1);
        String format = lDTToStringFormat(localDateTime, YYYY_MM_DD_HH_MM_SS);
        return format;
    }
    /**
     * LocalDateTime获取今天下一个时间点,如现在为2021-08-30 15:30:00,上一个整点时间点为，2021-08-30 16:00:00
     * @return
     */
    public static String lDTNextLocalDateTimeHour(){
        LocalDateTime localDateTime = LocalDateTime.now().withMinute(0).withSecond(0).withNano(0).plusHours(1);
        String format = lDTToStringFormat(localDateTime, YYYY_MM_DD_HH_MM_SS);
        return format;
    }

    /**
     * 秒级时间戳转成小时分钟秒，格式为：11:30:30
     * @param timestamp
     * @return
     */
    public static String timestampToHHmmss(Long timestamp){
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        String sd = sdf.format(new Date(timestamp * 1000)); // 时间戳转换日期
        return sd;
    }

    /**
     * 秒级时间戳转成月日，格式为：08.26
     * @param timestamp
     * @return
     */
    public static String timestampToMMDD(Long timestamp){
        SimpleDateFormat sdf = new SimpleDateFormat("MM.dd");
        String sd = sdf.format(new Date(timestamp * 1000)); // 时间戳转换日期
        return sd;
    }

    /**
     * 计算当前月有多少天
     *
     * @return
     */
    public static int getDays(int year, int month) {
        int days = 0;
        if (month != 2) {
            switch (month) {
                case 1:
                case 3:
                case 5:
                case 7:
                case 8:
                case 10:
                case 12:
                    days = 31;
                    break;
                case 4:
                case 6:
                case 9:
                case 11:
                    days = 30;
            }
        } else {
            // 闰年
            if (year % 4 == 0 && year % 100 != 0 || year % 400 == 0)
                days = 29;
            else
                days = 28;
        }
        return days;
    }


    public static void main(String[] args) {
        // 时间戳转成datetime
        String beginDate = "1634723010";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String sd = sdf.format(new Date(Long.parseLong(beginDate) * 1000)); // 时间戳转换日期
        System.out.println(sd);

        //获取秒数
//        Long second = LocalDateTime.now().toEpochSecond(ZoneOffset.of("+8"));
//        System.out.println(second);//1629797079

//        Long beforeSecond = lDTBeforeOrAfterHourTimestamp(-3);
//        System.out.println(beforeSecond);
    }

}
