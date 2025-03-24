package com.citc.nce.im.broadcast.fastgroupmessage.service;

import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.citc.nce.auth.accountmanagement.AccountManagementApi;
import com.citc.nce.auth.accountmanagement.vo.AccountManagementOptionVo;
import com.citc.nce.auth.contactgroup.ContactGroupApi;
import com.citc.nce.auth.contactgroup.vo.ContactGroupTreeResp;
import com.citc.nce.auth.contactlist.vo.ContactListResp;
import com.citc.nce.auth.csp.msgTemplate.MsgTemplateApi;
import com.citc.nce.auth.csp.recharge.Const.PaymentTypeEnum;
import com.citc.nce.auth.csp.sms.account.CspSmsAccountApi;
import com.citc.nce.auth.csp.videoSms.account.CspVideoSmsAccountApi;
import com.citc.nce.auth.messagetemplate.MessageTemplateApi;
import com.citc.nce.auth.messagetemplate.vo.MessageTemplateResp;
import com.citc.nce.auth.prepayment.PrepaymentApi;
import com.citc.nce.authcenter.csp.CspCustomerApi;
import com.citc.nce.authcenter.csp.vo.CustomerDetailReq;
import com.citc.nce.authcenter.csp.vo.CustomerDetailResp;
import com.citc.nce.authcenter.csp.vo.UserInfoVo;
import com.citc.nce.common.core.enums.CustomerPayType;
import com.citc.nce.common.core.enums.MsgSubTypeEnum;
import com.citc.nce.common.core.enums.MsgTypeEnum;
import com.citc.nce.common.core.exception.BizException;
import com.citc.nce.common.core.pojo.BaseUser;
import com.citc.nce.common.core.pojo.PageResult;
import com.citc.nce.common.util.SessionContextUtil;
import com.citc.nce.common.util.SpringUtils;
import com.citc.nce.im.broadcast.BroadcastPlanService;
import com.citc.nce.im.broadcast.client.FifthSendClient;
import com.citc.nce.im.broadcast.exceptions.GroupPlanExecuteException;
import com.citc.nce.im.broadcast.fastgroupmessage.configure.FastGroupRocketMQConfigure;
import com.citc.nce.im.broadcast.fastgroupmessage.dao.FastGroupMessageMapper;
import com.citc.nce.im.broadcast.fastgroupmessage.entity.FastGroupMessage;
import com.citc.nce.im.broadcast.node.AbstractNode;
import com.citc.nce.im.broadcast.utils.BroadcastContactUtils;
import com.citc.nce.im.broadcast.utils.NodeFactory;
import com.citc.nce.im.entity.GroupNodeAccountDetail;
import com.citc.nce.im.service.impl.GroupNodeAccountDetailService;
import com.citc.nce.im.util.DateUtils;
import com.citc.nce.msgenum.DeliveryEnum;
import com.citc.nce.msgenum.SendStatus;
import com.citc.nce.robot.enums.FastGroupMessageStatus;
import com.citc.nce.robot.enums.MessageResourceType;
import com.citc.nce.robot.req.FastGroupMessageQueryReq;
import com.citc.nce.robot.req.FastGroupMessageReq;
import com.citc.nce.robot.res.FastGroupMessageItem;
import com.citc.nce.robot.res.FastGroupMessageSelectAllResp;
import com.citc.nce.tenant.MsgRecordApi;
import com.citc.nce.tenant.vo.resp.MsgRecordResp;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.apache.shardingsphere.transaction.annotation.ShardingSphereTransactionType;
import org.apache.shardingsphere.transaction.core.TransactionType;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.BeanUtils;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.citc.nce.common.core.enums.CustomerPayType.PREPAY;
import static com.citc.nce.im.broadcast.utils.BroadcastContactUtils.getContactPhones;

/**
 * @author jcrenc
 * @since 2024/6/25 14:28
 */
@RequiredArgsConstructor
@Slf4j
@Transactional(rollbackFor = Exception.class)
@Service
public class FastGroupMessageService extends ServiceImpl<FastGroupMessageMapper, FastGroupMessage> implements IService<FastGroupMessage> {
    private final PrepaymentApi prepaymentApi;
    private final GroupNodeAccountDetailService groupNodeAccountDetailService;
    private final RedissonClient redissonClient;
    private final MsgRecordApi msgRecordApi;
    private final AccountManagementApi accountManagementApi;
    private final CspVideoSmsAccountApi cspVideoSmsAccountApi;
    private final CspSmsAccountApi cspSmsAccountApi;
    private final ContactGroupApi contactGroupApi;

    private final FastGroupRocketMQConfigure rocketMQConfigure;
    private final RocketMQTemplate rocketMQTemplate;
    private final CspCustomerApi cspCustomerApi;
    private final MsgTemplateApi msgTemplateApi;
    private final MessageTemplateApi messageTemplateApi;

    private final BroadcastPlanService broadcastPlanService;

    public static final String START = "start";
    public static final String END = "end";

    /**
     * 创建并启动快捷群发
     */
    @ShardingSphereTransactionType(TransactionType.BASE)
    public void createAndStartFastGroupMessage(FastGroupMessageReq req) {
        BaseUser user = SessionContextUtil.getUser();
        String customerId = user.getUserId();
        //校验联系人组
        List<ContactListResp> contactPhones = getContactPhones(Collections.singletonList(req.getGroupId()))
                .getOrDefault(req.getGroupId(), Collections.emptyList());
        if (CollectionUtils.isEmpty(contactPhones))
            throw new GroupPlanExecuteException("联系人群组不能为空");

        FastGroupMessage fastGroupMessage = new FastGroupMessage()
                .setCustomerId(customerId)
                .setType(req.getType())
                .setAccounts(req.getAccounts())
                .setTemplateId(req.getTemplateId())
                .setGroupId(req.getGroupId())
                .setIsTiming(req.getIsTiming())
                .setSettingTime(req.getSettingTime() == null ? LocalDateTime.now() : req.getSettingTime())
                .setStatus(FastGroupMessageStatus.WAIT)
                .setDeducted(0);
        if (fastGroupMessage.getType() == MsgTypeEnum.M5G_MSG) {
            Integer messageType = messageTemplateApi.queryMessageTypeByTemplateId(fastGroupMessage.getTemplateId());
            fastGroupMessage.setSubType(MsgSubTypeEnum.convertTemplateType2MsgSubType(messageType));
        }
        //预付费可以选择扣费类型，后付费只能是余额
        if (user.getPayType() == CustomerPayType.PREPAY) {
            Assert.notNull(req.getPaymentTypeCode(), "预付费支付方式不能为空");
            fastGroupMessage.setPaymentType(req.getPaymentTypeCode());
        } else {
            fastGroupMessage.setPaymentType(PaymentTypeEnum.BALANCE.getCode());
        }

        // 将消息模版内容保存在数据库表中

        saveMessageTemplate(fastGroupMessage);

        save(fastGroupMessage);

        //扣除预付费资源
        AbstractNode node = NodeFactory.createNodeForFastGroup(fastGroupMessage, contactPhones.size());
        broadcastPlanService.balanceDeductForNodeForFastGroup(fastGroupMessage, node);

        //将快捷群发加入到mq中
        addFastGroupToMQ();
    }

    public void addFastGroupToMQ() {
        log.info("--------------------------------------------------更新待发送的快捷群发计划-------------------------------------------------------");
        //获取今天需要发送的快捷群发计划
        List<FastGroupMessage> fastGroupMessages = list(new LambdaQueryWrapper<FastGroupMessage>()
                .eq(FastGroupMessage::getStatus, FastGroupMessageStatus.WAIT)
                .and(wrapper -> wrapper.eq(FastGroupMessage::getIsTiming, 0)
                        .or()
                        .eq(FastGroupMessage::getIsTiming, 1)
                        .between(FastGroupMessage::getSettingTime, new Date(), DateUtils.getTomorrowEnd())));


        //异步发送
        if (!CollectionUtils.isEmpty(fastGroupMessages)) {
            //立即发送的快捷群发
            List<FastGroupMessage> nowList = fastGroupMessages.stream()
                    .filter(msg -> msg.getIsTiming() == 0)
                    .collect(Collectors.toList());
            //定时发送的快捷群发
            List<FastGroupMessage> stimingList = fastGroupMessages.stream()
                    .filter(msg -> msg.getIsTiming() == 1)
                    .collect(Collectors.toList());

            //定时发送
            if (!CollectionUtils.isEmpty(stimingList)) {
                //查询需要发送的订阅内容
                for (FastGroupMessage item : stimingList) {
                    //获取延迟时间
                    long ms = DateUtil.between(new Date(), DateUtils.LocalDateTimeToDate(item.getSettingTime()), DateUnit.MS);
                    log.info("FastGroupMessageService 将快捷群发发送到mq --定时-- 对象为 ：{}", item);
                    //将订阅的信息加载到mq中
                    String subscribeSendInfo = JSON.toJSONString(item);
                    Message<String> message = MessageBuilder.withPayload(subscribeSendInfo).build();
                    //同步发送该消息，获取发送结果
                    SendResult sendResult = rocketMQTemplate.syncSendDelayTimeMills(rocketMQConfigure.getTopic(), message, ms);
                    log.info("FastGroupMessageService 将快捷群发发送到mq --定时-- 结果为 ：{}", sendResult);
                }
            }


            //立即发送
            if (!CollectionUtils.isEmpty(nowList)) {
                //查询需要发送的订阅内容
                for (FastGroupMessage item : nowList) {
                    log.info("FastGroupMessageService 将快捷群发发送到mq --立即-- 对象为 ：{}", item);
                    //将订阅的信息加载到mq中
                    String subscribeSendInfo = JSON.toJSONString(item);
                    Message<String> message = MessageBuilder.withPayload(subscribeSendInfo).build();
                    //同步发送该消息，获取发送结果
                    SendResult sendResult = rocketMQTemplate.syncSend(rocketMQConfigure.getTopic(), message);
                    log.info("FastGroupMessageService 将快捷群发发送到mq --立即-- 结果为 ：{}", sendResult);
                }
            }
        }
    }

    /**
     * 异步发送快捷群发
     *
     * @param fastGroupMessage 群发
     */
    @ShardingSphereTransactionType(TransactionType.BASE)
    public void asyncSend(FastGroupMessage fastGroupMessage) {
        String failedReason = null;
        List<String> phones = new ArrayList<>();//可发送的手机号
        List<String> successPhones = new ArrayList<>();//发送成功的手机号
        AbstractNode node = null;

        RLock lock = redissonClient.getLock("fastGroupMessageLock:" + fastGroupMessage.getId());
        try {
            if (!lock.tryLock(500, TimeUnit.MILLISECONDS))
                return;
            //检查发送状态
            FastGroupMessage one = this.lambdaQuery().eq(FastGroupMessage::getId, fastGroupMessage.getId()).one();
            if (one != null && one.getStatus() == FastGroupMessageStatus.WAIT) {
                fastGroupMessage.setStatus(FastGroupMessageStatus.SENDING);
                fastGroupMessage.setSendTime(LocalDateTime.now());
                updateById(fastGroupMessage);//更新发送中状态

                List<ContactListResp> contactPhones = getContactPhones(Collections.singletonList(one.getGroupId()))
                        .getOrDefault(one.getGroupId(), Collections.emptyList());
                if (CollectionUtils.isEmpty(contactPhones))
                    throw new GroupPlanExecuteException("联系人群组不能为空");
                phones = BroadcastContactUtils.getCheckedContactPhones(fastGroupMessage.getCustomerId(), fastGroupMessage.getGroupId());
                node = NodeFactory.createNodeForFastGroup(fastGroupMessage, contactPhones.size());
                successPhones = node.send(phones);
                if (node.getType() == MsgTypeEnum.M5G_MSG && !successPhones.isEmpty() && successPhones.size() < phones.size()) {
                    FifthSendClient sendClient = SpringUtils.getBean(FifthSendClient.class);
                    MessageTemplateResp template = (MessageTemplateResp) node.getTemplate();
                    final List<String> sucCopy = new ArrayList<>(successPhones);
                    List<String> noSendAccountPhones = phones.stream()
                            .filter(phone -> !sucCopy.contains(phone))
                            .collect(Collectors.toList());
                    node.setSendNumber(node.getSendNumber() + noSendAccountPhones.size());
                    sendClient.saveMsgRecordForNoOperator(node.getPlanId(), node.getId(), noSendAccountPhones, template, MessageResourceType.FAST_GROUP, node.getCustomerId());
                }
                if (CollectionUtils.isEmpty(successPhones))
                    throw new GroupPlanExecuteException("无可用账号");
            } else {
                failedReason = "快捷群发状态异常";
            }
        } catch (Throwable throwable) {
            if (throwable instanceof InterruptedException)
                Thread.currentThread().interrupt();
            if (throwable instanceof GroupPlanExecuteException) {
                failedReason = ((GroupPlanExecuteException) throwable).getMsg();
                log.warn("快捷群发执行异常:{}", failedReason);
            } else {
                log.error("快捷群发执行未知异常", throwable);
                failedReason = "未知异常";
            }
            fastGroupMessage.setStatus(FastGroupMessageStatus.FAIL)
                    .setFailureReason(failedReason);
        } finally {
            if (lock.isLocked() && lock.isHeldByCurrentThread()) {
                //现在快捷群发的设计每次addFastGroupToMQ时都会把当天的待发送的快捷群发重复发送到mq，可能会导致重复消费，使用此判断跳过非待执行状态的快捷群发
                if (!Objects.equals(failedReason, "快捷群发状态异常")) {
                    if (Strings.isNotBlank(failedReason)) {
                        fastGroupMessage.setStatus(FastGroupMessageStatus.FAIL)
                                .setFailureReason(failedReason);
                    } else {
                        if (node != null) {
                            fastGroupMessage.setSendNumber(node.getSendNumber())
                                    .setUnknownNumber(successPhones.size())
                                    .setFailedNumber(phones.size() - successPhones.size())
                                    .setStatus(FastGroupMessageStatus.SUCCESS);
                            node.syncAccountSendDetail();
                        }
                    }
                    Map<String, AbstractNode.AccountSendDetail> accountSendDetail;
                    if (node != null && (accountSendDetail = node.getAccountSendDetail()) != null && checkUserIsPrepay(fastGroupMessage.getCustomerId())) {
                        backResource(fastGroupMessage, accountSendDetail);
                    }
                    updateById(fastGroupMessage);
                }
                lock.unlock();
            }
        }
    }

    private boolean checkUserIsPrepay(String customerId) {
        CustomerDetailReq req = new CustomerDetailReq();
        req.setCustomerId(customerId);
        CustomerDetailResp detailByCustomerId = cspCustomerApi.getDetailByCustomerId(req);
        return detailByCustomerId != null && detailByCustomerId.getPayType() == CustomerPayType.PREPAY;
    }

    /**
     * 处理快捷群发消息回调
     * 1. 更新消息状态统计信息
     */
    @Transactional(rollbackFor = Exception.class)
    public void handleSendStatistic(String messageId, Integer updatedRecordNumber, DeliveryEnum state) {
        MsgRecordResp record = msgRecordApi.queryRecordByMessageId(messageId);
        Long id = record.getPlanId();
        FastGroupMessage fastGroupMessage = this.getById(id);
        if (fastGroupMessage == null)
            return;
        switch (state) {
            case FAILED:
                fastGroupMessage.setUnknownNumber(fastGroupMessage.getUnknownNumber() - updatedRecordNumber);
                fastGroupMessage.setFailedNumber(fastGroupMessage.getFailedNumber() + updatedRecordNumber);
                break;
            case DELIVERED:
                fastGroupMessage.setUnknownNumber(fastGroupMessage.getUnknownNumber() - updatedRecordNumber);
                fastGroupMessage.setSuccessNumber(fastGroupMessage.getSuccessNumber() + updatedRecordNumber);
                break;
            case DELIVERED_TO_NETWORK:
                fastGroupMessage.setUnknownNumber(fastGroupMessage.getUnknownNumber() - updatedRecordNumber);
                fastGroupMessage.setSuccessNumber(fastGroupMessage.getSuccessNumber() + updatedRecordNumber);
                fastGroupMessage.setFallbackNumber(fastGroupMessage.getFallbackNumber() + updatedRecordNumber);
                break;
            default: {
                log.warn("无效快捷群发消息统计状态:{}", state);
                return;
            }
        }
        updateById(fastGroupMessage);
    }

    @Transactional(rollbackFor = Exception.class)
    public void refundByDeliveryState(String messageId, DeliveryEnum state, Integer sendNumber, String customerId) {
        if (sendNumber == null || sendNumber <= 0)
            return;
        MsgRecordResp record = msgRecordApi.queryRecordByMessageIdAndCustomerId(messageId, customerId).get(0);
        Long id = record.getPlanId();
        FastGroupMessage fastGroupMessage = this.getById(id);
        if (fastGroupMessage == null)
            return;
        UserInfoVo userInfoVo = cspCustomerApi.getByCustomerId(record.getCustomerId());
        if (userInfoVo.getPayType() != PREPAY)
            return;
        MsgSubTypeEnum subType = fastGroupMessage.getSubType();
        if (state == DeliveryEnum.FAILED) {
            prepaymentApi.returnRemaining(record.getAccountId(), fastGroupMessage.getType(), subType, sendNumber.longValue());
            log.info("返还网关回调失败消息:{}额度:{}成功", subType, sendNumber);
        } else if (state == DeliveryEnum.DELIVERED_TO_NETWORK) {
            prepaymentApi.returnRemaining(record.getAccountId(), fastGroupMessage.getType(), subType, sendNumber.longValue());
            log.info("返还网关回调转回落消息:{}额度:{}成功", subType, sendNumber);
        } else {
            log.warn("无需更新预付费客户消息额度状态:{}", state);
        }
    }

    @ShardingSphereTransactionType(TransactionType.BASE)
    @Transactional(rollbackFor = Exception.class)
    public void updateFastGroupMessage(FastGroupMessageReq req) {
        RLock lock = redissonClient.getLock("fastGroupMessageLock:" + req.getId());
        try {
            if (!lock.tryLock(500, TimeUnit.MILLISECONDS))
                return;
            FastGroupMessage fastGroupMessage = this.getById(req.getId());
            Assert.notNull(fastGroupMessage, "快捷群发不存在:" + req.getId());
            if (fastGroupMessage.getStatus() != FastGroupMessageStatus.WAIT)
                throw new BizException("快捷群发已启动，无法修改");
            List<ContactListResp> contactPhones = getContactPhones(Collections.singletonList(req.getGroupId()))
                    .getOrDefault(req.getGroupId(), Collections.emptyList());
            if (CollectionUtils.isEmpty(contactPhones))
                throw new GroupPlanExecuteException("联系人群组不能为空");
            //先返回资源再重新
            AbstractNode node = NodeFactory.createNodeForFastGroup(fastGroupMessage, contactPhones.size());
            broadcastPlanService.revertNodeResources(node, fastGroupMessage.getPaymentType());
            fastGroupMessage.setType(req.getType())
                    .setAccounts(req.getAccounts())
                    .setTemplateId(req.getTemplateId())
                    .setGroupId(req.getGroupId())
                    .setSettingTime(req.getSettingTime())
                    .setIsTiming(req.getIsTiming())
                    .setStatus(FastGroupMessageStatus.WAIT)
                    .setPaymentType(req.getPaymentTypeCode())
                    .setDeducted(0);
            updateById(fastGroupMessage); //先保存到数据库，这样当前事务里的其它查询才能意识到Deducted字段已经修改
            //重新构建节点进行扣费
            node = NodeFactory.createNodeForFastGroup(fastGroupMessage, contactPhones.size());
            //扣除预付费资源
            broadcastPlanService.balanceDeductForNodeForFastGroup(fastGroupMessage, node);

            // 将消息模版内容保存在数据库表中
            saveMessageTemplate(fastGroupMessage);

            updateById(fastGroupMessage);

            //将快捷群发加入到mq中
            addFastGroupToMQ();


        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new BizException("编辑失败");
        } finally {
            if (lock.isLocked() && lock.isHeldByCurrentThread())
                lock.unlock();
        }
    }

    private void saveMessageTemplate(FastGroupMessage fastGroupMessage) {
        MsgTypeEnum type = fastGroupMessage.getType();
        Long templateId = fastGroupMessage.getTemplateId();
        fastGroupMessage.setTemplateContent(msgTemplateApi.templateContentQuery(type, templateId, fastGroupMessage.getCustomerId()));

    }


    private void backResource(FastGroupMessage fastGroupMessage, Map<String, AbstractNode.AccountSendDetail> deductedDetail) {
        for (Map.Entry<String, AbstractNode.AccountSendDetail> entry : deductedDetail.entrySet()) {
            String accountId = entry.getKey();
            AbstractNode.AccountSendDetail detail = entry.getValue();
            int accountReturnNumber = detail.getPreemptedNumber() - Optional.ofNullable(detail.getSendNumber()).orElse(0);
            if (accountReturnNumber <= 0)
                continue;
            prepaymentApi.returnRemaining(accountId, fastGroupMessage.getType(), fastGroupMessage.getSubType(), (long) accountReturnNumber);
            detail.setReturnNumber(accountReturnNumber);
        }
    }


    private Map<String, AbstractNode.AccountSendDetail> queryDeductedDetail(long id) {
        return groupNodeAccountDetailService.lambdaQuery()
                .eq(GroupNodeAccountDetail::getNodeId, id)
                .list().stream()
                .collect(Collectors.toMap(
                        GroupNodeAccountDetail::getAccountId,
                        detail -> new AbstractNode.AccountSendDetail()
                                .setPreemptedNumber(detail.getPreemptedNumber())
                                .setSendNumber(detail.getActualSendNumber())
                                .setReturnNumber(detail.getReturnNumber())
                ));
    }


    public PageResult<FastGroupMessageItem> queryList(FastGroupMessageQueryReq req) {
        Page<FastGroupMessage> page = new Page<>(req.getPageNo(), req.getPageSize());
        Date sendStartTime = null;
        Date sendEndTime = null;
        Date createStartTime = null;
        Date createEndTime = null;
        if (req.getSendStartTime() != null && req.getSendEndTime() != null) {
            sendStartTime = DateUtils.obtainDayTime(START, DateUtils.obtainDate(req.getSendStartTime()));
            sendEndTime = DateUtils.obtainDayTime(END, DateUtils.obtainDate(req.getSendEndTime()));
        }

        if (req.getCreateStartTime() != null && req.getCreateEndTime() != null) {
            createStartTime = DateUtils.obtainDayTime(START, DateUtils.obtainDate(req.getCreateStartTime()));
            createEndTime = DateUtils.obtainDayTime(END, DateUtils.obtainDate(req.getCreateEndTime()));
        }

        page(page, new LambdaQueryWrapper<FastGroupMessage>()
                .eq(req.getType() != MsgTypeEnum.ALL, FastGroupMessage::getType, req.getType())
                .eq(req.getStatus() != FastGroupMessageStatus.ALL, FastGroupMessage::getStatus, req.getStatus())
                .like(StringUtils.hasLength(req.getAccount()), FastGroupMessage::getAccounts, req.getAccount())
                .eq(FastGroupMessage::getCustomerId, SessionContextUtil.getUserId())
                .between((sendStartTime != null && sendEndTime != null), FastGroupMessage::getSendTime, sendStartTime, sendEndTime)
                .between((createStartTime != null && createEndTime != null), FastGroupMessage::getCreateTime, createStartTime, createEndTime)
                .orderByDesc(FastGroupMessage::getCreateTime)
        );

        Map<String, String> accountMap = getAccountMap();
//        Map<String,String> messageTemplateMap = getMessageTemplateMap();
        Map<Long, String> groupNameMap = contactGroupApi.getTreeList().stream().collect(Collectors.toMap(ContactGroupTreeResp::getId, ContactGroupTreeResp::getGroupName));

        //查询操作人名称
        List<FastGroupMessageItem> result = page.getRecords().stream()
                .map(item -> {
                    FastGroupMessageItem fastGroupMessageItem = new FastGroupMessageItem();
                    BeanUtils.copyProperties(item, fastGroupMessageItem);
                    fastGroupMessageItem.setAccount(accountMap.get(item.getAccounts()));
                    fastGroupMessageItem.setGroupName(groupNameMap.get(item.getGroupId()));
                    fastGroupMessageItem.setStatus(item.getStatus().getCode());
                    fastGroupMessageItem.setFailureReason(item.getFailureReason());
                    return fastGroupMessageItem;
                }).collect(Collectors.toList());

        return new PageResult<>(result, page.getTotal());
    }

//    private Map<String, String> getMessageTemplateMap() {
//
//    }

    private Map<String, String> getAccountMap() {
        // 5G消息账号
        Map<String, String> accountMap = new HashMap<>();
        List<AccountManagementOptionVo> allAccountManagement = accountManagementApi.getAllAccountManagement(true);
        for (AccountManagementOptionVo item : allAccountManagement) {
            item.getOptions().forEach(option -> accountMap.put(option.getChatbotAccount(), option.getAccountName()));
        }
        // 视频短信账号
        Map<String, String> videoSmsAccountMap = cspVideoSmsAccountApi.queryAccountIdNameMapByCustomerId(SessionContextUtil.getUserId());
        if (!CollectionUtils.isEmpty(videoSmsAccountMap)) {
            accountMap.putAll(videoSmsAccountMap);
        }

        // 短信账号
        Map<String, String> smsAccountMap = cspSmsAccountApi.queryAccountIdNameMapByCustomerId(SessionContextUtil.getUserId());
        if (!CollectionUtils.isEmpty(smsAccountMap)) {
            accountMap.putAll(smsAccountMap);
        }
        return accountMap;
    }

    public FastGroupMessageSelectAllResp selectAll() {
        FastGroupMessageSelectAllResp resp = new FastGroupMessageSelectAllResp();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        List<FastGroupMessage> fastGroupMessageList = this.lambdaQuery()
                .eq(FastGroupMessage::getCreator, SessionContextUtil.getUserId())
                .eq(FastGroupMessage::getStatus, FastGroupMessageStatus.SUCCESS)
                .eq(FastGroupMessage::getType, MsgTypeEnum.M5G_MSG)
                .orderByDesc(FastGroupMessage::getCreateTime)
                .list();
        Map<Long, String> fastGroupMessageMap = new LinkedHashMap<>(fastGroupMessageList.size());
        fastGroupMessageList.forEach(fastGroupMessage ->
                fastGroupMessageMap.put(fastGroupMessage.getId(), String.format("%s/%s", fastGroupMessage.getType().getDesc(), formatter.format(fastGroupMessage.getSendTime())))
        );
        resp.setFastGroupMessageInfo(fastGroupMessageMap);
        return resp;
    }

    public void updateStatus(Long planId, String failedReason) {
        FastGroupMessage one = this.lambdaQuery().eq(FastGroupMessage::getId, planId).one();
        if (one != null) {
            one.setStatus(FastGroupMessageStatus.FAIL)
                    .setFailureReason(failedReason);
            updateById(one);
        }
    }

    public Long createStatistics(String customerId, Date startTime, Date endTime) {
        return this.lambdaQuery().eq(FastGroupMessage::getCustomerId, customerId)
                .between(FastGroupMessage::getCreateTime, startTime, endTime)
                .ne(FastGroupMessage::getStatus, FastGroupMessageStatus.WAIT).count();

    }

    public FastGroupMessage queryById(Long id) {
        return this.lambdaQuery().eq(FastGroupMessage::getId, id).one();
    }

    public FastGroupMessageItem findByPlanId(Long planId) {
        FastGroupMessageItem fastGroupMessageItem = new FastGroupMessageItem();
        FastGroupMessage fastGroupMessage = queryById(planId);
        if (fastGroupMessage != null) {
            BeanUtils.copyProperties(fastGroupMessage, fastGroupMessageItem);
        }
        return fastGroupMessageItem;
    }
}
