package com.citc.nce.robot.vo.directcustomer;

import lombok.Data;

/**
 * 文件名:FallbackSms
 * 创建者:zhujinyu
 * 创建时间:2024/2/28 11:32
 * 描述:
 */
@Data
public class FallbackSms {

        //直发文本内容，templateType为TEXT类型必选
        String text;

//    携带链接类型：
//    NONE：无链接，
//    CHATBOT_LINK：携带H5Chatbot链接，
//    AIM_LINK： 携带智能短信链接
        String linkType;
}
