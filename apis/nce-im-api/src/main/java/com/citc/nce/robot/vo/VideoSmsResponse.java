package com.citc.nce.robot.vo;

import lombok.Data;

import java.io.Serializable;

/**
 *
 * 单条视频短信发送响应
 */
@Data
public class VideoSmsResponse implements Serializable {

    private static final long serialVersionUID = -1264199668011732386L;

    //发送普通视频短信和个性视频短信返回字段
    private String vmsId;

    private String mobile;

    private String customSmsId;

    /*-----------------------接收状态报告字段----------------------------*/
    private Integer state;
    private String operCode;
    private String message;
    private String submitTime;
    private String reportTime;
    private String extendedCode;
    private String dataSource;
    private String subPort;
    private String channelNumber;
}
