package com.citc.nce.auth.csp.mediasms.template.entity;

import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.citc.nce.auth.csp.mediasms.template.enums.AuditStatus;
import com.citc.nce.auth.csp.mediasms.template.enums.OperatorPlatform;
import com.citc.nce.mybatis.core.entity.BaseDo;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * @author jiancheng
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("csp_video_sms_template_audit")
@Accessors(chain = true)
public class MediaSmsTemplateAuditDo extends BaseDo<MediaSmsTemplateAuditDo> {
    private static final long serialVersionUID = 996648017439631658L;

    /*关联视频模板ID*/
    private Long mediaTemplateId;

    /*平台模板ID*/
    private String platformTemplateId;

    /*运营商,移动 CMCC,联通 CUCC 电信 CTCC*/
    private OperatorPlatform operator;

    /*审核状态，0:待审核 1:审核通过 2:审核拒绝*/
    private AuditStatus status;

    /*原因*/
    private String reason;

    @TableLogic(value = "null", delval = "now()")
    private LocalDateTime deletedTime;
}
