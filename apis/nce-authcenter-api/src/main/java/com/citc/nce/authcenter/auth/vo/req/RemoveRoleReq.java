package com.citc.nce.authcenter.auth.vo.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class RemoveRoleReq {
    @ApiModelProperty(value = "code", dataType = "String", required = true)
    private String code;
}
