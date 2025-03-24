package com.citc.nce.authcenter.auth.vo.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class CheckUserInfoReq {
    @ApiModelProperty(value = "校验值", dataType = "String", required = true)
    @NotBlank(message = "校验值不能为空")
    private String checkValue;
}
