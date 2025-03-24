package com.citc.nce.auth.prepayment.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.citc.nce.auth.csp.csp.dao.CspDao;
import com.citc.nce.auth.csp.csp.entity.CspDo;
import com.citc.nce.auth.csp.recharge.Const.AccountTypeEnum;
import com.citc.nce.auth.csp.recharge.Const.ConsumeTypeEnum;
import com.citc.nce.auth.csp.recharge.Const.PayTypeEnum;
import com.citc.nce.auth.csp.recharge.Const.ProcessStatusEnum;
import com.citc.nce.auth.csp.recharge.Const.TariffTypeEnum;
import com.citc.nce.auth.csp.recharge.entity.ChargeConsumeRecord;
import com.citc.nce.auth.csp.recharge.service.ChargeConsumeRecordService;
import com.citc.nce.auth.csp.recharge.service.ChargeTariffService;
import com.citc.nce.auth.csp.recharge.vo.RechargeTariffDetailResp;
import com.citc.nce.auth.prepayment.service.DeductionAndRefundService;
import com.citc.nce.auth.prepayment.vo.CreateChargeConsumeRecordReq;
import com.citc.nce.auth.prepayment.vo.FeeDeductReq;
import com.citc.nce.auth.prepayment.vo.ReceiveConfirmReq;
import com.citc.nce.auth.prepayment.vo.ReturnBalanceBatchReq;
import com.citc.nce.auth.prepayment.vo.ReturnBalanceReq;
import com.citc.nce.auth.readingLetter.shortUrl.service.ReadingLetterShortUrlService;
import com.citc.nce.auth.readingLetter.shortUrl.vo.ReadingLetterShortUrlVo;
import com.citc.nce.authcenter.csp.CspCustomerApi;
import com.citc.nce.authcenter.csp.vo.ReduceBalanceResp;
import com.citc.nce.common.constants.Constants;
import com.citc.nce.common.core.enums.MsgTypeEnum;
import com.citc.nce.common.core.exception.BizException;
import com.citc.nce.common.redis.config.RedisService;
import com.citc.nce.msgenum.DeliveryEnum;
import com.citc.nce.msgenum.RequestEnum;
import com.citc.nce.tenant.MsgRecordApi;
import com.citc.nce.tenant.vo.req.UpdateStatusBatchByMessageIdReq;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.shardingsphere.transaction.annotation.ShardingSphereTransactionType;
import org.apache.shardingsphere.transaction.core.TransactionType;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author jcrenc
 * @since 2024/3/12 9:41
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class DeductionAndRefundServiceImpl implements DeductionAndRefundService {
    @Resource
    private RedissonClient redissonClient;
    @Resource
    private ChargeTariffService chargeTariffService;
    @Resource
    private ChargeConsumeRecordService chargeConsumeRecordService;
    @Resource
    CspCustomerApi customerApi;
    @Resource
    ReadingLetterShortUrlService readingLetterShortUrlService;
    @Resource
    MsgRecordApi msgRecordApi;
    @Resource
    private RedisService redisService;
    @Resource
    private CspDao cspDao;

    private static final String FEE_DEDUCT_LOCK = "method-fee-deduct-lock-account-%s";
    //5G消息可能的记录资费类型
    Set<Integer> fifthMessageTariffTypeSet = new HashSet<>(
            CollectionUtil.newArrayList(TariffTypeEnum.TEXT_MESSAGE.getCode()
                    , TariffTypeEnum.RICH_MEDIA_MESSAGE.getCode()
                    , TariffTypeEnum.SESSION_MESSAGE.getCode()));
    //5G阅信可能的记录资费类型
    Set<Integer> fifthReadingLetterTariffTypeSet = new HashSet<>(CollectionUtil.newArrayList(TariffTypeEnum.FIVE_G_READING_LETTER_PARSE.getCode()
            , TariffTypeEnum.FALLBACK_SMS.getCode()));

    @Override
    @Transactional(rollbackFor = Exception.class)
    @ShardingSphereTransactionType(TransactionType.BASE)
    public List<String> tryDeductRemaining(FeeDeductReq feeDeductReq) {
        List<String> phoneNumbers = feeDeductReq.getPhoneNumbers();
        if (phoneNumbers.isEmpty()) {
            throw new BizException("余额扣除金额必须为正整数,发送目标不能为空");
        }
        String customerId = feeDeductReq.getCustomerId();
        //'消息类型 1:5g消息,2:视频短信,3:短信,4:阅信+'
        Integer msgType = feeDeductReq.getAccountType();

        //todo 找到当前资费 根据消息类型决定单条价格.
        RechargeTariffDetailResp tariffVo = chargeTariffService.getRechargeTariff(feeDeductReq.getAccountId());

        //真正发送一条消息的资费(可能会产生两条记录, 如 短信回落+阅信解析 4,5)
        ArrayList<Integer> tariffTypeList = CollectionUtil.newArrayList();
        if (msgType.equals(AccountTypeEnum.FIFTH_MESSAGES.getCode())) {
            //回落类型 null:无回落  1:回落阅信  2:回落短信
            findRealDeductTariffType(feeDeductReq, tariffVo, tariffTypeList);
        } else {
            tariffTypeList.add(feeDeductReq.getTariffType());
        }

        //计算总值
        int allPriceOfTariffType = tariffTypeList.stream().map(tariffVo::getPriceOfTariffType).mapToInt(Integer::intValue).sum();

        RLock deductLock = redissonClient.getLock(String.format(FEE_DEDUCT_LOCK, customerId));
        try {
            deductLock.lock();
            if (feeDeductReq.getPayType().equals(PayTypeEnum.PREPAYMENT.getCode())) {
                log.debug("尝试扣余额, feeDeductReq:{}", feeDeductReq);
                ReduceBalanceResp reduceBalanceResp = customerApi.reduceBalance(customerId, (long) allPriceOfTariffType, (long) phoneNumbers.size() * (long) feeDeductReq.getChargeNum(), true);
                log.debug("扣除余额成功,reduceBalanceResp:{}", reduceBalanceResp);
                //一条都没扣除成功
                if (reduceBalanceResp.getDeductAmount() <= 0 && reduceBalanceResp.getNumOfDeduct() <= 0) {
                    return CollectionUtil.newArrayList();
                }
                //实际发送的名单
                if (phoneNumbers.size() > reduceBalanceResp.getNumOfDeduct()) {
                    phoneNumbers = phoneNumbers.subList(0, reduceBalanceResp.getNumOfDeduct().intValue());
                }
            }
            //记录扣费
            ArrayList<ChargeConsumeRecord> ChargeConsumeRecords = CollectionUtil.newArrayList();
            for (String phoneNumber : phoneNumbers) {
                for (Integer tariffType : tariffTypeList) {
                    ChargeConsumeRecord record = new ChargeConsumeRecord();
                    record.setAccountId(feeDeductReq.getAccountId());
                    record.setCustomerId(customerId);
                    record.setConsumeType(ConsumeTypeEnum.FEE_DEDUCTION.getCode());
                    record.setMessageId(feeDeductReq.getMessageId());
                    record.setMsgType(msgType);
                    record.setPhoneNumber(phoneNumber);
                    record.setProcessed(ProcessStatusEnum.UNTREATED.getCode());
                    record.setMessageId(feeDeductReq.getMessageId());
                    record.setPayType(feeDeductReq.getPayType());
                    record.setPrice(tariffVo.getPriceOfTariffType(tariffType));
                    record.setTariffId(tariffVo.getId());
                    record.setTariffType(tariffType);
                    record.setChargeNum(feeDeductReq.getChargeNum());
                    ChargeConsumeRecords.add(record);
                }
            }
            chargeConsumeRecordService.saveBatch(ChargeConsumeRecords);
            log.info("记录扣费成功");
        } catch (Exception e) {
            log.error("扣费失败或记录失败", e);
            throw new BizException("扣费失败");
        } finally {
            if (deductLock.isLocked() && deductLock.isHeldByCurrentThread())
                deductLock.unlock();
        }
        return phoneNumbers;
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    // @ShardingSphereTransactionType(TransactionType.BASE)
    public void deductFee(FeeDeductReq feeDeductReq) {
        log.info("deductFee.feeDeductReq:{}", JSON.toJSONString(feeDeductReq));
        List<String> phoneNumbers = feeDeductReq.getPhoneNumbers();
        if (phoneNumbers.isEmpty()) {
            throw new BizException("余额扣除金额必须为正整数,发送目标不能为空");
        }
        String customerId = feeDeductReq.getCustomerId();
        //'消息类型 1:5g消息,2:视频短信,3:短信,4:阅信+'
        Integer msgType = feeDeductReq.getAccountType();

        //todo 找到当前资费 根据消息类型决定单条价格.
        RechargeTariffDetailResp tariffVo = chargeTariffService.getRechargeTariff(feeDeductReq.getAccountId());

        //真正发送一条消息的资费(可能会产生两条记录, 如 短信回落+阅信解析 4,5)
        ArrayList<Integer> tariffTypeList = CollectionUtil.newArrayList();
        if (msgType.equals(AccountTypeEnum.FIFTH_MESSAGES.getCode())) {
            findRealDeductTariffType(feeDeductReq, tariffVo, tariffTypeList);
        } else {
            tariffTypeList.add(feeDeductReq.getTariffType());
        }
        //计算总值
        int allPriceOfTariffType = tariffTypeList.stream().map(tariffVo::getPriceOfTariffType).mapToInt(Integer::intValue).sum();

        RLock deductLock = redissonClient.getLock(String.format(FEE_DEDUCT_LOCK, customerId));
        try {
            deductLock.lock();
            if (feeDeductReq.getPayType().equals(PayTypeEnum.PREPAYMENT.getCode())) {
                log.info("尝试扣余额");
                customerApi.reduceBalance(customerId, (long) allPriceOfTariffType, (long) phoneNumbers.size() * feeDeductReq.getChargeNum(), false);
                log.info("扣除余额成功");
            }
            //记录扣费
            ArrayList<ChargeConsumeRecord> ChargeConsumeRecords = CollectionUtil.newArrayList();
            for (String phoneNumber : phoneNumbers) {
                for (Integer tariffType : tariffTypeList) {
                    ChargeConsumeRecord record = new ChargeConsumeRecord();
                    record.setAccountId(feeDeductReq.getAccountId());
                    record.setCustomerId(customerId);
                    record.setConsumeType(ConsumeTypeEnum.FEE_DEDUCTION.getCode());
                    record.setMessageId(feeDeductReq.getMessageId());
                    record.setMsgType(msgType);
                    record.setPhoneNumber(phoneNumber);
                    record.setProcessed(ProcessStatusEnum.UNTREATED.getCode());
                    record.setMessageId(feeDeductReq.getMessageId());
                    record.setPayType(feeDeductReq.getPayType());
                    record.setPrice(tariffVo.getPriceOfTariffType(tariffType));
                    record.setTariffId(tariffVo.getId());
                    record.setTariffType(tariffType);
                    record.setChargeNum(feeDeductReq.getChargeNum());
                    ChargeConsumeRecords.add(record);
                }
            }
            chargeConsumeRecordService.saveBatch(ChargeConsumeRecords);
            log.info("记录扣费成功");
        } catch (Exception e) {
            log.error("扣费失败或记录失败", e);
            throw new BizException("扣费失败");
        } finally {
            if (deductLock.isLocked() && deductLock.isHeldByCurrentThread())
                deductLock.unlock();
        }
    }

    private void findRealDeductTariffType(FeeDeductReq feeDeductReq, RechargeTariffDetailResp tariffVo, ArrayList<Integer> tariffTypeList) {
        //回落类型 null:无回落 1:回落短信 2:回落阅信
        if (Objects.isNull(feeDeductReq.getFifthFallbackType())) {
            tariffTypeList.add(feeDeductReq.getTariffType());
        } else if (feeDeductReq.getFifthFallbackType() == 2) {
            int priceOfFifth = tariffVo.getPriceOfTariffType(feeDeductReq.getTariffType());
            int priceOfFifthReadingLetter = tariffVo.getPriceOfTariffType(TariffTypeEnum.FIVE_G_READING_LETTER_PARSE.getCode());
            int priceOfFallbackSms = tariffVo.getPriceOfTariffType(TariffTypeEnum.FALLBACK_SMS.getCode());

            if (priceOfFifth < priceOfFallbackSms + priceOfFifthReadingLetter) {
                tariffTypeList.add(TariffTypeEnum.FIVE_G_READING_LETTER_PARSE.getCode());
                tariffTypeList.add(TariffTypeEnum.FALLBACK_SMS.getCode());
            } else {
                //这里需要记录填一下
                tariffTypeList.add(feeDeductReq.getTariffType());
            }
        } else if (feeDeductReq.getFifthFallbackType() == 1) {
            int priceOfFifth = tariffVo.getPriceOfTariffType(feeDeductReq.getTariffType());
            int priceOfSms = tariffVo.getPriceOfTariffType(TariffTypeEnum.FALLBACK_SMS.getCode());

            if (priceOfFifth < priceOfSms) {
                tariffTypeList.add(TariffTypeEnum.FALLBACK_SMS.getCode());
            } else {
                tariffTypeList.add(feeDeductReq.getTariffType());
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    // @ShardingSphereTransactionType(TransactionType.BASE)
    public void returnBalance(ReturnBalanceReq req) {
        String customerId = req.getCustomerId();
        List<ChargeConsumeRecord> chargeConsumeRecords = getChargeConsumeRecordList(req);
        Long tariffId = chargeConsumeRecords.get(0).getTariffId();
        //原记录的资费类型
        List<Integer> tariffTypes = chargeConsumeRecords.stream().map(ChargeConsumeRecord::getTariffType).collect(Collectors.toList());
        //本次的资费类型
        Integer realTariffType = req.getTariffType();
        returnBalance(customerId, tariffTypes, realTariffType, chargeConsumeRecords, tariffId);
    }

    @Override
    // @ShardingSphereTransactionType(TransactionType.BASE)
    public void returnBalanceBatch(ReturnBalanceBatchReq req) {
        log.info("开始批量返还, req:{}", req);
        String customerId = req.getCustomerId();
        List<ChargeConsumeRecord> chargeConsumeRecords = getChargeConsumeRecordList(req);
        log.info("批量返还记录:chargeConsumeRecords:{}", chargeConsumeRecords);
        Long tariffId = chargeConsumeRecords.get(0).getTariffId();
        //原记录的资费类型
        List<Integer> tariffTypes = chargeConsumeRecords.stream().map(ChargeConsumeRecord::getTariffType).distinct().collect(Collectors.toList());
        //本次的资费类型
        Integer realTariffType = req.getTariffType();
        returnBalance(customerId, tariffTypes, realTariffType, chargeConsumeRecords, tariffId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    // @ShardingSphereTransactionType(TransactionType.BASE)
    public void receiveConfirm(ReceiveConfirmReq req) {
        List<ChargeConsumeRecord> chargeConsumeRecords = getChargeConsumeRecordList(req);
        log.info("开始确认扣费,req:{}", req);
        String customerId = req.getCustomerId();
        ChargeConsumeRecord record = chargeConsumeRecords.get(0);
        String accountId = record.getAccountId();
        Integer payType = record.getPayType();
        //原记录的资费类型
        List<Integer> tariffTypes = chargeConsumeRecords.stream().map(ChargeConsumeRecord::getTariffType).collect(Collectors.toList());
        //本次的资费类型
        Integer realTariffType = req.getTariffType();

        RLock deductLock = redissonClient.getLock(String.format(FEE_DEDUCT_LOCK, customerId));
        try {
            deductLock.lock();
            //是相同类型的
            if (tariffTypes.contains(realTariffType)) {
                //需要判断此次扣除是否已经记录过了
                ChargeConsumeRecord exist = chargeConsumeRecords.stream()
                        .filter(chargeConsumeRecord -> chargeConsumeRecord.getConsumeType()
                                .equals(ConsumeTypeEnum.FEE_DEDUCTION.getCode())
                                && chargeConsumeRecord.getTariffType().equals(realTariffType)
                                && chargeConsumeRecord.getProcessed().equals(ProcessStatusEnum.PROCESSED.getCode())).findFirst().orElse(null);
                if (exist != null) {
                    log.info("本条扣费已返还,chargeConsumeRecord:{}", exist);
                    return;
                }
                for (ChargeConsumeRecord chargeConsumeRecord : chargeConsumeRecords) {
                    if (chargeConsumeRecord.getTariffType().equals(req.getTariffType())) {
                        //旧数据变成已处理
                        chargeConsumeRecordService.updateProcessStatus(CollectionUtil.newArrayList(chargeConsumeRecord.getId()), customerId.substring(0, 10), ProcessStatusEnum.PROCESSED.getCode());
                        break;
                    }
                }
            } else {
                //不是相同类型的返还  找到真实类型的资费信息
                RechargeTariffDetailResp tariff = chargeTariffService.getRechargeTariff(accountId);
                int priceOfTariffType = tariff.getPriceOfTariffType(realTariffType);

                //将原纪录全部删除(逻辑删除)
                List<Long> ids = chargeConsumeRecords.stream().map(ChargeConsumeRecord::getId).collect(Collectors.toList());
                chargeConsumeRecordService.removeBatchByIdsAndCustomerId(ids, customerId);

                //退还金额
                if (PayTypeEnum.PREPAYMENT.getCode().equals(payType)) {
                    // int sum = Math.toIntExact(chargeConsumeRecords.stream().map(ChargeConsumeRecord::getPrice).collect(Collectors.summarizingInt(Integer::intValue)).getSum());
                    int sum = chargeConsumeRecords.stream().mapToInt(re -> re.getPrice() * re.getChargeNum()).sum();
                    customerApi.addBalance(customerId, (long) sum);
                }

                //新建另外组合的扣费
                //realTariff如果是5G消息, 那么就应该新建5G消息(1条)并处理完成
                if (fifthMessageTariffTypeSet.contains(realTariffType)) {
                    ChargeConsumeRecord newChargeConsumeRecords =
                            getNewChargeConsumeRecord(record, priceOfTariffType, realTariffType, ConsumeTypeEnum.FEE_DEDUCTION.getCode(), ProcessStatusEnum.PROCESSED.getCode());
                    chargeConsumeRecordService.save(newChargeConsumeRecords);
                    if (PayTypeEnum.PREPAYMENT.getCode().equals(payType)) {
                        customerApi.reduceBalance(customerId, (long) priceOfTariffType, (long) newChargeConsumeRecords.getChargeNum(), false);
                    }
                }
                //如果是真实类型属于5G阅信组合
                else if (fifthReadingLetterTariffTypeSet.contains(realTariffType)) {
                    int totalPrice = 0;
                    ArrayList<ChargeConsumeRecord> newChargeConsumeRecords = CollectionUtil.newArrayList();
                    for (Integer fifthMessageTariffType : fifthReadingLetterTariffTypeSet) {
                        if (Objects.equals(fifthMessageTariffType, TariffTypeEnum.FIVE_G_READING_LETTER_PARSE.getCode())) {
                            //如果没有选择回落阅信, 那么就不需要新建阅信扣费记录
                            if (!redisService.hasKey(String.format(Constants.FIFTH_MSG_FALLBACK_READING_LETTER_KEY, req.getMessageId()))) {
                                log.info("查询到5G消息发送messageId={}是选择的回落短信,不生产阅信解析扣费记录",  req.getMessageId());
                                continue;
                            }
                            log.info("查询到5G消息发送messageId={}是选择的回落5G阅信,生成阅信解析扣费记录",  req.getMessageId());
                        }
                        int tariffValue = tariff.getPriceOfTariffType(fifthMessageTariffType);
                        ChargeConsumeRecord newChargeConsumeRecord = getNewChargeConsumeRecord(record, tariffValue, fifthMessageTariffType, 0, ProcessStatusEnum.UNTREATED.getCode());
                        if (Objects.equals(fifthMessageTariffType, realTariffType) || realTariffType.equals(TariffTypeEnum.FIVE_G_READING_LETTER_PARSE.getCode())) {
                            newChargeConsumeRecord.setProcessed(ProcessStatusEnum.PROCESSED.getCode());
                        }
                        newChargeConsumeRecords.add(newChargeConsumeRecord);
                        totalPrice += (tariffValue * newChargeConsumeRecord.getChargeNum());
                    }
                    chargeConsumeRecordService.saveBatch(newChargeConsumeRecords);
                    //需要重新扣费
                    if (PayTypeEnum.PREPAYMENT.getCode().equals(payType)) {
                        customerApi.reduceBalance(customerId, (long) totalPrice, 1L, false);
                    }
                } else {
                    throw new BizException("资费类型错误");
                }
            }
        } catch (Exception e) {
            log.info("扣费失败或记录失败", e);
        } finally {
            if (deductLock.isLocked() && deductLock.isHeldByCurrentThread())
                deductLock.unlock();
        }
    }

    @Override
    // @ShardingSphereTransactionType(TransactionType.BASE)
    public void justCreateChargeConsumeRecord(CreateChargeConsumeRecordReq req) {

        ChargeConsumeRecord record = new ChargeConsumeRecord();
        record.setAccountId(req.getAccountId());
        record.setChargeNum(req.getChargeNum());
        record.setCustomerId(req.getCustomerId());
        record.setConsumeType(ConsumeTypeEnum.FEE_DEDUCTION.getCode());
        record.setMessageId(req.getMessageId());
        record.setMsgType(req.getMsgType());
        record.setPhoneNumber(req.getPhoneNumber());
        record.setProcessed(req.getProcessed());
        record.setPayType(req.getPayType());
        record.setPrice(req.getPrice());
        record.setTariffId(req.getTariffId());
        record.setTariffType(req.getTariffType());
        chargeConsumeRecordService.save(record);

    }

    @Override
    public void regularReturnRechargeConsumeRecord() {

        //找到所有的CSP
        Set<String> cspIdSet = new HashSet<>();
        LambdaQueryWrapper<CspDo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CspDo::getIsSplite,1);
        List<CspDo> cspDos = cspDao.selectList(queryWrapper);
        cspDos.forEach(i->cspIdSet.add(i.getCspId()));

        for (String cspId : cspIdSet) {

            //超过3天未收到消息回执状态/超过7天未收到5G阅信解析回执/超过解析有效期未收到阅信+解析回执，则这些消息及短链金额从冻结金额中解冻
            List<ChargeConsumeRecord> recordsNeedThaw = getRecordsNeedThaw(cspId);
            Map<String, List<ChargeConsumeRecord>> groupConsumeRecordMap = recordsNeedThaw.stream().collect(Collectors.groupingBy(ChargeConsumeRecord::getCustomerId));

            for (Map.Entry<String, List<ChargeConsumeRecord>> entry : groupConsumeRecordMap.entrySet()) {
                String customerId = entry.getKey();
                List<ChargeConsumeRecord> records = entry.getValue();
                log.info("customerId: {},需要返还的扣费记录 records.size: {}", customerId, records.size());
                if(ObjUtil.isEmpty(records))continue;
                    int total=records.size();
                    int pageSize=10000;
                    int remaining=total;
                    int toIndex;
                    int fromIndex=0;
                    while (remaining>0)
                    {
                        if(remaining<pageSize)
                        {
                            pageSize=remaining;
                        }
                        toIndex=fromIndex+pageSize;
                        log.info("fromIndex:{},toIndex:{}",fromIndex,toIndex);
                        List<ChargeConsumeRecord> manageRecord=records.subList(fromIndex,toIndex);
                        regularReturnRechargeConsumeRecordForMsg(manageRecord,cspId,customerId);
                        fromIndex=toIndex;
                        remaining=total-toIndex;
                        log.info("remaining:{}",remaining);
                    }
            }
        }
        regularReturnRechargeConsumeRecordForShortUrl();
    }
    @ShardingSphereTransactionType(TransactionType.BASE)
    @Transactional(rollbackFor = Exception.class)
    public void regularReturnRechargeConsumeRecordForMsg(List<ChargeConsumeRecord> records, String cspId, String customerId)
    {
        List<Long> selectedIds = new ArrayList<>();
        List<String> selectedMsgIds = new ArrayList<>();
        List<ChargeConsumeRecord> returnRecords = new ArrayList<>();
        for (int i = 0; i < records.size(); i++) {
            ChargeConsumeRecord record = records.get(i);
            selectedIds.add(record.getId());
            selectedMsgIds.add(record.getMessageId());
            if (record.getPrice() !=null && record.getPrice()>0) {
                returnRecords.add(getNewChargeConsumeRecord(record, record.getPrice(), record.getTariffType(), ConsumeTypeEnum.RETURN.getCode(), ProcessStatusEnum.PROCESSED.getCode()));
            }
            if (selectedIds.size()==500 || i == records.size()-1) {
                //将原纪录全部修改为已处理
                chargeConsumeRecordService.updateProcessStatus(selectedIds, cspId, ProcessStatusEnum.PROCESSED.getCode());
                log.info("以下chargeConsumeRecord数据的处理状态已修改 subList.size: {}", selectedIds.size());
                selectedIds = new ArrayList<>();

                UpdateStatusBatchByMessageIdReq req = new UpdateStatusBatchByMessageIdReq();
                req.setMessageIds(selectedMsgIds);
                req.setCustomerId(customerId);
                req.setSendResult(DeliveryEnum.FAILED.getCode());
                req.setFinalResult(RequestEnum.FAILED.getCode());
                msgRecordApi.updateStatusBatchByMsgId(req);
                log.info("msgRecord数据的处理状态已修改 selectedMsgIds.size: {}", selectedMsgIds.size());
                selectedMsgIds = new ArrayList<>();

                //新建返还记录(价格非零)
                if (CollectionUtil.isNotEmpty(returnRecords)) {
                    chargeConsumeRecordService.saveBatch(returnRecords);
                    log.info("将原纪录全部修改为已处理,新建返还记录: returnRecords.size=: {}", returnRecords.size());
                    returnRecords = new ArrayList<>();
                }
            }
        }

        int sum = records.stream().filter(record -> PayTypeEnum.PREPAYMENT.getCode().equals(record.getPayType())).filter(record -> record.getPrice() !=null && record.getPrice()>0).mapToInt(re -> re.getPrice() * re.getChargeNum()).sum();
        if (sum > 0) {
            log.info("customerId: {} 返还金额:{}", customerId, sum);
            customerApi.addBalance(customerId, (long) sum);
        }
    }
    @ShardingSphereTransactionType(TransactionType.BASE)
    @Transactional(rollbackFor = Exception.class)
    public void regularReturnRechargeConsumeRecordForShortUrl() {
        //找到已经过期的阅信+短链(把短链处理状态已经改了)
        List<ReadingLetterShortUrlVo> expiredUnprocessedShortUrl = readingLetterShortUrlService.findExpiredUnprocessedShortUrl();
        Map<String, List<ReadingLetterShortUrlVo>> collect = expiredUnprocessedShortUrl.stream().collect(Collectors.groupingBy(ReadingLetterShortUrlVo::getCustomerId));

        for (Map.Entry<String, List<ReadingLetterShortUrlVo>> entry : collect.entrySet()) {
            String customerId = entry.getKey();
            List<ReadingLetterShortUrlVo> urls = entry.getValue();
            log.info("customerId: {}, 需要返还的短链urls.size: {}", customerId, urls.size());
            int sum = urls.stream().filter(url -> Objects.nonNull(url.getPrice())).mapToInt(url -> url.getPrice() * (url.getRequestParseNumber() - url.getResolvedNumber())).sum();
            if (sum != 0) {
                log.info("customerId: {} 返还金额:{}", customerId, sum);
                customerApi.addBalance(customerId, (long) sum);
            }
        }
    }
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void returnBalanceBatchWithoutTariffType(ReturnBalanceBatchReq req) {
        String customerId = req.getCustomerId();
        List<ChargeConsumeRecord> chargeConsumeRecords = getChargeConsumeRecordList(req);

        //找到相同类型的资费信息
        //需要判断此次回退是否已经记录过了
        ChargeConsumeRecord exist = chargeConsumeRecords.stream()
                .filter(chargeConsumeRecord -> chargeConsumeRecord.getProcessed().equals(ProcessStatusEnum.PROCESSED.getCode())).findFirst().orElse(null);
        if (exist != null) {
            log.info("已返还,chargeConsumeRecords:{}", chargeConsumeRecords);
            return;
        }
        long returnTotal = 0L;
        for (ChargeConsumeRecord chargeConsumeRecord : chargeConsumeRecords) {
            //旧数据已处理
            chargeConsumeRecordService.updateProcessStatus(CollectionUtil.newArrayList(chargeConsumeRecord.getId()), customerId.substring(0, 10), ProcessStatusEnum.PROCESSED.getCode());
            //生成一条返还记录
            ChargeConsumeRecord returnRecord = getNewChargeConsumeRecord(chargeConsumeRecord, chargeConsumeRecord.getPrice(), chargeConsumeRecord.getTariffType(), ConsumeTypeEnum.RETURN.getCode(), ProcessStatusEnum.PROCESSED.getCode());
            chargeConsumeRecordService.save(returnRecord);
            if (chargeConsumeRecord.getPayType().equals(PayTypeEnum.PREPAYMENT.getCode())) {
                returnTotal += (long) chargeConsumeRecord.getPrice() * chargeConsumeRecord.getChargeNum();
            }
        }
        //退还金额
        if (returnTotal > 0) {
            customerApi.addBalance(customerId, returnTotal);
        }
    }

    private void returnBalance(String customerId, List<Integer> tariffTypes, Integer realTariffType, List<ChargeConsumeRecord> chargeConsumeRecords, Long tariffId) {

        RLock deductLock = redissonClient.getLock(String.format(FEE_DEDUCT_LOCK, customerId));
        try {
            deductLock.lock();
            //是相同类型的返还
            if (tariffTypes.contains(realTariffType)) {
                log.info("是相同类型的返还");
                //找到相同类型的资费信息
                //需要判断此次回退是否已经记录过了
                ChargeConsumeRecord exist = chargeConsumeRecords.stream()
                        .filter(chargeConsumeRecord -> chargeConsumeRecord.getConsumeType().equals(ConsumeTypeEnum.RETURN.getCode())
                                && chargeConsumeRecord.getTariffType().equals(realTariffType)).findFirst().orElse(null);
                if (exist != null) {
                    log.info("已返还,exist:{}", exist);
                    return;
                }
                for (ChargeConsumeRecord chargeConsumeRecord : chargeConsumeRecords) {
                    log.info("开始返还,chargeConsumeRecord:{}", chargeConsumeRecord);
                    if (chargeConsumeRecord.getTariffType().equals(realTariffType)) {
                        //旧数据已处理
                        chargeConsumeRecordService.updateProcessStatus(CollectionUtil.newArrayList(chargeConsumeRecord.getId()), customerId.substring(0, 10), ProcessStatusEnum.PROCESSED.getCode());
                        //生成一条返还记录
                        ChargeConsumeRecord returnRecord = getNewChargeConsumeRecord(chargeConsumeRecord, chargeConsumeRecord.getPrice(), realTariffType, ConsumeTypeEnum.RETURN.getCode(), ProcessStatusEnum.PROCESSED.getCode());
                        chargeConsumeRecordService.save(returnRecord);
                        //退还金额
                        if (chargeConsumeRecord.getPayType().equals(PayTypeEnum.PREPAYMENT.getCode())) {
                            customerApi.addBalance(customerId, (long) chargeConsumeRecord.getPrice() * (long) chargeConsumeRecord.getChargeNum());
                        }
                    }
                }
            } else {
                //不是相同类型的返还  找到真实类型的资费信息
                RechargeTariffDetailResp tariff = chargeTariffService.getById(tariffId);
                int priceOfTariffType = tariff.getPriceOfTariffType(realTariffType);

                //将原纪录全部删除(逻辑删除)
                //只返回预付费订单的钱
                List<Long> ids = chargeConsumeRecords.stream().map(ChargeConsumeRecord::getId).collect(Collectors.toList());
                chargeConsumeRecordService.removeBatchByIdsAndCustomerId(ids, customerId);
                log.info("是不同类型的返还,customerId:{} 的原扣费记录 ids:{}将被删除", customerId, ids);
                //退还金额
                int sum = Math.toIntExact(chargeConsumeRecords.stream().filter(record -> record.getPayType().equals(PayTypeEnum.PREPAYMENT.getCode())).mapToInt(re -> re.getPrice() * re.getChargeNum()).sum());
                if (sum > 0) {
                    customerApi.addBalance(customerId, (long) sum);
                }

                //新建另外组合的扣费(realTariff如果是5G消息, 那么就应该新建5G消息扣费和返还记录(1条))
                if (fifthMessageTariffTypeSet.contains(realTariffType)) {

                    //5G阅信和回落只留下回落(可能不存在阅信解析扣费记录).且避免生成重复的5G消息扣费(退还)记录
                    List<ChargeConsumeRecord> fallbackConsumeRecordList = chargeConsumeRecords.stream()
                            .filter(record -> Objects.equals(record.getTariffType(), TariffTypeEnum.FALLBACK_SMS.getCode()))
                            .collect(Collectors.toList());
                    ArrayList<ChargeConsumeRecord> newChargeConsumeRecords = getNewProcessedChargeConsumeRecords(fallbackConsumeRecordList, priceOfTariffType, realTariffType);
                    chargeConsumeRecordService.saveBatch(newChargeConsumeRecords);
                }
                //如果是真实类型属于5G阅信组合
                else if (fifthReadingLetterTariffTypeSet.contains(realTariffType)) {
                    int totalPrice = 0;
                    ArrayList<ChargeConsumeRecord> newChargeConsumeRecords = CollectionUtil.newArrayList();
                    for (Integer fifthMessageTariffType : fifthReadingLetterTariffTypeSet) {
                        //这种5G阅信组合的价格
                        int priceOfThisFifthReadingLetter = tariff.getPriceOfTariffType(fifthMessageTariffType);
                        //每条原纪录都需要生成 5G阅信组合扣费记录(2条)
                        for (ChargeConsumeRecord chargeConsumeRecord : chargeConsumeRecords) {
                            ChargeConsumeRecord newChargeConsumeRecord = getNewChargeConsumeRecord
                                    (chargeConsumeRecord, priceOfThisFifthReadingLetter, fifthMessageTariffType, ConsumeTypeEnum.FEE_DEDUCTION.getCode(), ProcessStatusEnum.UNTREATED.getCode());
                            newChargeConsumeRecords.add(newChargeConsumeRecord);
                            //只有预付费才需要在余额上操作
                            if (chargeConsumeRecord.getPayType().equals(PayTypeEnum.PREPAYMENT.getCode())) {
                                totalPrice += (priceOfThisFifthReadingLetter * newChargeConsumeRecord.getChargeNum());
                            }
                            //判断是否需要同时生成返钱记录
                            if (Objects.equals(fifthMessageTariffType, realTariffType)) {
                                newChargeConsumeRecord.setProcessed(ProcessStatusEnum.PROCESSED.getCode());
                                ChargeConsumeRecord realChargeConsumeRecordReturn = getNewChargeConsumeRecord
                                        (chargeConsumeRecord, priceOfTariffType, fifthMessageTariffType, ConsumeTypeEnum.RETURN.getCode(), ProcessStatusEnum.PROCESSED.getCode());
                                newChargeConsumeRecords.add(realChargeConsumeRecordReturn);
                                if (realChargeConsumeRecordReturn.getPayType().equals(PayTypeEnum.PREPAYMENT.getCode())) {
                                    totalPrice -= (priceOfTariffType * realChargeConsumeRecordReturn.getChargeNum());
                                }
                            }
                        }
                    }
                    chargeConsumeRecordService.saveBatch(newChargeConsumeRecords);
                    //需要重新扣费
                    if (totalPrice > 0) {
                        customerApi.reduceBalance(customerId, (long) totalPrice, 1L, false);
                    }
                } else {
                    throw new BizException("资费类型错误");
                }
            }
        } finally {
            if (deductLock.isLocked() && deductLock.isHeldByCurrentThread())
                deductLock.unlock();
        }
    }

    private List<ChargeConsumeRecord> getChargeConsumeRecordList(ReceiveConfirmReq req) {
        String customerId = req.getCustomerId();
        String messageId = req.getMessageId();
        String phoneNumber = req.getPhoneNumber();

        //通过messageId,CustomerId,phoneNumber   找到ChargeConsumeRecord
        List<ChargeConsumeRecord> chargeConsumeRecords = chargeConsumeRecordService.getChargeConsumeRecordsByMessageIdAndPhoneNumber(messageId, customerId, phoneNumber);
        if (chargeConsumeRecords.isEmpty()) {
            throw new BizException("无记录");
        }
        return chargeConsumeRecords;
    }

    //查找到需要解冻的数据
    private List<ChargeConsumeRecord> getRecordsNeedThaw(String cspId) {
        //通过messageId,CustomerId,phoneNumber   找到ChargeConsumeRecord
        List<ChargeConsumeRecord> chargeConsumeRecordsOfFifth = chargeConsumeRecordService.getChargeConsumeRecordNeedThaw(MsgTypeEnum.M5G_MSG.getCode(), cspId);
        List<ChargeConsumeRecord> result = new ArrayList<>(chargeConsumeRecordsOfFifth);

        List<ChargeConsumeRecord> chargeConsumeRecordsOfSms = chargeConsumeRecordService.getChargeConsumeRecordNeedThaw(MsgTypeEnum.SHORT_MSG.getCode(), cspId);
        result.addAll(chargeConsumeRecordsOfSms);
        List<ChargeConsumeRecord> chargeConsumeRecordsOfMediaSms = chargeConsumeRecordService.getChargeConsumeRecordNeedThaw(MsgTypeEnum.MEDIA_MSG.getCode(), cspId);
        result.addAll(chargeConsumeRecordsOfMediaSms);
        return result;
    }

    private List<ChargeConsumeRecord> getChargeConsumeRecordList(ReturnBalanceReq req) {
        String customerId = req.getCustomerId();
        String messageId = req.getMessageId();
        String phoneNumber = req.getPhoneNumber();

        //通过messageId,CustomerId,phoneNumber   找到ChargeConsumeRecord
        List<ChargeConsumeRecord> chargeConsumeRecords = chargeConsumeRecordService.getChargeConsumeRecordsByMessageIdAndPhoneNumber(messageId, customerId, phoneNumber);
        if (chargeConsumeRecords.isEmpty()) {
            throw new BizException("无记录");
        }
        return chargeConsumeRecords;
    }

    private List<ChargeConsumeRecord> getChargeConsumeRecordList(ReturnBalanceBatchReq req) {
        String customerId = req.getCustomerId();
        String messageId = req.getMessageId();
        List<String> phoneNumbers = req.getPhoneNumbers();

        //通过messageId,CustomerId,phoneNumber   找到ChargeConsumeRecord
        List<ChargeConsumeRecord> chargeConsumeRecords = chargeConsumeRecordService.
                getChargeConsumeRecordsByMessageIdAndPhoneNumbers(messageId, customerId, phoneNumbers);
        if (chargeConsumeRecords.isEmpty()) {
            throw new BizException("无记录");
        }
        return chargeConsumeRecords;
    }

    //生成两条扣费及退还记录
    private ArrayList<ChargeConsumeRecord> getNewProcessedChargeConsumeRecords(List<ChargeConsumeRecord> chargeConsumeRecords, Integer tariffValue, Integer realTariffType) {
        ArrayList<ChargeConsumeRecord> result = CollectionUtil.newArrayList();

        for (ChargeConsumeRecord chargeConsumeRecord : chargeConsumeRecords) {
            ChargeConsumeRecord newRecordDeduct = new ChargeConsumeRecord();
            BeanUtils.copyProperties(chargeConsumeRecord, newRecordDeduct);
            newRecordDeduct.setId(null);
            newRecordDeduct.setPrice(tariffValue);
            newRecordDeduct.setConsumeType(ConsumeTypeEnum.FEE_DEDUCTION.getCode());
            newRecordDeduct.setProcessed(ProcessStatusEnum.PROCESSED.getCode());
            newRecordDeduct.setTariffType(realTariffType);
            newRecordDeduct.setCreateTime(new Date());
            newRecordDeduct.setUpdateTime(new Date());
            result.add(newRecordDeduct);

            ChargeConsumeRecord newRecordReturn = new ChargeConsumeRecord();
            BeanUtils.copyProperties(chargeConsumeRecord, newRecordReturn);
            newRecordReturn.setId(null);
            newRecordReturn.setPrice(tariffValue);
            newRecordReturn.setConsumeType(ConsumeTypeEnum.RETURN.getCode());
            newRecordReturn.setProcessed(ProcessStatusEnum.PROCESSED.getCode());
            newRecordReturn.setTariffType(realTariffType);
            newRecordReturn.setCreateTime(new Date());
            newRecordReturn.setUpdateTime(new Date());
            result.add(newRecordReturn);
        }
        return result;
    }

    //生成一条仿制记录
    private ChargeConsumeRecord getNewChargeConsumeRecord(ChargeConsumeRecord chargeConsumeRecord, Integer priceOfTariffType, Integer realTariffType, Integer consumeType, Integer processed) {
        ChargeConsumeRecord newRecord = new ChargeConsumeRecord();
        BeanUtils.copyProperties(chargeConsumeRecord, newRecord);
        newRecord.setId(null);
        newRecord.setPrice(priceOfTariffType);
        newRecord.setConsumeType(consumeType);
        newRecord.setProcessed(processed);
        newRecord.setTariffType(realTariffType);
        newRecord.setCreateTime(new Date());
        newRecord.setUpdateTime(new Date());
        return newRecord;
    }
}
