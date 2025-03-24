package com.citc.nce.im.mq.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.citc.nce.auth.accountmanagement.AccountManagementApi;
import com.citc.nce.auth.accountmanagement.vo.AccountManagementResp;
import com.citc.nce.auth.csp.recharge.Const.*;
import com.citc.nce.auth.csp.recharge.RechargeTariffApi;
import com.citc.nce.auth.csp.recharge.vo.RechargeTariffDetailResp;
import com.citc.nce.auth.csp.sms.account.CspSmsAccountApi;
import com.citc.nce.auth.csp.sms.account.vo.CspSmsAccountDeductResidueReq;
import com.citc.nce.auth.csp.sms.account.vo.CspSmsAccountDetailResp;
import com.citc.nce.auth.csp.sms.account.vo.CspSmsAccountQueryDetailReq;
import com.citc.nce.auth.csp.smsTemplate.SmsTemplateApi;
import com.citc.nce.auth.csp.smsTemplate.vo.SmsTemplateDetailVo;
import com.citc.nce.auth.csp.videoSms.account.CspVideoSmsAccountApi;
import com.citc.nce.auth.csp.videoSms.account.vo.CspVideoSmsAccountDeductResidueReq;
import com.citc.nce.auth.prepayment.DeductionAndRefundApi;
import com.citc.nce.auth.prepayment.PrepaymentApi;
import com.citc.nce.auth.prepayment.vo.CreateChargeConsumeRecordReq;
import com.citc.nce.auth.prepayment.vo.ReceiveConfirmReq;
import com.citc.nce.auth.prepayment.vo.ReturnBalanceBatchReq;
import com.citc.nce.auth.prepayment.vo.ReturnBalanceReq;
import com.citc.nce.authcenter.csp.CspCustomerApi;
import com.citc.nce.authcenter.csp.vo.UserInfoVo;
import com.citc.nce.common.constants.Constants;
import com.citc.nce.common.core.enums.CustomerPayType;
import com.citc.nce.common.core.enums.MsgTypeEnum;
import com.citc.nce.common.core.exception.BizException;
import com.citc.nce.common.redis.config.RedisService;
import com.citc.nce.developer.DeveloperSendApi;
import com.citc.nce.developer.vo.DeveloperSend5gSaveDataVo;
import com.citc.nce.developer.vo.DeveloperSendVideoSaveDataVo;
import com.citc.nce.developer.vo.enums.DeveloperSendStatus;
import com.citc.nce.im.broadcast.BroadcastPlanService;
import com.citc.nce.im.broadcast.fastgroupmessage.service.FastGroupMessageService;
import com.citc.nce.im.broadcast.utils.BroadcastPlanUtils;
import com.citc.nce.im.entity.RobotClickResult;
import com.citc.nce.im.exp.SendGroupExp;
import com.citc.nce.im.mapper.RobotClickResultMapper;
import com.citc.nce.im.mapper.RobotNodeResultMapper;
import com.citc.nce.im.mq.service.MsgSendResponseManage;
import com.citc.nce.im.service.RobotGroupSendPlansDetailService;
import com.citc.nce.im.session.entity.FifthDeliveryStatusDto;
import com.citc.nce.msgenum.DeliveryEnum;
import com.citc.nce.msgenum.RequestEnum;
import com.citc.nce.robot.req.RichMediaResultArray;
import com.citc.nce.robot.req.RobotGroupSendPlansDetailReq;
import com.citc.nce.robot.sms.ReportResponse;
import com.citc.nce.robot.vo.RobotGroupSendPlansDetail;
import com.citc.nce.robot.vo.VideoSmsResponse;
import com.citc.nce.tenant.MsgRecordApi;
import com.citc.nce.tenant.vo.req.MsgIdMappingVo;
import com.citc.nce.tenant.vo.req.MsgRecordVo;
import com.citc.nce.tenant.vo.req.UpdateByPhoneAndMessageIdReq;
import com.citc.nce.tenant.vo.resp.MsgRecordResp;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.util.Strings;
import org.apache.shardingsphere.transaction.annotation.ShardingSphereTransactionType;
import org.apache.shardingsphere.transaction.core.TransactionType;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Slf4j
public class MsgSendResponseManageImpl implements MsgSendResponseManage {
    @Resource
    RobotGroupSendPlansDetailService robotGroupSendPlansDetailService;
    @Resource
    RobotNodeResultMapper robotNodeResultMapper;
    @Resource
    MsgRecordApi msgRecordApi;
    @Resource
    CspVideoSmsAccountApi cspVideoSmsAccountApi;
    @Resource
    RedissonClient redissonClient;
    @Resource
    RobotClickResultMapper clickResultMapper;
    @Resource
    CspSmsAccountApi cspSmsAccountApi;
    @Resource
    SmsTemplateApi smsTemplateApi;
    @Resource
    DeveloperSendApi developerSendApi;
    @Resource
    private BroadcastPlanService broadcastPlanService;
    @Resource
    private PrepaymentApi prepaymentApi;
    @Resource
    private CspCustomerApi cspCustomerApi;
    @Resource
    private FastGroupMessageService fastGroupMessageService;

    @Resource
    private DeductionAndRefundApi deductionAndRefundApi;
    @Resource
    private AccountManagementApi accountManagementApi;
    @Resource
    private RechargeTariffApi rechargeTariffApi;
    @Resource
    private RedisService redisService;
    private static final int SUCCESS = 4;//视频短信---成功
    private static final int FAIL = 5;//视频短信---失败
    private static final int TIME_OUT = 6;//视频短信---超时

    @ShardingSphereTransactionType(TransactionType.BASE)
    @Transactional(rollbackFor = Exception.class)
    public void handleMessageStatusCallback(FifthDeliveryStatusDto deliveryInfo) {
        String platFormMessageId = deliveryInfo.getMessageId();
        String chatbotId = deliveryInfo.getChatbotId();
        String realChatbotId = getRealChatbotId(chatbotId);
        AccountManagementResp chatbot = accountManagementApi.getAccountManagementByAccountId(realChatbotId);
        String customerId = chatbot.getCustomerId();
        if (customerId == null) {
            log.error("未找到对应的chatbot账号信息, chatbotId:{}, realChatbotId:{}", chatbotId, realChatbotId);
            throw new BizException("chatbot账号信息");
        }

        MsgRecordVo msgRecordVoUpdate = new MsgRecordVo();
        String status = deliveryInfo.getStatus();
        String oldMessageId = deliveryInfo.getOldMessageId();
        String phoneNum = deliveryInfo.getSender();
        int updatedRecordNumber = 0; //更新发送明细的数量（可能是一条一条更新的，也可能是整个messageId的一个批次一起更新的）
        switch (status) {
            case "gatewaysent":
                if (StringUtils.isNotEmpty(deliveryInfo.getOldMessageId())) {
                    //生成mag_id_mapping
                    createMsgIdMapping(platFormMessageId, oldMessageId, customerId);
                }
                break;
            case "sent":
                break;
            case "delivered": {
                //查询本地的messageId
                String localMessageId = msgRecordApi.queryMsgIdMapping(platFormMessageId);
                msgRecordVoUpdate.setSendResult(DeliveryEnum.DELIVERED.getCode()).setReceiptTime(new Date());
                msgRecordVoUpdate.setFinalResult(RequestEnum.SUCCESS.getCode());
                updatedRecordNumber = updateMsgRecordInfo(phoneNum, localMessageId, msgRecordVoUpdate, MsgTypeEnum.M5G_MSG.getCode(), customerId);
                //查找MsgRecord表,  如果是使用的余额付费, 需要修改扣费记录表中的行数据.
                confirmDelivered(localMessageId, customerId, phoneNum);

                //更新发送计划统计详细信息
                updateMassSendNodeStatisticInfo(localMessageId, updatedRecordNumber, "SUCCESS", customerId);
                //保存开发者数据
                this.saveDeveloper(localMessageId, DeveloperSendStatus.DELIVRD.getValue(), deliveryInfo.getSender());
                //更新快捷群发发送明细
                this.updateFastGroupMessageStatistic(localMessageId, updatedRecordNumber, DeliveryEnum.DELIVERED);//问题water1118
            }
            break;
            case "displayed": {
                //查询本地的messageId
                String localMessageIdDisplayed = msgRecordApi.queryMsgIdMapping(platFormMessageId);
                //保存消息按钮点击信息
                saveRobotClickInfo(localMessageIdDisplayed, customerId);
                //更新消息状态
                msgRecordVoUpdate.setSendResult(DeliveryEnum.DISPLAYED.getCode());
                msgRecordVoUpdate.setReadTime(new Date());
                msgRecordVoUpdate.setFinalResult(RequestEnum.SUCCESS.getCode());
                updateMsgRecordInfo(phoneNum, localMessageIdDisplayed, msgRecordVoUpdate, MsgTypeEnum.M5G_MSG.getCode(), customerId);
                //保存开发者数据
                this.saveDeveloper(localMessageIdDisplayed, DeveloperSendStatus.DISPLAYED.getValue(), deliveryInfo.getSender());
            }
            break;
            case "failed": {

                // 如果messageId和sender都不为空，则视为批次中某个手机号下发失败
                // 如果messageId和sender都为空且oldMessageId不为空，则视为oldMessageId对应的整个批次下发失败
                String localMessageIdFailed;
                if (StringUtils.isNotEmpty(oldMessageId)) {
                    //网关直接发送失败时，返回的有oldMessageId
                    localMessageIdFailed = oldMessageId;
                } else {
                    //平台发送失败时，返回的没有oldMessageId
                    localMessageIdFailed = msgRecordApi.queryMsgIdMapping(platFormMessageId);
                }
                //失败号码处理策略
                msgRecordVoUpdate.setSendResult(DeliveryEnum.FAILED.getCode())
                        .setReceiptTime(new Date())
                        .setFailedReason(deliveryInfo.translateCodeToFailReason())
                        .setFinalResult(RequestEnum.FAILED.getCode());
                updatedRecordNumber = updateMsgRecordInfo(phoneNum, localMessageIdFailed, msgRecordVoUpdate, MsgTypeEnum.M5G_MSG.getCode(), customerId);

                //查找MsgRecord表, 如果是使用的余额付费, 需要修改扣费记录表中的行数据.
                boolean needDealSetMealOfFail = returnBalanceOfChargeConsumeRecord(localMessageIdFailed, customerId, phoneNum);

                //更新发送计划统计详细信息
                updateMassSendNodeStatisticInfo(localMessageIdFailed, updatedRecordNumber, "FAIL", customerId);
                //保存开发者数据
                this.saveDeveloper(localMessageIdFailed, DeveloperSendStatus.FAIL.getValue(), deliveryInfo.getSender());
                this.updateFastGroupMessageStatistic(localMessageIdFailed, updatedRecordNumber, DeliveryEnum.FAILED);
                if (needDealSetMealOfFail) {
                    broadcastPlanService.refundByDeliveryState(localMessageIdFailed, DeliveryEnum.FAILED, updatedRecordNumber, customerId);
                    fastGroupMessageService.refundByDeliveryState(localMessageIdFailed, DeliveryEnum.FAILED, updatedRecordNumber, customerId);
                }
            }
            break;
            case "deliveredToNetwork": {
                //查询本地的messageId
                String localMessageIdDeliveredToNetwork = msgRecordApi.queryMsgIdMapping(platFormMessageId);
                msgRecordVoUpdate.setSendResult(DeliveryEnum.DELIVERED_TO_NETWORK.getCode())
                        .setReceiptTime(new Date())
                        .setFinalResult(RequestEnum.FALLBACK_SMS.getCode());
                updatedRecordNumber = updateMsgRecordInfo(phoneNum, localMessageIdDeliveredToNetwork, msgRecordVoUpdate, MsgTypeEnum.M5G_MSG.getCode(), customerId);
                //在扣费记录表中处理本条记录
                boolean needDealSetMealOfDeliveredToNetwork = confirmFallback(localMessageIdDeliveredToNetwork, customerId, phoneNum);
                updateMassSendNodeStatisticInfo(localMessageIdDeliveredToNetwork, updatedRecordNumber, "SUCCESS", customerId);
                this.saveDeveloper(localMessageIdDeliveredToNetwork, DeveloperSendStatus.DELIVERED_TO_NETWORK.getValue(), deliveryInfo.getSender());
                this.updateFastGroupMessageStatistic(localMessageIdDeliveredToNetwork, updatedRecordNumber, DeliveryEnum.DELIVERED_TO_NETWORK);
                if (needDealSetMealOfDeliveredToNetwork) {
                    broadcastPlanService.refundByDeliveryState(localMessageIdDeliveredToNetwork, DeliveryEnum.DELIVERED_TO_NETWORK, updatedRecordNumber, customerId);
                    fastGroupMessageService.refundByDeliveryState(localMessageIdDeliveredToNetwork, DeliveryEnum.DELIVERED_TO_NETWORK, updatedRecordNumber, customerId);
                }
            }
            break;
            case "revokeFail": {
                log.info("消息撤回失败: msgId: {} phone: {}", platFormMessageId, phoneNum);
                break;
            }
            case "revokeOk": {
                msgRecordVoUpdate.setSendResult(DeliveryEnum.REVOKE_SUCCESS.getCode())
                        .setReceiptTime(new Date())
                        .setFailedReason(deliveryInfo.translateCodeToFailReason())
                        .setFinalResult(RequestEnum.FAILED.getCode());
                updatedRecordNumber = updateMsgRecordInfo(phoneNum, platFormMessageId, msgRecordVoUpdate, MsgTypeEnum.M5G_MSG.getCode(), customerId);
                boolean needDealSetMealOfFail = returnBalanceOfChargeConsumeRecord(platFormMessageId, customerId, phoneNum);

                //更新发送计划统计详细信息
                updateMassSendNodeStatisticInfo(platFormMessageId, updatedRecordNumber, "FAIL", customerId);
                //保存开发者数据
                this.saveDeveloper(platFormMessageId, DeveloperSendStatus.FAIL.getValue(), deliveryInfo.getSender());
                this.updateFastGroupMessageStatistic(platFormMessageId, updatedRecordNumber, DeliveryEnum.FAILED);
                if (needDealSetMealOfFail) {
                    broadcastPlanService.refundByDeliveryState(platFormMessageId, DeliveryEnum.FAILED, updatedRecordNumber, customerId);
                    fastGroupMessageService.refundByDeliveryState(platFormMessageId, DeliveryEnum.FAILED, updatedRecordNumber, customerId);
                }
                break;
            }
            default:
                break;
        }
    }

    private String getRealChatbotId(String chatbotId) {
        if (chatbotId.contains("sip:")) {
            chatbotId = chatbotId.replace("sip:", "");
            if (chatbotId.contains("@")) {
                String[] split1 = chatbotId.split("@");
                chatbotId = split1[0];
            }
        }
        return chatbotId;
    }

    private boolean confirmFallback(String localMessageId, String customerId, String phoneNum) {

        //查找MsgRecord表,  如果是使用的余额付费, 需要修改扣费记录表中的行数据.
        MsgRecordResp msgRecordRespFallbackSms = msgRecordApi.queryRecordByMessageIdAndPhoneNumber(localMessageId, phoneNum, customerId);
        if (Objects.isNull(msgRecordRespFallbackSms)) {
            log.error("未找到对应的msgRecord记录, oldMessageId:{}, phoneNum:{}", localMessageId, phoneNum);
            throw new BizException("未找到对应的msgRecord记录");
        }
        //是扣余额,需要处理余额扣费记录
        if (Objects.equals(PaymentTypeEnum.BALANCE.getCode(), msgRecordRespFallbackSms.getConsumeCategory())) {
            // 需要通过平台msgId找到对应的本地msgId
            deductionAndRefundApi.receiveConfirm(ReceiveConfirmReq.builder()
                    .messageId(localMessageId)
                    .customerId(customerId)
                    .tariffType(TariffTypeEnum.FALLBACK_SMS.getCode())
                    .phoneNumber(phoneNum)
                    .build());
            return false;
        } else {
            //把msgRecord的记录的消费类型consumeCategory更改为余额(防止多次计费)
            msgRecordApi.updateMsgRecordConsumeCategory(localMessageId, phoneNum, customerId);
            //找到chatbot信息
            AccountManagementResp chatbot = accountManagementApi.getAccountManagementByAccountId(msgRecordRespFallbackSms.getAccountId());
            //找到当前资费id及价格
            RechargeTariffDetailResp rechargeTariff = rechargeTariffApi.getRechargeTariff(chatbot.getChatbotAccountId());
            //在扣费记录表中添加一条记录
            deductionAndRefundApi.justCreateChargeConsumeRecord(CreateChargeConsumeRecordReq.builder()
                    .accountId(chatbot.getChatbotAccountId())
                    .customerId(chatbot.getCustomerId())
                    .messageId(msgRecordRespFallbackSms.getMessageId())
                    .msgType(AccountTypeEnum.FIFTH_MESSAGES.getCode())
                    .payType(PayTypeEnum.POST_PAYMENT.getCode())
                    .processed(ProcessStatusEnum.PROCESSED.getCode())
                    .phoneNumber(phoneNum)
                    .price(rechargeTariff.getFallbackSmsPrice())
                    .tariffId(rechargeTariff.getId())
                    .tariffType(TariffTypeEnum.FALLBACK_SMS.getCode())
                    .build());
            //在扣费记录表中添加一条记录
            if (redisService.hasKey(String.format(Constants.FIFTH_MSG_FALLBACK_READING_LETTER_KEY, msgRecordRespFallbackSms.getMessageId()))) {
                deductionAndRefundApi.justCreateChargeConsumeRecord(CreateChargeConsumeRecordReq.builder()
                        .accountId(chatbot.getChatbotAccountId())
                        .customerId(chatbot.getCustomerId())
                        .messageId(msgRecordRespFallbackSms.getMessageId())
                        .msgType(AccountTypeEnum.FIFTH_MESSAGES.getCode())
                        .payType(PayTypeEnum.POST_PAYMENT.getCode())
                        .processed(ProcessStatusEnum.UNTREATED.getCode())
                        .phoneNumber(phoneNum)
                        .price(rechargeTariff.getYxAnalysisPrice())
                        .tariffId(rechargeTariff.getId())
                        .tariffType(TariffTypeEnum.FIVE_G_READING_LETTER_PARSE.getCode())
                        .build());
            }
            return true;
        }
    }

    private boolean returnBalanceOfChargeConsumeRecord(String localMessageId, String customerId, String phoneNum) {
        List<MsgRecordResp> msgRecordRespFailed = CollectionUtil.newArrayList();
        if (phoneNum == null) {
            List<MsgRecordResp> msgRecordResps = msgRecordApi.queryRecordByMessageIdAndCustomerId(localMessageId, customerId);
            for (int i = 0; i < 10; i++) {
                if (CollectionUtil.isNotEmpty(msgRecordResps)) {
                    break;
                }
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException(e);
                }
                msgRecordResps = msgRecordApi.queryRecordByMessageIdAndCustomerId(localMessageId, customerId);
            }
            msgRecordRespFailed.addAll(msgRecordResps);
        } else {
            MsgRecordResp msgRecordResp = msgRecordApi.queryRecordByMessageIdAndPhoneNumber(localMessageId, phoneNum, customerId);
            for (int i = 0; i < 10; i++) {
                if (null != msgRecordResp) {
                    break;
                }
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException(e);
                }
                msgRecordResp = msgRecordApi.queryRecordByMessageIdAndPhoneNumber(localMessageId, phoneNum, customerId);
            }
            msgRecordRespFailed.add(msgRecordResp);
        }
        if (CollectionUtil.isEmpty(msgRecordRespFailed)) {
            log.error("未找到对应的msgRecord记录, localMessageId:{}, phoneNum:{}", localMessageId, phoneNum);
            throw new BizException("未找到对应的msgRecord记录");
        }

        MsgRecordResp msgRecordResp = msgRecordRespFailed.get(0);
        //是扣余额,需要处理余额扣费记录
        if (Objects.equals(PaymentTypeEnum.BALANCE.getCode(), msgRecordResp.getConsumeCategory())) {
            int tariffType;
            //扣费(冻结)记录修改
            Integer messageType = msgRecordResp.getMessageType();
            if (StrUtil.isNotBlank(msgRecordResp.getConversationId())) {
                tariffType = TariffTypeEnum.SESSION_MESSAGE.getCode();
            } else if (messageType != 1 && messageType != 8) {
                tariffType = TariffTypeEnum.RICH_MEDIA_MESSAGE.getCode();
            } else {
                tariffType = TariffTypeEnum.TEXT_MESSAGE.getCode();
            }

            deductionAndRefundApi.returnBalanceBatch(ReturnBalanceBatchReq.builder()
                    .messageId(localMessageId)
                    .customerId(customerId)
                    .tariffType(tariffType)
                    .phoneNumbers(msgRecordRespFailed.stream().map(MsgRecordResp::getPhoneNum).collect(Collectors.toList()))
                    .build());
            return false;
        } else {
            return true;
        }
    }

    private boolean confirmDelivered(String localMessageId, String customerId, String phoneNum) {
        log.info("confirm Delivered msgRecord记录, localMessageId:{}, phoneNum:{}, customerId:{}", localMessageId, phoneNum, customerId);
        MsgRecordResp msgRecordResp = msgRecordApi.queryRecordByMessageIdAndPhoneNumber(localMessageId, phoneNum, customerId);
        if (Objects.isNull(msgRecordResp)) {
            log.error("未找到对应的msgRecord记录, localMessageId:{}, phoneNum:{}", localMessageId, phoneNum);
            throw new BizException("未找到对应的msgRecord记录");
        }
        //是扣余额,需要处理余额扣费记录
        if (Objects.equals(PaymentTypeEnum.BALANCE.getCode(), msgRecordResp.getConsumeCategory())) {
            int tariffType;
            //扣费(冻结)记录修改
            String conversationId = msgRecordResp.getConversationId();
            Integer messageType = msgRecordResp.getMessageType();
            if (StrUtil.isNotBlank(conversationId)) {
                tariffType = TariffTypeEnum.SESSION_MESSAGE.getCode();
            } else if (messageType != 1 && messageType != 8) {
                tariffType = TariffTypeEnum.RICH_MEDIA_MESSAGE.getCode();
            } else {
                tariffType = TariffTypeEnum.TEXT_MESSAGE.getCode();
            }
            deductionAndRefundApi.receiveConfirm(ReceiveConfirmReq.builder()
                    .messageId(localMessageId)
                    .customerId(customerId)
                    .tariffType(tariffType)
                    .phoneNumber(phoneNum)
                    .build());
            return false;
        } else {
            return true;
        }
    }


    private void saveDeveloper(String messageId, Integer status, String phoneNum) {
        if (!Boolean.TRUE.equals(developerSendApi.isDeveloperMessage(messageId))) {
            return;
        }
        log.info("messageId：{} 匹配到开发者服务消息", messageId);
        DeveloperSend5gSaveDataVo developerSend5gSaveDataVo = new DeveloperSend5gSaveDataVo();
        developerSend5gSaveDataVo.setMessageId(messageId);
        developerSend5gSaveDataVo.setStatus(status);
        developerSend5gSaveDataVo.setPhoneNum(phoneNum);
        developerSendApi.save5gData(developerSend5gSaveDataVo);
    }

    /**
     * 更新快捷群发统计信息
     */
    private void updateFastGroupMessageStatistic(String messageId, Integer updatedRecordNumber, DeliveryEnum state) {
        if (StringUtils.isNotEmpty(messageId) && updatedRecordNumber != null && updatedRecordNumber > 0) {
            fastGroupMessageService.handleSendStatistic(messageId, updatedRecordNumber, state);
        }
    }

    /**
     * 更新群发统计信息
     */
    private void updateMassSendNodeStatisticInfo(String messageId, Integer updatedRecordNumber, String result, String customerId) {
        if (StringUtils.isNotEmpty(messageId) && updatedRecordNumber != null && updatedRecordNumber > 0) {
            RobotGroupSendPlansDetailReq sendPlansDetailUpdate = queryMassSendNodeInfo(messageId, customerId);
            if (sendPlansDetailUpdate != null && sendPlansDetailUpdate.getUnknowAmount() != null && sendPlansDetailUpdate.getUnknowAmount() > 0) {
                if ("FAIL".equals(result)) {//更新失败和成功的数据
                    sendPlansDetailUpdate.setFailAmount(sendPlansDetailUpdate.getFailAmount() + updatedRecordNumber);
                } else {
                    sendPlansDetailUpdate.setSuccessAmount(sendPlansDetailUpdate.getSuccessAmount() + updatedRecordNumber);
                }
                //更新未知的数据
                sendPlansDetailUpdate.setUnknowAmount(sendPlansDetailUpdate.getUnknowAmount() - updatedRecordNumber);
                robotGroupSendPlansDetailService.updateNodeDetail(sendPlansDetailUpdate);
            }
        }
    }

    private RobotGroupSendPlansDetailReq queryMassSendNodeInfo(String messageId, String customerId) {
        RobotGroupSendPlansDetailReq groupSendPlansDetailUpdate = null;
        Long planDetailId = msgRecordApi.queryNodeIdByMessageId(messageId, customerId);
        if (planDetailId != null) {
            RobotGroupSendPlansDetail robotGroupSendPlansDetail = robotGroupSendPlansDetailService.queryById(planDetailId);
            groupSendPlansDetailUpdate = new RobotGroupSendPlansDetailReq();
            groupSendPlansDetailUpdate.setId(planDetailId);
            groupSendPlansDetailUpdate.setUnknowAmount(robotGroupSendPlansDetail.getUnknowAmount());
            groupSendPlansDetailUpdate.setFailAmount(robotGroupSendPlansDetail.getFailAmount());
            groupSendPlansDetailUpdate.setSuccessAmount(robotGroupSendPlansDetail.getSuccessAmount());
            groupSendPlansDetailUpdate.setSendAmount(robotGroupSendPlansDetail.getSendAmount());
        }
        return groupSendPlansDetailUpdate;
    }

    /**
     * 保存消息被点击信息
     */
    private void saveRobotClickInfo(String messageId, String customerId) {
        if (StringUtils.isNotEmpty(messageId)) {
            Date date = new Date();
            SimpleDateFormat dayFormat = new SimpleDateFormat("yyyy-MM-dd");
            String sendDay = dayFormat.format(date);
            SimpleDateFormat hourFormat = new SimpleDateFormat("HH:00");
            String sendHour = hourFormat.format(date);
            Long planDetailId = msgRecordApi.queryNodeIdByMessageId(messageId, customerId);
            RobotClickResult robotClickResult = new RobotClickResult();
            LambdaQueryWrapper<RobotClickResult> clickWrapper = new LambdaQueryWrapper<>();
            clickWrapper.eq(RobotClickResult::getPlanDetailId, planDetailId);
            clickWrapper.eq(RobotClickResult::getSendTimeDay, sendDay);
            clickWrapper.eq(RobotClickResult::getSendTimeHour, sendHour);
            if (!clickResultMapper.exists(clickWrapper)) {
                try {
                    Date day = dayFormat.parse(sendDay);
                    robotClickResult.setSendTimeDay(day);
                    robotClickResult.setSendTimeHour(sendHour);
                    robotClickResult.setPlanDetailId(planDetailId);
                    robotClickResult.setReadAmount(1L);
                    robotClickResult.setCreator("admin");
                    clickResultMapper.insert(robotClickResult);
                } catch (ParseException e) {
                    log.info(e.getMessage());
                }
            }
        }
    }

    private void createMsgIdMapping(String messageId, String oldMessageId, String customerId) {
        if (StringUtils.isNotEmpty(messageId)) {
            MsgIdMappingVo msgIdMappingVo = new MsgIdMappingVo();
            msgIdMappingVo.setMessageId(oldMessageId);
            msgIdMappingVo.setPlatformMsgIds(CollectionUtil.newHashSet(messageId));
            msgIdMappingVo.setCustomerId(customerId);
            msgRecordApi.insertMsgIdMapping(msgIdMappingVo);
        }
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public void msgSendResponseAsynManageForMedia(RichMediaResultArray resultArray) {

        RLock lock = redissonClient.getLock("richMediaNotify");
        try {
            lock.lock();
            log.info("-------视频短信--发送结果异步MQ消费-------{}", resultArray);
            if (Boolean.TRUE.equals(resultArray.getSuccess())) {//请求成功
                List<VideoSmsResponse> result = resultArray.getResult();
                if (!result.isEmpty()) {//响应有结果
                    for (int i = 0; i < result.size(); i++) {
                        MsgRecordVo msgRecordVoUpdate = new MsgRecordVo();
                        VideoSmsResponse videoSmsResponse = result.get(i);
                        String messageId = videoSmsResponse.getVmsId();
                        Integer status = videoSmsResponse.getState();
                        String phoneNum = videoSmsResponse.getMobile();
                        int updatedRecordNumber = 0; //更新发送明细的数量（可能是一条一条更新的，也可能是整个messageId的一个批次一起更新的）
                        //检查消息状态是否已处理过
                        while (redisService.hasKey(String.format(Constants.MSG_RECORDS_INSERT_REDIS_KEY, messageId))) {
                            try {
                                Thread.sleep(500);
                            } catch (InterruptedException e) {
                                Thread.currentThread().interrupt();
                                throw new RuntimeException(e);
                            }
                        }

                        MsgIdMappingVo msgIdMappingVo = msgRecordApi.queryMsgMapping(messageId);

                        //查询消息记录
                        MsgRecordVo msgRecordVo = msgRecordApi.selectByPhoneAndMessageId(MsgTypeEnum.MEDIA_MSG.getCode(), phoneNum, messageId, msgIdMappingVo.getCustomerId());
                        if (msgRecordVo != null && msgRecordVo.getSendResult() == DeliveryEnum.UN_KNOW.getCode()) {
                            switch (status) {
                                case SUCCESS:
                                    //更新消息
                                    msgRecordVoUpdate.setSendResult(DeliveryEnum.DELIVERED.getCode()).setReceiptTime(new Date());
                                    msgRecordVoUpdate.setFinalResult(RequestEnum.SUCCESS.getCode());
                                    updatedRecordNumber = updateMsgRecordInfo(phoneNum, messageId, msgRecordVoUpdate, MsgTypeEnum.MEDIA_MSG.getCode(), null);
                                    confirmDelivered(msgRecordVo);
                                    //更新发送计划统计详细信息
                                    updateMassSendNodeStatisticInfo(messageId, updatedRecordNumber, "SUCCESS", msgRecordVo.getCreator());
                                    updateFastGroupMessageStatistic(messageId, updatedRecordNumber, DeliveryEnum.DELIVERED);
                                    break;
                                case FAIL:
                                case TIME_OUT:
                                    //更新视频短信账号的扣除信息
                                    String accountId = msgRecordVo.getAccountId();
                                    UserInfoVo userInfo = cspCustomerApi.getByCustomerId(msgRecordVo.getCreator());

                                    returnBalance(msgRecordVo, accountId, userInfo);
                                    //失败号码处理策略
                                    msgRecordVoUpdate.setSendResult(DeliveryEnum.FAILED.getCode()).setReceiptTime(new Date());
                                    msgRecordVoUpdate.setFinalResult(RequestEnum.FAILED.getCode());
                                    updatedRecordNumber = updateMsgRecordInfo(phoneNum, messageId, msgRecordVoUpdate, MsgTypeEnum.MEDIA_MSG.getCode(), null);
                                    //更新发送计划统计详细信息
                                    updateMassSendNodeStatisticInfo(messageId, updatedRecordNumber, "FAIL", msgRecordVo.getCreator());
                                    updateFastGroupMessageStatistic(messageId, updatedRecordNumber, DeliveryEnum.FAILED);
                                    break;
                                default:
                                    break;
                            }
                            //存储开发者数据
                            DeveloperSendVideoSaveDataVo developerSendVideoSaveDataVo = new DeveloperSendVideoSaveDataVo();
                            developerSendVideoSaveDataVo.setMessageId(messageId);
                            developerSendVideoSaveDataVo.setStatus(status);
                            developerSendVideoSaveDataVo.setPhoneNum(phoneNum);
                            developerSendApi.saveMediaData(developerSendVideoSaveDataVo);
                        } else {
                            log.warn("视频短信发送记录不存在或状态不匹配 messageId: {}", messageId);
                        }
                    }
                }
            }
        } catch (Exception e) {
            if (e instanceof InterruptedException)
                Thread.currentThread().interrupt();
            log.error("视频短信状态回调消费失败 reason:{}", e.getMessage(), e);
            throw new BizException(SendGroupExp.SQL_ERROR);
        } finally {
            //释放锁
            if (lock.isLocked() && lock.isHeldByCurrentThread())
                lock.unlock();
        }
    }


    private void confirmDelivered(MsgRecordVo msgRecordVo) {
        String platformMessageId = msgRecordVo.getMessageId();
        String customerId = msgRecordVo.getCreator();
        //是扣余额,需要处理余额扣费记录
        if (Objects.equals(PaymentTypeEnum.BALANCE.getCode(), msgRecordVo.getConsumeCategory())) {
            String localMessageId = msgRecordApi.queryMsgIdMapping(platformMessageId);
            if (Objects.isNull(localMessageId)) {
                log.error("未找到对应的msgIdMapping记录, msgRecordVo:{}", msgRecordVo);
                return;
            }
            deductionAndRefundApi.receiveConfirm(ReceiveConfirmReq.builder()
                    .messageId(localMessageId)
                    .customerId(customerId)
                    .tariffType(TariffTypeEnum.VIDEO_SMS.getCode())
                    .phoneNumber(msgRecordVo.getPhoneNum())
                    .build());
        }
    }

    private void returnBalance(MsgRecordVo msgRecordVo, String accountId, UserInfoVo userInfo) {
        String platformMessageId = msgRecordVo.getMessageId();
        String customerId = msgRecordVo.getCreator();
        //是扣余额,需要处理余额扣费记录
        if (Objects.equals(PaymentTypeEnum.BALANCE.getCode(), msgRecordVo.getConsumeCategory())) {
            String localMessageId = msgRecordApi.queryMsgIdMapping(platformMessageId);
            if (Objects.isNull(localMessageId)) {
                log.error("未找到对应的msgIdMapping记录, msgRecordVo:{}", msgRecordVo);
                return;
            }
            deductionAndRefundApi.returnBalance(ReturnBalanceReq.builder()
                    .messageId(localMessageId)
                    .customerId(customerId)
                    .tariffType(TariffTypeEnum.VIDEO_SMS.getCode())
                    .phoneNumber(msgRecordVo.getPhoneNum())
                    .build());
        } else {
            if (Strings.isNotEmpty(accountId)
                    && msgRecordVo.getSendResult() != DeliveryEnum.FAILED.getCode()
                    && userInfo.getPayType() == CustomerPayType.PREPAY) {
                prepaymentApi.returnRemaining(accountId, MsgTypeEnum.MEDIA_MSG, null, 1L);
            }
        }
    }


    /**
     * 更新消息记录表中的状态
     *
     * @param phone             手机号
     * @param messageId         消息ID
     * @param msgRecordVoUpdate 待更新内容
     * @param msgType           消息类型
     */
    private Integer updateMsgRecordInfo(String phone, String messageId, MsgRecordVo msgRecordVoUpdate, int msgType, String customerId) {
        if (StringUtils.isNotEmpty(messageId)) {
            if (MsgTypeEnum.M5G_MSG.getCode() == msgType && phone != null && phone.length() > 13) {
                phone = phone.substring(7);
            }
            UpdateByPhoneAndMessageIdReq req = new UpdateByPhoneAndMessageIdReq();
            req.setMsgType(msgType);
            req.setPhoneNum(phone);
            req.setMessageId(messageId);
            req.setCustomerId(customerId);
            req.setMsgRecordVoUpdate(msgRecordVoUpdate);
            return msgRecordApi.updateByPhoneAndMessageId(req);
        }
        return 0;
    }

    /**
     * 更新账号的扣除信息
     *
     * @param msgRecordVo 消息记录
     */
    private void updateMediaAccountDeduct(MsgRecordVo msgRecordVo, Integer msgType) {
        if (msgRecordVo != null && Strings.isNotEmpty(msgRecordVo.getAccountId()) && msgRecordVo.getSendResult() != DeliveryEnum.FAILED.getCode()) {
            MsgTypeEnum typeEnum = MsgTypeEnum.getValue(msgType);

            if (MsgTypeEnum.MEDIA_MSG.getCode() == msgType) {//视频短信扣除
                CspVideoSmsAccountDeductResidueReq cspOrderResidueReq = new CspVideoSmsAccountDeductResidueReq();
                cspOrderResidueReq.setAccountId(msgRecordVo.getAccountId());
                cspOrderResidueReq.setNum(1L);
                cspVideoSmsAccountApi.deductResidue(cspOrderResidueReq);
            } else if (MsgTypeEnum.SHORT_MSG.getCode() == msgType) {//短信扣除
                CspSmsAccountDeductResidueReq cspOrderResidueReq = new CspSmsAccountDeductResidueReq();
                cspOrderResidueReq.setAccountId(msgRecordVo.getAccountId());
                SmsTemplateDetailVo templateInfo = smsTemplateApi.getTemplateInfo(msgRecordVo.getTemplateId(), true);
                cspOrderResidueReq.setNum(Long.parseLong(BroadcastPlanUtils.getContentLength(templateInfo).toString()));
                cspSmsAccountApi.deductResidue(cspOrderResidueReq);
            }
        }
    }


    /**
     * 通过账号ID查询结果
     *
     * @param accountId 账号ID
     * @return 响应结果
     */
    private ReportResponse[] queryShortMsgSendStatusByAccountId(String accountId) {
        if (Strings.isNotEmpty(accountId)) {
            CspSmsAccountQueryDetailReq accountReq = new CspSmsAccountQueryDetailReq();
            accountReq.setAccountId(accountId);
            accountReq.setInnerCall(true);
            CspSmsAccountDetailResp accountDetail = cspSmsAccountApi.queryDetail(accountReq);
            if (accountDetail != null) {
                //使用推送的方式获取短信状态 此方法不应该再使用
                return null;
            }
        }
        return new ReportResponse[0];
    }

}
