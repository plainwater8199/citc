package com.citc.nce.robot.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @Author: yangchuang
 * @Date: 2022/7/8 10:47
 * @Version: 1.0
 * @Description:
 */
@Data
public class RobotProcessSettingNodeEditDerailReq implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * id
     */
    @ApiModelProperty("id")
    @NotNull(message = "id不能为空")
    private Long id;

    /**
     * 0放开1关闭，默认0
     */
    @ApiModelProperty("0放开1关闭，默认0")
    @NotNull(message = "开关不能为空")
    private int derail;

}
