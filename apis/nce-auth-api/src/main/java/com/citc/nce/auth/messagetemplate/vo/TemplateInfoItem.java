package com.citc.nce.auth.messagetemplate.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class TemplateInfoItem {
    @ApiModelProperty("模板ID")
    private Long templateId;
    @ApiModelProperty("模板名称")
    private String templateName;
}
