package com.citc.nce.robot.api.mall.common;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * <p>TODO</p>
 *
 * @Author zy.qiu
 * @CreatedTime 2023/11/25 11:48
 */
@Data
@Accessors(chain = true)
public class RobotVariable {
    @ApiModelProperty(value = "变量id", dataType = "Integer")
    private String id;

    @ApiModelProperty(value = "变量名", dataType = "String")
    private String variableName;

    @ApiModelProperty(value = "默认值", dataType = "String")
    private String variableValue;

    @ApiModelProperty(value = "创建时间", dataType = "String")
    private Date createTime;

    @ApiModelProperty(value = "类型", dataType = "boolean")
    private String type;
}
