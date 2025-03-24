package com.citc.nce.dataStatistics.vo.resp;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel
public class ChannelInfo {
    @ApiModelProperty(value = "通道名称")
    private String name;
    @ApiModelProperty(value = "发送数目")
    private Long sendSum;
}
