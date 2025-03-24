package com.citc.nce.authcenter.auth.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class UserBaseInfo {
    @ApiModelProperty(value = "用户id", dataType = "String")
    private String userId;

    @ApiModelProperty(value = "用户名称", dataType = "String")
    private String name;

    @ApiModelProperty(value = "用户头像UUID")
    private String userImgUuid;
}
