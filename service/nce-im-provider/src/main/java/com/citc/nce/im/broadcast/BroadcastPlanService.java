package com.citc.nce.im.broadcast;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.citc.nce.auth.accountmanagement.AccountManagementApi;
import com.citc.nce.auth.accountmanagement.vo.AccountManagementResp;
import com.citc.nce.auth.contactlist.ContactListApi;
import com.citc.nce.auth.contactlist.vo.ContactListPageReq;
import com.citc.nce.auth.contactlist.vo.ContactListResp;
import com.citc.nce.auth.csp.recharge.Const.AccountTypeEnum;
import com.citc.nce.auth.csp.recharge.Const.PayTypeEnum;
import com.citc.nce.auth.csp.recharge.Const.PaymentTypeEnum;
import com.citc.nce.auth.csp.recharge.Const.TariffTypeEnum;
import com.citc.nce.auth.csp.recharge.RechargeTariffApi;
import com.citc.nce.auth.csp.recharge.vo.RechargeTariffDetailResp;
import com.citc.nce.auth.csp.recharge.vo.RechargeTariffExistReq;
import com.citc.nce.auth.csp.sms.account.CspSmsAccountApi;
import com.citc.nce.auth.csp.sms.account.vo.CspSmsAccountDetailResp;
import com.citc.nce.auth.csp.sms.account.vo.CspSmsAccountQueryDetailReq;
import com.citc.nce.auth.csp.smsTemplate.SmsTemplateApi;
import com.citc.nce.auth.csp.smsTemplate.vo.SmsTemplateDetailVo;
import com.citc.nce.auth.csp.videoSms.account.CspVideoSmsAccountApi;
import com.citc.nce.auth.csp.videoSms.account.vo.CspVideoSmsAccountDetailResp;
import com.citc.nce.auth.csp.videoSms.account.vo.CspVideoSmsAccountQueryDetailReq;
import com.citc.nce.auth.messagetemplate.vo.MessageTemplateResp;
import com.citc.nce.auth.prepayment.DeductionAndRefundApi;
import com.citc.nce.auth.prepayment.PrepaymentApi;
import com.citc.nce.auth.prepayment.vo.FeeDeductReq;
import com.citc.nce.auth.prepayment.vo.ReturnBalanceBatchReq;
import com.citc.nce.authcenter.constant.AuthCenterError;
import com.citc.nce.authcenter.csp.CspCustomerApi;
import com.citc.nce.authcenter.csp.vo.UserInfoVo;
import com.citc.nce.common.core.enums.CustomerPayType;
import com.citc.nce.common.core.enums.MsgSubTypeEnum;
import com.citc.nce.common.core.enums.MsgTypeEnum;
import com.citc.nce.common.core.exception.BizException;
import com.citc.nce.common.core.pojo.BaseUser;
import com.citc.nce.common.core.pojo.PageParam;
import com.citc.nce.common.util.PhoneFilterUtil;
import com.citc.nce.common.util.SessionContextUtil;
import com.citc.nce.common.util.SpringUtils;
import com.citc.nce.common.util.UUIDUtils;
import com.citc.nce.im.broadcast.client.FifthSendClient;
import com.citc.nce.im.broadcast.enums.GroupPlanAction;
import com.citc.nce.im.broadcast.exceptions.GroupPlanExecuteException;
import com.citc.nce.im.broadcast.exceptions.GroupPlanStoppedException;
import com.citc.nce.im.broadcast.fastgroupmessage.entity.FastGroupMessage;
import com.citc.nce.im.broadcast.fastgroupmessage.service.FastGroupMessageService;
import com.citc.nce.im.broadcast.node.AbstractNode;
import com.citc.nce.im.broadcast.node.BroadcastNode;
import com.citc.nce.im.broadcast.node.FifthNode;
import com.citc.nce.im.broadcast.utils.BroadcastPlanUtils;
import com.citc.nce.im.entity.GroupNodeAccountDetail;
import com.citc.nce.im.entity.RobotGroupSendPlansDetailDo;
import com.citc.nce.im.entity.RobotGroupSendPlansDo;
import com.citc.nce.im.mapper.GroupNodeAccountDetailMapper;
import com.citc.nce.im.service.RobotGroupSendPlansDetailService;
import com.citc.nce.im.service.RobotGroupSendPlansService;
import com.citc.nce.im.service.impl.GroupNodeAccountDetailService;
import com.citc.nce.msgenum.DeliveryEnum;
import com.citc.nce.msgenum.SendStatus;
import com.citc.nce.mybatis.core.entity.BaseDo;
import com.citc.nce.robot.api.MassSegmentApi;
import com.citc.nce.robot.constant.MessagePaymentTypeConstant;
import com.citc.nce.robot.enums.FastGroupMessageStatus;
import com.citc.nce.robot.enums.MessageResourceType;
import com.citc.nce.robot.vo.GroupSendValidResult;
import com.citc.nce.robot.vo.RobotGroupSendPlans;
import com.citc.nce.robot.vo.RobotGroupSendPlansDetail;
import com.citc.nce.tenant.MsgRecordApi;
import com.citc.nce.tenant.vo.resp.MsgRecordResp;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.shardingsphere.transaction.annotation.ShardingSphereTransactionType;
import org.apache.shardingsphere.transaction.core.TransactionType;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.citc.nce.common.core.enums.CustomerPayType.POSTPAY;
import static com.citc.nce.common.core.enums.CustomerPayType.PREPAY;
import static com.citc.nce.im.broadcast.utils.BroadcastAccountUtils.*;
import static com.citc.nce.im.broadcast.utils.BroadcastContactUtils.getCheckedContactPhones;
import static com.citc.nce.im.broadcast.utils.BroadcastPlanUtils.*;
import static com.citc.nce.im.broadcast.utils.BroadcastTemplateUtils.*;
import static com.citc.nce.msgenum.SendStatus.*;

/**
 * @author jcrenc
 * @since 2024/4/26 16:52
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class BroadcastPlanService {
    private final PrepaymentApi prepaymentApi;
    private final RedissonClient redissonClient;
    private final RobotGroupSendPlansService planService;
    private final CspCustomerApi cspCustomerApi;
    private final StringRedisTemplate redisTemplate;
    private final ContactListApi contactApi;
    private final AccountManagementApi accountManagementApi;
    private final CspSmsAccountApi smsAccountApi;
    private final CspVideoSmsAccountApi cspVideoSmsAccountApi;
    private final DeductionAndRefundApi deductionAndRefundApi;
    private final RechargeTariffApi rechargeTariffApi;
    private final SmsTemplateApi smsTemplateApi;
    private final MsgRecordApi msgRecordApi;
    private final GroupNodeAccountDetailService nodeAccountDetailService;
    private final MassSegmentApi massSegmentApi;
    private final GroupNodeAccountDetailMapper nodeAccountDetailMapper;
    private static final String FEE_DEDUCT_LOCK = "fee-deduct-lock-account-%s";

    @Resource(name = "broadcastTaskExecutor")
    private ThreadPoolTaskExecutor broadcastTaskExecutor;

    /**
     * 检测是否能启动计划和启动方式
     *
     * @param planId 需要启动的群发计划的ID
     * @return <消息类型-子类型,对应消息资源需求数和剩余数>
     */
    public Map<String, Map<String, Integer>> tryStartPlan(Long planId) {
        RobotGroupSendPlansDo plan = this.queryPlanByPlanIdAndStatus(planId, TO_BE_SEND, SEND_STOP);
        String customerId = plan.getCreator();
        if (SessionContextUtil.getUser().getPayType() != CustomerPayType.PREPAY)
            return Collections.emptyMap();
        Map<MsgTypeEnum, Map<MsgSubTypeEnum, Integer>> resourceRequirements = new HashMap<>();
        Map<String, Map<String, Integer>> result = new HashMap<>();
        List<AbstractNode> tree = getNodeTreeByPlanId(planId);
        BroadcastPlanUtils.applyExecutableNode(tree, node -> {
            Map<MsgSubTypeEnum, Integer> map = resourceRequirements.computeIfAbsent(node.getType(), k -> new HashMap<>());
            int chargeNum = 1;
            // 如果是发送的短信，要计算短信长度进行预估
            if (node.getType() == MsgTypeEnum.SHORT_MSG) {
                checkAndSettingSmsTemplate(node);
                SmsTemplateDetailVo template = (SmsTemplateDetailVo) node.getTemplate();
                chargeNum = BroadcastPlanUtils.getContentLength(template);
            }
            map.put(node.getSubType(), (map.getOrDefault(node.getSubType(), 0) + node.getMaxSendNumber()) * chargeNum);
            return true;
        });

        for (MsgTypeEnum type : resourceRequirements.keySet()) {
            List<String> accountIds = new ArrayList<>();
            // 如果是预付费用户，使用套餐进行群发，如果是发送5G消息也需要校验资费 bug#4063
            if (type == MsgTypeEnum.M5G_MSG) {
                List<String> chatbotAccountList = resolveAccountIds(plan, MsgTypeEnum.M5G_MSG);
                for (String account : chatbotAccountList) {
                    RechargeTariffExistReq req = new RechargeTariffExistReq();
                    req.setAccountId(account);
                    req.setAccountType(MsgTypeEnum.M5G_MSG.getCode());
                    RechargeTariffDetailResp resp = rechargeTariffApi.checkRechargeTariffExist(req);
                    if (resp == null) {
                        AccountManagementResp chatbotAccount = accountManagementApi.getAccountManagementByAccountId(account);
                        throw new BizException(String.format("您的账号%s未设置资费，请联系您的服务商进行设置", chatbotAccount.getAccountName()));
                    }
                }
                accountIds = chatbotAccountList;
            }
            if (type == MsgTypeEnum.MEDIA_MSG) {
                accountIds = resolveAccountIds(plan, MsgTypeEnum.MEDIA_MSG);
            }
            if (type == MsgTypeEnum.SHORT_MSG) {
                accountIds = resolveAccountIds(plan, MsgTypeEnum.SHORT_MSG);
            }
            Map<MsgSubTypeEnum, Integer> requirementMap = resourceRequirements.get(type);
            for (MsgSubTypeEnum subType : requirementMap.keySet()) {
                String key = subType == null
                        ? type.getDesc()
                        : type.getDesc() + "-" + subType.getDesc();
                Long remaining = prepaymentApi.getRemainingCountTest(customerId, type, subType, accountIds);
                Map<String, Integer> detail = result.computeIfAbsent(key, k -> new HashMap<>());
                detail.put("requirement", requirementMap.get(subType));
                // 如果套餐没有数据，则默认剩余数量为0
                detail.put("remaining", remaining != null ? remaining.intValue() : 0);
            }
        }
        plan.setConsumeCategory(PaymentTypeEnum.SET_MEAL.getCode());
        planService.updateById(plan);
        return result;
    }

    /**
     * 检测是否能使用余额启动计划和启动方式
     *
     * @param planId 需要启动的群发计划的ID
     */
    public GroupSendValidResult tryBalanceStartPlan(Long planId) {
        GroupSendValidResult result = new GroupSendValidResult();
        result.setValidResult(false);
        // 1.使用的账号是否全部设置单价
        RobotGroupSendPlansDo plan = this.queryPlanByPlanIdAndStatus(planId, TO_BE_SEND, SEND_STOP);
        List<String> chatbotAccountList = resolveAccountIds(plan, MsgTypeEnum.M5G_MSG);
        List<String> shortMsgAccountList = resolveAccountIds(plan, MsgTypeEnum.SHORT_MSG);
        List<String> richMediaAccountList = resolveAccountIds(plan, MsgTypeEnum.MEDIA_MSG);
        Map<String, RechargeTariffDetailResp> accountIdTariffDetailMap = new HashMap<>();
        // 校验5G消息单价
        if (!Objects.equals(chatbotAccountList.get(0), "")) {
            for (String account : chatbotAccountList) {
                RechargeTariffExistReq req = new RechargeTariffExistReq();
                req.setAccountId(account);
                req.setAccountType(MsgTypeEnum.M5G_MSG.getCode());
                RechargeTariffDetailResp resp = rechargeTariffApi.checkRechargeTariffExist(req);
                if (resp == null) {
                    AccountManagementResp chatbotAccount = accountManagementApi.getAccountManagementByAccountId(account);
                    result.setNoticeMsg(String.format("您的账号%s未设置单价，请联系您的服务商进行设置", chatbotAccount.getAccountName()));
                    return result;
                }
                accountIdTariffDetailMap.put(account, resp);
            }
        }
        // 校验短信单价
        if (!Objects.equals(shortMsgAccountList.get(0), "")) {
            for (String account : shortMsgAccountList) {
                RechargeTariffExistReq req = new RechargeTariffExistReq();
                req.setAccountId(account);
                req.setAccountType(MsgTypeEnum.SHORT_MSG.getCode());
                RechargeTariffDetailResp resp = rechargeTariffApi.checkRechargeTariffExist(req);
                if (resp == null) {
                    CspSmsAccountQueryDetailReq smsDetailReq = new CspSmsAccountQueryDetailReq();
                    smsDetailReq.setAccountId(account);
                    CspSmsAccountDetailResp smsAccountDetailResp = smsAccountApi.queryDetail(smsDetailReq);
                    result.setNoticeMsg(String.format("您的账号%s未设置单价，请联系您的服务商进行设置", smsAccountDetailResp.getAccountName()));
                    return result;
                }
                accountIdTariffDetailMap.put(account, resp);
            }
        }
        // 校验视频短信单价
        if (!Objects.equals(richMediaAccountList.get(0), "")) {
            for (String account : richMediaAccountList) {
                RechargeTariffExistReq req = new RechargeTariffExistReq();
                req.setAccountId(account);
                req.setAccountType(MsgTypeEnum.MEDIA_MSG.getCode());
                RechargeTariffDetailResp resp = rechargeTariffApi.checkRechargeTariffExist(req);
                if (resp == null) {
                    CspVideoSmsAccountQueryDetailReq videoSmsDetailReq = new CspVideoSmsAccountQueryDetailReq();
                    videoSmsDetailReq.setAccountId(account);
                    CspVideoSmsAccountDetailResp videoSmsAccountDetailResp = cspVideoSmsAccountApi.queryDetail(videoSmsDetailReq);
                    result.setNoticeMsg(String.format("您的账号%s未设置单价，请联系您的服务商进行设置", videoSmsAccountDetailResp.getAccountName()));
                    return result;
                }
                accountIdTariffDetailMap.put(account, resp);
            }
        }
        log.info("群发发送校验: 账号MAP为{}", accountIdTariffDetailMap);
        // 2.使用的账号余额是否为0
        String customerId = plan.getCreator();
        UserInfoVo byCustomerId = cspCustomerApi.getByCustomerId(customerId);
        Long balance = byCustomerId.getBalance();
        if (balance == 0L) {
            result.setNoticeMsg("您的账号余额为0，请充值");
            return result;
        }

        // 3.获取整体计划所需的最大金额
        // 查询节点树
        List<AbstractNode> tree = getNodeTreeByPlanId(plan.getId());
        List<AbstractNode> nodeList = BroadcastPlanUtils.flatNodeTree(tree);
        long planAmount = 0L;
        // 遍历nodeList，累加资费
        for (AbstractNode node : nodeList) {
            // 只过滤需要扣费的节点
            if (!EXCEPT_DEDUCT_NODE_STATUS.contains(node.getStatus())) {
                continue;
            }
            int nodeAmount = 0;
            MsgTypeEnum type = node.getType();
            MsgSubTypeEnum subType = node.getSubType();
            List<String> accountIds = resolveAccountIds(plan, type);
            log.info("当前节点，节点类型为:{}, 账号集为:{}", type, accountIds);
            List<String> sendPhones = this.querySendPhones(node, node.getParent());
            for (String accountId : accountIds) {
                // 5g消息会有多个accountId,需要计算每个accountId对应发送的数量，再统计资费
                if (MsgTypeEnum.M5G_MSG == type) {
                    AccountManagementResp chatbotAccount = accountManagementApi.getAccountManagementByAccountId(accountId);
                    Integer accountTypeCode = chatbotAccount.getAccountTypeCode();
                    Map<String, String> allSegment = massSegmentApi.queryAllSegment();
                    List<String> selectedPhones = PhoneFilterUtil.createSelectedPhones(sendPhones, accountTypeCode.toString(), allSegment);
                    // 默认是发送价格
                    log.info("当前节点，节点类型为:{}, 账号为:{}, 子类型为:{}", type, accountId, subType);
                    log.info("账号总体资费为:{}", accountIdTariffDetailMap.get(accountId));
                    log.info("账号类型资费为:{}", accountIdTariffDetailMap.get(accountId).getPriceOfTariffType(subType.getCode()));
                    int msgPrice = accountIdTariffDetailMap.get(accountId).getPriceOfTariffType(subType.getCode());
                    if (node.getAllowFallback() == 1) { // 允许回落的情况下，取的是发送和回落比较的最大值
                        // 发送价格
                        int sendPrice = accountIdTariffDetailMap.get(accountId).getPriceOfTariffType(subType.getCode());
                        // 回落价格 如果是回落短信 取短信价格 回落是5G阅信 取短信+5G阅信解析价格 fallbackType为1的时候为回落短信
                        int fallbackPrice = accountIdTariffDetailMap.get(accountId).getPriceOfTariffType(MsgSubTypeEnum.FALLBACK.getCode()) +
                                (node.getFallbackType() == 1 ? 0 :
                                        Optional.ofNullable(accountIdTariffDetailMap.get(accountId).getPriceOfTariffType(MsgSubTypeEnum.DELIVER_YX.getCode())).orElse(0));
                        msgPrice = Math.max(sendPrice, fallbackPrice);
                    }
                    nodeAmount += msgPrice * selectedPhones.size();
                } else if (MsgTypeEnum.SHORT_MSG == type) {
                    // 如果是发送的短信，要计算短信长度进行预估
                    // 先给node设置模板
                    checkAndSettingSmsTemplate(node);
                    SmsTemplateDetailVo template = (SmsTemplateDetailVo) node.getTemplate();
                    Integer chargeNum = BroadcastPlanUtils.getContentLength(template);
                    nodeAmount += accountIdTariffDetailMap.get(accountId).getSmsPrice() * node.getMaxSendNumber() * chargeNum;
                } else {
                    nodeAmount += accountIdTariffDetailMap.get(accountId).getVideoSmsPrice() * node.getMaxSendNumber();
                }
                log.info("群发发送校验: 计划{} 节点{} 账号{} 需要金额{}", plan.getId(), node.getId(), accountId, nodeAmount);
            }
            planAmount += nodeAmount;
        }
        log.info("群发发送校验: 计划{} 需要总金额{}", plan.getId(), planAmount);
        if (planAmount > balance) {
            result.setNoticeMsg("您的余额不足以支持本计划的所有节点的发送任务，不足部分将会发送失败，是否继续？");
        }
        result.setValidResult(true);
        plan.setConsumeCategory(PaymentTypeEnum.BALANCE.getCode());
        planService.updateById(plan);
        return result;
    }

    /**
     * 启动计划，如果是预付费用户需要进行资源预扣除
     *
     * @param planId 要启动的计划id
     */
    @Transactional(rollbackFor = Exception.class)
    @ShardingSphereTransactionType(TransactionType.BASE)
    public void startPlan(Long planId) {
        RLock lock = this.getPlanLock(planId, GroupPlanAction.EDIT);
        try {
            lock.lock();
            RobotGroupSendPlansDo plan = this.queryPlanByPlanIdAndStatus(planId, TO_BE_SEND, SEND_STOP);
            log.info("群发计划:【{}-{}】启动中", plan.getId(), plan.getPlanName());
            // List<AbstractNode> tree = handleNodePreDeductAndSaveAccountDetail(plan);

            List<AbstractNode> tree = getNodeTreeByPlanId(planId);
            //扣除第一层节点的余额或者套餐数量
            deductTopLevelNodeFee(plan, tree);
            plan.setPlanStatus(SENDING.getCode());
            Date now = new Date();
            plan.setStartTime(now);
            plan.setUpdateTime(now);
            BaseUser user = SessionContextUtil.getLoginUser();
            //预付费用户在启动计划前有个tryStart步骤会检测是否能启动成功并设置consumeCategory字段，而后付费用户没有好的初始化此字段的时机，
            //所以在启动计划是判断如果是后付费用户，则设置其扣费方式为余额
            if (user != null && user.getPayType() == POSTPAY) {
                plan.setConsumeCategory(PaymentTypeEnum.BALANCE.getCode());
            }
            planService.updateById(plan);
            broadcastTaskExecutor.execute(() -> this.runPlan(plan, tree));
            log.info("启动成功");
        } catch (Throwable throwable) {
            log.warn("启动计划失败:{}", throwable.getMessage(), throwable);
            throw new BizException("计划启动失败：" + Optional.ofNullable(throwable.getMessage()).orElse("未知异常"));
        } finally {
            lock.unlock();
        }
    }

    //扣除第一层节点的余额或套餐
    private void deductTopLevelNodeFee(RobotGroupSendPlansDo plan, List<AbstractNode> tree) {
        tree.forEach(node -> {
            if (Objects.isNull(node.getParent())) {
                //检查节点是否过期或者是否可执行，过期不扣分，可执行才扣费
                if (node.nodeIsNeedDeduct()) {
                    this.checkAndSettingSendAccount(plan, node);
                    this.checkAndSettingTemplate(node);
                    balanceDeductForNodeForGroup(plan, node);
                } else {
                    log.info("节点:{},不需要扣费", node.getId());
                }
            }
        });
    }


    /**
     * 暂停计划
     *
     * @param planId 要暂停计划Id
     */
    @Transactional(rollbackFor = Exception.class)
    @ShardingSphereTransactionType(TransactionType.BASE)
    public void stopPlan(Long planId) {
        RLock lock = this.getPlanLock(planId, GroupPlanAction.EDIT);
        try {
            lock.lock();
            RobotGroupSendPlansDo plan = new RobotGroupSendPlansDo();
            boolean updated = planService.lambdaUpdate()
                    .set(RobotGroupSendPlansDo::getPlanStatus, SEND_STOP.getCode())
                    .eq(RobotGroupSendPlansDo::getId, planId)
                    .eq(RobotGroupSendPlansDo::getPlanStatus, SENDING.getCode())
                    .update(plan);
            if (!updated) {
                throw new BizException("暂停失败");
            }
            if (SessionContextUtil.getUser().getPayType() == CustomerPayType.PREPAY) {
                SendStatus cacheState = getPlanCacheState(planId);
                if (Objects.equals(cacheState, SENDING)) {
                    log.info("计划在执行中,通知执行线程。。。。");
                    plan.setId(planId);
                    cacheAndSetPlanState(plan, SEND_STOP);
                } else {
                    log.info("计划不在执行中，开始归还资源。。。");
                    RobotGroupSendPlans robotGroupSendPlans = planService.queryById(planId);
                    List<AbstractNode> tree = getNodeTreeByPlanId(planId);
                    BroadcastPlanUtils.applyExecutableNode(tree, node -> {
                        revertNodeResources(node, robotGroupSendPlans.getConsumeCategory());
                        return true;
                    });
                }
            }
        } finally {
            lock.unlock();
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @ShardingSphereTransactionType(TransactionType.BASE)
    public void runPlan(Long planId) {
        log.info("定时任务尝试获取plan execute lock:{}", planId);

        RLock planLock = getPlanLock(planId, GroupPlanAction.EXECUTE);
        try {
            //尝试获取执行计划的锁，超时仍未获取到则不执行（其它线程在执行此计划）
            if (planLock.tryLock(1000, TimeUnit.MILLISECONDS)) {
                RobotGroupSendPlansDo plan = this.queryPlanByPlanIdAndStatus(planId);
                log.info("定时任务 准备执行群发计划 {}:{}", planId, plan.getPlanName());
                List<AbstractNode> tree = getNodeTreeByPlanId(planId);
                this.runPlan(plan, tree);
            }
        } catch (InterruptedException e) {
            log.error("获取锁失败", e);
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        } finally {
            if (planLock.isLocked() && planLock.isHeldByCurrentThread())
                planLock.unlock();
        }
    }

    /**
     * 执行计划
     */
    @Transactional(rollbackFor = Exception.class)
    @ShardingSphereTransactionType(TransactionType.BASE)
    public void runPlan(RobotGroupSendPlansDo plan, List<AbstractNode> tree) {
        Long planId = plan.getId();
        UserInfoVo userInfo = cspCustomerApi.getByCustomerId(plan.getCreator());

        RLock planLock = getPlanLock(planId, GroupPlanAction.EXECUTE);
        try {
            //尝试获取执行计划的锁，超时仍未获取到则不执行（其它线程在执行此计划）
            log.info("准备执行群发计划 {}:{}", planId, plan.getPlanName());
            if (planLock.tryLock(500, TimeUnit.MILLISECONDS)) {
                log.info("获取锁成功，开始执行");
                cacheAndSetPlanState(plan, SENDING);
                //计划执行
                BroadcastPlanUtils.applyExecutableNode(tree, node -> {
                    this.beforeExecuteNode(plan, node);
                    this.executeNode(plan, node);
                    return this.afterNodeExecute(node, userInfo, plan);
                });

                //计划执行后
                if (!BroadcastPlanUtils.hasExecutableNode(tree)) {
                    SendStatus status = SEND_SUCCESS;
                    List<AbstractNode> tmpNodes = flatNodeTree(tree);
                    //只要有过期节点得群发计划状态一直在执行中
                    if (tmpNodes.stream().anyMatch(node -> node.getStatus() == EXPIRED)) {
                        status = SENDING;
                    } else if (tmpNodes.stream().noneMatch(node -> node.getStatus() == SEND_SUCCESS)) {
                        status = SEND_FAIL;
                    }
                    cacheAndSetPlanState(plan, status);
                    log.info("执行成功");
                } else {
                    log.info("计划有阻塞节点待执行。。。");
                }
            } else {
                log.info("获取执行锁失败，放弃执行");
            }
        } catch (Throwable throwable) {
            log.error("执行失败:{}", throwable.getMessage(), throwable);
            if (throwable instanceof InterruptedException)
                Thread.currentThread().interrupt();
            SendStatus status = (throwable instanceof GroupPlanStoppedException) ? SEND_STOP : SEND_FAIL;
            if("素材审核中".equals(throwable.getMessage())){
                status = SENDING;
            }
            cacheAndSetPlanState(plan, status);
        } finally {
            try {
                planService.updateById(plan);
                clearPlanCacheState(planId);
            } catch (Throwable throwable) {
                log.error("finally更新计划失败:{}", throwable.getMessage(), throwable);
            } finally {
                if (planLock.isLocked() && planLock.isHeldByCurrentThread())
                    planLock.unlock();
            }
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void closePlan(Long planId) {
        RobotGroupSendPlansDo plan = this.queryPlanByPlanIdAndStatus(planId, SEND_STOP);
        plan.setPlanStatus(SEND_CLOSE.getCode());
        planService.updateById(plan);
    }


    /**
     * 5G消息状态回调，根据消息状态更新客户套餐额度
     *
     * @param messageId 消息id
     * @param state     消息状态
     */
    @Transactional(rollbackFor = Exception.class)
    public void refundByDeliveryState(String messageId, DeliveryEnum state, Integer sendNumber, String customerId) {
        if (sendNumber == null || sendNumber <= 0)
            return;
        MsgRecordResp resp = msgRecordApi.queryRecordByMessageIdAndCustomerId(messageId, customerId).get(0);
        UserInfoVo userInfoVo = cspCustomerApi.getByCustomerId(resp.getCustomerId());
        if (userInfoVo.getPayType() != PREPAY)
            return;
        MsgSubTypeEnum subType = resp.getConversationId() != null
                ? MsgSubTypeEnum.CONVERSATION
                : MsgSubTypeEnum.convertTemplateType2MsgSubType(resp.getMessageType());
        if (state == DeliveryEnum.FAILED) {
            prepaymentApi.returnRemaining(resp.getAccountId(), MsgTypeEnum.M5G_MSG, subType, (long) sendNumber);
            log.info("返还网关回调失败消息:{}额度:{}成功", subType, sendNumber);
        } else if (state == DeliveryEnum.DELIVERED_TO_NETWORK) {
            prepaymentApi.returnRemaining(resp.getAccountId(), MsgTypeEnum.M5G_MSG, subType, (long) sendNumber);
            //回落消息通过后付费订单方式结算
            log.info("返还网关回调转回落消息:{}额度:{}成功", subType, sendNumber);
        } else {
            log.warn("无需更新预付费客户消息额度状态:{}", state);
        }
    }


    public void balanceDeductForNodeForFastGroup(FastGroupMessage fastGroupMessage, AbstractNode node) {
        List<String> accountIds = Arrays.asList(fastGroupMessage.getAccounts().split(","));
        balanceDeductForNodeForGroup(fastGroupMessage.getCreator(), accountIds, fastGroupMessage.getPaymentType(), node);
    }

    //扣除节点套餐或余额
    private void balanceDeductForNodeForGroup(RobotGroupSendPlansDo plan, AbstractNode node) {
        //找到所有的account
        List<String> accountIds = resolveAccountIds(plan, node.getType());
        balanceDeductForNodeForGroup(plan.getCreator(), accountIds, plan.getConsumeCategory(), node);
    }


    /**
     * 扣除节点套餐或余额
     *
     * @param creator         创建者
     * @param accountIds      发送账号
     * @param consumeCategory 消费类型  1 充值 2 预购套餐
     * @param node            发送结点
     */
    //扣除节点套餐或余额
    private void balanceDeductForNodeForGroup(String creator, List<String> accountIds, Integer consumeCategory, AbstractNode node) {
        UserInfoVo userInfo = cspCustomerApi.getByCustomerId(creator);
        log.info("节点扣费开始{}-{}节点:{}", node.getType(), node.getSubType(), node.getId());
        List<String> sendPhones = this.querySendPhones(node, node.getParent());
        if (CollectionUtil.isEmpty(sendPhones)) {
            log.info("该节点没有需要发送的手机号码，节点：{}", node.getId());
            return;
        }
        Integer deducted;
        if (node.getIsFastGroup() != null && node.getIsFastGroup() == 1) {
            FastGroupMessageService fastGroupMessageService = SpringUtils.getBean(FastGroupMessageService.class);
            FastGroupMessage fastGroupMessage = fastGroupMessageService.queryById(node.getId());
            if (fastGroupMessage == null) {
                throw new BizException("快捷群发未找到，节点:" + node.getId());
            }
            deducted = fastGroupMessage.getDeducted();
        } else {
            //扣费
            RobotGroupSendPlansDetailService nodeService = SpringUtils.getBean(RobotGroupSendPlansDetailService.class);
            RobotGroupSendPlansDetail nodeDetail = nodeService.queryById(node.getId());
            //只有未扣除需要处理,  如果是
            if (Objects.isNull(nodeDetail.getDeducted())) {
                //旧版代码
                log.info("旧版本, 已经扣费完成");
            }
            deducted = nodeDetail.getDeducted();
        }

        // 已扣除
        if (deducted == 1) {
            log.info("作为首节点,已扣费完成,继续执行node节点功能");
        } else if (deducted == 0) {
            String customerId = node.getCustomerId();

            RLock deductLock = redissonClient.getLock(String.format(FEE_DEDUCT_LOCK, customerId));
            NodeDeduct nodeDeduct = new NodeDeduct(sendPhones);
            try {
                deductLock.lock();
                //后付费
                if (userInfo.getPayType().getCode().equals(PayTypeEnum.POST_PAYMENT.getCode())) {
                    log.info("后付费用户，不需要扣费,但是需要记录消费记录");
                }
                //找到各种资费
                if (node.getType().equals(MsgTypeEnum.M5G_MSG)) {
                    MessageTemplateResp template = (MessageTemplateResp) node.getTemplate();
                    if (Objects.equals(consumeCategory, PaymentTypeEnum.SET_MEAL.getCode())) {
                        MsgSubTypeEnum subType = MsgSubTypeEnum.convertTemplateType2MsgSubType(template.getMessageType());
                        for (String accountId : accountIds) {
                            //扣费并且在node-account 详情单上记录详细信息
                            deductFifthMsgOfSetMeal(node, accountId, sendPhones, subType, nodeDeduct);
                        }
                    } else {
                        //预付费和后付费
                        for (String accountId : accountIds) {
                            //扣费并且在node-account 详情单上记录详细信息
                            deductFifthMsg(node, accountId, sendPhones, customerId, template, userInfo.getPayType().getCode(), nodeDeduct);
                        }
                    }
                } else if (node.getType().equals(MsgTypeEnum.SHORT_MSG)) {
                    //实际上短信账号只有一个???
                    if (Objects.equals(consumeCategory, PaymentTypeEnum.SET_MEAL.getCode())) {
                        for (String accountId : accountIds) {
                            //扣费并且在node-account 详情单上记录详细信息
                            deductSmsOfSetMeal(node, accountId, sendPhones, nodeDeduct);
                        }
                    } else {
                        //预付费和后付费
                        for (String accountId : accountIds) {
                            //扣费并且在node-account 详情单上记录详细信息
                            deductSms(node, accountId, sendPhones, customerId, userInfo.getPayType().getCode(), nodeDeduct);
                        }
                    }
                } else if (node.getType().equals(MsgTypeEnum.MEDIA_MSG)) {
                    if (Objects.equals(consumeCategory, PaymentTypeEnum.SET_MEAL.getCode())) {
                        for (String accountId : accountIds) {
                            //扣费并且在node-account 详情单上记录详细信息
                            deductVideoSmsOfSetMeal(node, accountId, sendPhones, nodeDeduct);
                        }
                    } else {
                        //预付费和后付费
                        for (String accountId : accountIds) {
                            //扣费并且在node-account 详情单上记录详细信息
                            deductVideoSms(node, accountId, sendPhones, customerId, userInfo.getPayType().getCode(), nodeDeduct);
                        }
                    }
                }
                log.info("节点资源扣除完毕，扣除详情：{}", nodeDeduct);
                if (nodeDeduct.realSend == 0) {
                    String failureReason = nodeDeduct.canSend == 0 ? "无可用账号" : "余额不足";
                    node.setStatus(SEND_FAIL)
                            .setFailMsg(failureReason);
                    if (node.getIsFastGroup() != null && node.getIsFastGroup() == 1) {
                        FastGroupMessageService fastGroupMessageService = SpringUtils.getBean(FastGroupMessageService.class);
                        fastGroupMessageService.lambdaUpdate()
                                .eq(FastGroupMessage::getId, node.getId())
                                .set(FastGroupMessage::getStatus, FastGroupMessageStatus.FAIL.getCode())
                                .set(FastGroupMessage::getFailureReason, failureReason)
                                .update(new FastGroupMessage());
                    } else {
                        node.syncDb();
                    }
                }
            } finally {
                if (deductLock.isLocked() && deductLock.isHeldByCurrentThread())
                    deductLock.unlock();
            }
            //更新nodeDetail节点的扣费状态为已经扣费
            if (node.getIsFastGroup() != null && node.getIsFastGroup() == 1) {
                FastGroupMessageService fastGroupMessageService = SpringUtils.getBean(FastGroupMessageService.class);
                fastGroupMessageService.lambdaUpdate()
                        .eq(FastGroupMessage::getId, node.getId())
                        .set(FastGroupMessage::getDeducted, 1)
                        .update(new FastGroupMessage());
            } else {
                RobotGroupSendPlansDetailService nodeService = SpringUtils.getBean(RobotGroupSendPlansDetailService.class);
                nodeService.lambdaUpdate()
                        .eq(BaseDo::getId, node.getId())
                        .set(RobotGroupSendPlansDetailDo::getDeducted, 1)
                        .update(new RobotGroupSendPlansDetailDo());
            }


            LambdaQueryWrapper<GroupNodeAccountDetail> queryWrapper = Wrappers.<GroupNodeAccountDetail>lambdaQuery()
                    .in(GroupNodeAccountDetail::getNodeId, node.getId());
            Map<Long, Map<String, GroupNodeAccountDetail>> detailMap = nodeAccountDetailMapper.selectList(queryWrapper)
                    .stream()
                    .collect(Collectors.groupingBy(GroupNodeAccountDetail::getNodeId, Collectors.toMap(GroupNodeAccountDetail::getAccountId, Function.identity())));
            //设置账号发送详情,一个节点可能使用多个账号进行发送，从数据库读取每个账号的扣除、发送、返还数量
            Map<String, AbstractNode.AccountSendDetail> accountDetailMap = new HashMap<>();
            detailMap.getOrDefault(node.getId(), Collections.emptyMap())
                    .forEach((accountId, dbDetail) -> {
                        AbstractNode.AccountSendDetail detail = new AbstractNode.AccountSendDetail();
                        detail.setPreemptedNumber(dbDetail.getPreemptedNumber())
                                .setSendNumber(dbDetail.getActualSendNumber())
                                .setReturnNumber(dbDetail.getReturnNumber())
                                .setChargeNum(dbDetail.getChargeNum());
                        accountDetailMap.put(accountId, detail);
                    });
            node.setAccountSendDetail(accountDetailMap);
        }
    }

    private void beforeExecuteNode(RobotGroupSendPlansDo plan, AbstractNode node) {
        //已过期的
        if (node.isExpired()) {
            log.info("前置处理 {}-{}节点已经过期:{}", node.getType(), node.getSubType(), node.getId());
            RobotGroupSendPlansDetailService nodeService = SpringUtils.getBean(RobotGroupSendPlansDetailService.class);
            RobotGroupSendPlansDetail nodeDetail = nodeService.queryById(node.getId());

            //已经扣费的
            if (nodeDetail.getDeducted() == 1) {
                log.info("过期节点已扣费,需要退还");
                String planChatbotAccount = plan.getPlanChatbotAccount();
                String[] accountIds = planChatbotAccount.split(",");
                for (String accountId : accountIds) {
                    GroupNodeAccountDetail groupNodeAccountDetail = nodeAccountDetailMapper.selectOne(Wrappers.<GroupNodeAccountDetail>lambdaQuery()
                            .eq(GroupNodeAccountDetail::getNodeId, node.getId())
                            .eq(GroupNodeAccountDetail::getAccountId, accountId));
                    if (Objects.equals(PaymentTypeEnum.SET_MEAL.getCode(), plan.getConsumeCategory())) {
                        Integer preemptedNumber = groupNodeAccountDetail.getPreemptedNumber();
                        prepaymentApi.returnRemaining(accountId, node.getType(), node.getSubType(), (long) preemptedNumber);
                        log.info("{}-{}节点:{},已经扣费，由于过期将全部退费,数量:{}", node.getType(), node.getSubType(), node.getId(), preemptedNumber);
                    } else {
                        //全部退费
                        deductionAndRefundApi.returnBalanceBatchWithoutTariffType(ReturnBalanceBatchReq.builder()
                                .messageId(groupNodeAccountDetail.getMessageId())
                                .customerId(node.getCustomerId())
                                .build());
                        log.info("{}-{}节点:{},已经扣费，由于过期将全部退费", node.getType(), node.getSubType(), node.getId());
                    }
                }
                //在node-account 删除详单(以免重新启动)
                nodeAccountDetailService.clearByNodeIds(Collections.singletonList(node.getId()));
                //更新nodeDetail节点的扣费状态为已经未扣费,防止重复退费 #BUG4138
                nodeService.lambdaUpdate()
                        .eq(BaseDo::getId, node.getId())
                        .set(RobotGroupSendPlansDetailDo::getDeducted, 0)
                        .update(new RobotGroupSendPlansDetailDo());
            }
            return;
        }

        if (!node.nodeIsNeedDeduct()) {
            log.info("前置处理 {}-{}节点:{},不需要扣费", node.getType(), node.getSubType(), node.getId());
            return;
        }
        this.checkAndSettingSendAccount(plan, node);
        //设置模板
        this.checkAndSettingTemplate(node);
        node.setPaymentType(PaymentTypeEnum.ofCode(plan.getConsumeCategory()));
        balanceDeductForNodeForGroup(plan, node);
    }

    private void deductFifthMsg(AbstractNode node, String accountId, List<String> sendPhones, String
            customerId, MessageTemplateResp template, Integer payType, NodeDeduct nodeDeduct) {
        //每个chatbot只发送自己运营商的5G消息
        String messageId = UUIDUtils.generateUUID();
        AccountManagementResp chatbotAccount = accountManagementApi.getAccountManagementByAccountId(accountId);
        //当前资费
        RechargeTariffDetailResp rechargeTariff = rechargeTariffApi.getRechargeTariff(chatbotAccount.getChatbotAccountId());
        if (rechargeTariff == null) {
            throw new BizException(String.format(AuthCenterError.Tarrif_Not_config.getMsg(), chatbotAccount.getAccountName()));
        }

        Integer accountTypeCode = chatbotAccount.getAccountTypeCode();
        Map<String, String> allSegment = massSegmentApi.queryAllSegment();
        List<String> selectedPhones = PhoneFilterUtil.createSelectedPhones(sendPhones, accountTypeCode.toString(), allSegment);
        if (CollectionUtil.isEmpty(selectedPhones)) {
            nodeDeduct.recordAccountDeductDetail(accountId, null, null);
            return;
        }
        //生成扣费记录(后付费用户不扣费)
        FeeDeductReq build = FeeDeductReq.builder()
                .phoneNumbers(selectedPhones)
                .messageId(messageId)
                .customerId(customerId)
                .accountId(chatbotAccount.getChatbotAccountId())
                .payType(payType)
                .accountType(AccountTypeEnum.FIFTH_MESSAGES.getCode())
                .tariffType((template.getMessageType() == 1 || template.getMessageType() == 8) ? TariffTypeEnum.TEXT_MESSAGE.getCode() : TariffTypeEnum.RICH_MEDIA_MESSAGE.getCode())
                .fifthFallbackType(node.getAllowFallback() == 0 ? null : node.getFallbackType())
                .chargeNum(1)
                .build();
        List<String> realSendPhones = CollectionUtil.newArrayList();
        realSendPhones.addAll(deductionAndRefundApi.tryDeductFee(build));
        //找到真正扣除的资费价格
        ArrayList<Integer> tariffTypeList = CollectionUtil.newArrayList();
        findRealDeductTariffType(build, rechargeTariff, tariffTypeList);
        int allPriceOfTariffType = tariffTypeList.stream().map(rechargeTariff::getPriceOfTariffType).mapToInt(Integer::intValue).sum();
        //在node-account 详情单上记录详细信息
        GroupNodeAccountDetail accountDetail = createAccountDetail(node, accountId, messageId, realSendPhones, allPriceOfTariffType, 1);
        nodeAccountDetailService.save(accountDetail);

        redisTemplate.opsForValue().set(MessagePaymentTypeConstant.redisPrefix + messageId, PaymentTypeEnum.BALANCE.getCode().toString(), 5, TimeUnit.MINUTES);
        nodeDeduct.recordAccountDeductDetail(accountId, selectedPhones, realSendPhones);
    }

    private static GroupNodeAccountDetail createAccountDetail(AbstractNode node, String accountId, String
            messageId, List<String> realSendPhones, Integer allPriceOfTariffType, Integer chargeNum) {
        return new GroupNodeAccountDetail()
                .setNodeId(node.getId())
                .setAccountId(accountId)
                .setMessageId(messageId)
                .setSelectPhoneNumbers(String.join(",", realSendPhones))
                .setPrice(allPriceOfTariffType * chargeNum)
                .setPreemptedNumber(realSendPhones.size() * chargeNum)
                .setMaxSend(realSendPhones.size())
                .setChargeNum(chargeNum);
    }

    private void deductFifthMsgOfSetMeal(AbstractNode node, String
            accountId, List<String> sendPhones, MsgSubTypeEnum subType, NodeDeduct nodeDeduct) {
        //每个chatbot只发送自己运营商的5G消息
        String messageId = UUIDUtils.generateUUID();
        AccountManagementResp chatbotAccount = accountManagementApi.getAccountManagementByAccountId(accountId);

        RechargeTariffDetailResp rechargeTariff = rechargeTariffApi.getRechargeTariff(chatbotAccount.getChatbotAccountId());
        if (rechargeTariff == null) {
            throw new BizException(String.format(AuthCenterError.Tarrif_Not_config.getMsg(), chatbotAccount.getAccountName()));
        }

        Integer accountTypeCode = chatbotAccount.getAccountTypeCode();
        Map<String, String> allSegment = massSegmentApi.queryAllSegment();
        //选中的手机号(剔除不应发送的手机号)
        List<String> selectedPhones = PhoneFilterUtil.createSelectedPhones(sendPhones, accountTypeCode.toString(), allSegment);
        if (CollectionUtil.isEmpty(selectedPhones)) {
            nodeDeduct.recordAccountDeductDetail(accountId, null, null);
            return;
        }
        //尝试扣除
        Long successSize = prepaymentApi.tryDeductRemaining(accountId, MsgTypeEnum.M5G_MSG, subType, (long) selectedPhones.size(), 1L);
        List<String> realSendPhones = new ArrayList<>(selectedPhones.subList(0, Math.toIntExact(successSize)));
        //在node-account 详情单上记录详细信息
        GroupNodeAccountDetail accountDetail = createAccountDetailOfSetMeal(node, accountId, messageId, realSendPhones, 1);
        nodeAccountDetailService.save(accountDetail);

        //将messageId 和 finalPaymentType 对应关系 存到redis,并设置过期时间
        redisTemplate.opsForValue().set(MessagePaymentTypeConstant.redisPrefix + messageId, PaymentTypeEnum.SET_MEAL.getCode().toString(), 5, TimeUnit.MINUTES);
        nodeDeduct.recordAccountDeductDetail(accountId, selectedPhones, realSendPhones);
    }

    private static GroupNodeAccountDetail createAccountDetailOfSetMeal(AbstractNode node, String accountId, String
            messageId, List<String> realSendPhones, Integer chargeNum) {
        return new GroupNodeAccountDetail()
                .setNodeId(node.getId())
                .setAccountId(accountId)
                .setMessageId(messageId)
                .setSelectPhoneNumbers(String.join(",", realSendPhones))
                .setPreemptedNumber(realSendPhones.size() * chargeNum)
                .setMaxSend(realSendPhones.size())
                .setChargeNum(chargeNum)
                .setPrice(0);
    }

    private void deductVideoSms(AbstractNode node, String accountId, List<String> sendPhones, String
            customerId, Integer payType, NodeDeduct nodeDeduct) {
        String messageId = UUIDUtils.generateUUID();
        FeeDeductReq build = FeeDeductReq.builder()
                .phoneNumbers(sendPhones)
                .customerId(customerId)
                .messageId(messageId)
                .accountId(accountId)
                .payType(payType)
                .accountType(AccountTypeEnum.VIDEO_SMS.getCode())
                .tariffType(TariffTypeEnum.VIDEO_SMS.getCode())
                .chargeNum(1)
                .build();
        //调用扣费接口
        List<String> realSendPhones = CollectionUtil.newArrayList();
        //找到真正扣除的资费价格
        RechargeTariffDetailResp tariffVo = rechargeTariffApi.getRechargeTariff(accountId);
        if (tariffVo == null) {
            CspVideoSmsAccountDetailResp videoSmsAccount = ((List<CspVideoSmsAccountDetailResp>) node.getSendAccount()).get(0);
            throw new BizException(String.format(AuthCenterError.Tarrif_Not_config.getMsg(), videoSmsAccount.getAccountName()));
        }
        realSendPhones.addAll(deductionAndRefundApi.tryDeductFee(build));
        ArrayList<Integer> tariffTypeList = CollectionUtil.newArrayList();
        findRealDeductTariffType(build, tariffVo, tariffTypeList);
        int allPriceOfTariffType = tariffTypeList.stream().map(tariffVo::getPriceOfTariffType).mapToInt(Integer::intValue).sum();
        //在node-account 详情单上记录详细信息
        GroupNodeAccountDetail accountDetail = createAccountDetail(node, accountId, messageId, realSendPhones, allPriceOfTariffType, 1);
        nodeAccountDetailService.save(accountDetail);

        redisTemplate.opsForValue().set(MessagePaymentTypeConstant.redisPrefix + messageId, PaymentTypeEnum.BALANCE.getCode().toString(), 5, TimeUnit.MINUTES);
        nodeDeduct.recordAccountDeductDetail(accountId, sendPhones, realSendPhones);
    }

    private void deductVideoSmsOfSetMeal(AbstractNode node, String accountId, List<String> sendPhones, NodeDeduct nodeDeduct) {
        String messageId = UUIDUtils.generateUUID();
        RechargeTariffExistReq req = new RechargeTariffExistReq();
        req.setAccountId(accountId);
        req.setAccountType(MsgTypeEnum.MEDIA_MSG.getCode());
        Long successSize = prepaymentApi.tryDeductRemaining(accountId, MsgTypeEnum.MEDIA_MSG, null, (long) sendPhones.size(), 1L);
        List<String> realSendPhones = new ArrayList<>(sendPhones.subList(0, Math.toIntExact(successSize)));
        GroupNodeAccountDetail accountDetail = createAccountDetailOfSetMeal(node, accountId, messageId, realSendPhones, 1);
        nodeAccountDetailService.save(accountDetail);
        redisTemplate.opsForValue().set(MessagePaymentTypeConstant.redisPrefix + messageId, PaymentTypeEnum.SET_MEAL.getCode().toString(), 5, TimeUnit.MINUTES);
        nodeDeduct.recordAccountDeductDetail(accountId, sendPhones, realSendPhones);
    }


    private void deductSms(AbstractNode node, String accountId, List<String> sendPhones, String
            customerId, Integer payType, NodeDeduct nodeDeduct) {
        String messageId = UUIDUtils.generateUUID();
        //是短信发送节点, 需要过滤掉未被选择的运营商号段
        String selectedCarriers = node.getSelectedCarriers();
        Map<String, String> allSegment = massSegmentApi.queryAllSegment();
        List<String> selectedPhones = PhoneFilterUtil.createSelectedPhones(sendPhones, selectedCarriers, allSegment);

        if (CollectionUtil.isEmpty(selectedPhones)) {
            nodeDeduct.recordAccountDeductDetail(accountId, null, null);
            return;
        }

        // 统计短信长度，根据长度进行计费
        SmsTemplateDetailVo template = (SmsTemplateDetailVo) node.getTemplate();
        Integer chargeNum = BroadcastPlanUtils.getContentLength(template);

        FeeDeductReq build = FeeDeductReq.builder()
                .phoneNumbers(selectedPhones)
                .customerId(customerId)
                .messageId(messageId)
                .accountId(accountId)
                .payType(payType)
                .accountType(AccountTypeEnum.SMS.getCode())
                .tariffType(TariffTypeEnum.SMS.getCode())
                .chargeNum(chargeNum)
                .build();
        List<String> realSendPhones = CollectionUtil.newArrayList();
        //找到真正扣除的资费价格
        RechargeTariffDetailResp tariffVo = rechargeTariffApi.getRechargeTariff(accountId);
        if (tariffVo == null) {
            CspSmsAccountDetailResp smsAccount = ((List<CspSmsAccountDetailResp>) node.getSendAccount()).get(0);
            throw new BizException(String.format(AuthCenterError.Tarrif_Not_config.getMsg(), smsAccount.getAccountName()));
        }
        realSendPhones.addAll(deductionAndRefundApi.tryDeductFee(build));


        ArrayList<Integer> tariffTypeList = CollectionUtil.newArrayList();
        findRealDeductTariffType(build, tariffVo, tariffTypeList);
        int allPriceOfTariffType = tariffTypeList.stream().map(tariffVo::getPriceOfTariffType).mapToInt(Integer::intValue).sum();
        //在node-account 详情单上记录详细信息
        GroupNodeAccountDetail accountDetail = createAccountDetail(node, accountId, messageId, realSendPhones, allPriceOfTariffType, chargeNum);
        nodeAccountDetailService.save(accountDetail);

        redisTemplate.opsForValue().set(MessagePaymentTypeConstant.redisPrefix + messageId, PaymentTypeEnum.BALANCE.getCode().toString(), 5, TimeUnit.MINUTES);
        nodeDeduct.recordAccountDeductDetail(accountId, selectedPhones, realSendPhones);
    }

    private void deductSmsOfSetMeal(AbstractNode node, String accountId, List<String> sendPhones, NodeDeduct nodeDeduct) {
        String messageId = UUIDUtils.generateUUID();
        //是短信发送节点, 需要过滤掉未被选择的运营商号段
        String selectedCarriers = node.getSelectedCarriers();
        Map<String, String> allSegment = massSegmentApi.queryAllSegment();
        List<String> selectedPhones = PhoneFilterUtil.createSelectedPhones(sendPhones, selectedCarriers, allSegment);

        if (CollectionUtil.isEmpty(selectedPhones)) {
            nodeDeduct.recordAccountDeductDetail(accountId, null, null);
            return;
        }
        RechargeTariffExistReq req = new RechargeTariffExistReq();
        req.setAccountId(accountId);
        req.setAccountType(MsgTypeEnum.SHORT_MSG.getCode());
        // 统计短信长度，根据长度进行计费
        SmsTemplateDetailVo template = (SmsTemplateDetailVo) node.getTemplate();
        Integer smsSendNum = BroadcastPlanUtils.getContentLength(template);
        Long successSize = prepaymentApi.tryDeductRemaining(accountId, MsgTypeEnum.SHORT_MSG, null, (long) selectedPhones.size(), (long) smsSendNum);

        List<String> realSendPhones = new ArrayList<>(selectedPhones.subList(0, Math.toIntExact(successSize)));

        //在node-account 详情单上记录详细信息
        GroupNodeAccountDetail accountDetail = createAccountDetailOfSetMeal(node, accountId, messageId, realSendPhones, smsSendNum);
        nodeAccountDetailService.save(accountDetail);
        redisTemplate.opsForValue().set(MessagePaymentTypeConstant.redisPrefix + messageId, PaymentTypeEnum.SET_MEAL.getCode().toString(), 5, TimeUnit.MINUTES);
        nodeDeduct.recordAccountDeductDetail(accountId, selectedPhones, realSendPhones);
    }

    private static void findRealDeductTariffType(FeeDeductReq feeDeductReq, RechargeTariffDetailResp
            tariffVo, ArrayList<Integer> tariffTypeList) {
        //回落类型 null:无回落  1:回落阅信  2:回落短信
        if (Objects.isNull(feeDeductReq.getFifthFallbackType())) {
            tariffTypeList.add(feeDeductReq.getTariffType());
        } else if (feeDeductReq.getFifthFallbackType() == 2) {
            int priceOfFifth = tariffVo.getPriceOfTariffType(feeDeductReq.getTariffType());
            int priceOfFifthReadingLetter = tariffVo.getPriceOfTariffType(TariffTypeEnum.FIVE_G_READING_LETTER_PARSE.getCode());
            int priceOfFallbackSms = tariffVo.getPriceOfTariffType(TariffTypeEnum.FALLBACK_SMS.getCode());

            if (priceOfFifth < priceOfFallbackSms + priceOfFifthReadingLetter) {
                tariffTypeList.add(TariffTypeEnum.FIVE_G_READING_LETTER_PARSE.getCode());
                tariffTypeList.add(TariffTypeEnum.FALLBACK_SMS.getCode());
            } else {
                tariffTypeList.add(feeDeductReq.getTariffType());
            }
        } else if (feeDeductReq.getFifthFallbackType() == 1) {
            int priceOfFifth = tariffVo.getPriceOfTariffType(feeDeductReq.getTariffType());
            int priceOfSms = tariffVo.getPriceOfTariffType(TariffTypeEnum.FALLBACK_SMS.getCode());

            if (priceOfFifth < priceOfSms) {
                tariffTypeList.add(TariffTypeEnum.FALLBACK_SMS.getCode());
            } else {
                tariffTypeList.add(feeDeductReq.getTariffType());
            }
        }
    }

    /**
     * 节点执行方法
     * 1. 每次执行前先检查计划状态，如果计划被暂停则抛出计划暂停异常 {@link GroupPlanStoppedException}
     * 2. 定时发送节点时间检测，如果时间不匹配则更新节点到对应状态
     * 3. 发送账号检测
     * 4. 发送模板检测
     * 5. 发送并保存发送记录
     * 6. 发送成功后保存节点信息
     * 7. 其它异常则将节点设置为失败并保存
     *
     * @param plan 计划
     * @param node 待发送节点
     */
    private void executeNode(RobotGroupSendPlansDo plan, AbstractNode node) {
        try {
            log.info("开始执行{}-{}节点:{}", node.getType(), node.getSubType(), node.getId());
            this.ensurePlanIsRunning(node.getPlanId());
            node.timingCheck();

//            this.checkAndSettingTemplate(node);
            node.setStatus(SENDING)
                    .setSendTime(LocalDateTime.now());
//            List<String> sendPhones = this.querySendPhones(node, node.getParent());
            //找到所有需要发送的手机号
            List<String> accountIds = resolveAccountIds(plan, node.getType());
            List<GroupNodeAccountDetail> groupNodeAccountDetails = nodeAccountDetailMapper.selectList(Wrappers.<GroupNodeAccountDetail>lambdaQuery()
                    .eq(GroupNodeAccountDetail::getNodeId, node.getId())
                    .in(GroupNodeAccountDetail::getAccountId, accountIds));
            List<String> needSendPhones = groupNodeAccountDetails.stream()
                    .map(GroupNodeAccountDetail::getSelectPhoneNumbers)
                    .filter(StrUtil::isNotBlank)
                    .flatMap(phones -> Arrays.stream(phones.split(",")))
                    .collect(Collectors.toList());
            if (CollectionUtil.isEmpty(needSendPhones)) {
                /*
                 * 所有号码都找不到运营商的chatbot（所有都是移动的号码，但是是电信的chatbot）---》
                 * 1.节点发送失败，
                 * 2.不记录发送数量和统计数量，
                 * 3.不记录发送明细，
                 * 4.发送节点赞开标签显示“无可用账号”
                 */
//                throw new GroupPlanExecuteException(SendGroupExp.USER_ERROR);
                node.setStatus(SendStatus.SEND_FAIL);
                node.setFailMsg("无可用账号");
                node.setSendNumber(0);
                node.setUnknownNumber(0);
                node.setFailNumber(0);
            } else {
                List<String> sendPhones = this.querySendPhones(node, node.getParent());
                sendPhones.removeAll(needSendPhones);
                //全部选中的手机号，移除匹配账号成功可以发送的，剩下的就是没有匹配到发送账号
                List<String> nonMatchAccountPhones = sendPhones;
                if (!nonMatchAccountPhones.isEmpty()) {
                    MessageTemplateResp template = (MessageTemplateResp) node.getTemplate();
                    node.setSendNumber(sendPhones.size());
                    /*
                     * 如果部分的手机号找不到运营商chatbot，下发成功的正常记录，无对应运营商的chatbot，
                     * 1、同样计算发送数量，发送状态为“发送失败”，
                     * 2、计算统计数据，
                     * 3、记录发送明细，
                     * 4、发送明细中的主叫账号为空。
                     */
                    node.setFailNumber(sendPhones.size());
                    node.setSendNumber(sendPhones.size());
                    FifthSendClient sendClient = SpringUtils.getBean(FifthSendClient.class);
                    sendClient.saveMsgRecordForNoOperator(node.getPlanId(), node.getId(), sendPhones, template, MessageResourceType.GROUP, node.getCustomerId());
                } else {
                    //所有手机号都可以发送
                    node.setSendNumber(0);
                }

                List<String> successPhones = node.send(needSendPhones);
                int failNumber = node.getSendNumber() - successPhones.size();
                node.setStatus(SEND_SUCCESS)
                        .setUnknownNumber(successPhones.size())
                        .setFailNumber(failNumber);
                log.info("节点执行成功");
            }

        } catch (Throwable throwable) {
            log.error("节点执行失败:{}", throwable.getMessage(), throwable);
            if (throwable instanceof GroupPlanStoppedException) {
                throw throwable;
            } else if (throwable instanceof GroupPlanExecuteException) {
                GroupPlanExecuteException executeException = (GroupPlanExecuteException) throwable;
                node.setStatus(executeException.getState())
                        .setFailMsg(executeException.getMsg());
            } else {
                node.setStatus(SEND_FAIL)
                        .setFailMsg("未知异常");
            }
        } finally {
            node.syncDb();
        }
    }


    /**
     * 节点执行后方法，根据节点执行完状态执行相应逻辑，并返回要添加到执行队列中的节点列表
     * 成功：返还节点未使用完的预扣除资源，并返回成功节点的直系子节点(旧 不用单独返还了吧?)
     * 失败：返还当前节点和子节点预扣除资源，更新节点信息到数据库
     *
     * @param node     节点
     * @param userInfo 用户信息
     * @return 是否要继续执行节点的子节点
     */
    private boolean afterNodeExecute(AbstractNode node, UserInfoVo userInfo, RobotGroupSendPlansDo plan) {
        CustomerPayType payType = userInfo.getPayType();
        boolean success = false;
        boolean dealWithSetMeal = (payType == PREPAY && Objects.equals(plan.getConsumeCategory(), PaymentTypeEnum.SET_MEAL.getCode()));
        switch (node.getStatus()) {
            case SEND_SUCCESS: {
                if (dealWithSetMeal) {
                    refundUnusedNodeResources(node);
                    node.syncAccountSendDetail();
                } else {
                    backNodeResourceOfBalance(node);
                    node.syncAccountSendDetail();
                }
                success = true;
                break;
            }
            case SEND_FAIL: {
                //返还失败节点资源
                if (dealWithSetMeal) {
                    refundUnusedNodeResources(node); //返还当前节点资源
                } else {
                    backNodeResourceOfBalance(node);
                }
                break;
            }
            case TO_BE_SEND:
            case EXPIRED:
            case MATERIAL_REVIEW: {
                log.info("节点阻塞等待执行时机");
                break;
            }
            default:
                throw new BizException("节点执行返回非法状态:" + node.getStatus());
        }
        return success;
    }

    //发送以后,处理其中发送失败的node-account的短信
    private void backNodeResourceOfBalance(AbstractNode node) {
        //返还余额  缓存中node发送
        Map<String, AbstractNode.AccountSendDetail> accountSendDetail = node.getAccountSendDetail();
        if (accountSendDetail != null) {
            for (Map.Entry<String, AbstractNode.AccountSendDetail> entry : accountSendDetail.entrySet()) {
                String accountId = entry.getKey();
                AbstractNode.AccountSendDetail detail = entry.getValue();
                //所有发送成功的短信的集合
                List<String> sendPhoneNumbers = detail.getSendPhoneNumbers();

                GroupNodeAccountDetail groupNodeAccountDetail = nodeAccountDetailMapper.selectOne(Wrappers.<GroupNodeAccountDetail>lambdaQuery()
                        .eq(GroupNodeAccountDetail::getNodeId, node.getId())
                        .eq(GroupNodeAccountDetail::getAccountId, accountId));
                //所有尝试发送的短信的集合(从groupNodeAccountDetail中查找)
                List<String> sendPhones = Arrays.stream(groupNodeAccountDetail.getSelectPhoneNumbers()
                                .split(","))
                        .map(String::trim)
                        .collect(Collectors.toList());
                //发送失败的短信的集合
                sendPhones.removeAll(sendPhoneNumbers);

                if ((sendPhones.size() == 1 && Objects.equals(sendPhones.get(0), "")) || CollectionUtil.isEmpty(sendPhones)) {
                    log.info("node:{}, accountId:{},无需返还", node.getId(), accountId);
                    continue;
                }
                log.info("发送失败的短信:{},将返还余额", sendPhones);
                ReturnBalanceBatchReq feeDeductReq = ReturnBalanceBatchReq.builder()
                        .messageId(groupNodeAccountDetail.getMessageId())
                        .customerId(node.getCustomerId())
                        .phoneNumbers(sendPhones)
                        .build();
                deductionAndRefundApi.returnBalanceBatchWithoutTariffType(feeDeductReq);
            }
        }
    }






















    /*----------------------------------------------tool methods--------------------------------------------------*/

    /**
     * 根据planId和action获取计划锁
     */
    private RLock getPlanLock(Long id, GroupPlanAction action) {
        return redissonClient.getLock("broadcastPlan:plan:" + action + ":" + id);
    }

    /**
     * 缓存计划状态
     */
    private void cacheAndSetPlanState(RobotGroupSendPlansDo plan, SendStatus state) {
        redisTemplate.opsForValue().set("broadcastPlan:plan:state" + ":" + plan.getId(), state.getCode() + "");
        plan.setPlanStatus(state.getCode());
        plan.setUpdateTime(new Date());
    }

    /**
     * 获取计划缓存状态
     */
    private SendStatus getPlanCacheState(Long id) {
        String code = redisTemplate.opsForValue().get("broadcastPlan:plan:state" + ":" + id);
        if (code == null) return null;
        return SendStatus.getValue(Integer.parseInt(code));
    }

    /**
     * 清理计划缓存状态
     */
    private void clearPlanCacheState(Long id) {
        redisTemplate.delete("broadcastPlan:plan:state" + ":" + id);
    }

    /**
     * 校验缓存状态是否为发送中
     */
    private void ensurePlanIsRunning(Long planId) {
        SendStatus planState = getPlanCacheState(planId);
        if (planState == SEND_STOP)
            throw new GroupPlanStoppedException("计划被暂停");
    }

    /**
     * 根据planId查询计划，并验证计划是否是目标状态
     *
     * @return 计划
     */
    private RobotGroupSendPlansDo queryPlanByPlanIdAndStatus(Long planId, SendStatus... status) {
        RobotGroupSendPlansDo plan = planService.lambdaQuery()
                .eq(BaseDo::getId, planId)
                .one();
        if (plan == null)
            throw new BizException("计划不存在");
        if (status.length != 0 && !Arrays.asList(status).contains(SendStatus.getValue(plan.getPlanStatus()))) {
            throw new BizException("计划状态异常");
        }
        return plan;
    }


    /**
     * 处理节点预扣除和保存节点账号发送明细
     */
    private List<AbstractNode> handleNodePreDeductAndSaveAccountDetail(RobotGroupSendPlansDo plan) {
        List<AbstractNode> tree = getNodeTreeByPlanId(plan.getId());
        List<GroupNodeAccountDetail> accountDetails = new ArrayList<>();
        List<Long> nodeIds = new ArrayList<>();
        BroadcastPlanUtils.applyExecutableNode(tree, node -> {
            MsgTypeEnum type = node.getType();
            MsgSubTypeEnum subType = node.getSubType();
            List<String> accountIds = resolveAccountIds(plan, type);
            Map<String, AbstractNode.AccountSendDetail> detailMap = new HashMap<>();

            for (String accountId : accountIds) {
                Integer actualDeducted = null;
                if (SessionContextUtil.getUser().getPayType() == PREPAY) {
                    actualDeducted = prepaymentApi.tryDeductRemaining(accountId, type, subType, (long) node.getMaxSendNumber(), 1L).intValue();
                    if (actualDeducted == 0)
                        throw new BizException(type.getDesc() + " 账号 " + accountId + " 余额为0，请先充值");
                    log.info("节点{}扣除{}账号:{} {}条", node.getId(), type.getDesc(), accountId, actualDeducted);
                }
                //保存节点账号发送记录
                GroupNodeAccountDetail accountDetail = createAccountDetail(node, accountId, actualDeducted);
                accountDetails.add(accountDetail);
                nodeIds.add(node.getId());
                AbstractNode.AccountSendDetail detail = new AbstractNode.AccountSendDetail();
                detail.setPreemptedNumber(actualDeducted);
                detailMap.put(accountId, detail);
            }
            node.setAccountSendDetail(detailMap);
            return true;
        });
        nodeAccountDetailService.clearByNodeIds(nodeIds);
        if (!CollectionUtils.isEmpty(accountDetails))
            nodeAccountDetailService.saveBatch(accountDetails);
        return tree;
    }

    private static GroupNodeAccountDetail createAccountDetail(AbstractNode node, String accountId, Integer
            actualDeducted) {
        return new GroupNodeAccountDetail()
                .setNodeId(node.getId())
                .setAccountId(accountId)
                .setMaxSend(node.getMaxSendNumber())
                .setPreemptedNumber(actualDeducted);
    }

    private static List<String> resolveAccountIds(RobotGroupSendPlansDo plan, MsgTypeEnum type) {
        List<String> accountIds = null;
        switch (type) {
            case M5G_MSG:
                accountIds = Arrays.asList(plan.getPlanChatbotAccount().split(","));
                break;
            case SHORT_MSG:
                accountIds = Collections.singletonList(plan.getShortMsgIds());
                break;
            case MEDIA_MSG:
                accountIds = Collections.singletonList(plan.getRichMediaIds());
                break;
        }
        return accountIds;
    }

    /**
     * 查询节点要发送的手机号列表
     */
    private List<String> querySendPhones(AbstractNode node, AbstractNode parent) {
        List<String> phoneList = !isParentFilterNode(node, parent)
                ? getCheckedContactPhones(node.getCustomerId(), node.getGroupId())
                : getParentFilterPhonesByCondition(node, parent);
        if (CollectionUtils.isEmpty(phoneList))
            return CollectionUtil.newArrayList();
        return phoneList;
    }

    /**
     * 获取联系人组的手机号列表（过滤黑名单手机号）
     *
     * @param groupId 联系人组id
     * @return 手机号列表
     */
    private List<String> getContactGroupPhones(String customerId, Long groupId) {
        return contactApi.getContactListAlls(new ContactListPageReq()
                        .setGroupId(groupId)
                        .setPageParam(new PageParam(1, Integer.MAX_VALUE, true))
                        .setUserId(customerId)
                )
                .stream()
                .filter(contactListResp -> {
                    if (contactListResp.getBlacklist() == 0)
                        return true;
                    else {
                        log.info("过滤黑名单手机号:{}", contactListResp.getPhoneNum());
                        return false;
                    }
                })
                .map(ContactListResp::getPhoneNum)
                .collect(Collectors.toList());
    }

    /**
     * 人群筛选节点，根据筛选条件筛选父节点发送记录手机号
     *
     * @return 手机号列表
     */
    private List<String> getParentFilterPhonesByCondition(AbstractNode node, AbstractNode parent) {
        BroadcastNode.FilterStrategy filterStrategy = parent.getFilterRouters().stream()
                .filter(route -> route.getChildren().contains(node.getLeftPoint()))
                .findAny()
                .orElseThrow(() -> new GroupPlanExecuteException("父节点人群筛选路由中没有当前节点"));
        return parent.filterSendPhones(filterStrategy);
    }

    /**
     * 校验并设置节点发送账号
     */
    private void checkAndSettingSendAccount(RobotGroupSendPlansDo plan, AbstractNode node) {
        MsgTypeEnum type = node.getType();
        Object sendAccount;
        switch (type) {
            case M5G_MSG:
                sendAccount = getCheckedFifthGenAccount(Arrays.asList(plan.getPlanChatbotAccount().split(",")));
                break;
            case MEDIA_MSG:
                sendAccount = getCheckedMediaSmsAccount(plan.getRichMediaIds());
                break;
            case SHORT_MSG:
                sendAccount = getCheckedSmsAccount(plan.getShortMsgIds());
                break;
            default:
                throw new GroupPlanExecuteException("Unsupported node type: " + type);
        }
        node.setSendAccount(sendAccount);
    }

    /**
     * 校验快捷群发的发送账号
     */
    public void checkAndSettingSendAccountForFastGroup(FastGroupMessage fastGroupMessage, AbstractNode node) {
        MsgTypeEnum type = node.getType();
        Object sendAccount;
        switch (type) {
            case M5G_MSG:
                sendAccount = getCheckedFifthGenAccount(Arrays.asList(fastGroupMessage.getAccounts().split(",")));
                break;
            case MEDIA_MSG:
                sendAccount = getCheckedMediaSmsAccounts(Arrays.asList(fastGroupMessage.getAccounts().split(",")));
                break;
            case SHORT_MSG:
                sendAccount = getCheckedSmsAccounts(Arrays.asList(fastGroupMessage.getAccounts().split(",")));
                break;
            default:
                throw new GroupPlanExecuteException("Unsupported node type: " + type);
        }
        node.setSendAccount(sendAccount);
    }

    /**
     * 校验并设置节点发送模板
     */
    public void checkAndSettingTemplate(AbstractNode node) {
        MsgTypeEnum type = node.getType();
        Object template;
        switch (type) {
            case M5G_MSG:
                @SuppressWarnings("unchecked")
                List<AccountManagementResp> chatbotList = (List<AccountManagementResp>) node.getSendAccount();
                template = getCheckedFifthGenTemplate(node.getTemplateId(), chatbotList, (FifthNode) node);
                break;
            case MEDIA_MSG:
                template = getCheckedMediaTemplate(node.getTemplateId());
                break;
            case SHORT_MSG:
                template = getCheckedSmsTemplate(node.getTemplateId());
                break;
            default:
                throw new GroupPlanExecuteException("Unsupported node type: " + type);
        }
        node.setTemplate(template);
    }

    private void checkAndSettingSmsTemplate(AbstractNode node) {
        SmsTemplateDetailVo template = smsTemplateApi.getTemplateInfoInner(node.getTemplateId(), false);
        if (Objects.isNull(template)) {
            throw new GroupPlanExecuteException("短信模板不存在");
        }
        log.info("node模板 Id:{},短信 PlatformTemplateId:{}", node.getTemplateId(), template.getPlatformTemplateId());
        node.setTemplate(template);
    }

    /**
     * 退还节点未使用的资源（节点执行后）
     */
    private void refundUnusedNodeResources(AbstractNode node) {
        for (Map.Entry<String, AbstractNode.AccountSendDetail> entry : node.getAccountSendDetail().entrySet()) {
            String accountId = entry.getKey();
            AbstractNode.AccountSendDetail detail = entry.getValue();
            int chargeNum = Optional.ofNullable(detail.getChargeNum()).orElse(1);
            int accountReturnNumber = detail.getPreemptedNumber() - (Optional.ofNullable(detail.getSendNumber()).orElse(0) * chargeNum);
            if (accountReturnNumber <= 0)
                continue;
            log.info("节点:【{}：{}-{}】需要返还账号:{}:{}条", node.getId(), node.getType(), node.getSubType(), accountId, accountReturnNumber);
            prepaymentApi.returnRemaining(accountId, node.getType(), node.getSubType(), (long) accountReturnNumber);
            detail.setReturnNumber(accountReturnNumber);
        }
    }

    /**
     * 返还节点全部资源（节点执行前）
     */
    @ShardingSphereTransactionType(TransactionType.BASE)
    @Transactional(rollbackFor = Exception.class)
    public void revertNodeResources(AbstractNode node, Integer consumeCategory) {
        RobotGroupSendPlansDetailService nodeService = SpringUtils.getBean(RobotGroupSendPlansDetailService.class);
        boolean shouldRevert;
        if(node.getMessageResourceType() == MessageResourceType.FAST_GROUP){
            shouldRevert = node.getStatus() == TO_BE_SEND;
        }else{
            RobotGroupSendPlansDetail nodeDetail = nodeService.queryById(node.getId());
            shouldRevert = (SendStatus.NO_STATUS == node.getStatus() || node.getStatus() == SendStatus.TO_BE_SEND) && nodeDetail.getDeducted() == 1;
        }
        if (shouldRevert) {
            //返还余额
            Map<String, AbstractNode.AccountSendDetail> accountSendDetail = node.getAccountSendDetail();
            log.info("节点:【{}】返还余额", node.getId());
            //退还套餐
            if (Objects.equals(consumeCategory, PaymentTypeEnum.SET_MEAL.getCode())) {
                for (Map.Entry<String, AbstractNode.AccountSendDetail> entry : node.getAccountSendDetail().entrySet()) {
                    String accountId = entry.getKey();
                    AbstractNode.AccountSendDetail detail = entry.getValue();
                    int chargeNum = Optional.ofNullable(detail.getChargeNum()).orElse(1);
                    int accountReturnNumber = detail.getPreemptedNumber() - (Optional.ofNullable(detail.getSendNumber()).orElse(0) * chargeNum);
                    if (accountReturnNumber <= 0)
                        continue;
                    log.info("节点:【{}：{}-{}】需要返还账号:{}:{}条", node.getId(), node.getType(), node.getSubType(), accountId, accountReturnNumber);
                    prepaymentApi.returnRemaining(accountId, node.getType(), node.getSubType(), (long) accountReturnNumber);
                    detail.setReturnNumber(accountReturnNumber);
                }
                //在node-account 删除详单
                nodeAccountDetailService.clearByNodeIds(Collections.singletonList(node.getId()));
                //更新nodeDetail节点的扣费状态为已经未扣费,防止重复退费 #BUG4138
                nodeService.lambdaUpdate()
                        .eq(BaseDo::getId, node.getId())
                        .set(RobotGroupSendPlansDetailDo::getDeducted, 0)
                        .update(new RobotGroupSendPlansDetailDo());
            }
            //退还余额
            else if (Objects.equals(consumeCategory, PaymentTypeEnum.BALANCE.getCode())) {
                if (accountSendDetail != null) {
                    for (Map.Entry<String, AbstractNode.AccountSendDetail> entry : accountSendDetail.entrySet()) {
                        String accountId = entry.getKey();
                        AbstractNode.AccountSendDetail detail = entry.getValue();
                        //所有发送成功的短信的集合
                        GroupNodeAccountDetail groupNodeAccountDetail = nodeAccountDetailMapper.selectOne(Wrappers.<GroupNodeAccountDetail>lambdaQuery()
                                .eq(GroupNodeAccountDetail::getNodeId, node.getId())
                                .eq(GroupNodeAccountDetail::getAccountId, accountId));
                        //所有尝试发送的短信的集合
                        List<String> sendPhones = Arrays.stream(groupNodeAccountDetail.getSelectPhoneNumbers().split(",")).map(String::trim).collect(Collectors.toList());
                        //发送失败的短信的集合

                        if (CollectionUtil.isNotEmpty(sendPhones)) {
                            ReturnBalanceBatchReq feeDeductReq = ReturnBalanceBatchReq.builder()
                                    .messageId(groupNodeAccountDetail.getMessageId())
                                    .customerId(node.getCustomerId())
                                    .phoneNumbers(sendPhones)
                                    .build();
                            deductionAndRefundApi.returnBalanceBatchWithoutTariffType(feeDeductReq);
                            detail.setReturnNumber(sendPhones.size());
                            log.info("节点:【{}：{}-{}】释放冻结的余额,账号:{},返还数量为:{}", node.getId(), node.getType(), node.getSubType(), accountId, detail.getReturnNumber());
                        }
                    }
                    //在node-account 删除详单
                    nodeAccountDetailService.clearByNodeIds(Collections.singletonList(node.getId()));

                    nodeService.lambdaUpdate()
                            .eq(BaseDo::getId, node.getId())
                            .set(RobotGroupSendPlansDetailDo::getDeducted, 0)
                            .update(new RobotGroupSendPlansDetailDo());
                    log.info("节点:【{}】返还余额完毕, 节点状态修改为未扣费", node.getId());
                }
            }
        }
    }

    /**
     * 节点扣除详情
     * （因为只根据真正扣除数量当扣除为0时导致的节点和计划失败，得不到真正的失败原因（可发送手机号为空还是余额不足？），需要根据实际扣除情况来设置计划和节点的状态,
     * 所以通过此对象来记录节点扣除的详细情况（包括每个账号的扣除）)
     */
    private static class NodeDeduct {
        //选中的手机号数量(原始联系人组的size)
        final int total;

        //可以发送手机号数量（根据选中账号的运营商号段匹配到的手机号数量，也是尝试去扣除余额的数量）
        int canSend;

        // 最终发送的数量（期望10，可能余额只有3，最后扣除即为3，真正发送也为3）
        int realSend;

        //原始要发送的手机号列表
        final List<String> originalSendPhones;

        //每个账号的扣除详情
        final Map<String, NodeAccountDeductDetail> accountDeductDetail;

        public NodeDeduct(List<String> originalSendPhones) {
            this.originalSendPhones = originalSendPhones;
            this.accountDeductDetail = new HashMap<>();
            total = originalSendPhones.size();
            canSend = 0;
            realSend = 0;
        }

        void recordAccountDeductDetail(String accountId, List<String> canSendPhones, List<String> realSendPhones) {
            if (accountDeductDetail.containsKey(accountId))
                throw new BizException("节点扣费详情已记录，不允许覆盖");
            accountDeductDetail.put(accountId, new NodeAccountDeductDetail(canSendPhones, realSendPhones));
            if (canSendPhones != null)
                canSend += canSendPhones.size();
            if (realSendPhones != null)
                realSend += realSendPhones.size();
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("{");
            sb.append("\"total\": ").append(total).append(", ");
            sb.append("\"canSend\": ").append(canSend).append(", ");
            sb.append("\"realSend\": ").append(realSend).append(", ");
            sb.append("\"originalSendPhones\": ").append(originalSendPhones).append(", ");
            sb.append("\"accountDeductDetail\": {");
            int i = 0;
            for (Map.Entry<String, NodeAccountDeductDetail> entry : accountDeductDetail.entrySet()) {
                if (i++ > 0) {
                    sb.append(", ");
                }
                sb.append("\"").append(entry.getKey()).append("\": ").append(entry.getValue().toString());
            }
            sb.append("}");
            sb.append("}");
            return sb.toString();
        }
    }

    @NoArgsConstructor
    @AllArgsConstructor
    private static class NodeAccountDeductDetail {
        //支持发送的手机号列表
        List<String> canSendPhones;

        //扣费成功，真正会发送的手机号列表
        List<String> realSendPhones;

        @Override
        public String toString() {
            return "{"
                    + "\"canSendPhones\": " + canSendPhones
                    + ", \"realSendPhones\": " + realSendPhones
                    + "}";
        }
    }
}