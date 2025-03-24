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
public class RobotAccountReq  implements Serializable {

    /**
     * 5g消息账户
     */
    @ApiModelProperty(value = "5g消息账户")
    @NotBlank(message = "5g消息账户不能为空")
    private String account;

    /**
     * 5g消息账户id
     */
    @ApiModelProperty(value = "5g消息账户id")
    private String chatbotAccountId;

    /**
     * 0 硬核桃 1联调 2移动
     */
    @ApiModelProperty(value = "0 硬核桃 1联调 2移动")
    @NotNull(message = "通道不能为空")
    private Integer channelType;

    /**
     * 手机号
     */
    @ApiModelProperty(value = "手机号")
    @NotBlank(message = "手机号不能为空")
    private String mobileNum;



    /**
     * 会话id
     */
    @ApiModelProperty(value ="会话id")
    @NotBlank(message = "会话id不能为空")
    private String conversationId;

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
