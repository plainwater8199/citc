package com.citc.nce.authcenter.auth.vo.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
@Data
public class UpdateClientUserReq {
    @NotNull(message = "user_id不能为空")
    @ApiModelProperty(value = "客户端用户user_id", required = true)
    private String userId;
    @ApiModelProperty(value = "客户端用户账号名", required = true)
    private String accountName;
    @ApiModelProperty(value = "客户端用户phone", required = true)
    private String phone;
    @ApiModelProperty(value = "客户端用户email", required = true)
    private String email;
    @ApiModelProperty(value = "客户端用户图片", required = true)
    private String userImgUuid;
}
