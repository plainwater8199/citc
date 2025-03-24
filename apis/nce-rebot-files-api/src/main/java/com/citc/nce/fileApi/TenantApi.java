package com.citc.nce.fileApi;

import com.citc.nce.vo.tenant.req.TenantUpdateInfoReq;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;


@FeignClient(contextId = "TenantApi",value = "robot-files-service", url = "${robotFile:}")
public interface TenantApi {
    @PostMapping("/tenant/file/dataSyn")
    void updateData(@RequestBody TenantUpdateInfoReq req);
    @PostMapping("/tenant/file/updateChatbot")
    void updateChatbot(@RequestBody TenantUpdateInfoReq req);
}
