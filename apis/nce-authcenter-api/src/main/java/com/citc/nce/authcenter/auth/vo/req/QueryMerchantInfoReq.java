package com.citc.nce.authcenter.auth.vo.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class QueryMerchantInfoReq {
    @ApiModelProperty(value = "商户的userId",dataType = "String")
    private String userId;
}
