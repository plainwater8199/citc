package com.citc.nce.auth.utils;


import org.springframework.util.Assert;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * 日期工具类
 */
public class DateUtil {

//	public DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");


    /**
     * String字符串往Calendar转换
     *
     * @param timeStr
     * @return
     */
    public static Calendar stringToCalendar(String timeStr) {
        if (timeStr != null) {
            SimpleDateFormat formatter = null;
            switch (timeStr.trim().length()) {
                case 19:
                    formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    break;
                case 13:
                    formatter = new SimpleDateFormat("yyyy-MM-dd HH");
                    break;
                case 16:
                    formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                    break;
                case 10:
                    formatter = new SimpleDateFormat("yyyy-MM-dd");
                    break;
                case 7:
                    formatter = new SimpleDateFormat("yyyy-MM");
                    break;
                case 4:
                    formatter = new SimpleDateFormat("yyyy");
                    break;
                default:
                    break;
            }
            Calendar calendar = Calendar.getInstance();
            try {
                if (formatter != null) {
                    Date date = formatter.parse(timeStr.trim());
                    calendar.setTime(date);
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
            return calendar;
        }
        return null;
    }

    /**
     * String字符串往Calendar转换
     *
     * @param time
     * @param format
     * @return
     */
    public static Calendar stringToCalendar(String time, String format) {
        if (time != null && !time.equals("") && format != null && !time.equals("null")) {
            SimpleDateFormat formatter = new SimpleDateFormat(format);
            Calendar calendar = Calendar.getInstance();
            try {
                Date bdate = formatter.parse(time.trim());
                calendar.setTime(bdate);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            return calendar;
        } else {
            return null;
        }

    }

    /**
     * String字符串往Date转换
     *
     * @param time
     * @param format
     * @return
     */
    public static Date stringToDate(String time, String format) {
        if (time == null || format == null) return null;
        SimpleDateFormat formatter = new SimpleDateFormat(format);
        try {
            Date bdate = formatter.parse(time);
            return bdate;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * String字符串往Timestamp转换
     *
     * @param time
     * @param format
     * @return
     */
    public static Timestamp stringToTimestamp(String time, String format) {
        if (time == null || format == null) return null;
        SimpleDateFormat formatter = new SimpleDateFormat(format);
        try {
            Date bdate = formatter.parse(time);
            Timestamp timestamp = new Timestamp(bdate.getTime());
            return timestamp;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Calendar往字符串String转换
     *
     * @param time
     * @param format
     * @return
     */
    public static String calendarToString(Calendar time, String format) {
        SimpleDateFormat formatter = new SimpleDateFormat(format);
        return formatter.format(time.getTime());
    }

    /**
     * Calendar往字符串String转换
     *
     * @param time
     * @return
     */
    public static String calendarToString(Calendar time) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return formatter.format(time.getTime());
    }

    /**
     * Calendar往字符串String转换
     *
     * @param time
     * @param format
     * @return
     */
    public static String dateToString(Date time, String format) {
        SimpleDateFormat formatter = new SimpleDateFormat(format);
        return formatter.format(time);
    }


    public static int getWeek(Calendar calendar) {
        int dayOfYear = calendar.get(Calendar.DAY_OF_YEAR);
        Calendar tempTime = (Calendar) calendar.clone();
        tempTime.set(tempTime.get(Calendar.YEAR), 0, 1, 0, 0, 0);
        int dayOfWeek = tempTime.get(Calendar.DAY_OF_WEEK);
        if (dayOfWeek >= 2 && dayOfWeek <= 5) {
            dayOfYear += dayOfWeek - 2;
        } else if (dayOfWeek == 1) {
            dayOfYear -= dayOfWeek;
        } else {
            dayOfYear -= 9 - dayOfWeek;
        }
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int tempRemainder = (dayOfYear - 1) % 7;
        if (month == 11
                && ((day == 29 && tempRemainder < 1)
                || (day == 30 && tempRemainder < 2)
                || (day == 31 && tempRemainder < 3))) {
            return 1;
        }
        //有些天落在上一个年份
        if (dayOfYear < 1) {
            tempTime.add(Calendar.DAY_OF_MONTH, -1);
            return getWeek(tempTime);
        }
        return (dayOfYear - 1) / 7 + 1;
    }

    /**
     * 时间戳往Calendar转换
     *
     * @param time
     * @return
     */
    public static Calendar longToCalendar(Long time) {
        if (time != null) {
            //SimpleDateFormat formatter = new SimpleDateFormat(format);
            Calendar calendar = Calendar.getInstance();
            try {
                Date date = new Date(time);
                //Date bdate = formatter.parse(time);
                calendar.setTime(date);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return calendar;
        } else {
            return null;
        }

    }

    /**
     * 传入两个时间范围，返回这两个时间范围内的所有日期，并保存在一个集合中
     *
     * @param beginTime
     * @param endTime
     * @return
     * @throws ParseException
     */
    public static List<String> findEveryDay(String beginTime, String endTime) {
        //1.创建一个放所有日期的集合
        List<String> dates = new ArrayList();
        //2.创建时间解析对象规定解析格式
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        //3.将传入的时间解析成Date类型,相当于格式化
        Date dBegin = null;
        Date dEnd = null;
        try {
            dBegin = sdf.parse(beginTime);
            dEnd = sdf.parse(endTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (dBegin != null && dEnd != null) {
            //4.将格式化后的第一天添加进集合
            dates.add(sdf.format(dBegin));
            //5.使用本地的时区和区域获取日历
            Calendar calBegin = Calendar.getInstance();
            //6.传入起始时间将此日历设置为起始日历
            calBegin.setTime(dBegin);
            //8.判断结束日期是否在起始日历的日期之后
            while (dEnd.after(calBegin.getTime())) {
                // 9.根据日历的规则:月份中的每一天，为起始日历加一天
                calBegin.add(Calendar.DAY_OF_MONTH, 1);
                //10.得到的每一天就添加进集合
                dates.add(sdf.format(calBegin.getTime()));
                //11.如果当前的起始日历超过结束日期后,就结束循环
            }
        }
        return dates;
    }

    /**
     * 传入两个时间范围，返回这两个时间范围内的所有日期，并保存在一个集合中
     *
     * @param beginTime
     * @param endTime
     * @return
     * @throws ParseException
     */
    public static List<String> findHours(String beginTime, String endTime) {
        //1.创建一个放所有日期的集合
        List<String> dates = new ArrayList();
        //2.创建时间解析对象规定解析格式
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        //3.将传入的时间解析成Date类型,相当于格式化
        Date dBegin = null;
        Date dEnd = null;
        try {
            dBegin = sdf.parse(beginTime);
            dEnd = sdf.parse(endTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (dBegin != null && dEnd != null) {
            //4.将格式化后的第一天添加进集合
            dates.add(sdf.format(dBegin));
            //5.使用本地的时区和区域获取日历
            Calendar calBegin = Calendar.getInstance();
            //6.传入起始时间将此日历设置为起始日历
            calBegin.setTime(dBegin);
            //8.判断结束日期是否在起始日历的日期之后
            while (dEnd.after(calBegin.getTime())) {
                // 9.根据日历的规则:月份中的每一天，为起始日历加一天
                calBegin.add(Calendar.HOUR_OF_DAY, 1);
                //10.得到的每一天就添加进集合
                dates.add(sdf.format(calBegin.getTime()));
                //11.如果当前的起始日历超过结束日期后,就结束循环
            }
        }
        return dates;
    }

    /**
     * 传入两个时间范围，返回这两个时间范围内的所有日期，并保存在一个集合中
     *
     * @param beginTime
     * @param endTime
     * @return
     * @throws ParseException
     */
    public static List<String> findWeeks(String beginTime, String endTime) {
        //1.创建一个放所有日期的集合
        List<String> dates = new ArrayList();
        //2.创建时间解析对象规定解析格式
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        //3.将传入的时间解析成Date类型,相当于格式化
        try {
            Date dBegin = sdf.parse(beginTime);
            Date dEnd = sdf.parse(endTime);


            //4.将格式化后的第一天添加进集合
            //5.使用本地的时区和区域获取日历
            Calendar calBegin = Calendar.getInstance();
            //6.传入起始时间将此日历设置为起始日历
            calBegin.setTime(dBegin);
            if (calBegin.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
                dates.add(sdf.format(dBegin));
            }
            //8.判断结束日期是否在起始日历的日期之后
            while (dEnd.after(calBegin.getTime())) {
                // 9.根据日历的规则:月份中的每一天，为起始日历加一天
                calBegin.add(Calendar.DAY_OF_MONTH, 1);
                if (calBegin.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
                    //10.得到的每一天就添加进集合
                    dates.add(sdf.format(calBegin.getTime()));
                    //11.如果当前的起始日历超过结束日期后,就结束循环
                }
            }
            return dates;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取两个时间之间的间隔天数
     *
     * @param startTimeStr 开始时间
     * @param endTimeStr   结束时间
     * @return 天数          例如2018-11-01 00:00:00至2018-11-30 23:59:59  返回为30
     */
    public static int getBetweenDays(String startTimeStr, String endTimeStr) {
        int betweenDays = 0;
        Date startTime = strToDateLong(startTimeStr);
        Date endTime = strToDateLong(endTimeStr);

        long start = startTime.getTime();
        long end = endTime.getTime();

        betweenDays = (int) (Math.abs(end - start) / (24 * 3600 * 1000));

        return betweenDays + 1;
    }

    public static Date strToDateLong(String strDate) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        ParsePosition pos = new ParsePosition(0);
        Date strtodate = formatter.parse(strDate, pos);
        return strtodate;
    }


    public static String getTimeString(String timeStr, int flag) {
        Calendar calendar = stringToCalendar(timeStr);
        Assert.notNull(calendar, "calendar is null");
        if (flag == 1) {
            String time = calendarToString(calendar, "HH:mm");
            return time;
        } else if (flag == 2) {
            String time = calendarToString(calendar, "yyyy-MM-dd");
            return time;
        } else {
            String time = calendarToString(calendar, "yyyy");
            int week = getWeek(calendar);
            return time + "第" + week + "周";
        }
    }

    public static String getYesterdayTime() {
        LocalDate currentDate = LocalDate.now();
        LocalDate yesterdayDate = currentDate.minusDays(1);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String formattedDate = yesterdayDate.format(formatter);
        System.out.println(formattedDate);
        return formattedDate;
    }

    public static Date getStartOfToday() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

}
