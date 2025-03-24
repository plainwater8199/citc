package com.citc.nce.authcenter.auth.vo.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class ApplyPlatformPermissionReq {
    @ApiModelProperty(value = "用户ID", dataType = "String",required = true)
    private String userId;
    //    平台信息(1核能商城2硬核桃3chatbot)
    @ApiModelProperty(value = "平台信息(1核能商城2硬核桃3chatbot)", dataType = "Integer",required = true)
    private Integer protal;
}
