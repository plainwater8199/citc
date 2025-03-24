package com.citc.nce.auth.csp.sms.account;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(value = "auth-service", contextId = "CspShortSmsAccountApi", url = "${auth:}")
public interface CspShortSmsAccountApi {
    @PostMapping("/csp/short/account/queryAccountIdListByCspIds")
    List<String> queryAccountIdListByCspIds(@RequestBody List<String> cspIds);
    @PostMapping("/csp/short/account/queryAccountIdListByCustomerIds")
    List<String> queryAccountIdListByCustomerIds(@RequestBody List<String> customerIds);
}
