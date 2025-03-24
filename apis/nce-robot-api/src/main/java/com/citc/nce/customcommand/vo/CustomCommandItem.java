package com.citc.nce.customcommand.vo;

import com.citc.nce.customcommand.enums.CustomCommandType;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class CustomCommandItem {
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    @ApiModelProperty("指令uuid")
    private String uuid;

    @ApiModelProperty("指令名称")
    private String name;

    @ApiModelProperty("指令描述")
    private String description;

    @ApiModelProperty("指令类型,0:定制,1:商品")
    private CustomCommandType type;

    @ApiModelProperty("客户ID")
    private String customerId;

    @ApiModelProperty("客户名称")
    private String customerName;
}
