package com.citc.nce.robot.vo;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @Author: yangchuang
 * @Date: 2022/7/7 15:04
 * @Version: 1.0
 * @Description:
 */

@Data
public class RobotSceneNodeEditReq implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 场景名称
     */
    @NotNull(message = "id不能为空")
    private Long id;

    /**
     * 场景名称
     */
    @NotBlank(message = "场景名称不能为空")
    private String sceneName;

    /**
     * 场景描述
     */
    private String sceneValue;
    /**
     * 关联账号
     */
    private String accounts;
}
