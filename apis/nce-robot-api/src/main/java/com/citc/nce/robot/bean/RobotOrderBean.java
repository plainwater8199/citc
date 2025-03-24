package com.citc.nce.robot.bean;

import lombok.Data;
import java.io.Serializable;
import java.util.Date;

/**
 * @BelongsProject: chatbot
 * @BelongsPackage: com.citc.nce.robot.bean
 * @Author: weilanglang
 * @CreateTime: 2022-07-01  15:41
 
 * @Version: 1.0
 */
@Data
public class RobotOrderBean implements Serializable {
    private Long id;
    private Long robotId;
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
    private Date deletedTime;
    //描述
    private String depiction;

    private Integer orderType;

    private String requestUrlName;


}
