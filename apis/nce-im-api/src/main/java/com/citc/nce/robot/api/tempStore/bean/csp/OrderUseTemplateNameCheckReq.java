package com.citc.nce.robot.api.tempStore.bean.csp;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author bydud
 * @since 2023-11-17 11:29:51
 */
@Data
public class OrderUseTemplateNameCheckReq {

    @ApiModelProperty(value = "类型 0:机器人, 1:5G消息,2:h5,3:自定义指令", required = true)
    private Integer type;

    @ApiModelProperty(value = "消息模板名称/应用场景名称", required = true)
    private String name;
}
