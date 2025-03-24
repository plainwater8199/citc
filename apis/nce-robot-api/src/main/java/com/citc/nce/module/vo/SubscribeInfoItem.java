package com.citc.nce.module.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@Data
public class SubscribeInfoItem {

    @ApiModelProperty("订阅内容ID")
    private String subContentId;

    @ApiModelProperty("订阅内容标题")
    private String title;

    @ApiModelProperty("订阅内容模板ID")
    private Long msg5GId;

    @ApiModelProperty("订阅发送内容")
    private String content;

    @ApiModelProperty("发送时间")
    private Date sendTime;
}
