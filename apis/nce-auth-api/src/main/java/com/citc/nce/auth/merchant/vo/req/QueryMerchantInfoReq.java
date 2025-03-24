package com.citc.nce.auth.merchant.vo.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author pengwanggf
 * @version 1.0
 * @project base-archetype
 * @description
 * @date 2022/8/27 08:36:10
 */
@Data
public class QueryMerchantInfoReq {
    @ApiModelProperty(value = "商户的userId",dataType = "String")
    private String userId;
}

