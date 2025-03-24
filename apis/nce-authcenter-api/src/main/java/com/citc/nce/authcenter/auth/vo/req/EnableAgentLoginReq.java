package com.citc.nce.authcenter.auth.vo.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import javax.validation.constraints.NotNull;

@Data
public class EnableAgentLoginReq {

    @ApiModelProperty(value = "允许服务商代登录状态")
    @NotNull(message = "服务商代登录状态不能为空")
    private Boolean enableAgentLogin;
}
