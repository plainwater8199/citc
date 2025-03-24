package com.citc.nce.auth.csp.mediasms.template.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author jiancheng
 */
@Data
public class MediaSmsTemplateUpdateVo {

    @NotNull(message = "模板ID不能为空")
    @ApiModelProperty("模板ID")
    private Long id;

    @NotBlank(message = "模板名称不能为空")
    @ApiModelProperty("模板名称")
    private String templateName;

    /*主题名称*/
    @NotBlank(message = "主题名称不能为空")
    @ApiModelProperty("主题名称")
    private String topic;

    @NotNull(message = "视频账号ID不能为空")
    @ApiModelProperty("视频账号ID")
    private String accountId;

    @NotNull(message = "签名ID不能为空")
    @ApiModelProperty("签名ID")
    private Long signatureId;

    @ApiModelProperty("模板内容")
    @NotEmpty(message = "至少添加一个媒体资源")
    @Valid
    private List<MediaSmsTemplateContentAddVo> contents;

    @ApiModelProperty("是否送审")
    private Boolean submitForAudit = false;

    @ApiModelProperty("模板类型(普通模板:1,个性模板:2)")
    @NotNull(message = "模板类型不能为空")
    private Integer templateType;

}
