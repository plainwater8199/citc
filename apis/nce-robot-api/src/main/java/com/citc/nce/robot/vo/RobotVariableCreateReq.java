package com.citc.nce.robot.vo;

import com.citc.nce.common.core.pojo.PageParam;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Author: yangchuang
 * @Date: 2022/10/24 15:29
 * @Version: 1.0
 * @Description:
 */

@Data
public class RobotVariableCreateReq{

    /**
     * 创建者
     */
    @ApiModelProperty("创建者")
    private String creator;

}
