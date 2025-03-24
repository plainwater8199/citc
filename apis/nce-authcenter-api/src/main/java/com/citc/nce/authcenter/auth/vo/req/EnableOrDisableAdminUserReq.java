package com.citc.nce.authcenter.auth.vo.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@Accessors(chain = true)
public class EnableOrDisableAdminUserReq {
    @NotBlank(message = "code不能为空")
    @ApiModelProperty(value = "code", dataType = "String", required = true)
    private String code;

    @NotNull(message = "状态不能为空")
    @ApiModelProperty(value = "状态(1启用,2禁用)", dataType = "Integer", required = true)
    private Integer status;
}
