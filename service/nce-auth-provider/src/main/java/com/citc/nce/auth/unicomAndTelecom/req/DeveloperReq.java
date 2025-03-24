package com.citc.nce.auth.unicomAndTelecom.req;

import lombok.Data;

@Data
public class DeveloperReq {
    //协议  1:http，2:https
    private String agreement;
    private String token;
    //回调URL地址根目录  以http://开头，IP+端口的形式，用来接收下行消息状态报告以及消息通知
    private String url;
    private String accessTagNo;
    //Chatbot接口是否启用，1:启用，0:不启用
    private String enable;

}
