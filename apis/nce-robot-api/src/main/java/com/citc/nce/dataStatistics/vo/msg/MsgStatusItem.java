package com.citc.nce.dataStatistics.vo.msg;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author jiancheng
 */
@Data
@ApiModel
public class MsgStatusItem {

    @ApiModelProperty("发送时间")
    private String sendTime;

    @ApiModelProperty("群发数量")
    private Long sendAmount;

    @ApiModelProperty("成功数")
    private Long successAmount;

    @ApiModelProperty("发送失败数")
    private Long failAmount;

    @ApiModelProperty("已阅数")
    private Long readAmount;

    @ApiModelProperty("未知数")
    private Long unknowAmount;
}
