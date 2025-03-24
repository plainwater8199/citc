package com.citc.nce.authcenter.auth.vo.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class CheckUserInfoIsUniqueReq {
    @ApiModelProperty(value = "账户名", dataType = "String")
    private String name;
    @ApiModelProperty(value = "手机号", dataType = "String")
    private String phone;
}
