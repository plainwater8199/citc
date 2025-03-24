package com.citc.nce.auth.adminUser.vo.req;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
@ApiModel(description = "管理端 更新client端用户")
public class UpdateClientUserReq extends DyzOnlyIdentificationReq {
    /**
     * 客户端用户user_id
     */
    @NotNull(message = "user_id不能为空")
    @ApiModelProperty(value = "客户端用户user_id", required = true)
    private String userId;
    @ApiModelProperty(value = "客户端用户账号名")
    private String accountName;
    @ApiModelProperty(value = "客户端用户phone")
    private String phone;
    @ApiModelProperty(value = "客户端用户email")
    private String email;
    @ApiModelProperty(value = "客户端用户图片")
    private String userImgUuid;
}
