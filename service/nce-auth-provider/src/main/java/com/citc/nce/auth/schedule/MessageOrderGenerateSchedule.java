package com.citc.nce.auth.schedule;

import cn.hutool.core.util.ObjUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.citc.nce.auth.constant.csp.common.CSPOperatorCodeEnum;
import com.citc.nce.auth.csp.recharge.Const.PaymentTypeEnum;
import com.citc.nce.auth.csp.recharge.entity.ChargeConsumeStatistic;
import com.citc.nce.auth.csp.recharge.service.ChargeConsumeStatisticService;
import com.citc.nce.auth.postpay.config.vo.CustomerPostpayConfigVo;
import com.citc.nce.auth.postpay.customerconfig.service.CustomerPostpayConfigService;
import com.citc.nce.auth.postpay.order.entity.PostpayOrder;
import com.citc.nce.auth.postpay.order.entity.PostpayOrderDetail;
import com.citc.nce.auth.postpay.order.service.PostpayOrderDetailService;
import com.citc.nce.auth.postpay.order.service.PostpayOrderService;
import com.citc.nce.auth.prepayment.service.ICustomerPrepaymentConfigService;
import com.citc.nce.auth.prepayment.vo.CustomerPrepaymentConfigVo;
import com.citc.nce.auth.utils.MsgPaymentUtils;
import com.citc.nce.authcenter.csp.CspApi;
import com.citc.nce.authcenter.csp.CspCustomerApi;
import com.citc.nce.authcenter.csp.vo.UserInfoVo;
import com.citc.nce.common.core.enums.CustomerPayType;
import com.citc.nce.common.core.enums.MsgSubTypeEnum;
import com.citc.nce.common.core.enums.MsgTypeEnum;
import com.citc.nce.common.core.exception.BizException;
import com.citc.nce.robot.vo.SendMessageNumberDetail;
import com.citc.nce.robot.vo.messagesend.SimpleMessageSendNumberDetail;
import com.citc.nce.tenant.MsgRecordApi;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import static com.citc.nce.auth.postpay.order.enums.PostpayOrderStatus.EMPTY_ORDER;
import static com.citc.nce.auth.postpay.order.enums.PostpayOrderStatus.WAITING;
import static com.citc.nce.auth.utils.MsgPaymentUtils.MSG_PAYMENT_OPERATORS;
import static com.citc.nce.auth.utils.MsgPaymentUtils.generateOrderId;

/**
 * 消息订单生成定时任务
 *
 * @author jcrenc
 * @since 2024/3/7 10:13
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class MessageOrderGenerateSchedule {

    private final CspApi cspApi;
    private final CspCustomerApi cspCustomerApi;
    private final MsgRecordApi msgRecordApi;
    private final ChargeConsumeStatisticService chargeConsumeStatisticService;
    private final CustomerPostpayConfigService postpayConfigService;
    private final PostpayOrderService orderService;
    private final PostpayOrderDetailService orderDetailService;
    private final ObjectMapper objectMapper;
    private final ICustomerPrepaymentConfigService prepaymentConfigService;
    private final DateTimeFormatter amountDaysFormatter = DateTimeFormatter.ofPattern("yyyyMM");
    //是否开启根据消息类型记录总金额
    private static final boolean ENABLE_LOG_AMOUNT_BY_MSG_TYPE = true;

    //是否开启打印每条订单明细
    private static final boolean ENABLE_LOG_ORDER_DETAIL = true;

    @Transactional(rollbackFor = Exception.class)
    public void clear(List<String> cspList, String amountDay) {
        String amountDays;
        if (StringUtils.isNotEmpty(amountDay)){
            amountDays = amountDay;
        }else{
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime minusMonths = now.minusMonths(1);
            amountDays = minusMonths.format(amountDaysFormatter);
        }
        List<String> customerIds = null;
        if (CollectionUtils.isNotEmpty(cspList)) {
            customerIds = cspList.stream()
                    .flatMap(cspId -> cspCustomerApi.queryCustomerIdsByCspId(cspId).stream())
                    .collect(Collectors.toList());
        }

        List<PostpayOrder> orders = orderService.lambdaQuery()
                .eq(PostpayOrder::getPaymentDays, amountDays)
                .in(CollectionUtils.isNotEmpty(customerIds),PostpayOrder::getCustomerId,customerIds)
                .list();

        orderService.removeBatchByIds(orders);
        LambdaQueryWrapper<PostpayOrderDetail> queryWrapper = new LambdaQueryWrapper<PostpayOrderDetail>()
                .in(PostpayOrderDetail::getOrderId,
                        orders.stream()
                                .map(PostpayOrder::getOrderId)
                                .collect(Collectors.toSet())
                );
        LambdaQueryWrapper<ChargeConsumeStatistic> chargeConsumeStatisticQueryWrapper = new LambdaQueryWrapper<ChargeConsumeStatistic>()
                .in(ChargeConsumeStatistic::getOrderId,
                        orders.stream()
                                .map(PostpayOrder::getOrderId)
                                .collect(Collectors.toSet())
                );
        orderDetailService.remove(queryWrapper);
        chargeConsumeStatisticService.remove(chargeConsumeStatisticQueryWrapper);
    }

    @Transactional(rollbackFor = Exception.class)
    @SuppressWarnings("all")
    public void manualGenerate(List<String> cspList, String amountDay) {
        try {
            log.info("================ 【客户消息月度账单结算】定时任务开始 ===================");
            String amountDays;
            LocalDateTime start;
            LocalDateTime end;
            if (StringUtils.isNotEmpty(amountDay)) {
                amountDays = amountDay;
                YearMonth yearMonth = YearMonth.parse(amountDay, amountDaysFormatter);
                LocalDate localDate = yearMonth.atDay(1);
                start = localDate.with(TemporalAdjusters.firstDayOfMonth()).atTime(LocalTime.MIN);
                end = localDate.with(TemporalAdjusters.lastDayOfMonth()).atTime(LocalTime.MAX);
            } else {
                LocalDateTime now = LocalDateTime.now();
                LocalDateTime minusMonths = now.minusMonths(1);
                amountDays = amountDaysFormatter.format(minusMonths);
                start = minusMonths.with(TemporalAdjusters.firstDayOfMonth()).withHour(0).withMinute(0).withSecond(0);
                end = minusMonths.with(TemporalAdjusters.lastDayOfMonth()).withHour(23).withMinute(59).withSecond(59);
            }
            List<String> cspIds = CollectionUtils.isNotEmpty(cspList)
                    ? cspList
                    : cspApi.queryAllCspId().getCspIds();
            generate(cspIds,start,end,amountDay);
            log.info("================ 【月度账单结算完成】 ===================");
        } catch (Throwable throwable) {
            log.error("订单生成失败:{}", throwable.getMessage(), throwable);
            throw new BizException("");
        }
    }
    private void generate(List<String> cspIds,LocalDateTime start,LocalDateTime end,String billMonth) throws JsonProcessingException {
        log.info("账期:{}-{} csp size:{}", start.format(DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss")),end, cspIds.size());
        for (String cspId : cspIds) {
            List<UserInfoVo> customers = cspCustomerApi.getAllCustomer(cspId);
            log.info("=========== csp:{} start ===========", cspId);
            for (UserInfoVo userInfo : customers) {
                String customerId = userInfo.getCustomerId();
                String username = userInfo.getName();
                CustomerPayType payType = userInfo.getPayType();
                Assert.notNull(payType, "customer payType couldn't be null");
                log.info("{} 客户【{}】【{}】开始结算", payType.getDesc(), username, customerId);
                String orderId = generateOrderId();
                List<PostpayOrderDetail> orderDetails = null; //为null时不生成订单，为空list时生成金额为0的订单
                String priceDetail = null; //结算时用户消息单价配置快照
                BigDecimal chargeAmount=BigDecimal.ZERO;
                switch (payType) {
                    case PREPAY: {
                        //预付费用户需要生成回落消息的后付费订单
                        CustomerPrepaymentConfigVo prepaymentConfig = prepaymentConfigService.queryCustomerPrepaymentConfig(customerId);
                        if (prepaymentConfig != null) {
                            orderDetails = handlePrepayment(customerId, prepaymentConfig, start, end, orderId);
                            priceDetail = objectMapper.writeValueAsString(prepaymentConfig);
                            chargeAmount = chargeConsumeStatisticService.generateBill(billMonth, customerId, start, end, orderId, CustomerPayType.POSTPAY.getCode());
                        }else {
                            log.info("{}未配置预付费套餐资费不统计套餐账单",username);
                        }
                        break;
                    }
                    case POSTPAY: {
                        CustomerPostpayConfigVo postpayConfig = postpayConfigService.queryCustomerPostpayConfig(customerId);
                        if (postpayConfig != null) {
                        orderDetails = handlePostpayPayment(customerId, postpayConfig, start, end, orderId);
                        priceDetail = objectMapper.writeValueAsString(postpayConfig);
                    }
                        else {
                            log.info("{}未配置后付费套餐资费不统计套餐账单",username);
                        }
                        chargeAmount=chargeConsumeStatisticService.generateBill(billMonth,customerId,start,end,orderId,CustomerPayType.POSTPAY.getCode());
                        break;
                    }
                    default:
                        throw new BizException("unknown customer payType " + payType);
                }
                if (orderDetails == null&&chargeAmount.equals(BigDecimal.valueOf(-1L))) {
                    //不生成订单
                    log.info("【{}】无需生成账单", username);
                    continue;
                }
                BigDecimal orderAmount=chargeAmount.equals(BigDecimal.valueOf(-1L))?BigDecimal.ZERO:chargeAmount;
                //计算订单总金额
                if(orderDetails != null) {
                    log.info("{}套餐使用数量{}",username,orderDetails.size());
                    BigDecimal Postpay = orderDetails.stream()
                            .map(PostpayOrderDetail::getAmount)
                            .map(BigDecimal::new)
                            .reduce(BigDecimal.ZERO, BigDecimal::add);
                    orderAmount=orderAmount.add(Postpay);
                    log.info("套餐账单金额为：{}",Postpay);
                    orderDetailService.saveBatch(orderDetails);
                }
                orderAmount = MsgPaymentUtils.formatOrderAmount(orderAmount);

                //保存订单和明细
                PostpayOrder postpayOrder = new PostpayOrder()
                        .setCustomerId(customerId)
                        .setPaymentDays(billMonth)
                        .setPriceDetail(priceDetail)
                        .setOrderId(orderId)
                        .setAmount(orderAmount.toPlainString())
                        .setStatus(BigDecimal.ZERO.compareTo(orderAmount) == 0 ? EMPTY_ORDER : WAITING)
                        .setInvoicing(false)
                        .setInvoicableAmount(orderAmount);
                orderService.save(postpayOrder);


                //打印日志
                loggingOrder(userInfo, postpayOrder, orderDetails);
            }
            log.info("=========== csp:{} end ===========", cspId);
        }
    }
    /**
     * 1.未配置后付费单价的历史用户不出账单；
     * 2.后续走正常流程新增的后付费客户，若某类消息单价为0，但发送量不为0，账单正常出具，账单金额为0（但不能开票，在开票记录中不显示）。
     * 3.其余情况均正常出账单。
     * 账期：账期由4位年份加2位月份组成。
     * 账单金额：账单金额为下拉列表中各类消息账单之和。
     * 账单金额：套餐账单金额+资费账单金额
     * 账单生成时间：默认是每月的4号凌晨由系统自动生成。
     */
    @Scheduled(cron = "0 0 0 4 * *")
    @Transactional(rollbackFor = Exception.class)
    public void generateMonthlyBilling() {
        try {
            log.info("================ 【客户消息月度账单结算】定时任务开始 ===================");
            String amountDays;
            LocalDateTime start;
            LocalDateTime end;
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime minusMonths = now.minusMonths(1);
            amountDays = amountDaysFormatter.format(minusMonths);
            start = minusMonths.with(TemporalAdjusters.firstDayOfMonth()).withHour(0).withMinute(0).withSecond(0);
            end = minusMonths.with(TemporalAdjusters.lastDayOfMonth()).withHour(23).withMinute(59).withSecond(59);
            List<String> cspIds = cspApi.queryAllCspId().getCspIds();
            generate(cspIds,start,end,amountDays);
            log.info("================ 【月度账单结算完成】 ===================");
        } catch (Throwable throwable) {
            log.error("订单生成失败:{}", throwable.getMessage(), throwable);
            throw new BizException("");
        }
    }

    private List<PostpayOrderDetail> handlePrepayment(String customerId, CustomerPrepaymentConfigVo prepaymentConfig, LocalDateTime start, LocalDateTime end, String orderId) {
        List<SimpleMessageSendNumberDetail> sendNumberDetails = msgRecordApi.queryFallbackMessageSendNumberDetail(customerId, start, end);
        if (CollectionUtils.isNotEmpty(sendNumberDetails)) {
            return generate5gFallbackDetail(prepaymentConfig.getFallbackPrice(), sendNumberDetails, orderId);
        }
        return null;
    }

    private List<PostpayOrderDetail> handlePostpayPayment(String customerId, CustomerPostpayConfigVo postpayConfig, LocalDateTime start, LocalDateTime end, String orderId) {
        Map<CSPOperatorCodeEnum, CustomerPostpayConfigVo.Config> _5gConfig = postpayConfig.getFifthConfigMap();
        List<PostpayOrderDetail> orderDetails = new ArrayList<>();
        //查询用户在账期内所有消息的发送情况
        SendMessageNumberDetail sendNumberDetail = msgRecordApi.querySendNumberDetail(customerId, start, end);
        //根据消息类型和账号生成订单明细
        if (CollectionUtils.isNotEmpty(sendNumberDetail.getMediaUsage())) {
            orderDetails.addAll(generateMediaOrderDetail(postpayConfig.getVideoPrice(), sendNumberDetail.getMediaUsage(), orderId));
        }
        if (CollectionUtils.isNotEmpty(sendNumberDetail.getSmsUsage())) {
            orderDetails.addAll(generateSmsOrderDetail(postpayConfig.getSmsPrice(), sendNumberDetail.getSmsUsage(), orderId));
        }
        if (CollectionUtils.isNotEmpty(sendNumberDetail.get_5gTextUsage())) {
            orderDetails.addAll(generate5gTextOrderDetail(_5gConfig, sendNumberDetail.get_5gTextUsage(), orderId));
        }
        if (CollectionUtils.isNotEmpty(sendNumberDetail.get_5gRichUsage())) {
            orderDetails.addAll(generate5gRichOrderDetail(_5gConfig, sendNumberDetail.get_5gRichUsage(), orderId));
        }
        if (CollectionUtils.isNotEmpty(sendNumberDetail.get_5gConversationUsage())) {
            orderDetails.addAll(generate5gConversationDetail(_5gConfig, sendNumberDetail.get_5gConversationUsage(), orderId));
        }
        if (CollectionUtils.isNotEmpty(sendNumberDetail.get_5gFallbackUsage())) {
            orderDetails.addAll(generate5gFallbackDetail(_5gConfig, sendNumberDetail.get_5gFallbackUsage(), orderId));
        }
        return orderDetails;
    }

    private void loggingOrder(UserInfoVo userInfo, PostpayOrder order, List<PostpayOrderDetail> details) {
        log.info("{} 客户 【{}】账期为【{}】的后付费订单生成完成，总金额:{} 订单ID:{}", userInfo.getPayType().getDesc(), userInfo.getName(), order.getPaymentDays(), order.getAmount(), order.getOrderId());
        if (ENABLE_LOG_AMOUNT_BY_MSG_TYPE&& ObjUtil.isNotNull(details)) {
            Map<MsgTypeEnum, Map<MsgSubTypeEnum, List<PostpayOrderDetail>>> groupingDetails = details.stream()
                    .collect(
                            Collectors.groupingBy(
                                    PostpayOrderDetail::getMsgType,
                                    groupingByMsgSubType()  //自带的收集器不能收集键为null的情况，所以自定义一个
                            ));
            for (MsgTypeEnum msgTypeEnum : groupingDetails.keySet()) {
                for (MsgSubTypeEnum subType : groupingDetails.get(msgTypeEnum).keySet()) {
                    List<PostpayOrderDetail> detailList = groupingDetails.get(msgTypeEnum).get(subType);
                    BigDecimal amount = detailList.stream()
                            .map(PostpayOrderDetail::getAmount)
                            .map(BigDecimal::new)
                            .reduce(BigDecimal.ZERO, BigDecimal::add);
                    String msgType = subType == null ? msgTypeEnum.getDesc() : msgTypeEnum.getDesc() + "-" + subType.getDesc();
                    log.info("【{}】共 {} 条订单明细,总金额:{}", msgType, detailList.size(), amount);
                    if (ENABLE_LOG_ORDER_DETAIL) {
                        for (PostpayOrderDetail detail : detailList) {
                            String operatorName = detail.getOperatorCode() == null ? "" : detail.getOperatorCode().getName();
                            log.info("【{}】账号【{}】使用 【{}】 消息 {} 条，单价:{}，总金额:{}", operatorName, detail.getAccountId(), msgType, detail.getUsage(), detail.getPrice(), detail.getAmount());
                        }
                    }
                }
            }
        }
    }

    public static Collector<PostpayOrderDetail, ?, Map<MsgSubTypeEnum, List<PostpayOrderDetail>>> groupingByMsgSubType() {
        return Collector.of(
                // 初始化
                HashMap::new,
                // 累加器
                (map, detail) -> {
                    MsgSubTypeEnum subType = detail.getMsgSubType();
                    if (!map.containsKey(subType))
                        map.put(subType, new ArrayList<>());
                    map.get(subType).add(detail);
                },
                // 组合器
                (map1, map2) -> {
                    map2.forEach((key, value) -> map1.merge(key, value, (list1, list2) -> {
                        list1.addAll(list2);
                        return list1;
                    }));
                    return map1;
                },
                // 结束器
                Collector.Characteristics.IDENTITY_FINISH
        );
    }


    private static List<PostpayOrderDetail> generate5gFallbackDetail(Map<CSPOperatorCodeEnum, CustomerPostpayConfigVo.Config> _5gConfig, List<SimpleMessageSendNumberDetail> sendNumberDetails, String orderId) {
        List<PostpayOrderDetail> _5gFallbackDetails = new ArrayList<>();
        for (SimpleMessageSendNumberDetail useDetail : sendNumberDetails) {
            _5gFallbackDetails.add(
                    createOrderDetail(
                            MsgTypeEnum.M5G_MSG,
                            MsgSubTypeEnum.FALLBACK,
                            useDetail.getAccount(),
                            useDetail.getOperator(),
                            _5gConfig.get(useDetail.getOperator()).getFallbackPrice(),
                            useDetail.getUsage(),
                            orderId)
            );
        }
        return _5gFallbackDetails;
    }


    private static List<PostpayOrderDetail> generate5gFallbackDetail(BigDecimal fallbackPrice, List<SimpleMessageSendNumberDetail> sendNumberDetails, String orderId) {
        List<PostpayOrderDetail> _5gFallbackDetails = new ArrayList<>();
        for (SimpleMessageSendNumberDetail useDetail : sendNumberDetails) {
            CSPOperatorCodeEnum operator = useDetail.getOperator();
            if (!MSG_PAYMENT_OPERATORS.contains(operator)) {
                log.info("{} 运营商无需生成订单，使用明细：{}", operator.getName(), useDetail);
                continue;
            }
            _5gFallbackDetails.add(
                    createOrderDetail(
                            MsgTypeEnum.M5G_MSG,
                            MsgSubTypeEnum.FALLBACK,
                            useDetail.getAccount(),
                            useDetail.getOperator(),
                            fallbackPrice,
                            useDetail.getUsage(),
                            orderId)
            );
        }
        return _5gFallbackDetails;
    }

    private static List<PostpayOrderDetail> generate5gConversationDetail(Map<CSPOperatorCodeEnum, CustomerPostpayConfigVo.Config> _5gConfig, List<SimpleMessageSendNumberDetail> sendNumberDetails, String orderId) {
        List<PostpayOrderDetail> _5gConversationDetails = new ArrayList<>();
        for (SimpleMessageSendNumberDetail useDetail : sendNumberDetails) {
            CSPOperatorCodeEnum operator = useDetail.getOperator();
            if (!MSG_PAYMENT_OPERATORS.contains(operator)) {
                log.info("{} 运营商无需生成订单，使用明细：{}", operator.getName(), useDetail);
                continue;
            }
            _5gConversationDetails.add(
                    createOrderDetail(
                            MsgTypeEnum.M5G_MSG,
                            MsgSubTypeEnum.CONVERSATION,
                            useDetail.getAccount(),
                            useDetail.getOperator(),
                            _5gConfig.get(useDetail.getOperator()).getConversionPrice(),
                            useDetail.getUsage(),
                            orderId)
            );
        }
        return _5gConversationDetails;
    }

    private static List<PostpayOrderDetail> generate5gRichOrderDetail(Map<CSPOperatorCodeEnum, CustomerPostpayConfigVo.Config> _5gConfig, List<SimpleMessageSendNumberDetail> sendNumberDetails, String orderId) {
        List<PostpayOrderDetail> _5gRichDetails = new ArrayList<>();
        for (SimpleMessageSendNumberDetail useDetail : sendNumberDetails) {
            CSPOperatorCodeEnum operator = useDetail.getOperator();
            if (!MSG_PAYMENT_OPERATORS.contains(operator)) {
                log.info("{} 运营商无需生成订单，使用明细：{}", operator.getName(), useDetail);
                continue;
            }
            _5gRichDetails.add(
                    createOrderDetail(
                            MsgTypeEnum.M5G_MSG,
                            MsgSubTypeEnum.RICH,
                            useDetail.getAccount(),
                            useDetail.getOperator(),
                            _5gConfig.get(useDetail.getOperator()).getRichMessagePrice(),
                            useDetail.getUsage(),
                            orderId)
            );
        }
        return _5gRichDetails;
    }

    private static List<PostpayOrderDetail> generate5gTextOrderDetail(Map<CSPOperatorCodeEnum, CustomerPostpayConfigVo.Config> _5gConfig, List<SimpleMessageSendNumberDetail> sendNumberDetails, String orderId) {
        List<PostpayOrderDetail> _5gTextDetails = new ArrayList<>();
        for (SimpleMessageSendNumberDetail useDetail : sendNumberDetails) {
            CSPOperatorCodeEnum operator = useDetail.getOperator();
            if (!MSG_PAYMENT_OPERATORS.contains(operator)) {
                log.info("{} 运营商无需生成订单，使用明细：{}", operator.getName(), useDetail);
                continue;
            }
            _5gTextDetails.add(
                    createOrderDetail(
                            MsgTypeEnum.M5G_MSG,
                            MsgSubTypeEnum.TEXT,
                            useDetail.getAccount(),
                            operator,
                            _5gConfig.get(operator).getTextMessagePrice(),
                            useDetail.getUsage(),
                            orderId)
            );
        }
        return _5gTextDetails;
    }

    private static List<PostpayOrderDetail> generateSmsOrderDetail(BigDecimal smsPrice, List<SimpleMessageSendNumberDetail> smsAccountUseDetailList, String orderId) {
        List<PostpayOrderDetail> smsDetails = new ArrayList<>();
        for (SimpleMessageSendNumberDetail useDetail : smsAccountUseDetailList) {
            smsDetails.add(createOrderDetail(MsgTypeEnum.SHORT_MSG, null, useDetail.getAccount(), null, smsPrice, useDetail.getUsage(), orderId));
        }
        return smsDetails;
    }

    private static List<PostpayOrderDetail> generateMediaOrderDetail(BigDecimal videoPrice, List<SimpleMessageSendNumberDetail> mediaAccountUseDetailList, String orderId) {
        List<PostpayOrderDetail> mediaDetails = new ArrayList<>();
        for (SimpleMessageSendNumberDetail useDetail : mediaAccountUseDetailList) {
            mediaDetails.add(createOrderDetail(MsgTypeEnum.MEDIA_MSG, null, useDetail.getAccount(), null, videoPrice, useDetail.getUsage(), orderId));
        }
        return mediaDetails;
    }

    private static PostpayOrderDetail createOrderDetail(MsgTypeEnum msgType,
                                                        MsgSubTypeEnum subType,
                                                        String account,
                                                        CSPOperatorCodeEnum operatorCode,
                                                        BigDecimal price,
                                                        Long usage,
                                                        String orderId) {
        String amount = MsgPaymentUtils.formatOrderAmount(price.multiply(BigDecimal.valueOf(usage))).toPlainString();
        return new PostpayOrderDetail()
                .setOrderId(orderId)
                .setAccountId(account)
                .setOperatorCode(operatorCode)
                .setMsgType(msgType)
                .setMsgSubType(subType)
                .setUsage(usage)
                .setPrice(price)
                .setAmount(amount);
    }
}
