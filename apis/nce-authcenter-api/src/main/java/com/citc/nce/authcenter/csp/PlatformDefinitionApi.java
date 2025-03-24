package com.citc.nce.authcenter.csp;

import com.citc.nce.authcenter.csp.vo.resp.PlatformDefinition;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.Valid;

/**
 * bydud
 * 2024/1/25
 **/
@FeignClient(value = "authcenter-service", contextId = "platformDefinitionApi", url = "${authCenter:}")
public interface PlatformDefinitionApi {


    @GetMapping("/csp/platformDefinition/get")
    PlatformDefinition platformDefinition(@RequestParam("cspId") String cspId);

    @PostMapping("/csp/platformDefinition/update")
    void updatePlatformDefinition(@RequestBody @Valid PlatformDefinition platformDefinition);
}
