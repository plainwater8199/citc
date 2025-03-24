package com.citc.nce.auth.csp.agent.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @BelongsPackage: com.citc.nce.auth.csp.contract.vo
 * @Author: litao
 * @CreateTime: 2023-02-17  14:23
 
 * @Version: 1.0
 */
@Data
public class AgentIdReq {
    @ApiModelProperty("服务代码")
    private Long agentInfoId;
    @ApiModelProperty("服务商编码")
    private Integer operatorCode;
}
