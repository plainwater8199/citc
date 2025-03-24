package com.citc.nce.authcenter.systemmsg.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@Data
public class UserMsgInfo {
    @ApiModelProperty(value = "id", dataType = "Long")
    private Long id;

    @ApiModelProperty(value = "msgId", dataType = "String")
    private String msgId;

    @ApiModelProperty(value = "标题", dataType = "String")
    private String msgTitle;

    @ApiModelProperty(value = "发送时间", dataType = "Date")
    private Date postTime;

    @ApiModelProperty(value = "消息类型", dataType = "String")
    private Integer msgType;

    @ApiModelProperty(value = "业务类型(1:API 2:工单 9:系统消息)")
    private Integer businessType;
}
