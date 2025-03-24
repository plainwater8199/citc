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
 * 扩展商城-资源管理-图片-分组
 * </p>
 *
 * @author bydud
 * @since 2023-11-29 02:11:37
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("ts_resources_img_group")
@ApiModel(value = "ResourcesImgGroup对象", description = "扩展商城-资源管理-图片-分组")
public class ResourcesImgGroup implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "img_group_id", type = IdType.ASSIGN_ID)
    @JsonSerialize(using = ToStringSerializer.class)
    private Long imgGroupId;

    @ApiModelProperty("分组名")
    @TableField("name")
    private String name;

    @ApiModelProperty("创建人")
    @TableField(value = "creator", fill = FieldFill.INSERT)
    private String creator;

    @ApiModelProperty("修改人")
    @TableField(value = "updater", fill = FieldFill.INSERT_UPDATE)
    private String updater;

    @ApiModelProperty("创建时间")
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private Date createTime;

    @ApiModelProperty("修改时间")
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;


    @ApiModelProperty("是否删除")
    @TableField("deleted")
    @TableLogic
    private Integer deleted;
}
