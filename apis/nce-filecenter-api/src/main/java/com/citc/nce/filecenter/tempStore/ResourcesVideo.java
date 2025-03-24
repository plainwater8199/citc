package com.citc.nce.filecenter.tempStore;

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
import java.util.List;

/**
 * <p>
 * 扩展商城-资源管理-视频
 * </p>
 *
 * @author bydud
 * @since 2023-11-29 03:11:54
 */
@Getter
@Setter
@ApiModel(value = "ResourcesVideo对象", description = "扩展商城-资源管理-视频")
public class ResourcesVideo implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "video_id", type = IdType.ASSIGN_ID)
    @JsonSerialize(using = ToStringSerializer.class)
    private Long videoId;

    @ApiModelProperty("封面")
    @TableField("cover")
    private String cover;

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

    @TableField("crop_obj")
    private String cropObj;

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

    @TableField(exist = false)
    @ApiModelProperty("视频封面推荐图")
    private List<String> covers;





}
