package com.citc.nce.authcenter.auth.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class UserIdInfo {
    @ApiModelProperty(value = "用户id", dataType = "String", required = true)
    private String userId;
}
