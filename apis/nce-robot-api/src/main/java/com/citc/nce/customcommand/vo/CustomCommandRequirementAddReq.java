package com.citc.nce.customcommand.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @author jiancheng
 */
@Data
public class CustomCommandRequirementAddReq {
    @NotNull(message = "滑动验证码")
    @ApiModelProperty(value = "滑动验证码")
    private String rotateCheck2Id;


    @ApiModelProperty("联系名称")
    @NotBlank
    @Length(max = 10, message = "联系人姓名不能超过10个字符")
    private String contactName;

    @ApiModelProperty("联系电话")
    @NotBlank
    private String contactPhone;

    @ApiModelProperty("需求描述")
    @NotBlank
    private String description;

}
