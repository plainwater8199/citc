package com.citc.nce.auth.postpay.order.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.citc.nce.auth.postpay.order.entity.PostpayOrder;
import com.citc.nce.auth.postpay.order.enums.PostpayOrderStatus;
import com.citc.nce.auth.postpay.order.vo.PostpayOrderCustomerVo;
import com.citc.nce.auth.postpay.order.vo.PostpayOrderVo;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @author jcrenc
 * @since 2024/3/7 16:14
 */
public interface PostpayOrderMapper extends BaseMapper<PostpayOrder> {
    Page<PostpayOrderVo> searchPostpayOrder(@Param("paymentDays") String paymentDays,
                                            @Param("enterpriseAccountName") String enterpriseAccountName,
                                            @Param("status") PostpayOrderStatus status,
                                            @Param("cspId") String cspId,
                                            @Param("customer_table") String customer_table,
                                            Page<PostpayOrderVo> page);

    Page<PostpayOrderCustomerVo> customerSearchPostpayOrder(@Param("paymentDays") String paymentDays,
                                                            @Param("status") PostpayOrderStatus status,
                                                            @Param("customerId") String customerId,
                                                            @Param("startTime") LocalDateTime startTime,
                                                            @Param("endTime") LocalDateTime endTime,
                                                            Page<PostpayOrderCustomerVo> page);

    BigDecimal sumPostInvoicableAmount(@Param("customerId") String cusId);
}
