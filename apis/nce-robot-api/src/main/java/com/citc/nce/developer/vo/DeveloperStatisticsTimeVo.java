package com.citc.nce.developer.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author ping chen
 */
@Data
public class DeveloperStatisticsTimeVo {


    @ApiModelProperty("开始时间")
    @NotNull(message = "开始时间不能为空")
    private String startTime;

    @ApiModelProperty("结束时间")
    @NotNull(message = "结束时间不能为空")
    private String endTime;

    @ApiModelProperty("类型 3:短信，2:视屏短信，1：5G消息")
    @NotNull(message = "类型不能为空")
    private Integer type;

    @ApiModelProperty("应用Id(5G消息专用)")
    private String applicationUniqueId;

}
