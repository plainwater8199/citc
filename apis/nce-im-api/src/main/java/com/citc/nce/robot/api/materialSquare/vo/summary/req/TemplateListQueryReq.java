package com.citc.nce.robot.api.materialSquare.vo.summary.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class TemplateListQueryReq {
    @NotNull(message = "模版类型不能为空,0:机器人, 1:5G消息")
    @ApiModelProperty("模版类型")
    private Integer templateType;
    @ApiModelProperty("模版ID")
    private String id;

}
