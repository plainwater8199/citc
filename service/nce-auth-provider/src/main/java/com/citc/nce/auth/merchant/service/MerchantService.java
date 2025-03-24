package com.citc.nce.auth.merchant.service;


import com.citc.nce.auth.merchant.vo.req.QueryMerchantInfoReq;
import com.citc.nce.auth.merchant.vo.req.UpdateMerchantInfoReq;
import com.citc.nce.auth.merchant.vo.resp.QueryMerchantInfoResp;
import com.citc.nce.auth.merchant.vo.resp.UpdateMerchantInfoResp;

/**
 * @author pengwanggf
 * @version 1.0
 * @project base-archetype
 * @description
 * @date 2022/8/27 09:23:04
 */
public interface MerchantService {
    /**
     * 查询商户的基本信息
     * @param req 请求信息
     * @return 商户的基本信息
     */
    QueryMerchantInfoResp queryMerchantInfo(QueryMerchantInfoReq req);

    /**
     * 更新商户的基本信息
     * @param req 请求信息
     * @return 商户的基本信息
     */
    UpdateMerchantInfoResp updateMerchantInfo(UpdateMerchantInfoReq req);
}
