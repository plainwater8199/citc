package com.citc.nce.robot.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

/**

 *
 * @Author zy.qiu
 * @CreatedTime 2023/1/13 17:07
 */
@Data
public class RobotCustomCommandResp {
    @ApiModelProperty(value="状态码 0：成功， 1：失败")
    String code;
//    @ApiModelProperty(value="python返回的送审数据")
//    private List<RobotCustomCommandDataParam> data;

    @ApiModelProperty(value="redisKey")
    String redisKey;
}
