package com.citc.nce.robot.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @Author: yangchuang
 * @Date: 2022/7/11 15:43
 * @Version: 1.0
 * @Description:
 */
@Data
public class RobotProcessTriggerNodeOneReq implements Serializable {
    private static final long serialVersionUID = 1L;


    /**
     * 场景id
     */
    @ApiModelProperty("场景id")
    @NotNull(message = "场景id不能为空")
    private Long sceneId;

    /**
     * 流程id
     */
    @ApiModelProperty("流程id")
    @NotNull(message = "流程id不能为空")
    private Long processId;


}
