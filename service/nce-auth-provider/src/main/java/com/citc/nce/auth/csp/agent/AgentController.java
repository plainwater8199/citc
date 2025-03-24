package com.citc.nce.auth.csp.agent;

import com.citc.nce.auth.csp.agent.service.AgentService;
import com.citc.nce.auth.csp.agent.vo.AgentIdReq;
import com.citc.nce.auth.csp.agent.vo.ServiceCodeResp;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;

/**
 * @BelongsPackage: com.citc.nce.auth.csp.agent
 * @Author: litao
 * @CreateTime: 2023-02-17  14:32
 
 * @Version: 1.0
 */
@RestController
@Slf4j
public class AgentController implements AgentApi{
    @Resource
    private AgentService agentService;

    @Override
    public List<ServiceCodeResp> queryServiceCodeList(@RequestBody @Valid AgentIdReq req) {
        return agentService.queryServiceCodeList(req);
    }
}
