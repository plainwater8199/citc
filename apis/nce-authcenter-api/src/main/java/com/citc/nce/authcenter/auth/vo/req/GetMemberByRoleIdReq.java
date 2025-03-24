package com.citc.nce.authcenter.auth.vo.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class GetMemberByRoleIdReq {
    @ApiModelProperty(value = "code", dataType = "String", required = true)
    private String code;
}
