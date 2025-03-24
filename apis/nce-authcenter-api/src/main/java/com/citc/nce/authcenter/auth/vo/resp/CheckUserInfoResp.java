package com.citc.nce.authcenter.auth.vo.resp;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class CheckUserInfoResp {
    @ApiModelProperty(value = "执行结果", dataType = "boolean")
    private boolean result;
}
