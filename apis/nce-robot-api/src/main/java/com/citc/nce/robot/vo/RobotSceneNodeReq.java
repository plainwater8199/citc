package com.citc.nce.robot.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.Date;

/**
 * @Author: yangchuang
 * @Date: 2022/7/7 15:04
 * @Version: 1.0
 * @Description:
 */

@Data
public class RobotSceneNodeReq implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 场景名称
     */
    @ApiModelProperty("场景名称")
    @NotBlank(message = "场景名称不能为空")
    private String sceneName;

    /**
     * 场景描述
     */
    @ApiModelProperty("场景描述")
    private String sceneValue;

    /**
     * 关联账号
     */
    @ApiModelProperty("关联账号")
    private String accounts;

}
