package com.citc.nce.auth.csp.recharge.service.impl;

import cn.hutool.core.util.ObjUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.citc.nce.auth.csp.recharge.Const.ConsumeTypeEnum;
import com.citc.nce.auth.csp.recharge.dao.ChargeConsumeStatisticDao;
import com.citc.nce.auth.csp.recharge.entity.ChargeConsumeStatistic;
import com.citc.nce.auth.csp.recharge.service.ChargeConsumeStatisticService;
import com.citc.nce.auth.postpay.order.entity.PostpayOrderDetail;
import com.citc.nce.auth.postpay.order.vo.PostpayOrderDetailVo;
import com.citc.nce.common.core.exception.BizException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author yy
 * @date 2024-10-23 16:56:13
 */
@Service
@Slf4j
public class ChargeConsumeStatisticServiceImpl extends ServiceImpl<ChargeConsumeStatisticDao, ChargeConsumeStatistic> implements ChargeConsumeStatisticService {
    @Resource
    ChargeConsumeStatisticDao chargeConsumeStatisticDao;

    @Override
    public BigDecimal generateBill(String paymentDays, String customerId, LocalDateTime start, LocalDateTime end, String orderId, int payType) {
        List<ChargeConsumeStatistic> chargeConsumeStatistics = chargeConsumeStatisticDao.generateBill(paymentDays, customerId, start, end, orderId, payType);
        if (ObjUtil.isEmpty(chargeConsumeStatistics)) {
            log.info("客户{}，账期：{}，没有充值的消费", customerId, paymentDays);
            return BigDecimal.valueOf(-1);
        }
        //所有返还记录统计
        List<ChargeConsumeStatistic> returnRecordStatistics = chargeConsumeStatistics.stream().filter(item -> Objects.equals(item.getConsumeType(), ConsumeTypeEnum.RETURN.getCode())).collect(Collectors.toList());
        log.info("返还记录{}条", returnRecordStatistics.size());
        //所有扣费记录统计
        List<ChargeConsumeStatistic> deDuctedRecordStatistics = chargeConsumeStatistics.stream().filter(item -> Objects.equals(item.getConsumeType(), ConsumeTypeEnum.FEE_DEDUCTION.getCode())).collect(Collectors.toList());
        log.info("扣费记录{}条", deDuctedRecordStatistics.size());
        //扣费记录减去返还记录
        returnRecordStatistics.forEach(item -> {
           List<ChargeConsumeStatistic> optionalChargeConsumeStatistic = deDuctedRecordStatistics.stream().filter(returnItem ->
                    returnItem.getAccountId().equals(item.getAccountId()) && returnItem.getTariffType().compareTo(item.getTariffType())==0 && returnItem.getPrice().compareTo(item.getPrice())==0).collect(Collectors.toList());
            if (ObjUtil.isNotEmpty(optionalChargeConsumeStatistic)) {
                ChargeConsumeStatistic deDuctedRecordStatistic = optionalChargeConsumeStatistic.get(0);
                deDuctedRecordStatistic.setMsgNum(deDuctedRecordStatistic.getMsgNum() - item.getMsgNum());
                deDuctedRecordStatistic.setAmount(deDuctedRecordStatistic.getAmount() - item.getAmount());
            }
        });
        //筛出扣费<=返还的记录
        List<ChargeConsumeStatistic> realStatistics = deDuctedRecordStatistics.stream().filter(item -> item.getAmount() > 0).collect(Collectors.toList());
        if (!this.saveBatch(realStatistics)) {
            log.error("saveBatch账单保存返还false");
            throw new BizException("账单保存失败");
        }
        Long amount = realStatistics.stream().mapToLong(ChargeConsumeStatistic::getAmount).sum();
        return BigDecimal.valueOf(amount).divide(new BigDecimal(100000));
    }

    @Override
    public List<PostpayOrderDetailVo> getDetail(String orderId) {
        return chargeConsumeStatisticDao.getStatisticDetail(orderId);
    }

    @Override
    public List<PostpayOrderDetail> getStatisticDetailDo(String orderId) {
        return chargeConsumeStatisticDao.getStatisticDetailDo(orderId);
    }
}
