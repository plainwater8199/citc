package com.citc.nce.authcenter.tenantdata.user.entity;

import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.citc.nce.auth.csp.smsTemplate.enums.SmsAuditStatus;
import com.citc.nce.mybatis.core.entity.BaseDo;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;
import java.util.Date;

/**
 * @author ping chen
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("csp_sms_template_audit")
@Accessors(chain = true)
public class SmsTemplateAuditDo extends BaseDo<SmsTemplateAuditDo> {
    private static final long serialVersionUID = 996648017439631622L;

    /**
     * 关联视频模板ID
     */
    private Long smsTemplateId;

    /**
     * 平台模板ID
     */
    private String platformTemplateId;

    /**
     * 审核状态，0:待审核 1:审核中 2:审核通过 3:审核拒绝
     */
    private SmsAuditStatus status;

    /**
     * 原因
     */
    private String reason;

    private Date deletedTime;


    private String creatorOld;

    private String updaterOld;
}
