package com.citc.nce.robot.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * <p>回调参数</p>
 *
 * @Author zy.qiu
 * @CreatedTime 2022/12/1 11:10
 */
@Data
public class RobotCallBackParam<T> {

    /**
     * 成功或者失败的code错误码
     **/
    @ApiModelProperty(value="0:成功,1:失败", required = true, dataType = "int")
    private Integer code;

    /**
     * redisKey
     **/
    @ApiModelProperty(required = true, dataType = "string")
    private String redisKey;

    /**
     * 成功时返回的数据，失败时返回具体的异常信息
     **/
    @ApiModelProperty(value="成功时返回的数据，失败时返回具体的异常信息")
    private T data;

    /**
     * 请求失败返回的提示信息，给前端进行页面展示的信息
     **/
//    @ApiModelProperty(value="请求失败返回的提示信息，给前端进行页面展示的信息")
//    private Object msg;
}
