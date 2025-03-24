package com.citc.nce.dataStatistics.vo.msg;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class SingleMassTrendItem {

    @ApiModelProperty("发送时间")
    private String sendTime;

    @ApiModelProperty(value = "发送成功")
    private Long successAmount;

    @ApiModelProperty(value = "发送失败")
    private Long failAmount;

    @ApiModelProperty(value = "已阅")
    private Long readAmount;

    @ApiModelProperty(value = "点击按钮")
    private Long clickButtonAmount;

}
