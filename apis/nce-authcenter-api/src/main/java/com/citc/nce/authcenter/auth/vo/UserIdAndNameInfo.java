package com.citc.nce.authcenter.auth.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class UserIdAndNameInfo {
    @ApiModelProperty(value = "用户id", dataType = "String")
    private String userId;

    @ApiModelProperty(value = "用户名称", dataType = "String")
    private String name;

    @ApiModelProperty(value = "用户头像UUID")
    private String userImgUuid;

    @ApiModelProperty(value = "用户状态(0初始化 默认未开启,1启用,2禁用)", dataType = "Integer")
    private Integer userStatus;

    @ApiModelProperty(value = "个人实名认证状态 0 未认证 1 认证审核中 2 认证不通过 3 认证通过", dataType = "Integer")
    private Integer personAuthStatus;
}
