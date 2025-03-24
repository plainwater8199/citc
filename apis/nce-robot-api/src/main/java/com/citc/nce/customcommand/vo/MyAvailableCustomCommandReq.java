package com.citc.nce.customcommand.vo;

import com.citc.nce.common.core.pojo.PageParam;
import com.citc.nce.customcommand.enums.CustomCommandContentType;
import com.citc.nce.customcommand.enums.CustomCommandType;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author jiancheng
 */
@Data
public class MyAvailableCustomCommandReq extends PageParam {
    @ApiModelProperty("是否需要内容")
    private Boolean needContent = false;
}
