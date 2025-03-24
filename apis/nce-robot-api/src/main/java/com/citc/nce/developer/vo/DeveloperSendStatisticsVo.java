package com.citc.nce.developer.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author ping chen
 */
@Data
public class DeveloperSendStatisticsVo {

    @ApiModelProperty("开发者信息id")
    private String appId;

    @ApiModelProperty("客户账号")
    private String accountId;

    @ApiModelProperty("电话号码")
    private String phone;

    @ApiModelProperty("平台模板Id")
    private String platformTemplateId;

    @ApiModelProperty("调用时间")
    private LocalDateTime callTime;

    @ApiModelProperty("客户调用结果,0:成功 1:失败")
    private Integer callResult;

    @ApiModelProperty("发送MQ结果，0：成功 1：失败")
    private Integer sendMqResult;

    @ApiModelProperty("发送平台结果,0:成功 1:失败")
    private Integer sendPlatformResult;

    @ApiModelProperty("平台回执消息发送结果,0:成功，1:失败,2:未知,3:已阅(5G消息状态)")
    private Integer callbackPlatformResult;

    @ApiModelProperty("回调客户结果0:成功，1:失败")
    private Integer callbackResult;

    @ApiModelProperty("回调客户时间")
    private LocalDateTime callbackTime;

    @ApiModelProperty("客户登录账号")
    private String customerId;

    @ApiModelProperty("平台消息Id")
    private String smsId;

    @ApiModelProperty("唯一Id和手机号对应")
    private String customSmsId;

    @ApiModelProperty("1:短信，2:视屏短信，3：5G消息")
    private Integer type;

    @ApiModelProperty("5G消息类型所属应用Id")
    private String applicationUniqueId;

    @ApiModelProperty("cspId")
    private String cspId;
}
