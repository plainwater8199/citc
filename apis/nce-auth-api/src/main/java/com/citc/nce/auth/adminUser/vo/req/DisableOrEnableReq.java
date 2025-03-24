package com.citc.nce.auth.adminUser.vo.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @Author: ylzouf
 * @Contact: ylzouf
 * @Date: 2022/9/23 12:43
 * @Version 1.0
 * @Description:
 */
@Data
@Accessors(chain = true)
public class DisableOrEnableReq {
    @NotBlank(message = "code不能为空")
    @ApiModelProperty(value = "code", dataType = "String", required = true)
    private String code;

    @NotNull(message = "状态不能为空")
    @ApiModelProperty(value = "状态(1启用,2禁用)", dataType = "Integer", required = true)
    private Integer status;
}
