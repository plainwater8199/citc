package com.citc.nce.authcenter.identification;


import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;


@FeignClient(value = "authcenter-service", contextId = "identification", url = "${authCenter:}", primary = false)
public interface IdentificationApi {
    @PostMapping("/worksheet/getCertificateListByUserId")
    List<String> getCertificateListByUserId(@RequestParam("userId") String userId);

}
