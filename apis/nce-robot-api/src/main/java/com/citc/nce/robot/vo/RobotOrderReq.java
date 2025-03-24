package com.citc.nce.robot.vo;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * @BelongsProject: chatbot
 * @BelongsPackage: com.citc.nce.robot.vo
 * @Author: weilanglang
 * @CreateTime: 2022-07-01  15:31
 * @Description:  机器人设置--指令管理REQ
 * @Version: 1.0
 */
@Data
@Accessors(chain = true)
public class RobotOrderReq implements Serializable {
    private Long id;
    private Long oldId;
    private String chatbotAccountId;
    private String orderName;
    private Integer requestType;
    private String requestUrl;
    private String headerList;
    private Integer requestBodyType;
    private Integer requestRawType;
    private String bodyList;
    private Integer responseType;
    private String responseList;
    private String creator;
    private Date createTime;
    private String updater;
    private Date updateTime;
    private Integer deleted;
    //描述
    private String depiction;
    //指令类型   0 http   1自定义
    private Integer orderType;

    private String requestUrlName;

    // 扩展商城订单Id
    private Long tsOrderId;

}
