package com.citc.nce.aim.vo.resp;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Map;

@Data
@Accessors(chain = true)
public class AimDataStatisticsResp {
    @ApiModelProperty(value = "发送总数", dataType = "Integer")
    private Integer total;

    @ApiModelProperty(value = "折现图数据", dataType = "list")
    private Map<String,Integer> sentDataItemMap;
}
