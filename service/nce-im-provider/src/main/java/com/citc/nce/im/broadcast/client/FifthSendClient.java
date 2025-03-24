package com.citc.nce.im.broadcast.client;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.citc.nce.auth.accountmanagement.vo.AccountManagementResp;
import com.citc.nce.auth.cardstyle.CardStyleApi;
import com.citc.nce.auth.cardstyle.vo.CardStyleOneReq;
import com.citc.nce.auth.cardstyle.vo.CardStyleResp;
import com.citc.nce.auth.constant.csp.common.CSPOperatorCodeEnum;
import com.citc.nce.auth.csp.recharge.Const.AccountTypeEnum;
import com.citc.nce.auth.csp.recharge.Const.PaymentTypeEnum;
import com.citc.nce.auth.csp.recharge.Const.TariffTypeEnum;
import com.citc.nce.auth.csp.recharge.RechargeTariffApi;
import com.citc.nce.auth.csp.recharge.vo.RechargeTariffDetailResp;
import com.citc.nce.auth.messagetemplate.MessageTemplateApi;
import com.citc.nce.auth.messagetemplate.vo.MessageTemplateProvedReq;
import com.citc.nce.auth.messagetemplate.vo.MessageTemplateResp;
import com.citc.nce.auth.prepayment.DeductionAndRefundApi;
import com.citc.nce.auth.prepayment.PrepaymentApi;
import com.citc.nce.auth.prepayment.vo.FeeDeductReq;
import com.citc.nce.auth.prepayment.vo.ReturnBalanceBatchReq;
import com.citc.nce.auth.readingLetter.template.ReadingLetterTemplateApi;
import com.citc.nce.auth.readingLetter.template.vo.ReadingLetterTemplateVo;
import com.citc.nce.authcenter.constant.AuthCenterError;
import com.citc.nce.authcenter.csp.CspCustomerApi;
import com.citc.nce.authcenter.csp.vo.UserInfoVo;
import com.citc.nce.common.constants.Constants;
import com.citc.nce.common.core.enums.MsgSubTypeEnum;
import com.citc.nce.common.core.enums.MsgTypeEnum;
import com.citc.nce.common.core.exception.BizException;
import com.citc.nce.common.core.pojo.ConversationContext;
import com.citc.nce.common.redis.config.RedisService;
import com.citc.nce.common.util.UUIDUtils;
import com.citc.nce.common.xss.core.XssCleanIgnore;
import com.citc.nce.dto.FileAccept;
import com.citc.nce.dto.FileTidReq;
import com.citc.nce.dto.PictureReq;
import com.citc.nce.dto.VideoReq;
import com.citc.nce.fileApi.PictureApi;
import com.citc.nce.fileApi.PlatformApi;
import com.citc.nce.fileApi.VideoApi;
import com.citc.nce.filecenter.FileApi;
import com.citc.nce.filecenter.platform.vo.UpReceiveData;
import com.citc.nce.filecenter.vo.DownloadReq;
import com.citc.nce.filecenter.vo.UploadReq;
import com.citc.nce.im.robot.util.MsgUtils;
import com.citc.nce.robot.api.FastGroupMessageApi;
import com.citc.nce.robot.enums.MessageResourceType;
import com.citc.nce.im.config.LayoutStyleUtil;
import com.citc.nce.im.excle.VariableData;
import com.citc.nce.im.exp.SendGroupExp;
import com.citc.nce.im.msgenum.SupplierConstant;
import com.citc.nce.im.service.PlatformService;
import com.citc.nce.im.session.ApplicationContextUil;
import com.citc.nce.im.util.MyFileUtil;
import com.citc.nce.msgenum.DeliveryEnum;
import com.citc.nce.msgenum.RequestEnum;
import com.citc.nce.robot.constant.MessagePaymentTypeConstant;
import com.citc.nce.robot.vo.BaseMessage;
import com.citc.nce.robot.vo.CardObject;
import com.citc.nce.robot.vo.FileIdResp;
import com.citc.nce.robot.vo.FileMessage;
import com.citc.nce.robot.vo.FileObject;
import com.citc.nce.robot.vo.FontdoFileMessage;
import com.citc.nce.robot.vo.Layout;
import com.citc.nce.robot.vo.Media;
import com.citc.nce.robot.vo.MessageData;
import com.citc.nce.robot.vo.Suggestions;
import com.citc.nce.robot.vo.TextObject;
import com.citc.nce.robot.vo.directcustomer.FallbackSms;
import com.citc.nce.robot.vo.directcustomer.FallbackTemplate;
import com.citc.nce.robot.vo.directcustomer.Parameter;
import com.citc.nce.tenant.MsgRecordApi;
import com.citc.nce.tenant.vo.req.MsgRecordVo;
import com.citc.nce.vo.VideoResp;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.shardingsphere.transaction.annotation.ShardingSphereTransactionType;
import org.apache.shardingsphere.transaction.core.TransactionType;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.citc.nce.common.core.enums.CustomerPayType.POSTPAY;
import static com.citc.nce.common.core.enums.CustomerPayType.PREPAY;
import static com.citc.nce.common.core.enums.MsgSubTypeEnum.convertTemplateType2MsgSubType;
import static com.citc.nce.im.broadcast.utils.BroadcastConstants._5G_SENT_CODE_SUCCESS;
import static com.citc.nce.im.broadcast.utils.MsgRecordFactory.buildMsgRecords;

/**
 * 5G消息发送客户端
 *
 * @author jcrenc
 * @since 2024/5/8 15:12
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class FifthSendClient {
    private final MessageTemplateApi messageTemplateApi;
    private final CardStyleApi cardStyleApi;
    private final FileApi fileApi;
    private final VideoApi videoApi;
    private final PlatformApi platformApi;
    private final PictureApi pictureApi;
    @Value("${form.shareUrl}")
    private String formShareUrl;
    private final PlatformService platformService;
    private final MsgRecordApi msgRecordApi;
    private final CspCustomerApi cspCustomerApi;
    private final PrepaymentApi prepaymentApi;
    @Resource
    private FastGroupMessageApi fastGroupMessageApi;
    private final DeductionAndRefundApi deductionAndRefundApi;
    private final ReadingLetterTemplateApi readingLetterTemplateApi;
    private final RedisTemplate<Object, Object> redisTemplate;
    private final RedisService redisService;
    private final RechargeTariffApi rechargeTariffApi;


    /**
     * 发送5G消息
     *
     * @param resourceType 消息来源
     * @param account      账号
     * @param template     模板
     * @param sendPhones   发送手机号列表
     * @param variableData 变量数据
     * @param paymentType  支付方式  1: 扣余额   2: 扣套餐  null:先扣套餐再尝试余额
     * @param context      会话上下文，可以为空
     * @return 发送结果
     */
    @XssCleanIgnore
    @Transactional(rollbackFor = Exception.class)
    @ShardingSphereTransactionType(TransactionType.BASE)
    public MessageData deductAndSend(AccountManagementResp account,
                                     MessageTemplateResp template,
                                     List<String> sendPhones,
                                     VariableData variableData,
                                     MessageResourceType resourceType,
                                     Integer paymentType,
                                     ConversationContext context) {
        UserInfoVo userInfo = cspCustomerApi.getByCustomerId(account.getCustomerId());
        MsgSubTypeEnum subType = convertTemplateType2MsgSubType(template.getMessageType());
        String messageId = UUIDUtils.generateUUID();
        BaseMessage message = this.buildMsgBody(0L, sendPhones, template, account, variableData, messageId, context);
        //关键词回复也要支持会话消息
        String conversationId = null;
        if (resourceType == MessageResourceType.KEY_WORD) {
            if (MsgUtils.trySendSessionMessage(message, CSPOperatorCodeEnum.byCode(account.getAccountTypeCode()))) {
                FileMessage fileMessage = (FileMessage) message;
                fileMessage.setInReplyTo(fileMessage.getContributionId());
                subType = MsgSubTypeEnum.CONVERSATION;
                conversationId = fileMessage.getInReplyTo();
            }
        }
        PaymentTypeEnum finalPaymentType;
        RechargeTariffDetailResp rechargeTariff = rechargeTariffApi.getRechargeTariff(account.getChatbotAccountId());
        if (rechargeTariff == null) {
            throw new BizException(String.format(AuthCenterError.Tarrif_Not_config.getMsg(), account.getAccountName()));
        }

        if (userInfo.getPayType() == PREPAY) {
            //套餐
            if (PaymentTypeEnum.SET_MEAL.getCode().equals(paymentType)) {
                prepaymentApi.deductRemaining(account.getChatbotAccount(), MsgTypeEnum.M5G_MSG, subType, (long) sendPhones.size(), 1L);
                finalPaymentType = PaymentTypeEnum.SET_MEAL;
            }
            //余额
            else if (PaymentTypeEnum.BALANCE.getCode().equals(paymentType)) {
                FeeDeductReq feeDeductReq = FeeDeductReq.builder().accountType(AccountTypeEnum.FIFTH_MESSAGES.getCode())
                        .tariffType((template.getMessageType() == 1 || template.getMessageType() == 8) ? TariffTypeEnum.TEXT_MESSAGE.getCode() : TariffTypeEnum.RICH_MEDIA_MESSAGE.getCode())
                        .payType(PREPAY.getCode())
                        .accountId(account.getChatbotAccountId())
                        .messageId(messageId)
                        .fifthFallbackType(template.getFallbackReadingLetterTemplateId() != null ? Integer.valueOf(2) : template.getFallbackSmsContent() != null ? 1 : null)
                        .customerId(account.getCustomerId())
                        .phoneNumbers(sendPhones)
                        .chargeNum(1)
                        .build();
                deductionAndRefundApi.deductFee(feeDeductReq);
                finalPaymentType = PaymentTypeEnum.BALANCE;
            }
            //默认先扣套餐再尝试余额
            else {
                try {
                    prepaymentApi.deductRemaining(account.getChatbotAccount(), MsgTypeEnum.M5G_MSG, subType, (long) sendPhones.size(), 1L);
                    finalPaymentType = PaymentTypeEnum.SET_MEAL;
                } catch (Exception e) {
                    try {
                        log.info("先扣除套餐失败");
                        FeeDeductReq feeDeductReq = FeeDeductReq.builder().accountType(AccountTypeEnum.FIFTH_MESSAGES.getCode())
                                .tariffType((template.getMessageType() == 1 || template.getMessageType() == 8) ? TariffTypeEnum.TEXT_MESSAGE.getCode() : TariffTypeEnum.RICH_MEDIA_MESSAGE.getCode())
                                .payType(PREPAY.getCode())
                                .accountId(account.getChatbotAccountId())
                                .messageId(messageId)
                                .customerId(account.getCustomerId())
                                .phoneNumbers(sendPhones)
                                .chargeNum(1)
                                .build();
                        deductionAndRefundApi.deductFee(feeDeductReq);
                        finalPaymentType = PaymentTypeEnum.BALANCE;
                    } catch (Exception e1) {
                        log.info("扣除余额也失败");
                        throw new BizException("扣减失败");
                    }
                }
            }
        } else {
            //后付费
            FeeDeductReq feeDeductReq = FeeDeductReq.builder().accountType(AccountTypeEnum.FIFTH_MESSAGES.getCode())
                    .tariffType((template.getMessageType() == 1 || template.getMessageType() == 8) ? TariffTypeEnum.TEXT_MESSAGE.getCode() : TariffTypeEnum.RICH_MEDIA_MESSAGE.getCode())
                    .payType(POSTPAY.getCode())
                    .accountId(account.getChatbotAccountId())
                    .messageId(messageId)
                    .customerId(account.getCustomerId())
                    .phoneNumbers(sendPhones)
                    .chargeNum(1)
                    .build();
            deductionAndRefundApi.deductFee(feeDeductReq);
            finalPaymentType = PaymentTypeEnum.BALANCE;
        }
        //将messageId 和 finalPaymentType 对应关系 存到redis,并设置过期时间
        redisTemplate.opsForValue().set(MessagePaymentTypeConstant.redisPrefix + messageId, finalPaymentType.toString(), 5, TimeUnit.MINUTES);
        MessageData messageData = platformService.sendMessage(message, account);
        messageData.setTemplateReplaceModuleInformation(template.getModuleInformation());
        boolean success = messageData.getCode() == _5G_SENT_CODE_SUCCESS;
        //保存消息记录，msg_record表
        List<MsgRecordVo> msgRecords = buildMsgRecords(resourceType, null, null, MsgTypeEnum.M5G_MSG, message.getDestinationAddress(), messageId, success, account, template, conversationId, null, finalPaymentType);

        if (success) {
            redisService.setCacheObject(String.format(Constants.MSG_RECORDS_INSERT_REDIS_KEY, messageId), messageId, 30L, TimeUnit.SECONDS);
            //Redis缓存7天，表明该5G消息对应的messageId是选择的回落5G阅信
            if (template.getFallbackReadingLetterTemplateId() != null) {
                log.info("5G消息发送messageId={}是选择的回落5G阅信", messageId);
                redisService.setCacheObject(String.format(Constants.FIFTH_MSG_FALLBACK_READING_LETTER_KEY, messageId), 1, 7L, TimeUnit.DAYS);
            }
        }
        msgRecordApi.insertBatch(msgRecords);

        if (!success) {
            if (finalPaymentType == (PaymentTypeEnum.SET_MEAL)) {
                prepaymentApi.returnRemaining(account.getChatbotAccount(), MsgTypeEnum.M5G_MSG, subType, (long) sendPhones.size());
            } else {
                ReturnBalanceBatchReq feeDeductReq = ReturnBalanceBatchReq.builder()
                        .tariffType((template.getMessageType() == 1 || template.getMessageType() == 8) ? TariffTypeEnum.TEXT_MESSAGE.getCode() : TariffTypeEnum.RICH_MEDIA_MESSAGE.getCode())
                        .messageId(messageId)
                        .customerId(account.getCustomerId())
                        .phoneNumbers(sendPhones)
                        .build();
                deductionAndRefundApi.returnBalanceBatch(feeDeductReq);
            }
        } else {
            redisService.deleteObject(String.format(Constants.MSG_RECORDS_INSERT_REDIS_KEY, messageId));
        }
        return messageData;
    }

    @XssCleanIgnore
    @Transactional(rollbackFor = Exception.class)
    @ShardingSphereTransactionType(TransactionType.BASE)
    public MessageData sendWithMessageId(
            AccountManagementResp account,
            MessageTemplateResp template,
            Long planId,
            Long nodeId,
            List<String> sendPhones,
            String messageId,
            VariableData variableData,
            MessageResourceType resourceType,
            PaymentTypeEnum paymentType) {
        BaseMessage message = this.buildMsgBody(nodeId, sendPhones, template, account, variableData, messageId, null);
        MessageData messageData = platformService.sendMessage(message, account);
        boolean success = messageData.getCode() == _5G_SENT_CODE_SUCCESS;
        messageId = ObjectUtil.isNull(messageData.getData()) ? "" : messageData.getData().getMessageId();
        String failedReason = null;
        if (!success) {
            failedReason = messageData.getMessage();
            if ((MessageResourceType.FAST_GROUP.equals(resourceType)))
                fastGroupMessageApi.updateStatus(planId, failedReason);
        }
        //保存消息记录，msg_record表
        redisService.setCacheObject(String.format(Constants.MSG_RECORDS_INSERT_REDIS_KEY, messageId), messageId, 30L, TimeUnit.SECONDS);
        //Redis缓存7天，表明该5G消息对应的messageId是选择的回落5G阅信
        if (template.getFallbackReadingLetterTemplateId() != null) {
            log.info("5G消息发送messageId={}是选择的回落5G阅信", messageId);
            redisService.setCacheObject(String.format(Constants.FIFTH_MSG_FALLBACK_READING_LETTER_KEY, messageId), 1, 7L, TimeUnit.DAYS);
        }
        List<MsgRecordVo> msgRecords = buildMsgRecords(resourceType, planId, nodeId, MsgTypeEnum.M5G_MSG, message.getDestinationAddress(), messageId, success, account, template,null, failedReason, paymentType);
        msgRecordApi.insertBatch(msgRecords);
        redisService.deleteObject(String.format(Constants.MSG_RECORDS_INSERT_REDIS_KEY, messageId));
        return messageData;
    }


    public void saveMsgRecordForNoOperator(Long planId, Long actualNodeId, List<String> failPhoneList, MessageTemplateResp template, MessageResourceType resourceType, String customerId) {
        if (!CollectionUtil.isEmpty(failPhoneList)) {
            List<MsgRecordVo> msgRecords = new ArrayList<>(failPhoneList.size());
            Date now = new Date(); // 重用时间戳
            DeliveryEnum sendResult = DeliveryEnum.FAILED;
            RequestEnum finalResult = RequestEnum.FAILED;
            //查找本messageId对应的扣款类型
            for (String phone : failPhoneList) {
                MsgRecordVo msgRecord = new MsgRecordVo();
                // 通用属性赋值
                msgRecord
                        .setMessageResource(resourceType.getCode())
                        .setPlanId(planId)
                        .setPlanDetailId(actualNodeId)
                        .setAccountType(1)
                        .setMessageId("")
                        .setPhoneNum(phone)
                        .setSendResult(sendResult.getCode())
                        .setFinalResult(finalResult.getCode())
                        .setFailedReason("无可用账号")
                        .setSendTime(now)
                        .setReceiptTime(now)
                        .setCreateTime(now)
                        .setUpdateTime(now)
                        .setConversationId("")
                        .setAccountId("")
                        .setAccountName("")
                        .setOperatorCode(-1)
                        .setCallerAccount("")
                        .setCreator(customerId)
                        .setUpdater(customerId)
                        .setTemplateId((template.getId()))
                        .setTemplateName(template.getTemplateName())
                        .setButtonContent(template.getShortcutButton())
                        .setMessageContent(template.getModuleInformation())
                        .setMessageType(template.getMessageType());
                msgRecords.add(msgRecord);
            }
            log.info("msgRecords:{}", msgRecords);
            msgRecordApi.insertBatch(msgRecords);
        }
    }

    @XssCleanIgnore
    @Transactional(rollbackFor = Exception.class)
    public MessageData sendByMessage(BaseMessage message,
                                     AccountManagementResp account,
                                     MessageTemplateResp template,
                                     MessageResourceType resourceType,
                                     PaymentTypeEnum paymentType) {
        MessageData messageData = platformService.sendMessage(message, account);
        boolean success = messageData.getCode() == _5G_SENT_CODE_SUCCESS;
        String messageId = messageData.getData().getMessageId();
        if (success) {
            redisService.setCacheObject(String.format(Constants.MSG_RECORDS_INSERT_REDIS_KEY, messageId), messageId, 30L, TimeUnit.SECONDS);
        }
        String conversationId = null;
        if (message instanceof FileMessage) {
            conversationId = ((FileMessage) message).getInReplyTo();
        }
        List<MsgRecordVo> msgRecords = buildMsgRecords(resourceType, null, null, MsgTypeEnum.M5G_MSG, message.getDestinationAddress(), messageId, success, account, template, conversationId, null, paymentType);
        msgRecordApi.insertBatch(msgRecords);
        if (success) {
            redisService.deleteObject(String.format(Constants.MSG_RECORDS_INSERT_REDIS_KEY, messageId));
        }
        return messageData;
    }


    public BaseMessage buildMsgBody(Long nodeId,
                                    List<String> sendPhones,
                                    MessageTemplateResp template,
                                    AccountManagementResp account,
                                    VariableData variableData,
                                    String messageId,
                                    ConversationContext context) {
        try {
            String content = template.getModuleInformation();
            Map<Integer, String> variableMap = extractVariables(content);
            String replaced = this.replaceVariables(content, variableMap, variableData);
            template.setModuleInformation(replaced);

            return SupplierConstant.FONTDO.equals(account.getSupplierTag())
                    ? buildSupplierMessage(nodeId, sendPhones, template, account, variableMap, variableData, messageId)
                    : buildDirectMessage(nodeId, sendPhones, template, account, variableData, messageId, context);
        } catch (Throwable throwable) {
            log.error("失败原因:{}", throwable.getMessage());
            log.error("异常信息", throwable);
            throw new BizException(throwable.getMessage());
        }
    }

    /**
     * 替换模板变量
     *
     * @param templateInfo 模板内容
     * @param variableMap  变量列表
     * @param variableData 发送时填写的变量值
     * @return 替换后的模板内容
     */
    private String replaceVariables(String templateInfo, Map<Integer, String> variableMap, VariableData variableData) {
        if (variableMap.isEmpty()) {
            return templateInfo;
        }
        String[] variableValues = variableData != null ? variableData.getVariables() : null;
        boolean hasVariableValues = variableValues != null && variableValues.length > 0;
        List<Integer> sortedNums = new ArrayList<>(variableMap.keySet());
        Collections.sort(sortedNums);
        for (Integer num : sortedNums) {
            String uuid = variableMap.get(num);
            String replacement = "";
            // 检查是否有该变量的值，注意下标是从0开始，所以使用 num-1
            if (hasVariableValues && num <= variableValues.length && StringUtils.isNotEmpty(variableValues[num - 1])) {
                replacement = Matcher.quoteReplacement(variableValues[num - 1]); // 避免特殊字符的问题
            }
            String reg = "\\{\\{" + Pattern.quote(uuid) + "}}"; // 使用Pattern.quote避免UUID中特殊字符影响
            templateInfo = templateInfo.replaceAll(reg, replacement);
        }
        return templateInfo;
    }


    private static Map<Integer, String> extractVariables(String templateInfo) {
        JSONObject templateJsonObject = JSONObject.parseObject(templateInfo);
        JSONArray variables = new JSONArray();
        variables.addAll(extractVariables(templateJsonObject));
        //多卡
        if (!CollectionUtil.isEmpty(templateJsonObject.getJSONArray("cardList"))) {
            JSONArray cardList = templateJsonObject.getJSONArray("cardList");
            for (int i = 0; i < cardList.size(); i++) {
                JSONObject cardJsonObject = cardList.getJSONObject(i);
                variables.addAll(extractVariables(cardJsonObject));
            }
        }
        return extract(variables);
    }

    private static Map<Integer, String> extract(JSONArray variables) {
        Map<Integer, String> variableMap = new HashMap<>();
        for (int i = 0; i < variables.size(); i++) {
            JSONObject variableObj = variables.getJSONObject(i);
            String uuid = variableObj.getString("id");
            String name = variableObj.getString("name");
            try {
                int num = Integer.parseInt(name.replace("变量", ""));
                variableMap.put(num, uuid);
            } catch (NumberFormatException e) {
                log.error("解析变量失败，name:{}", name);
            }
        }
        return variableMap;
    }

    private BaseMessage buildDirectMessage(Long nodeId,
                                           List<String> sendPhones,
                                           MessageTemplateResp template,
                                           AccountManagementResp account,
                                           VariableData variableData,
                                           String messageId,
                                           ConversationContext context) {
        try {
            String moduleInformation = template.getModuleInformation();
            //按钮信息
            String shortcutButton = "";
            JSONArray buttonJson = new JSONArray();
            if (null != template.getShortcutButton()) {
                shortcutButton = template.getShortcutButton();
                buttonJson = JSONObject.parseArray(shortcutButton);
            }
            log.info("shortcutButton information is {}", shortcutButton);
            //获取模板的类型
            int type = template.getMessageType();
            JSONObject jsonObject = JSONObject.parseObject(moduleInformation, JSONObject.class);

            FileMessage fileMessage = new FileMessage();
            Optional<ConversationContext> contextOptional = Optional.ofNullable(context);
            String conversationId = contextOptional.map(ConversationContext::getConversationId).orElseGet(IdUtil::fastSimpleUUID);
            String contributionId = contextOptional.map(ConversationContext::getContributionId).orElseGet(IdUtil::fastSimpleUUID);
            fileMessage.setConversationId(conversationId);
            fileMessage.setMessageId(messageId);
            fileMessage.setContributionId(contributionId);
            fileMessage.setDestinationAddress(sendPhones);
            List<Suggestions> suggestions = buildButton(buttonJson, nodeId, 1, -1);
            TextObject textObject = new TextObject();
            switch (getType(type)) {
                //发送文件消息
                case "file":
                    fileMessage.setMessageType("file");
                    FileIdResp fileIdResp = getFileTid(jsonObject, account.getAccountType());
                    FileObject fileObject = new FileObject();
                    fileObject.setFileId(fileIdResp.getFileTid());
                    if (StringUtils.isNotEmpty(fileIdResp.getThumbnailId())) {
                        fileObject.setThumbnailId(fileIdResp.getThumbnailId());
                    }
                    fileObject.setSuggestions(suggestions);
                    fileMessage.setContent(fileObject);
                    break;
                //发送卡片消息
                case "card":
                    String creator = account.getCustomerId();
                    CardObject cardObject = new CardObject();
                    JSONArray cardList = jsonObject.getJSONArray("cardList");
                    Long styleId = jsonObject.getLong("style");
                    String fileUuid = "";
                    if (ObjectUtil.isNotEmpty(styleId)) {
                        CardStyleOneReq cardReq = new CardStyleOneReq();
                        cardReq.setId(styleId);
                        CardStyleResp cardStyleResp = cardStyleApi.getCardStyleByIdInner(cardReq);
                        if (ObjectUtil.isNotEmpty(cardStyleResp)) {
                            jsonObject.put("styleInfo", cardStyleResp.getStyleInfo());
                            String info = jsonObject.toJSONString();
                            template.setModuleInformation(info);
                            fileUuid = cardStyleResp.getFileId();
                            log.info("样式文件id为{}", fileUuid);
                            //根据文件id上传到平台
                            UploadReq uploadReq = new UploadReq();
                            uploadReq.setList(account.getAccountType());
                            uploadReq.setFile(MyFileUtil.getMultipartFile(getFile(fileUuid)));
                            uploadReq.setCreator(creator);
                            uploadReq.setSceneId("codeincodeservice");
                            UpReceiveData upReceiveData = fileApi.upToConsumer(uploadReq);
                            log.info("网关上传后的返回结果为{}", upReceiveData);
                            if (upReceiveData.getCode() != 200) {
                                throw new BizException(SendGroupExp.FILE_UPLOAD_ERROR);
                            }
                        }
                    }
                    List<Media> medias = new ArrayList<>();
                    //单卡
                    if (CollectionUtil.isEmpty(cardList)) {
                        medias = getMedias(nodeId, jsonObject, account.getAccountType(), -1);
                        //横向还是纵向
                        String orientation = jsonObject.getString("layout");
                        Layout layout = new Layout();
                        if (StringUtils.equals(orientation, "transverse")) {
                            //横向
                            layout.setCardOrientation("HORIZONTAL");
                            //设置媒体左右
                            layout.setImageAlignment(jsonObject.getString("position").toUpperCase());
                        } else {
                            //纵向
                            layout.setCardOrientation("VERTICAL");
                        }
                        if (StringUtils.isNotEmpty(fileUuid)) {
                            layout.setStyle(LayoutStyleUtil.appendPrefix(fileUuid));
                            ;
                        }
                        cardObject.setLayout(layout);
                        if (medias.size() == 1) {
                            setCardHeight(jsonObject, medias.get(0));
                        }
                    } else {
                        //多卡
                        for (int i = 0; i < cardList.size(); i++) {
                            List<Media> list = getMedias(nodeId, cardList.getJSONObject(i), account.getAccountType(), i + 1);
                            list.forEach(media -> {
                                setCardHeight(jsonObject, media);
                            });
                            medias.addAll(list);
                            Layout layout = new Layout();
                            String width = jsonObject.getString("width");
                            setCardWidth(layout, width);
                            if (StringUtils.isNotEmpty(fileUuid)) {
                                layout.setStyle(LayoutStyleUtil.appendPrefix(fileUuid));
                                ;
                            }
                            cardObject.setLayout(layout);
                        }
                    }
                    cardObject.setMedia(medias);
                    if (!CollectionUtil.isEmpty(suggestions)) {
                        cardObject.setSuggestions(suggestions);
                    }
                    fileMessage.setMessageType("card");
                    fileMessage.setContent(cardObject);
                    break;
                case "text":
                    //发送文本
                    JSONObject input = jsonObject.getObject("input", JSONObject.class);
                    String text = input.getString("value");
                    textObject.setText(text);
                    //如果按钮信息不为空就加按钮信息
                    if (!CollectionUtil.isEmpty(suggestions)) {
                        textObject.setSuggestions(suggestions);
                    }
                    fileMessage.setMessageType("text");
                    fileMessage.setContent(textObject);
                    break;
                //发送地址
                case "location":
                    JSONArray jsonArray = jsonObject.getJSONObject("locationDetail").getJSONArray("names");
                    if (CollectionUtil.isNotEmpty(jsonArray) && ObjectUtil.isNotEmpty(variableData)) {
                        template.setModuleInformation(this.replaceVariables(template.getModuleInformation(), extract(jsonArray), variableData));
                    }
                    JSONObject newInfo = JSONObject.parseObject(template.getModuleInformation());
                    String locationDetail = newInfo.getJSONObject("locationDetail").getString("value");
                    log.info("jsonObject is {}", newInfo);
                    log.info("替换以后的模板 messageTemplateResp {}", template.getModuleInformation());
                    log.info("发送地理位置的描述信息为：{}", locationDetail);
                    String latitude = jsonObject.getJSONObject("baidu").getString("lat");
                    String longitude = jsonObject.getJSONObject("baidu").getString("lng");
                    String location = "geo:" + latitude + "," + longitude + ";crs=gcj02;u=10;rcs-l=" + locationDetail;
                    textObject.setText(location);
                    if (!CollectionUtil.isEmpty(suggestions)) {
                        textObject.setSuggestions(suggestions);
                    }
                    fileMessage.setMessageType("text");
                    fileMessage.setContent(textObject);
                    break;
                default:
                    break;
            }
            //短信内容回落
            if (StrUtil.isNotBlank(template.getFallbackSmsContent())) {
                fileMessage.setSmsSupported(true);
                fileMessage.setSmsContent(template.getFallbackSmsContent());
            }
            //阅信5G消息回落转为短信内容回落
            if (Objects.nonNull(template.getFallbackReadingLetterTemplateId())) {
                ReadingLetterTemplateVo templateInfo = readingLetterTemplateApi.getTemplateInfoWithoutLogin(template.getFallbackReadingLetterTemplateId());
                JSONObject readingLetterTemplateJsonObject = JSONObject.parseObject(templateInfo.getModuleInformation());
                if (Objects.nonNull(readingLetterTemplateJsonObject) && StrUtil.isNotBlank(readingLetterTemplateJsonObject.getString("sms"))) {
                    fileMessage.setSmsSupported(true);
                    fileMessage.setSmsContent(readingLetterTemplateJsonObject.getString("sms"));
                }
            }

            return fileMessage;
        } catch (Exception exception) {
            throw new BizException(exception.getMessage());
        }
    }


    private BaseMessage buildSupplierMessage(Long detailId, List<String> phoneNums, MessageTemplateResp messageTemplateResp, AccountManagementResp account,
                                             Map<Integer, String> variableMap, VariableData variableData, String messageId) {
        FontdoFileMessage fontdoFileMessage = new FontdoFileMessage();
        //!!重点,  supplierTag 供应商标识
        fontdoFileMessage.setSupplierTag(account.getSupplierTag());
        fontdoFileMessage.setNumbers(phoneNums);
        fontdoFileMessage.setMessageId(messageId);
        //这里是找到模板对应的Proved templateId
        MessageTemplateProvedReq templateProvedReq = new MessageTemplateProvedReq();
        templateProvedReq.setTemplateId(messageTemplateResp.getId());
        templateProvedReq.setSupplierTag(account.getSupplierTag());
        templateProvedReq.setOperator(account.getAccountTypeCode());
        templateProvedReq.setAccountType(account.getAccountType());
        MessageTemplateResp platformTemplate = messageTemplateApi.getProvedTemplate(templateProvedReq);
        //文字短信回落
        if (StrUtil.isNotBlank(messageTemplateResp.getFallbackSmsContent())) {
            FallbackSms fallbackSms = new FallbackSms();
            fallbackSms.setText(messageTemplateResp.getFallbackSmsContent());
            fontdoFileMessage.setFallbackSms(fallbackSms);
        }
        //阅信回落
        if (Objects.nonNull(messageTemplateResp.getFallbackReadingLetterTemplateId())) {
            String chatbotAccountId = account.getChatbotAccountId();
            Long fallbackReadingLetterTemplateId = messageTemplateResp.getFallbackReadingLetterTemplateId();
            //通过chatbotAccountId和fallbackReadingLetterTemplateId找到过审的platformTemplateId
            String platformTemplateId = readingLetterTemplateApi.getPlatformTemplateIdByAccountIdAndTemplateId(chatbotAccountId, fallbackReadingLetterTemplateId);
            if (StrUtil.isNotBlank(platformTemplateId)) {
                FallbackTemplate fallbackTemplate = new FallbackTemplate();
                fallbackTemplate.setTemplateId(platformTemplateId);
                fontdoFileMessage.setFallbackAim(fallbackTemplate);
            } else {
                log.warn("未找到过审的阅信模板,chatbotAccountId:{},fallbackReadingLetterTemplateId:{}", chatbotAccountId, fallbackReadingLetterTemplateId);
            }
        }

        //这里需要判断素材是否被删除/过期等
        validateTemplate(platformTemplate, account);

        fontdoFileMessage.setTemplateId(Long.parseLong(platformTemplate.getPlatformTemplateId()));
        //5G消息模板
        fontdoFileMessage.setTemplateType("RCS");
        fontdoFileMessage.setGroupSendPlanDetailId(detailId.toString());
        //这里是公共的方法所需要的参数
        fontdoFileMessage.setDestinationAddress(phoneNums);
        List<Parameter> parameters = getVariableParameter(variableMap, variableData);
        //必须添加detailId
        parameters.add(new Parameter("STRING", "detailId", detailId.toString()));
        fontdoFileMessage.setParams(parameters);

        return fontdoFileMessage;
    }

    //判断素材是否被删除或过期等
    private void validateTemplate(MessageTemplateResp template, AccountManagementResp account) {
        String moduleInformation = template.getModuleInformation();
        //获取模板的类型
        int type = template.getMessageType();
        JSONObject jsonObject = JSONObject.parseObject(moduleInformation, JSONObject.class);
        //验证所有资源类型
        switch (getType(type)) {
            //发送文件消息
            case "file":
                validateFile(jsonObject, account.getAccountType());
                break;
            //发送卡片消息
            case "card":
                JSONArray cardList = jsonObject.getJSONArray("cardList");
                //单卡
                if (CollectionUtil.isEmpty(cardList)) {
                    validateFile(jsonObject, account.getAccountType());
                } else {
                    //多卡
                    for (int i = 0; i < cardList.size(); i++) {
                        validateFile(cardList.getJSONObject(i), account.getAccountType());
                    }
                }
            default:
                break;
        }
    }

    private void validateFile(JSONObject jsonObject, String operator) {
        try {
            log.info("发送文件消息的节点信息为：{}", jsonObject);
            FileTidReq tidReq = new FileTidReq();
            tidReq.setOperator(operator);
            FileAccept fileInfo;
            if (StringUtils.isNotEmpty(jsonObject.getString("pictureUrlId"))) {
                tidReq.setFileUrlId(jsonObject.getString("pictureUrlId"));
                PictureReq pictureReq = new PictureReq();
                pictureReq.setPictureUrlId(jsonObject.getString("pictureUrlId"));
                fileInfo = platformApi.getFileTid(tidReq);
                if (Objects.isNull(fileInfo.getFileId())) {
                    throw new BizException("图片不存在或已过期");
                }
                try {
                    pictureApi.findByUuid(pictureReq);
                } catch (Exception e) {
                    log.error("图片不存在或已过期", e);
                    throw new BizException("图片不存在或已过期");
                }
            }
            if (StringUtils.isNotEmpty(jsonObject.getString("videoUrlId"))) {
                tidReq.setFileUrlId(jsonObject.getString("videoUrlId"));
                fileInfo = platformApi.getFileTid(tidReq);
                if (Objects.isNull(fileInfo.getFileId())) {
                    throw new BizException("视频不存在或已过期");
                }
                VideoReq req = new VideoReq();
                req.setVideoUrlId(jsonObject.getString("videoUrlId"));
                VideoResp videoResp = videoApi.findOneByUuid(req);
                if (Objects.isNull(videoResp.getId())) {
                    throw new BizException("视频不存在或已过期");
                }
            }
            if (StringUtils.isNotEmpty(jsonObject.getString("fileUrlId"))) {
                tidReq.setFileUrlId(jsonObject.getString("fileUrlId"));
                fileInfo = platformApi.getFileTid(tidReq);
                if (Objects.isNull(fileInfo.getFileId())) {
                    throw new BizException("文件不存在或已过期");
                }
            }
            if (StringUtils.isNotEmpty(jsonObject.getString("audioUrlId"))) {
                tidReq.setFileUrlId(jsonObject.getString("audioUrlId"));
                fileInfo = platformApi.getFileTid(tidReq);
                if (Objects.isNull(fileInfo.getFileId())) {
                    throw new BizException("音频不存在或已过期");
                }
            }
        } catch (Exception e) {
            log.error("文件资源不存在:", e);
            throw new BizException("文件资源不存在");
        }
    }


    private List<Parameter> getVariableParameter(Map<Integer, String> variableMap, VariableData variableData) {
        List<Parameter> parameters = new ArrayList<>();
        // 生成排序后的数字索引列表
        List<Integer> numIndexList = new ArrayList<>(variableMap.keySet());
        Collections.sort(numIndexList);
        // 构建参数列表
        String[] variableValues = variableData != null ? variableData.getVariables() : null;
        for (int index : numIndexList) {
            String uuid = variableMap.get(index);
            String value = "";
            if (variableValues != null && index - 1 < variableValues.length) {
                value = variableValues[index - 1]; // 假设variableValues是从0开始的
            }
            Parameter param = new Parameter("STRING", uuid, value);
            parameters.add(param);
        }
        return parameters;
    }


    private static JSONArray extractVariables(JSONObject jsonObject) {
        JSONArray jsonArray = new JSONArray();
        if (jsonObject.containsKey("description")) {
            jsonArray.addAll(jsonObject.getJSONObject("description").getJSONArray("names"));
        }
        if (jsonObject.containsKey("title")) {
            jsonArray.addAll(jsonObject.getJSONObject("title").getJSONArray("names"));
        }
        if (jsonObject.containsKey("input")) {
            jsonArray.addAll(jsonObject.getJSONObject("input").getJSONArray("names"));
        }
        return jsonArray;
    }

    private String getType(Integer type) {
        Map<Integer, String> map = new HashMap<>();
        map.put(1, "text");
        map.put(2, "file");
        map.put(3, "file");
        map.put(4, "file");
        map.put(5, "file");
        map.put(6, "card");
        map.put(7, "card");
        map.put(8, "location");
        return map.get(type);
    }


    private String getButtonType(Integer type) {
        Map<Integer, String> map = new HashMap<>();
        map.put(1, "reply");
        map.put(2, "urlAction");
        map.put(3, "openApp");
        map.put(4, "dialerAction");
        map.put(5, "ownMapAction");
        map.put(6, "mapAction");
        map.put(7, "composeVideoAction");
        map.put(8, "composeTextAction");
        map.put(9, "calendarAction");
        map.put(10, "deviceAction");
        map.put(11, "reply");
        map.put(12, "reply");
        map.put(13, "reply");
        map.put(14, "reply");
        return map.get(type);
    }

    private File getFile(String fileUuid) {
        DownloadReq downloadReq = new DownloadReq();
        downloadReq.setFileUUID(fileUuid);
        ResponseEntity<byte[]> responseEntity = fileApi.download(downloadReq);
        byte[] bytes = responseEntity.getBody();
        try {
            return MyFileUtil.bytesToFile(bytes, "css");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private List<Media> getMedias(Long detailId, JSONObject jsonObject, String operator, Integer cardNum) {
        List<Media> mediaList = new ArrayList<>();
        Media media = new Media();
        String description = jsonObject.getObject("description", JSONObject.class).getString("value");
        String title = jsonObject.getObject("title", JSONObject.class).getString("value");
        media.setTitle(title);
        media.setDescription(description);
        media.setContentDescription(description);
        media.setTitle(title);
        if (null != jsonObject.getJSONArray("buttonList")) {
            JSONArray btnArray = jsonObject.getJSONArray("buttonList");
            List<Suggestions> suggestions = buildButton(btnArray, detailId, 2, cardNum);
            FileIdResp fileIdResp = getFileTid(jsonObject, operator);
            media.setMediaId(fileIdResp.getFileTid());
            if (StringUtils.isNotEmpty(fileIdResp.getThumbnailId())) {
                media.setThumbnailId(fileIdResp.getThumbnailId());
            }
            media.setSuggestions(suggestions);
        }
        mediaList.add(media);
        return mediaList;
    }

    /**
     * 获取卡片的高度
     *
     * @param jsonObject
     * @param media
     */
    private void setCardHeight(JSONObject jsonObject, Media media) {
        String height = jsonObject.getString("height");
        //根据不同的高度匹配网关枚举
        switch (height) {
            case "max":
                media.setHeight("TALL_HEIGHT");
                break;
            case "min":
                media.setHeight("SHORT_HEIGHT");
                break;
            default:
                media.setHeight("MEDIUM_HEIGHT");
        }
    }

    private void setCardWidth(Layout layout, String width) {
        switch (width) {
            case "max":
                layout.setCardWidth("MEDIUM_WIDTH");
                break;
            case "min":
                layout.setCardWidth("SMALL_WIDTH");
                break;
            default:
                break;
        }
    }

    /**
     * 构建按钮信息
     *
     * @param buttonJson 按钮数组
     * @return 构建参数
     */
    private List<Suggestions> buildButton(JSONArray buttonJson, Long detailId, Integer btnType, Integer cardNum) {
        log.info("buttonJson is {}", buttonJson);
        List<Suggestions> suggestions = new ArrayList<>();
        if (!CollectionUtil.isEmpty(buttonJson)) {
            for (int i = 0; i < buttonJson.size(); i++) {
                Suggestions suggestion = new Suggestions();
                JSONObject button = buttonJson.getJSONObject(i);
                //按钮类型
                Integer type = button.getInteger("type");
                String buttonType = getButtonType(type);
                log.info("type is {}", type);
                log.info("buttonType is {}", buttonType);
                //按钮的内容
                Map<String, String> actionParams = getActionParams(buttonType, button);
                log.info("actionParams is {}", actionParams);
                String buttonText = button.getJSONObject("buttonDetail").getJSONObject("input").getString("value");
                String buttonUUID = button.getString("uuid");
                suggestion.setType(buttonType);
                //打开app设置为urlAction
                if (StringUtils.equals("openApp", buttonType)) {
                    suggestion.setType("urlAction");
                }
                //自定义数据加上detailId
                suggestion.setPostbackData(buttonUUID + "#&#&" + detailId + "#&#&" + type + "#&#&" + buttonText + "#&#&" + cardNum);
                suggestion.setDisplayText(buttonText);
                suggestion.setActionParams(actionParams);
                suggestions.add(suggestion);
            }
        }
        return suggestions;
    }

    /**
     * 获取按钮的信息
     *
     * @param buttonType 按钮的类型
     * @return 按钮信息map
     */
    private Map<String, String> getActionParams(String buttonType, JSONObject button) {
        Map<String, String> actionParams = new HashMap<>();
        JSONObject buttonDetail = button.getJSONObject("buttonDetail");
        //满足下面条件的不加参数 回复按钮、发送地址
        //根据不同按钮构建不通参数
        switch (buttonType) {
            case "mapAction":
                // 地址定位
                actionParams.put("latitude", buttonDetail.getString("localLatitude"));
                actionParams.put("longitude", buttonDetail.getString("localLongitude"));
                actionParams.put("label", buttonDetail.getJSONObject("localLocation").getString("name"));
                break;
            case "urlAction":
                //跳转链接
                Long formId = buttonDetail.getLong("formId");
                if (ObjectUtil.isEmpty(formId)) {
                    //为空就按照linkUrl
                    actionParams.put("url", buttonDetail.getString("linkUrl"));
                } else {
                    //构建表单url
                    actionParams.put("url", formShareUrl + formId);
                }
                if (buttonDetail.getInteger("localOpenMethod") == 1) {
                    actionParams.put("application", "webview");
                } else {
                    actionParams.put("application", "browser");
                }
                actionParams.put("viewMode", "full");
                break;
            case "dialerAction":
                //打电话
                actionParams.put("phoneNumber", buttonDetail.getString("phoneNum"));
                break;
            case "composeTextAction":
                //调起指定联系人
                actionParams.put("phoneNumber", buttonDetail.getString("targetPhoneNum"));
                actionParams.put("text", buttonDetail.getJSONObject("previewInput").getString("name"));
                break;
            case "composeVideoAction":
                //拍摄
                actionParams.put("phoneNumber", buttonDetail.getString("targetPhoneNumForPhoto"));
                break;
            //打开APP
            case "openApp":
                log.info("打开App按钮匹配");
                actionParams.put("url", buttonDetail.getString("linkUrl"));
                actionParams.put("application", "browser");
                actionParams.put("viewMode", "full");
                break;
            case "calendarAction":
                log.info("打开日历");
                actionParams.put("startTime", buildCalendar(buttonDetail.getString("calendarStartTime")));
                actionParams.put("endTime", buildCalendar(buttonDetail.getString("calendarEndTime")));
                actionParams.put("title", buttonDetail.getJSONObject("calendarTitle").getString("name"));
                actionParams.put("description", buttonDetail.getJSONObject("calendarTitle").getString("name"));
                break;
            default:
                return null;
        }
        return actionParams;
    }

    private String buildCalendar(String date) {
        date = date.replace(" ", "T");
        StringBuffer sb = new StringBuffer();
        sb.append(date);
        sb.append("Z");
        return sb.toString();
    }

    private FileIdResp getFileTid(JSONObject jsonObject, String operator) {
        try {
            log.info("发送文件消息的节点信息为：{}", jsonObject);
            String pictureUrlId = jsonObject.getString("pictureUrlId");
            String videoUrlId = jsonObject.getString("videoUrlId");
            String fileUrlId = jsonObject.getString("fileUrlId");
            String audioUrlId = jsonObject.getString("audioUrlId");
            String fileUuid =
                    (pictureUrlId != null && !pictureUrlId.isEmpty()) ? pictureUrlId :
                            (videoUrlId != null && !videoUrlId.isEmpty()) ? videoUrlId :
                                    (fileUrlId != null && !fileUrlId.isEmpty()) ? fileUrlId :
                                            (audioUrlId != null && !audioUrlId.isEmpty()) ? audioUrlId : null;
            FileIdResp fileIdResp = new FileIdResp();
            if (fileUuid != null) {
                FileTidReq tidReq = new FileTidReq();
                tidReq.setOperator(operator);
                tidReq.setFileUrlId(fileUuid);
                FileAccept fileAccept = platformApi.getFileTid(tidReq);
                fileIdResp.setFileTid(fileAccept.getFileId());
                fileIdResp.setThumbnailId(fileAccept.getThumbnailTid());
            }
            log.info("fileIdResp = fileTid:{},ThumbnailId:{}", fileIdResp.getFileTid(), fileIdResp.getThumbnailId());
            return fileIdResp;
        } catch (Exception e) {
            log.error("文件资源查询报错", e);
            throw new BizException("文件资源不存在");
        }
    }
}
