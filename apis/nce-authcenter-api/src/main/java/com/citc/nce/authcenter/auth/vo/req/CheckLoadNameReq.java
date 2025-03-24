package com.citc.nce.authcenter.auth.vo.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class CheckLoadNameReq {
    @NotBlank(message = "检查值不能为空")
    @ApiModelProperty(value = "检查值", dataType = "String",required = true)
    private String checkValue;
}
