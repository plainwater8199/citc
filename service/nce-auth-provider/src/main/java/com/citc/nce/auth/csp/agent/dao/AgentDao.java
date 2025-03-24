package com.citc.nce.auth.csp.agent.dao;

import com.citc.nce.auth.csp.account.vo.AgentResp;
import com.citc.nce.auth.csp.agent.entity.AgentDo;
import com.citc.nce.mybatis.core.mapper.BaseMapperX;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @BelongsPackage: com.citc.nce.auth.csp.agent.dao
 * @Author: litao
 * @CreateTime: 2023-02-14  16:06
 
 * @Version: 1.0
 */
@Mapper
public interface AgentDao extends BaseMapperX<AgentDo> {
    List<AgentResp> queryAgentList(String userId);
}
