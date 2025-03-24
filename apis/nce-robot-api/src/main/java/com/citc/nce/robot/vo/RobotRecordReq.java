package com.citc.nce.robot.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @Author: yangchuang
 * @Date: 2022/10/16 10:18
 * @Version: 1.0
 * @Description:
 */
@Data
public class RobotRecordReq implements Serializable {

    /**
     * 消息内容
     */
    @ApiModelProperty(value = "消息内容")
    @NotBlank(message = "消息内容不能为空")
    private String message;


    /**
     * 会话id
     */
    @ApiModelProperty(value = "会话id")
    @NotBlank(message = "会话id不能为空")
    private String conversationId;

    /**
     * 对话序列号
     */
    @ApiModelProperty(value = "对话序列号")
    @NotNull(message = "对话序列号不能为空")
    private Long serialNum;

    /**
     * 消息发送人
     */
    @ApiModelProperty(value = "消息发送人")
    @NotBlank(message = "消息发送人不能为空")
    private String sendPerson;

    /**
     * 消息类型
     */
    @ApiModelProperty(value = "消息类型")
    @NotNull(message = "消息类型不能为空")
    private Integer messageType;

    /**
     * 消息类型
     */
    @ApiModelProperty(value = "手机号")
    @NotNull(message = "手机号")
    private String mobileNum;

    /**
     * 消息类型
     */
    @ApiModelProperty(value = "5g账号id")
    private String account;

    /**
     * 通道类型
     */
    @ApiModelProperty(value = "channel_type")
    private Integer channelType;

    /**
     * 创建者
     */
    @ApiModelProperty("创建者")
    private String creator;

    /**
     * 更新者
     */
    @ApiModelProperty("更新者")
    private String updater;
}
