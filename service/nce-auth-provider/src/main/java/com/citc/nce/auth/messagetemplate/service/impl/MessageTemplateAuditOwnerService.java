package com.citc.nce.auth.messagetemplate.service.impl;

import com.citc.nce.auth.accountmanagement.vo.AccountManagementResp;
import com.citc.nce.auth.messagetemplate.entity.MessageTemplateDo;
import com.citc.nce.auth.messagetemplate.entity.TemplateDataResp;
import com.citc.nce.auth.messagetemplate.service.MessageTemplateAuditService;
import com.citc.nce.filecenter.vo.TemplateOwnershipReflect;
import org.springframework.stereotype.Component;

/**
 * @author yy
 * @date 2024-03-14 19:55:37
 */
@Component
public class MessageTemplateAuditOwnerService implements MessageTemplateAuditService {
    /**
     * 运营商的模板不需要去创建，只有服务商才需要去创建模板（如蜂动）
     * @param messageTemplateDo
     * @param templateOwnershipReflect
     * @return
     */
    @Override
    public TemplateDataResp applyCreateTemplate(MessageTemplateDo messageTemplateDo, TemplateOwnershipReflect templateOwnershipReflect) {
        TemplateDataResp<String> templateDataResp = new TemplateDataResp<>();
        templateDataResp.setCode(200);
        templateDataResp.setData(Long.toString(messageTemplateDo.getId()));
        return templateDataResp;
    }
}
