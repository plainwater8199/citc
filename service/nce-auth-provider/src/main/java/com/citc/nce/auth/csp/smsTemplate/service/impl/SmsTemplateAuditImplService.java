package com.citc.nce.auth.csp.smsTemplate.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.citc.nce.auth.csp.smsTemplate.dao.SmsTemplateAuditMapper;
import com.citc.nce.auth.csp.smsTemplate.entity.SmsTemplateAuditDo;
import com.citc.nce.auth.csp.smsTemplate.service.SmsTemplateAuditService;
import org.springframework.stereotype.Service;

/**
 * @author jiancheng
 */
@Service
public class SmsTemplateAuditImplService extends ServiceImpl<SmsTemplateAuditMapper, SmsTemplateAuditDo> implements SmsTemplateAuditService {
}
