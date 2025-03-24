package com.citc.nce.keywordsreply.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


@Data
public class KeywordsReplyStatisticsInfo {

    @ApiModelProperty("关键字")
    private String keywords;
    @ApiModelProperty("用户ID")
    private String customerId;
    @ApiModelProperty("消息ID")
    private String messageId;
}
