package com.citc.nce.dataStatistics.vo.msg;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel
public class ActiveTrendItem {
    @ApiModelProperty(value = "活跃时间")
    private String activeTime;
    @ApiModelProperty(value = "活跃用户数")
    private Long activeCustomerSum = 0L;
    @ApiModelProperty(value = "活跃5G账号")
    private Long activeAccountSumFor5G = 0L;
    @ApiModelProperty(value = "活跃视频短信账号")
    private Long activeAccountSumForMedia = 0L;
    @ApiModelProperty(value = "活跃短信账号")
    private Long activeAccountSumForShort = 0L;
}
