package com.citc.nce.auth.csp.smsTemplate.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author ping chen
 */
@Data
public class SmsTemplateDetailVo {
    @ApiModelProperty("模板ID")
    private Long id;

    @ApiModelProperty("模板名称")
    private String templateName;

    @ApiModelProperty("平台模板ID")
    private String platformTemplateId;

    @ApiModelProperty("所属短信账号")
    private String accountId;

    @ApiModelProperty("账号删除状态,0:未删除 1:已删除")
    private Integer accountDelete;

    @ApiModelProperty("关联签名删除状态,0:未删除 1:已删除")
    private Integer signatureDelete;

    @ApiModelProperty("关联签名ID")
    private Long signatureId;

    @ApiModelProperty("关联签名内容")
    private String signatureContent;

    @ApiModelProperty("内容总计字节数(单位:字节)")
    private long contentTotalSize;

    @ApiModelProperty("模板内容")
    private String content;

    @ApiModelProperty("模板类型(普通模板:1,个性模板:2)")
    private Integer templateType;

    @ApiModelProperty("审核状态 0:待审核 1:审核中2:审核通过 3:审核拒绝")
    private Integer audit;

    @ApiModelProperty("删除时间")
    private LocalDateTime deletedTime;

    @ApiModelProperty("用户Id")
    private String customerId;

}
