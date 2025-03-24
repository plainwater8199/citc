package com.citc.nce.robot.util;

import com.citc.nce.utils.DateUtils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Test {
    private static final String WEEK = "week";
    public static void main(String[] args) {
        if(WEEK.equals("week")){
            System.out.println("相等");
        }else{
            System.out.println("不相等");
        }

//        String dayStr = "2023-12-29 12:00:00";
//
//
//        Integer year = DateUtils.obtainYear(DateUtils.obtainDate(dayStr));
//        System.out.println("当前年："+year);
//        Integer week = DateUtils.obtainWeek(DateUtils.obtainDate(dayStr));
//        System.out.println("当前周："+week);
//
//        String startDate = DateUtils.obtainDateStr(DateUtils.obtainWeekStartDay(year,week));
//        System.out.println("开始时间："+startDate);
//
//        String endDate = DateUtils.obtainDateStr(DateUtils.obtainWeekEndDay(year,week));
//        System.out.println("结束时间："+endDate);

//
//        Date day = DateUtils.obtainDate(dayStr);
//        Calendar calendar = Calendar.getInstance();
//        calendar.setTime(day);
//        calendar.setFirstDayOfWeek(Calendar.MONDAY);
//        int weekYear = calendar.get(Calendar.YEAR);//获得当前的年
//        System.out.println("当前年："+weekYear);
//        int weekOfYear = calendar.get(Calendar.WEEK_OF_YEAR);
//        System.out.println("第几周："+weekOfYear);
//
//
//
//        calendar.setWeekDate(weekYear, weekOfYear, 2);//获得指定年的第几周的开始日期
//        long starttime = calendar.getTime().getTime();//创建日期的时间该周的第一天，
//        calendar.setWeekDate(weekYear, weekOfYear, 1);//获得指定年的第几周的结束日期
//        long endtime = calendar.getTime().getTime();
//        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
//        String dateStart = simpleDateFormat.format(starttime);//将时间戳格式化为指定格式
//        String dateEnd = simpleDateFormat.format(endtime);
////        String dateStart = simpleDateFormat.format(getFirstDayOfWeek(weekYear,3));//将时间戳格式化为指定格式
////        String dateEnd = simpleDateFormat.format(getLastDayOfWeek(weekYear,3));
//        System.out.println("开始时间："+dateStart);
//        System.out.println("结束时间："+dateEnd);
    }
    //region 获取指定周的第一天 及 指定周的 最后一天
    /**
     * 获取指定周的第一天
     *
     * @param year
     * @param week
     * @return
     */
    public static Date getFirstDayOfWeek(int year, int week) {
        if(week > 1) {
            Calendar cal = Calendar.getInstance();
            // 设置年份
            cal.set(Calendar.YEAR, year);
            // 设置周
            cal.set(Calendar.WEEK_OF_YEAR, week);
            // 设置该周第一天为星期一
            cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
            cal.set(Calendar.HOUR_OF_DAY, 0);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
            return cal.getTime();
        }
        else {
            Calendar cal = Calendar.getInstance();
            cal.set(year,0,1);
            return cal.getTime();
        }
    }

    /**
     * 获取指定周的最后一天
     *
     * @param year
     * @param week
     * @return
     */
    public static Date getLastDayOfWeek(int year, int week) {
        if(week * 7 >= 365){  // 说明是年的最后一天
            Calendar cal = Calendar.getInstance();
            cal.set(year,11,31);
            return cal.getTime();
        }else{
            Calendar cal = Calendar.getInstance();
            // 设置年份
            cal.set(Calendar.YEAR, year);
            // 设置周
            cal.set(Calendar.WEEK_OF_YEAR, week);
            // 设置该周第一天为星期一
            cal.setFirstDayOfWeek(Calendar.MONDAY);
            // 设置最后一天是星期日
            cal.set(Calendar.DAY_OF_WEEK, cal.getFirstDayOfWeek() + 6); // Sunday
            cal.set(Calendar.HOUR_OF_DAY, 23);
            cal.set(Calendar.MINUTE, 59);
            cal.set(Calendar.SECOND, 59);

            return cal.getTime();
        }
    }

}
