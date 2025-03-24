package com.citc.nce.dataStatistics.vo.resp;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@ApiModel
public class UserStatisticsResp {
    @ApiModelProperty(value = "新增用户总数", dataType = "Long")
    private Long newUsersSumNum;

    @ApiModelProperty(value = "活跃用户总数", dataType = "Long")
    private Long activeUsersSumNum;

    @ApiModelProperty(value = "用户分布", dataType = "List")
    private List<UserStatisticsByChatbotIdItem> userList;

    @ApiModelProperty(value = "新增用户时间分布", dataType = "Map")
    private Map<String, Long> newUserDateMap;

    @ApiModelProperty(value = "活跃用户时间分布", dataType = "Map")
    private Map<String, Long> activeUserDateMap;
}
