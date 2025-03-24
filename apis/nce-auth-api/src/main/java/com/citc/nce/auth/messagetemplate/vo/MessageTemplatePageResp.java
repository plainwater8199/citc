package com.citc.nce.auth.messagetemplate.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class MessageTemplatePageResp {
    @ApiModelProperty("模板列表")
    private List<TemplateInfoItem> templateInfoList;
}
