package com.citc.nce.dataStatistics.vo.msg;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class ClickButtonInfoItem {
    @ApiModelProperty(value = "按钮Uuid")
    private String btnUuid;
    @ApiModelProperty(value = "按钮名称")
    private String buttonName;
    @ApiModelProperty(value = "按钮点击数量")
    private Long clickAmount;
}
