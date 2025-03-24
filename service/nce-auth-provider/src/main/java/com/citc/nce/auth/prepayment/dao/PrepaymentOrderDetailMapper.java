package com.citc.nce.auth.prepayment.dao;

import com.citc.nce.auth.prepayment.entity.PrepaymentOrderDetail;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.citc.nce.common.core.enums.MsgSubTypeEnum;
import com.citc.nce.common.core.enums.MsgTypeEnum;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 预付费订单详情表 Mapper 接口
 * </p>
 *
 * @author jcrenc
 * @since 2024-01-25 10:01:03
 */
public interface PrepaymentOrderDetailMapper extends BaseMapper<PrepaymentOrderDetail> {
    Long getRemainingCountByMessageType(@Param("accountId") String accountId, @Param("msgType") MsgTypeEnum msgType, @Param("msgSubType") MsgSubTypeEnum msgSubType);

    /**
     * 根据消息类型查询最老的可用订单
     *
     * @return orderId
     */
    String findOldestAvailableDetail(@Param("accountId") String accountId, @Param("msgType") MsgTypeEnum msgType, @Param("msgSubType") MsgSubTypeEnum msgSubType);

    String findNewestUsedDetail(@Param("accountId") String accountId, @Param("msgType") MsgTypeEnum msgType, @Param("msgSubType") MsgSubTypeEnum msgSubType);

    Long getRemainingCountByMessageTypeAndCustomerId(@Param("customerId") String customerId, @Param("msgType") MsgTypeEnum msgType, @Param("msgSubType") MsgSubTypeEnum msgSubType, @Param("accountIds") List<String> accountIds);

}
