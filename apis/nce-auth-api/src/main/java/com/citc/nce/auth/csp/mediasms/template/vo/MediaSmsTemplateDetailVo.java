package com.citc.nce.auth.csp.mediasms.template.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author jiancheng
 */
@Data
public class MediaSmsTemplateDetailVo {
    @ApiModelProperty("模板ID")
    private Long id;

    /*模板名称*/
    @ApiModelProperty("模板名称")
    private String templateName;

    /*主题名称*/
    private String topic;

    /*平台模板ID*/
    @ApiModelProperty("平台模板ID")
    private String platformTemplateId;

    /*所属视频短信账号*/
    @ApiModelProperty("所属视频短信账号")
    private String accountId;
    @ApiModelProperty("账号删除状态,0:未删除 1:已删除")
    private Integer accountDelete;

    @ApiModelProperty("关联签名删除状态,0:未删除 1:已删除")
    private Integer signatureDelete;

    /*关联签名ID*/
    @ApiModelProperty("关联签名ID")
    private Long signatureId;

    @ApiModelProperty("关联签名内容")
    private String signatureContent;

    @ApiModelProperty("内容总计字节数(单位:字节)")
    private long contentTotalSize;

    @ApiModelProperty("模板类型(普通模板:1,个性模板:2)")
    private Integer templateType;

    @ApiModelProperty("模板内容")
    private List<MediaSmsTemplateContentVo> contents;

    @ApiModelProperty("删除时间")
    private LocalDateTime deletedTime;

    private String customerId;

    private Integer audit;
}
