package com.citc.nce.auth.csp.smsTemplate.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author ping chen
 */
@Data
public class SmsTemplateSimpleVo {

    @ApiModelProperty("模版ID")
    private Long id;

    @ApiModelProperty("平台模版ID")
    private String platformTemplateId;

    @ApiModelProperty("模板名称")
    private String templateName;

    @ApiModelProperty("模板类型")
    private Integer templateType;

    @ApiModelProperty("账号名称")
    private String accountName;

    @ApiModelProperty("账号是否已删除 0:未删除 1:已删除")
    private Boolean deleted;

    @ApiModelProperty("账号状态 0:禁用 1:启用")
    private Boolean accountStatus;

    @ApiModelProperty("审核状态 0:待审核 1:审核中2:审核通过 3:审核拒绝")
    private Integer audit;

    @ApiModelProperty("创建时间")
    private LocalDateTime createTime;

    @ApiModelProperty("审批时间")
    private LocalDateTime approveTime;

    @ApiModelProperty("模板内容")
    private String content;

    @ApiModelProperty("签名Id")
    private Long signatureId;

    @ApiModelProperty("签名内容")
    private String signature;
}
