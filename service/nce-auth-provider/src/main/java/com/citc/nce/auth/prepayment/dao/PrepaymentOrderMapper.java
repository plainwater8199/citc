package com.citc.nce.auth.prepayment.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.citc.nce.auth.csp.recharge.Const.PrepaymentStatus;
import com.citc.nce.auth.prepayment.entity.PrepaymentOrder;
import com.citc.nce.auth.prepayment.vo.FifthPlanOrderListVo;
import com.citc.nce.auth.prepayment.vo.PrepaymentOrderListVo;
import com.citc.nce.auth.prepayment.vo.PrepaymentOrderManageListVo;
import com.citc.nce.auth.prepayment.vo.SmsPlanOrderListVo;
import com.citc.nce.auth.prepayment.vo.VideoSmsPlanOrderListVo;
import com.citc.nce.common.core.enums.MsgTypeEnum;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 * 预付费订单 Mapper 接口
 * </p>
 *
 * @author jcrenc
 * @since 2024-01-25 10:01:02
 */
public interface PrepaymentOrderMapper extends BaseMapper<PrepaymentOrder> {

    Page<FifthPlanOrderListVo> query5gOrder(@Param("chatbotAccount") String chatbotAccount, Page<FifthPlanOrderListVo> page);

    Page<SmsPlanOrderListVo> querySmsOrder(@Param("accountId") String accountId, Page<SmsPlanOrderListVo> page);

    Page<VideoSmsPlanOrderListVo> queryVideoSmsOrder(@Param("accountId") String accountId, Page<VideoSmsPlanOrderListVo> page);

    Page<PrepaymentOrderListVo> customerSearch(
            @Param("orderId") String orderId,
            @Param("type") Integer type,
            @Param("status") PrepaymentStatus status,
            @Param("customerId") String customerId,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime,
            @Param("consumeCategory") Integer consumeCategory,
            Page<PrepaymentOrderListVo> page);

    Page<PrepaymentOrderManageListVo> managerSearch(
            @Param("orderId") String orderId,
            @Param("customerName") String customerName,
            @Param("type") Integer type,
            @Param("status") PrepaymentStatus status,
            @Param("consumeCategory") Integer consumeCategory,
            @Param("cspId") String cspId,
            @Param("customer_table")String customerTable,
            Page<PrepaymentOrderManageListVo> page);

    PrepaymentOrderManageListVo selectByTOrderId(@Param("id") Long tOrderId);

    /**
     * 查询可退款id
     * 没删除  已支付 可用量大于0
     *
     * @param customerId 客户id
     */
    List<String> enableRefundByCustomerId(@Param("cspId") String cspId, @Param("customerId") String customerId);

    /**
     * 用户可退款类型
     * 没删除  已支付 可用量大于0 用户id是当前用户
     *
     * @param customerId 客户id
     */
    List<MsgTypeEnum> enableRefundMsgType(@Param("customerId") String customerId);

    List<String> enableRefundAccount(@Param("customerId") String customerId, @Param("msgTypeEnum") MsgTypeEnum msgTypeEnum);

    List<Long> enableRefundOrder(@Param("customerId") String customerId, @Param("msgTypeEnum") MsgTypeEnum msgTypeEnum, @Param("accountId") String accountId);

    BigDecimal sumPreInvoicableAmount(@Param("customerId") String cusId);

    List<Integer> checkPaymentForMsgType(@Param("customerId") String customerId);

}
