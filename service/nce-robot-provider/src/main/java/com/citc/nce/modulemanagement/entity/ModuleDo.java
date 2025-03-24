package com.citc.nce.modulemanagement.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * @author jcrenc
 * @since 2024/5/29 15:23
 */
@TableName("module")
@Data
@Accessors(chain = true)
public class ModuleDo {
    @TableId(type = IdType.ASSIGN_ID)
    private Long id ;

    @ApiModelProperty("组件名称")
    private String moduleName ;

    @ApiModelProperty("组件类别：SIGN-打卡组件，SUBSCRIBE-订阅组件")
    private String moduleType;

    @ApiModelProperty("组件描述")
    private String description;

    @ApiModelProperty("创建者")
    @TableField(fill = FieldFill.INSERT)
    private String creator;

    @ApiModelProperty("创建时间")
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @ApiModelProperty("更新者")
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private String updater;

    @ApiModelProperty("更新时间")
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @ApiModelProperty("是否删除")
    private Integer deleted;

    @ApiModelProperty("素材ID")
    private Long mssId;
}
