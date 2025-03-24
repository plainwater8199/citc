package com.citc.nce.robot.domain.massSegment;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;

/**
 *
 * @author bydud
 * @since 2024/5/6
 */
@Data
@ApiModel(value = "MassSegmentVo")
public class MassSegmentVo {
    @ApiModelProperty("手机号前缀")
    @NotBlank(message = "号段不能为空")
    @Length(min = 3, max = 3, message = "号段只能是3位数")
    private String phoneSegment;
    @NotBlank(message = "运营商不能为空")
    @Length(min = 2, max = 2, message = "运营商错误")
    private String operator;
}
