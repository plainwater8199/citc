package com.citc.nce.im.mq.service.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.citc.nce.auth.accountmanagement.AccountManagementApi;
import com.citc.nce.auth.accountmanagement.vo.AccountManagementResp;
import com.citc.nce.auth.csp.recharge.Const.*;
import com.citc.nce.auth.csp.recharge.RechargeTariffApi;
import com.citc.nce.auth.csp.recharge.vo.RechargeTariffDetailResp;
import com.citc.nce.auth.prepayment.DeductionAndRefundApi;
import com.citc.nce.auth.prepayment.vo.CreateChargeConsumeRecordReq;
import com.citc.nce.auth.prepayment.vo.ReceiveConfirmReq;
import com.citc.nce.auth.prepayment.vo.ReturnBalanceReq;
import com.citc.nce.common.constants.Constants;
import com.citc.nce.common.core.enums.MsgTypeEnum;
import com.citc.nce.common.core.exception.BizException;
import com.citc.nce.common.redis.config.RedisService;
import com.citc.nce.developer.DeveloperSendApi;
import com.citc.nce.developer.vo.DeveloperSend5gSaveDataVo;
import com.citc.nce.developer.vo.enums.DeveloperSendStatus;
import com.citc.nce.im.broadcast.BroadcastPlanService;
import com.citc.nce.im.broadcast.fastgroupmessage.service.FastGroupMessageService;
import com.citc.nce.im.entity.RobotClickResult;
import com.citc.nce.im.mapper.RobotClickResultMapper;
import com.citc.nce.im.mapper.RobotNodeResultMapper;
import com.citc.nce.im.mq.service.FontdoMsgSendResponseManage;
import com.citc.nce.im.service.RobotGroupSendPlanBindTaskService;
import com.citc.nce.im.service.RobotGroupSendPlansDetailService;
import com.citc.nce.msgenum.DeliveryEnum;
import com.citc.nce.msgenum.RequestEnum;
import com.citc.nce.robot.req.FontdoMessageStatusReq;
import com.citc.nce.robot.req.RobotGroupSendPlansDetailReq;
import com.citc.nce.robot.vo.RobotGroupSendPlanBindTask;
import com.citc.nce.robot.vo.RobotGroupSendPlansDetail;
import com.citc.nce.tenant.MsgRecordApi;
import com.citc.nce.tenant.vo.req.MsgRecordVo;
import com.citc.nce.tenant.vo.req.UpdateByPhoneAndMessageIdReq;
import com.citc.nce.tenant.vo.resp.MsgRecordResp;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
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
import java.util.Objects;

@Service
@Slf4j
public class FontdoMsgSendResponseManageImpl implements FontdoMsgSendResponseManage {

    @Resource
    RobotGroupSendPlansDetailService robotGroupSendPlansDetailService;

    @Resource
    RobotNodeResultMapper robotNodeResultMapper;

    @Resource
    MsgRecordApi msgRecordApi;

    @Resource
    RobotClickResultMapper clickResultMapper;

    @Resource
    RobotGroupSendPlanBindTaskService robotGroupSendPlanBindTaskService;

    @Resource
    DeductionAndRefundApi deductionAndRefundApi;

    @Resource
    RechargeTariffApi rechargeTariffApi;
    @Resource
    AccountManagementApi accountManagementApi;
    @Resource
    DeveloperSendApi developerSendApi;
    @Resource
    BroadcastPlanService broadcastPlanService;
    @Resource
    private RedisService redisService;
    @Resource
    private FastGroupMessageService fastGroupMessageService;

    private static final int SUCCESS = 4;//视频短信---成功
    private static final int FAIL = 5;//视频短信---失败
    private static final int TIME_OUT = 6;//视频短信---超时

    @Override
    @Transactional(rollbackFor = Exception.class)
    @ShardingSphereTransactionType(TransactionType.BASE)
    public void msgSendResponseAsynManageFor5G(FontdoMessageStatusReq statusReq) {
        try {
            log.info("-------供应商 5G消息状态报告--发送结果异步MQ消费-------{}", statusReq);
            MsgRecordVo msgRecordVoUpdate = new MsgRecordVo();
            String realMessageId = statusReq.getMsgId();
        /*  DELIVERED 已送达
            DISPLAYED 已读
            FALLBACK_SMS 回落短信
            FALLBACK_MMS 回落视信
            FAILED 失败
            */
            String status = statusReq.getStatus();
            String taskId = statusReq.getTaskId();
            String phoneNum = statusReq.getNumber();
            String appId = statusReq.getAppId();
            String failedReason = statusReq.translateErrorToFailReason();
            int updatedRecordNumber = 0; //更新发送明细的数量（可能是一条一条更新的，也可能是整个messageId的一个批次一起更新的）
            switch (status) {
                case "DELIVERED":
                    while (redisService.hasKey(String.format(Constants.MSG_RECORDS_INSERT_REDIS_KEY, statusReq.getTaskId()))) {
                        Thread.sleep(500);
                    }

                    msgRecordVoUpdate.setSendResult(DeliveryEnum.DELIVERED.getCode()).setReceiptTime(new Date());
                    msgRecordVoUpdate.setFinalResult(RequestEnum.SUCCESS.getCode());
                    //根据msgId去更新数据(包括msgId)
                    log.info("5G消息状态回调:TaskId:{},状态:DELIVERED", statusReq.getTaskId());
                    RobotGroupSendPlanBindTask bindDelivered = getOldMessageId(taskId, appId);
                    if (Objects.isNull(bindDelivered)) {
                        log.error("taskId:{} 未找到对应的oldMessageId", taskId);
                        return;
                    }
                    updatedRecordNumber = updateMsgRecordInfoIncludeMsgId(phoneNum, bindDelivered.getCustomerId(), taskId, appId, msgRecordVoUpdate, MsgTypeEnum.M5G_MSG.getCode());
                    //查找MsgRecord表,  如果是使用的余额付费, 需要修改扣费记录表中的行数据.
                    confirmDelivered(bindDelivered, phoneNum);

                    //更新发送计划统计详细信息(这里主要是更新成功/失败/未知的数量)
                    updateMassSendNodeStatisticInfo(bindDelivered.getOldMessageId(), updatedRecordNumber, "SUCCESS", bindDelivered.getCustomerId());
                    //处理开发者服务的记录
                    saveDeveloper(bindDelivered.getOldMessageId(), phoneNum, DeveloperSendStatus.DELIVRD.getValue());
                    //更新快捷群发发送明细
                    this.updateFastGroupMessageStatistic(bindDelivered.getOldMessageId(), updatedRecordNumber, DeliveryEnum.DELIVERED);
                    break;
                case "DISPLAYED":
                    //保存消息按钮点击信息
                    while (redisService.hasKey(String.format(Constants.MSG_RECORDS_INSERT_REDIS_KEY, statusReq.getTaskId()))) {
                        Thread.sleep(500);
                    }
                    RobotGroupSendPlanBindTask bindDisplayed = getOldMessageId(taskId, appId);
                    if (Objects.isNull(bindDisplayed)) {
                        log.error("taskId:{} 未找到对应的oldMessageId", taskId);
                        return;
                    }
                    saveRobotClickInfo(bindDisplayed.getOldMessageId(), bindDisplayed.getCustomerId());
                    //更新消息状态
                    msgRecordVoUpdate.setSendResult(DeliveryEnum.DISPLAYED.getCode());
                    msgRecordVoUpdate.setReadTime(new Date());
                    msgRecordVoUpdate.setFinalResult(RequestEnum.SUCCESS.getCode());


                    updateMsgRecordInfoIncludeMsgId(phoneNum, bindDisplayed.getCustomerId(), taskId, appId, msgRecordVoUpdate, MsgTypeEnum.M5G_MSG.getCode());
                    //updateMsgRecordInfo(phoneNum, realMessageId, taskId, msgRecordVoUpdate, MsgTypeEnum.M5G_MSG.getCode());
                    //处理开发者服务的记录
                    saveDeveloper(bindDisplayed.getOldMessageId(), phoneNum, DeveloperSendStatus.DISPLAYED.getValue());
                    break;
                case "FAILED":
                    //失败号码处理策略
                    while (redisService.hasKey(String.format(Constants.MSG_RECORDS_INSERT_REDIS_KEY, statusReq.getTaskId()))) {
                        Thread.sleep(500);
                    }
                    msgRecordVoUpdate.setSendResult(DeliveryEnum.FAILED.getCode())
                            .setReceiptTime(new Date())
                            .setFailedReason(failedReason)
                            .setFinalResult(RequestEnum.FAILED.getCode());

                    //根据taskId找到planDetailId
                    RobotGroupSendPlanBindTask bindFailed = getOldMessageId(taskId, appId);
                    if (Objects.isNull(bindFailed)) {
                        log.error("taskId:{} 未找到对应的oldMessageId", taskId);
                        return;
                    }

                    //根据msgId去更新 群发单条信息的状态
                    updatedRecordNumber = updateMsgRecordInfoIncludeMsgId(phoneNum, bindFailed.getCustomerId(), taskId, appId, msgRecordVoUpdate, MsgTypeEnum.M5G_MSG.getCode());


                    //查找MsgRecord表,  如果是使用的余额付费, 需要修改扣费记录表中的行数据.
                    //返回值用来确认是否需要进行套餐余额的处理
                    boolean needDealSetMealOfFail = returnBalanceOfChargeConsumeRecord(bindFailed, phoneNum);

                    saveDeveloper(bindFailed.getOldMessageId(), phoneNum, DeveloperSendStatus.FAIL.getValue());
                    //更新发送计划统计详细信息(这里主要是更新成功/失败/未知的数量)
                    updateMassSendNodeStatisticInfo(bindFailed.getOldMessageId(), updatedRecordNumber, "FAIL", bindFailed.getCustomerId());
                    //更新快捷群发发送明细
                    this.updateFastGroupMessageStatistic(bindFailed.getOldMessageId(), updatedRecordNumber, DeliveryEnum.FAILED);

                    if (needDealSetMealOfFail) {
                        broadcastPlanService.refundByDeliveryState(bindFailed.getOldMessageId(), DeliveryEnum.FAILED, updatedRecordNumber, bindFailed.getCustomerId());
                        fastGroupMessageService.refundByDeliveryState(bindFailed.getOldMessageId(), DeliveryEnum.FAILED, updatedRecordNumber, bindFailed.getCustomerId());
                    }
                    break;
                //回落短信
                case "FALLBACK_SMS":
                    while (redisService.hasKey(String.format(Constants.MSG_RECORDS_INSERT_REDIS_KEY, statusReq.getTaskId()))) {
                        Thread.sleep(500);
                    }
                    RobotGroupSendPlanBindTask bindFallbackSms = getOldMessageId(taskId, appId);
                    if (Objects.isNull(bindFallbackSms)) {
                        log.error("taskId:{} 未找到对应的oldMessageId", taskId);
                        return;
                    }
                    msgRecordVoUpdate
                            .setSendResult(DeliveryEnum.DELIVERED_TO_NETWORK.getCode())
                            .setReceiptTime(new Date())
                            .setFinalResult(RequestEnum.FALLBACK_SMS.getCode());
                    updatedRecordNumber = updateMsgRecordInfoIncludeMsgId(phoneNum, bindFallbackSms.getCustomerId(), taskId, appId, msgRecordVoUpdate, MsgTypeEnum.M5G_MSG.getCode());
                    //查找MsgRecord表,  如果是使用的余额付费, 需要修改扣费记录表中的行数据.
                    //返回值用来确认是否需要进行套餐余额的处理
                    boolean needDealSetMealOfFallbackSms = confirmFallback(bindFallbackSms, phoneNum);

                    //更新发送计划统计详细信息(这里主要是更新成功/失败/未知的数量)  5G消息回落为阅信/短信设置为发送成功
                    updateMassSendNodeStatisticInfo(bindFallbackSms.getOldMessageId(), updatedRecordNumber, "SUCCESS", bindFallbackSms.getCustomerId());
                    //增加开发者服务的记录
                    saveDeveloper(bindFallbackSms.getOldMessageId(), phoneNum, DeveloperSendStatus.DELIVRD.getValue());
                    //更新快捷群发发送明细
                    this.updateFastGroupMessageStatistic(bindFallbackSms.getOldMessageId(), updatedRecordNumber, DeliveryEnum.DELIVERED_TO_NETWORK);
                    //返一条5G消息余额
                    if (needDealSetMealOfFallbackSms) {
                        broadcastPlanService.refundByDeliveryState(bindFallbackSms.getOldMessageId(), DeliveryEnum.DELIVERED_TO_NETWORK, updatedRecordNumber, bindFallbackSms.getCustomerId());
                        fastGroupMessageService.refundByDeliveryState(bindFallbackSms.getOldMessageId(), DeliveryEnum.DELIVERED_TO_NETWORK, updatedRecordNumber, bindFallbackSms.getCustomerId());
                    }
                    log.info("供应商 5G消息状态回调:messageId:{},回落为短信", statusReq.getMsgId());
                    break;

                //回落视信
                case "FALLBACK_MMS":
                    msgRecordVoUpdate.setSendResult(DeliveryEnum.DELIVERED_TO_NETWORK.getCode()).setReceiptTime(new Date());
                    updateMsgRecordInfoIncludeMsgId(phoneNum, realMessageId, taskId, appId, msgRecordVoUpdate, MsgTypeEnum.M5G_MSG.getCode());
                    log.info("供应商 5G消息状态回调:messageId:{},回落为视信", statusReq.getMsgId());
                    break;
                default:
                    break;
            }
        } catch (Exception e) {
            if (e instanceof InterruptedException)
                Thread.currentThread().interrupt();
            log.error("FontdoMsgSendResponseManage.msgSendResponseAsynManageFor5G:"+e.getMessage(), e);
            throw new BizException(e.getMessage());
        }
    }

    /**
     * 更新快捷群发统计信息
     */
    private void updateFastGroupMessageStatistic(String messageId, Integer updatedRecordNumber, DeliveryEnum state) {
        if (StringUtils.isNotEmpty(messageId) && updatedRecordNumber != null && updatedRecordNumber > 0) {
            fastGroupMessageService.handleSendStatistic(messageId, updatedRecordNumber, state);
        }
    }

    private boolean confirmDelivered(RobotGroupSendPlanBindTask bindDelivered, String phoneNum) {
        String oldMessageId = bindDelivered.getOldMessageId();
        String customerId = bindDelivered.getCustomerId();
        MsgRecordResp msgRecordResp = msgRecordApi.queryRecordByMessageIdAndPhoneNumber(oldMessageId, phoneNum, customerId);
        if (Objects.isNull(msgRecordResp)) {
            log.error("未找到对应的msgRecord记录, oldMessageId:{}, phoneNum:{}", oldMessageId, phoneNum);
            return true;
        }
        //是扣余额,需要处理余额扣费记录
        if (Objects.equals(PaymentTypeEnum.BALANCE.getCode(), msgRecordResp.getConsumeCategory())) {
            int tariffType;
            //扣费(冻结)记录修改
            Integer messageType = msgRecordResp.getMessageType();
            if (messageType != 1 && messageType != 8) {
                tariffType = TariffTypeEnum.RICH_MEDIA_MESSAGE.getCode();
            } else {
                tariffType = TariffTypeEnum.TEXT_MESSAGE.getCode();
            }
            deductionAndRefundApi.receiveConfirm(ReceiveConfirmReq.builder()
                    .messageId(oldMessageId).customerId(customerId).tariffType(tariffType)
                    .phoneNumber(phoneNum).build());
            return false;
        } else {
            return true;
        }
    }

    //返回值用来确认是否需要进行套餐余额的处理
    private boolean returnBalanceOfChargeConsumeRecord(RobotGroupSendPlanBindTask bindFailed, String phoneNum) {
        String oldMessageIdFailed = bindFailed.getOldMessageId();
        String customerIdFailed = bindFailed.getCustomerId();
        MsgRecordResp msgRecordRespFailed = msgRecordApi.queryRecordByMessageIdAndPhoneNumber(oldMessageIdFailed, phoneNum, customerIdFailed);
        if (Objects.isNull(msgRecordRespFailed)) {
            log.error("未找到对应的msgRecord记录, oldMessageId:{}, phoneNum:{}", oldMessageIdFailed, phoneNum);
            return true;
        }
        //是扣余额,需要处理余额扣费记录
        if (Objects.equals(PaymentTypeEnum.BALANCE.getCode(), msgRecordRespFailed.getConsumeCategory())) {
            int tariffType;
            //扣费(冻结)记录修改
            String conversationId = msgRecordRespFailed.getConversationId();
            Integer messageType = msgRecordRespFailed.getMessageType();
            if (StrUtil.isNotBlank(conversationId)) {
                tariffType = TariffTypeEnum.SESSION_MESSAGE.getCode();
            } else if (messageType != 1 && messageType != 8) {
                tariffType = TariffTypeEnum.RICH_MEDIA_MESSAGE.getCode();
            } else {
                tariffType = TariffTypeEnum.TEXT_MESSAGE.getCode();
            }
            deductionAndRefundApi.returnBalance(ReturnBalanceReq.builder()
                    .messageId(oldMessageIdFailed).customerId(customerIdFailed).tariffType(tariffType)
                    .phoneNumber(phoneNum).build());
            return false;
        } else {
            return true;
        }

    }

    private boolean confirmFallback(RobotGroupSendPlanBindTask bindFallbackSms, String phoneNum) {

        //查找MsgRecord表,  如果是使用的余额付费, 需要修改扣费记录表中的行数据.
        String oldMessageIdFallbackSms = bindFallbackSms.getOldMessageId();
        String customerIdFallbackSms = bindFallbackSms.getCustomerId();
        MsgRecordResp msgRecordRespFallbackSms = msgRecordApi.queryRecordByMessageIdAndPhoneNumber(oldMessageIdFallbackSms, phoneNum, customerIdFallbackSms);
        if (Objects.isNull(msgRecordRespFallbackSms)) {
            log.error("未找到对应的msgRecord记录, oldMessageId:{}, phoneNum:{}", oldMessageIdFallbackSms, phoneNum);
            return true;
        }
        //是扣余额,需要处理余额扣费记录
        if (Objects.equals(PaymentTypeEnum.BALANCE.getCode(), msgRecordRespFallbackSms.getConsumeCategory())) {
            deductionAndRefundApi.receiveConfirm(ReceiveConfirmReq.builder()
                    .messageId(oldMessageIdFallbackSms).customerId(customerIdFallbackSms).tariffType(TariffTypeEnum.FALLBACK_SMS.getCode())
                    .phoneNumber(phoneNum).build());
            return false;
        } else {
            //把msgRecord的记录的消费类型consumeCategory更改为余额(防止多次计费)
            msgRecordApi.updateMsgRecordConsumeCategory(oldMessageIdFallbackSms, phoneNum, customerIdFallbackSms);
            //套餐用户的回落
            //找到chatbot信息
            AccountManagementResp chatbot = accountManagementApi.getAccountManagementByAccountId(bindFallbackSms.getAppId());
            //找到当前资费id及价格
            RechargeTariffDetailResp rechargeTariff = rechargeTariffApi.getRechargeTariff(chatbot.getChatbotAccountId());
            //在扣费记录表中添加一条回落记录
            deductionAndRefundApi.justCreateChargeConsumeRecord(CreateChargeConsumeRecordReq.builder()
                    .accountId(chatbot.getChatbotAccountId())
                    .customerId(customerIdFallbackSms)
                    .messageId(oldMessageIdFallbackSms)
                    .msgType(AccountTypeEnum.FIFTH_MESSAGES.getCode())
                    .payType(PayTypeEnum.POST_PAYMENT.getCode())
                    .phoneNumber(phoneNum)
                    .processed(ProcessStatusEnum.PROCESSED.getCode())
                    .price(rechargeTariff.getFallbackSmsPrice())
                    .tariffId(rechargeTariff.getId())
                    .tariffType(TariffTypeEnum.FALLBACK_SMS.getCode())
                    .build());

            if (redisService.hasKey(String.format(Constants.FIFTH_MSG_FALLBACK_READING_LETTER_KEY, oldMessageIdFallbackSms))) {
                //在扣费记录表中添加一条记录
                log.info("查询到5G消息发送messageId={}是选择的回落5G阅信,生成回落阅信解析扣费记录", oldMessageIdFallbackSms);
                deductionAndRefundApi.justCreateChargeConsumeRecord(CreateChargeConsumeRecordReq.builder()
                        .accountId(chatbot.getChatbotAccountId())
                        .customerId(customerIdFallbackSms)
                        .messageId(oldMessageIdFallbackSms)
                        .msgType(AccountTypeEnum.FIFTH_MESSAGES.getCode())
                        .payType(PayTypeEnum.POST_PAYMENT.getCode())
                        .phoneNumber(phoneNum)
                        .processed(ProcessStatusEnum.UNTREATED.getCode())
                        .price(rechargeTariff.getYxAnalysisPrice())
                        .tariffId(rechargeTariff.getId())
                        .tariffType(TariffTypeEnum.FIVE_G_READING_LETTER_PARSE.getCode())
                        .build());
            }
            return true;
        }
    }

    //根据taskId和ChatbotId 找到网关生成的messageId
    private RobotGroupSendPlanBindTask getOldMessageId(String taskId, String appId) {
        //根据taskId找到planDetailId
        RobotGroupSendPlanBindTask robotGroupSendPlanBindTask = robotGroupSendPlanBindTaskService.queryByTaskId(taskId, appId);
        //根据planDetailId找到对应的旧messageId
        if (Objects.nonNull(robotGroupSendPlanBindTask)) {
            return robotGroupSendPlanBindTask;
        }
        return null;
    }


    private void updateMassSendNodeStatisticInfo(String messageId, Integer updatedRecordNumber, String result, String customerId) {
        if (StringUtils.isNotEmpty(messageId) && updatedRecordNumber != null && updatedRecordNumber > 0) {

            //查到groupSendPlansDetail信息
            RobotGroupSendPlansDetailReq sendPlansDetailUpdate = queryMassSendNodeInfo(messageId, customerId);
            if (sendPlansDetailUpdate != null && sendPlansDetailUpdate.getUnknowAmount() != null && sendPlansDetailUpdate.getUnknowAmount() > 0) {
                if ("FAIL".equals(result)) {
                    //更新失败的数据
                    sendPlansDetailUpdate.setFailAmount(sendPlansDetailUpdate.getFailAmount() + updatedRecordNumber);
                } else {
                    //更新成功的数据
                    sendPlansDetailUpdate.setSuccessAmount(sendPlansDetailUpdate.getSuccessAmount() + updatedRecordNumber);
                }
                //更新未知的数据
                sendPlansDetailUpdate.setUnknowAmount(sendPlansDetailUpdate.getUnknowAmount() - updatedRecordNumber);
                log.info(sendPlansDetailUpdate.toString());
                robotGroupSendPlansDetailService.updateNodeDetail(sendPlansDetailUpdate);
            }
        }
    }

    //    处理开发者服务的记录
    private void saveDeveloper(String messageId, String phoneNum, Integer status) {
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

    /*
     * @describe 查到groupSendPlansDetail节点信息
     * @Param
     * @param messageId
     * @return com.citc.nce.robot.req.RobotGroupSendPlansDetailReq
     **/
    private RobotGroupSendPlansDetailReq queryMassSendNodeInfo(String messageId, String customerId) {
        RobotGroupSendPlansDetailReq groupSendPlansDetailUpdate = null;
        Long planDetailId = msgRecordApi.queryNodeIdByMessageId(messageId, customerId);
        log.info("messageId:{} ,planDetailId:{}", messageId, planDetailId);
        if (planDetailId != null) {
            RobotGroupSendPlansDetail robotGroupSendPlansDetail = robotGroupSendPlansDetailService.queryById(planDetailId);
            groupSendPlansDetailUpdate = new RobotGroupSendPlansDetailReq();
            groupSendPlansDetailUpdate.setId(planDetailId);
            groupSendPlansDetailUpdate.setUnknowAmount(robotGroupSendPlansDetail.getUnknowAmount());
            groupSendPlansDetailUpdate.setFailAmount(robotGroupSendPlansDetail.getFailAmount());
            groupSendPlansDetailUpdate.setSuccessAmount(robotGroupSendPlansDetail.getSuccessAmount());
            groupSendPlansDetailUpdate.setSendAmount(robotGroupSendPlansDetail.getSendAmount());
            log.info(groupSendPlansDetailUpdate.toString());
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
            RobotClickResult selectOne = clickResultMapper.selectOne(clickWrapper);
            //如果还没建立过, 那么就新建一个该对象
            if (ObjectUtil.isEmpty(selectOne)) {
                try {
                    Date day = dayFormat.parse(sendDay);
                    robotClickResult.setSendTimeDay(day);
                    robotClickResult.setSendTimeHour(sendHour);
                    robotClickResult.setPlanDetailId(planDetailId);
                    robotClickResult.setReadAmount(1L);
                    robotClickResult.setCreator("admin");
                    clickResultMapper.insert(robotClickResult);
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    /**
     * 更新消息记录表中的状态
     *
     * @param phone             手机号
     * @param customerId        customerId
     * @param msgRecordVoUpdate 待更新内容
     * @param msgType           消息类型
     */
    private Integer updateMsgRecordInfoIncludeMsgId(String phone, String customerId, String taskId, String appId, MsgRecordVo msgRecordVoUpdate, int msgType) {
        if (StringUtils.isNotEmpty(taskId)) {
            //如果是5G消息，手机号码需要截取
            if (MsgTypeEnum.M5G_MSG.getCode() == msgType && phone != null && phone.length() > 13) {
                phone = phone.substring(7);
            }
            String oldMessageId = null;
            //根据taskId找到planDetailId
            //暂时使用循环查询，后续可以优化
            for (int i = 0; i < 3; i++) {
                RobotGroupSendPlanBindTask robotGroupSendPlanBindTask = robotGroupSendPlanBindTaskService.queryByTaskId(taskId, appId);
                //根据planDetailId找到对应的旧messageId
                if (robotGroupSendPlanBindTask != null) {
                    oldMessageId = robotGroupSendPlanBindTask.getOldMessageId();
                    break;
                }
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    log.error(e.getMessage(), e);
                }
            }

            //查询条件
            UpdateByPhoneAndMessageIdReq req = new UpdateByPhoneAndMessageIdReq();
            req.setMsgType(msgType);
            req.setPhoneNum(phone);
            req.setMessageId(oldMessageId);
            req.setCustomerId(customerId);

            req.setMsgRecordVoUpdate(msgRecordVoUpdate);
            return msgRecordApi.updateByPhoneAndMessageId(req);
        }
        return 0;
    }

    /**
     * 更新消息记录表中的状态,不包括msgId
     *
     * @param phone             手机号
     * @param realMessageId     真实的消息ID
     * @param msgRecordVoUpdate 待更新内容
     * @param msgType           消息类型
     */
    private Integer updateMsgRecordInfoExcludeMsgId(String phone, String realMessageId, String taskId, String appId, MsgRecordVo msgRecordVoUpdate, int msgType) {
        if (StringUtils.isNotEmpty(taskId)) {
            //如果是5G消息，手机号码需要截取
            if (MsgTypeEnum.M5G_MSG.getCode() == msgType && phone != null && phone.length() > 13) {
                phone = phone.substring(7);
            }

            //查询条件
            UpdateByPhoneAndMessageIdReq req = new UpdateByPhoneAndMessageIdReq();
            req.setMsgType(msgType);
            req.setPhoneNum(phone);
            req.setMessageId(realMessageId);
            //set后的值,最终的MsgId,后续就可以直接根据msgId进行查询
            req.setMsgRecordVoUpdate(msgRecordVoUpdate);
            return msgRecordApi.updateByPhoneAndMessageId(req);
        }
        return 0;
    }

    /**
     * 更新消息记录表中的状态
     *
     * @param phone             手机号
     * @param realMessageId     真实的消息ID
     * @param msgRecordVoUpdate 待更新内容
     * @param msgType           消息类型
     */
    private Integer updateMsgRecordInfo(String phone, String realMessageId, String taskId, MsgRecordVo msgRecordVoUpdate, int msgType) {
        if (StringUtils.isNotEmpty(taskId)) {
            //如果是5G消息，手机号码需要截取
            if (MsgTypeEnum.M5G_MSG.getCode() == msgType && phone != null && phone.length() > 13) {
                phone = phone.substring(7);
            }
            UpdateByPhoneAndMessageIdReq req = new UpdateByPhoneAndMessageIdReq();
            req.setMsgType(msgType);
            req.setMessageId(realMessageId);
            req.setMsgRecordVoUpdate(msgRecordVoUpdate);
            return msgRecordApi.updateByPhoneAndMessageId(req);
        }
        return 0;
    }


    /**
     * 更新消息记录表中的状态
     *
     * @param phone             手机号
     * @param messageId         消息ID
     * @param msgRecordVoUpdate 待更新内容
     * @param msgType           消息类型
     */
    private Integer bindMsgStatusByTaskIdAndPhoneNumber(String phone, String taskId, String messageId, MsgRecordVo msgRecordVoUpdate, int msgType) {
        if (StringUtils.isNotEmpty(messageId)) {
            if (MsgTypeEnum.M5G_MSG.getCode() == msgType && phone != null && phone.length() > 13) {
                phone = phone.substring(7);
            }
            UpdateByPhoneAndMessageIdReq req = new UpdateByPhoneAndMessageIdReq();
            req.setMsgType(msgType);
            req.setMsgType(msgType);
            req.setPhoneNum(phone);
            req.setMessageId(messageId);
            req.setMsgRecordVoUpdate(msgRecordVoUpdate);
            return msgRecordApi.updateByPhoneAndMessageId(req);
        }
        return 0;
    }
}
