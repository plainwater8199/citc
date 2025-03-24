package com.citc.nce.customcommand.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @author jiancheng
 */
@Data
public class CustomCommandRequirementUpdateNoteReq {
    @NotNull
    private Long id;

    @ApiModelProperty("沟通记录")
    private String note;
}
