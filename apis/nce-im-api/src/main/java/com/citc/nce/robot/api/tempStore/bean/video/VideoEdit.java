package com.citc.nce.robot.api.tempStore.bean.video;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author bydud
 * @since 15:07
 */
@Data
@ApiModel(value = "ResourcesVideo对象Edit", description = "扩展商城-资源管理-视频")
public class VideoEdit {
    @NotNull(message = "视频id不能为空")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long videoId;

    @ApiModelProperty("封面")
    private String cover;

    @ApiModelProperty("名称")
    private String name;
}
