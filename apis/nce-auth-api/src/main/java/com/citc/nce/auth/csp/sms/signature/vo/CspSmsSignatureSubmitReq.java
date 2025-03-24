package com.citc.nce.auth.csp.sms.signature.vo;


import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.Valid;
import java.io.Serializable;
import java.util.List;

@Data
public class CspSmsSignatureSubmitReq implements Serializable {

    @ApiModelProperty(value = "账号id")
    private String accountId;

    @ApiModelProperty(value = "类型 0:短信 1:其他")
    private Integer type;

    @ApiModelProperty(value = "签名具体属性", dataType = "List")
    @Valid
    private List<CspSmsSignatureSaveReq> signatureList;
}
