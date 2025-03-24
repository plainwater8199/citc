package com.citc.nce.aim.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class ResponseInfo {
    @ApiModelProperty(value = "code", dataType = "Integer")
    private Integer code;
    @ApiModelProperty(value = "响应信息", dataType = "String")
    private String msg;
    @ApiModelProperty(value = "发送结果信息", dataType = "Object")
    private ResponseData data;
}
