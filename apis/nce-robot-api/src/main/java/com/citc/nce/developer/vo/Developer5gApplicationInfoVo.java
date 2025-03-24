package com.citc.nce.developer.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotBlank;

/**
 * @author ping chen
 */
@Data
public class Developer5gApplicationInfoVo {

    @ApiModelProperty("名称")
    @NotBlank(message = "名称不能为空")
    @Max(value = 50,message = "名称长度最大允许50字符")
    private String name;

    @ApiModelProperty("描述")
    @Max(value = 100,message = "描述长度最大允许100字符")
    private String describe;

    @ApiModelProperty("模板类型，1:普通模板，2:个性模板")
    @NotBlank(message = "模板类型不能为空")
    private Integer templateType;

    @ApiModelProperty("模板Id")
    @NotBlank(message = "模板Id不能为空")
    private Long templateId;

    @ApiModelProperty("chabot账号Id")
    @NotBlank(message = "chabot账号Id不能为空")
    private String chatbotAccountId;

    @ApiModelProperty("唯一键(编辑的时候必传)")
    private String uniqueId;
}
