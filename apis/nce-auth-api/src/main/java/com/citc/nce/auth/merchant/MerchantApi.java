package com.citc.nce.auth.merchant;

import com.citc.nce.auth.merchant.vo.req.QueryMerchantInfoReq;
import com.citc.nce.auth.merchant.vo.req.UpdateMerchantInfoReq;
import com.citc.nce.auth.merchant.vo.resp.QueryMerchantInfoResp;
import com.citc.nce.auth.merchant.vo.resp.UpdateMerchantInfoResp;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;

/**
 * @author pengwanggf
 * @version 1.0
 * @project base-archetype
 * @description 商户中心接口
 * @date 2022/8/27 08:22:18
 */
@FeignClient(value = "auth-service", contextId = "merchant-auth", url = "${auth:}")
public interface MerchantApi {


    /**
     * 商户中心--基本信息查询
     *
     * @param Req  商户基本信息
     * @return 结果
     */
    @PostMapping("/merchant/queryMerchantInfo")
    QueryMerchantInfoResp queryMerchantInfo(@RequestBody @Valid QueryMerchantInfoReq Req);

    /**
     * 商户中心--基本信息维护
     *
     * @param Req  商户基本信息
     * @return 结果
     */
    @PostMapping("/merchant/updateMerchantInfo")
    UpdateMerchantInfoResp updateMerchantInfo(@RequestBody @Valid UpdateMerchantInfoReq Req);
}
