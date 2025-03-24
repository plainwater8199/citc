package com.citc.nce.misc.email.service.impl;

import cn.hutool.core.util.StrUtil;
import com.citc.nce.common.core.exception.BizException;
import com.citc.nce.misc.constant.MiscErrorCode;
import com.citc.nce.misc.email.configure.MailInfoConfigure;
import com.citc.nce.misc.email.req.EmailSendReq;
import com.citc.nce.misc.email.service.EmailService;
import com.citc.nce.misc.msg.entity.MsgTemplateDo;
import com.citc.nce.misc.msg.mapper.MsgTemplateMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;


/**
 * @Author: zouyili
 * @Contact: ylzouf
 * @Date: 2022/6/21 17:06
 * @Version: 1.0
 * @Description:
 */
@Service
@Slf4j
@RequiredArgsConstructor
@EnableConfigurationProperties(MailInfoConfigure.class)
public class EmailServiceImpl implements EmailService {

    @Resource
    JavaMailSender javaMailSender;

    private final MailInfoConfigure mailInfoConfigure;

    @Resource
    private MsgTemplateMapper msgTemplateMapper;


    @Override
    public void sendEmail(EmailSendReq emailDetailReq) {
        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "utf-8");
            helper.setFrom(new InternetAddress(mailInfoConfigure.getUsername(), "硬核桃"));
            MsgTemplateDo msgTemplateDo = msgTemplateMapper.selectOne(MsgTemplateDo::getTempldateCode, emailDetailReq.getTemplateCode());
            helper.setSubject(msgTemplateDo.getTempldateSubject());
            helper.setTo(emailDetailReq.getTargetMailAccounts());
            String text = msgTemplateDo.getTempldateContent();
            String finalText = StrUtil.format(text, emailDetailReq.getTemplateParam());
            if (text.contains("html")) {
                helper.setText(finalText, true);
            } else {
                helper.setText(finalText, false);
            }
            javaMailSender.send(message);
        } catch (Exception e) {
            log.error("send mail failed", e);
            throw BizException.build(MiscErrorCode.MAIL_SERVICE_ABNORMAL);
        }
    }
}
