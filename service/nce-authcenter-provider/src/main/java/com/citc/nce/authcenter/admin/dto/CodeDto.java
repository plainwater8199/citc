package com.citc.nce.authcenter.admin.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class CodeDto {
    @ApiModelProperty(value = "code", dataType = "String", required = true)
    private String code;
}
