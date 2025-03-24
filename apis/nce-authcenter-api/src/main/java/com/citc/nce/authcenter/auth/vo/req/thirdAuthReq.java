package com.citc.nce.authcenter.auth.vo.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class thirdAuthReq {
    @NotBlank(message = "code不能为空")
    @ApiModelProperty(value = "code", dataType = "String", required = true)
    private String code;

    @NotBlank(message = "state不能为空")
    @ApiModelProperty(value = "code", dataType = "String", required = true)
    private String state;

    @NotNull(message = "来源")
    @ApiModelProperty(value = "来源（1-aicc)", dataType = "Integer", required = true)
    private Integer source;
}
