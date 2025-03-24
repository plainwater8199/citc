package com.citc.nce.authcenter.auth.vo.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class UpdateDyzUserReq {
    @ApiModelProperty(value = "是否为管理员", dataType = "Boolean")
    Boolean isAdmin;
    @ApiModelProperty(value = "旧手机号", dataType = "String", required = true)
    String oldPhone;
    @ApiModelProperty(value = "旧用户名称", dataType = "String", required = true)
    String oldUserName;
    @ApiModelProperty(value = "新手机号", dataType = "String", required = true)
    String newPhone;
    @ApiModelProperty(value = "新用户名称", dataType = "String", required = true)
    String newUserName;
    @ApiModelProperty(value = "邮箱", dataType = "String", required = true)
    String email;

}
