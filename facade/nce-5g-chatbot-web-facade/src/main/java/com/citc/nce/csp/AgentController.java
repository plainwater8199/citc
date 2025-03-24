package com.citc.nce.csp;

import com.citc.nce.auth.csp.agent.AgentApi;
import com.citc.nce.auth.csp.agent.vo.AgentIdReq;
import com.citc.nce.auth.csp.agent.vo.ServiceCodeResp;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;

/**
 * @BelongsPackage: com.citc.nce.csp
 * @Author: litao
 * @CreateTime: 2023-02-17  14:45
 
 * @Version: 1.0
 */
@RestController
@RequestMapping("/csp")
@Api(value = "AgentInfoController",tags = "CSP--代理商管理")
public class AgentController {
    @Resource
    private AgentApi agentApi;

    @PostMapping("/agent/queryServiceCodeList")
    @ApiOperation(value = "列表查询", notes = "列表查询")
    public List<ServiceCodeResp> queryServiceCodeList(@RequestBody @Valid AgentIdReq req){
        return agentApi.queryServiceCodeList(req);
    }
}
