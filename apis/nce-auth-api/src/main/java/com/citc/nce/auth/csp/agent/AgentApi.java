package com.citc.nce.auth.csp.agent;

import com.citc.nce.auth.csp.agent.vo.AgentIdReq;
import com.citc.nce.auth.csp.agent.vo.ServiceCodeResp;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;
import java.util.List;

/**
 * @BelongsPackage: com.citc.nce.auth.csp.agent
 * @Author: litao
 * @CreateTime: 2023-02-17  14:26
 
 * @Version: 1.0
 */
@FeignClient(value = "auth-service", contextId = "CSPAgent", url = "${auth:}")
public interface AgentApi {
    @PostMapping("/csp/agent/queryServiceCodeList")
    List<ServiceCodeResp> queryServiceCodeList(@RequestBody @Valid AgentIdReq req);
}
