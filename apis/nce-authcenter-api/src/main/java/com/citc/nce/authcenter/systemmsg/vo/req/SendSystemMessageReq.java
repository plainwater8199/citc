package com.citc.nce.authcenter.systemmsg.vo.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.swing.*;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;

@Data
public class SendSystemMessageReq {
    @NotNull
    @ApiModelProperty(value = "标题", dataType = "String")
    private String msgTitle;
    @NotNull
    @ApiModelProperty(value = "标题", dataType = "String")
    private String msgDetail;
    @NotNull
    @ApiModelProperty(value = "用户ID", dataType = "String")
    private List<String> userIds;
    @ApiModelProperty(value = "消息来源ID：关联业务ID", dataType = "Long")
    private Long sourceId;
    @NotNull
    @ApiModelProperty(value = "业务类型(1:API 2:工单 3:商户中心-订单 4:买家中心-订单 9：系统管理 10:社区系统消息)", dataType = "Integer")
    private Integer businessType;
    @NotNull
    @ApiModelProperty(value = "发送者", dataType = "String")
    private String creator;
}
