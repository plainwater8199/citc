package com.citc.nce.developer.service.impl;

import cn.hutool.core.date.DateTime;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.citc.nce.common.util.DateUtil;
import com.citc.nce.common.util.SessionContextUtil;
import com.citc.nce.dataStatistics.vo.msg.SendTrendsStatisticDto;
import com.citc.nce.developer.dao.DeveloperSendMapper;
import com.citc.nce.developer.dao.DeveloperSendStatisticsMapper;
import com.citc.nce.developer.dto.DeveloperStatisticsItem;
import com.citc.nce.developer.entity.DeveloperSendDo;
import com.citc.nce.developer.entity.DeveloperSendStatisticsDo;
import com.citc.nce.developer.service.DeveloperSendStatisticsService;
import com.citc.nce.developer.vo.*;
import com.citc.nce.tenant.robot.dao.MsgRecordDao;
import com.citc.nce.tenant.robot.entity.MsgQuantityStatisticsDo;
import com.citc.nce.tenant.robot.entity.MsgRecordDo;
import com.citc.nce.utils.DateUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.*;

import static com.citc.nce.dataStatistics.constant.StatisticsConstants.*;

/**
 * @author ping chen
 */
@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(rollbackFor = Exception.class)
public class DeveloperSendStatisticsServiceImpl extends ServiceImpl<DeveloperSendStatisticsMapper, DeveloperSendStatisticsDo> implements DeveloperSendStatisticsService {

    private final DeveloperSendMapper smsDeveloperSendMapper;
    private final DeveloperSendStatisticsMapper developerSendStatisticsMapper;
    private final MsgRecordDao msgRecordDao;

    /**
     * 管理平台-开发者服务分析-总的统计
     *
     * @return
     */
    @Override
    public DeveloperAllStatisticsVo allStatistics() {
        DateTime yesterday = cn.hutool.core.date.DateUtil.yesterday();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String yesterdayStr = sdf.format(yesterday);
        DeveloperAllStatisticsVo smsDeveloperAllStatisticsVo = new DeveloperAllStatisticsVo();
        Set<String> customerUserIdList = new HashSet<>();
        //查询短信开发者列表
        int applicationCount = 0;//应用数量
        List<String> customerSms = developerSendStatisticsMapper.selectSmsCustomer();
        if (CollectionUtils.isNotEmpty(customerSms)) {
            customerUserIdList.addAll(customerSms);
            applicationCount += customerUserIdList.size();
        }

        List<String> customerVideo = developerSendStatisticsMapper.selectVideoCustomer();
        if (CollectionUtils.isNotEmpty(customerVideo)) {
            customerUserIdList.addAll(customerVideo);
            applicationCount += customerUserIdList.size();
        }

        //查询全部应用
        List<String> customer5g = developerSendStatisticsMapper.select5gCustomer();
        Long applicationCountFor5G = developerSendStatisticsMapper.select5GApplicationCount();
        if (CollectionUtils.isNotEmpty(customer5g)) {
            customerUserIdList.addAll(customer5g);
        }
        applicationCount += applicationCountFor5G;


        smsDeveloperAllStatisticsVo.setDeveloperCount(customerUserIdList.size());
        smsDeveloperAllStatisticsVo.setUseCount(applicationCount);
        smsDeveloperAllStatisticsVo.setCallCount(developerSendStatisticsMapper.selectSendCustomerYesterday(yesterdayStr));
        return smsDeveloperAllStatisticsVo;
    }

    /**
     * 管理平台-开发者服务分析-调用趋势
     *
     * @param developerStatisticsTimeVo
     * @return
     */
    @Override
    public List<DeveloperCllTrendVo> cllTrend(DeveloperStatisticsTimeVo developerStatisticsTimeVo) {
        Date startTime = DateUtils.obtainDayTime(START, DateUtils.obtainDate(developerStatisticsTimeVo.getStartTime()));
        Date endTime = DateUtils.obtainDayTime(END, DateUtils.obtainDate(developerStatisticsTimeVo.getEndTime()));
        String timeType = DateUtils.obtainTimeType(startTime, endTime);
        List<String> times = HOUR.equals(timeType) ? DateUtils.obtainAllHourList() : DateUtils.obtainDayList(startTime, endTime);
        Map<String, DeveloperCllTrendVo> developerCllTrendVoMap = initResultByTimeForCllTrend(times);
        List<DeveloperCllTrendVo> developerCllTrendVoList;
        if (StringUtils.equals(developerStatisticsTimeVo.getStartTime(), developerStatisticsTimeVo.getEndTime())) {
            //按小时统计
            developerCllTrendVoList = smsDeveloperSendMapper.cllTrend(developerStatisticsTimeVo, null, developerStatisticsTimeVo.getType());
        } else {
            //按天统计
            developerCllTrendVoList = developerSendStatisticsMapper.cllTrend(developerStatisticsTimeVo, null, developerStatisticsTimeVo.getType());
        }
        if (CollectionUtils.isNotEmpty(developerCllTrendVoList)) {
            for (DeveloperCllTrendVo item : developerCllTrendVoList) {
                if (developerCllTrendVoMap.containsKey(item.getTime())) {
                    developerCllTrendVoMap.put(item.getTime(), item);
                }
            }
        }
        return new ArrayList<>(developerCllTrendVoMap.values());
    }

    private Map<String, DeveloperCllTrendVo> initResultByTimeForCllTrend(List<String> times) {
        Map<String, DeveloperCllTrendVo> developerCllTrendVoMap = new TreeMap<>();
        DeveloperCllTrendVo developerCllTrendVo;
        for (String time : times) {
            developerCllTrendVo = new DeveloperCllTrendVo();
            developerCllTrendVo.setCallCount(0);
            developerCllTrendVo.setFailCount(0);
            developerCllTrendVo.setSuccessCount(0);
            developerCllTrendVo.setTime(time);
            developerCllTrendVoMap.put(time, developerCllTrendVo);
        }
        return developerCllTrendVoMap;
    }

    /**
     * 开发者统计分析-昨日概览
     *
     * @return
     */
    @Override
    public DeveloperYesterdayOverviewVo yesterdayOverview(Integer type) {
        String time = DateUtil.getYesterdayTime();
        DeveloperYesterdayOverviewVo developerYesterdayOverviewVo = developerSendStatisticsMapper.yesterdayOverview(time, SessionContextUtil.getUser().getUserId(), type);

        Date yesterdayStart = DateUtils.obtainDayTime(START,DateUtils.obtainDate(DateUtils.getYesterday(new Date())));
        Date yesterdayEnd = DateUtils.obtainDayTime(END,DateUtils.obtainDate(DateUtils.getYesterday(new Date())));
        DeveloperSendAnalysisVo sendAnalysisVo = getDeveloperSendInfo(yesterdayStart,yesterdayEnd,SessionContextUtil.getUser().getUserId(), type,null);
        developerYesterdayOverviewVo.setSendSuccessCount(sendAnalysisVo.getSuccessCount());
        developerYesterdayOverviewVo.setSendFailCount(sendAnalysisVo.getFailCount());
        developerYesterdayOverviewVo.setSendUnknownCount(sendAnalysisVo.getUnknownCount());
        developerYesterdayOverviewVo.setSendDisplayedCount(sendAnalysisVo.getDisplayedCount());
        BigDecimal successCount = new BigDecimal(developerYesterdayOverviewVo.getSuccessCount());
        BigDecimal callCount = new BigDecimal(developerYesterdayOverviewVo.getCallCount());
        developerYesterdayOverviewVo.setSuccessRate(callCount.intValue() == 0 ? BigDecimal.valueOf(0.00) : successCount.divide(callCount, 4, RoundingMode.HALF_UP).multiply(new BigDecimal("100")));
        return developerYesterdayOverviewVo;
    }

    private DeveloperSendAnalysisVo getDeveloperSendInfo(Date startDate,Date endDate, String customerId, Integer type,List<String> messageIds) {
        DeveloperSendAnalysisVo  send = new DeveloperSendAnalysisVo();
        QueryWrapper<MsgRecordDo> msgRecordDoQueryWrapper = new QueryWrapper<>();
        msgRecordDoQueryWrapper.select("IFNULL(SUM(send_result = 1 or send_result = 6),0) AS sendSuccessCount, " +
                "IFNULL(SUM(send_result = 2),0) AS sendFailCount, " +
                "IFNULL(SUM(send_result = 0),0) AS sendUnknownCount, " +
                "IFNULL(SUM(send_result = 6),0) AS sendDisplayedCount ");
        msgRecordDoQueryWrapper.eq("account_type",type);
        msgRecordDoQueryWrapper.eq("creator",customerId);
        msgRecordDoQueryWrapper.in("message_resource",Arrays.asList(MSG_SEND_DEVELOPER));
        if (endDate != null) {//结束时间
            msgRecordDoQueryWrapper.le("send_time", endDate);
        }
        if (startDate != null) {//结束时间
            msgRecordDoQueryWrapper.ge("send_time",startDate);
        }
        if(CollectionUtils.isNotEmpty(messageIds)){
            msgRecordDoQueryWrapper.in("message_id",messageIds);
        }
        List<Map<String, Object>> maps = msgRecordDao.selectMaps(msgRecordDoQueryWrapper);
        if(CollectionUtils.isNotEmpty(maps)){
            for(Map<String, Object> item : maps){
                if(item != null){
                    send.setSuccessCount(Integer.valueOf(item.get("sendSuccessCount")+""));
                    send.setFailCount(Integer.valueOf(item.get("sendFailCount")+""));
                    send.setUnknownCount(Integer.valueOf(item.get("sendUnknownCount")+""));
                    send.setDisplayedCount(Integer.valueOf(item.get("sendDisplayedCount")+""));
                }
            }
        }
        return send;

    }

    /**
     * 开发者统计分析-调用分析
     *
     * @param developerStatisticsTimeVo
     * @return
     */
    @Override
    public DeveloperCllAnalysisVo callAnalysis(DeveloperStatisticsTimeVo developerStatisticsTimeVo) {
        return developerSendStatisticsMapper.callAnalysis(SessionContextUtil.getUser().getUserId(), developerStatisticsTimeVo, developerStatisticsTimeVo.getType());
    }

    /**
     * 开发者统计分析-发送分析
     *
     * @param developerStatisticsTimeVo
     * @return
     */
    @Override
    public DeveloperSendAnalysisVo sendAnalysis(DeveloperStatisticsTimeVo developerStatisticsTimeVo) {
        Date start = DateUtils.obtainDayTime(START,DateUtils.obtainDate(developerStatisticsTimeVo.getStartTime()));
        Date end = DateUtils.obtainDayTime(END,DateUtils.obtainDate(developerStatisticsTimeVo.getEndTime()));
        List<String> messageIdList = new ArrayList<>();
        if(Strings.isNotEmpty(developerStatisticsTimeVo.getApplicationUniqueId())){
           //1、查询消息ID
            messageIdList = getMessageIdsForDeveloper(start,end,developerStatisticsTimeVo.getApplicationUniqueId());
            if(CollectionUtils.isEmpty(messageIdList)){
                DeveloperSendAnalysisVo developerSendAnalysisVo = new DeveloperSendAnalysisVo();
                developerSendAnalysisVo.setDisplayedCount(0);
                developerSendAnalysisVo.setFailCount(0);
                developerSendAnalysisVo.setSuccessCount(0);
                developerSendAnalysisVo.setUnknownCount(0);
                return developerSendAnalysisVo;
            }
        }
        return getDeveloperSendInfo(start,end,SessionContextUtil.getUser().getUserId(), developerStatisticsTimeVo.getType(),messageIdList);
    }

    private List<String> getMessageIdsForDeveloper(Date startDate, Date endDate, String applicationUniqueId) {
        List<String> messageIds = new ArrayList<>();
        QueryWrapper<DeveloperSendDo> developerSendDoQueryWrapper = new QueryWrapper<>();
        developerSendDoQueryWrapper.select("sms_id as smsId ");
        developerSendDoQueryWrapper.eq("application_unique_id",applicationUniqueId);
        if (endDate != null) {//结束时间
            developerSendDoQueryWrapper.le("call_time", endDate);
        }
        if (startDate != null) {//结束时间
            developerSendDoQueryWrapper.ge("call_time",startDate);
        }
        List<Map<String, Object>> maps = smsDeveloperSendMapper.selectMaps(developerSendDoQueryWrapper);
        if(CollectionUtils.isNotEmpty(maps)){
            for(Map<String, Object> item : maps){
                if(item != null){
                    messageIds.add(item.get("smsId")+"");
                }
            }
        }
        return messageIds;

    }


    /**
     * 开发者统计分析-调用量趋势
     *
     * @param developerStatisticsTimeVo
     * @return
     */
    @Override
    public List<DeveloperCllTrendVo> cllTrendByUser(DeveloperStatisticsTimeVo developerStatisticsTimeVo) {
        Date startTime = DateUtils.obtainDayTime(START, DateUtils.obtainDate(developerStatisticsTimeVo.getStartTime()));
        Date endTime = DateUtils.obtainDayTime(END, DateUtils.obtainDate(developerStatisticsTimeVo.getEndTime()));
        String timeType = DateUtils.obtainTimeType(startTime, endTime);
        List<String> times = HOUR.equals(timeType) ? DateUtils.obtainAllHourList() : DateUtils.obtainDayList(startTime, endTime);
        Map<String, DeveloperCllTrendVo> developerCllTrendVoMap = initResultByTimeForCllTrend(times);
        List<DeveloperCllTrendVo> smsDeveloperCllTrendVoList;
        if (StringUtils.equals(developerStatisticsTimeVo.getStartTime(), developerStatisticsTimeVo.getEndTime())) {
            //按小时统计
            smsDeveloperCllTrendVoList = smsDeveloperSendMapper.cllTrend(developerStatisticsTimeVo, SessionContextUtil.getUser().getUserId(), developerStatisticsTimeVo.getType());
        } else {
            smsDeveloperCllTrendVoList = developerSendStatisticsMapper.cllTrend(developerStatisticsTimeVo, SessionContextUtil.getUser().getUserId(), developerStatisticsTimeVo.getType());
        }
        if (CollectionUtils.isNotEmpty(smsDeveloperCllTrendVoList)) {
            for (DeveloperCllTrendVo item : smsDeveloperCllTrendVoList) {
                if (developerCllTrendVoMap.containsKey(item.getTime())) {
                    developerCllTrendVoMap.put(item.getTime(), item);
                }
            }
        }
        return new ArrayList<>(developerCllTrendVoMap.values());
    }

    /**
     * 开发者统计分析-发送量趋势
     *
     * @param developerStatisticsTimeVo
     * @return
     */
    @Override
    public List<DeveloperSendTrendByUserVo> sendTrendByUser(DeveloperStatisticsTimeVo developerStatisticsTimeVo) {
        Date startTime = DateUtils.obtainDayTime(START, DateUtils.obtainDate(developerStatisticsTimeVo.getStartTime()));
        Date endTime = DateUtils.obtainDayTime(END, DateUtils.obtainDate(developerStatisticsTimeVo.getEndTime()));
        String timeType = DateUtils.obtainTimeType(startTime, endTime);
        List<String> times = HOUR.equals(timeType) ? DateUtils.obtainAllHourList() : DateUtils.obtainDayList(startTime, endTime);
        Map<String, DeveloperSendTrendByUserVo> developerSendTrendByUserVoMap = initResultByTimeForSendTrendByUser(times);
        List<DeveloperSendTrendByUserVo> smsDeveloperSendTrendByUserVoList;
        if (StringUtils.equals(developerStatisticsTimeVo.getStartTime(), developerStatisticsTimeVo.getEndTime())) {
            //按小时统计
            smsDeveloperSendTrendByUserVoList = smsDeveloperSendMapper.sendTrendByUser(developerStatisticsTimeVo, SessionContextUtil.getUser().getUserId(), developerStatisticsTimeVo.getType());
        } else {
            //按天统计
            smsDeveloperSendTrendByUserVoList = developerSendStatisticsMapper.sendTrendByUser(developerStatisticsTimeVo, SessionContextUtil.getUser().getUserId(), developerStatisticsTimeVo.getType());
        }
        if (CollectionUtils.isNotEmpty(smsDeveloperSendTrendByUserVoList)) {
            for (DeveloperSendTrendByUserVo item : smsDeveloperSendTrendByUserVoList) {
                if (developerSendTrendByUserVoMap.containsKey(item.getTime())) {
                    developerSendTrendByUserVoMap.put(item.getTime(), item);
                }
            }
        }
        return new ArrayList<>(developerSendTrendByUserVoMap.values());
    }

    private Map<String, DeveloperSendTrendByUserVo> initResultByTimeForSendTrendByUser(List<String> times) {
        Map<String, DeveloperSendTrendByUserVo> developerSendTrendByUserVoMap = new TreeMap<>();
        DeveloperSendTrendByUserVo developerSendTrendByUserVo;
        for (String time : times) {
            developerSendTrendByUserVo = new DeveloperSendTrendByUserVo();
            developerSendTrendByUserVo.setDisplayedCount(0);
            developerSendTrendByUserVo.setFailCount(0);
            developerSendTrendByUserVo.setSuccessCount(0);
            developerSendTrendByUserVo.setUnknownCount(0);
            developerSendTrendByUserVo.setTime(time);
            developerSendTrendByUserVoMap.put(time, developerSendTrendByUserVo);
        }
        return developerSendTrendByUserVoMap;
    }

    @Override
    public void saveData(List<DeveloperSendStatisticsDo> developerSendStatisticsDoArrayList) {
        this.saveBatch(developerSendStatisticsDoArrayList);
    }

    /**
     * 开发者统计分析-应用排行榜
     *
     * @param developerStatisticsTimeVo
     * @return
     */
    @Override
    public List<DeveloperApplication5gRankingVo> applicationRanking(DeveloperStatisticsTimeVo developerStatisticsTimeVo) {
        return developerSendStatisticsMapper.applicationRanking(developerStatisticsTimeVo, SessionContextUtil.getUser().getUserId());
    }

    @Override
    public String resetStatistics(Date startDate, Date endDate) {
//        startDate = DateUtils.obtainDayTime("start",DateUtils.obtainDate("2024-03-15"));
//        endDate = DateUtils.obtainDayTime("end",DateUtils.obtainDate("2024-03-15"));
        log.info("定时清洗统计开发者统计数据开始");
        //1、查询基础表数据
        List<DeveloperStatisticsItem> developerStatisticsItems = getDeveloperStatisticsForDate(startDate, endDate);
        if (CollectionUtils.isNotEmpty(developerStatisticsItems)) {
            List<DeveloperSendStatisticsDo> saveList = new ArrayList<>();
            List<DeveloperSendStatisticsDo> updateList = new ArrayList<>();
            DeveloperSendStatisticsDo developerSendStatisticsDo;
            //2、查询已统计的数据
            Map<String, DeveloperSendStatisticsDo> developerSendStatisticsDoOldMap = getDeveloperStatisticsForOld(startDate, endDate);
            for (DeveloperStatisticsItem item : developerStatisticsItems) {
                String key = "" + item.getApplicationUniqueId() + item.getCspId() + item.getCustomerId() + item.getAccountType() + item.getAccountId() + item.getCallTime();
                if (developerSendStatisticsDoOldMap.containsKey(key)) {
                    developerSendStatisticsDo = developerSendStatisticsDoOldMap.get(key);
                    developerSendStatisticsDo.setCallCount(item.getCallCount());
                    developerSendStatisticsDo.setSuccessCount(item.getSuccessCount());
                    developerSendStatisticsDo.setFailCount(item.getFailCount());
                    developerSendStatisticsDo.setSendSuccessCount(item.getSendSuccessCount());
                    developerSendStatisticsDo.setSendFailCount(item.getSendFailCount());
                    developerSendStatisticsDo.setSendUnknownCount(item.getSendUnknownCount());
                    developerSendStatisticsDo.setSendDisplayedCount(item.getSendDisplayedCount());
                    developerSendStatisticsDo.setUpdateTime(new Date());
                    updateList.add(developerSendStatisticsDo);
                } else {
                    developerSendStatisticsDo = new DeveloperSendStatisticsDo();
                    developerSendStatisticsDo.setApplicationUniqueId(item.getApplicationUniqueId());
                    developerSendStatisticsDo.setCspId(item.getCspId());
                    developerSendStatisticsDo.setCustomerId(item.getCustomerId());
                    developerSendStatisticsDo.setAccountType(item.getAccountType());
                    developerSendStatisticsDo.setAccountId(item.getAccountId());
                    developerSendStatisticsDo.setTime(DateUtils.obtainDate(item.getCallTime()));
                    developerSendStatisticsDo.setCallCount(item.getCallCount());
                    developerSendStatisticsDo.setCallCount(item.getCallCount());
                    developerSendStatisticsDo.setSuccessCount(item.getSuccessCount());
                    developerSendStatisticsDo.setFailCount(item.getFailCount());
                    developerSendStatisticsDo.setSendSuccessCount(item.getSendSuccessCount());
                    developerSendStatisticsDo.setSendFailCount(item.getSendFailCount());
                    developerSendStatisticsDo.setSendUnknownCount(item.getSendUnknownCount());
                    developerSendStatisticsDo.setSendDisplayedCount(item.getSendDisplayedCount());
                    developerSendStatisticsDo.setCreator(item.getCustomerId());
                    developerSendStatisticsDo.setCreateTime(new Date());
                    saveList.add(developerSendStatisticsDo);
                }
            }
            if (CollectionUtils.isNotEmpty(saveList)) {
                developerSendStatisticsMapper.insertBatch(saveList);
            }
            if (CollectionUtils.isNotEmpty(updateList)) {
                developerSendStatisticsMapper.updateBatch(updateList);
            }
        }
        return "END";
    }

    private Map<String, DeveloperSendStatisticsDo> getDeveloperStatisticsForOld(Date startDate, Date endDate) {
        Map<String, DeveloperSendStatisticsDo> developerSendStatisticsDoOldMap = new HashMap<>();
        LambdaQueryWrapper<DeveloperSendStatisticsDo> wrapper = new LambdaQueryWrapper<>();
        if (startDate != null) {
            wrapper.ge(DeveloperSendStatisticsDo::getTime, startDate);
        }
        if (startDate != null) {
            wrapper.le(DeveloperSendStatisticsDo::getTime, endDate);
        }
        List<DeveloperSendStatisticsDo> developerSendStatisticsDos = developerSendStatisticsMapper.selectList(wrapper);
        if (CollectionUtils.isNotEmpty(developerSendStatisticsDos)) {
            String key;
            for (DeveloperSendStatisticsDo item : developerSendStatisticsDos) {
                key = "" + item.getApplicationUniqueId() + item.getCspId() + item.getCustomerId() + item.getAccountType() + item.getAccountId() + DateUtils.obtainDateStr(item.getTime(), "yyyy-MM-dd");
                developerSendStatisticsDoOldMap.put(key, item);
            }
        }
        return developerSendStatisticsDoOldMap;
    }

    //统计基础数据

    /**
     * 调用次数：该用户下所有5G消息应用在昨日被调用后响应200状态码的次数合计（即只要接口被调通了就计算一次，不管业务处理是否成功）。
     * 成功次数：指开发者服务接口被调用后，通过解析入参，组装报文后调用网关，成功进行消息下发的次数（不管网关回执结果），比如接口响应码为200，定义接口规范resultCode=0000是下发网关成功，9999是鉴权失败，4000是接口被禁用等等。其中的0000就是此处需要统计的成功次数（业务上的成功），而9999、4000则算做失败次数（业务上的失败，而不是接口调用上的失败）。
     * 成功率：成功次数/调用次数×100%，保留两位小数。
     * 发送成功：通过调用开发者服务进行消息发送，网关回执消息发送成功的次数合计
     * 发送失败：通过调用开发者服务进行消息发送，网关回执消息发送失败的次数合计
     * 未知：通过调用开发者服务进行消息发送，未收到网关回执的次数合计
     * 已阅：通过调用开发者服务进行消息发送，网关回执消息已阅的次数合计
     *
     * @param startDate
     * @return
     */
    private List<DeveloperStatisticsItem> getDeveloperStatisticsForDate(Date startDate, Date endDate) {
        List<DeveloperStatisticsItem> developerStatisticsItems = new ArrayList<>();
        QueryWrapper<DeveloperSendDo> developerSendDoQueryWrapper = new QueryWrapper<>();
        developerSendDoQueryWrapper.select("DATE_FORMAT(call_time,'%Y-%m-%d') AS callTime, " +
                "application_unique_id AS applicationUniqueId, " +
                "csp_id AS cspId, " +
                "customer_id AS customerId, " +
                "account_type AS accountType, " +
                "account_id AS accountId, " +
//                "callback_platform_result AS callbackPlatformResult, "+
                "count(call_result) AS callCount, " +
                "IFNULL(SUM(call_result = 0),0) AS successCount, " +
                "IFNULL(SUM(call_result = 1),0) AS failCount, " +
                "IFNULL(SUM(send_platform_result = 0),0) AS sendSuccessCount, " +
                "IFNULL(SUM(send_platform_result = 1),0) AS sendFailCount, " +
                "IFNULL(SUM(callback_platform_result = 2),0) AS sendUnknownCount, " +
                "IFNULL(SUM(send_mq_result = 0 and send_platform_result is null),0) AS sendDisplayedCount ");
        if (startDate != null) {//结束时间
            developerSendDoQueryWrapper.ge("call_time", startDate);
        }
        if (endDate != null) {//结束时间
            developerSendDoQueryWrapper.le("call_time", endDate);
        }
        developerSendDoQueryWrapper.isNotNull("csp_id");
        developerSendDoQueryWrapper.isNotNull("customer_id");
        developerSendDoQueryWrapper.isNotNull("account_type");
        developerSendDoQueryWrapper.groupBy("callTime,applicationUniqueId,cspId,customerId,accountType,accountId");
        List<Map<String, Object>> maps = smsDeveloperSendMapper.selectMaps(developerSendDoQueryWrapper);
        if (CollectionUtils.isNotEmpty(maps)) {
            DeveloperStatisticsItem developerStatisticsItem;
            for (Map<String, Object> item : maps) {
                developerStatisticsItem = new DeveloperStatisticsItem();
                developerStatisticsItem.setCallTime(item.get("callTime") + "");
                developerStatisticsItem.setApplicationUniqueId(item.get("applicationUniqueId") + "");
                developerStatisticsItem.setCspId(item.get("cspId") + "");
                developerStatisticsItem.setCustomerId(item.get("customerId") + "");
                developerStatisticsItem.setAccountType(Integer.valueOf(item.get("accountType") + ""));
                developerStatisticsItem.setAccountId(item.get("accountId") + "");
                developerStatisticsItem.setCallCount(Long.valueOf(item.get("callCount") + ""));
                developerStatisticsItem.setSuccessCount(Long.parseLong(item.get("successCount") + ""));
                developerStatisticsItem.setFailCount(Long.valueOf(item.get("failCount") + ""));
                developerStatisticsItem.setSendSuccessCount(Long.parseLong(item.get("sendSuccessCount") + "") + Long.parseLong(item.get("sendDisplayedCount") + ""));
                developerStatisticsItem.setSendFailCount(Long.valueOf(item.get("sendFailCount") + ""));
                developerStatisticsItem.setSendUnknownCount(Long.valueOf(item.get("sendUnknownCount") + ""));
                developerStatisticsItem.setSendDisplayedCount(Long.valueOf(item.get("sendDisplayedCount") + ""));
                developerStatisticsItems.add(developerStatisticsItem);
            }
        }
        return developerStatisticsItems;
    }
}
