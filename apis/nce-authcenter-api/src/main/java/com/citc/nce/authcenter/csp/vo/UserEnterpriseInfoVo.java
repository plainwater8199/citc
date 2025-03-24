package com.citc.nce.authcenter.csp.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author jcrenc
 * @since 2024/2/20 15:12
 */
@Data
@Accessors(chain = true)
@ApiModel("用户企业认证信息")
public class UserEnterpriseInfoVo {
    @ApiModelProperty("用户ID")
    private String userId;
    @ApiModelProperty("企业名称")
    private String enterpriseName;
    @ApiModelProperty("企业账户名称")
    private String enterpriseAccountName;

}
