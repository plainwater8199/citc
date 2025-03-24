package com.citc.nce.filecenter.util;

import cn.hutool.core.text.StrBuilder;

import java.text.DecimalFormat;

public class DurationUtil {

    public static String millisToStringShort(long millis) {
        StrBuilder strBuilder = new StrBuilder();
        long temp = millis;
        long hper = 60 * 60 * 1000L;
        long mper = 60 * 1000L;
        long sper = 1000L;
        if (temp / hper > 0) {
            strBuilder.append(temp / hper).append("小时");
        }
        temp = temp % hper;

        if (temp / mper > 0) {
            strBuilder.append(temp / mper).append("分钟");
        }
        temp = temp % mper;
        if (temp / sper > 0) {
            strBuilder.append(temp / sper).append("秒");
        }
        return strBuilder.toString();
    }

    public static String longToString(long size) {
        long kb = 1024;
        long mb = kb * 1024;
        long gb = mb * 1024;
        String ret = "";
        DecimalFormat df = new DecimalFormat("0.00");
        if(size >= gb){
            ret = df.format(size/(gb*1.0)) + " GB";
        }else if(size >= mb){
            ret = df.format(size/(mb*1.0)) + " MB";
        }else if(size >= kb){
            ret = df.format(size/(kb*1.0)) + " KB";
        }else if(size > 0){
            ret = df.format(size/(1.0)) + " Byte";
        }
        return ret;
    }

    public static long stringToLong(String str) {
        long kb = 1024;
        long mb = kb * 1024;
        long gb = mb * 1024;

        String[] parts = str.split(" ");
        if (parts.length != 2) {
            throw new IllegalArgumentException("Invalid input format");
        }

        double value = Double.parseDouble(parts[0]);
        String unit = parts[1].toLowerCase();

        switch (unit) {
            case "byte":
                return (long) value;
            case "kb":
                return (long) (value * kb);
            case "mb":
                return (long) (value * mb);
            case "gb":
                return (long) (value * gb);
            default:
                throw new IllegalArgumentException("Invalid unit");
        }
    }

}
