package com.citc.nce.auth.orderRefund.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.citc.nce.auth.invoice.service.InvoiceAccountService;
import com.citc.nce.auth.messageplan.entity.FifthMessagePlan;
import com.citc.nce.auth.messageplan.entity.SmsPlan;
import com.citc.nce.auth.messageplan.entity.VideoSmsPlan;
import com.citc.nce.auth.orderRefund.dao.OrderRefundMapper;
import com.citc.nce.auth.orderRefund.domain.OrderRefund;
import com.citc.nce.auth.orderRefund.service.IOrderRefundService;
import com.citc.nce.auth.orderRefund.vo.*;
import com.citc.nce.auth.orderRefund.vo.refundFilter.*;
import com.citc.nce.auth.prepayment.entity.PrepaymentOrder;
import com.citc.nce.auth.prepayment.service.IPrepaymentOrderDetailService;
import com.citc.nce.auth.prepayment.service.IPrepaymentOrderService;
import com.citc.nce.authcenter.csp.CspCustomerApi;
import com.citc.nce.authcenter.csp.vo.UserEnterpriseInfoVo;
import com.citc.nce.common.core.enums.MsgSubTypeEnum;
import com.citc.nce.common.core.enums.MsgTypeEnum;
import com.citc.nce.common.core.exception.BizException;
import com.citc.nce.common.core.exception.GlobalErrorCode;
import com.citc.nce.common.core.pojo.PageResult;
import com.citc.nce.common.util.SessionContextUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 订单退款 服务实现类
 * </p>
 *
 * @author bydud
 * @since 2024-03-12 04:03:10
 */
@Service
@Slf4j
public class OrderRefundServiceImpl extends ServiceImpl<OrderRefundMapper, OrderRefund> implements IOrderRefundService {
    @Autowired
    private IPrepaymentOrderService orderService;

    @Autowired
    private IPrepaymentOrderDetailService orderDetailService;

    @Autowired
    private InvoiceAccountService accountService;

    @Autowired
    private CspCustomerApi customerApi;
    @Autowired
    private ObjectMapper objectMapper;

    @Override
    @Transactional
    @SneakyThrows(JsonProcessingException.class)
    public void add(OrderRefundAdd refund) {
        PrepaymentOrder order = orderService.getById(refund.getOrderId());
        if (Objects.isNull(order)) {
            throw new BizException("订单不存在");
        }
        SessionContextUtil.sameCsp(order.getCustomerId().substring(0, 10));

        BigDecimal amount = new BigDecimal(order.getAmount());//订单金额
        //总金额-输入退款金额-已退款金额
        BigDecimal subtract = amount.subtract(refund.getRefundAmount()).subtract(order.getRefund());
        if (subtract.compareTo(BigDecimal.ZERO) < 0) {
            throw new BizException("可退款金额不足");
        }
        //退款金额=输入金额+之前退款的金额
        order.setRefund(refund.getRefundAmount().add(order.getRefund()));
        //可开票金额=订单总金额-退款总金额
        order.setInvoicableAmount(amount.subtract(order.getRefund()));
        orderService.updateById(order);//修改订单


        //本次退款
        OrderRefund orderRefund = new OrderRefund();
        orderRefund.setCustomerId(refund.getCustomerId());
        orderRefund.setOrderId(order.getOrderId());
        orderRefund.setRefundAmount(refund.getRefundAmount());
        orderRefund.setRemark(refund.getRemark());
        orderRefund.setTOrderId(order.getId());
        orderRefund.setMsgType(order.getMsgType());
        orderRefund.setAccountId(order.getAccountId());
        List<ResidueInfo> residueInfos = orderDetailService.refundAll(order.getOrderId());
        orderRefund.setResidueInfoList(objectMapper.writeValueAsString(residueInfos));
        save(orderRefund);
    }

    @Override
    public PageResult<OrderRefundPageInfo> refundPage(OrderRefundPageQuery refund) {
        Page<OrderRefund> page = new Page<>(refund.getPageNo(), refund.getPageSize());
        //客户id集合
        final Map<String, String> userMap = new HashMap<>(10);
        if (StringUtils.hasLength(refund.getEnterpriseAccountName())) {
            Map<String, String> userCollect = customerApi.getUserUserIdsLikeEnterpriseAccountName(refund.getEnterpriseAccountName())
                    .stream().collect(Collectors.toMap(UserEnterpriseInfoVo::getUserId, UserEnterpriseInfoVo::getEnterpriseAccountName));
            if (userCollect.isEmpty()) {
                return new PageResult<>(new ArrayList<>(2), 0L);
            }
            userMap.putAll(userCollect);
        }
        //查询退款数据
        page(page, new LambdaQueryWrapper<OrderRefund>()
                .eq(StringUtils.hasLength(refund.getCspId()), OrderRefund::getCreator, refund.getCspId())
                .in(StringUtils.hasLength(refund.getEnterpriseAccountName()), OrderRefund::getCustomerId, userMap.keySet())
                .like(StringUtils.hasLength(refund.getOrderId()), OrderRefund::getOrderId, refund.getOrderId())
                .eq(Objects.nonNull(refund.getMsgType()), OrderRefund::getMsgType, refund.getMsgType())
                .orderByDesc(OrderRefund::getCreateTime)
        );

        //封装数据
        List<OrderRefundPageInfo> list = new ArrayList<>(refund.getPageSize());
        HashMap<String, String> map = new HashMap<>();
        if (!CollectionUtils.isEmpty(page.getRecords())) {
            if (userMap.isEmpty()) {
                Set<String> customerIds = page.getRecords().stream().map(OrderRefund::getCustomerId).collect(Collectors.toSet());
                userMap.putAll(customerApi.getUserEnterpriseInfoByUserIds(customerIds).stream()
                        .collect(Collectors.toMap(UserEnterpriseInfoVo::getUserId, UserEnterpriseInfoVo::getEnterpriseAccountName)));
            }

            list = page.getRecords().stream().map(s -> {
                OrderRefundPageInfo info = new OrderRefundPageInfo();
                BeanUtils.copyProperties(s, info);
                info.setAccountName(accountService.getAccountName(map, s.getAccountId(), s.getMsgType()));
                info.setEnterpriseAccountName(userMap.get(s.getCustomerId()));
                if (StringUtils.hasLength(s.getResidueInfoList())) {
                    try {
                        info.setResidueInfo(objectMapper.readerForListOf(ResidueInfo.class).readValue(s.getResidueInfoList()));
                    } catch (JsonProcessingException e) {
                        log.error("json 解析失败", e);
                    }
                }
                return info;
            }).collect(Collectors.toList());
        }
        return new PageResult<>(list, page.getTotal());
    }

    @Override
    public void editRemark(OrderRefundEditRemark remark) {
        OrderRefund refund = getById(remark.getOrId());
        if (Objects.isNull(refund)) {
            throw new BizException(GlobalErrorCode.USER_AUTH_ERROR);
        }
        SessionContextUtil.sameCsp(refund.getCreator());
        refund.setRemark(remark.getRemark());
        updateById(refund);
    }

    @Override
    public OrderRefundFilterResult refundQuery(OrderRefundFilter filter) {
        OrderRefundFilterResult result = new OrderRefundFilterResult();

        //查询能退款订单的用户
        List<String> customerIds = orderService.enableRefundCustomer(SessionContextUtil.verifyCspLogin(), filter.getCustomerId());
        result.setCustomerIdList(getCustomerInfo(customerIds));
        //选择了客户，查询拥有的消息类型
        if (!CollectionUtils.isEmpty(result.getCustomerIdList()) && StringUtils.hasLength(filter.getCustomerId())) {
            List<MsgTypeEnum> msgTypeList = orderService.enableRefundMsgType(filter.getCustomerId());
            result.setMsgTypeList(msgTypeList);
        }
        //选择了消息类型，查询机器人账号
        if (!CollectionUtils.isEmpty(result.getMsgTypeList()) && Objects.nonNull(filter.getMsgType())) {
            MsgTypeEnum msgTypeEnum = filter.getMsgType();
            String customerId = result.getCustomerIdList().get(0).getCustomerId();
            List<String> accountList = orderService.enableRefundAccount(customerId, msgTypeEnum);
            result.setAccountList(getAccountList(msgTypeEnum, accountList));
        }

        //选择了机器人，查询订单
        if (!CollectionUtils.isEmpty(result.getAccountList()) && StringUtils.hasLength(filter.getAccountId())) {
            String customerId = filter.getCustomerId();
            MsgTypeEnum msgTypeEnum = filter.getMsgType();
            String accountId = filter.getAccountId();
            List<Long> tOrderId = orderService.enableRefundOrder(customerId, msgTypeEnum, accountId);
            result.setRefundOrderList(getRefundOrderList(tOrderId));//此处查询明细，只有订单编号和id
        }
        return result;
    }

    @Override
    public OrderInfo refundQueryOrder(Long orderId) {
        PrepaymentOrder order = orderService.getById(orderId);
        if (Objects.isNull(order)) {
            throw new BizException("订单不存在");
        }
        OrderInfo orderInfo = new OrderInfo();
        orderInfo.setPlanDetail(order.getPlanDetail());
        List<ResidueInfo> residueInfo = orderDetailService.getResidueInfo(order.getOrderId());
        setUnitPrice(order.getMsgType(), order.getPlanDetail(), residueInfo);
        orderInfo.setResidueInfoList(residueInfo);

        //计算可退款金额
        BigDecimal amount = new BigDecimal(order.getAmount());//订单总金额
        BigDecimal refund = order.getRefund();//已退款金额
        orderInfo.setAmount(amount);
        orderInfo.setMaxRefund(amount.compareTo(refund) <= 0 ? BigDecimal.ZERO : amount.subtract(refund));

        return orderInfo;
    }

    private List<OrderInfo> getRefundOrderList(List<Long> tOrderId) {
        List<PrepaymentOrder> orderList = orderService.listByIds(tOrderId);
        return orderList.stream().map(s -> {
            OrderInfo orderInfo = new OrderInfo();
            orderInfo.setId(s.getId());
            orderInfo.setOrderId(s.getOrderId());
            return orderInfo;
        }).collect(Collectors.toList());
    }


    //RefundOrder 设置单价
    private void setUnitPrice(MsgTypeEnum msgType, String planDetail, List<ResidueInfo> residueInfo) {
        if (Objects.nonNull(msgType) && StringUtils.hasLength(planDetail)) {
            try {
                if (MsgTypeEnum.M5G_MSG.equals(msgType)) {
                    FifthMessagePlan meal = objectMapper.readValue(planDetail, FifthMessagePlan.class);
                    for (ResidueInfo residue : residueInfo) {
                        MsgSubTypeEnum msgSubType = residue.getMsgSubType();
                        if (MsgSubTypeEnum.TEXT.equals(msgSubType)) {
                            residue.setUnitPrice(meal.getTextMessagePrice());
                        }
                        if (MsgSubTypeEnum.RICH.equals(msgSubType)) {
                            residue.setUnitPrice(meal.getRichMessagePrice());
                        }
                        if (MsgSubTypeEnum.CONVERSATION.equals(msgSubType)) {
                            residue.setUnitPrice(meal.getConversionPrice());
                        }
                    }
                }
                if (MsgTypeEnum.MEDIA_MSG.equals(msgType)) {
                    VideoSmsPlan meal = objectMapper.readValue(planDetail, VideoSmsPlan.class);
                    for (ResidueInfo residue : residueInfo) {
                        residue.setUnitPrice(meal.getPrice());
                    }
                }
                if (MsgTypeEnum.SHORT_MSG.equals(msgType)) {
                    SmsPlan meal = objectMapper.readValue(planDetail, SmsPlan.class);
                    for (ResidueInfo residue : residueInfo) {
                        residue.setUnitPrice(meal.getPrice());
                    }
                }
            } catch (JsonProcessingException jsonMappingException) {
                log.error("设置单价失败 json 映射错误", jsonMappingException);
            }
        }
    }

    private List<ChannelInfo> getAccountList(MsgTypeEnum msgTypeEnum, List<String> accountList) {
        if (CollectionUtils.isEmpty(accountList)) return new ArrayList<>();
        return accountList.stream().map(s -> new ChannelInfo(s, accountService.getAccountName(null, s, msgTypeEnum)))
                .collect(Collectors.toList());
    }

    private List<CustomerInfo> getCustomerInfo(List<String> customerIds) {
        if (CollectionUtils.isEmpty(customerIds)) return new ArrayList<>();
        Collection<UserEnterpriseInfoVo> userEnterpriseInfo = customerApi.getUserEnterpriseInfoByUserIds(customerIds);
        return userEnterpriseInfo.stream()
                .map(s -> {
                    CustomerInfo customerInfo = new CustomerInfo();
                    BeanUtils.copyProperties(s, customerInfo);
                    customerInfo.setCustomerId(s.getUserId());
                    return customerInfo;
                }).collect(Collectors.toList());
    }


}
