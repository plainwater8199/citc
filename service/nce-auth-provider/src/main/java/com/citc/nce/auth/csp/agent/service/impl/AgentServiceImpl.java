package com.citc.nce.auth.csp.agent.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.citc.nce.auth.csp.agent.dao.AgentDao;
import com.citc.nce.auth.csp.agent.entity.AgentDo;
import com.citc.nce.auth.csp.agent.service.AgentService;
import com.citc.nce.auth.csp.agent.vo.AgentIdReq;
import com.citc.nce.auth.csp.agent.vo.ServiceCodeResp;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * @BelongsPackage: com.citc.nce.auth.csp.agent.service.impl
 * @Author: litao
 * @CreateTime: 2023-02-17  14:33
 
 * @Version: 1.0
 */
@Service
public class AgentServiceImpl implements AgentService {
    @Resource
    private AgentDao agentDao;

    @Override
    public List<ServiceCodeResp> queryServiceCodeList(AgentIdReq req) {

        AgentDo agentDo = agentDao.selectOne("id", req.getAgentInfoId());
        QueryWrapper<AgentDo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("agent_code", agentDo.getAgentCode());
        queryWrapper.eq("agent_name", agentDo.getAgentName());
        queryWrapper.eq("operator_account_id", agentDo.getOperatorAccountId());
        List<AgentDo> list = agentDao.selectList(queryWrapper);
        List<ServiceCodeResp> serviceCodeRespList = new ArrayList<>();
        list.forEach(item -> {
            ServiceCodeResp resp = new ServiceCodeResp();
            resp.setServiceCode(item.getServiceCode());
            serviceCodeRespList.add(resp);
        });
        return serviceCodeRespList;
    }
}
