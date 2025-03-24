package com.citc.nce.auth.merchant.vo.resp;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author pengwanggf
 * @version 1.0
 * @project base-archetype
 * @description
 * @date 2022/8/27 09:22:12
 */
@Data
public class UpdateMerchantInfoResp {
    @ApiModelProperty(value = "商户基本信息")
    private MerchantInfo merchantInfo;

}
