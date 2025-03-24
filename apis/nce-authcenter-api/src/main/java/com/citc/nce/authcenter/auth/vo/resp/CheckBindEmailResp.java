package com.citc.nce.authcenter.auth.vo.resp;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class CheckBindEmailResp {
    @ApiModelProperty(value = "执行结果", dataType = "Boolean")
    private Boolean result;
}
