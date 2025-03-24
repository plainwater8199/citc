package com.citc.nce.robot.api.tempStore.bean.video;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.util.List;

/**
 * @author bydud
 * @since 8:38
 */
@Data
@ApiModel(value = "ResourcesVideo对象Edit-query", description = "扩展商城-资源管理-视频")
public class VideoQuery {
    @JsonSerialize(using = ToStringSerializer.class)
    private Long videoId;

    private String cover;

    private String name;

    private List<String> covers;
}
