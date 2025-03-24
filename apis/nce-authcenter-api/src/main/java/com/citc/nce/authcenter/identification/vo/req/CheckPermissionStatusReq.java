package com.citc.nce.authcenter.identification.vo.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class CheckPermissionStatusReq {
    @ApiModelProperty(value = "用户id", dataType = "String")
    private String userId;
}
