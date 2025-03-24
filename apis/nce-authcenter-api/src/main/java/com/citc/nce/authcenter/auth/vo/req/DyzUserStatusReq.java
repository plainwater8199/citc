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
public class DyzUserStatusReq {
    @ApiModelProperty(value = "用户名称", dataType = "String", required = true)
    String name;
    @ApiModelProperty(value = "用户状态(0:启用 1:注销 2:冻结)", dataType = "String", required = true)
    String status;
}
