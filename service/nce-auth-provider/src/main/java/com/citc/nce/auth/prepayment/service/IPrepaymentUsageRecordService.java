package com.citc.nce.auth.prepayment.service;

import com.citc.nce.auth.prepayment.entity.PrepaymentUsageRecord;
import com.baomidou.mybatisplus.extension.service.IService;
import com.citc.nce.common.core.enums.MsgSubTypeEnum;
import com.citc.nce.common.core.enums.MsgTypeEnum;

/**
 * <p>
 * 预付费订单使用记录 服务类
 * </p>
 *
 * @author jcrenc
 * @since 2024-01-25 10:01:03
 */
public interface IPrepaymentUsageRecordService extends IService<PrepaymentUsageRecord> {

    void record(MsgTypeEnum msgType, MsgSubTypeEnum msgSubType,String orderId,Integer number);

}
