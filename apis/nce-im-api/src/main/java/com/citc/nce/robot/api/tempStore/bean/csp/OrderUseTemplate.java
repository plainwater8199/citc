package com.citc.nce.robot.api.tempStore.bean.csp;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author bydud
 * @since 2023-11-17 11:29:51
 */
@Data
public class OrderUseTemplate {

    @ApiModelProperty(value = "订单Id", required = true)
    private Long orderId;

    @ApiModelProperty(value = "类型 0:机器人, 1:5G消息,2:h5,3:自定义指令 4:组件", required = true)
    private Integer type;

    @ApiModelProperty(value = "消息模板名称/应用场景名称")
    private String name;

    @ApiModelProperty(value = "关联5G消息账号")
    private String[] chatbotAccount;

    @ApiModelProperty(value = "场景描述")
    private String desc;

    @ApiModelProperty(value = "变量名list")
    private List<EditName> variableEditList;

    @ApiModelProperty(value = "指令名list")
    private List<EditName> orderEditList;
}
