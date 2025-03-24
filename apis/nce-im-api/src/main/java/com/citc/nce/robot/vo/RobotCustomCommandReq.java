package com.citc.nce.robot.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * <p>机器人自定义指令参数</p>
 *
 * @Author zy.qiu
 * @CreatedTime 2023/1/11 18:29
 */
@Data
public class RobotCustomCommandReq {

    /**
     * python返回的送审数据
     **/
    @ApiModelProperty(value="python返回数据")
    @NotNull
    private List<RobotCustomCommandDataParam> data;

    @ApiModelProperty(value = "redisKey",example = "aaaaa")
    private String redisKey;

    private String creator;

    private Integer pageNo;

    private Integer pageSize;

    private String variableName;

    private String variableValue;
}
