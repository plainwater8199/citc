package com.citc.nce.customcommand.vo;

import com.citc.nce.customcommand.enums.CustomCommandRequirementState;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author jiancheng
 */
@Data
public class CustomCommandRequirementDetailVo {
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    @ApiModelProperty("联系名称")
    private String contactName;

    @ApiModelProperty("联系电话")
    private String contactPhone;

    @ApiModelProperty("需求描述")
    private String description;

    @ApiModelProperty("沟通记录")
    private String note;

    @ApiModelProperty("关闭原因")
    private String closeReason;

    @ApiModelProperty("需求状态")
    private CustomCommandRequirementState state;

    @ApiModelProperty("创建时间")
    private LocalDateTime createTime;

    @ApiModelProperty("客户ID")
    private String customerId;
    @ApiModelProperty("账户名")
    private String name;
}
