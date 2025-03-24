package com.citc.nce.robot.api.mall.variable.vo.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * <p>扩展商城-商品 新增</p>
 *
 * @Author zy.qiu
 * @CreatedTime 2023/6/8 17:44
 */
@Data
public class MallRobotVariableSaveReq implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "变量名", dataType = "String", required = true)
    @NotBlank(message = "变量名不能为空")
    @Length(max = 25,message = "变量名值不能大于25字符")
    private String variableName;

    @ApiModelProperty(value = "默认值", dataType = "String")
    @Length(max = 1000,message = "变量默认值不能大于1000字符")
    private String variableValue;
}
