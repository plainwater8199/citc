package com.citc.nce.authcenter.auth.vo.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class SetUseImgOrNameVReq {

    @ApiModelProperty(value = "账户名", dataType = "String")
    private String name;

    @ApiModelProperty(value = "头像信息", dataType = "String")
    private String userImg;

    @ApiModelProperty(value = "userId", dataType = "String", required = false)
    private String userId;

    private boolean csp;
}
