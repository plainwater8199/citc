package com.citc.nce.utils;

import cn.hutool.core.annotation.MirroredAnnotationAttribute;
import cn.hutool.core.date.DateUnit;
import com.citc.nce.common.core.exception.BizException;
import com.citc.nce.robot.util.DateUtil;
import com.google.common.base.Strings;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

public class DateUtils {

    private DateUtils() {
        throw new IllegalStateException("Utility class");
    }

    private static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
    private static final String HOUR = "hour";
    private static final String DAY = "day";
    private static final String WEEK = "week";
    private static final String START = "start";
    private static final String END = "end";
    /**
     * String 转 Date
     * @param dateString 时间字符串
     * @return 时间date
     */
    public static Date obtainDate(String dateString) {
        if(!Strings.isNullOrEmpty(dateString)){
            return dateString.contains(":") ? DateUtils.obtainDate(dateString,DATE_FORMAT) : DateUtils.obtainDate(dateString,"yyyy-MM-dd");
        }
        return null;
    }
    /**
     * String 转 Date
     * @param dateString 时间字符串
     * @return 时间date
     */
    public static Date obtainDate(String dateString,String format) {
        if(!Strings.isNullOrEmpty(dateString)){
            DateFormat dateFormat =new SimpleDateFormat(format);
            try {
                return dateFormat.parse(dateString);
            } catch (ParseException e) {
                throw new BizException("日期格式错误,请提供："+format+" 格式的日期字符串");
            }
        }
        return null;
    }
    /**
     * date 转 String
     * @param date 时间
     * @return 时间字符串
     */
    public static String obtainDateStr(Date date) {
        return obtainDateStr(date,DATE_FORMAT);
    }
    /**
     * date 转 String
     * @param date 时间
     * @return 时间字符串
     */
    public static String obtainDateStr(Date date,String format) {
        if(date != null){
            DateFormat dateFormat =new SimpleDateFormat(format);
            return dateFormat.format(date);
        }
        return null;
    }

    public static String obtainHour(String dateStr, String format) {
        Date date = obtainDate(dateStr);
        DateFormat dateFormat =new SimpleDateFormat(format);
        return dateFormat.format(date);
    }

    /**
     * 获取时间的当前周
     * @param date 时间
     * @return 第几周
     */
    public static Integer obtainWeek(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.setFirstDayOfWeek(Calendar.MONDAY);
        int month = calendar.get(Calendar.MONTH);
        int week = calendar.get(Calendar.WEEK_OF_YEAR);
        if(week >2){
            return week;
        }else{
            if(month == 0){
                return week;
            }else{
                while (week < 2){
                    date = obtainAddDay(date,-1);
                    calendar.setTime(date);
                    week = calendar.get(Calendar.WEEK_OF_YEAR);
                }
                return week + 1;
            }
        }
    }

    /**
     * 获取时间的当前年
     * @param date 时间
     * @return 当前年
     */
    public static Integer obtainYear(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.YEAR);
    }

    /**
     * 获取某年莫一周的第一天
     * @param year 那一年
     * @param weekOfYear 这一年的第几周
     * @return 开始时间
     */
    public static Date obtainWeekStartDay(Integer year,Integer weekOfYear){
        Calendar calendar = Calendar.getInstance();
        calendar.setFirstDayOfWeek(Calendar.MONDAY);
        Date date;
        if(weekOfYear > 1){
            calendar.setWeekDate(year, weekOfYear, 2);
            date = calendar.getTime();
        }else{
            Calendar cal = Calendar.getInstance();
            cal.set(year,0,1);
            date = cal.getTime();
        }
        return obtainDate(obtainDateStr(date,"yyyy-MM-dd")+" 00:00:00");

    }

    /**
     * 获取某年莫一周的最后一天
     * @param year 那一年
     * @param weekOfYear 这一年的第几周
     * @return 开始时间
     */
    public static Date obtainWeekEndDay(Integer year,Integer weekOfYear){
        Calendar calendar = Calendar.getInstance();
        calendar.setFirstDayOfWeek(Calendar.MONDAY);
        Date date;
        if(weekOfYear * 7 >= 365){
            Calendar cal = Calendar.getInstance();
            cal.set(year,11,31);
            date = cal.getTime();
        }else{
            calendar.setWeekDate(year, weekOfYear, 1);
            date = calendar.getTime();
        }
        return obtainDate(obtainDateStr(date,"yyyy-MM-dd")+" 23:59:59");
    }

    public static int obtainYearLastWeek(int tempYear) {
        String dateStr = tempYear+"-12-31";
        return obtainWeek(obtainDate(dateStr,"yyyy-MM-dd"));
    }

    public static Date obtainAddDay(String dateStr, int num) {
        Date date;
        if(dateStr.contains(":")){
            date = obtainDate(dateStr);
        }else{
            date = obtainDate(dateStr,"yyyy-MM-dd");
        }
        if(date != null){
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            cal.add(Calendar.DATE, num);
            return cal.getTime();
        }
        return null;
    }

    public static Date obtainAddDay(Date date, int num) {
        if(date != null){
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            cal.add(Calendar.DATE, num);
            return cal.getTime();
        }
        return null;
    }

    public static void main(String[] args) {
//        System.out.println(obtainYearLastWeek(2022));
//        System.out.println(obtainWeek(obtainDate("2022-1-3 12:00:00")));
        System.out.println(getDayOfWeek(obtainDate("2024-4-14 12:00:00")));
    }

    public static Date obtainTime(String dayOrWeek, String startOrEnd, Date date) {
        if(DAY.equals(dayOrWeek)){
            return obtainDayTime(startOrEnd,date);
        }else{
            return obtainWeekTime(startOrEnd,date);
        }
    }

    private static Date obtainWeekTime(String startOrEnd, Date date) {
        Integer year = DateUtils.obtainYear(date);
        Integer week = DateUtils.obtainWeek(date);
        if(startOrEnd.equals(START)){
            return obtainWeekStartDay(year,week);
        }else{
            return obtainWeekEndDay(year,week);
        }
    }

    public static Date obtainDayTime(String startOrEnd,Date date) {
        if(startOrEnd.equals(START)){
            return obtainDate(obtainDateStr(date,"yyyy-MM-dd")+" 00:00:00");
        }else{
            return obtainDate(obtainDateStr(date,"yyyy-MM-dd")+" 23:59:59");
        }
    }

    public static List<String> obtainWeekList(Date startTime, Date endTime) {
        List<String> weeks = new ArrayList<>();
        int yearS = DateUtils.obtainYear(startTime);
        Integer weekS = DateUtils.obtainWeek(startTime);
        int yearE ;
        int weekE ;
        if(endTime != null){
            yearE = DateUtils.obtainYear(endTime);
            weekE = DateUtils.obtainWeek(endTime);
        }else{
            yearE = DateUtils.obtainYear(new Date());
            weekE = DateUtils.obtainWeek(new Date());
        }

        for(int tempYear = yearS;tempYear <= yearE; tempYear++){
            int maxWeekForYear = DateUtils.obtainYearLastWeek(tempYear);
            if(tempYear == yearS){
                if(tempYear == yearE){
                    obtainWeekList(weeks,weekS,weekE,tempYear);
                }else{
                    obtainWeekList(weeks,weekS,maxWeekForYear,tempYear);
                }
            } else if (yearS < tempYear && tempYear < yearE) {
                obtainWeekList(weeks, 1, maxWeekForYear, tempYear);
            }else{
                obtainWeekList(weeks,1,weekE,tempYear);
            }
        }
        return weeks;
    }

    private static void obtainWeekList(List<String> weeks, Integer weekS, int maxWeekForYear, int tempYear) {
        for(int tempWeek = weekS; tempWeek <= maxWeekForYear;tempWeek++){
            weeks.add(tempYear+"第"+tempWeek+"周");
        }
    }

    public static List<String> obtainDayList(Date startDate, Date endDate) {
        List<String> days = new ArrayList<>();
        for(Date tempDate = startDate; Objects.requireNonNull(tempDate).before(endDate) || Objects.requireNonNull(tempDate).equals(endDate); tempDate = DateUtils.obtainAddDay(DateUtils.obtainDateStr(tempDate),1)){
            days.add(DateUtils.obtainDateStr(tempDate,"yyyy-MM-dd"));
        }
        return days;
    }

    public static List<String> obtainHourList(Date startTime) {
        String startTimeStr = obtainDateStr(startTime,"yyyy-MM-dd");
        List<String> hours = new ArrayList<>();
        String time;
        for(int i = 0;i < 24; i++){
            if(i<10){
                time = startTimeStr+" 0"+i+":00:00";
            }else{
                time = startTimeStr+ " "+i+":00:00";
            }
            hours.add(time);
        }
        return hours;
    }

    public static List<String> obtainHourMinList(Date startTime) {
        String startTimeStr = obtainDateStr(startTime,"yyyy-MM-dd");
        List<String> hours = new ArrayList<>();
        String time;
        for(int i = 0;i < 24; i++){
            if(i<10){
                time = startTimeStr+" 0"+i;
            }else{
                time = startTimeStr+ " "+i;
            }
            hours.add(time);
        }
        return hours;
    }

    public static String obtainTimeType(Date startTime, Date endTime) {
        int betweenDays = DateUtil.getBetweenDays(obtainDateStr(startTime), obtainDateStr(endTime));
        if(betweenDays == 1){//获取当天小时
            return HOUR;
//        } else if (betweenDays <= 30) {//获取当天
//            return DAY;
        }else{//获取当周
            return DAY;
        }
    }

    /**
     * 获取时间段
     * @param createTime 数据生成时间
     * @return 该数据所属时间段
     */
    public static Date obtainTimesHoursKey(Date createTime) {
        String dateStr =DateUtils.obtainDateStr(createTime);
        if(!Strings.isNullOrEmpty(dateStr)){
            String[] dateStrArray = dateStr.split(":");
            String dateHourStr = dateStrArray[0]+":00:00";
            return DateUtils.obtainDate(dateHourStr);
        }
        return null;
    }

    public static String obtainTimeMapKey(String timeType, Date createTime) {
        if (HOUR.equals(timeType)) {
            return DateUtils.obtainDateStr(DateUtils.obtainTimesHoursKey(createTime));
        } else if (DAY.equals(timeType)) {
            return DateUtils.obtainDateStr(createTime, "yyyy-MM-dd");
        } else {
            return DateUtils.obtainYear(createTime) + "第" + DateUtils.obtainWeek(createTime)+"周";
        }
    }

    public static Date obtainYesterdayDate() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.add(Calendar.DATE,-1);
        return cal.getTime();
    }

    public static Date obtainLatestTime(Date yesterdayDate) {
        return obtainDate(obtainDateStr(yesterdayDate,"yyyy-MM-dd")+" 23:59:59");
    }

    public static Date addDays(Date yesterdayDate, int days) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(yesterdayDate);
        cal.add(Calendar.DATE,days);
        return cal.getTime();
    }

    //获取下一周的一天 Calendar.MONDAY
    public static Date getNextWeekDay(int day) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.WEEK_OF_YEAR, 1); // 下一周
        calendar.set(Calendar.DAY_OF_WEEK, day); // 设置为下周一
        return calendar.getTime();
    }



    public static Date obtainEarliestTime(Date startDate) {
        return obtainDate(obtainDateStr(startDate,"yyyy-MM-dd")+" 00:00:00");
    }

    /**
     * 获取特殊时间字符
     * @param dateStr 时间
     * @param timeType 时间类型：HOUR、DAY、WEEK
     * @param showOrHover 展示类型：SHOW，HOVER
     * @return 指定时间格式
     */
    public static String obtainSpecificFormatTime(String dateStr, String timeType, String showOrHover) {
        if(HOUR.equals(timeType)){
            Date date = obtainDate(dateStr,"yyyy-MM-dd HH");
            return obtainDateStr(date,"HH:mm");
        } else if (DAY.equals(timeType)) {
            Date date = obtainDate(dateStr,"yyyy-MM-dd");
            return obtainDateStr(date,"yyyy-MM-dd");
        }else{
            return dateStr;
        }
    }

    public static String getYesterday(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DATE,-1);
        return obtainDateStr(cal.getTime(),"yyyy-MM-dd");
    }

    public static List<String> obtainAllHourList() {
        List<String> hours = new ArrayList<>();
        String time;
        for(int i = 0;i < 24; i++){
            if(i<10){
                time = "0"+i+":00";
            }else{
                time = i+":00";
            }
            hours.add(time);
        }
        return hours;
    }

    /**
     * 获取当前日期是一周中的第几天，注意Java中周日是一周的第一天，值为1，周一为2，依此类推
     * @param date 当前时间
     * @return int
     */
    public static int getDayOfWeek(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.DAY_OF_WEEK);
    }

    /**
     * 判断两个时间点的差值是否在指定范围内
     * @param date1 时间点2
     * @param date2 时间点2
     * @param i 差值
     * @return 两个时间点是否在指定范围内
     */
    public static boolean comparison(Date date1, Date date2, Long i) {
        long ms = cn.hutool.core.date.DateUtil.between(date1,date2, DateUnit.MS);
        return i > Math.abs(ms);
    }

    public static Date localDateTime2Date(LocalDateTime localDateTime) {
        // 将 LocalDateTime 转换为 Instant 对象
        Instant instant = localDateTime.atZone(ZoneId.systemDefault()).toInstant();

        // 从 Instant 对象创建 Date 对象
        return Date.from(instant);
    }
}
