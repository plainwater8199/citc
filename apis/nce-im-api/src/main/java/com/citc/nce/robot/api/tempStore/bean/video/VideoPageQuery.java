package com.citc.nce.robot.api.tempStore.bean.video;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author bydud
 * @since 10:22
 */
@Data
@ApiModel(value = "csp-模板商城-资源管理-video管理-分页参数")
public class VideoPageQuery {

    private String name;
    @NotNull(message = "分页大小不能为空")
    private Long pageSize;
    @NotNull(message = "当前页不能为空")
    private Long pageNo;
}
