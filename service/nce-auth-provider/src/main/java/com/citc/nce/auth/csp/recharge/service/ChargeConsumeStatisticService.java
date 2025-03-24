package com.citc.nce.auth.csp.recharge.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.citc.nce.auth.csp.recharge.entity.ChargeConsumeStatistic;
import com.citc.nce.auth.postpay.config.vo.CustomerPostpayConfigVo;
import com.citc.nce.auth.postpay.order.entity.PostpayOrderDetail;
import com.citc.nce.auth.postpay.order.vo.PostpayOrderDetailVo;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @author yy
 * @date 2024-10-23 16:55:50
 */
public interface ChargeConsumeStatisticService extends IService<ChargeConsumeStatistic> {
    BigDecimal generateBill(String paymentDays,String customerId, LocalDateTime start, LocalDateTime end, String orderId,int payType);
    List<PostpayOrderDetailVo> getDetail(String orderId);
    List<PostpayOrderDetail> getStatisticDetailDo(String orderId);
}
