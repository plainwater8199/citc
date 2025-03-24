package com.citc.nce.auth.adminUser.vo.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class ClientUserCertificateReq {

    /**
     * 客户端用户user_id
     */
    @ApiModelProperty(value = "客户端用户user_id", required = true)
    private String clientUserId;
}
