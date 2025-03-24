package com.citc.nce.auth.csp.smsDeveloper.vo;

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
}
