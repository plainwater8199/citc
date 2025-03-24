package com.citc.nce.auth.prepayment.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.citc.nce.auth.csp.recharge.vo.RechargeReq;
import com.citc.nce.auth.invoice.vo.InvoiceOrderPrePageInfo;
import com.citc.nce.auth.invoice.vo.InvoiceOrderPreQuery;
import com.citc.nce.auth.prepayment.entity.PrepaymentOrder;
import com.citc.nce.auth.prepayment.vo.*;
import com.citc.nce.common.core.enums.MsgSubTypeEnum;
import com.citc.nce.common.core.enums.MsgTypeEnum;
import com.citc.nce.common.core.pojo.PageResult;

import java.math.BigDecimal;
import java.util.List;

/**
 * <p>
 * 预付费订单 服务类
 * </p>
 *
 * @author jcrenc
 * @since 2024-01-25 10:01:02
 */
public interface IPrepaymentOrderService extends IService<PrepaymentOrder> {

    void addPrepaymentOrder(PrepaymentOrderAddVo addVo);

    void recharge(RechargeReq req);
    PageResult<FifthPlanOrderListVo> query5gOrderList(FifthPlanOrderQueryVo queryVo);

    PageResult<SmsPlanOrderListVo> querySmsOrderList(SmsPlanOrderQueryVo queryVo);

    PageResult<VideoSmsPlanOrderListVo> queryVideoSmsOrderList(VideoSmsPlanOrderQueryVo queryVo);

    /**
     * 客户搜索预付费订单
     */
    PageResult<PrepaymentOrderListVo> customerSearch(PrepaymentOrderCustomerSearchVo searchVo);

    /**
     * 客户取消订单
     *
     * @param id 要取消的订单ID
     */
    void customerCancelOrder(Long id);

    /**
     * csp搜索预付费订单
     */
    PageResult<PrepaymentOrderManageListVo> managerSearch(PrepaymentOrderManagerSearchVo searchVo);

    /**
     * csp给订单设置备注
     *
     * @param id
     * @param note
     */
    void noteOrder(Long id, String note);

    /**
     * csp取消订单
     *
     * @param id 要取消的订单ID
     */
    void managerCancelOrder(Long id);

    /**
     * 完成订单
     *
     * @param id           要完成的订单ID
     * @param serialNumber csp输入的订单流水号
     */
    void finishOrder(Long id, String serialNumber);

    PageResult<InvoiceOrderPrePageInfo> prePageSelect(InvoiceOrderPreQuery query);

    PrepaymentOrderManageListVo getByTOrderId(Long tOrderId);

    /**
     * 查询能退款订单的用户
     *
     * @param cspId      cspId
     * @param customerId 限制用户
     */
    List<String> enableRefundCustomer(String cspId, String customerId);

    List<MsgTypeEnum> enableRefundMsgType(String customerId);

    List<String> enableRefundAccount(String customerId, MsgTypeEnum msgTypeEnum);

    List<Long> enableRefundOrder(String customerId, MsgTypeEnum msgTypeEnum, String accountId);

    BigDecimal preInvoicableAmount(String cusId);

    /**
     * 根据消息类型查询消息可用条数
     *
     * @param accountId  账号id
     * @param msgTypeEnum 消息类型
     * @param subType     子类型
     * @return 返回实际可用条数
     */
    Long getRemainingCountByMessageType(String accountId, MsgTypeEnum msgTypeEnum, MsgSubTypeEnum subType);

    /**
     * 扣减消息剩余条数
     *
     * @param accountId    账号id
     * @param msgTypeEnum  消息类型
     * @param subType      子类型
     * @param deductNumber 扣减条数
     * @param deductNumber 次数
     */
    void deductRemaining(String accountId, MsgTypeEnum msgTypeEnum, MsgSubTypeEnum subType, Long deductNumber, Long chargeNum);

    /**
     * 返还消息条数
     *
     * @param accountId    账号id
     * @param msgTypeEnum  消息类型
     * @param subType      子类型
     * @param returnNumber 返还数量
     */
    void returnRemaining(String accountId, MsgTypeEnum msgTypeEnum, MsgSubTypeEnum subType, Long returnNumber);

    List<Integer> checkPaymentForMsgType();

    /**
     * 尝试扣减消息余额
     * @return 真实的扣除数量
     */
    Long tryDeductRemaining(String accountId, MsgTypeEnum msgTypeEnum, MsgSubTypeEnum subType, Long deductNumber, Long chargeNum);

    Boolean existsOrderByMsgTypeAndAccountId(String customerId, MsgTypeEnum msgType, String accountId);
}
