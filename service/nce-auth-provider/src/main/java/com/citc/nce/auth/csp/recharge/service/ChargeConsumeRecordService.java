package com.citc.nce.auth.csp.recharge.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.citc.nce.auth.csp.recharge.entity.ChargeConsumeRecord;
import com.citc.nce.auth.csp.recharge.vo.ChargeConsumeRecordVo;
import com.citc.nce.common.core.enums.MsgTypeEnum;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 充值-计费核销类
 *
 * @author yy
 * @date 2024-10-22 15:04:26
 */
public interface ChargeConsumeRecordService extends IService<ChargeConsumeRecord> {
    void initChargeConsumeRecordTable(String cspId);

    List<ChargeConsumeRecord> getChargeConsumeRecordsByMessageIdAndPhoneNumber(String messageId, String customerId, String phoneNumber);

    List<ChargeConsumeRecord> getChargeConsumeRecordsByMessageIdAndPhoneNumbers(String messageId, String customerId, List<String> phoneNumbers);

    boolean addRecord(ChargeConsumeRecordVo chargeConsumeRecordVo);

    boolean updateRecordCompleted(String messageId, String phone, String msgType);

    boolean removeBatchByIdsAndCustomerId(List<Long> ids, String customerId);

    List<ChargeConsumeRecord> getChargeConsumeRecordNeedThaw(Integer msgType, String cspId);

    void updateProcessStatus(List<Long> ids, String cspId, Integer code);

    Long getChargeConsumeRecordByMsgTypeAndConsumeTypeAndAccountIdAndBetweenDate(String customerId, String accountId, Integer consumeType, MsgTypeEnum msgType, LocalDateTime start, LocalDateTime end);
}
