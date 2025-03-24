package com.citc.nce.customcommand.entity;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.time.LocalDateTime;

import com.citc.nce.customcommand.enums.CustomCommandContentType;
import com.citc.nce.customcommand.enums.CustomCommandState;
import com.citc.nce.customcommand.enums.CustomCommandType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * <p>
 * 自定义指令
 * </p>
 *
 * @author jcrenc
 * @since 2023-11-09 02:53:48
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("custom_command")
@ApiModel(value = "CustomCommand对象", description = "自定义指令")
public class CustomCommand implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    @ApiModelProperty("指令名称")
    private String name;

    @ApiModelProperty("指令ID")
    private String uuid;

    @ApiModelProperty("指令描述")
    private String description;

    @ApiModelProperty("指令类型,0:定制,1:商品")
    private CustomCommandType type;

    @ApiModelProperty("客户ID")
    private String customerId;

    @ApiModelProperty("指令内容类型,0:python")
    private CustomCommandContentType contentType;

    @ApiModelProperty("指令内容")
    private String content;

    @ApiModelProperty("操作内容 0:未发布,1:已发布,2:编辑未发布")
    private CustomCommandState status;

    @ApiModelProperty("启用状态")
    private Boolean active;

    @ApiModelProperty("素材ID")
    private Long mssId;

    /**
     * 记录用户看的此指令的创建时间，因为createTime字段用来实现多版本功能，同一指令编辑后变化，所以需要此冗余字段
     */
    @ApiModelProperty("生产时间")
    private LocalDateTime produceTime;

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
