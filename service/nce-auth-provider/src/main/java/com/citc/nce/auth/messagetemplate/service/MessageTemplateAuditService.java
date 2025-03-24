package com.citc.nce.auth.messagetemplate.service;

import com.citc.nce.auth.accountmanagement.vo.AccountManagementResp;
import com.citc.nce.auth.messagetemplate.entity.MessageTemplateDo;
import com.citc.nce.auth.messagetemplate.entity.TemplateDataResp;
import com.citc.nce.filecenter.vo.TemplateOwnershipReflect;

/**
 * @author yy
 * @date 2024-03-15 16:39:56
 */
public interface MessageTemplateAuditService {
    TemplateDataResp applyCreateTemplate(MessageTemplateDo messageTemplateDo, TemplateOwnershipReflect templateOwnershipReflect);
}

