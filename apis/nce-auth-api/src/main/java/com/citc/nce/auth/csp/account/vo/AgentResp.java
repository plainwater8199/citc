package com.citc.nce.auth.csp.account.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @BelongsPackage: com.citc.nce.auth.csp.account.vo
 * @Author: litao
 * @CreateTime: 2023-02-14  17:28

 * @Version: 1.0
 */
@Data
public class AgentResp {
    @ApiModelProperty("代理商id")
    private Long id;

    @ApiModelProperty("代理商名称")
    private String agentName;

    @ApiModelProperty("代理商编码")
    private String agentCode;

    @ApiModelProperty("服务代码")
    private String serviceCode;

}
