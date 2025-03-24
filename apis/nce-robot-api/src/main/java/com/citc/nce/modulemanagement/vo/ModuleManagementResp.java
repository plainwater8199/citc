package com.citc.nce.modulemanagement.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class ModuleManagementResp {
    @ApiModelProperty("组件管理列表")
    private List<ModuleManagementItem> items;
}
