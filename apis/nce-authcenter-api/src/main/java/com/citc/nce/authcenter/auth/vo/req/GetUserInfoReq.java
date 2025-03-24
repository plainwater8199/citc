package com.citc.nce.authcenter.auth.vo.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class GetUserInfoReq {
    @NotNull(message = "用户id不能为空")
    @ApiModelProperty(value = "用户id", dataType = "String", required = true)
    private String userId;
}
