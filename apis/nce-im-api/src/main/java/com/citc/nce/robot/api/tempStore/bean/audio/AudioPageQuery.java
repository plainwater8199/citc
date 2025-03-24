package com.citc.nce.robot.api.tempStore.bean.audio;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author bydud
 * @since 10:22
 */
@Data
@ApiModel(value = "资源管理-audio-分页查询参数")
public class AudioPageQuery {

    private String name;
    @NotNull(message = "分页大小不能为空")
    private Long pageSize;
    @NotNull(message = "当前页不能为空")
    private Long pageNo;
}
