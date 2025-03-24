package com.citc.nce.auth.csp.mediasms.template.vo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author jiancheng
 */
@Data
public class MediaSmsTemplatePreviewVo {

    private Long templateId;
    @ApiModelProperty("签名")
    private String signature;

    /*模板名称*/
    private String templateName;

    /*主题名称*/
    private String topic;

    /*平台模板ID*/
    private String platformTemplateId;

    /*所属视频短信账号*/
    private String accountId;

    @ApiModelProperty("模板类型(普通模板:1,个性模板:2)")
    private Integer templateType;

    @ApiModelProperty("账号删除状态,0:未删除 1:已删除")
    private Integer accountDelete;

    @ApiModelProperty("关联签名删除状态,0:未删除 1:已删除")
    private Integer signatureDelete;

    @ApiModelProperty("内容")
    private List<MediaSmsTemplateContentVo> contents;

    /**
     * 模板内容快照，存msg_record表时使用
     */
    @JsonIgnore
    private String templateContent;
}
