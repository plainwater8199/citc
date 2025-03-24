package com.citc.nce.robot.vo;

import com.citc.nce.common.core.pojo.PageParam;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @Author: yangchuang
 * @Date: 2022/7/12 11:53
 * @Version: 1.0
 * @Description:
 */
@Data
public class RobotProcessSettingNodePageReq implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 场景id
     */
    @ApiModelProperty("场景id")
    @NotNull(message = "场景id不能为空")
    private Long sceneId;

    @ApiModelProperty("分页插件")
    private PageParam pageParam;

}
