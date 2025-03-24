package com.citc.nce.auth.csp.smsTemplate.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * @author ping chen
 */
@Data
public class SmsTemplateUpdateVo {

    @NotNull(message = "模板ID不能为空")
    @ApiModelProperty("模板ID")
    private Long id;

    @NotBlank(message = "模板名称不能为空")
    @ApiModelProperty("模板名称")
    private String templateName;

    @NotNull(message = "视频账号ID不能为空")
    @ApiModelProperty("视频账号ID")
    private String accountId;

    @ApiModelProperty("签名ID")
    @NotNull(message = "签名ID不能为空")
    private Long signatureId;


    @ApiModelProperty("模板内容")
    @NotNull(message = "模板内容不能为空")
    private String content;

    @ApiModelProperty("是否送审")
    private Boolean submitForAudit = false;

    @ApiModelProperty("模板类型(普通模板:1,个性模板:2)")
    @NotNull(message = "模板类型不能为空")
    private Integer templateType;

    @ApiModelProperty("短链Id")
    private Long shortUrlId;

}
