package com.citc.nce.auth.identification.vo.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @BelongsProject: chatbot
 * @BelongsPackage: com.citc.nce.auth.identification.vo.req
 * @Author: weilanglang
 * @CreateTime: 2022-08-23  16:26
 * @Description: 平台使用权限请求
 * @Version: 1.0
 */
@Data
public class IdentificationPlatformPermissionReq {
    @ApiModelProperty(value = "用户ID", dataType = "String")
    private String userId;
//    平台信息(1核能商城2硬核桃3chatbot)
    @ApiModelProperty(value = "平台信息(1核能商城2硬核桃3chatbot)", dataType = "Integer")
    private Integer protal;
}
