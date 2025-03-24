package com.citc.nce.robot.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @Author: yangchuang
 * @Date: 2022/7/8 10:47
 * @Version: 1.0
 * @Description:
 */
@Data
public class RobotProcessSettingNodeTreeResp implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * id
     */
    @ApiModelProperty("id")
    private Long id;

    /**
     * 场景id
     */
    @ApiModelProperty("场景id")
    private Long sceneId;

    /**
     * 流程名称
     */
    @ApiModelProperty("流程名称")
    private String processName;


}
