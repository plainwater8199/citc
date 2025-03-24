package com.citc.nce.authcenter.auth.vo.resp;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class ModifyUserPhoneOrEmailResp {
    @ApiModelProperty(value = "验证码校验执行结果", dataType = "Boolean")
    private Boolean checkResult;

    @ApiModelProperty(value = "执行结果", dataType = "Boolean")
    private Boolean execResult;


}
