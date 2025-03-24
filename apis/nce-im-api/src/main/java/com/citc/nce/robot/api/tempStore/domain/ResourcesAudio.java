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
 * 扩展商城-资源管理-audio
 * </p>
 *
 * @author bydud
 * @since 2023-11-29 03:11:48
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("ts_resources_audio")
@ApiModel(value = "ResourcesAudio对象", description = "扩展商城-资源管理-audio")
public class ResourcesAudio implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "audio_id", type = IdType.ASSIGN_ID)
    @JsonSerialize(using = ToStringSerializer.class)
    private Long audioId;

    @ApiModelProperty("持续时间")
    @TableField("duration")
    private String duration;

    @ApiModelProperty("名称")
    @TableField("name")
    private String name;

    @ApiModelProperty("名称")
    @TableField("file_id")
    private String fileId;


    @ApiModelProperty("大小")
    @TableField("size")
    private String size;

    @ApiModelProperty("文件类型")
    @TableField("format")
    private String format;

    @ApiModelProperty("创建者")
    @TableField(value = "creator", fill = FieldFill.INSERT)
    private String creator;

    @ApiModelProperty("修改者")
    @TableField(value = "updater", fill = FieldFill.INSERT_UPDATE)
    private String updater;

    @ApiModelProperty("上传时间")
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private Date createTime;

    @ApiModelProperty("修改时间")
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;

    @ApiModelProperty("逻辑删除")
    @TableField("deleted")
    @TableLogic
    private Integer deleted;


}
