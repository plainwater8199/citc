package com.citc.nce.auth.adminUser.vo.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @Author: ylzouf
 * @Contact: ylzouf
 * @Date: 2022/11/9 16:47
 * @Version 1.0
 * @Description:
 */
@Data
@Accessors(chain = true)
public class ChangeDyzUserStatusReq {
    @ApiModelProperty(value = "资产标识", dataType = "String", required = true)
    private String appCode;
    @ApiModelProperty(value = "签名", dataType = "String", required = true)
    private String sign;
    @ApiModelProperty(value = "业务流水号", dataType = "String", required = true)
    private String bizSn;
    @ApiModelProperty(value = "应用系统账号", dataType = "String", required = true)
    private String userAccount;
    @ApiModelProperty(value = "状态 0:启用 1:注销 2:冻结", dataType = "String", required = true)
    private String status;
}
