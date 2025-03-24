package com.citc.nce.auth.csp.smsTemplate.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author ping chen
 */
@Data
public class SmsDeveloperSendPhoneVo {

    @ApiModelProperty("消息发送详细Id")
    private String developerSenId;

    @ApiModelProperty("电话")
    private String phone;

    @ApiModelProperty("状态")
    private Integer state;

    @ApiModelProperty("描素")
    private String desc;

    @ApiModelProperty("回调地址")
    private String callBackUrl;
}
