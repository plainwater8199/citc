package com.citc.nce.authcenter.identification.vo.req;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@ApiModel(description = "Admin端 审核client 用户相关认证")
public class AuditIdentificationReq {
    /**
     * 客户端用户user_id
     */
    @ApiModelProperty(value = "客户端用户user_id", required = true)
    private String clientUserId;

    @ApiModelProperty(value = "资质id", required = true)
    private Integer identificationId;

    /**
     * 1 用户  2 企业
     */
    @ApiModelProperty(value = "1 用户  2 企业", required = true)
    private Integer flag;
    /**
     * 2 认证不通过 3 认证通过
     */
    @ApiModelProperty(value = "2 认证不通过 3 认证通过", required = true)
    private Integer authStatus;
    /**
     * 备注
     */
    @ApiModelProperty(value = "备注")
    private String auditRemark;
}
