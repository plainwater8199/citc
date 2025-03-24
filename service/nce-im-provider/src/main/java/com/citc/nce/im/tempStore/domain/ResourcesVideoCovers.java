package com.citc.nce.im.tempStore.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * <p>
 * 扩展商城-资源管理-视频-封面资源表
 * </p>
 *
 * @author bydud
 * @since 2023-11-29 03:11:51
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("ts_resources_video_covers")
@ApiModel(value = "ResourcesVideoCovers对象", description = "扩展商城-资源管理-视频-封面资源表")
public class ResourcesVideoCovers implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("封面文件对应id")
    @TableField("cover")
    private String cover;

    @ApiModelProperty("归属视频资源id")
    @TableField("video_id")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long videoId;


}
