package com.citc.nce.aim.privatenumber.dto;

import lombok.Data;
import lombok.ToString;

/**
 * 文件名:UserSettings
 * 创建者:zhujinyu
 * 创建时间:2023/6/2 10:42
 * 描述:
 */
@Data
@ToString
public class AimUserSettings {

    // {"projectId": "sas1ds", "orderId": "1", "calling": "1", "smsTemplate": "1", "type": "1", "key": "1", "pw": "1"}

    /* 项目id */
    String projectId;
    /* 订单id */
    Long orderId;
    /* 主叫号码,也就是配置的客户号码 */
    String calling;
    /* 短信模板 */
    String smsTemplate;
    /* 短信服务器类型,默认为default */
    String type;
    /* 短信通道key */
    String key;
    /* 短信通道Secret */
    String pw;

    //判断对象属性中有没有null值
    public static boolean hasNullAttribute(AimUserSettings userSettings){
        //判断对象属性中有没有null值
        if(userSettings.getOrderId()==null||
                userSettings.getProjectId()==null||
                userSettings.getCalling()==null||
                userSettings.getSmsTemplate()==null||
                userSettings.getType()==null||
                userSettings.getKey()==null||
                userSettings.getPw()==null){
            return true;
        }
        return false;
    }


}
