package com.citc.nce.auth.csp.smsTemplate.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author ping chen
 */
@Data
public class SmsTemplateAuditUpdateVo {
    @ApiModelProperty("平台模板ID")
    private String platformTemplateId;

    @ApiModelProperty("要修改的运营商的审核状态")
    private Integer audits;
}
