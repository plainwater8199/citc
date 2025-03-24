package com.citc.nce.im.broadcast.utils;

import com.citc.nce.auth.accountmanagement.vo.AccountManagementResp;
import com.citc.nce.auth.csp.mediasms.template.vo.MediaSmsTemplatePreviewVo;
import com.citc.nce.auth.csp.recharge.Const.PaymentTypeEnum;
import com.citc.nce.auth.csp.sms.account.vo.CspSmsAccountDetailResp;
import com.citc.nce.auth.csp.smsTemplate.vo.SmsTemplateDetailVo;
import com.citc.nce.auth.csp.videoSms.account.vo.CspVideoSmsAccountDetailResp;
import com.citc.nce.auth.messagetemplate.vo.MessageTemplateResp;
import com.citc.nce.common.core.enums.MsgTypeEnum;
import com.citc.nce.robot.enums.MessageResourceType;
import com.citc.nce.common.redis.config.RedisService;
import com.citc.nce.im.session.ApplicationContextUil;
import com.citc.nce.msgenum.DeliveryEnum;
import com.citc.nce.msgenum.RequestEnum;
import com.citc.nce.robot.constant.MessagePaymentTypeConstant;
import com.citc.nce.tenant.vo.req.MsgRecordVo;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * @author jcrenc
 * @since 2024/5/17 9:06
 */
@Slf4j
public class MsgRecordFactory {
    public static List<MsgRecordVo> buildMsgRecords(MessageResourceType resourceType,
                                                    Long planId,
                                                    Long nodeId,
                                                    MsgTypeEnum msgType,
                                                    List<String> phoneList,
                                                    String messageId,
                                                    boolean success,
                                                    Object account,
                                                    Object template,
                                                    String conversationId,
                                                    String failedReason,
                                                    PaymentTypeEnum paymentType) {
        // 提前检查参数
        if (phoneList == null || phoneList.isEmpty()) {
            return Collections.emptyList();
        }

        List<MsgRecordVo> msgRecords = new ArrayList<>(phoneList.size());
        Date now = new Date(); // 重用时间戳
        DeliveryEnum sendResult = success ? DeliveryEnum.UN_KNOW : DeliveryEnum.FAILED;
        RequestEnum finalResult = success ? RequestEnum.UN_KNOW : RequestEnum.FAILED;
        long fallbackLong = 0L; // 避免装箱
        long actualPlanId = planId != null ? planId : fallbackLong;
        long actualNodeId = nodeId != null ? nodeId : fallbackLong;
        for (String phone : phoneList) {
            MsgRecordVo msgRecord = new MsgRecordVo();
            // 通用属性赋值
            msgRecord
                    .setMessageResource(resourceType.getCode())
                    .setPlanId(actualPlanId)
                    .setPlanDetailId(actualNodeId)
                    .setAccountType(msgType.getCode())
                    .setMessageId(messageId)
                    .setPhoneNum(phone)
                    .setSendResult(sendResult.getCode())
                    .setFinalResult(finalResult.getCode())
                    .setSendTime(now)
                    .setReceiptTime(now)
                    .setCreateTime(now)
                    .setUpdateTime(now)
                    .setConversationId(conversationId)
                    .setFailedReason(failedReason)
                    .setConsumeCategory(paymentType == null ? null : paymentType.getCode());
            // 特有属性赋值
            switch (msgType) {
                case M5G_MSG:
                    buildM5GMsgRecord(account, template, msgRecord);
                    break;
                case SHORT_MSG:
                    buildShortMsgRecord(account, template, msgRecord);
                    break;
                case MEDIA_MSG:
                    buildMediaMsgRecord(account, template, msgRecord);
                    break;
            }
            msgRecords.add(msgRecord);
        }
        if (Objects.nonNull(messageId)) {
            RedisService redisService = ApplicationContextUil.getBean(RedisService.class);
            redisService.deleteObject(MessagePaymentTypeConstant.redisPrefix + messageId);
        }
        return msgRecords;
    }

    // 分担特有属性赋值至单独的方法中
    private static void buildM5GMsgRecord(Object account, Object template, MsgRecordVo msgRecord) {
        AccountManagementResp chatbotAccount = (AccountManagementResp) account;
        MessageTemplateResp m5gTemplate = (MessageTemplateResp) template;
        msgRecord.setAccountId(chatbotAccount.getChatbotAccount())
                .setAccountName(chatbotAccount.getAccountName())
                .setOperatorCode(chatbotAccount.getAccountTypeCode())
                .setCallerAccount(chatbotAccount.getAccountType())
                .setCreator(chatbotAccount.getCustomerId())
                .setUpdater(chatbotAccount.getCustomerId())
                .setTemplateId((m5gTemplate.getId()))
                .setTemplateName(m5gTemplate.getTemplateName())
                .setButtonContent(m5gTemplate.getShortcutButton())
                .setMessageContent(m5gTemplate.getModuleInformation())
                .setMessageType(m5gTemplate.getMessageType());
    }

    private static void buildShortMsgRecord(Object account, Object template, MsgRecordVo msgRecord) {
        CspSmsAccountDetailResp smsAccount = (CspSmsAccountDetailResp) account;
        SmsTemplateDetailVo smsTemplate = (SmsTemplateDetailVo) template;
        msgRecord.setAccountId(smsAccount.getAccountId())
                .setAccountName(smsAccount.getAccountName())
                .setCallerAccount(smsAccount.getAccountName())
                .setAccountDictCode(smsAccount.getDictCode())
                .setCreator(smsAccount.getCustomerId())
                .setUpdater(smsAccount.getCustomerId())
                .setOperatorCode(0)
                .setTemplateId(smsTemplate.getId())
                .setTemplateName(smsTemplate.getTemplateName())
                .setSign(smsTemplate.getSignatureContent())
                .setMessageContent(smsTemplate.getContent());
    }

    private static void buildMediaMsgRecord(Object account, Object template, MsgRecordVo msgRecord) {
        CspVideoSmsAccountDetailResp mediaAccount = (CspVideoSmsAccountDetailResp) account;
        MediaSmsTemplatePreviewVo mediaTemplate = (MediaSmsTemplatePreviewVo) template;
        msgRecord.setAccountId(mediaAccount.getAccountId())
                .setAccountName(mediaAccount.getAccountName())
                .setAccountDictCode(mediaAccount.getDictCode())
                .setCallerAccount(mediaAccount.getAccountName())
                .setCreator(mediaAccount.getCustomerId())
                .setUpdater(mediaAccount.getCustomerId())
                .setOperatorCode(0)
                .setTemplateId(mediaTemplate.getTemplateId())
                .setTemplateName(mediaTemplate.getTemplateName())
                .setSign(mediaTemplate.getSignature())
                .setMessageContent(mediaTemplate.getTemplateContent());
    }
}
