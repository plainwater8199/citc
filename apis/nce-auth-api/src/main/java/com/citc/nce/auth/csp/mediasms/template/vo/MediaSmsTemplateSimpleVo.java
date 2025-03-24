package com.citc.nce.auth.csp.mediasms.template.vo;

import com.citc.nce.auth.csp.mediasms.template.enums.AuditStatus;
import com.citc.nce.auth.csp.mediasms.template.enums.OperatorPlatform;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author jiancheng
 */
@Data
public class MediaSmsTemplateSimpleVo {

    @ApiModelProperty("模版ID")
    private Long id;

    @ApiModelProperty("平台模版ID")
    private String platformTemplateId;

    @ApiModelProperty("模板名称")
    private String templateName;

    @ApiModelProperty("账号名称")
    private String accountName;

    @ApiModelProperty("账号是否已删除")
    private Boolean deleted;

    @ApiModelProperty("审核状态")
    private List<TemplateAuditVo> audits;

    @ApiModelProperty("创建时间")
    private LocalDateTime createTime;

    @ApiModelProperty("模板类型(普通模板:1,个性模板:2)")
    private Integer templateType;



    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class TemplateAuditVo {
        @ApiModelProperty("运营商")
        private OperatorPlatform operator;

        @ApiModelProperty("审核状态")
        private AuditStatus status;

        @ApiModelProperty("审核原因")
        private String reason;
    }

}
