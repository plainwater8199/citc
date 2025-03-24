package com.citc.nce.misc.guide.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * @author jcrenc
 * @since 2024/11/6 16:08
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("sys_guide")
@ApiModel(value = "系统引导类型")
public class SysGuide {

    @ApiModelProperty("主键")
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    @ApiModelProperty("引导名称")
    private String name;

    @ApiModelProperty("引导描述")
    private String description;

    @ApiModelProperty("总步骤数")
    private Integer totalSteps;

    @TableField(value = "is_required")
    @ApiModelProperty("是否必须完成")
    private Boolean required;

    @ApiModelProperty("状态 1:启用 0:禁用")
    private Integer status;




    /*-------------------------审计字段--------------------------*/

    @ApiModelProperty("创建者")
    @TableField(fill = FieldFill.INSERT)
    private String creator;

    @ApiModelProperty("创建时间")
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @ApiModelProperty("更新人")
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private String updater;

    @ApiModelProperty("更新时间")
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @ApiModelProperty("删除时间")
    @TableLogic(value = "'1000-01-01 00:00:00'", delval = "now()")
    private LocalDateTime deleteTime;
}
