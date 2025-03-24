package com.citc.nce.im.massSegment.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 运营商号段关系表
 * </p>
 *
 * @author bydud
 * @since 2024-05-06 04:05:59
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("mass_segment")
@ApiModel(value = "MassSegment对象", description = "运营商号段关系表")
public class MassSegment implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("主键id")
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    @ApiModelProperty("号段")
    @TableField("phone_segment")
    private String phoneSegment;

    @ApiModelProperty("运营商")
    @TableField("operator")
    private String operator;

    @ApiModelProperty("号段类型 system custom")
    @TableField("ms_type")
    private String msType;

    @ApiModelProperty("创建者")
    @TableField(value = "creator", fill = FieldFill.INSERT)
    private String creator;

    @ApiModelProperty("创建时间")
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private Date createTime;

    @ApiModelProperty("更新时间")
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;

    @ApiModelProperty("是否删除")
    @TableField("deleted")
    @TableLogic
    private Integer deleted;

    @ApiModelProperty("删除时间")
    @TableField("delete_time")
    private Date deleteTime;

    @ApiModelProperty("更新者")
    @TableField(value = "updater", fill = FieldFill.INSERT_UPDATE)
    private String updater;


}
