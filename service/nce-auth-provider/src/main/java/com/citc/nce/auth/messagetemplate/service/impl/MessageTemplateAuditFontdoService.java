package com.citc.nce.auth.messagetemplate.service.impl;

import com.citc.nce.auth.accountmanagement.AccountManagementApi;
import com.citc.nce.auth.accountmanagement.vo.AccountManagementResp;
import com.citc.nce.auth.accountmanagement.vo.AccountManagementTypeReq;
import com.citc.nce.auth.messagetemplate.entity.MessageTemplateDo;
import com.citc.nce.auth.messagetemplate.entity.TemplateDataResp;
import com.citc.nce.auth.messagetemplate.service.MessageTemplateAuditService;
import com.citc.nce.auth.messagetemplate.service.TemplateContentMakeUp;
import com.citc.nce.common.constants.Constants;
import com.citc.nce.common.redis.config.RedisService;
import com.citc.nce.filecenter.vo.TemplateOwnershipReflect;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

/**
 * @author yy
 * @date 2024-03-14 19:55:37
 */
@Component
@Slf4j
public class MessageTemplateAuditFontdoService implements MessageTemplateAuditService {
    @Resource
    PlatfomManageTemplateService platfomManageTemplateService;
    @Resource
    AccountManagementApi accountManagementApi;
    @Resource
    TemplateContentMakeUp templateContentMakeUp;

    @Override
    public TemplateDataResp applyCreateTemplate(MessageTemplateDo messageTemplateDo, TemplateOwnershipReflect templateOwnershipReflect) {
        try {
            AccountManagementTypeReq accountManagementTypeReq=new AccountManagementTypeReq();
            accountManagementTypeReq.setAccountType(templateOwnershipReflect.getOperator());
            accountManagementTypeReq.setCreator(messageTemplateDo.getCreator());
            AccountManagementResp accountManagementResp=accountManagementApi.getAccountManagementByAccountType(accountManagementTypeReq);
            return platfomManageTemplateService.createTemplate(templateContentMakeUp.buildMessageParams(messageTemplateDo, templateOwnershipReflect), accountManagementResp);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("向网关请求创建模板是出错：");
            log.error(e.getMessage());
            TemplateDataResp templateDataResp = new TemplateDataResp();
            templateDataResp.setCode(500);
            templateDataResp.setMessage(e.getMessage());
            return  templateDataResp;
        }
    }
}
