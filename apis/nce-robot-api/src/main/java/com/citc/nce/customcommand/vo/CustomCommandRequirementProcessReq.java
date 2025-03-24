package com.citc.nce.customcommand.vo;

import com.citc.nce.customcommand.enums.CustomCommandRequirementState;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author jiancheng
 */
@Data
public class CustomCommandRequirementProcessReq {
    @NotNull
    private Long id;

    @ApiModelProperty("需求状态, 0:待处理,1:已处理,2:已关闭")
    private CustomCommandRequirementState status;

    @ApiModelProperty("关闭原因")
    private String closeReason;
}
