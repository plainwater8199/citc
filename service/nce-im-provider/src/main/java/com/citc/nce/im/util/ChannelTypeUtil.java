package com.citc.nce.im.util;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;

/**
 * @Author: yangchuang
 * @Date: 2022/10/31 15:43
 * @Version: 1.0
 * @Description:
 */
public class ChannelTypeUtil {
    //0 硬核桃 1联通 2移动 3电信
    public static Integer getChannelType(String channelType){
         if(StringUtils.isBlank(channelType)){
             return null;
         }else if(channelType.equals("硬核桃")){
             return 0;
         }else if(channelType.equals("联通")){
             return 1;
         }else if(channelType.equals("移动")){
             return 2;
         }else if(channelType.equals("电信")){
             return 3;
         }
        return null;
    }

    //0 硬核桃 1联通 2移动 3电信
    public static String getChannelType(Integer channelType){
        if(channelType==null){
            return null;
        }else if(channelType==0){
            return "硬核桃";
        }else if(channelType==1){
            return "联通";
        }else if(channelType==2){
            return "移动";
        }else if(channelType==3){
            return "电信";
        }
        return null;
    }
}
