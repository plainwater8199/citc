package com.citc.nce.authcenter.auth.vo.resp;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class UpdateUserViolationResp {
    @ApiModelProperty(value = "执行结果",dataType = "Boolean")
    public Boolean result;
}
