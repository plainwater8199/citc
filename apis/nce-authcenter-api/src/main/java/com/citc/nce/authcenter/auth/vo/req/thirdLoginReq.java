package com.citc.nce.authcenter.auth.vo.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class thirdLoginReq {
    @NotBlank(message = "token不能为空")
    @ApiModelProperty(value = "token", dataType = "String", required = true)
    private String token;

    @NotNull(message = "来源")
    @ApiModelProperty(value = "来源（1-aicc)", dataType = "Integer", required = true)
    private Integer source;
}
