package com.citc.nce.authcenter.systemmsg.vo.resp;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@Data
public class QueryMsgDetailsResp {
    @ApiModelProperty(value = "id", dataType = "Long")
    private Long id;

    @ApiModelProperty(value = "标题", dataType = "String")
    private String msgTitle;

    @ApiModelProperty(value = "发送时间", dataType = "Date")
    private Date postTime;

    @ApiModelProperty(value = "消息详情", dataType = "String")
    private String msgDetail;

    @ApiModelProperty(value = "业务类型(1:API 2:工单 3:商户中心-订单 4:买家中心-订单)", dataType = "Integer")
    private Integer businessType;

    @ApiModelProperty(value = "消息来源ID", dataType = "Integer")
    private Integer sourceId;
}
