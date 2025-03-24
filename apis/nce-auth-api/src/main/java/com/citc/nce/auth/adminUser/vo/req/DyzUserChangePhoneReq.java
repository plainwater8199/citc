package com.citc.nce.auth.adminUser.vo.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @Author: ylzouf
 * @Contact: ylzouf
 * @Date: 2022/11/11 16:29
 * @Version 1.0
 * @Description:
 */
@Data
@Accessors(chain = true)
public class DyzUserChangePhoneReq extends DyzOnlyIdentificationReq {
    @ApiModelProperty(value = "旧手机号", dataType = "String", required = true)
    String oldPhone;
    @ApiModelProperty(value = "新手机号", dataType = "String", required = true)
    String newPhone;
    @ApiModelProperty(value = "邮箱", dataType = "String", required = true)
    String email;
}
