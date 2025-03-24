package com.citc.nce.authcenter.userDataSyn;


import io.swagger.annotations.Api;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

@Api(tags = "用户--社区用户同步模块")
@FeignClient(value = "authcenter-service", contextId = "TenantDataSynApi", url = "${authCenter:}")
public interface TenantDataSynApi {
    @PostMapping(value = "/tenant/userDataSyn")
    void dataSyn();
}
