package com.citc.nce.auth.csp.statistics.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @BelongsPackage: com.citc.nce.auth.csp.menu.vo
 * @Author: litao
 * @CreateTime: 2023-02-16  14:18
 
 * @Version: 1.0
 */
@Data
public class CspStatisticsChatbotResp {


    @ApiModelProperty("csp账号绑定总量")
    private Long totalCount;

    @ApiModelProperty(value = "移动")
    private Long cmccCount;

    @ApiModelProperty(value = "联通")
    private Long cuncSendCount;

    @ApiModelProperty(value = "电信")
    private Long ctSendCount;
}
