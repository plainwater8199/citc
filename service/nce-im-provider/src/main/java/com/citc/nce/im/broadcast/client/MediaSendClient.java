package com.citc.nce.im.broadcast.client;

import com.citc.nce.auth.csp.mediasms.template.enums.ContentMediaType;
import com.citc.nce.auth.csp.mediasms.template.vo.MediaSmsTemplateContentVo;
import com.citc.nce.auth.csp.mediasms.template.vo.MediaSmsTemplatePreviewVo;
import com.citc.nce.auth.csp.recharge.Const.AccountTypeEnum;
import com.citc.nce.auth.csp.recharge.Const.ConsumeTypeEnum;
import com.citc.nce.auth.csp.recharge.Const.PaymentTypeEnum;
import com.citc.nce.auth.csp.recharge.Const.TariffTypeEnum;
import com.citc.nce.auth.csp.recharge.RechargeTariffApi;
import com.citc.nce.auth.csp.recharge.vo.RechargeTariffDetailResp;
import com.citc.nce.auth.csp.videoSms.account.vo.CspVideoSmsAccountDetailResp;
import com.citc.nce.auth.prepayment.DeductionAndRefundApi;
import com.citc.nce.auth.prepayment.PrepaymentApi;
import com.citc.nce.auth.prepayment.vo.FeeDeductReq;
import com.citc.nce.auth.prepayment.vo.ReturnBalanceBatchReq;
import com.citc.nce.authcenter.constant.AuthCenterError;
import com.citc.nce.authcenter.csp.CspCustomerApi;
import com.citc.nce.authcenter.csp.vo.UserInfoVo;
import com.citc.nce.common.constants.Constants;
import com.citc.nce.common.core.enums.MsgTypeEnum;
import com.citc.nce.common.core.exception.BizException;
import com.citc.nce.robot.enums.MessageResourceType;
import com.citc.nce.common.redis.config.RedisService;
import com.citc.nce.common.util.UUIDUtils;
import com.citc.nce.im.richMedia.RichMediaPlatformService;
import com.citc.nce.robot.constant.MessagePaymentTypeConstant;
import com.citc.nce.robot.domain.common.NameItem;
import com.citc.nce.robot.domain.mediatemplate.TextContent;
import com.citc.nce.robot.req.RichMediaResultArray;
import com.citc.nce.robot.res.RichMediaSendParam;
import com.citc.nce.robot.res.RichMediaSendParamRes;
import com.citc.nce.robot.vo.VideoSmsResponse;
import com.citc.nce.tenant.MsgRecordApi;
import com.citc.nce.tenant.vo.req.MsgIdMappingVo;
import com.citc.nce.tenant.vo.req.MsgRecordVo;
import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.shardingsphere.transaction.annotation.ShardingSphereTransactionType;
import org.apache.shardingsphere.transaction.core.TransactionType;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.Arrays;
import java.util.Collections;
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
public class MediaSendClient {
    private final ObjectMapper objectMapper;
    private final RichMediaPlatformService mediaPlatformService;
    private final MsgRecordApi msgRecordApi;
    private final CspCustomerApi cspCustomerApi;
    private final PrepaymentApi prepaymentApi;
    private final DeductionAndRefundApi deductionAndRefundApi;
    private final RedisTemplate<Object, Object> redisTemplate;
    private final RedisService redisService;
    private final RechargeTariffApi rechargeTariffApi;

    @Transactional(rollbackFor = Exception.class)
    @ShardingSphereTransactionType(TransactionType.BASE)
    public RichMediaResultArray deductAndSend(MediaSmsTemplatePreviewVo template,
                                              CspVideoSmsAccountDetailResp account,
                                              List<String> phones,
                                              String variables,
                                              String customId,
                                              MessageResourceType resourceType,
                                              Integer paymentType) {
        UserInfoVo userInfo = cspCustomerApi.getByCustomerId(account.getCustomerId());
        String accountId = account.getAccountId();
        //最终是扣费还是扣套餐来记录
        PaymentTypeEnum finalPaymentType;
        //平台扣费messageId
        String messageId = UUIDUtils.generateUUID();
        if (userInfo.getPayType() == PREPAY) {
            //套餐
            if (PaymentTypeEnum.SET_MEAL.getCode().equals(paymentType)) {
                prepaymentApi.deductRemaining(account.getAccountId(), MsgTypeEnum.MEDIA_MSG, null, (long) phones.size(), 1L);
                finalPaymentType = PaymentTypeEnum.SET_MEAL;
            }
            //余额
            else if (PaymentTypeEnum.BALANCE.getCode().equals(paymentType)) {
                checkTariffSet(account);
                FeeDeductReq feeDeductReq = FeeDeductReq.builder()
                        .accountType(AccountTypeEnum.VIDEO_SMS.getCode())
                        .tariffType(TariffTypeEnum.VIDEO_SMS.getCode())
                        .payType(PREPAY.getCode())
                        .accountId(accountId)
                        .messageId(messageId)
                        .customerId(account.getCustomerId())
                        .phoneNumbers(phones)
                        .chargeNum(1)
                        .build();
                deductionAndRefundApi.deductFee(feeDeductReq);
                finalPaymentType = PaymentTypeEnum.BALANCE;
            }
            //默认先扣套餐再尝试余额
            else {
                try {
                    prepaymentApi.deductRemaining(account.getAccountId(), MsgTypeEnum.MEDIA_MSG, null, (long) phones.size(), 1L);
                    finalPaymentType = PaymentTypeEnum.SET_MEAL;
                } catch (Exception e) {
                    log.info("先扣除套餐失败");
                    checkTariffSet(account);
                    FeeDeductReq feeDeductReq = FeeDeductReq.builder()
                            .accountType(AccountTypeEnum.VIDEO_SMS.getCode())
                            .tariffType(TariffTypeEnum.VIDEO_SMS.getCode())
                            .payType(PREPAY.getCode())
                            .accountId(accountId)
                            .messageId(messageId)
                            .customerId(account.getCustomerId())
                            .phoneNumbers(phones)
                            .chargeNum(1)
                            .build();
                    deductionAndRefundApi.deductFee(feeDeductReq);
                    finalPaymentType = PaymentTypeEnum.BALANCE;
                }
            }
        } else {
            //后付费
            checkTariffSet(account);
            FeeDeductReq feeDeductReq = FeeDeductReq.builder()
                    .accountType(AccountTypeEnum.VIDEO_SMS.getCode())
                    .tariffType(TariffTypeEnum.VIDEO_SMS.getCode())
                    .payType(POSTPAY.getCode())
                    .accountId(accountId)
                    .messageId(messageId)
                    .customerId(account.getCustomerId())
                    .phoneNumbers(phones)
                    .chargeNum(1)
                    .build();
            deductionAndRefundApi.deductFee(feeDeductReq);
            finalPaymentType = PaymentTypeEnum.BALANCE;
        }
        RichMediaResultArray resultArray = send(template, account, null, null, phones, messageId, variables, customId, resourceType, finalPaymentType);
        //如果发送失败 手动返还额度
        if (resultArray != null && Boolean.FALSE.equals(resultArray.getSuccess())) {
            if (finalPaymentType == PaymentTypeEnum.SET_MEAL) {
                prepaymentApi.returnRemaining(account.getAccountId(), MsgTypeEnum.MEDIA_MSG, null, (long) phones.size());
            } else {
                ReturnBalanceBatchReq feeDeductReq = ReturnBalanceBatchReq.builder()
                        .tariffType(TariffTypeEnum.VIDEO_SMS.getCode())
                        .messageId(messageId)
                        .customerId(account.getCustomerId())
                        .phoneNumbers(phones)
                        .build();
                deductionAndRefundApi.returnBalanceBatch(feeDeductReq);
            }
        }
        return resultArray;
    }

    private void checkTariffSet(CspVideoSmsAccountDetailResp account) {
        //没有设置资费没法发送
        RechargeTariffDetailResp rechargeTariff = rechargeTariffApi.getRechargeTariff(account.getAccountId());
        if (rechargeTariff == null) {
            throw new BizException(String.format(AuthCenterError.Tarrif_Not_config.getMsg(), account.getAccountName()));
        }
    }


    @Transactional(rollbackFor = Exception.class)
    public RichMediaResultArray send(MediaSmsTemplatePreviewVo template,
                                     CspVideoSmsAccountDetailResp account,
                                     Long planId,
                                     Long nodeId,
                                     List<String> phones,
                                     String messageId,
                                     String variables,
                                     String customId,
                                     MessageResourceType resourceType,
                                     PaymentTypeEnum paymentType) {
        RichMediaSendParam mediaSendParam = buildMsgBody(template, phones, variables, customId);
        RichMediaSendParamRes richMediaSendParamRes = new RichMediaSendParamRes();
        richMediaSendParamRes.setAppId(account.getAppId());
        richMediaSendParamRes.setAppSecret(account.getAppSecret());
        richMediaSendParamRes.setRichMediaSendParam(mediaSendParam);
        RichMediaResultArray resultArray;
        if (template.getTemplateType() == 1) {
            resultArray = mediaPlatformService.messageSend(richMediaSendParamRes);//普通发送
        } else {
            resultArray = mediaPlatformService.sendPny(richMediaSendParamRes);//个性发送
        }
        boolean success = Boolean.TRUE.equals(resultArray.getSuccess());

        String templateSnapshot = createTemplateSnapshot(template, mediaSendParam);
        template.setTemplateContent(templateSnapshot);
        resultArray.setContents(templateSnapshot);
        Map<String, String> phone2VmsIdMap = new HashMap<>();
        if (success) {
            phone2VmsIdMap = resultArray.getResult().stream().collect(Collectors.toMap(VideoSmsResponse::getMobile, VideoSmsResponse::getVmsId));

            for (Map.Entry<String, String> stringStringEntry : phone2VmsIdMap.entrySet()) {
                redisService.setCacheObject(String.format(Constants.MSG_RECORDS_INSERT_REDIS_KEY, stringStringEntry.getValue()), stringStringEntry.getValue(), 30L, TimeUnit.SECONDS);
            }

            List<MsgRecordVo> msgRecords = buildMsgRecords(resourceType, planId, nodeId, MsgTypeEnum.MEDIA_MSG, phones, null, true, account, template, null, null, paymentType);
            for (MsgRecordVo msgRecord : msgRecords) {
                msgRecord.setMessageId(phone2VmsIdMap.get(msgRecord.getPhoneNum()));
            }
            msgRecordApi.insertBatch(msgRecords);
        } else {
            List<VideoSmsResponse> result = resultArray.getResult();

            if (result != null && !result.isEmpty()) {
                phone2VmsIdMap = result.stream().collect(Collectors.toMap(VideoSmsResponse::getMobile, VideoSmsResponse::getVmsId));
            }
            List<MsgRecordVo> msgRecords = buildMsgRecords(resourceType, planId, nodeId, MsgTypeEnum.MEDIA_MSG, phones, null, true, account, template, null, resultArray.getMessage(), paymentType);
            for (MsgRecordVo msgRecord : msgRecords) {
                msgRecord.setMessageId(phone2VmsIdMap.containsKey(msgRecord.getPhoneNum()) ? phone2VmsIdMap.get(msgRecord.getPhoneNum()) : msgRecord.getPhoneNum());
            }
            msgRecordApi.insertBatch(msgRecords);
        }

        MsgIdMappingVo msgIdMappingVo = new MsgIdMappingVo();
        msgIdMappingVo.setMessageId(messageId);
        msgIdMappingVo.setCustomerId(account.getCustomerId());
        for (Map.Entry<String, String> stringStringEntry : phone2VmsIdMap.entrySet()) {
            String platformMsgId = stringStringEntry.getValue();
            msgIdMappingVo.getPlatformMsgIds().add(platformMsgId);
        }
        msgRecordApi.insertMsgIdMapping(msgIdMappingVo);

        for (Map.Entry<String, String> stringStringEntry : phone2VmsIdMap.entrySet()) {
            redisService.deleteObject(String.format(Constants.MSG_RECORDS_INSERT_REDIS_KEY, stringStringEntry.getValue()));
        }
        return resultArray;
    }


    /**
     * 构建发送视频短信参数
     *
     * @param templatePreviewVo 模板详情
     * @param phones            要发送的手机号
     * @param variables         发送时传入的变量值（可为空）
     * @param customId          自定义参数
     * @return 调用网关需要的视频短信发送参数对象
     */
    private RichMediaSendParam buildMsgBody(MediaSmsTemplatePreviewVo templatePreviewVo, List<String> phones, String variables, String customId) {
        RichMediaSendParam richMediaSendParam = new RichMediaSendParam();
        richMediaSendParam.setTemplateNumber(templatePreviewVo.getPlatformTemplateId());
        richMediaSendParam.setMobile(phones.toString().replaceAll("(?:\\[|null|\\]| +)", ""));
        if (StringUtils.isNotEmpty(variables)) {
            List<String> variableList = Arrays.asList(variables.split(","));
            RichMediaSendParam.DataItem dataItem = new RichMediaSendParam.DataItem();
            dataItem.setMobile(phones.get(0));
            HashMap<String, String> paramMap = new HashMap<>();
            for (MediaSmsTemplateContentVo content : templatePreviewVo.getContents()) {
                if (content.getMediaType() == ContentMediaType.TEXT) {
                    TextContent textContent;
                    try {
                        textContent = objectMapper.readValue(content.getContent(), TextContent.class);
                    } catch (JsonProcessingException e) {
                        log.error("json解析失败", e);
                        throw new BizException("解析视频模板文本内容失败");
                    }
                    for (NameItem nameItem : textContent.getNames()) {
                        int varIndex = Integer.parseInt(nameItem.getName().replace(NameItem.PREFIX, "")) - 1;
                        String varValue = "";
                        if (variableList.size() > varIndex && varIndex >= 0) {
                            varValue = variableList.get(varIndex);
                        }
                        paramMap.put(nameItem.getName(), varValue);
                    }
                }
            }
            dataItem.setParams(paramMap);
            richMediaSendParam.setData(Collections.singletonList(dataItem));
        } else {
            if (templatePreviewVo.getTemplateType() == 2) {
                RichMediaSendParam.DataItem dataItem = new RichMediaSendParam.DataItem();
                dataItem.setMobile(phones.get(0));
                HashMap<String, String> paramMap = new HashMap<>();
                for (MediaSmsTemplateContentVo content : templatePreviewVo.getContents()) {
                    if (content.getMediaType() == ContentMediaType.TEXT) {
                        TextContent textContent;
                        try {
                            textContent = objectMapper.readValue(content.getContent(), TextContent.class);
                        } catch (JsonProcessingException e) {
                            log.error("json解析失败", e);
                            throw new BizException("解析视频模板文本内容失败");
                        }
                        for (NameItem nameItem : textContent.getNames()) {
                            String varValue = "NULL";
                            paramMap.put(nameItem.getName(), varValue);
                        }
                    }
                }
                dataItem.setParams(paramMap);
                richMediaSendParam.setData(Collections.singletonList(dataItem));
            }
        }

        if (StringUtils.isNotBlank(customId)) {
            richMediaSendParam.setCustomId(customId);
        }
        return richMediaSendParam;
    }

    /**
     * 根据模板详情和使用时参数生成模板快照
     *
     * @param templateInfo       模板信息
     * @param richMediaSendParam 参数
     * @return string类型的模板快照
     */
    private String createTemplateSnapshot(MediaSmsTemplatePreviewVo templateInfo, RichMediaSendParam richMediaSendParam) {
        try {
            if (richMediaSendParam != null && !CollectionUtils.isEmpty(richMediaSendParam.getData())) {
                RichMediaSendParam.DataItem dataItem = richMediaSendParam.getData().get(0);
                Map<String, String> variableMap = dataItem.getParams();
                final String defaultVariableValue = "";
                //替换模板内容中的变量占位符为变量值
                for (MediaSmsTemplateContentVo content : templateInfo.getContents()) {
                    if (content.getMediaType() == ContentMediaType.TEXT) {
                        TextContent textContent = objectMapper.readValue(content.getContent(), TextContent.class);
                        for (NameItem nameItem : textContent.getNames()) {
                            String varValue = variableMap.getOrDefault(nameItem.getName(), defaultVariableValue);
                            String replaced = textContent.getValue().replaceAll("\\{\\{" + nameItem.getId() + "}}", varValue);
                            textContent.setValue(replaced);
                        }
                        content.setContent(objectMapper.writeValueAsString(textContent));
                    }
                }
            }
            return objectMapper.writeValueAsString(templateInfo);
        } catch (JacksonException e) {
            log.error("创建模板快照失败:", e);
            throw new BizException("创建快照失败");
        }
    }

}
