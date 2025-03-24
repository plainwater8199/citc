package com.citc.nce.auth.csp.account;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(value = "auth-service", contextId = "Csp5GSmsAccountApi", url = "${auth:}")
public interface Csp5GSmsAccountApi {
    @PostMapping("/csp/5G/account/queryAccountIdListByCspIds")
    List<String> queryAccountIdListByCspIds(@RequestBody List<String> userIds);
    @PostMapping("/csp/5G/account/queryAccountIdListByCustomerIds")
    List<String> queryAccountIdListByCustomerIds(@RequestBody List<String> customerIds);
}
