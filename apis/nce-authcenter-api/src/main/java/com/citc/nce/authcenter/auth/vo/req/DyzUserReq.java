package com.citc.nce.authcenter.auth.vo.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @Author: hhluop
 * @Date: 2023/03/01 16:31
 * @Version 1.0
 * @Description:
 */
@Data
@Accessors(chain = true)
public class DyzUserReq {
    @ApiModelProperty(value = "手机号", dataType = "String", required = true)
    String phone;
    @ApiModelProperty(value = "用户名称", dataType = "String", required = true)
    String userName;
    @ApiModelProperty(value = "邮箱", dataType = "String", required = true)
    String email;
    @ApiModelProperty(value = "是否为管理员", dataType = "Boolean")
    Boolean isAdmin;
}
