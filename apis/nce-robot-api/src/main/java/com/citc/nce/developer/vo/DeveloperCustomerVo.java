package com.citc.nce.developer.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author ping chen
 */
@Data
public class DeveloperCustomerVo {

    @ApiModelProperty("id")
    private Long Id;

    @ApiModelProperty("唯一标识")
    private String appId;

    @ApiModelProperty("客户账号Id")
    private String accountId;

    @ApiModelProperty("客户账号名称")
    private String accountName;

    @ApiModelProperty("手机号")
    private String phone;

    @ApiModelProperty("模板Id")
    private String templateId;
    @ApiModelProperty("模板名称")
    private String templateName;
    @ApiModelProperty("模板签名")
    private String templateSign;
    @ApiModelProperty("消息类型")
    private Integer messageType;
    @ApiModelProperty("模板内容")
    private String templateContent;
    @ApiModelProperty("快捷按钮")
    private String shortcutButton;

    @ApiModelProperty("关联签名内容")
    private String signatureContent;

    @ApiModelProperty("发送结果")
    private String sendResult;

    @ApiModelProperty("客户调用结果,0:成功 1:失败")
    private Integer callResult;

    @ApiModelProperty("发送结果消息信息")
    private String callResultMsg;

    @ApiModelProperty("发送网关结果,0:成功，1:失败")
    private Integer sendPlatformResult;

    @ApiModelProperty("网关回执消息发送结果,0:成功，1:失败,2:未知,3:已阅(5G消息状态)")
    private Integer callbackPlatformResult;

    @ApiModelProperty("发送时间")
    private LocalDateTime callTime;

    @ApiModelProperty("回调结果0:成功，1:失败")
    private Integer callbackResult;

    @ApiModelProperty("回调时间")
    private LocalDateTime callbackTime;

    @ApiModelProperty("客户登录账号")
    private String customerUserId;
}
