package com.citc.nce.robot.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class SendQuantityResp {
    @ApiModelProperty(value = "通道类型")
    private String channelType;

    @ApiModelProperty(value = "数量")
    private Integer quantity;
}
