package com.citc.nce.customcommand.vo;

import com.citc.nce.customcommand.enums.CustomCommandContentType;
import com.citc.nce.customcommand.enums.CustomCommandType;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @author jiancheng
 */
@Data
public class CustomCommandActiveReq {
    @NotNull
    @ApiModelProperty("id")
    private Long id;

    @NotNull
    @ApiModelProperty("是否激活 激活为true")
    private Boolean active;
}
