package com.citc.nce.misc.guide.req;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

/**
 * @author jcrenc
 * @since 2024/11/6 16:59
 */
@Data
public class SysGuideAddReq {
    @ApiModelProperty("引导名称")
    @NotBlank
    private String name;

    @ApiModelProperty("引导描述")
    private String description;

    @ApiModelProperty("总步骤数")
    @NotNull
    @Positive
    private Integer totalSteps;

    @TableField(value = "is_required")
    @ApiModelProperty("是否必须完成")
    @NotNull
    private Boolean required;

    @ApiModelProperty("状态 1:启用 0:禁用")
    @NotNull
    private Integer status;
}
