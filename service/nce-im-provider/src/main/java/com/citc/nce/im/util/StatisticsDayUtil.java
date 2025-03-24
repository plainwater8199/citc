package com.citc.nce.im.util;

import cn.hutool.core.date.DateUtil;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class StatisticsDayUtil {

    public static List<String> getBetweenDays(String startTime, String endTime)  {
        List<String> betweenTime = new ArrayList<String>();
        try
        {
            Date sdate= new SimpleDateFormat("yyyy-MM-dd").parse(startTime);
            Date edate= new SimpleDateFormat("yyyy-MM-dd").parse(endTime);

            SimpleDateFormat outformat = new SimpleDateFormat("yyyy-MM-dd");

            Calendar sCalendar = Calendar.getInstance();
            sCalendar.setTime(sdate);
            int year = sCalendar.get(Calendar.YEAR);
            int month = sCalendar.get(Calendar.MONTH);
            int day = sCalendar.get(Calendar.DATE);
            sCalendar.set(year, month, day, 0, 0, 0);

            Calendar eCalendar = Calendar.getInstance();
            eCalendar.setTime(edate);
            year = eCalendar.get(Calendar.YEAR);
            month = eCalendar.get(Calendar.MONTH);
            day = eCalendar.get(Calendar.DATE);
            eCalendar.set(year, month, day, 0, 0, 0);

            while (sCalendar.before(eCalendar))
            {
                betweenTime.add(outformat.format(sCalendar.getTime()));
                sCalendar.add(Calendar.DAY_OF_YEAR, 1);
            }
            betweenTime.add(outformat.format(eCalendar.getTime()));
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return betweenTime;
    }

    public static List<String> getAllHours() {
        ArrayList<String> dates = new ArrayList<>();
        try {
            Date day = new Date();
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd 00:00:00");
            SimpleDateFormat format = new SimpleDateFormat("HH:mm");
            String s = df.format(day);
            Date date = df.parse(s);
            for (int i = 0; i < 24; i++) {
                Calendar cal = Calendar.getInstance();
                cal.setTime(date);
                cal.add(Calendar.HOUR, 1);
                date = cal.getTime();
                String s1 = format.format(date);
                dates.add(s1);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return dates;
    }

    /**
     * 得到某一天是这一年的第几周
     *
     * @param date 时间
     * @return 哪一年第几周
     */
    public static String getWeek(String date) {
        try {
            Calendar cal = Calendar.getInstance();
            cal.setFirstDayOfWeek(Calendar.MONDAY);
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            Date parse = format.parse(date);
            cal.setTime(parse);
            int weekOfYear  = cal.get(Calendar.WEEK_OF_YEAR);
            if (cal.get(Calendar.MONTH)>=11 && weekOfYear<=1 ){
                weekOfYear += 52;
            }
            return DateUtil.year(parse) + "第" + weekOfYear + "周";
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }
}
