package com.citc.nce.auth.csp.mediasms.template.vo;

import com.citc.nce.auth.csp.mediasms.template.enums.AuditStatus;
import com.citc.nce.auth.csp.mediasms.template.enums.OperatorPlatform;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author jiancheng
 */
@Data
public class MediaSmsTemplateSearchVo {
    @ApiModelProperty("模板名称，模糊查找")
    private String templateName;

    @ApiModelProperty("账号ID")
    private String accountId;

    @ApiModelProperty("运营商,移动 CMCC,联通 CUCC 电信 CTCC")
    private OperatorPlatform operator;

    @ApiModelProperty("审核状态 0等待送审 1审核中 2审核通过 3审核被拒")
    private AuditStatus auditStatus;

    @ApiModelProperty("模板类型(普通模板:1,个性模板:2)")
    private Integer templateType;

    @NotNull
    private Long pageNo;

    @NotNull
    private Long pageSize;

}
