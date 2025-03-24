package com.citc.nce.auth.csp.smsTemplate.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author ping chen
 */
@Data
public class SmsHaveTemplateAccountVo {
    @ApiModelProperty("id")
    private Long id;

    @ApiModelProperty("账户名称")
    private String accountName;

    @ApiModelProperty("账户id")
    private String accountId;

    @ApiModelProperty("状态 0:禁用 1:启用")
    private Integer status;
}
