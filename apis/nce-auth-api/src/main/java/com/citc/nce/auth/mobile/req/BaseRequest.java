package com.citc.nce.auth.mobile.req;

import lombok.Data;

/**
 * 公用字段基础类
 */
@Data
public class BaseRequest {

    //Chatbot归属的CSP平台 ID
    private String  cspId;
    //操作类型：0-新增1-修改
    private Integer  opType;

    //操作时间，新增时必填
    private String  opTime;
    //版本号，从1开始递增。
    private String  eTag;
    //操作流水号
    private String  messageId;

    private String creator;
}
