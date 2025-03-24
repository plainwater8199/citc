package com.citc.nce.auth.msgTemplate.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.citc.nce.auth.constant.AuthError;
import com.citc.nce.auth.csp.mediasms.template.dao.MediaSmsTemplateMapper;
import com.citc.nce.auth.csp.mediasms.template.entity.MediaSmsTemplateDo;
import com.citc.nce.auth.csp.smsTemplate.dao.SmsTemplateMapper;
import com.citc.nce.auth.csp.smsTemplate.entity.SmsTemplateDo;
import com.citc.nce.auth.messagetemplate.dao.MessageTemplateDao;
import com.citc.nce.auth.messagetemplate.entity.MessageTemplateDo;
import com.citc.nce.auth.msgTemplate.service.MsgTemplateService;
import com.citc.nce.common.core.enums.MsgTypeEnum;
import com.citc.nce.common.core.exception.BizException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
@Slf4j
public class MsgTemplateServiceImpl implements MsgTemplateService {
    @Resource
    private MessageTemplateDao messageTemplateDao;
    @Resource
    private SmsTemplateMapper smsTemplateMapper;
    @Resource
    private MediaSmsTemplateMapper mediaSmsTemplateMapper;

    @Override
    public String templateContentQuery(MsgTypeEnum msgType, Long templateId, String customerId) {
        log.debug("templateContentQuery msgType={}, templateId={}, customerId={}", msgType, templateId, customerId);
        if (msgType != null && templateId != null) {
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                if (MsgTypeEnum.M5G_MSG.equals(msgType)) {//5G消息模版
                    LambdaQueryWrapper<MessageTemplateDo> queryWrapper = new LambdaQueryWrapper<>();
                    queryWrapper.eq(MessageTemplateDo::getId, templateId);
                    queryWrapper.eq(MessageTemplateDo::getCreator, customerId);
                    MessageTemplateDo messageTemplateDo = messageTemplateDao.selectOne(queryWrapper);
                    if (messageTemplateDo != null) {
                        return objectMapper.writeValueAsString(messageTemplateDo);
                    } else {
                        throw new BizException(AuthError.MESSAGE_TEMPLATE_NOT_EXIST);
                    }
                } else if (MsgTypeEnum.SHORT_MSG.equals(msgType)) {//短信模版
                    LambdaQueryWrapper<SmsTemplateDo> queryWrapper = new LambdaQueryWrapper<>();
                    queryWrapper.eq(SmsTemplateDo::getId, templateId);
                    queryWrapper.eq(SmsTemplateDo::getCreator, customerId);
                    SmsTemplateDo smsTemplateDo = smsTemplateMapper.selectOne(queryWrapper);
                    if (smsTemplateDo != null) {
                        return objectMapper.writeValueAsString(smsTemplateDo);
                    } else {
                        throw new BizException(AuthError.MESSAGE_TEMPLATE_NOT_EXIST);
                    }
                } else {
                    LambdaQueryWrapper<MediaSmsTemplateDo> queryWrapper = new LambdaQueryWrapper<>();
                    queryWrapper.eq(MediaSmsTemplateDo::getId, templateId);
                    queryWrapper.eq(MediaSmsTemplateDo::getCreator, customerId);
                    MediaSmsTemplateDo mediaSmsTemplateDo = mediaSmsTemplateMapper.selectOne(queryWrapper);
                    if (mediaSmsTemplateDo != null) {
                        return objectMapper.writeValueAsString(mediaSmsTemplateDo);
                    } else {
                        throw new BizException(AuthError.MESSAGE_TEMPLATE_NOT_EXIST);
                    }
                }
            } catch (Exception e) {
                throw new BizException(AuthError.MESSAGE_TEMPLATE_NOT_EXIST);
            }
        } else {
            throw new BizException(AuthError.MESSAGE_TEMPLATE_NOT_EXIST);
        }
    }
}
