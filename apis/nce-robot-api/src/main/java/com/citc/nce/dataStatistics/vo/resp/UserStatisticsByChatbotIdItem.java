package com.citc.nce.dataStatistics.vo.resp;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel
public class UserStatisticsByChatbotIdItem {
    @ApiModelProperty(value = "chatbotId")
    private String chatbotId;

    @ApiModelProperty(value = "账户名称")
    private String chatbotName;

    @ApiModelProperty(value = "新增用户总数", dataType = "Long")
    private Long newUsersSumNum;

    @ApiModelProperty(value = "活跃用户总数", dataType = "Long")
    private Long activeUsersSumNum;
}
