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
public class DyzUserChangePhoneReq {
    @ApiModelProperty(value = "姓名", dataType = "String")
    String name;
    @ApiModelProperty(value = "旧手机号", dataType = "String", required = true)
    String oldPhone;
    @ApiModelProperty(value = "新手机号", dataType = "String", required = true)
    String newPhone;
    @ApiModelProperty(value = "邮箱", dataType = "String", required = true)
    String email;
}
