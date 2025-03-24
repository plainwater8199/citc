package com.citc.nce.auth.prepayment.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.citc.nce.auth.prepayment.dao.PrepaymentUsageRecordMapper;
import com.citc.nce.auth.prepayment.entity.PrepaymentUsageRecord;
import com.citc.nce.auth.prepayment.service.IPrepaymentUsageRecordService;
import com.citc.nce.common.core.enums.MsgSubTypeEnum;
import com.citc.nce.common.core.enums.MsgTypeEnum;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * <p>
 * 预付费订单使用记录 服务实现类
 * </p>
 *
 * @author jcrenc
 * @since 2024-01-25 10:01:03
 */
@Service
public class PrepaymentUsageRecordServiceImpl extends ServiceImpl<PrepaymentUsageRecordMapper, PrepaymentUsageRecord> implements IPrepaymentUsageRecordService {

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void record(MsgTypeEnum msgType, MsgSubTypeEnum msgSubType, String orderId, Integer number) {
        PrepaymentUsageRecord usageRecord = new PrepaymentUsageRecord()
                .setMsgType(msgType)
                .setMsgSubType(msgSubType)
                .setOrderId(orderId)
                .setNumber(number);
        save(usageRecord);
    }
}
