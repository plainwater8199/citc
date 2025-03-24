package com.citc.nce.auth.user;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@ApiModel(description = "Admin端 通过账号名发送短信")
public class SmsCaptchaOnNameLoadReq {
    @ApiModelProperty(value = "账号名 或手机号")
    private String account;

}
