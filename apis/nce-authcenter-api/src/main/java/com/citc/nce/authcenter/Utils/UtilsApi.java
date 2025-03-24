package com.citc.nce.authcenter.Utils;


import io.swagger.annotations.Api;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Api(tags = "用户中心--校验工具信息")
@FeignClient(value = "authcenter-service", contextId = "UtilsApi", url = "${authCenter:}")
public interface UtilsApi {
    @PostMapping("/checkIPVisitCount")
    void checkIPVisitCount(@RequestParam("ip") String ip);
}
