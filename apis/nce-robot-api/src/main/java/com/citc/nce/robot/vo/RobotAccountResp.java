package com.citc.nce.robot.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

/**
 * @Author: yangchuang
 * @Date: 2022/10/16 10:18
 * @Version: 1.0
 * @Description:
 */
@Data
public class RobotAccountResp implements Serializable {

    @ApiModelProperty("id")
    private Long id;

    /**
     * 5g消息账户
     */
    @ApiModelProperty(value = "5g消息账户")
    private String account;

    /**
     * 5g消息账户id
     */
    @ApiModelProperty(value = "5g消息账户id")
    private String chatbotAccountId;

    /**
     * 5g消息账户名称
     */
    @ApiModelProperty(value = "5g消息账户名称")
    private String accountName;

    /**
     * 0 硬核桃 1联调 2移动
     */
    @ApiModelProperty(value = "0 硬核桃 1联调 2移动")
    private int channelType;

    /**
     * 手机号
     */
    @ApiModelProperty(value = "手机号")
    private String mobileNum;

    /**
     * 会话id
     */
    @ApiModelProperty(value ="会话id")
    private String conversationId;

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
