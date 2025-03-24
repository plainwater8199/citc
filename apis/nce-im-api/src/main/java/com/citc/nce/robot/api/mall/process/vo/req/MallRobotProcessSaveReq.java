package com.citc.nce.robot.api.mall.process.vo.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * <p>扩展商城-商品 新增</p>
 *
 * @Author zy.qiu
 * @CreatedTime 2023/6/8 17:44
 */
@Data
public class MallRobotProcessSaveReq implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 流程名称
     */
    @ApiModelProperty("流程名称")
    @NotBlank(message = "流程名称不能为空")
    private String processName;

    /**
     * 流程描述
     */
    @ApiModelProperty("流程描述")
    private String processValue;

    @ApiModelProperty(value = "模板uuid", dataType = "String")
    private String templateId;
}
