package com.citc.nce.auth.csp.sms.signature.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
@Data
public class CspSmsSignatureResp implements Serializable {

    @ApiModelProperty(value = "id")
    private Long id;

    @ApiModelProperty(value = "账号id")
    private String accountId;

    @ApiModelProperty(value = "签名")
    private String signature;

    @ApiModelProperty(value = "类型 0:视频短信 1:其他")
    private Integer type;
}
