package com.citc.nce.auth.adminUser.vo.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @Author: ylzouf
 * @Contact: ylzouf
 * @Date: 2022/11/11 16:18
 * @Version 1.0
 * @Description:
 */
@Data
@Accessors(chain = true)
public class DyzUserReq extends DyzOnlyIdentificationReq {
    @ApiModelProperty(value = "手机号", dataType = "String", required = true)
    String phone;
    @ApiModelProperty(value = "用户名称", dataType = "String", required = true)
    String userName;
    @ApiModelProperty(value = "邮箱", dataType = "String", required = true)
    String email;
}
