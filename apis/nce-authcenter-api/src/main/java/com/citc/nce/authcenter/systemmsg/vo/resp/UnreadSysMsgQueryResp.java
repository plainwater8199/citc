package com.citc.nce.authcenter.systemmsg.vo.resp;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class UnreadSysMsgQueryResp {
    @ApiModelProperty(value = "未读", dataType = "String")
    private String unread;
}
