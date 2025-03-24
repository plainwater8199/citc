package com.citc.nce.authcenter.tenantdata.user.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.citc.nce.mybatis.core.entity.BaseDo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @BelongsPackage: com.citc.nce.auth.csp.agent.entity
 * @Author: litao
 * @CreateTime: 2023-02-14  16:04
 
 * @Version: 1.0
 */
@Data
@TableName("chatbot_csp_agent_info")
public class AgentDo extends BaseDo<AgentDo> implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty("账号")
    private Long cspAccountManageId;   // TODO 废弃

    private String operatorAccountId;

    @ApiModelProperty("代理商名称")
    private String agentName;

    @ApiModelProperty("代理商编码")
    private String agentCode;

    @ApiModelProperty("服务代码")
    private String serviceCode;

    private String creatorOld;

    private String updaterOld;
}
