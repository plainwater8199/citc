package com.citc.nce.auth.adminUser.vo.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class SmsCheckReq {
    @ApiModelProperty(value = "资产标识", dataType = "String", required = true)
    private String appCode;
    @ApiModelProperty(value = "签名", dataType = "String", required = true)
    private String sign;
    @ApiModelProperty(value = "业务流水号", dataType = "String", required = true)
    private String bizSn;
    @ApiModelProperty(value = "手机号", dataType = "String", required = true)
    private String mobile;
    @ApiModelProperty(value = "验证码", dataType = "String", required = true)
    private String authCode;
}
