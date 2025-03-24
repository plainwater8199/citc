package com.citc.nce.auth.adminUser.vo.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @Author: ylzouf
 * @Contact: ylzouf
 * @Date: 2022/11/11 16:27
 * @Version 1.0
 * @Description:
 */
@Data
@Accessors(chain = true)
public class DyzOnlyIdentificationReq {
    @ApiModelProperty(value = "资产标识", dataType = "String", required = false)
    String appCode;
    @ApiModelProperty(value = "秘钥", dataType = "String", required = false)
    String priKey;
    @ApiModelProperty(value = "所属机构", dataType = "String", required = false)
    String dyzOrg;
}
