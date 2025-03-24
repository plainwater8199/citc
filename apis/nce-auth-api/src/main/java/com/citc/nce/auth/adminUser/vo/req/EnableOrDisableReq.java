package com.citc.nce.auth.adminUser.vo.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;

@Data
@Accessors(chain = true)
public class EnableOrDisableReq {
    @NotNull(message = "userId不能为空")
    @ApiModelProperty(value = "用户id", dataType = "String", required = true)
    private String userId;

    @NotNull(message = "用户状态不能为空")
    @ApiModelProperty(value = "平台权限(0初始化 默认未开启,1启用,2禁用)", dataType = "Integer", required = false)
    private Integer userStatus;

    @NotNull(message = "protal不能为空")
    @ApiModelProperty(value = "平台信息(1核能商城 2硬核桃 3chatbot)", dataType = "Integer", required = true)
    private Integer protal;
}
