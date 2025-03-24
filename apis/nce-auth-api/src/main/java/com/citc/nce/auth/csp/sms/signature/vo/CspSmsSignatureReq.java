package com.citc.nce.auth.csp.sms.signature.vo;

import com.citc.nce.common.core.pojo.PageParam;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class CspSmsSignatureReq extends PageParam implements Serializable {

    @ApiModelProperty(value = "账号id")
    private String accountId;

    @ApiModelProperty(value = "类型 0:视频短信 1:其他")
    private Integer type;
}
