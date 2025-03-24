package com.citc.nce.customcommand.vo;

import com.citc.nce.common.core.pojo.PageParam;
import com.citc.nce.customcommand.enums.CustomCommandRequirementState;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author jiancheng
 */
@Data
public class CustomCommandRequirementSearchReq extends PageParam {
    @NotNull
    private CustomCommandRequirementState state;

    private String contactNameOrContactPhone;
}
