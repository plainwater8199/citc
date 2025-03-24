package com.citc.nce.auth.prepayment.service;

import com.citc.nce.auth.orderRefund.vo.ResidueInfo;
import com.citc.nce.auth.prepayment.entity.PrepaymentOrderDetail;
import com.baomidou.mybatisplus.extension.service.IService;
import com.citc.nce.auth.prepayment.vo.MessagePlanDetailDto;

import java.util.List;

/**
 * <p>
 * 预付费订单详情表 服务类
 * </p>
 *
 * @author jcrenc
 * @since 2024-01-25 10:01:03
 */
public interface IPrepaymentOrderDetailService extends IService<PrepaymentOrderDetail> {

    void saveOrderDetails(String orderId, List<MessagePlanDetailDto> planDetails);

    /**
     * @param orderId 订单编码
     */
    List<PrepaymentOrderDetail> listByOrderId(String orderId);


    /**
     * 订单全额退款
     * @param order 订单id
     * @return 退款前用户使用情况
     */
    List<ResidueInfo> refundAll(String order);

    /**
     * 查询当前订单当前使用额度
     * @param orderId 订单id
     */
    List<ResidueInfo> getResidueInfo(String orderId);
}
