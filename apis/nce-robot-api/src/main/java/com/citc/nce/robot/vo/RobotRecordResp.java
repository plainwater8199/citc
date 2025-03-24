package com.citc.nce.robot.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @Author: yangchuang
 * @Date: 2022/10/16 10:18
 * @Version: 1.0
 * @Description:
 */
@Data
public class RobotRecordResp implements Serializable {

    @ApiModelProperty("id")
    private Long id;

    /**
     * 消息内容
     */
    @ApiModelProperty(value = "消息内容")
    private String message;


    /**
     * 会话id
     */
    @ApiModelProperty(value = "会话id")
    private String conversationId;

    /**
     * 对话序列号
     */
    @ApiModelProperty(value = "对话序列号")
    private Long serialNum;

    /**
     * 消息发送人
     */
    @ApiModelProperty(value = "消息发送人")
    private String sendPerson;

    /**
     * 消息类型
     */
    @ApiModelProperty(value = "消息类型")
    private int messageType;

    /**
     * 创建者
     */
    @ApiModelProperty("创建者")
    private String creator;

    /**
     * 创建时间
     */
    @ApiModelProperty("创建时间")
    private Date createTime;

    /**
     * 更新者
     */
    @ApiModelProperty("更新者")
    private String updater;

    /**
     * 更新时间
     */
    @ApiModelProperty("更新时间")
    private Date updateTime;
}
