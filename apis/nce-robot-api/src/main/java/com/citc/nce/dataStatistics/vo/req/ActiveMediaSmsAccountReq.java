package com.citc.nce.dataStatistics.vo.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class ActiveMediaSmsAccountReq {
    /**
     * 开始时间
     */
    @ApiModelProperty(value = "开始时间",example = "2022-09-09")
    private String startTime;

    /**
     * 结束时间
     */
    @ApiModelProperty(value = "结束时间",example = "2022-09-09")
    private String endTime;
}
