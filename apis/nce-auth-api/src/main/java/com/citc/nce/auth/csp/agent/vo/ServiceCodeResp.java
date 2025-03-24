package com.citc.nce.auth.csp.agent.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @BelongsPackage: com.citc.nce.auth.csp.contract.vo
 * @Author: litao
 * @CreateTime: 2023-02-17  14:20

 * @Version: 1.0
 */
@Data
public class ServiceCodeResp {
    @ApiModelProperty("服务代码")
    private String serviceCode;
}
