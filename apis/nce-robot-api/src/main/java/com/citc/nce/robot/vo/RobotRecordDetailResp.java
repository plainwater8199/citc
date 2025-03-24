package com.citc.nce.robot.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@Data
public class RobotRecordDetailResp {
    /**
     * 5G账号名称
     */
    @ApiModelProperty("5G账号名称")
    private String accountName;
    /**
     * 消息内容
     */
    @ApiModelProperty("消息内容")
    private String message;
    /**
     * 手机号
     */
    @ApiModelProperty("手机号")
    private Long mobileNum;

    /**
     * 对话序列号
     */
    @ApiModelProperty("对话序列号")
    private Long serialNum;

    /**
     * 时间
     */
    @ApiModelProperty("时间")
    private Date time;

    /**
     * 会话id
     */
    @ApiModelProperty("会话id")
    private String conversationId;
}
