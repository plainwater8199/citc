package com.citc.nce.robot.api.mall.variable.vo.resp;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

/**
 * <p>扩展商城-商品 新增</p>
 *
 * @Author zy.qiu
 * @CreatedTime 2023/6/8 17:44
 */
@Data
public class MallRobotVariableDetailResp implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "变量id", dataType = "String")
    private String id;

    @ApiModelProperty(value = "变量名", dataType = "String")
    private String variableName;

    @ApiModelProperty(value = "默认值", dataType = "String")
    private String variableValue;

    @ApiModelProperty(value = "创建时间", dataType = "String")
    private Date createTime;
}
