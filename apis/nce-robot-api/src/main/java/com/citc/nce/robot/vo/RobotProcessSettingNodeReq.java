package com.citc.nce.robot.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

/**
 * @Author: yangchuang
 * @Date: 2022/7/8 10:47
 * @Version: 1.0
 * @Description:
 */
@Data
public class RobotProcessSettingNodeReq implements Serializable {

    private static final long serialVersionUID = 1L;


    /**
     * 场景id
     */
    @ApiModelProperty("场景id")
    @NotNull(message = "场景id不能为空")
    private Long sceneId;

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

}
