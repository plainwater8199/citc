package com.citc.nce.authcenter.systemmsg.vo.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class UnreadSysMsgQueryReq {
    @ApiModelProperty(value = "管理员用户ID", dataType = "String")
    private String adminUserId;
}
