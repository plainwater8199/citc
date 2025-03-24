package com.citc.nce.auth.prepayment.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.citc.nce.auth.csp.recharge.Const.PaymentTypeEnum;
import com.citc.nce.auth.csp.recharge.Const.PrepaymentStatus;
import com.citc.nce.auth.csp.recharge.vo.RechargeReq;
import com.citc.nce.auth.invoice.vo.InvoiceOrderPrePageInfo;
import com.citc.nce.auth.invoice.vo.InvoiceOrderPreQuery;
import com.citc.nce.auth.prepayment.dao.PrepaymentOrderDetailMapper;
import com.citc.nce.auth.prepayment.dao.PrepaymentOrderMapper;
import com.citc.nce.auth.prepayment.entity.PrepaymentOrder;
import com.citc.nce.auth.prepayment.entity.PrepaymentOrderDetail;
import com.citc.nce.auth.prepayment.service.IPrepaymentOrderDetailService;
import com.citc.nce.auth.prepayment.service.IPrepaymentOrderService;
import com.citc.nce.auth.prepayment.service.IPrepaymentService;
import com.citc.nce.auth.prepayment.service.IPrepaymentUsageRecordService;
import com.citc.nce.auth.prepayment.vo.FifthPlanOrderListVo;
import com.citc.nce.auth.prepayment.vo.FifthPlanOrderQueryVo;
import com.citc.nce.auth.prepayment.vo.MessagePlanInfoDto;
import com.citc.nce.auth.prepayment.vo.PrepaymentOrderAddVo;
import com.citc.nce.auth.prepayment.vo.PrepaymentOrderCustomerSearchVo;
import com.citc.nce.auth.prepayment.vo.PrepaymentOrderListVo;
import com.citc.nce.auth.prepayment.vo.PrepaymentOrderManageListVo;
import com.citc.nce.auth.prepayment.vo.PrepaymentOrderManagerSearchVo;
import com.citc.nce.auth.prepayment.vo.SmsPlanOrderListVo;
import com.citc.nce.auth.prepayment.vo.SmsPlanOrderQueryVo;
import com.citc.nce.auth.prepayment.vo.VideoSmsPlanOrderListVo;
import com.citc.nce.auth.prepayment.vo.VideoSmsPlanOrderQueryVo;
import com.citc.nce.auth.utils.MsgPaymentUtils;
import com.citc.nce.auth.utils.SerialNumberUtils;
import com.citc.nce.authcenter.csp.CspCustomerApi;
import com.citc.nce.authcenter.csp.vo.UserInfoVo;
import com.citc.nce.common.core.enums.CustomerPayType;
import com.citc.nce.common.core.enums.MsgSubTypeEnum;
import com.citc.nce.common.core.enums.MsgTypeEnum;
import com.citc.nce.common.core.exception.BizException;
import com.citc.nce.common.core.pojo.BaseUser;
import com.citc.nce.common.core.pojo.PageResult;
import com.citc.nce.common.util.DateUtil;
import com.citc.nce.common.util.SessionContextUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.shardingsphere.transaction.annotation.ShardingSphereTransactionType;
import org.apache.shardingsphere.transaction.core.TransactionType;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.citc.nce.auth.utils.MsgPaymentUtils.formatOrderAmount;
import static com.citc.nce.auth.utils.MsgPaymentUtils.generateOrderId;

/**
 * <p>
 * 预付费订单 服务实现类
 * </p>
 *
 * @author jcrenc
 * @since 2024-01-25 10:01:02
 */
@Service(value = "PrepaymentOrder")
@Slf4j
@RequiredArgsConstructor
public class PrepaymentOrderServiceImpl extends ServiceImpl<PrepaymentOrderMapper, PrepaymentOrder> implements IPrepaymentOrderService {

    private final IPrepaymentOrderDetailService prepaymentOrderDetailService;
    private final CspCustomerApi cspCustomerApi;
    private final PrepaymentOrderDetailMapper orderDetailMapper;
    private final RedissonClient redissonClient;
    private final IPrepaymentUsageRecordService usageRecordService;

    /**
     * 1. 校验账号
     * 2. 校验套餐,计算订单金额，生成套餐快照
     * 3. 生成订单
     * 4. 生成订单详情
     *
     * @param addVo
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void addPrepaymentOrder(PrepaymentOrderAddVo addVo) {
        BaseUser user = SessionContextUtil.getLoginUser();
        String customerId = user.getUserId();
        UserInfoVo userInfo = cspCustomerApi.getByCustomerId(customerId);
        Assert.isTrue(userInfo.getPayType() == CustomerPayType.PREPAY, "非预付费客户不能购买预付费套餐");
        MsgTypeEnum msgTypeEnum = MsgTypeEnum.getValue(addVo.getMsgType());
        Assert.notNull(msgTypeEnum, "msgType can't be null");
        String planId = addVo.getPlanId();
        String accountId = addVo.getAccountId();
        IPrepaymentService prepaymentService = MsgPaymentUtils.getPrepaymentServiceImpl(msgTypeEnum);
        prepaymentService.verifyAccount(customerId, accountId);

        MessagePlanInfoDto planInfo = prepaymentService.getPlanInfo(customerId, planId);

        BigDecimal amount = formatOrderAmount(planInfo.getAmount());
        PrepaymentOrder prepaymentOrder = new PrepaymentOrder()
                .setOrderId(generateOrderId())
                .setMsgType(msgTypeEnum)
                .setCustomerId(customerId)
                .setAccountId(accountId)
                .setPlanId(planId)
                .setAmount(amount.toPlainString())
                .setRefund(BigDecimal.ZERO)
                .setPlanDetail(planInfo.getPlanDetail())
                .setStatus(PrepaymentStatus.PENDING)
                .setInvoicableAmount(amount);
        this.save(prepaymentOrder);
        prepaymentOrderDetailService.saveOrderDetails(prepaymentOrder.getOrderId(), planInfo.getDetailList());
        log.info("客户 {} 为账号 {} 充值 {} 套餐 {} 成功", customerId, accountId, msgTypeEnum.getDesc(), planId);
    }

    @Transactional(rollbackFor = Exception.class)
    @ShardingSphereTransactionType(TransactionType.BASE)
    public void recharge(RechargeReq req) {
        log.info("客户充值：{}", req);
        if (req.getPayAmount() <= 0) {
            throw new BizException("支付金额非法");
        }
        if (req.getChargeAmount() <= 0) {
            throw new BizException("充值金额非法");
        }
        SerialNumberUtils.applyGlobalUniqueSerialNumber(SerialNumberUtils.MSG_ORDER, req.getSerialNumber());
        //前端传入金额元为单位，后端乘以100000以厘后一位为单位
        Boolean result = cspCustomerApi.recharge(req.getCustomerId(), req.getChargeAmount());
        if (!result) {
            log.warn("更新客户余额失败");
            throw new BizException("充值失败");
        }
        PrepaymentOrder prepaymentOrder = new PrepaymentOrder()
                .setOrderId(generateOrderId())

                .setCustomerId(req.getCustomerId())
                .setPayAmount(req.getPayAmount())
                .setConsumeCategory(PaymentTypeEnum.BALANCE.getCode())
                .setChargeAmount(req.getChargeAmount())
                .setStatus(PrepaymentStatus.FINISH)
                .setRefund(BigDecimal.ZERO)
                .setPayTime(LocalDateTime.now())
                .setSerialNumber(req.getSerialNumber())
                .setInvoicableAmount(BigDecimal.valueOf(req.getPayAmount()).divide(BigDecimal.valueOf(100000)).setScale(4));
        this.save(prepaymentOrder);
        log.info("充值成功");
    }

    @Override
    public PageResult<FifthPlanOrderListVo> query5gOrderList(FifthPlanOrderQueryVo queryVo) {
        String chatbotAccount = queryVo.getChatbotAccount();
        IPrepaymentService prepaymentService = MsgPaymentUtils.getPrepaymentServiceImpl(MsgTypeEnum.M5G_MSG);
        String customerId = SessionContextUtil.getLoginUser().getUserId();
        try {
            prepaymentService.verifyAccount(customerId, chatbotAccount);
        } catch (Exception ignore) {
            //如果账号校验失败，返回空列表
            return new PageResult<>();
        }
        Page<FifthPlanOrderListVo> page = new Page<>(queryVo.getPageNo(), queryVo.getPageSize());
        page.setOrders(OrderItem.descs("create_time"));
        baseMapper.query5gOrder(chatbotAccount, page);
        return new PageResult<>(page.getRecords(), page.getTotal());
    }

    @Override
    public PageResult<SmsPlanOrderListVo> querySmsOrderList(SmsPlanOrderQueryVo queryVo) {
        String accountId = queryVo.getAccountId();
        IPrepaymentService prepaymentService = MsgPaymentUtils.getPrepaymentServiceImpl(MsgTypeEnum.SHORT_MSG);
        String customerId = SessionContextUtil.getLoginUser().getUserId();
        try {
            prepaymentService.verifyAccount(customerId, accountId);
        } catch (Exception ignore) {
            return new PageResult<>();
        }
        Page<SmsPlanOrderListVo> page = new Page<>(queryVo.getPageNo(), queryVo.getPageSize());
        page.setOrders(OrderItem.descs("create_time"));
        baseMapper.querySmsOrder(accountId, page);
        return new PageResult<>(page.getRecords(), page.getTotal());
    }

    @Override
    public PageResult<VideoSmsPlanOrderListVo> queryVideoSmsOrderList(VideoSmsPlanOrderQueryVo queryVo) {
        String accountId = queryVo.getAccountId();
        IPrepaymentService prepaymentService = MsgPaymentUtils.getPrepaymentServiceImpl(MsgTypeEnum.MEDIA_MSG);
        String customerId = SessionContextUtil.getLoginUser().getUserId();
        try {
            prepaymentService.verifyAccount(customerId, accountId);
        } catch (Exception ignore) {
            return new PageResult<>();
        }
        Page<VideoSmsPlanOrderListVo> page = new Page<>(queryVo.getPageNo(), queryVo.getPageSize());
        page.setOrders(OrderItem.descs("create_time"));
        baseMapper.queryVideoSmsOrder(accountId, page);
        return new PageResult<>(page.getRecords(), page.getTotal());
    }

    @Override
    public PageResult<PrepaymentOrderListVo> customerSearch(PrepaymentOrderCustomerSearchVo searchVo) {
        Page<PrepaymentOrderListVo> page = new Page<>(searchVo.getPageNo(), searchVo.getPageSize());
        page.setOrders(OrderItem.descs("create_time"));
        String customerId = SessionContextUtil.getLoginUser().getUserId();
        baseMapper.customerSearch(searchVo.getOrderId(), searchVo.getType(), searchVo.getStatus(), customerId, searchVo.getStartTime(), searchVo.getEndTime(), searchVo.getConsumeCategory(), page);
        return new PageResult<>(page.getRecords(), page.getTotal());
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void customerCancelOrder(Long id) {
        String customerId = SessionContextUtil.getLoginUser().getUserId();
        boolean updated = this.lambdaUpdate()
                .eq(PrepaymentOrder::getId, id)
                .eq(PrepaymentOrder::getCustomerId, customerId)
                .eq(PrepaymentOrder::getStatus, PrepaymentStatus.PENDING)
                .set(PrepaymentOrder::getStatus, PrepaymentStatus.CANCELED)
                .set(PrepaymentOrder::getCancelBy, customerId)
                .set(PrepaymentOrder::getInvoicableAmount, new BigDecimal(0))
                .update(new PrepaymentOrder());
        if (!updated)
            throw new BizException("取消订单失败");
    }

    @Override
    public PageResult<PrepaymentOrderManageListVo> managerSearch(PrepaymentOrderManagerSearchVo searchVo) {
        String cspId = SessionContextUtil.verifyCspLogin();
        String customerTable = "csp_customer_" + cspId;

        Page<PrepaymentOrderManageListVo> page = new Page<>(searchVo.getPageNo(), searchVo.getPageSize());
        page.setOrders(OrderItem.descs("create_time"));
        page = baseMapper.managerSearch(searchVo.getOrderId(), searchVo.getCustomerName(), searchVo.getType(), searchVo.getStatus(), searchVo.getConsumeCategory(), cspId, customerTable, page);
        List<PrepaymentOrderManageListVo> records = page.getRecords();
        if (CollectionUtils.isNotEmpty(records)) {
            List<String> customerIdList = records.stream()
                    .map(PrepaymentOrderManageListVo::getCustomerId)
                    .collect(Collectors.toList());
            Map<String, String> userPhoneMap = cspCustomerApi.getByCustomerIds(customerIdList)
                    .stream()
                    .collect(Collectors.toMap(UserInfoVo::getCustomerId, UserInfoVo::getPhone));
            for (PrepaymentOrderManageListVo record : records) {
                record.setPhone(userPhoneMap.get(record.getCustomerId()));
            }
        }
        return new PageResult<>(page.getRecords(), page.getTotal());
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void noteOrder(Long id, String note) {
        String cspId = SessionContextUtil.verifyCspLogin();
        boolean updated = this.lambdaUpdate()
                .eq(PrepaymentOrder::getId, id)
                .likeRight(PrepaymentOrder::getCustomerId, cspId)
                .set(PrepaymentOrder::getNote, note)
                .update(new PrepaymentOrder());
        if (!updated)
            throw new BizException("备注失败");
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void managerCancelOrder(Long id) {
        String cspId = SessionContextUtil.verifyCspLogin();
        boolean updated = this.lambdaUpdate()
                .eq(PrepaymentOrder::getId, id)
                .likeRight(PrepaymentOrder::getCustomerId, cspId)
                .eq(PrepaymentOrder::getStatus, PrepaymentStatus.PENDING)
                .set(PrepaymentOrder::getStatus, PrepaymentStatus.CANCELED)
                .set(PrepaymentOrder::getCancelBy, cspId)
                .update(new PrepaymentOrder());
        if (!updated)
            throw new BizException("取消订单失败");
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void finishOrder(Long id, String serialNumber) {
        String cspId = SessionContextUtil.verifyCspLogin();
        SerialNumberUtils.applyGlobalUniqueSerialNumber(SerialNumberUtils.MSG_ORDER, serialNumber);
        boolean updated = this.lambdaUpdate()
                .eq(PrepaymentOrder::getId, id)
                .likeRight(PrepaymentOrder::getCustomerId, cspId)
                .eq(PrepaymentOrder::getStatus, PrepaymentStatus.PENDING)
                .set(PrepaymentOrder::getStatus, PrepaymentStatus.FINISH)
                .set(PrepaymentOrder::getSerialNumber, serialNumber)
                .set(PrepaymentOrder::getPayTime, LocalDateTime.now())
                .update(new PrepaymentOrder());
        if (!updated)
            throw new BizException("支付完成失败");
    }

    @Override
    public PageResult<InvoiceOrderPrePageInfo> prePageSelect(InvoiceOrderPreQuery query) {
        LambdaQueryWrapper<PrepaymentOrder> ne = new LambdaQueryWrapper<PrepaymentOrder>()
                .eq(PrepaymentOrder::getCreator, query.getCreator())
                .eq(PrepaymentOrder::getInvoicing, 0)
                .gt(PrepaymentOrder::getInvoicableAmount, 0)
                .ne(PrepaymentOrder::getStatus, PrepaymentStatus.CANCELED)
                .like(StringUtils.hasLength(query.getOrderId()), PrepaymentOrder::getOrderId, query.getOrderId())
                .ge(Objects.nonNull(query.getStartTime()), PrepaymentOrder::getPayTime, DateUtil.getBeginDay(query.getStartTime()))
                .lt(Objects.nonNull(query.getEndTime()), PrepaymentOrder::getPayTime, DateUtil.getBeginAfterDay(query.getEndTime()))
                .orderByDesc(PrepaymentOrder::getCreateTime);

        Page<PrepaymentOrder> page = new Page<>(query.getPageNo(), query.getPageSize());
        page(page, ne);
        List<InvoiceOrderPrePageInfo> list = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(page.getRecords())) {
            list = page.getRecords().stream().map(s -> {
                InvoiceOrderPrePageInfo info = new InvoiceOrderPrePageInfo();
                BeanUtils.copyProperties(s, info);
                return info;
            }).collect(Collectors.toList());
        }
        return new PageResult<>(list, page.getTotal());
    }

    @Override
    public PrepaymentOrderManageListVo getByTOrderId(Long tOrderId) {
        return getBaseMapper().selectByTOrderId(tOrderId);
    }

    @Override
    public List<String> enableRefundCustomer(String cspId, String customerId) {
        return getBaseMapper().enableRefundByCustomerId(cspId, customerId);
    }

    @Override
    public List<MsgTypeEnum> enableRefundMsgType(String customerId) {
        return getBaseMapper().enableRefundMsgType(customerId);
    }

    @Override
    public List<String> enableRefundAccount(String customerId, MsgTypeEnum msgTypeEnum) {
        return getBaseMapper().enableRefundAccount(customerId, msgTypeEnum);
    }

    @Override
    public List<Long> enableRefundOrder(String customerId, MsgTypeEnum msgTypeEnum, String accountId) {
        return getBaseMapper().enableRefundOrder(customerId, msgTypeEnum, accountId);
    }

    @Override
    public BigDecimal preInvoicableAmount(String cusId) {
        return getBaseMapper().sumPreInvoicableAmount(cusId);
    }

    @Override
    public Long getRemainingCountByMessageType(String accountId, MsgTypeEnum msgTypeEnum, MsgSubTypeEnum subType) {
        return Optional.ofNullable(orderDetailMapper.getRemainingCountByMessageType(accountId, msgTypeEnum, subType))
                .orElse(0L);
    }


    /**
     * 1. 扣减需要根据账号id+消息类型获取扣减锁
     * 2. 优先扣先充值的订单
     * 3. 如果某个订单明细扣完后修改runOut标志为true
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void deductRemaining(String accountId, MsgTypeEnum msgTypeEnum, MsgSubTypeEnum subType, Long deductNumber, Long chargeNum) {
        if (deductNumber <= 0) throw new BizException("扣除数量必须为正整数");
        
        RLock deductLock = redissonClient.getLock(String.format("prepayment-order-%s", accountId));
        try {
            deductLock.lock();
            long sendNum = deduct(accountId, msgTypeEnum, subType, deductNumber, chargeNum);
            if (sendNum == 0L) {
                throw new BizException("没有套餐可以扣减，扣减失败！");
            }
//            String orderId = orderDetailMapper.findOldestAvailableDetail(accountId, msgTypeEnum, subType);
//            if (Objects.isNull(orderId))
//                throw new BizException("扣减失败");
//            PrepaymentOrderDetail orderDetail = prepaymentOrderDetailService.lambdaQuery()
//                    .eq(PrepaymentOrderDetail::getOrderId, orderId)
//                    .eq(PrepaymentOrderDetail::getMsgType, msgTypeEnum)
//                    .eq(Objects.nonNull(subType), PrepaymentOrderDetail::getMsgSubType, subType)
//                    .one();
//            long use = Math.min(deductNumber, orderDetail.getAvailableAmount());
//            orderDetail.setUsedAmount(orderDetail.getUsedAmount() + use);
//            orderDetail.setAvailableAmount(orderDetail.getAvailableAmount() - use);
//            deductNumber -= use;
//            prepaymentOrderDetailService.updateById(orderDetail);
//            usageRecordService.record(msgTypeEnum, subType, orderId, (int) -use);
//            log.info("扣除客户【{}】-【{}】账号:【{}】可用额度:{}条,扣除订单ID:{},订单剩余可用额度:{}", msgTypeEnum, subType, accountId, use, orderId, orderDetail.getAvailableAmount());
//            if (deductNumber != 0) {
//                //RLock是可重入的
//                deductRemaining(accountId, msgTypeEnum, subType, deductNumber);
//            }
        } finally {
            if (deductLock.isLocked() && deductLock.isHeldByCurrentThread())
                deductLock.unlock();
        }
    }

    /**
     * 返还消息条数
     * 1. 扣减需要根据账号id+消息类型获取返还锁
     * 2. 优先返还后充值的订单
     * 3. 如果返还了用完的订单需要重置runOut为false
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void returnRemaining(String accountId, MsgTypeEnum msgTypeEnum, MsgSubTypeEnum subType, Long returnNumber) {
        if (returnNumber <= 0) throw new BizException("返还数量必须为正整数");
        
        RLock returnLock = redissonClient.getLock(String.format("prepayment-order-%s", accountId));
        try {
            returnLock.lock();
            String orderId = orderDetailMapper.findNewestUsedDetail(accountId, msgTypeEnum, subType);
            if (Objects.isNull(orderId))
                throw new BizException("返还失败");
            PrepaymentOrderDetail orderDetail = prepaymentOrderDetailService.lambdaQuery()
                    .eq(PrepaymentOrderDetail::getOrderId, orderId)
                    .eq(PrepaymentOrderDetail::getMsgType, msgTypeEnum)
                    .eq(Objects.nonNull(subType), PrepaymentOrderDetail::getMsgSubType, subType)
                    .one();
            long back = Math.min(returnNumber, orderDetail.getUsedAmount());
            orderDetail.setAvailableAmount(orderDetail.getAvailableAmount() + back);
            orderDetail.setUsedAmount(orderDetail.getUsedAmount() - back);
            if (orderDetail.getIsDepleted())
                orderDetail.setIsDepleted(false);
            returnNumber -= back;
            prepaymentOrderDetailService.updateById(orderDetail);
            usageRecordService.record(msgTypeEnum, subType, orderId, (int) back);
            log.info("返还客户【{}】-【{}】账号:【{}】可用额度:{}条,返还订单ID:{},订单剩余可用额度:{}", msgTypeEnum, subType, accountId, back, orderId, orderDetail.getAvailableAmount());
            if (returnNumber != 0)
                returnRemaining(accountId, msgTypeEnum, subType, returnNumber);
        } finally {
            if (returnLock.isLocked() && returnLock.isHeldByCurrentThread())
                returnLock.unlock();
        }
    }

    @Override
    public List<Integer> checkPaymentForMsgType() {
        return baseMapper.checkPaymentForMsgType(SessionContextUtil.getLoginUser().getUserId());
    }

    @Override
    public Boolean existsOrderByMsgTypeAndAccountId(String customerId, MsgTypeEnum msgType, String accountId) {
        return this.exists(
                new LambdaQueryWrapper<PrepaymentOrder>()
                        .eq(PrepaymentOrder::getCustomerId, customerId)
                        .eq(PrepaymentOrder::getMsgType, msgType)
                        .eq(PrepaymentOrder::getAccountId, accountId)
        );
    }

    /**
     * 尝试扣减消息余额
     *
     * @return 真实的扣除数量
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public Long tryDeductRemaining(String accountId, MsgTypeEnum msgTypeEnum, MsgSubTypeEnum subType, Long deductNumber, Long chargeNum) {
        if (deductNumber <= 0) throw new BizException("扣除数量必须为正整数");
        
        RLock deductLock = redissonClient.getLock(String.format("prepayment-order-%s", accountId));
        try {
            deductLock.lock();
//            long totalDeduct = 0;
//            while (totalDeduct < deductNumber) {
//                long deducted = deduct(accountId, msgTypeEnum, subType, deductNumber - totalDeduct);
//                if (deducted == 0) //没有余额了
//                    break;
//                totalDeduct += deducted;
//            }
            return deduct(accountId, msgTypeEnum, subType, deductNumber, chargeNum);
        } finally {
            if (deductLock.isLocked() && deductLock.isHeldByCurrentThread())
                deductLock.unlock();
        }
    }

    private long deduct(String accountId, MsgTypeEnum msgTypeEnum, MsgSubTypeEnum subType, long deductNumber, long chargeNum) {
        String orderId = orderDetailMapper.findOldestAvailableDetail(accountId, msgTypeEnum, subType);
        if (orderId == null)
            return 0L;
        // Todo 统计用户套餐所有详情一共能发多少条
        // 一共需要发送的条数
        long sumSend = deductNumber * chargeNum;
        // 能够发送的总条数
        long actualDeduct = 0L;
        // 发送轮数
        long sendCount = 0L;
        List<PrepaymentOrderDetail> orderDetails = prepaymentOrderDetailService.lambdaQuery()
                .eq(PrepaymentOrderDetail::getOrderId, orderId)
                .eq(PrepaymentOrderDetail::getMsgType, msgTypeEnum)
                .eq(Objects.nonNull(subType), PrepaymentOrderDetail::getMsgSubType, subType)
                .list();

        // 计算用户套餐发送次数以及发送条数
        for (PrepaymentOrderDetail detail : orderDetails) {
            actualDeduct += detail.getAvailableAmount();
        }

        // 够发
        if (actualDeduct >= sumSend) {
            sendCount = deductNumber;
        } else { // 不够发
            Assert.isTrue(actualDeduct != 0, "actualDeduct can't be 0");
            sendCount = sumSend % actualDeduct;
        }

        sumSend = sendCount * chargeNum; // 更新发送次数

        // 如果发送次数为0，直接返回
        if (sumSend == 0L) {
            return sumSend;
        }

        // 开始扣减
        for (PrepaymentOrderDetail detail : orderDetails) {
            if (sumSend == 0L) { // 已经扣除完，直接跳出循环
                break;
            }
            if (sumSend >= detail.getAvailableAmount()) {
                detail.setAvailableAmount(0L);
                detail.setUsedAmount(detail.getUsedAmount() + detail.getAvailableAmount());
                sumSend -= detail.getAvailableAmount();
            } else {
                detail.setAvailableAmount(detail.getAvailableAmount() - sumSend);
                detail.setUsedAmount(detail.getUsedAmount() + sumSend);
                sumSend = 0L;
            }
        }

        // 落库
        for (PrepaymentOrderDetail detail : orderDetails) {
            prepaymentOrderDetailService.updateById(detail);
        }

        usageRecordService.record(msgTypeEnum, subType, orderId, (int) -sumSend);

        return sendCount;
    }
}
