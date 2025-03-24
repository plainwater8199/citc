package com.citc.nce.robot.api.tempStore.bean.csp;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Max;
import java.util.List;

/**
 * @author bydud
 * @since 2023-11-17 11:29:51
 */
@Data
public class OrderUseTemplateResp {
    @ApiModelProperty(value = "变量名list")
    private List<EditName> variableEditList;

    @ApiModelProperty(value = "指令名list")
    private List<EditName> orderEditList;
}
