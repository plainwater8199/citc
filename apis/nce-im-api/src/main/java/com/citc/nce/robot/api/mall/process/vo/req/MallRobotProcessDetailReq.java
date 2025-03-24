package com.citc.nce.robot.api.mall.process.vo.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * <p>扩展商城-商品 新增</p>
 *
 * @Author zy.qiu
 * @CreatedTime 2023/6/8 17:44
 */
@Data
public class MallRobotProcessDetailReq implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "流程uuid", dataType = "String", required = true)
    @NotBlank(message = "流程uuid不能为空")
    private String processId;
}
