package com.citc.nce.auth.csp.recharge.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.citc.nce.auth.csp.recharge.Const.ConsumeTypeEnum;
import com.citc.nce.auth.csp.recharge.Const.PaymentTypeEnum;
import com.citc.nce.auth.csp.recharge.Const.ProcessStatusEnum;
import com.citc.nce.auth.csp.recharge.Const.RechargeConst;
import com.citc.nce.auth.csp.recharge.Const.TariffTypeEnum;
import com.citc.nce.auth.csp.recharge.dao.ChargeConsumeRecordDao;
import com.citc.nce.auth.csp.recharge.entity.ChargeConsumeRecord;
import com.citc.nce.auth.csp.recharge.entity.ChargeConsumeStatistic;
import com.citc.nce.auth.csp.recharge.service.ChargeConsumeRecordService;
import com.citc.nce.auth.csp.recharge.vo.ChargeConsumeRecordVo;
import com.citc.nce.common.core.enums.MsgTypeEnum;
import com.citc.nce.common.core.exception.BizException;
import com.citc.nce.mybatis.core.entity.BaseDo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

/**
 * @author yy
 * @date 2024-10-22 16:10:38
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ChargeConsumeRecordServiceImpl extends ServiceImpl<ChargeConsumeRecordDao, ChargeConsumeRecord> implements ChargeConsumeRecordService {
    @Resource
    ChargeConsumeRecordDao chargeConsumeRecordDao;
    String chargeConsumeRecord_tableName = "charge_consume_record_%s";

    @Override
    public void initChargeConsumeRecordTable(String cspId) {
        chargeConsumeRecordDao.createTableIfNotExist(String.format(chargeConsumeRecord_tableName, cspId));
    }

    @Override
    public Long getChargeConsumeRecordByMsgTypeAndConsumeTypeAndAccountIdAndBetweenDate(String customerId, String accountId, Integer consumeType, MsgTypeEnum msgType, LocalDateTime start, LocalDateTime end) {
        LambdaQueryWrapper<ChargeConsumeRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ChargeConsumeRecord::getCustomerId, customerId)
                .eq(ChargeConsumeRecord::getConsumeType, consumeType)
                .eq(ChargeConsumeRecord::getMsgType, msgType.getCode())
                .eq(ChargeConsumeRecord::getAccountId, accountId)
                .gt(BaseDo::getCreateTime, Date.from(start.atZone(ZoneId.systemDefault()).toInstant()))
                .lt(BaseDo::getCreateTime, Date.from(end.atZone(ZoneId.systemDefault()).toInstant()));
        List<ChargeConsumeRecord> records = chargeConsumeRecordDao.selectList(wrapper);
        long sum = 0L;
        if (records != null) {
            sum = records.stream().mapToLong(re -> (long) re.getPrice() * re.getChargeNum()).sum();
        }
        return sum;
    }

    @Override
    public List<ChargeConsumeRecord> getChargeConsumeRecordsByMessageIdAndPhoneNumber(String messageId, String customerId, String phoneNumber) {
        LambdaQueryWrapper<ChargeConsumeRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ChargeConsumeRecord::getMessageId, messageId)
                .eq(ChargeConsumeRecord::getPhoneNumber, phoneNumber)
                .eq(ChargeConsumeRecord::getCustomerId, customerId);
        return list(wrapper);
    }

    @Override
    public List<ChargeConsumeRecord> getChargeConsumeRecordsByMessageIdAndPhoneNumbers(String messageId, String customerId, List<String> phoneNumbers) {
        LambdaQueryWrapper<ChargeConsumeRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ChargeConsumeRecord::getMessageId, messageId)
                .in(CollectionUtil.isNotEmpty(phoneNumbers), ChargeConsumeRecord::getPhoneNumber, phoneNumbers)
                .eq(ChargeConsumeRecord::getCustomerId, customerId);
        return list(wrapper);
    }

    @Override
    public boolean addRecord(ChargeConsumeRecordVo chargeConsumeRecordVo) {
        ChargeConsumeRecord chargeConsumeRecord = BeanUtil.toBean(chargeConsumeRecordVo, ChargeConsumeRecord.class);
        return this.getBaseMapper().insert(chargeConsumeRecord) == 1;
    }

    @Override
    public boolean updateRecordCompleted(String messageId, String phone, String msgType) {
        ChargeConsumeRecord chargeConsumeRecord = this.lambdaQuery().eq(ChargeConsumeRecord::getMessageId, messageId).eq(ChargeConsumeRecord::getMsgType, msgType)
                .eq(ChargeConsumeRecord::getPhoneNumber, phone).eq(ChargeConsumeRecord::getConsumeType, RechargeConst.CONSUME_TYPE_DEDUCT)
                .one();
        if (ObjUtil.isNull(chargeConsumeRecord)) {
            throw new BizException("订单异常");
        }
        chargeConsumeRecord.setProcessed(RechargeConst.PROCESSED_YES);
        return this.getBaseMapper().updateById(chargeConsumeRecord) == 1;
    }

    @Override
    public boolean removeBatchByIdsAndCustomerId(List<Long> ids, String customerId) {

        LambdaQueryWrapper<ChargeConsumeRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(ChargeConsumeRecord::getId, ids)
                .eq(ChargeConsumeRecord::getCustomerId, customerId);
        return this.getBaseMapper().delete(wrapper) > 0;
    }

    @Override
    public void updateProcessStatus(List<Long> ids, String cspId, Integer code) {
        chargeConsumeRecordDao.updateProcessStatus(ids, "charge_consume_record_" + cspId, code);
    }

    @Override
    public List<ChargeConsumeRecord> getChargeConsumeRecordNeedThaw(Integer msgType , String cspId) {
        LambdaQueryWrapper<ChargeConsumeRecord> wrapper = new LambdaQueryWrapper<>();
        if (msgType == MsgTypeEnum.M5G_MSG.getCode()) {
            List<ChargeConsumeRecord> result = CollectionUtil.newArrayList();
            wrapper.eq(ChargeConsumeRecord::getMsgType, msgType)
                    .eq(ChargeConsumeRecord::getProcessed, ProcessStatusEnum.UNTREATED)
                    .eq(ChargeConsumeRecord::getPayType, PaymentTypeEnum.BALANCE)
                    .eq(ChargeConsumeRecord::getConsumeType, ConsumeTypeEnum.FEE_DEDUCTION)
                    .eq(ChargeConsumeRecord::getTariffType, TariffTypeEnum.FIVE_G_READING_LETTER_PARSE)
                    .likeRight(ChargeConsumeRecord::getCustomerId, cspId)
                    .lt(ChargeConsumeRecord::getCreateTime, new Date(System.currentTimeMillis() - 7 * 24 * 60 * 60 * 1000L));
            //把wrapper中的条件用SQL写出来

            List<ChargeConsumeRecord> chargeConsumeRecordsOfReadingLetter = this.getBaseMapper().getChargeConsumeRecordNeedThaw
                    (msgType, "charge_consume_record_"+cspId, CollectionUtil.newArrayList(TariffTypeEnum.FIVE_G_READING_LETTER_PARSE.getCode())
                            , new Date(System.currentTimeMillis() - 7 * 24 * 60 * 60 * 1000L));
            result.addAll(chargeConsumeRecordsOfReadingLetter);

            LambdaQueryWrapper<ChargeConsumeRecord> wrapper2 = new LambdaQueryWrapper<>();
            wrapper2.eq(ChargeConsumeRecord::getMsgType, msgType)
                    .eq(ChargeConsumeRecord::getProcessed, ProcessStatusEnum.UNTREATED)
                    .eq(ChargeConsumeRecord::getPayType, PaymentTypeEnum.BALANCE)
                    .eq(ChargeConsumeRecord::getConsumeType, ConsumeTypeEnum.FEE_DEDUCTION)
                    .likeRight(ChargeConsumeRecord::getCustomerId, cspId)
                    .in(ChargeConsumeRecord::getTariffType, CollectionUtil.newArrayList(TariffTypeEnum.SESSION_MESSAGE.getCode(), TariffTypeEnum.TEXT_MESSAGE.getCode(), TariffTypeEnum.RICH_MEDIA_MESSAGE.getCode(), TariffTypeEnum.FALLBACK_SMS.getCode()))
                    .lt(ChargeConsumeRecord::getCreateTime, new Date(System.currentTimeMillis() - 3 * 24 * 60 * 60 * 1000L));
            List<ChargeConsumeRecord> chargeConsumeRecordsOfFifthOther = this.getBaseMapper().getChargeConsumeRecordNeedThaw(
                    msgType,
                    "charge_consume_record_"+cspId,
                    CollectionUtil.newArrayList(TariffTypeEnum.SESSION_MESSAGE.getCode(), TariffTypeEnum.TEXT_MESSAGE.getCode(),
                            TariffTypeEnum.RICH_MEDIA_MESSAGE.getCode(), TariffTypeEnum.FALLBACK_SMS.getCode()),
                    new Date(System.currentTimeMillis() - 3 * 24 * 60 * 60 * 1000L));

            result.addAll(chargeConsumeRecordsOfFifthOther);

            return result;
        } else {
            wrapper.eq(ChargeConsumeRecord::getMsgType, msgType)
                    .eq(ChargeConsumeRecord::getProcessed, ProcessStatusEnum.UNTREATED)
                    .eq(ChargeConsumeRecord::getPayType, PaymentTypeEnum.BALANCE)
                    .eq(ChargeConsumeRecord::getConsumeType, ConsumeTypeEnum.FEE_DEDUCTION)
                    .likeRight(ChargeConsumeRecord::getCustomerId, cspId)
                    .lt(ChargeConsumeRecord::getCreateTime, new Date(System.currentTimeMillis() - 3 * 24 * 60 * 60 * 1000L));
            return this.getBaseMapper().getChargeConsumeRecordNeedThaw(
                    msgType,
                    "charge_consume_record_"+cspId,
                    null,
                    new Date(System.currentTimeMillis() - 3 * 24 * 60 * 60 * 1000L));
        }
    }
}
