package com.citc.nce.im.broadcast.client;

import com.alibaba.fastjson.JSONObject;
import com.citc.nce.auth.csp.recharge.Const.AccountTypeEnum;
import com.citc.nce.auth.csp.recharge.Const.PaymentTypeEnum;
import com.citc.nce.auth.csp.recharge.Const.TariffTypeEnum;
import com.citc.nce.auth.csp.recharge.RechargeTariffApi;
import com.citc.nce.auth.csp.recharge.vo.RechargeTariffDetailResp;
import com.citc.nce.auth.csp.sms.account.CspSmsAccountApi;
import com.citc.nce.auth.csp.sms.account.vo.CspSmsAccountDetailResp;
import com.citc.nce.auth.csp.smsTemplate.SmsTemplateApi;
import com.citc.nce.auth.csp.smsTemplate.vo.SmsTemplateContentVo;
import com.citc.nce.auth.csp.smsTemplate.vo.SmsTemplateDataVo;
import com.citc.nce.auth.csp.smsTemplate.vo.SmsTemplateDetailVo;
import com.citc.nce.auth.prepayment.DeductionAndRefundApi;
import com.citc.nce.auth.prepayment.PrepaymentApi;
import com.citc.nce.auth.prepayment.vo.FeeDeductReq;
import com.citc.nce.auth.prepayment.vo.ReturnBalanceBatchReq;
import com.citc.nce.authcenter.constant.AuthCenterError;
import com.citc.nce.authcenter.csp.CspCustomerApi;
import com.citc.nce.authcenter.csp.vo.UserInfoVo;
import com.citc.nce.common.core.enums.CustomerPayType;
import com.citc.nce.common.core.enums.MsgTypeEnum;
import com.citc.nce.im.broadcast.utils.BroadcastPlanUtils;
import com.citc.nce.robot.api.FastGroupMessageApi;
import com.citc.nce.robot.enums.MessageResourceType;
import com.citc.nce.common.constants.Constants;
import com.citc.nce.common.core.enums.MsgTypeEnum;
import com.citc.nce.common.core.exception.BizException;
import com.citc.nce.common.redis.config.RedisService;
import com.citc.nce.common.util.UUIDUtils;
import com.citc.nce.im.shortMsg.ShortMsgPlatformService;
import com.citc.nce.robot.constant.MessagePaymentTypeConstant;
import com.citc.nce.robot.vo.SmsMessageResponse;
import com.citc.nce.robot.vo.SmsResponse;
import com.citc.nce.robot.vo.SmsTemplateSendNormalReq;
import com.citc.nce.robot.vo.SmsTemplateSendVariableReq;
import com.citc.nce.robot.vo.TemplateSmsIdAndMobile;
import com.citc.nce.tenant.MsgRecordApi;
import com.citc.nce.tenant.vo.req.MsgIdMappingVo;
import com.citc.nce.tenant.vo.req.MsgRecordVo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.shardingsphere.transaction.annotation.ShardingSphereTransactionType;
import org.apache.shardingsphere.transaction.core.TransactionType;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.citc.nce.common.core.enums.CustomerPayType.POSTPAY;
import static com.citc.nce.common.core.enums.CustomerPayType.PREPAY;
import static com.citc.nce.im.broadcast.utils.MsgRecordFactory.buildMsgRecords;

/**
 * @author jcrenc
 * @since 2024/5/8 15:12
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class SmsSendClient {
    private final ShortMsgPlatformService smsPlatformService;
    private final MsgRecordApi msgRecordApi;
    private final PrepaymentApi prepaymentApi;
    private final CspCustomerApi cspCustomerApi;
    private final CspSmsAccountApi smsAccountApi;
    private final SmsTemplateApi smsTemplateApi;
    private final DeductionAndRefundApi deductionAndRefundApi;
    private final RedisTemplate<Object, Object> redisTemplate;
    private final RedisService redisService;
    private final RechargeTariffApi rechargeTariffApi;

    @Resource
    private FastGroupMessageApi fastGroupMessageApi;

    @Transactional(rollbackFor = Exception.class)
    @ShardingSphereTransactionType(TransactionType.BASE)
    public SmsMessageResponse deductAndSend(String accountId,
                                            Long templateId,
                                            String variableStr,
                                            Long planId,
                                            Long nodeId,
                                            List<TemplateSmsIdAndMobile> mobiles,
                                            MessageResourceType resourceType,
                                            Integer paymentType) {
        CspSmsAccountDetailResp account = smsAccountApi.queryDetailInner(accountId);
        SmsTemplateDetailVo template = smsTemplateApi.getTemplateInfoInner(templateId, false);
        UserInfoVo userInfo = cspCustomerApi.getByCustomerId(account.getCustomerId());
        List<String> sendPhones = mobiles.stream().map(TemplateSmsIdAndMobile::getMobile).collect(Collectors.toList());
        //最终是扣费还是扣套餐来记录
        PaymentTypeEnum finalPaymentType;
        //平台扣费messageId
        String messageId = UUIDUtils.generateUUID();
        // 统计短信长度，根据长度进行计费
        Integer smsSendNum = BroadcastPlanUtils.getContentLength(template);
        if (userInfo.getPayType() == PREPAY) {
            //套餐
            if (PaymentTypeEnum.SET_MEAL.getCode().equals(paymentType)) {
                prepaymentApi.deductRemaining(account.getAccountId(), MsgTypeEnum.SHORT_MSG, null, (long) mobiles.size(), (long) smsSendNum);
                finalPaymentType = PaymentTypeEnum.SET_MEAL;
            }
            //余额
            else if (PaymentTypeEnum.BALANCE.getCode().equals(paymentType)) {
                //没有设置资费没法发送
                checkTariffSet(account);
                FeeDeductReq feeDeductReq = FeeDeductReq.builder()
                        .accountType(AccountTypeEnum.SMS.getCode())
                        .tariffType(TariffTypeEnum.SMS.getCode())
                        .payType(PREPAY.getCode())
                        .accountId(accountId)
                        .messageId(messageId)
                        .customerId(account.getCustomerId())
                        .phoneNumbers(sendPhones)
                        .chargeNum(smsSendNum)
                        .build();
                deductionAndRefundApi.deductFee(feeDeductReq);
                finalPaymentType = PaymentTypeEnum.BALANCE;
            }
            //默认先扣套餐再尝试余额
            else {
                try {
                    prepaymentApi.deductRemaining(account.getAccountId(), MsgTypeEnum.SHORT_MSG, null, (long) mobiles.size(), (long) smsSendNum);
                    finalPaymentType = PaymentTypeEnum.SET_MEAL;
                } catch (Exception e) {
                    log.info("先扣除套餐失败");
                    //没有设置资费没法发送
                    checkTariffSet(account);
                    FeeDeductReq feeDeductReq = FeeDeductReq.builder()
                            .accountType(AccountTypeEnum.SMS.getCode())
                            .tariffType(TariffTypeEnum.SMS.getCode())
                            .payType(PREPAY.getCode())
                            .accountId(accountId)
                            .messageId(messageId)
                            .customerId(account.getCustomerId())
                            .phoneNumbers(sendPhones)
                            .chargeNum(smsSendNum)
                            .build();
                    deductionAndRefundApi.deductFee(feeDeductReq);
                    finalPaymentType = PaymentTypeEnum.BALANCE;
                }
            }
        } else {
            checkTariffSet(account);
            //后付费
            FeeDeductReq feeDeductReq = FeeDeductReq.builder()
                    .accountType(AccountTypeEnum.SMS.getCode())
                    .tariffType(TariffTypeEnum.SMS.getCode())
                    .payType(POSTPAY.getCode())
                    .accountId(accountId)
                    .messageId(messageId)
                    .customerId(account.getCustomerId())
                    .phoneNumbers(sendPhones)
                    .chargeNum(smsSendNum)
                    .build();
            deductionAndRefundApi.deductFee(feeDeductReq);
            finalPaymentType = PaymentTypeEnum.BALANCE;

        }
        SmsMessageResponse response = send(account, template, variableStr, planId, nodeId, messageId, mobiles, resourceType, finalPaymentType);
        if (!response.isSuccess()) {
            if (finalPaymentType == PaymentTypeEnum.SET_MEAL) {
                prepaymentApi.returnRemaining(account.getAccountId(), MsgTypeEnum.SHORT_MSG, null, (long) mobiles.size() * smsSendNum);
            } else {
                ReturnBalanceBatchReq feeDeductReq = ReturnBalanceBatchReq.builder()
                        .tariffType(TariffTypeEnum.SMS.getCode())
                        .messageId(messageId)
                        .customerId(account.getCustomerId())
                        .phoneNumbers(sendPhones)
                        .build();
                deductionAndRefundApi.returnBalanceBatch(feeDeductReq);
            }
        }
        return response;
    }

    private void checkTariffSet(CspSmsAccountDetailResp account) {
        //没有设置资费没法发送
        RechargeTariffDetailResp rechargeTariff = rechargeTariffApi.getRechargeTariff(account.getAccountId());
        if (rechargeTariff == null) {
            throw new BizException(String.format(AuthCenterError.Tarrif_Not_config.getMsg(), account.getAccountName()));
        }
    }

    /**
     * 发送短信
     */
    @Transactional(rollbackFor = Exception.class)
    public SmsMessageResponse send(CspSmsAccountDetailResp account,
                                   SmsTemplateDetailVo template,
                                   String variableStr,
                                   Long planId,
                                   Long nodeId,
                                   String messageId,
                                   List<TemplateSmsIdAndMobile> mobiles,
                                   MessageResourceType resourceType,
                                   PaymentTypeEnum paymentType) {
        SmsMessageResponse response;
        String templateReplaceModuleInformation;
        if (template.getTemplateType() == 1) {
            SmsTemplateSendNormalReq smsReq = buildNormalReq(account, template, mobiles);
            templateReplaceModuleInformation = smsReq.getTemplateContent();
            response = smsPlatformService.sendNormalSms(smsReq);
        } else {
            SmsTemplateSendVariableReq variableReq = buildVariableReq(account, template, mobiles, variableStr);
            templateReplaceModuleInformation = variableReq.getTemplateContent();
            response = smsPlatformService.sendVariableSms(variableReq);
        }
        //todo  这里考虑阅信+模板
        template.setContent(templateReplaceModuleInformation);
        response.setTemplateReplaceModuleInformation(templateReplaceModuleInformation);
        if (response.isSuccess()) {

            Map<String, String> phone2SmsIdMap = response.getData().stream()
                    .collect(Collectors.toMap(SmsResponse::getMobile, SmsResponse::getSmsId));
            for (Map.Entry<String, String> stringStringEntry : phone2SmsIdMap.entrySet()) {
                redisService.setCacheObject(String.format(Constants.MSG_RECORDS_INSERT_REDIS_KEY, stringStringEntry.getValue()), stringStringEntry.getValue(), 30L, TimeUnit.SECONDS);
            }
            List<MsgRecordVo> msgRecords = buildMsgRecords(
                    resourceType,
                    planId,
                    nodeId,
                    MsgTypeEnum.SHORT_MSG,
                    response.getData().stream().map(SmsResponse::getMobile).collect(Collectors.toList()),
                    messageId,
                    true,
                    account,
                    template,
                    null,
                    null,
                    paymentType
            );
            for (MsgRecordVo msgRecord : msgRecords) {
                msgRecord.setMessageId(phone2SmsIdMap.get(msgRecord.getPhoneNum()));
            }
            msgRecordApi.insertBatch(msgRecords);
            MsgIdMappingVo msgIdMappingVo = new MsgIdMappingVo();
            msgIdMappingVo.setMessageId(messageId);
            msgIdMappingVo.setCustomerId(account.getCustomerId());
            for (Map.Entry<String, String> stringStringEntry : phone2SmsIdMap.entrySet()) {
                String platformMsgId = stringStringEntry.getValue();
                msgIdMappingVo.getPlatformMsgIds().add(platformMsgId);
            }
            msgRecordApi.insertMsgIdMapping(msgIdMappingVo);

            for (Map.Entry<String, String> stringStringEntry : phone2SmsIdMap.entrySet()) {
                redisService.deleteObject(String.format(Constants.MSG_RECORDS_INSERT_REDIS_KEY, stringStringEntry.getValue()));
            }
        } else {
            if (MessageResourceType.FAST_GROUP.equals(resourceType)) {//如果是快捷群发，并且如果发送失败，则返回发送失败原因
                fastGroupMessageApi.updateStatus(planId, "发送消息网关失败");
            }
        }
        return response;
    }

    private SmsTemplateSendNormalReq buildNormalReq(CspSmsAccountDetailResp account, SmsTemplateDetailVo template, List<TemplateSmsIdAndMobile> mobiles) {
        SmsTemplateSendNormalReq smsTemplateSendNormalReq = new SmsTemplateSendNormalReq();
        smsTemplateSendNormalReq.setAppId(account.getAppId());
        smsTemplateSendNormalReq.setSecretKey(account.getAppSecret());
        smsTemplateSendNormalReq.setPlatformTemplateId(template.getPlatformTemplateId());
        smsTemplateSendNormalReq.setCustomSmsIdAndMobiles(mobiles.toArray(new TemplateSmsIdAndMobile[0]));
        smsTemplateSendNormalReq.setAccountId(template.getAccountId());
        smsTemplateSendNormalReq.setTemplateContent(template.getContent());
        smsTemplateSendNormalReq.setTemplateId(template.getId());
        return smsTemplateSendNormalReq;
    }

    private SmsTemplateSendVariableReq buildVariableReq(
            CspSmsAccountDetailResp account,
            SmsTemplateDetailVo template,
            List<TemplateSmsIdAndMobile> mobiles,
            String variableStr) {
        String templateContent = template.getContent();
        SmsTemplateContentVo templateContentVo = JSONObject.parseObject(templateContent, SmsTemplateContentVo.class);
        List<SmsTemplateDataVo> smsTemplateDataVos = templateContentVo.getNames();
        String[] variables = null;
        if (StringUtils.isNotBlank(variableStr)) {
            variables = variableStr.split(",");
        }

        Map<String, String> content = new HashMap<>();
        int index = 0;
        for (SmsTemplateDataVo smsTemplateDataVo : smsTemplateDataVos) {
            String variable = "";
            if (variables != null && index < variables.length) {
                variable = variables[index];
            }
            String placeholder = "{{" + smsTemplateDataVo.getId() + "}}";
            content.put(smsTemplateDataVo.getName(), variable);
            templateContent = templateContent.replace(placeholder, variable);
            index++;
        }
        mobiles.forEach(mobile -> mobile.setContent(content));
        SmsTemplateSendVariableReq variableReq = new SmsTemplateSendVariableReq();
        variableReq.setAppId(account.getAppId());
        variableReq.setSecretKey(account.getAppSecret());
        variableReq.setCustomSmsIdAndMobiles(mobiles.toArray(new TemplateSmsIdAndMobile[0]));
        variableReq.setPlatformTemplateId(template.getPlatformTemplateId());
        variableReq.setAccountId(template.getAccountId());
        variableReq.setTemplateContent(templateContent);
        variableReq.setTemplateId(template.getId());
        return variableReq;
    }
}
