package com.citc.nce.auth.merchant;



import com.citc.nce.auth.merchant.service.MerchantService;
import com.citc.nce.auth.merchant.vo.req.QueryMerchantInfoReq;
import com.citc.nce.auth.merchant.vo.req.UpdateMerchantInfoReq;
import com.citc.nce.auth.merchant.vo.resp.QueryMerchantInfoResp;
import com.citc.nce.auth.merchant.vo.resp.UpdateMerchantInfoResp;
import com.citc.nce.common.core.pojo.RestResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author pengwanggf
 * @version 1.0
 * @project base-archetype
 * @description 商户中心接口
 * @date 2022/8/27 08:45:20
 */
@RestController
public class MerchantController implements MerchantApi {
    @Resource
    private MerchantService merchantService;

    @Override
    @PostMapping("/merchant/queryMerchantInfo")
    public  QueryMerchantInfoResp queryMerchantInfo(QueryMerchantInfoReq Req) {
        return merchantService.queryMerchantInfo(Req);
    }

    @Override
    @PostMapping("/merchant/updateMerchantInfo")
    public UpdateMerchantInfoResp updateMerchantInfo(UpdateMerchantInfoReq Req) {
        return merchantService.updateMerchantInfo(Req);
    }
}
