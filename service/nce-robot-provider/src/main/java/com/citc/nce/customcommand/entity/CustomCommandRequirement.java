package com.citc.nce.customcommand.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDateTime;

import com.citc.nce.customcommand.enums.CustomCommandRequirementState;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * <p>
 * 定制需求管理(自定义指令)
 * </p>
 *
 * @author jcrenc
 * @since 2023-11-09 02:53:48
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("custom_command_requirement")
@ApiModel(value = "CustomCommandRequirement对象", description = "定制需求管理(自定义指令)")
public class CustomCommandRequirement implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    @ApiModelProperty("客户ID")
    private String customerId;

    @ApiModelProperty("联系名称")
    private String contactName;

    @ApiModelProperty("联系电话")
    private String contactPhone;

    @ApiModelProperty("需求描述")
    private String description;

    @ApiModelProperty("沟通记录")
    private String note;

    @ApiModelProperty("关闭原因")
    private String closeReason;

    @ApiModelProperty("需求状态, 0:待处理,1:已处理,2:已关闭")
    private CustomCommandRequirementState status;

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
    @TableLogic(value = "null", delval = "now()")
    private LocalDateTime deleteTime;


}
