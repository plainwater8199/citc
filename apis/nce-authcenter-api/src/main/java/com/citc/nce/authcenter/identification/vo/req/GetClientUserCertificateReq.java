package com.citc.nce.authcenter.identification.vo.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class GetClientUserCertificateReq {
    @ApiModelProperty(value = "客户端用户user_id", required = true)
    private String clientUserId;
}
