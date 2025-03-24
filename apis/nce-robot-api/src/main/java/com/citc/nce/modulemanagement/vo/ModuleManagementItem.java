package com.citc.nce.modulemanagement.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ModuleManagementItem {
    @ApiModelProperty("组件id")
    private Long id;
    @ApiModelProperty("'组件名称'")
    private String moduleName;
    @ApiModelProperty("组件类别：SIGN-打卡组件，SUBSCRIBE-订阅组件")
    private String moduleType;
    @ApiModelProperty("'组件描述'")
    private String description;
    @ApiModelProperty("'创建时间'")
    private LocalDateTime createTime;

    @ApiModelProperty("素材ID")
    private Long mssId;
    @ApiModelProperty("素材ID")
    private String customerId;
}
