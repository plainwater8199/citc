package com.citc.nce.customcommand.vo;

import com.citc.nce.common.core.pojo.PageParam;
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
public class CustomCommandSearchReq extends PageParam {
    private CustomCommandType type;

    @ApiModelProperty("状态")
    private Boolean active;
}
