package com.citc.nce.modulemanagement.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 组件管理表-客户侧被使用
 * </p>
 *
 * @author fsyud
 * @since 2024-09-26 04:09:29
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("module_used")
@ApiModel(value = "ModuleUsed对象", description = "组件管理表-客户侧被使用")
public class ModuleUsed implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("主键id")
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    @ApiModelProperty("组件id(组件表主键id)")
    @TableField("module_id")
    private Long moduleId;

    @ApiModelProperty("组件名称")
    @TableField("module_name")
    private String moduleName;

    @ApiModelProperty("组件类别：SIGN-打卡组件，SUBSCRIBE-订阅组件")
    @TableField("module_type")
    private String moduleType;

    @ApiModelProperty("组件描述")
    @TableField("description")
    private String description;

    @ApiModelProperty("创建时间")
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @ApiModelProperty("创建者")
    @TableField(value = "creator", fill = FieldFill.INSERT)
    private String creator;

    @ApiModelProperty("更新时间")
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @ApiModelProperty("更新者")
    @TableField(value = "updater", fill = FieldFill.INSERT_UPDATE)
    private String updater;

    @ApiModelProperty("是否删除")
    @TableField("deleted")
    private Integer deleted;


}
