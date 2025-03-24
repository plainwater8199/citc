package com.citc.nce.dataStatistics.vo.resp;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@ApiModel
public class FastGroupMessageStatisticsResp {
    @ApiModelProperty(value = "快捷群发个数：昨日创建并启动发送的快捷群发数量", dataType = "Long")
    private Long count;

    @ApiModelProperty(value = "快捷群发执行量：通过快捷群发发送的消息数量", dataType = "Long")
    private Long sendNum;


}
