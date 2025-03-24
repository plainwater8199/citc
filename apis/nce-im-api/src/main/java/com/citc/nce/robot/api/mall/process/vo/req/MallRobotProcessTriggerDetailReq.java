package com.citc.nce.robot.api.mall.process.vo.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * @Author: yangchuang
 * @Date: 2022/7/11 15:43
 * @Version: 1.0
 * @Description:
 */
@Data
public class MallRobotProcessTriggerDetailReq implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 场景id
     */
    @ApiModelProperty("场景id")
    private String templateId;

    /**
     * 流程id
     */
    @ApiModelProperty("流程id")
    @NotBlank(message = "流程id不能为空")
    private String processId;
}
