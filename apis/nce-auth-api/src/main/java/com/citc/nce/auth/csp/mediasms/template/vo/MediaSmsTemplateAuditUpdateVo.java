package com.citc.nce.auth.csp.mediasms.template.vo;

import com.citc.nce.auth.csp.mediasms.template.enums.AuditStatus;
import com.citc.nce.auth.csp.mediasms.template.enums.OperatorPlatform;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author jiancheng
 */
@Data
public class MediaSmsTemplateAuditUpdateVo {
    @ApiModelProperty("平台模板ID")
    @NotNull(message = "平台模板ID不能为空")
    private String platformTemplateId;

    @ApiModelProperty("要修改的运营商的审核状态")
    private List<TemplateAudit> audits;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TemplateAudit {
        @ApiModelProperty("运营商")
        private OperatorPlatform operator;

        @ApiModelProperty("审核状态")
        private AuditStatus status;

        @ApiModelProperty("原因")
        private String reason;
    }
}
