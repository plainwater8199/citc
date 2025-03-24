package com.citc.nce.authcenter.auth.vo.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class SetUseImgOrNameReq {


    @ApiModelProperty(value = "头像信息", dataType = "String", required = true)
    private String userImg;

    @ApiModelProperty(value = "userId", dataType = "String", required = false)
    private String userId;
}
