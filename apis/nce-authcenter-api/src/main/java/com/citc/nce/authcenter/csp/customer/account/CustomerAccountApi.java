package com.citc.nce.authcenter.csp.customer.account;

import com.citc.nce.authcenter.csp.customer.account.vo.Query5GAccountListReq;
import com.citc.nce.authcenter.csp.customer.account.vo.Query5GAccountListResp;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

/**
 * @author jiancheng
 */
@FeignClient(value = "authcenter-service", contextId = "customerAccount", url = "${authCenter:}")
public interface CustomerAccountApi {

    /**
     * 获取用户的所有5G账号信息
     * @param req 请求信息
     * @return 响应结果
     */
    @PostMapping("/customer/account/query5GAccountList")
    Query5GAccountListResp query5GAccountList(@RequestBody Query5GAccountListReq req);


}

