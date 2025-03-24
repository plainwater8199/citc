package com.citc.nce.robot.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @Author: yangchuang
 * @Date: 2022/7/7 16:32
 * @Version: 1.0
 * @Description:
 */
@Data
public class RobotSceneNodeTreeResp implements Serializable {
    /**
     * id
     */
    @ApiModelProperty("id")
    private Long id;

    /**
     * 场景名称
     */
    @ApiModelProperty("场景名称")
    private String sceneName;

    /**
     * 流程集合
     */
    @ApiModelProperty("流程信息")
    private List<RobotProcessSettingNodeTreeResp> robotProcessSettingNodes;


}
