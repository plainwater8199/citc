package com.citc.nce.auth.adminUser.vo.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @Author: ylzouf
 * @Contact: ylzouf
 * @Date: 2022/11/11 16:36
 * @Version 1.0
 * @Description:
 */
@Data
@Accessors(chain = true)
public class DyzUserStatusReq extends DyzOnlyIdentificationReq {
    @ApiModelProperty(value = "用户名称", dataType = "String", required = true)
    String name;
    @ApiModelProperty(value = "用户状态(0:启用 1:注销 2:冻结)", dataType = "String", required = true)
    String status;
}
