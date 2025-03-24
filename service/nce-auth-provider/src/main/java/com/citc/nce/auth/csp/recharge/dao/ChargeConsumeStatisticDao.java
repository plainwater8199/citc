package com.citc.nce.auth.csp.recharge.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.citc.nce.auth.csp.recharge.entity.ChargeConsumeStatistic;
import com.citc.nce.auth.postpay.order.entity.PostpayOrderDetail;
import com.citc.nce.auth.postpay.order.vo.PostpayOrderDetailVo;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author yy
 * @date 2024-10-23 16:58:31
 */
public interface ChargeConsumeStatisticDao extends BaseMapper<ChargeConsumeStatistic> {
    List<PostpayOrderDetailVo> getStatisticDetail(String orderId);
    List<PostpayOrderDetail> getStatisticDetailDo(String orderId);


    List<ChargeConsumeStatistic> generateBill(@Param("paymentDays")String paymentDays, @Param("customerId") String customerId, @Param("start") LocalDateTime start, @Param("end") LocalDateTime end, @Param("orderId") String orderId, @Param("payType") int payType);

}
