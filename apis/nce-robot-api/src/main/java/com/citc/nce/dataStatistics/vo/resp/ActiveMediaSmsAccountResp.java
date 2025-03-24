package com.citc.nce.dataStatistics.vo.resp;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel
public class ActiveMediaSmsAccountResp {
    @ApiModelProperty(value = "活跃用户数")
    private List<ActiveMediaSmsAccountItem> activeMediaSmsAccountItems;


}
