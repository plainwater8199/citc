package com.citc.nce.robot.api.tempStore.domain;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 扩展商城—资源管理-表单管理
 * </p>
 *
 * @author bydud
 * @since 2023-11-23 10:11:59
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("ts_resources_form")
@ApiModel(value = "ResourcesForm对象", description = "扩展商城—资源管理-表单管理")
public class ResourcesForm implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("主键,唯一值")
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    @ApiModelProperty("表单名称")
    @TableField("form_name")
    private String formName;

    @ApiModelProperty("表单内容")
    @TableField("form_details")
    private String formDetails;

    @ApiModelProperty("表单封面")
    @TableField("form_cover")
    private String formCover;

    @ApiModelProperty("创建者")
    @TableField(value = "creator", fill = FieldFill.INSERT)
    private String creator;

    @ApiModelProperty("创建时间")
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private Date createTime;

    @ApiModelProperty("更新者")
    @TableField(value = "updater", fill = FieldFill.INSERT_UPDATE)
    private String updater;

    @ApiModelProperty("更新时间")
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;

    @ApiModelProperty("0未删除   1已删除")
    @TableField("deleted")
    @TableLogic
    private Integer deleted;

    @ApiModelProperty("关联素材id")
    @TableField(value = "mss_id")
    private Long mssId;


}
