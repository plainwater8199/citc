package com.citc.nce.auth.tenant.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class AuthCreateCspTableReq {
    @ApiModelProperty(value = "cspId")
    private String cspId;
}
