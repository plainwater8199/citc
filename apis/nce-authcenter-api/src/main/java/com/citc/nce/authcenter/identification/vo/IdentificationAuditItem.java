package com.citc.nce.authcenter.identification.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

@Data
@Accessors(chain = true)
public class IdentificationAuditItem {
    @ApiModelProperty(value = "审核时间")
    private Date auditTime;
    @ApiModelProperty(value = "admin 端审核账号")
    private String auditAccount;
    @ApiModelProperty(value = "备注")
    private String auditRemark;
}
