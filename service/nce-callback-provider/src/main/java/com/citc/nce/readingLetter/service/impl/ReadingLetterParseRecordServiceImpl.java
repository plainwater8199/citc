package com.citc.nce.readingLetter.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.citc.nce.auth.accountmanagement.AccountManagementApi;
import com.citc.nce.auth.accountmanagement.vo.AccountManagementResp;
import com.citc.nce.auth.csp.recharge.Const.*;
import com.citc.nce.auth.csp.recharge.RechargeTariffApi;
import com.citc.nce.auth.csp.recharge.vo.RechargeTariffDetailResp;
import com.citc.nce.auth.prepayment.DeductionAndRefundApi;
import com.citc.nce.auth.prepayment.vo.CreateChargeConsumeRecordReq;
import com.citc.nce.auth.prepayment.vo.ReceiveConfirmReq;
import com.citc.nce.auth.readingLetter.dataStatistics.vo.FifthReadingLetterDailyReportSelectVo;
import com.citc.nce.auth.readingLetter.dataStatistics.vo.ReadingLetterDailyReportSelectVo;
import com.citc.nce.auth.readingLetter.shortUrl.ReadingLetterShortUrlApi;
import com.citc.nce.auth.readingLetter.shortUrl.vo.ReadingLetterShortUrlVo;
import com.citc.nce.auth.readingLetter.template.enums.SmsTypeEnum;
import com.citc.nce.authcenter.csp.CspApi;
import com.citc.nce.authcenter.csp.CspCustomerApi;
import com.citc.nce.authcenter.csp.vo.UserInfoVo;
import com.citc.nce.common.constants.CarrierEnum;
import com.citc.nce.common.constants.Constants;
import com.citc.nce.common.core.exception.BizException;
import com.citc.nce.common.core.pojo.BaseUser;
import com.citc.nce.common.redis.config.RedisService;
import com.citc.nce.common.util.SessionContextUtil;
import com.citc.nce.developer.DeveloperSendApi;
import com.citc.nce.readingLetter.dao.ReadingLetterParseRecordDailyReportDao;
import com.citc.nce.readingLetter.dao.ReadingLetterParseRecordDao;
import com.citc.nce.readingLetter.entity.ReadingLetterParseRecordDailyReportDo;
import com.citc.nce.readingLetter.entity.ReadingLetterParseRecordDo;
import com.citc.nce.readingLetter.req.FifthReadingLetterDailyReportSelectReq;
import com.citc.nce.readingLetter.req.ReadingLetterDailyReportSelectByCspIdReq;
import com.citc.nce.readingLetter.req.ReadingLetterDailyReportSelectReq;
import com.citc.nce.readingLetter.req.ReadingLetterMsgSendTotalResp;
import com.citc.nce.readingLetter.req.ReadingLetterParseRecordReq;
import com.citc.nce.readingLetter.req.SelectParseRecordToDailyReportReq;
import com.citc.nce.readingLetter.req.TodayDataSelectReq;
import com.citc.nce.readingLetter.service.ReadingLetterParseRecordService;
import com.citc.nce.readingLetter.vo.CspReadingLetterNumVo;
import com.citc.nce.readingLetter.vo.DailyReportListVo;
import com.citc.nce.readingLetter.vo.ReadingLetterSendTotalResp;
import com.citc.nce.readingLetter.vo.ShortUrlIdListVo;
import com.citc.nce.robot.api.RobotGroupSendPlansApi;
import com.citc.nce.robot.api.RobotGroupSendPlansDetailApi;
import com.citc.nce.robot.vo.RobotGroupSendPlansAndOperatorCode;
import com.citc.nce.tenant.MsgRecordApi;
import com.citc.nce.tenant.vo.resp.MsgRecordResp;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 文件名:ReadingLetterParseRecordServiceImpl
 * 创建者:zhujinyu
 * 创建时间:2024/7/17 17:19
 * 描述:
 */
@Service
@Slf4j
public class ReadingLetterParseRecordServiceImpl implements ReadingLetterParseRecordService {

    @Resource
    private ReadingLetterShortUrlApi shortUrlApi;
    @Resource
    private RobotGroupSendPlansApi groupSendPlansApi;
    @Resource
    private ReadingLetterParseRecordDao parseRecordDao;
    @Resource
    private ReadingLetterParseRecordDailyReportDao recordDailyReportDao;
    @Resource
    private RobotGroupSendPlansDetailApi robotGroupSendPlansDetailApi;
    @Resource
    private CspApi cspApi;
    @Value("${rocketmq.readingLetterPlus.topic}")
    private String readingLetterPlusTopic;
    @Resource
    private RocketMQTemplate rocketMQTemplate;
    @Resource
    private RedisService redisService;
    @Resource
    private DeveloperSendApi developerSendApi;
    @Resource
    private DeductionAndRefundApi deductionAndRefundApi;
    @Resource
    private MsgRecordApi msgRecordApi;
    @Resource
    private RechargeTariffApi rechargeTariffApi;
    @Resource
    private AccountManagementApi accountManagementApi;
    @Resource
    private CspCustomerApi cspCustomerApi;
    @Resource
    private ReadingLetterShortUrlApi readingLetterShortUrlApi;

    public static final String FIFTH_REDIS_KEY_FORMAT = "fifth_reading_letter_parse_total:%s:%s";
    public static final String PLUS_REDIS_KEY_FORMAT = "reading_letter_plus_parse_total:%s:%s";
    private static final String TABLE_NAME_PREFIX = "reading_letter_parse_record_";

    @Override
    public void receive(ReadingLetterParseRecordReq req) {
        //先判断解析是否成功
        if (req.getStatus() == 0) {
            //解析成功
            String aimUrl = req.getAimUrl();
            //通过短链找到是哪个customer的记录
            ReadingLetterShortUrlVo shortUrl = shortUrlApi.findShortUrl(aimUrl);
            //5G阅信
            if (Objects.isNull(shortUrl)) {
                //根据taskId寻找对应信息
//                templateId=894284105975352924, custFlag=15108316546, custId=909140062845742169,
                String phoneNum = req.getCustFlag();
                String taskId = req.getCustId();
                log.info("5G阅信解析回执,状态为成功");
                ReadingLetterParseRecordDo readingLetterParseRecordDo = new ReadingLetterParseRecordDo();
                //找到task相关信息
                while (redisService.hasKey(String.format(Constants.MSG_RECORDS_INSERT_REDIS_KEY, taskId))) {
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        throw new RuntimeException(e);
                    }
                }
                RobotGroupSendPlansAndOperatorCode task = robotGroupSendPlansDetailApi.selectByTaskId(taskId);
                BeanUtils.copyProperties(req, readingLetterParseRecordDo);
                readingLetterParseRecordDo.setSmsType(SmsTypeEnum.FIFTH_READING_LETTER.getCode());
                readingLetterParseRecordDo.setSendResult(req.getStatus());
                Date date = new Date();
                String day = new SimpleDateFormat("yyyyMMdd").format(date);
                readingLetterParseRecordDo.setCustomerId(task.getCustomerId());
                readingLetterParseRecordDo.setReceiptTime(date);
                readingLetterParseRecordDo.setOperatorCode(task.getOperatorCode());
                readingLetterParseRecordDo.setPlatformTemplateId(req.getTemplateId());
                readingLetterParseRecordDo.setTaskId(taskId);
                readingLetterParseRecordDo.setChatbotAccount(task.getAppId());
                readingLetterParseRecordDo.setShortUrl(aimUrl);
                readingLetterParseRecordDo.setDayStr(day);
                readingLetterParseRecordDo.setPhoneNum(phoneNum);
                //正常来源是群发
                readingLetterParseRecordDo.setSourceType(1);
                readingLetterParseRecordDo.setGroupSendId(task.getPlanId());
                //查询是否是开发者服务发送的5G阅信
                if (Objects.isNull(task.getPlanId()) && Objects.isNull(task.getId())) {
                    String oldMessageId = task.getOldMessageId();
                    Long developerCustomer5gApplicationId = developerSendApi.findSendRecord(oldMessageId, task.getCustomerId());
                    if (Objects.nonNull(developerCustomer5gApplicationId)) {
                        // 来源是开发者服务
                        readingLetterParseRecordDo.setSourceType(2);
                        log.info("来源为开发者服务,taskId:{}, developerCustomer5gApplicationId:{}", taskId, developerCustomer5gApplicationId);
                        readingLetterParseRecordDo.setGroupSendId(developerCustomer5gApplicationId);
                    } else {
                        log.info("信息异常,既不是群发也不是开发者服务,taskId:{}", taskId);
                        return;
                    }
                }
                parseRecordDao.insert(readingLetterParseRecordDo);
                //拼凑redisKey
                String key = String.format(FIFTH_REDIS_KEY_FORMAT, task.getCustomerId().substring(0, 10), task.getOperatorCode());
                //redis中 解析量+1
                Long increase = redisService.increase(key);
                if (Objects.nonNull(increase)) {
                    log.info("csp:{} 5G阅信解析量+1,OperatorCode:{}当前值为:{}", task.getCustomerId().substring(0, 10), task.getOperatorCode(), increase);
                } else {
                    //不用分布式锁了,影响速度,并且没有
                    //找到真实的总数值
                    Map<Integer, Integer> readingLetterCspTotal = findReadingLetterCspTotal(task.getCustomerId().substring(0, 10), task.getOperatorCode(), SmsTypeEnum.FIFTH_READING_LETTER.getCode());
                    Integer num = readingLetterCspTotal.get(task.getOperatorCode());
                    redisService.setCacheObject(key, num);
                }
//查找MsgRecord表,  如果是使用的余额付费, 需要修改扣费记录表中的行数据.
                dealBalance(task, phoneNum);
            }
            //阅信+
            else {
                String customerId = shortUrl.getCustomerId();
                log.info("req.getCustFlag():{}", req.getCustFlag());
                log.info("阅信+解析回执,状态为成功");
                //根据CspId可以准确新建记录
                ReadingLetterParseRecordDo readingLetterParseRecordDo = new ReadingLetterParseRecordDo();
                BeanUtils.copyProperties(req, readingLetterParseRecordDo);
                readingLetterParseRecordDo.setCustomerId(customerId);
                readingLetterParseRecordDo.setSmsType(SmsTypeEnum.READING_LETTER_PLUS.getCode());
                readingLetterParseRecordDo.setSendResult(req.getStatus());
                readingLetterParseRecordDo.setShortUrlId(shortUrl.getId());
                readingLetterParseRecordDo.setOperatorCode(shortUrl.getOperatorCode());
                Date date = new Date();
                String day = new SimpleDateFormat("yyyyMMdd").format(date);
                readingLetterParseRecordDo.setReceiptTime(date);
                readingLetterParseRecordDo.setPlatformTemplateId(req.getTemplateId());
                readingLetterParseRecordDo.setSmsType(SmsTypeEnum.READING_LETTER_PLUS.getCode());
                readingLetterParseRecordDo.setDayStr(day);
                readingLetterParseRecordDo.setShortUrl(aimUrl);
                parseRecordDao.insert(readingLetterParseRecordDo);

                //拼凑redisKey
                String key = String.format(PLUS_REDIS_KEY_FORMAT, customerId.substring(0, 10), shortUrl.getOperatorCode());
                //redis中 解析量+1
                Long increase = redisService.increase(key);
                //不用分布式锁了,影响速度,并且没有重大影响
                if (Objects.isNull(increase)) {
                    //找到真实的总数值
                    Map<Integer, Integer> readingLetterCspTotal = findReadingLetterCspTotal(customerId.substring(0, 10), shortUrl.getOperatorCode(), SmsTypeEnum.READING_LETTER_PLUS.getCode());
                    Integer num = readingLetterCspTotal.get(shortUrl.getOperatorCode());
                    redisService.setCacheObject(key, num);
                }
                //查找MsgRecord表,  如果是使用的余额付费, 需要修改扣费记录表中的行数据.
                dealBalance(req, shortUrl);

                //短链解析记录需要在短链表中计数加一
                log.info("短链中的解析数+1 , shortUrl.Id : {}", shortUrl.getId());
                readingLetterShortUrlApi.handleParseRecord(shortUrl.getId());
            }
        } else {
            log.info("阅信+解析回执,状态为失败:{}", JSON.toJSONString(req));
        }
    }

    private void dealBalance(ReadingLetterParseRecordReq req, ReadingLetterShortUrlVo shortUrl) {
        //找到当前资费id及价格
        RechargeTariffDetailResp rechargeTariff = rechargeTariffApi.getRechargeTariff(shortUrl.getAccountId());
        UserInfoVo userInfo = cspCustomerApi.getByCustomerId(shortUrl.getCustomerId());
        //在扣费记录表中添加一条记录
        deductionAndRefundApi.justCreateChargeConsumeRecord(CreateChargeConsumeRecordReq.builder()
                .accountId(req.getCustFlag())
                .customerId(shortUrl.getCustomerId())
                .messageId(String.valueOf(shortUrl.getId()))
                .msgType(AccountTypeEnum.READING_LETTER_ACCOUNT.getCode())
                .payType(userInfo.getPayType().getCode())
                .processed(ProcessStatusEnum.PROCESSED.getCode())
                .price(rechargeTariff.getYxPlusAnalysisPrice())
                .tariffId(rechargeTariff.getId())
                .tariffType(TariffTypeEnum.READING_LETTER_PLUS_PARSE.getCode())
                .build());
    }

    private void dealBalance(RobotGroupSendPlansAndOperatorCode task, String phoneNum) {
        //处理5G阅信解析的扣费记录
        MsgRecordResp msgRecordResp = msgRecordApi.queryRecordByMessageIdAndPhoneNumber(task.getOldMessageId(), phoneNum, task.getCustomerId());
        if (Objects.isNull(msgRecordResp)) {
            log.error("未找到对应的msgRecord记录, oldMessageId:{}, phoneNum:{}", task.getOldMessageId(), phoneNum);
            return;
        }
        //是扣余额,需要处理余额扣费记录
        if (Objects.equals(PaymentTypeEnum.BALANCE.getCode(), msgRecordResp.getConsumeCategory())) {
            deductionAndRefundApi.receiveConfirm(ReceiveConfirmReq.builder()
                    .messageId(task.getOldMessageId())
                    .customerId(task.getCustomerId())
                    .tariffType(TariffTypeEnum.FIVE_G_READING_LETTER_PARSE.getCode())
                    .phoneNumber(phoneNum)
                    .build());
        } else if (Objects.equals(PaymentTypeEnum.SET_MEAL.getCode(), msgRecordResp.getConsumeCategory())) {
            //找到chatbot信息
            AccountManagementResp chatbot = accountManagementApi.getAccountManagementByAccountId(task.getAppId());
            //找到当前资费id及价格
            RechargeTariffDetailResp rechargeTariff = rechargeTariffApi.getRechargeTariff(chatbot.getChatbotAccountId());
            //在扣费记录表中添加一条记录
            deductionAndRefundApi.justCreateChargeConsumeRecord(CreateChargeConsumeRecordReq.builder()
                    .accountId(chatbot.getChatbotAccountId())
                    .customerId(task.getCustomerId())
                    .messageId(task.getOldMessageId())
                    .msgType(AccountTypeEnum.FIFTH_MESSAGES.getCode())
                    .payType(PayTypeEnum.POST_PAYMENT.getCode())
                    .phoneNumber(phoneNum)
                    .processed(ProcessStatusEnum.PROCESSED.getCode())
                    .price(rechargeTariff.getYxAnalysisPrice())
                    .tariffId(rechargeTariff.getId())
                    .tariffType(TariffTypeEnum.FIVE_G_READING_LETTER_PARSE.getCode())
                    .build());

            //在扣费记录表中添加一条短信记录
            deductionAndRefundApi.justCreateChargeConsumeRecord(CreateChargeConsumeRecordReq.builder()
                    .accountId(chatbot.getChatbotAccountId())
                    .customerId(task.getCustomerId())
                    .messageId(task.getOldMessageId())
                    .msgType(AccountTypeEnum.FIFTH_MESSAGES.getCode())
                    .payType(PayTypeEnum.POST_PAYMENT.getCode())
                    .phoneNumber(phoneNum)
                    .processed(ProcessStatusEnum.PROCESSED.getCode())
                    .price(rechargeTariff.getFallbackSmsPrice())
                    .tariffId(rechargeTariff.getId())
                    .tariffType(TariffTypeEnum.FALLBACK_SMS.getCode())
                    .build());
        }
    }

    //获取该CSP某类型(阅信+或5G阅信)的各个运营商的解析总数
    private Map<Integer, Integer> findReadingLetterCspTotal(String cspId, Integer operatorCode, Integer smsType) {
        //搜索日报记录表
        ReadingLetterDailyReportSelectByCspIdReq req = new ReadingLetterDailyReportSelectByCspIdReq();
        req.setOperatorCode(operatorCode);
        req.setCspId(cspId);
        req.setSmsType(smsType);
        List<CspReadingLetterNumVo> records = recordDailyReportDao.selectAllByCspId(req);

        //搜索解析记录表(今日)
        TodayDataSelectReq selectShortUrlIdListReq = new TodayDataSelectReq();
        String dayStr = new SimpleDateFormat("yyyyMMdd").format(new Date());
        selectShortUrlIdListReq.setDayStr(dayStr);
        //cspId
        selectShortUrlIdListReq.setCustomerId(cspId);
        selectShortUrlIdListReq.setSmsType(smsType);
        selectShortUrlIdListReq.setOperatorCode(operatorCode);
        List<CspReadingLetterNumVo> todayRecords = parseRecordDao.selectTodayDailyReportOfCsp(selectShortUrlIdListReq);

        HashMap<Integer, Integer> result = new HashMap<>();
        result.put(CarrierEnum.Unicom.getValue(), 0);
        result.put(CarrierEnum.CMCC.getValue(), 0);
        result.put(CarrierEnum.Telecom.getValue(), 0);
        for (CspReadingLetterNumVo record : records) {
            result.put(record.getOperatorCode(), result.get(record.getOperatorCode()) + record.getNum());
        }
        for (CspReadingLetterNumVo record : todayRecords) {
            result.put(record.getOperatorCode(), result.get(record.getOperatorCode()) + record.getNum());
        }
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void dailyReport(int offsetDay, String selectedCspId) {

        DateTime yesterday = DateUtil.date().offset(DateField.DAY_OF_MONTH, offsetDay);
        DateTime targetDay = DateUtil.beginOfDay(yesterday.toJdkDate());
        String dayStr = new SimpleDateFormat("yyyyMMdd").format(targetDay.toJdkDate());
        log.info("----------------准备开始制作阅信+日报----------------");
        List<String> cspIds = cspApi.queryAllCspId().getCspIds();
        if (StrUtil.isNotBlank(selectedCspId)) {
            String userId = SessionContextUtil.getUser().getUserId();
            if (!userId.equals(selectedCspId) && !userId.startsWith(selectedCspId)) {
                throw new BizException("无权限操作");
            }
            //先删除旧数据
            LambdaQueryWrapper<ReadingLetterParseRecordDailyReportDo> deleteWrapper = new LambdaQueryWrapper<>();
            deleteWrapper.likeRight(ReadingLetterParseRecordDailyReportDo::getCustomerId, selectedCspId)
                    .eq(ReadingLetterParseRecordDailyReportDo::getDayTime, targetDay.toJdkDate());
            int delete = recordDailyReportDao.delete(deleteWrapper);
            log.info("删除旧数据:{}", delete);
            cspIds = cspIds.stream().filter(cspId -> cspId.equals(selectedCspId)).collect(Collectors.toList());
        }
        //阅信+
        for (String cspId : cspIds) {
            log.info("cspId:{}", cspId);
            SelectParseRecordToDailyReportReq selectShortUrlIdListReq = new SelectParseRecordToDailyReportReq();
            selectShortUrlIdListReq.setTableName(TABLE_NAME_PREFIX + cspId);
            selectShortUrlIdListReq.setDayStr(dayStr);
            List<ShortUrlIdListVo> shortUrlIdListVos = new ArrayList<>();
            //查询该CSP下  昨天 的各阅信+短链的解析数据
            try {

                shortUrlIdListVos = parseRecordDao.selectShortUrlIdList(selectShortUrlIdListReq);
            } catch (Exception e) {
                log.error("查询短链失败,cspId:{},dayStr:{}", cspId, dayStr, e);
                createTable(cspId);
            }
            log.info("shortUrlIdListVos:{}", shortUrlIdListVos);

            //日报对象
            List<ReadingLetterParseRecordDailyReportDo> dailyReportDos = new ArrayList<>();
            //查询所有的短链
            if (CollectionUtil.isNotEmpty(shortUrlIdListVos)) {
                log.info("阅信+日报列表非空, 准备正式制作日报");
                List<Long> urlIds = shortUrlIdListVos.stream().map(ShortUrlIdListVo::getShortUrlId).collect(Collectors.toList());
                //查询到模板和账号的详细信息Map
                Map<Long, ReadingLetterShortUrlVo> result = shortUrlApi.findShortUrlsByUrlIds(urlIds);
                //将详细信息补充到短链对象中
                for (ShortUrlIdListVo shortUrl : shortUrlIdListVos) {
                    Long shortUrlId = shortUrl.getShortUrlId();
                    //找到属于该短链的详细信息
                    ReadingLetterShortUrlVo readingLetterShortUrlVo = result.get(shortUrlId);

                    dailyReportDos.add(
                            new ReadingLetterParseRecordDailyReportDo()
                                    .setDayTime(targetDay.toJdkDate())
                                    .setCustomerId(readingLetterShortUrlVo.getCustomerId())
                                    .setShortUrlId(shortUrl.getShortUrlId())
                                    .setSuccessNumber(shortUrl.getSuccessNumber())
                                    .setOperatorCode(shortUrl.getOperatorCode())
                                    .setSmsType(SmsTypeEnum.READING_LETTER_PLUS.getCode())
                                    .setShortUrl(readingLetterShortUrlVo.getShortUrl()));
                }
                try {
                    //将urls记录存入日报表中.
                    recordDailyReportDao.insertBatch(dailyReportDos);
                } catch (Exception e) {
                    log.error("阅信+日报插入失败,cspId:{},dayStr:{}", cspId, dayStr, e);
                }
            }
        }

        log.info("----------------准备开始制作5G阅信日报----------------");
        for (String cspId : cspIds) {

            List<ReadingLetterParseRecordDailyReportDo> fifthDailyReportDos = new ArrayList<>();
            log.info("cspId:{}", cspId);
            SelectParseRecordToDailyReportReq selectShortUrlIdListReq = new SelectParseRecordToDailyReportReq();
            selectShortUrlIdListReq.setTableName(TABLE_NAME_PREFIX + cspId);
            selectShortUrlIdListReq.setDayStr(dayStr);

            //查询该CSP下  昨天 的各5G阅信的解析数据
            List<DailyReportListVo> fifthReadingLetterToDailyReportos = new ArrayList<>();
            try {
                //查询该CSP下  昨天 的各5G阅信的解析数据
                fifthReadingLetterToDailyReportos = parseRecordDao.selectFifthReadingLetterToDailyReport(selectShortUrlIdListReq);
                log.info("shortUrlIdListVos:{}", fifthReadingLetterToDailyReportos);
            } catch (Exception e) {
                log.error("查询短链失败,cspId:{},dayStr:{}", cspId, dayStr, e);
                createTable(cspId);
            }
            //查询5G阅信的数据统计
            if (CollectionUtil.isNotEmpty(fifthReadingLetterToDailyReportos)) {
                log.info("5G阅信日报列表非空, 准备正式制作日报");
                //获取bind task信息
                Map<String, RobotGroupSendPlansAndOperatorCode> taskIdMap = new HashMap<>();
                List<String> taskIds = fifthReadingLetterToDailyReportos.stream().map(DailyReportListVo::getTaskId).map(Object::toString).collect(Collectors.toList());
                List<RobotGroupSendPlansAndOperatorCode> byTaskIds = robotGroupSendPlansDetailApi.getByTaskIds(taskIds);
                byTaskIds.forEach(task -> taskIdMap.put(task.getTaskId(), task));

                //将详细信息补充到短链对象中
                for (DailyReportListVo dailyReportVo : fifthReadingLetterToDailyReportos) {
                    //找到属于该短链的详细信息
                    String taskId = dailyReportVo.getTaskId();
                    RobotGroupSendPlansAndOperatorCode robotGroupSendPlansAndOperatorCode = taskIdMap.get(taskId);

                    fifthDailyReportDos.add(
                            new ReadingLetterParseRecordDailyReportDo()
                                    .setDayTime(targetDay.toJdkDate())
                                    .setCustomerId(robotGroupSendPlansAndOperatorCode.getCustomerId())
                                    .setSmsType(SmsTypeEnum.FIFTH_READING_LETTER.getCode())
                                    .setSuccessNumber(dailyReportVo.getSuccessNumber())
                                    .setOperatorCode(robotGroupSendPlansAndOperatorCode.getOperatorCode())
                                    .setGroupSendId(dailyReportVo.getGroupSendId())
                                    .setSourceType(dailyReportVo.getSourceType())
                                    .setChatbotAccount(robotGroupSendPlansAndOperatorCode.getAppId())
                                    .setPlatformTemplateId(dailyReportVo.getPlatformTemplateId())
                                    .setTaskId(dailyReportVo.getTaskId())
                                    .setShortUrl(dailyReportVo.getShortUrl()));
                }
                ;

                try {
                    //将urls记录存入日报表中.
                    recordDailyReportDao.insertBatch(fifthDailyReportDos);
                } catch (Exception e) {
                    log.error("5G阅信日报插入失败,cspId:{},dayStr:{}", cspId, dayStr, e);
                }
            }
        }

    }

    @Override
    public List<ReadingLetterDailyReportSelectVo> selectRecords(ReadingLetterDailyReportSelectReq req) {
        //每日报表(除今日以外)
        List<ReadingLetterDailyReportSelectVo> readingLetterDailyReportSelectVos = recordDailyReportDao.selectRecords(req);
        //判断是否需要把今天的数据也查出来
        if (DateUtil.isSameDay(req.getEndTime(), DateUtil.date())) {
            //查询今天的数据
            String customerId = SessionContextUtil.getLoginUser().getUserId();
            String dayStr = new SimpleDateFormat("yyyyMMdd").format(new Date());
            TodayDataSelectReq selectShortUrlIdListReq = new TodayDataSelectReq();
            selectShortUrlIdListReq.setCustomerId(req.getCustomerId());
            selectShortUrlIdListReq.setDayStr(dayStr);
            selectShortUrlIdListReq.setShortUrlIds(req.getShortUrlIds());

            //查询该CSP下  昨天 的各短链的解析数据
            List<ShortUrlIdListVo> shortUrlIdListVos = parseRecordDao.selectTodayDailyReport(selectShortUrlIdListReq);
            for (ShortUrlIdListVo shortUrl : shortUrlIdListVos) {
                //判断是否该短链记录已经查询出来一部分
                Long shortUrlId = shortUrl.getShortUrlId();
                Optional<ReadingLetterDailyReportSelectVo> first = readingLetterDailyReportSelectVos.stream().filter(vo -> vo.getShortUrlId().equals(shortUrlId)).findFirst();
                if (first.isPresent()) {
                    //在原来记录基础上加今日的成功解析数
                    ReadingLetterDailyReportSelectVo readingLetterDailyReportSelectVo = first.get();
                    readingLetterDailyReportSelectVo.setSuccessNumber(readingLetterDailyReportSelectVo.getSuccessNumber() + shortUrl.getSuccessNumber());
                } else {
                    //新建记录
                    readingLetterDailyReportSelectVos.add(
                            new ReadingLetterDailyReportSelectVo()
                                    .setShortUrlId(shortUrlId)
                                    .setSuccessNumber(shortUrl.getSuccessNumber()));
                }

            }
        }
        //把这些数据糅合到一起
        return readingLetterDailyReportSelectVos;
    }

    @Override
    public List<FifthReadingLetterDailyReportSelectVo> selectRecords(FifthReadingLetterDailyReportSelectReq req) {
        //每日报表(除今日以外)
        List<FifthReadingLetterDailyReportSelectVo> readingLetterDailyReportSelectVos = recordDailyReportDao.selectFifthtRecords(req);
        //判断是否需要把今天的数据也查出来
        if (DateUtil.isSameDay(req.getEndTime(), DateUtil.date())) {
            //查询今天的数据
            String customerId = SessionContextUtil.getLoginUser().getUserId();
            String dayStr = new SimpleDateFormat("yyyyMMdd").format(new Date());
            TodayDataSelectReq selectShortUrlIdListReq = new TodayDataSelectReq();
            selectShortUrlIdListReq.setCustomerId(req.getCustomerId());
            selectShortUrlIdListReq.setDayStr(dayStr);
            selectShortUrlIdListReq.setChatbotAccounts(req.getChatbotAccounts());
            selectShortUrlIdListReq.setPlanIds(req.getPlanIds());

            //查询该CSP下  今天 的各短链的解析数据
            List<DailyReportListVo> dailyReportListVos = parseRecordDao.selectTodayFifthDailyReport(selectShortUrlIdListReq);
            for (DailyReportListVo dailyReportListVO : dailyReportListVos) {
                //判断是否该短链记录已经查询出来一部分
                String taskId = dailyReportListVO.getTaskId();
                Optional<FifthReadingLetterDailyReportSelectVo> first = readingLetterDailyReportSelectVos.stream().filter(vo -> vo.getTaskId().equals(taskId)).findFirst();
                if (first.isPresent()) {
                    //在原来记录基础上加今日的成功解析数
                    FifthReadingLetterDailyReportSelectVo readingLetterDailyReportSelectVo = first.get();
                    readingLetterDailyReportSelectVo.setSuccessNumber(readingLetterDailyReportSelectVo.getSuccessNumber() + dailyReportListVO.getSuccessNumber());
                } else {
                    //新建记录
                    readingLetterDailyReportSelectVos.add(
                            new FifthReadingLetterDailyReportSelectVo()
                                    .setTaskId(taskId)
                                    .setPlatformTemplateId(dailyReportListVO.getPlatformTemplateId())
                                    .setShortUrl(dailyReportListVO.getShortUrl())
                                    .setChatbotAccount(dailyReportListVO.getChatbotAccount())
                                    .setGroupSendId(dailyReportListVO.getGroupSendId())
                                    .setSourceType(dailyReportListVO.getSourceType())
                                    .setSuccessNumber(dailyReportListVO.getSuccessNumber()));
                }
            }
        }
        //把这些数据糅合到一起
        return readingLetterDailyReportSelectVos;
    }

    @Override
    public ReadingLetterSendTotalResp queryReadingLetterCspTotal() {
        BaseUser user = SessionContextUtil.getUser();
        if (user.getIsCustomer()) {
            throw new BizException("非CSP用户");
        }
        String userId = user.getUserId();

        Map<Integer, Integer> fifthReadingLetterCspTotal = new HashMap<>();
        //从redis中查询5G阅信
        String keyUnicom = String.format(FIFTH_REDIS_KEY_FORMAT, userId, CarrierEnum.Unicom.getValue());
        String keyCmcc = String.format(FIFTH_REDIS_KEY_FORMAT, userId, CarrierEnum.CMCC.getValue());
        String keyTelecom = String.format(FIFTH_REDIS_KEY_FORMAT, userId, CarrierEnum.Telecom.getValue());
        List<String> req = new ArrayList<>();
        req.add(keyUnicom);
        req.add(keyCmcc);
        req.add(keyTelecom);
        List<Object> fifThCacheObject = redisService.getCacheObject(req);
        if (fifThCacheObject.contains(null)) {
            fifthReadingLetterCspTotal = findReadingLetterCspTotal(userId, null, SmsTypeEnum.FIFTH_READING_LETTER.getCode());
            fifthReadingLetterCspTotal.put(-1,
                    fifthReadingLetterCspTotal.get(CarrierEnum.Unicom.getValue()) +
                            fifthReadingLetterCspTotal.get(CarrierEnum.CMCC.getValue()) +
                            fifthReadingLetterCspTotal.get(CarrierEnum.Telecom.getValue()));
            redisService.setCacheObject(keyUnicom, fifthReadingLetterCspTotal.get(CarrierEnum.Unicom.getValue()));
            redisService.setCacheObject(keyCmcc, fifthReadingLetterCspTotal.get(CarrierEnum.CMCC.getValue()));
            redisService.setCacheObject(keyTelecom, fifthReadingLetterCspTotal.get(CarrierEnum.Telecom.getValue()));
        } else {
            fifthReadingLetterCspTotal.put(CarrierEnum.Unicom.getValue(), (Integer) fifThCacheObject.get(0));
            fifthReadingLetterCspTotal.put(CarrierEnum.CMCC.getValue(), (Integer) fifThCacheObject.get(1));
            fifthReadingLetterCspTotal.put(CarrierEnum.Telecom.getValue(), (Integer) fifThCacheObject.get(2));
            fifthReadingLetterCspTotal.put(-1, (Integer) fifThCacheObject.get(0) + (Integer) fifThCacheObject.get(1) + (Integer) fifThCacheObject.get(2));
        }


        Map<Integer, Integer> readingLetterPlusCspTotal = new HashMap<>();
        //从redis中查询阅信+
        String keyUnicomPlus = String.format(PLUS_REDIS_KEY_FORMAT, userId, CarrierEnum.Unicom.getValue());
        String keyCmccPlus = String.format(PLUS_REDIS_KEY_FORMAT, userId, CarrierEnum.CMCC.getValue());
        String keyTelecomPlus = String.format(PLUS_REDIS_KEY_FORMAT, userId, CarrierEnum.Telecom.getValue());
        List<String> req2 = new ArrayList<>();
        req2.add(keyUnicomPlus);
        req2.add(keyCmccPlus);
        req2.add(keyTelecomPlus);
        List<Object> plusCacheObject = redisService.getCacheObject(req2);
        if (plusCacheObject.contains(null)) {
            readingLetterPlusCspTotal = findReadingLetterCspTotal(userId, null, SmsTypeEnum.READING_LETTER_PLUS.getCode());

            readingLetterPlusCspTotal.put(-1,
                    readingLetterPlusCspTotal.get(CarrierEnum.Unicom.getValue()) +
                            readingLetterPlusCspTotal.get(CarrierEnum.CMCC.getValue()) +
                            readingLetterPlusCspTotal.get(CarrierEnum.Telecom.getValue()));

            redisService.setCacheObject(keyUnicomPlus, readingLetterPlusCspTotal.get(CarrierEnum.Unicom.getValue()));
            redisService.setCacheObject(keyCmccPlus, readingLetterPlusCspTotal.get(CarrierEnum.CMCC.getValue()));
            redisService.setCacheObject(keyTelecomPlus, readingLetterPlusCspTotal.get(CarrierEnum.Telecom.getValue()));
        } else {
            readingLetterPlusCspTotal.put(CarrierEnum.Unicom.getValue(), (Integer) plusCacheObject.get(0));
            readingLetterPlusCspTotal.put(CarrierEnum.CMCC.getValue(), (Integer) plusCacheObject.get(1));
            readingLetterPlusCspTotal.put(CarrierEnum.Telecom.getValue(), (Integer) plusCacheObject.get(2));
            readingLetterPlusCspTotal.put(-1, (Integer) plusCacheObject.get(0) + (Integer) plusCacheObject.get(1) + (Integer) plusCacheObject.get(2));
        }
        return new ReadingLetterSendTotalResp(fifthReadingLetterCspTotal, readingLetterPlusCspTotal);
    }

    @Override
    public ReadingLetterMsgSendTotalResp queryReadingLetterTotal() {
        ReadingLetterDailyReportSelectReq req = new ReadingLetterDailyReportSelectReq();
        req.setCustomerId(SessionContextUtil.getLoginUser().getUserId());
        DateTime yestoday = DateUtil.date().offset(DateField.DAY_OF_MONTH, -1);
        Date startTime = DateUtil.beginOfDay(yestoday).toJdkDate();
        req.setStartTime(startTime);
        Date endTime = DateUtil.endOfDay(yestoday).toJdkDate();
        req.setEndTime(endTime);
        //查昨日的阅信+记录
        List<ReadingLetterDailyReportSelectVo> readingLetterDailyReportSelectVos = recordDailyReportDao.selectRecords(req);

        FifthReadingLetterDailyReportSelectReq fifthSelectReq = new FifthReadingLetterDailyReportSelectReq();
        BeanUtil.copyProperties(req, fifthSelectReq);
        //查昨日的5G阅信记录
        List<FifthReadingLetterDailyReportSelectVo> fifthReadingLetterDailyReportSelectVos = recordDailyReportDao.selectFifthtRecords(fifthSelectReq);

        ReadingLetterMsgSendTotalResp readingLetterMsgSendTotalResp = new ReadingLetterMsgSendTotalResp();
        if (CollectionUtil.isNotEmpty(readingLetterDailyReportSelectVos)) {
            readingLetterMsgSendTotalResp.setReadingLetterPlusParseNum(readingLetterDailyReportSelectVos.stream().mapToInt(ReadingLetterDailyReportSelectVo::getSuccessNumber).sum());
        }
        if (CollectionUtil.isNotEmpty(fifthReadingLetterDailyReportSelectVos)) {
            readingLetterMsgSendTotalResp.setFifthReadingLetterParseNum(fifthReadingLetterDailyReportSelectVos.stream().mapToInt(FifthReadingLetterDailyReportSelectVo::getSuccessNumber).sum());
        }
        return readingLetterMsgSendTotalResp;
    }

    @Override
    public void createTable(String cspId) {
        parseRecordDao.createTableIfNotExist(TABLE_NAME_PREFIX + cspId);
    }
}
