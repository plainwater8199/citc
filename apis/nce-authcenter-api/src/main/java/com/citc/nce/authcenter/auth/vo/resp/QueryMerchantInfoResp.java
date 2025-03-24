package com.citc.nce.authcenter.auth.vo.resp;


import com.citc.nce.authcenter.auth.vo.MerchantInfo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class QueryMerchantInfoResp {
    @ApiModelProperty(value = "商户基本信息")
    private MerchantInfo merchantInfo;
}
