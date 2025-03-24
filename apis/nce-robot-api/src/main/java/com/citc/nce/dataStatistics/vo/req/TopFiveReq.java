package com.citc.nce.dataStatistics.vo.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class TopFiveReq extends StartAndEndTimeReq{
    @ApiModelProperty(value = "5G账户id", dataType = "String", required = false)
    private String chatBotId;
}
