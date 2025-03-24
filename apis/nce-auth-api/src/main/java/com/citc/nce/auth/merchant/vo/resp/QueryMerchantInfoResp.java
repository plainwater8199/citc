package com.citc.nce.auth.merchant.vo.resp;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author pengwanggf
 * @version 1.0
 * @project base-archetype
 * @description
 * @date 2022/8/27 08:51:08
 */
@Data
public class QueryMerchantInfoResp {
    @ApiModelProperty(value = "商户基本信息")
    private MerchantInfo merchantInfo;

}
