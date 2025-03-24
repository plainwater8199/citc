package com.citc.nce.auth.csp.agent.service;

import com.citc.nce.auth.csp.agent.vo.AgentIdReq;
import com.citc.nce.auth.csp.agent.vo.ServiceCodeResp;

import java.util.List;

/**
 * @BelongsPackage: com.citc.nce.auth.csp.agent.service
 * @Author: litao
 * @CreateTime: 2023-02-17  14:33
 
 * @Version: 1.0
 */
public interface AgentService {
    List<ServiceCodeResp> queryServiceCodeList(AgentIdReq req);
}
