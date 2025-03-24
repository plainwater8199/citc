package com.citc.nce.auth.csp.csp;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(value = "auth-service", contextId = "Csp", url = "${auth:}")
public interface CspApi {

    @PostMapping("/csp/queryCspId")
    String obtainCspId(@RequestBody String userId);
}
