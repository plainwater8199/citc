package com.citc.nce.module;

import com.citc.nce.module.vo.req.SignModuleReq;
import com.citc.nce.common.core.pojo.PageResult;
import com.citc.nce.module.vo.req.SignModuleUpdateReq;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;
import java.util.List;

@FeignClient(value = "rebot-service", contextId = "SignModuleApi", url = "${robot:}")
public interface SignModuleApi {

    @PostMapping("/csp/sign/save")
    int saveSignModule(@RequestBody @Valid SignModuleReq req);

    @PostMapping("/csp/sign/delete")
    int deleteSignModule(@RequestBody SignModuleReq req);

    @PostMapping("/csp/sign/update")
    int updateSignModule(@RequestBody @Valid SignModuleUpdateReq req);

    @PostMapping("/csp/sign/getSignModuleList")
    PageResult<SignModuleReq> getSignModuleList(@RequestBody SignModuleReq req);

    @PostMapping("/csp/sign/getSignModules")
    List<SignModuleReq> getSignModules();

    @PostMapping("/csp/sign/getSignModule")
    SignModuleReq getSignModule(@RequestBody SignModuleReq req);

}
