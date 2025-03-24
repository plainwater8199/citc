package com.citc.nce.dataStatistics.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.citc.nce.auth.csp.account.Csp5GSmsAccountApi;
import com.citc.nce.auth.csp.dict.CspDictApi;
import com.citc.nce.auth.csp.dict.vo.CspDictReq;
import com.citc.nce.auth.csp.dict.vo.CspDictResp;
import com.citc.nce.auth.csp.sms.account.CspShortSmsAccountApi;
import com.citc.nce.auth.csp.videoSms.account.CspVideoSmsAccountApi;
import com.citc.nce.auth.csp.videoSms.account.vo.CspVideoSmsAccountQueryAccountIdReq;
import com.citc.nce.authcenter.csp.CspCustomerApi;
import com.citc.nce.authcenter.csp.vo.resp.CspInfoResp;
import com.citc.nce.common.core.pojo.PageResult;
import com.citc.nce.common.util.SessionContextUtil;
import com.citc.nce.dataStatistics.dto.AccountStatisticsInfo;
import com.citc.nce.dataStatistics.service.MsgDataStatisticsService;
import com.citc.nce.dataStatistics.vo.msg.*;
import com.citc.nce.dataStatistics.vo.msg.req.*;
import com.citc.nce.dataStatistics.vo.msg.resp.*;
import com.citc.nce.dataStatistics.vo.resp.ChannelInfo;
import com.citc.nce.common.core.enums.MsgTypeEnum;
import com.citc.nce.robot.api.FastGroupMessageApi;
import com.citc.nce.robot.res.FastGroupMessageItem;
import com.citc.nce.robot.util.DateUtil;
import com.citc.nce.tenant.robot.dao.*;
import com.citc.nce.tenant.robot.entity.*;
import com.citc.nce.utils.DateUtils;
import com.google.common.base.Strings;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;
import java.util.List;

import static com.citc.nce.dataStatistics.constant.StatisticsConstants.*;
@Service
@Slf4j
public class MsgDataStatisticsServiceImpl implements MsgDataStatisticsService {

    @Resource
    private MsgQuantityStatisticDao msgQuantityStatisticDao;
    @Resource
    private CspVideoSmsAccountApi cspVideoSmsAccountApi;
    @Resource
    private CspShortSmsAccountApi cspShortSmsAccountApi;
    @Resource
    private Csp5GSmsAccountApi csp5GSmsAccountApi;
    @Resource
    private CspDictApi cspDictApi;

    @Resource
    private CspCustomerApi cspCustomerApi;
    @Resource
    private RobotGroupSendPlans1Dao robotGroupSendPlansDao;

    @Resource
    private RobotGroupSendPlansDetail1Dao robotGroupSendPlansDetailDao;

    @Resource
    private RobotClickResult1Dao robotClickResult1Dao;

    @Resource
    private FastGroupMessageApi fastGroupMessageApi;


    @Override
    public YesterdayOverviewResp yesterdayOverviewForMedia() {
        return yesterdayOverviewForAccountType(MSG_MEDIA);
    }

    @Override
    public YesterdayOverviewResp yesterdayOverviewFor5G() {
        return yesterdayOverviewForAccountType(MSG_5G);
    }

    @Override
    public YesterdayOverviewResp yesterdayOverviewForShort() {
        return yesterdayOverviewForAccountType(MSG_SHORT);
    }

    @Override
    public ActiveTrendsResp activeTrends(QueryReq req) {
        ActiveTrendsResp activeTrendsResp = new ActiveTrendsResp();
        Date startTime = DateUtils.obtainDayTime(START,DateUtils.obtainDate(req.getStartTime()));
        Date endTime = DateUtils.obtainDayTime(END,DateUtils.obtainDate(req.getEndTime()));
        String timeType = DateUtils.obtainTimeType(startTime, endTime);
        List<String> times = HOUR.equals(timeType) ?  DateUtils.obtainAllHourList() : DateUtils.obtainDayList(startTime, endTime);
        List<Integer> messageResource = req.getMessageResource() != null ? req.getMessageResource() : Arrays.asList(MSG_SEND_PLAN);

        //获取用户列表
        List<String> customerIds = queryCustomerList(SessionContextUtil.getUser().getUserId());
        //获取活跃用户
        Map<String,Long> activeUserSumForTrendMap = (!isAdmin() && CollectionUtils.isEmpty(customerIds)) ? new HashMap<>() : obtainActiveUserSumForTrends(startTime,endTime,timeType,customerIds,messageResource);
        //活跃发送趋势
        Map<String,ActiveTrendItem> activeTrendItemMap = (!isAdmin() && CollectionUtils.isEmpty(customerIds)) ? new HashMap<>() : obtainSendTrends(startTime,endTime,timeType,customerIds,messageResource);

        List<ActiveTrendItem> activeTrendItems = new ArrayList<>();
        ActiveTrendItem activeTrendItem;
        for(String item : times){
            activeTrendItem =new ActiveTrendItem();
            activeTrendItem.setActiveTime(item);
            if(activeTrendItemMap.containsKey(item)){
                BeanUtils.copyProperties(activeTrendItemMap.get(item),activeTrendItem);
            }else{
                activeTrendItem.setActiveAccountSumFor5G(0L);
                activeTrendItem.setActiveAccountSumForMedia(0L);
                activeTrendItem.setActiveAccountSumForShort(0L);
            }
            activeTrendItem.setActiveCustomerSum(activeUserSumForTrendMap.getOrDefault(item, 0L));
            activeTrendItems.add(activeTrendItem);
        }
        activeTrendsResp.setActiveTrendItems(activeTrendItems);
        return activeTrendsResp;
    }

    private Map<String, ActiveTrendItem> obtainSendTrends(Date startTime, Date endTime, String timeType, List<String> customerIds,List<Integer> messageResource) {

        QueryWrapper<MsgQuantityStatisticsDo> msgQuantityStatisticsDoQueryWrapper = new QueryWrapper<>();
        if(HOUR.equals(timeType)){
            msgQuantityStatisticsDoQueryWrapper.select("DATE_FORMAT(send_time,'%H:%i') AS activeTime, " +
                    "count(Distinct account_id) AS accountCount, "+
                    "account_type AS accountType ");
        }else{
            msgQuantityStatisticsDoQueryWrapper.select("DATE_FORMAT(send_time,'%Y-%m-%d') AS activeTime," +
                    "count(Distinct account_id) AS accountCount, "+
                    "account_type AS accountType ");
        }
        msgQuantityStatisticsDoQueryWrapper.in("message_resource",messageResource);
        msgQuantityStatisticsDoQueryWrapper.ge("send_time",startTime);
        if (endTime != null) {//结束时间
            msgQuantityStatisticsDoQueryWrapper.le("send_time", endTime);
        }
        if(CollectionUtils.isNotEmpty(customerIds)){
            msgQuantityStatisticsDoQueryWrapper.in("creator",customerIds);
        }
        msgQuantityStatisticsDoQueryWrapper.groupBy("activeTime,accountType");
        List<Map<String, Object>> maps = msgQuantityStatisticDao.selectMaps(msgQuantityStatisticsDoQueryWrapper);

        return obtainActiveTrendStatistic(maps);
    }

    private Map<String, ActiveTrendItem> obtainActiveTrendStatistic(List<Map<String, Object>> maps) {
        Map<String, ActiveTrendItem> activeTrendItemMap = new HashMap<>();
        if(CollectionUtils.isNotEmpty(maps)){
            String activeTime;
            ActiveTrendItem activeTrendItem;
            for(Map<String, Object> item : maps){
                if(item != null){
                    activeTime = item.get("activeTime")+"";
                    if(activeTrendItemMap.containsKey(activeTime)){
                        activeTrendItem = activeTrendItemMap.get(activeTime);
                        if(MSG_5G.equals(item.get("accountType")+"")){
                            activeTrendItem.setActiveAccountSumFor5G(Long.valueOf(item.get("accountCount")+""));
                        } else if (MSG_MEDIA.equals(item.get("accountType")+"")) {
                            activeTrendItem.setActiveAccountSumForMedia(Long.valueOf(item.get("accountCount")+""));
                        }else if(MSG_SHORT.equals(item.get("accountType")+"")){
                            activeTrendItem.setActiveAccountSumForShort(Long.valueOf(item.get("accountCount")+""));
                        }
                    }else{
                        activeTrendItem = new ActiveTrendItem();
                        activeTrendItem.setActiveTime(activeTime);
                        if(MSG_5G.equals(item.get("accountType")+"")){
                            activeTrendItem.setActiveAccountSumFor5G(Long.valueOf(item.get("accountCount")+""));
                            activeTrendItem.setActiveAccountSumForMedia(0L);
                            activeTrendItem.setActiveAccountSumForShort(0L);
                        } else if (MSG_MEDIA.equals(item.get("accountType")+"")) {
                            activeTrendItem.setActiveAccountSumFor5G(0L);
                            activeTrendItem.setActiveAccountSumForShort(0L);
                            activeTrendItem.setActiveAccountSumForMedia(Long.valueOf(item.get("accountCount")+""));
                        }else if(MSG_SHORT.equals(item.get("accountType")+"")){
                            activeTrendItem.setActiveAccountSumForMedia(0L);
                            activeTrendItem.setActiveAccountSumFor5G(0L);
                            activeTrendItem.setActiveAccountSumForShort(Long.valueOf(item.get("accountCount")+""));

                        }
                        activeTrendItemMap.put(activeTime,activeTrendItem);
                    }
                }
            }
        }
        return activeTrendItemMap;
    }

    private Map<String, Long> obtainActiveUserSumForTrends(Date startTime, Date endTime, String timeType, List<String> customerIds,List<Integer> messageResource) {
        Map<String,Long> obtainActiveUserSumMap = new HashMap<>();
        QueryWrapper<MsgQuantityStatisticsDo> msgQuantityStatisticsDoQueryWrapper = new QueryWrapper<>();
        if(HOUR.equals(timeType)){
            msgQuantityStatisticsDoQueryWrapper.select("DATE_FORMAT(send_time,'%H:%i') AS activeTime," +
                    "            count(Distinct creator) AS sum");
        }else{
            msgQuantityStatisticsDoQueryWrapper.select("DATE_FORMAT(send_time,'%Y-%m-%d') AS activeTime," +
                    "            count(Distinct creator) AS sum");
        }
        msgQuantityStatisticsDoQueryWrapper.in("message_resource",messageResource);
        msgQuantityStatisticsDoQueryWrapper.ge("send_time",startTime);
        if (endTime != null) {//结束时间
            msgQuantityStatisticsDoQueryWrapper.le("send_time", endTime);
        }
        if(CollectionUtils.isNotEmpty(customerIds)){
            msgQuantityStatisticsDoQueryWrapper.in("creator",customerIds);
        }
        msgQuantityStatisticsDoQueryWrapper.groupBy("activeTime");
        List<Map<String, Object>> maps = msgQuantityStatisticDao.selectMaps(msgQuantityStatisticsDoQueryWrapper);
        if(CollectionUtils.isNotEmpty(maps)){
            for(Map<String, Object> item : maps){
                if(item != null){
                    obtainActiveUserSumMap.put(item.get("activeTime")+"",Long.valueOf(item.get("sum")+""));
                }
            }
        }



        return obtainActiveUserSumMap;
    }

    @Override
    public SendTrendsResp sendTrendsFor5g(QueryReq req) {
        return sendTrendsForType(MSG_5G,req);
    }


    @Override
    public SendTrendsResp sendTrendsForMedia(QueryReq req) {
        return sendTrendsForType(MSG_MEDIA,req);
    }

    @Override
    public SendTrendsResp sendTrendsForShort(QueryReq req) {
        return sendTrendsForType(MSG_SHORT,req);
    }

    @Override
    public SendStatusAnalysisResp sendStatusAnalysis(QueryReq req) {
        //初始化结果，全部为0
        SendStatusAnalysisResp resp = initResult();

        Date startDate = DateUtils.obtainTime(DAY, START, DateUtils.obtainDate(req.getStartTime()));
        Date endDate = DateUtils.obtainTime(DAY, END, DateUtils.obtainDate(req.getEndTime()));
        List<Integer> messageResource = CollectionUtils.isNotEmpty(req.getMessageResource()) ? req.getMessageResource() : Collections.singletonList(MSG_SEND_PLAN);
        boolean admin = isAdmin();

        //1、根据状态查询
        querySendStatusAnalysisByStatus(resp,admin,startDate,endDate,req.getOperatorCode(),messageResource,req.getPlanId());
        //1、根据类别--机器人。群发
        querySendStatusAnalysisByType(resp,admin,startDate,endDate,req.getOperatorCode(),messageResource);
        return resp;
    }

    private void querySendStatusAnalysisByType(SendStatusAnalysisResp resp, boolean admin, Date startDate, Date endDate, String operatorCode, List<Integer> messageResource) {

        //查询通过发送计划的消息
        querySendStatusAnalysisByMsgResouce(resp,admin,startDate,endDate,operatorCode,messageResource);
        //查询通过机器人发送的消息
        querySendStatusAnalysisByMsgResouce(resp,admin,startDate,endDate,operatorCode, Collections.singletonList(MSG_SEND_ROBOT));


    }

    private void querySendStatusAnalysisByMsgResouce(SendStatusAnalysisResp resp, boolean admin, Date startDate, Date endDate, String operatorCode, List<Integer> messageResource) {
        QueryWrapper<MsgQuantityStatisticsDo> msgQuantityStatisticsDoQueryWrapper = new QueryWrapper<>();
        msgQuantityStatisticsDoQueryWrapper.select("sum(send_num) AS sum " );
        msgQuantityStatisticsDoQueryWrapper.ge("send_time",startDate);
        if (endDate != null) {//结束时间
            msgQuantityStatisticsDoQueryWrapper.le("send_time", endDate);
        }
        if (!Strings.isNullOrEmpty(operatorCode)) {//运营商
            msgQuantityStatisticsDoQueryWrapper.eq("operator_code", operatorCode);
        }
        if(!admin){
            List<String> customerIds = queryCustomerList(SessionContextUtil.getUser().getUserId());
            if(CollectionUtils.isNotEmpty(customerIds)){
                msgQuantityStatisticsDoQueryWrapper.in("creator",customerIds);
            }else{
                return;
            }
        }
        msgQuantityStatisticsDoQueryWrapper.in("message_resource",messageResource);
        msgQuantityStatisticsDoQueryWrapper.eq("account_type",1);
        List<Map<String, Object>> maps = msgQuantityStatisticDao.selectMaps(msgQuantityStatisticsDoQueryWrapper);
        if(CollectionUtils.isNotEmpty(maps)){
            for(Map<String, Object> item : maps){
                if(item != null){
                    SendStatusAnalysisItem sendStatusAnalysisItem = resp.getSendStatusFor5G();
                    if(messageResource.contains(MSG_SEND_PLAN)){
                        sendStatusAnalysisItem.setSendPlanAmount(Long.valueOf(item.get("sum")+""));
                    }else{
                        sendStatusAnalysisItem.setRobotAmount(Long.valueOf(item.get("sum")+""));
                    }
                }
            }
        }
    }

    private void querySendStatusAnalysisByStatus(SendStatusAnalysisResp resp, boolean admin, Date startDate, Date endDate, String operatorCode,List<Integer> messageResource,Long planId) {
        QueryWrapper<MsgQuantityStatisticsDo> msgQuantityStatisticsDoQueryWrapper = new QueryWrapper<>();
        msgQuantityStatisticsDoQueryWrapper.select("sum(unknow_num) AS unknowSum," +
                "sum(success_num) AS successSum," +
                "sum(failed_num) AS failedSum," +
                "sum(read_num) AS readSum," +
                "sum(send_num) AS sum," +
                "account_type as accountType" );
        msgQuantityStatisticsDoQueryWrapper.ge("send_time",startDate);
        if (endDate != null) {//结束时间
            msgQuantityStatisticsDoQueryWrapper.le("send_time", endDate);
        }
        if(planId != null){
            msgQuantityStatisticsDoQueryWrapper.eq("plan_id",planId);
        }
        if (!Strings.isNullOrEmpty(operatorCode)) {//运营商
            msgQuantityStatisticsDoQueryWrapper.eq("operator_code", operatorCode);
        }
        if(!admin){
            List<String> customerIds = queryCustomerList(SessionContextUtil.getUser().getUserId());
            if(CollectionUtils.isNotEmpty(customerIds)){
                msgQuantityStatisticsDoQueryWrapper.in("creator",customerIds);
            }else{
                return;
            }
        }
        msgQuantityStatisticsDoQueryWrapper.in("message_resource",messageResource);
        msgQuantityStatisticsDoQueryWrapper.groupBy("accountType");
        List<Map<String, Object>> maps = msgQuantityStatisticDao.selectMaps(msgQuantityStatisticsDoQueryWrapper);
        if(CollectionUtils.isNotEmpty(maps)){
            for(Map<String, Object> item : maps){
                if(item != null){
                    SendStatusAnalysisItem sendStatusAnalysisItem = new SendStatusAnalysisItem();
                    sendStatusAnalysisItem.setUnKnowAmount(Long.valueOf(item.get("unknowSum")+""));
                    sendStatusAnalysisItem.setSuccessAmount(Long.valueOf(item.get("successSum")+""));
                    sendStatusAnalysisItem.setFailAmount(Long.valueOf(item.get("failedSum")+""));
                    sendStatusAnalysisItem.setReadAmount(Long.valueOf(item.get("readSum")+""));
                    sendStatusAnalysisItem.setMassSendAmount(Long.valueOf(item.get("sum")+""));
                    sendStatusAnalysisItem.setRobotAmount(0L);
                    sendStatusAnalysisItem.setSendPlanAmount(0L);
                    if(1 == Integer.parseInt(item.get("accountType")+"")){
                        resp.setSendStatusFor5G(sendStatusAnalysisItem);
                    }else if(2 == Integer.parseInt(item.get("accountType")+"")){
                        resp.setSendStatusForMedia(sendStatusAnalysisItem);
                    }else{
                        resp.setSendStatusForShort(sendStatusAnalysisItem);
                    }

                }
            }
        }
    }

    private SendStatusAnalysisResp initResult() {
        SendStatusAnalysisResp resp = new SendStatusAnalysisResp();
        SendStatusAnalysisItem sendStatusAnalysisItem = new SendStatusAnalysisItem();
        sendStatusAnalysisItem.setUnKnowAmount(0L);
        sendStatusAnalysisItem.setSuccessAmount(0L);
        sendStatusAnalysisItem.setFailAmount(0L);
        sendStatusAnalysisItem.setReadAmount(0L);
        sendStatusAnalysisItem.setMassSendAmount(0L);
        sendStatusAnalysisItem.setRobotAmount(0L);
        sendStatusAnalysisItem.setSendPlanAmount(0L);
        resp.setSendStatusFor5G(sendStatusAnalysisItem);
        resp.setSendStatusForMedia(sendStatusAnalysisItem);
        resp.setSendStatusForShort(sendStatusAnalysisItem);
        return resp;
    }

    @Override
    public SendByPageQueryResp sendByPageQuery(PageQueryReq req) {
        SendByPageQueryResp resp = new SendByPageQueryResp();
        Date startDate = DateUtils.obtainTime(DAY, START, DateUtils.obtainDate(req.getStartTime()));
        Date endDate = DateUtils.obtainTime(DAY, END, DateUtils.obtainDate(req.getEndTime()));
        String timeType = DateUtils.obtainTimeType(startDate, endDate);
        List<String> times = HOUR.startsWith(timeType) ?  DateUtils.obtainAllHourList() : DateUtils.obtainDayList(startDate, endDate);
        Map<String,SendByPageQueryItem> sendByPageQueryItemMap = initResultByTime(times);

        //执行计划量， 查询指定时间段执行的
        Map<String,Long> sendPlanExecCountMap = obtainSendPlanExecCount(timeType,startDate,endDate,true,null);
        //群发次数，查询指定时间段执行的  执行完成的
        Map<String,Long> massSendCountMap = obtainMassSendCount(timeType,startDate,endDate,true,null);

        for(Map.Entry<String,SendByPageQueryItem> entry : sendByPageQueryItemMap.entrySet()){
            String key = entry.getKey();
            SendByPageQueryItem value = entry.getValue();
            value.setPlanExecAmount(sendPlanExecCountMap.getOrDefault(key,0L));
            value.setMassSendAmount(massSendCountMap.getOrDefault(key,0L));
        }

        QueryWrapper<MsgQuantityStatisticsDo> msgQuantityStatisticsDoQueryWrapper = new QueryWrapper<>();
        if(HOUR.startsWith(timeType)){
            msgQuantityStatisticsDoQueryWrapper.select("DATE_FORMAT(send_time,'%H:%i') AS sendTime," +
                    "sum(send_num) AS sum," +
                    "account_type as accountType" );
        }else{
            msgQuantityStatisticsDoQueryWrapper.select("DATE_FORMAT(send_time,'%Y-%m-%d') AS sendTime," +
                    "sum(send_num) AS sum," +
                    "account_type as accountType" );
        }
        msgQuantityStatisticsDoQueryWrapper.ge("send_time",startDate);
        if (endDate != null) {//结束时间
            msgQuantityStatisticsDoQueryWrapper.le("send_time", endDate);
        }
        msgQuantityStatisticsDoQueryWrapper.eq("message_resource",MSG_SEND_PLAN);
        msgQuantityStatisticsDoQueryWrapper.groupBy("accountType,sendTime");
        List<Map<String, Object>> maps = msgQuantityStatisticDao.selectMaps(msgQuantityStatisticsDoQueryWrapper);
        if(CollectionUtils.isNotEmpty(maps)){
            for(Map<String, Object> item : maps){
                if(item != null){
                    SendByPageQueryItem sendByPageQueryItem = sendByPageQueryItemMap.get(item.get("sendTime")+"");
                    if(1 == Integer.parseInt(item.get("accountType")+"")){
                        sendByPageQueryItem.setMassSendFor5GAmount(Long.valueOf(item.get("sum")+""));
                    }else if(2 == Integer.parseInt(item.get("accountType")+"")){
                        sendByPageQueryItem.setMassSendForMediaAmount(Long.valueOf(item.get("sum")+""));
                    }else{
                        sendByPageQueryItem.setMassSendForShortAmount(Long.valueOf(item.get("sum")+""));
                    }
                }
            }
        }

        int pageSize = req.getPageSize();
        int pageNum = req.getPageNo();
        List<SendByPageQueryItem> pageList;
        List<SendByPageQueryItem> values = new ArrayList<>( sendByPageQueryItemMap.values());
        Collections.reverse(values);
        try {
            pageList = values.subList(pageSize*(pageNum-1),pageSize*pageNum); //从下标0开始，找到第10个 即0-9
        } catch (IndexOutOfBoundsException e) {
            try {
                pageList = values.subList(pageSize*(pageNum-1),values.size());  //数组越界异常时，取到最后一个元素
            } catch (Exception e2) {
                pageList = new ArrayList<>();
            }
        }
        PageResult<SendByPageQueryItem> pageInfo = new PageResult<>();
        pageInfo.setList(pageList);
        pageInfo.setTotal((long) values.size());
        resp.setSendByPageQueryItems(pageInfo);
        return resp;
    }

    /**
     * 根据时间、账号、计划
     * @param req
     * @return
     */
    @Override
    public SendTrendsByStatusResp sendTrendsByStatus(QueryReq req) {
        SendTrendsByStatusResp resp =new SendTrendsByStatusResp();
        Integer accountType = req.getAccountType();
        String accountId = req.getAccountId();
        Long planId = req.getPlanId();
        Date startTime = DateUtils.obtainDayTime(START, DateUtils.obtainDate(req.getStartTime()));
        Date endTime = DateUtils.obtainDayTime(END, DateUtils.obtainDate(req.getEndTime()));
        String timeType = DateUtils.obtainTimeType(startTime, endTime);
        List<String> times = HOUR.equals(timeType) ? DateUtils.obtainAllHourList() : DateUtils.obtainDayList(startTime, endTime);
        List<Integer> messageResource = getMessageResource(req.getMessageResource());
        Map<String,MsgStatusItem> sendTrendsByStatusInitMap = initResultByTimeForStatusTrend(times);
        boolean admin = isAdmin();


        QueryWrapper<MsgQuantityStatisticsDo> msgQuantityStatisticsDoQueryWrapper = new QueryWrapper<>();
        if(HOUR.equals(timeType)){
            msgQuantityStatisticsDoQueryWrapper.select("DATE_FORMAT(send_time,'%H:%i') AS sendTime," +
                    "sum(success_num) AS successAmount," +
                    "sum(failed_num) AS failAmount," +
                    "sum(read_num) AS readAmount," +
                    "sum(send_num) AS sendAmount," +
                    "sum(unknow_num) AS unknowAmount,"+
                    "account_type as accountType" );
        }else{
            msgQuantityStatisticsDoQueryWrapper.select("DATE_FORMAT(send_time,'%Y-%m-%d') AS sendTime," +
                    "sum(success_num) AS successAmount," +
                    "sum(failed_num) AS failAmount," +
                    "sum(read_num) AS readAmount," +
                    "sum(send_num) AS sendAmount," +
                    "sum(unknow_num) AS unknowAmount,"+
                    "account_type as accountType" );
        }
        msgQuantityStatisticsDoQueryWrapper.ge("send_time",startTime);
        if (endTime != null) {//结束时间
            msgQuantityStatisticsDoQueryWrapper.le("send_time", endTime);
        }
        if(accountType != null){
            msgQuantityStatisticsDoQueryWrapper.eq("account_type",accountType);
        }
        if(!Strings.isNullOrEmpty(accountId)){
            msgQuantityStatisticsDoQueryWrapper.eq("account_id",accountId);
        }
        if(planId != null){
            msgQuantityStatisticsDoQueryWrapper.eq("plan_id",planId);
        }
        if(!admin){
            List<String> customerIds = queryCustomerList(SessionContextUtil.getUser().getUserId());
            if(CollectionUtils.isNotEmpty(customerIds)){
                msgQuantityStatisticsDoQueryWrapper.in("creator",customerIds);
            }else{
                return resp;
            }
        }
        msgQuantityStatisticsDoQueryWrapper.in("message_resource",messageResource);
        msgQuantityStatisticsDoQueryWrapper.groupBy("accountType,sendTime");
        List<Map<String, Object>> maps = msgQuantityStatisticDao.selectMaps(msgQuantityStatisticsDoQueryWrapper);
        if(CollectionUtils.isNotEmpty(maps)){
            for(Map<String, Object> item : maps){
                if(item != null){
                    MsgStatusItem msgStatusItem = sendTrendsByStatusInitMap.get(item.get("sendTime")+"");
                    msgStatusItem.setFailAmount(Long.valueOf(item.get("failAmount")+""));
                    msgStatusItem.setReadAmount(Long.valueOf(item.get("readAmount")+""));
                    msgStatusItem.setSendAmount(Long.valueOf(item.get("sendAmount")+""));
                    msgStatusItem.setSuccessAmount(Long.valueOf(item.get("successAmount")+""));
                    msgStatusItem.setUnknowAmount(Long.valueOf(item.get("unknowAmount")+""));
                }
            }
        }
        ArrayList<MsgStatusItem> msgStatusItems = new ArrayList<>(sendTrendsByStatusInitMap.values());
//        Collections.reverse(msgStatusItems);
        resp.setMsgStatusItems(msgStatusItems);
        return resp;
    }

    private List<Integer> getMessageResource(List<Integer> messageResource) {
        List<Integer> messageResourceList = new ArrayList<>();
        if(CollectionUtils.isEmpty(messageResource) ){
            //以前的发送计划+快捷群发的发送数量
            messageResourceList.add(MSG_SEND_PLAN);
            messageResourceList.add(FAST_GROUP_MSG);
        }else{
            messageResourceList.addAll(messageResource);
            if(messageResource.contains(MSG_SEND_ROBOT)){//机器人：机器人+关键字回复
                messageResourceList.add(KEYWORD_REPLY);
            }else{
                messageResourceList.add(FAST_GROUP_MSG);
            }
        }
        return messageResourceList;
    }

    @Override
    public QueryMsgSendTotalResp queryMsgSendTotal(QueryMsgSendTotalRep req) {
        List<Integer> messageResource = req.getMessageResource() != null ? req.getMessageResource() : Arrays.asList(MSG_SEND_PLAN);
        QueryMsgSendTotalResp resp = new QueryMsgSendTotalResp();
        Integer accountType = req.getAccountType();
        String startTimeStr = Strings.isNullOrEmpty(req.getStartTime()) ? "2023-01-01" : req.getStartTime();
        String endTimeStr = Strings.isNullOrEmpty(req.getEndTime()) ? DateUtils.obtainDateStr(new Date(),"yyyy-MM-dd") : req.getEndTime();
        Date startTime = DateUtils.obtainDayTime(START, DateUtils.obtainDate(startTimeStr));
        Date endTime = DateUtils.obtainDayTime(END, DateUtils.obtainDate(endTimeStr));
        long totalSendNum = 0L;
        long successSendNum = 0L;
        Map<String,MsgSendByChannelItem> msgSendByChannelItemMap = initMsgSendTotalByChannel(req.getAccountType(),req.getIsQueryYHT());
        boolean admin = isAdmin();
        QueryWrapper<MsgQuantityStatisticsDo> msgQuantityStatisticsDoQueryWrapper = new QueryWrapper<>();
        if(MsgTypeEnum.M5G_MSG.getCode() == req.getAccountType()){
            msgQuantityStatisticsDoQueryWrapper.select("sum(success_num) AS successAmount," +
                    "sum(send_num) AS sendAmount," +
                    "operator_code AS channelId," +
                    "account_type as accountType" );
        }else{
            msgQuantityStatisticsDoQueryWrapper.select("sum(success_num) AS successAmount," +
                    "sum(send_num) AS sendAmount," +
                    "account_dict_code AS channelId," +
                    "account_type as accountType" );
        }

        if(accountType != null){
            msgQuantityStatisticsDoQueryWrapper.eq("account_type",accountType);
        }
        if(startTime != null){
            msgQuantityStatisticsDoQueryWrapper.ge("send_time", startTime);
        }
        if (endTime != null) {//结束时间
            msgQuantityStatisticsDoQueryWrapper.le("send_time", endTime);
        }
        if(!admin){
            List<String> customerIds = queryCustomerList(SessionContextUtil.getUser().getUserId());
            if(CollectionUtils.isNotEmpty(customerIds)){
                msgQuantityStatisticsDoQueryWrapper.in("creator",customerIds);
            }else{
                return resp;
            }
        }
        msgQuantityStatisticsDoQueryWrapper.in("message_resource",messageResource);
        //如果是5G消息，通过operator_code去做渠道分类，如果是是视频短信和短信则通过account_dict_code来区别渠道
        msgQuantityStatisticsDoQueryWrapper.groupBy("channelId");

        List<Map<String, Object>> maps = msgQuantityStatisticDao.selectMaps(msgQuantityStatisticsDoQueryWrapper);
        if(CollectionUtils.isNotEmpty(maps)){
            for(Map<String, Object> item : maps){
                String channelId = item.get("channelId")+"";
                if(item != null && msgSendByChannelItemMap.containsKey(channelId)){
                    long sendAmount = Long.parseLong(item.get("sendAmount")+"");
                    long successSend = Long.parseLong(item.get("successAmount")+"");
                    totalSendNum += sendAmount;
                    successSendNum += successSend;

                    MsgSendByChannelItem msgSendByChannelItem = msgSendByChannelItemMap.get(channelId);
                    msgSendByChannelItem.setSendNum(sendAmount);
                    msgSendByChannelItem.setSuccessSendNum(successSend);
                }
            }
        }
        List<MsgSendByChannelItem> msgSendByChannelItems = new ArrayList<>(msgSendByChannelItemMap.values());
        if(totalSendNum>0){
            for(MsgSendByChannelItem item : msgSendByChannelItems){
                item.setSendNumPer( BigDecimal.valueOf(item.getSendNum()).divide(BigDecimal.valueOf(totalSendNum),4,BigDecimal.ROUND_HALF_UP ).multiply(BigDecimal.valueOf(100)).setScale(2,BigDecimal.ROUND_HALF_UP));
            }
        }
        resp.setTotalSendNum(totalSendNum);
        resp.setTotalSuccessSendNum(successSendNum);
        resp.setMsgSendByChannelItem(msgSendByChannelItems);
        return resp;
    }

    /**
     * 初始化渠道统计
     * @param accountType 账号类型
     * @return 初始化数据
     */
    private Map<String, MsgSendByChannelItem> initMsgSendTotalByChannel(Integer accountType,Integer isQueryYHT) {
        Map<String, MsgSendByChannelItem> msgSendByChannelItemMap = new HashMap<>();
        MsgSendByChannelItem msgSendByChannelItem;
        Map<String,String> channelInfo = new HashMap<>();
        if(MsgTypeEnum.M5G_MSG.getCode() == accountType){//运营商编码： 0:硬核桃，1：联通，2：移动，3：电信
            if(isQueryYHT != null && isQueryYHT == 1){
                channelInfo.put("0","硬核桃");
            }
            channelInfo.put("1","联通");
            channelInfo.put("2","移动");
            channelInfo.put("3","电信");
        }else{
            channelInfo.put("01","默认通道");
        }
        for(Map.Entry<String,String> entry : channelInfo.entrySet()){
            msgSendByChannelItem = new MsgSendByChannelItem();
            msgSendByChannelItem.setChannelId(entry.getKey());
            msgSendByChannelItem.setChannelName(entry.getValue());
            msgSendByChannelItem.setSendNum(0L);
            msgSendByChannelItem.setSuccessSendNum(0L);
            msgSendByChannelItem.setSendNumPer(new BigDecimal(0L));
            msgSendByChannelItemMap.put(entry.getKey(),msgSendByChannelItem);
        }
        return msgSendByChannelItemMap;
    }

    @Override
    public SendPlanQueryResp querySendPlanCount(SendPlanQueryRep req) {
        Date startTime = Strings.isNullOrEmpty(req.getStartTime()) ? null : DateUtils.obtainDayTime(START, DateUtils.obtainDate(req.getStartTime()));
        Date endTime =  Strings.isNullOrEmpty(req.getEndTime()) ? null : DateUtils.obtainDayTime(END, DateUtils.obtainDate(req.getEndTime()));
        SendPlanQueryResp resp = new SendPlanQueryResp();
        long sendPlanSum = 0L;
        long massSendSum = 0L;
        long execPlanCount = 0L;

        boolean admin = isAdmin();
        //查询发送计划信息
        List<Map<String, Object>> maps = queryPlanCount(admin,"create",startTime,endTime);
        if(CollectionUtils.isNotEmpty(maps)){
            for(Map<String, Object> item : maps){
                if(item != null){
                    sendPlanSum+=Long.parseLong(item.get("sum")+"");
                }
            }
        }
        //查询执行计划信息
        List<Map<String, Object>> exeMaps = queryPlanCount(admin,"start",startTime,endTime);
        if(CollectionUtils.isNotEmpty(exeMaps)){
            for(Map<String, Object> item : exeMaps){
                if(item != null && !(item.get("planStatus")+"").equals("0")){
                    execPlanCount+=Long.parseLong(item.get("sum")+"");
                }
            }
        }

        //查询群发次数信息
        List<Map<String, Object>> map2s = queryMassSendCount(admin,startTime,endTime);
        if(CollectionUtils.isNotEmpty(map2s)){
            for(Map<String, Object> item : map2s){
                if(item != null){
                    massSendSum+=Long.parseLong(item.get("sum")+"");
                }
            }
        }

        resp.setMassSendSum(massSendSum);
        resp.setSendPlanSum(sendPlanSum);
        resp.setExecPlanCount(execPlanCount);
        return resp;
    }

    private List<Map<String, Object>> queryMassSendCount(boolean admin,Date startTime,Date endTime) {
        QueryWrapper<RobotGroupSendPlansDetailDo> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("sum(send_amount) AS sum ");
        if(!admin){
            List<String> customerIds = queryCustomerList(SessionContextUtil.getUser().getUserId());
            if(CollectionUtils.isNotEmpty(customerIds)){
                queryWrapper.in("creator",customerIds);
            }else{
                return new ArrayList<>();
            }
        }
        if(startTime != null){
            queryWrapper.ge("send_time", startTime);
        }
        if (endTime != null) {//结束时间
            queryWrapper.le("send_time", endTime);
        }
        queryWrapper.eq("plan_status",2);
        return robotGroupSendPlansDetailDao.selectMaps(queryWrapper);
    }

    private List<Map<String, Object>> queryPlanCount(boolean admin,String dateType,Date startTime,Date endTime) {
        QueryWrapper<RobotGroupSendPlansDo> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("count(id) AS sum," +
                "plan_status as planStatus" );
        if(!admin){
            List<String> customerIds = queryCustomerList(SessionContextUtil.getUser().getUserId());
            if(CollectionUtils.isNotEmpty(customerIds)){
                queryWrapper.in("creator",customerIds);
            }else{
                return new ArrayList<>();
            }
        }
        if(startTime != null){
            queryWrapper.ge("create".equals(dateType) ?"create_time" : "start_time", startTime);
        }
        if (endTime != null) {//结束时间
            queryWrapper.le("create".equals(dateType) ?"create_time" : "start_time", endTime);
        }

        queryWrapper.groupBy("plan_status");
        return robotGroupSendPlansDao.selectMaps(queryWrapper);
    }

    @Override
    public QuerySendPlanExecTrendResp querySendPlanExecTrend(SendPlanQueryRep req) {
        QuerySendPlanExecTrendResp resp = new QuerySendPlanExecTrendResp();
        Date startTime = DateUtils.obtainDayTime(START, DateUtils.obtainDate(req.getStartTime()));
        Date endTime = DateUtils.obtainDayTime(END, DateUtils.obtainDate(req.getEndTime()));
        String timeType = DateUtils.obtainTimeType(startTime, endTime);
        List<String> times = HOUR.equals(timeType) ? DateUtils.obtainAllHourList() : DateUtils.obtainDayList(startTime, endTime);
        Map<String,SendPlanExecTrendItem> sendPlanExecTrendMap = initResultByTimeForSendPlanExecTrend(times);
        //发送计划，查询指定时间段新增的
        boolean isAdmin = isAdmin();
        List<String> customerIdList = queryCustomerList(SessionContextUtil.getUser().getUserId());
        if(!isAdmin && CollectionUtils.isEmpty(customerIdList))
            return resp;
        Map<String,Long> sendPlanCountMap = obtainSendPlanCount(timeType,startTime,endTime,isAdmin,customerIdList);
        //执行计划量， 查询指定时间段执行的
        Map<String,Long> sendPlanExecCountMap = obtainSendPlanExecCount(timeType,startTime,endTime,isAdmin,customerIdList);
        //群发次数，查询指定时间段执行的  执行完成的
        Map<String,Long> massSendCountMap = obtainMassSendCount(timeType,startTime,endTime,isAdmin,customerIdList);

        for(Map.Entry<String,SendPlanExecTrendItem> entry : sendPlanExecTrendMap.entrySet()){
            String time = entry.getKey();
            SendPlanExecTrendItem item = entry.getValue();
            item.setSendPlanAmount(sendPlanCountMap.getOrDefault(time,0L));
            item.setExecPlanAmount(sendPlanExecCountMap.getOrDefault(time,0L));
            item.setMassSendAmount(massSendCountMap.getOrDefault(time,0L));
        }
        resp.setSendPlanExecTrendItems(new ArrayList<>(sendPlanExecTrendMap.values()));
        return resp;
    }

    /**
     * 单次群发发送统计
     * @param req 请求信息
     * @return 响应结果
     */
    @Override
    public QuerySingleMassCountResp querySingleMassCount(SingleMassQueryRep req) {
        QuerySingleMassCountResp resp = new QuerySingleMassCountResp();
        Date startDate = DateUtils.obtainTime(DAY, START, DateUtils.obtainDate(req.getStartTime()));
        Date endDate = DateUtils.obtainTime(DAY, END, DateUtils.obtainDate(req.getEndTime()));
        Long planId = req.getPlanId();
        String userId = SessionContextUtil.getUser().getUserId();
        List<ClickButtonInfoItem> clickButtonInfoItems = new ArrayList<>();
        resp.setMassSendAmount(0L);
        resp.setSuccessAmount(0L);
        resp.setFailAmount(0L);
        resp.setUnknownAmount(0L);
        resp.setReadAmount(0L);
        resp.setClickButtonAmount(0L);
        resp.setClickButtonInfoItems(clickButtonInfoItems);

        if(planId != null && !Strings.isNullOrEmpty(userId)){
            //查询所有的节点ID
            List<Long> planDetailIds = (req.getPlanDetailId() != null) ? Collections.singletonList(req.getPlanDetailId()) : getPlanDetailsIds(planId, userId);
            if(CollectionUtils.isNotEmpty(planDetailIds)){
                //查询时间

                //查询点击数
                clickButtonInfoItems = getClickButtonInfo(null,planDetailIds,startDate,endDate);
                long clickButtonAmount = getClickButtonAllAmount(clickButtonInfoItems);
                //查询其它统计
                getSingleMassCount(null,planDetailIds, resp,startDate,endDate);
                resp.setClickButtonAmount(clickButtonAmount);
                resp.setClickButtonInfoItems(clickButtonInfoItems);
            }else{//表示为快捷群发
                //查询时间
                FastGroupMessageItem fastGroupMessageItem = fastGroupMessageApi.findByPlanId(planId);
                if(fastGroupMessageItem != null && fastGroupMessageItem.getSendTime() != null){
                    startDate = DateUtils.obtainTime(DAY, START, DateUtils.localDateTime2Date(fastGroupMessageItem.getSendTime()));
                    endDate = DateUtils.obtainTime(DAY, END, DateUtils.addDays(startDate,7));

                    //查询点击数
                    clickButtonInfoItems = getClickButtonInfo(planId,null,startDate,endDate);
                    long clickButtonAmount = getClickButtonAllAmount(clickButtonInfoItems);
                    //查询其它统计
                    getSingleMassCount(planId, null,resp,startDate,endDate);
                    resp.setClickButtonAmount(clickButtonAmount);
                    resp.setClickButtonInfoItems(clickButtonInfoItems);
                }

            }
        }
        return resp;
    }

    private List<Long> getPlanDetailsIds(Long planId, String userId) {
        LambdaQueryWrapper<RobotGroupSendPlansDetailDo> robotGroupSendPlansDetailDoLambdaQueryWrapper = new LambdaQueryWrapper<>();
        robotGroupSendPlansDetailDoLambdaQueryWrapper.eq(RobotGroupSendPlansDetailDo::getCreator,userId);
        robotGroupSendPlansDetailDoLambdaQueryWrapper.eq(RobotGroupSendPlansDetailDo::getPlanId,planId);
        List<RobotGroupSendPlansDetailDo> robotGroupSendPlansDetailDos = robotGroupSendPlansDetailDao.selectList(robotGroupSendPlansDetailDoLambdaQueryWrapper);
        if(CollectionUtils.isNotEmpty(robotGroupSendPlansDetailDos)) {
            List<Long> planDetailIds = new ArrayList<>();
            robotGroupSendPlansDetailDos.forEach(i -> planDetailIds.add(i.getId()));
            return planDetailIds;
        }
        return Collections.emptyList();
    }

    /**
     * 查询其它的统计
     *
     * @param planDetailIds 计划信息
     */
    private void getSingleMassCount(Long planId, List<Long> planDetailIds, QuerySingleMassCountResp resp,Date startDate,Date endDate) {
        //1、查询所有的数据
        List<MsgQuantityStatisticsDo> result = getSingleMassDBList(planId,planDetailIds,startDate,endDate);
        //2、统计数据
        if(CollectionUtils.isNotEmpty(result)){
            for(MsgQuantityStatisticsDo item : result){
                resp.setMassSendAmount(resp.getMassSendAmount()+item.getSendNum());
                resp.setSuccessAmount(resp.getSuccessAmount()+item.getSuccessNum());
                resp.setFailAmount(resp.getFailAmount()+item.getFailedNum());
                resp.setUnknownAmount(resp.getUnknownAmount()+item.getUnknowNum());
                resp.setReadAmount(resp.getReadAmount()+item.getReadNum());
            }
        }
//
//        QueryWrapper<MsgQuantityStatisticsDo> msgQuantityStatisticsDoQueryWrapper = new QueryWrapper<>();
//        msgQuantityStatisticsDoQueryWrapper.select("sum(send_num) AS massSendAmount,sum(failed_num) AS failAmount,sum(success_num) AS successAmount,sum(unknow_num) AS unknownAmount,sum(read_num) AS readAmount ");
//        msgQuantityStatisticsDoQueryWrapper.in("plan_detail_id",planDetailIds);
//        if(startDate != null){
//            msgQuantityStatisticsDoQueryWrapper.ge("send_time",startDate);
//        }
//        if(endDate!= null){
//            msgQuantityStatisticsDoQueryWrapper.le("send_time",endDate);
//        }
//        List<Map<String, Object>> maps = msgQuantityStatisticDao.selectMaps(msgQuantityStatisticsDoQueryWrapper);
//        if(CollectionUtils.isNotEmpty(maps)){
//            for(Map<String, Object> item : maps){
//                if(item !=null){
//                    resp.setMassSendAmount(Long.valueOf(item.get("massSendAmount")+""));
//                    resp.setSuccessAmount(Long.valueOf(item.get("successAmount")+""));
//                    resp.setFailAmount(Long.valueOf(item.get("failAmount")+""));
//                    resp.setUnknownAmount(Long.valueOf(item.get("unknownAmount")+""));
//                    resp.setReadAmount(Long.valueOf(item.get("readAmount")+""));
//                }
//            }
//        }
    }

    /**
     * 查询 单次群发分析的数据条数信息
     * @param planDetailIds 结点列表
     * @param startDate 开始时间
     * @param endDate 结束时间
     * @return 返回时间
     */
    private List<MsgQuantityStatisticsDo> getSingleMassDBList(Long planId,List<Long> planDetailIds, Date startDate, Date endDate) {
        LambdaQueryWrapper<MsgQuantityStatisticsDo> msgQuantityStatisticsDoLambdaQueryWrapper = new LambdaQueryWrapper<>();
        if(planId != null){
            msgQuantityStatisticsDoLambdaQueryWrapper.eq(MsgQuantityStatisticsDo::getPlanId,planId)
                    .isNotNull(MsgQuantityStatisticsDo::getAccountId)
                    .between(MsgQuantityStatisticsDo::getSendTime,startDate,endDate);
        }else{
            msgQuantityStatisticsDoLambdaQueryWrapper.in(MsgQuantityStatisticsDo::getPlanDetailId,planDetailIds)
                    .isNotNull(MsgQuantityStatisticsDo::getAccountId)
                    .between(MsgQuantityStatisticsDo::getSendTime,startDate,endDate);
        }
        return msgQuantityStatisticDao.selectList(msgQuantityStatisticsDoLambdaQueryWrapper);
    }

    private long getClickButtonAllAmount(List<ClickButtonInfoItem> clickButtonInfoItems) {
        long all = 0L;
        for(ClickButtonInfoItem item : clickButtonInfoItems){
            all += item.getClickAmount();
        }
        return all;
    }

    private List<ClickButtonInfoItem> getClickButtonInfo(Long planId,List<Long> planDetailIds,Date startDate,Date endDate) {
        List<ClickButtonInfoItem> clickButtonInfoItems = new ArrayList<>();
        QueryWrapper<RobotClickResultDo> robotClickResultDoQueryWrapper = new QueryWrapper<>();
        robotClickResultDoQueryWrapper.select("btn_uuid AS btnUuid,btn_name AS btnName,sum(click_amount) AS sum ");
        if(planId != null){
            robotClickResultDoQueryWrapper.eq("plan_detail_id",planId);//TODO 快捷群发
        }else{
            robotClickResultDoQueryWrapper.in("plan_detail_id",planDetailIds);
        }

        robotClickResultDoQueryWrapper.ge("click_amount",0);
        robotClickResultDoQueryWrapper.isNotNull("btn_name");
        robotClickResultDoQueryWrapper.isNotNull("btn_uuid");
        if(startDate != null){
            robotClickResultDoQueryWrapper.ge("create_time",startDate);
        }
        if(endDate!= null){
            robotClickResultDoQueryWrapper.le("create_time",endDate);
        }
        robotClickResultDoQueryWrapper.groupBy("btnUuid,btnName");

        List<Map<String, Object>> maps = robotClickResult1Dao.selectMaps(robotClickResultDoQueryWrapper);
        if(CollectionUtils.isNotEmpty(maps)){
            for(Map<String, Object> item : maps){
                if(item !=null){
                    ClickButtonInfoItem clickButtonInfoItem = new ClickButtonInfoItem();
                    clickButtonInfoItem.setBtnUuid(item.get("btnUuid")+"");
                    clickButtonInfoItem.setButtonName(item.get("btnName")+"");
                    clickButtonInfoItem.setClickAmount(Long.valueOf(item.get("sum")+""));
                    clickButtonInfoItems.add(clickButtonInfoItem);
                }
            }
        }
        return  clickButtonInfoItems;
    }

    @Override
    public QuerySingleMassTrendResp querySingleMassTrend(SingleMassQueryRep req) {
        QuerySingleMassTrendResp resp = new QuerySingleMassTrendResp();
        Date startDate = DateUtils.obtainTime(DAY, START, DateUtils.obtainDate(req.getStartTime()));
        Date endDate = DateUtils.obtainTime(DAY, END, DateUtils.obtainDate(req.getEndTime()));
        List<String> times =  DateUtils.obtainDayList(startDate, endDate);
        Long planId = req.getPlanId();
        String userId = SessionContextUtil.getUser().getUserId();
        Map<String,SingleMassTrendItem> singleMassTrendItems = initSingleMassTrendItems(times);
        if(planId != null && !Strings.isNullOrEmpty(userId)) {
            //查询所有的节点ID
            List<Long> planDetailIds = req.getPlanDetailId() != null ? Collections.singletonList(req.getPlanDetailId()) : getPlanDetailsIds(planId, userId);
            if (CollectionUtils.isNotEmpty(planDetailIds)) {
                //查询按钮点击情况
                queryClickButtonInfo(null,planDetailIds,startDate,endDate,singleMassTrendItems);
                //查询其它数据
                queryMassSendInfo(null,planDetailIds,startDate,endDate,singleMassTrendItems);
            }else{
                //查询时间
                FastGroupMessageItem fastGroupMessageItem = fastGroupMessageApi.findByPlanId(planId);
                if(fastGroupMessageItem != null && fastGroupMessageItem.getSendTime() != null){
                    startDate = DateUtils.obtainTime(DAY, START, DateUtils.localDateTime2Date(fastGroupMessageItem.getSendTime()));
                    endDate = DateUtils.obtainTime(DAY, END, DateUtils.addDays(startDate,7));
                    times =  DateUtils.obtainDayList(startDate, endDate);
                    singleMassTrendItems = initSingleMassTrendItems(times);
                    //查询按钮点击情况
                    queryClickButtonInfo(planId,null,startDate,endDate,singleMassTrendItems);
                    //查询其它数据
                    queryMassSendInfo(planId,null,startDate,endDate,singleMassTrendItems);
                }

            }
        }
        resp.setSingleMassTrendItems(new ArrayList<>(singleMassTrendItems.values()));
        return resp;
    }

    @Override
    public Map<Long, Long> queryIsSendPlanId() {
        Map<Long, Long> result = new HashMap<>();
        LambdaQueryWrapper<MsgQuantityStatisticsDo> msgQuantityStatisticsDoLambdaQueryWrapper = new LambdaQueryWrapper<>();
        msgQuantityStatisticsDoLambdaQueryWrapper.eq(MsgQuantityStatisticsDo::getCreator,SessionContextUtil.getUser().getUserId());
        msgQuantityStatisticsDoLambdaQueryWrapper.gt(MsgQuantityStatisticsDo::getSendNum,0);
        List<MsgQuantityStatisticsDo> msgQuantityStatisticsDos = msgQuantityStatisticDao.selectList(msgQuantityStatisticsDoLambdaQueryWrapper);
        if(CollectionUtils.isNotEmpty(msgQuantityStatisticsDos)){
            for(MsgQuantityStatisticsDo msgQuantityStatisticsDo : msgQuantityStatisticsDos){
                result.put(msgQuantityStatisticsDo.getPlanDetailId(),msgQuantityStatisticsDo.getPlanId());
            }
        }
        return result;
    }

    private void queryMassSendInfo(Long planId,List<Long> planDetailIds, Date startDate, Date endDate, Map<String, SingleMassTrendItem> singleMassTrendItems) {
        //1、查询所有的数据
        List<MsgQuantityStatisticsDo> result = getSingleMassDBList(planId,planDetailIds,startDate,endDate);
        //2、统计数据
        if(CollectionUtils.isNotEmpty(result)){
            for(MsgQuantityStatisticsDo item : result){
                String execTime = DateUtils.obtainDateStr(item.getSendTime(),"yyyy-MM-dd");
                if(singleMassTrendItems.containsKey(execTime)){
                    SingleMassTrendItem singleMassTrendItem = singleMassTrendItems.get(execTime);
                    singleMassTrendItem.setSuccessAmount(singleMassTrendItem.getSuccessAmount()+item.getSuccessNum());
                    singleMassTrendItem.setFailAmount(singleMassTrendItem.getFailAmount()+item.getFailedNum());
                    singleMassTrendItem.setReadAmount(singleMassTrendItem.getReadAmount()+item.getReadNum());
                }
            }
        }
//
//
//
//        QueryWrapper<MsgQuantityStatisticsDo> msgQuantityStatisticsDoQueryWrapper = new QueryWrapper<>();
//        msgQuantityStatisticsDoQueryWrapper.select("DATE_FORMAT(send_time,'%Y-%m-%d') AS execTime,sum(failed_num) AS failAmount,sum(success_num) AS successAmount,sum(read_num) AS readAmount ");
//        msgQuantityStatisticsDoQueryWrapper.in("plan_detail_id",planDetailIds);
//        if(startDate != null){
//            msgQuantityStatisticsDoQueryWrapper.ge("send_time",startDate);
//        }
//        if(endDate!= null){
//            msgQuantityStatisticsDoQueryWrapper.le("send_time",endDate);
//        }
//        msgQuantityStatisticsDoQueryWrapper.groupBy("execTime");
//        List<Map<String, Object>> maps = msgQuantityStatisticDao.selectMaps(msgQuantityStatisticsDoQueryWrapper);
//        if(CollectionUtils.isNotEmpty(maps)){
//            for(Map<String, Object> item : maps){
//                if(item !=null && (singleMassTrendItems.containsKey(item.get("execTime")+""))){
//                    SingleMassTrendItem singleMassTrendItem = singleMassTrendItems.get(item.get("execTime") + "");
//                    singleMassTrendItem.setSuccessAmount(Long.valueOf(item.get("successAmount")+""));
//                    singleMassTrendItem.setFailAmount(Long.valueOf(item.get("failAmount")+""));
//                    singleMassTrendItem.setReadAmount(Long.valueOf(item.get("readAmount")+""));
//                }
//            }
//        }
    }

    private void queryClickButtonInfo(Long planId,List<Long> planDetailIds, Date startDate, Date endDate, Map<String, SingleMassTrendItem> singleMassTrendItems) {
        QueryWrapper<RobotClickResultDo> robotClickResultDoQueryWrapper = new QueryWrapper<>();
        robotClickResultDoQueryWrapper.select("DATE_FORMAT(create_time,'%Y-%m-%d') AS execTime,sum(click_amount) AS sum ");
        if(planId != null){
            robotClickResultDoQueryWrapper.eq("plan_detail_id",planId); // TODO 快捷群发
        }else{
            robotClickResultDoQueryWrapper.in("plan_detail_id",planDetailIds);
        }
        robotClickResultDoQueryWrapper.ge("click_amount",0);
        robotClickResultDoQueryWrapper.isNotNull("btn_name");
        robotClickResultDoQueryWrapper.isNotNull("btn_uuid");
        if(startDate != null){
            robotClickResultDoQueryWrapper.ge("create_time",startDate);
        }
        if(endDate!= null){
            robotClickResultDoQueryWrapper.le("create_time",endDate);
        }
        robotClickResultDoQueryWrapper.groupBy("execTime");

        List<Map<String, Object>> maps = robotClickResult1Dao.selectMaps(robotClickResultDoQueryWrapper);
        if(CollectionUtils.isNotEmpty(maps)){
            for(Map<String, Object> item : maps){
                if(item !=null && (singleMassTrendItems.containsKey(item.get("execTime")+""))){
                    SingleMassTrendItem singleMassTrendItem = singleMassTrendItems.get(item.get("execTime") + "");
                    singleMassTrendItem.setClickButtonAmount(Long.valueOf(item.get("sum")+""));
                }
            }
        }
    }

    /**
     * 初始化查询列表
     * @param times 时间列表
     * @return
     */
    private Map<String,SingleMassTrendItem> initSingleMassTrendItems(List<String> times) {
        Map<String, SingleMassTrendItem> singleMassTrendItemMap = new TreeMap<>();
        SingleMassTrendItem singleMassTrendItem;
        for(String time : times){
            singleMassTrendItem = new SingleMassTrendItem();
            singleMassTrendItem.setClickButtonAmount(0L);
            singleMassTrendItem.setFailAmount(0L);
            singleMassTrendItem.setReadAmount(0L);
            singleMassTrendItem.setSuccessAmount(0L);
            singleMassTrendItem.setSendTime(time);
            singleMassTrendItemMap.put(time,singleMassTrendItem);
        }
        return singleMassTrendItemMap;
    }

    private Map<String, Long> obtainMassSendCount(String timeType, Date startTime, Date endTime, boolean isAdmin, List<String> customerIdList) {
        Map<String,Long> massSendCountMap = new HashMap<>();
        QueryWrapper<RobotGroupSendPlansDetailDo> robotGroupSendPlansDetailDoQueryWrapper = new QueryWrapper<>();
        if(HOUR.equals(timeType)){
            robotGroupSendPlansDetailDoQueryWrapper.select("DATE_FORMAT(send_time,'%H:%i') AS execTime,sum(send_amount) AS sum ");
        }else{
            robotGroupSendPlansDetailDoQueryWrapper.select("DATE_FORMAT(send_time,'%Y-%m-%d') AS execTime,sum(send_amount) AS sum ");
        }
        robotGroupSendPlansDetailDoQueryWrapper.eq("plan_status",2);//1:已送达、6：已阅
        robotGroupSendPlansDetailDoQueryWrapper.eq("deleted",0);
        robotGroupSendPlansDetailDoQueryWrapper.ge("send_time",startTime);
        if (endTime != null) {//结束时间
            robotGroupSendPlansDetailDoQueryWrapper.le("send_time", endTime);
        }
        if(!isAdmin){
            robotGroupSendPlansDetailDoQueryWrapper.in("creator",customerIdList);
        }
        robotGroupSendPlansDetailDoQueryWrapper.groupBy("execTime");

        List<Map<String, Object>> maps = robotGroupSendPlansDetailDao.selectMaps(robotGroupSendPlansDetailDoQueryWrapper);
        if(CollectionUtils.isNotEmpty(maps)){
            for(Map<String, Object> item : maps){
                if(item !=null){
                    massSendCountMap.put(item.get("execTime")+"",Long.valueOf(item.get("sum")+""));
                }
            }
        }
        return  massSendCountMap;
    }


    /**
     * 群发次数 查询指定时间段执行的
     * @param timeType 时间类型
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param isAdmin 是否时管理员
     * @param customerIdList 用户列表
     * @return
     */
    private Map<String, Long> obtainSendPlanExecCount(String timeType, Date startTime, Date endTime, boolean isAdmin, List<String> customerIdList) {
        Map<String,Long> sendPlanExecCountMap = new HashMap<>();
        QueryWrapper<RobotGroupSendPlansDetailDo> robotGroupSendPlansDetailDoQueryWrapper = new QueryWrapper<>();
        if(HOUR.equals(timeType)){
            robotGroupSendPlansDetailDoQueryWrapper.select("DATE_FORMAT(send_time,'%H:%i') AS execTime,count(Distinct plan_id) AS sum ");
        }else{
            robotGroupSendPlansDetailDoQueryWrapper.select("DATE_FORMAT(send_time,'%Y-%m-%d') AS execTime,count(Distinct plan_id) AS sum ");
        }
        robotGroupSendPlansDetailDoQueryWrapper.eq("deleted",0);
        robotGroupSendPlansDetailDoQueryWrapper.ge("send_time",startTime);
        if (endTime != null) {//结束时间
            robotGroupSendPlansDetailDoQueryWrapper.le("send_time", endTime);
        }
        if(!isAdmin){
            robotGroupSendPlansDetailDoQueryWrapper.in("creator",customerIdList);
        }
        robotGroupSendPlansDetailDoQueryWrapper.groupBy("execTime");

        List<Map<String, Object>> maps = robotGroupSendPlansDetailDao.selectMaps(robotGroupSendPlansDetailDoQueryWrapper);
        if(CollectionUtils.isNotEmpty(maps)){
            for(Map<String, Object> item : maps){
                if(item !=null){
                    sendPlanExecCountMap.put(item.get("execTime")+"",Long.valueOf(item.get("sum")+""));
                }
            }
        }
        return sendPlanExecCountMap;
    }

    /**
     * 查询发送计划量
     * @param timeType 时间类型
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param isAdmin 是否是管理员
     * @param customerIdList 用户列表
     * @return
     */
    private Map<String, Long> obtainSendPlanCount(String timeType, Date startTime, Date endTime,boolean isAdmin,List<String> customerIdList) {
        Map<String,Long> sendPlanCountMap = new HashMap<>();
        QueryWrapper<RobotGroupSendPlansDo> robotGroupSendPlansDoQueryWrapper = new QueryWrapper<>();
        if(HOUR.equals(timeType)){
            robotGroupSendPlansDoQueryWrapper.select("DATE_FORMAT(create_time,'%H:%i') AS createTime,count(id) AS sum ");
        }else{
            robotGroupSendPlansDoQueryWrapper.select("DATE_FORMAT(create_time,'%Y-%m-%d') AS createTime,count(id) AS sum ");
        }
        robotGroupSendPlansDoQueryWrapper.eq("deleted",0);
        robotGroupSendPlansDoQueryWrapper.ge("create_time",startTime);
        if (endTime != null) {//结束时间
            robotGroupSendPlansDoQueryWrapper.le("create_time", endTime);
        }
        if(!isAdmin){
            robotGroupSendPlansDoQueryWrapper.in("creator",customerIdList);
        }
        robotGroupSendPlansDoQueryWrapper.groupBy("createTime");

        List<Map<String, Object>> maps = robotGroupSendPlansDao.selectMaps(robotGroupSendPlansDoQueryWrapper);
        if(CollectionUtils.isNotEmpty(maps)){
            for(Map<String, Object> item : maps){
                if(item !=null){
                    sendPlanCountMap.put(item.get("createTime")+"",Long.valueOf(item.get("sum")+""));
                }
            }
        }

        return sendPlanCountMap;

    }

    private Map<String, SendPlanExecTrendItem> initResultByTimeForSendPlanExecTrend(List<String> times) {
        Map<String, SendPlanExecTrendItem> sendPlanExecTrendItemMap = new TreeMap<>();
        SendPlanExecTrendItem sendPlanExecTrendItem;
        for(String time : times){
            sendPlanExecTrendItem = new SendPlanExecTrendItem();
            sendPlanExecTrendItem.setExecPlanAmount(0L);
            sendPlanExecTrendItem.setMassSendAmount(0L);
            sendPlanExecTrendItem.setSendPlanAmount(0L);
            sendPlanExecTrendItem.setSendTime(time);
            sendPlanExecTrendItemMap.put(time,sendPlanExecTrendItem);
        }
        return sendPlanExecTrendItemMap;
    }

    private Map<String, MsgStatusItem> initResultByTimeForStatusTrend(List<String> times) {
        Map<String,MsgStatusItem> sendTrendsByStatusInitMap = new TreeMap<>();
        MsgStatusItem msgStatusItem;
        for(String time : times){
            msgStatusItem = new MsgStatusItem();
            msgStatusItem.setSendTime(time);
            msgStatusItem.setFailAmount(0L);
            msgStatusItem.setReadAmount(0L);
            msgStatusItem.setSendAmount(0L);
            msgStatusItem.setSuccessAmount(0L);
            msgStatusItem.setUnknowAmount(0L);
            sendTrendsByStatusInitMap.put(time,msgStatusItem);
        }
        return sendTrendsByStatusInitMap;
    }



    private Map<String,SendByPageQueryItem> initResultByTime(List<String> times) {
        Map<String,SendByPageQueryItem> sendByPageQueryItemMap = new TreeMap<>();
        SendByPageQueryItem sendByPageQueryItem ;
        for(String time : times){
            sendByPageQueryItem = new SendByPageQueryItem();
            sendByPageQueryItem.setSendTime(time);
            sendByPageQueryItem.setMassSendAmount(0L);
            sendByPageQueryItem.setMassSendFor5GAmount(0L);
            sendByPageQueryItem.setMassSendForMediaAmount(0L);
            sendByPageQueryItem.setMassSendForShortAmount(0L);
            sendByPageQueryItem.setPlanExecAmount(0L);
            sendByPageQueryItemMap.put(time,sendByPageQueryItem);
        }
        return sendByPageQueryItemMap;
    }

    private SendTrendsResp sendTrendsForType(String accountType, QueryReq req) {
        SendTrendsResp resp = new SendTrendsResp();
        List<SendTrendsItem> sendTrendsItems ;
        boolean isAccountDict = !MSG_5G.equals(accountType);//是否为账号渠道编码
        Date startDate = DateUtils.obtainTime(DAY, START, DateUtils.obtainDate(req.getStartTime()));
        Date endDate = DateUtils.obtainTime(DAY, END, DateUtils.obtainDate(req.getEndTime()));
        String timeType = DateUtils.obtainTimeType(startDate, endDate);
        List<String> times = HOUR.startsWith(timeType) ?  DateUtils.obtainAllHourList() : DateUtils.obtainDayList(startDate, endDate);
        List<SendTrendsStatisticDto> sendTrendsStatisticDtos;
        List<Integer> messageResource = req.getMessageResource() != null ? req.getMessageResource() : Arrays.asList(MSG_SEND_PLAN);
        if(!Strings.isNullOrEmpty(req.getAccountId())){//根据账号查询
            sendTrendsStatisticDtos = querySendTrendsByAccountId(startDate,endDate,timeType,req.getAccountId(),accountType,messageResource);
            //获取最后的列表
            sendTrendsItems = obtainResultList(timeType,times,sendTrendsStatisticDtos);
        }else{//根据渠道查询
            //获取用户列表
            List<String> customerIds = queryCustomerList(SessionContextUtil.getUser().getUserId());
            if(req.getAccountDictCode() != null || req.getOperatorCode() != null){
                if(req.getAccountDictCode() != null){
                    sendTrendsStatisticDtos = (!isAdmin() && CollectionUtils.isEmpty(customerIds)) ? new ArrayList<>() : querySendTrendsByChannelId(true,startDate,endDate,timeType,accountType,customerIds,req.getAccountDictCode(), messageResource);
                }else{
                    sendTrendsStatisticDtos = (!isAdmin() && CollectionUtils.isEmpty(customerIds)) ? new ArrayList<>() : querySendTrendsByChannelId(false,startDate,endDate,timeType,accountType,customerIds,req.getOperatorCode(), messageResource);
                }
            }else{

                sendTrendsStatisticDtos = (!isAdmin() && CollectionUtils.isEmpty(customerIds)) ? new ArrayList<>() : querySendTrends(startDate,endDate,timeType,accountType,customerIds,messageResource);
            }
            //获取最后的结果
            sendTrendsItems = obtainResultByChannelList(timeType,times,isAccountDict,sendTrendsStatisticDtos,req);

        }
        resp.setSendTrendsItems(sendTrendsItems);
        return resp;
    }



    private List<SendTrendsItem> obtainResultByChannelList(String timeType, List<String> times, boolean isAccountDict,List<SendTrendsStatisticDto> sendTrendsStatisticDtos, QueryReq req) {
        List<SendTrendsItem> sendTrendsItems = new ArrayList<>();
        List<ChannelInfo> channelInfos;
        SendTrendsItem sendTrendsItem;
        ChannelInfo channelInfo;
        Map<String,String> channelMap = isAccountDict ? obtainAccountDictInfoMap(req.getAccountDictCode()) : obtainOperatorMap(req.getOperatorCode());

        Map<String,Map<String,Long>> channelSendMap = obtainChannelSendMap(channelMap,sendTrendsStatisticDtos);

        for(String item : times) {//更具时间拼装
            sendTrendsItem = new SendTrendsItem();
            sendTrendsItem.setSendTime(item);
            long allSendSum = 0L;
            channelInfos = new ArrayList<>();
            for(Map.Entry<String,String> entry : channelMap.entrySet()){
                channelInfo = new ChannelInfo();
                channelInfo.setName(entry.getValue());
                channelInfo.setSendSum(channelSendMap.containsKey(item) ? channelSendMap.get(item).getOrDefault(entry.getKey(), 0L) :0L);
                allSendSum = allSendSum+channelInfo.getSendSum();
                channelInfos.add(channelInfo);
            }
            sendTrendsItem.setChannelInfos(channelInfos);
            sendTrendsItem.setAllSendSum(allSendSum);
            sendTrendsItems.add(sendTrendsItem);
        }
        return sendTrendsItems;
    }

    private Map<String, Map<String, Long>> obtainChannelSendMap(Map<String,String> channelMap,List<SendTrendsStatisticDto> sendTrendsStatisticDtos) {
        Map<String, Map<String, Long>> channelTimeMap = new HashMap<>();
        if(CollectionUtils.isNotEmpty(sendTrendsStatisticDtos)){
            Set<String> channelCodeSet = channelMap.keySet();
            String time;
            Map<String,Long> channelSendMap;
            for(SendTrendsStatisticDto item : sendTrendsStatisticDtos){
                time = item.getSendTime();
                if(channelTimeMap.containsKey(time)){
                    channelSendMap = channelTimeMap.get(time);
                    channelSendMap.put((Strings.isNullOrEmpty(item.getOperatorCode()) ? item.getAccountDictCode() : item.getOperatorCode()),item.getSendSum());
                }else{
                    channelSendMap =new HashMap<>();
                    for(String channelCode : channelCodeSet){
                        channelSendMap.put(channelCode,0L);
                    }
                    channelSendMap.put((Strings.isNullOrEmpty(item.getOperatorCode()) ? item.getAccountDictCode() : item.getOperatorCode()),item.getSendSum());
                    channelTimeMap.put(time,channelSendMap);
                }
            }
        }
        return channelTimeMap;
    }

    private List<SendTrendsItem> obtainResultList(String timeType,List<String> times,List<SendTrendsStatisticDto> sendTrendsStatisticDtos) {
        List<SendTrendsItem> sendTrendsItems = new ArrayList<>();
        SendTrendsItem sendTrendsItem;
        Map<String,Long> resultMap = new HashMap<>();
        sendTrendsStatisticDtos.forEach(i-> resultMap.put(i.getSendTime(),i.getSendSum()));
        for(String item : times){
            sendTrendsItem =new SendTrendsItem();
            sendTrendsItem.setSendTime(item);
            sendTrendsItem.setAllSendSum(resultMap.getOrDefault(item,0L));
            sendTrendsItems.add(sendTrendsItem);
        }
        return sendTrendsItems;
    }

    private List<SendTrendsStatisticDto> querySendTrendsByChannelId(boolean isAccountDictCode, Date startDate, Date endDate, String timeType, String accountType, List<String> customerIds, String channelCode, List<Integer> messageResource) {
        List<SendTrendsStatisticDto> sendTrendsStatisticDtos = new ArrayList<>();
        QueryWrapper<MsgQuantityStatisticsDo> msgQuantityStatisticsDoQueryWrapper = new QueryWrapper<>();
        if(HOUR.equals(timeType)){
            if(isAccountDictCode){
                msgQuantityStatisticsDoQueryWrapper.select("DATE_FORMAT(send_time,'%H:%i') AS sendTime,account_dict_code AS channelCode,sum(send_num) AS sum ");
                msgQuantityStatisticsDoQueryWrapper.eq("account_dict_code",channelCode);
            }else{
                msgQuantityStatisticsDoQueryWrapper.select("DATE_FORMAT(send_time,'%H:%i') AS sendTime,operator_code AS channelCode,sum(send_num) AS sum ");
                msgQuantityStatisticsDoQueryWrapper.eq("operator_code",channelCode);
            }

        }else{
            if(isAccountDictCode){
                msgQuantityStatisticsDoQueryWrapper.select("DATE_FORMAT(send_time,'%Y-%m-%d') AS sendTime,account_dict_code AS channelCode,sum(send_num) AS sum ");
                msgQuantityStatisticsDoQueryWrapper.eq("account_dict_code",channelCode);
            }else{
                msgQuantityStatisticsDoQueryWrapper.select("DATE_FORMAT(send_time,'%Y-%m-%d') AS sendTime,operator_code AS channelCode,sum(send_num) AS sum ");
                msgQuantityStatisticsDoQueryWrapper.eq("operator_code",channelCode);
            }

        }
        msgQuantityStatisticsDoQueryWrapper.eq("account_type",accountType);
        msgQuantityStatisticsDoQueryWrapper.ge("send_time",startDate);
        if (endDate != null) {//结束时间
            msgQuantityStatisticsDoQueryWrapper.le("send_time", endDate);
        }
        if(CollectionUtils.isNotEmpty(customerIds)){
            msgQuantityStatisticsDoQueryWrapper.in("creator",customerIds);
        }
        if(messageResource!=null){
            msgQuantityStatisticsDoQueryWrapper.in("message_resource",messageResource);
        }
        msgQuantityStatisticsDoQueryWrapper.groupBy("sendTime");
        List<Map<String, Object>> maps = msgQuantityStatisticDao.selectMaps(msgQuantityStatisticsDoQueryWrapper);
        if(CollectionUtils.isNotEmpty(maps)){
            for(Map<String, Object> item : maps){
                if(item !=null){
                    SendTrendsStatisticDto sendTrendsStatisticDto = new SendTrendsStatisticDto();
                    sendTrendsStatisticDto.setSendTime(item.get("sendTime")+"");
                    sendTrendsStatisticDto.setSendSum(Long.valueOf(item.get("sum")+""));
                    sendTrendsStatisticDto.setAccountDictCode(item.get("channelCode")+"");
                    sendTrendsStatisticDtos.add(sendTrendsStatisticDto);
                }
            }
        }
        return sendTrendsStatisticDtos;
    }

    private List<SendTrendsStatisticDto> querySendTrends(Date startDate, Date endDate, String timeType, String accountType, List<String> customerIds,List<Integer> messageResource) {
        List<SendTrendsStatisticDto> sendTrendsStatisticDtos = new ArrayList<>();
        QueryWrapper<MsgQuantityStatisticsDo> msgQuantityStatisticsDoQueryWrapper = new QueryWrapper<>();
        if(HOUR.equals(timeType)){
            if(MSG_5G.equals(accountType)){
                msgQuantityStatisticsDoQueryWrapper.select("DATE_FORMAT(send_time,'%H:%i') AS sendTime,operator_code AS accountDictCode,sum(send_num) AS sum ");
            }else{
                msgQuantityStatisticsDoQueryWrapper.select("DATE_FORMAT(send_time,'%H:%i') AS sendTime,account_dict_code AS accountDictCode,sum(send_num) AS sum ");
            }
        }else{
            if(MSG_5G.equals(accountType)){
                msgQuantityStatisticsDoQueryWrapper.select("DATE_FORMAT(send_time,'%Y-%m-%d') AS sendTime,operator_code AS accountDictCode,sum(send_num) AS sum ");
            }else{
                msgQuantityStatisticsDoQueryWrapper.select("DATE_FORMAT(send_time,'%Y-%m-%d') AS sendTime,account_dict_code AS accountDictCode,sum(send_num) AS sum ");
            }
        }
        msgQuantityStatisticsDoQueryWrapper.eq("account_type",accountType);
        msgQuantityStatisticsDoQueryWrapper.ge("send_time",startDate);
        msgQuantityStatisticsDoQueryWrapper.in("message_resource",messageResource);
        if (endDate != null) {//结束时间
            msgQuantityStatisticsDoQueryWrapper.le("send_time", endDate);
        }
        if(CollectionUtils.isNotEmpty(customerIds)){
            msgQuantityStatisticsDoQueryWrapper.in("creator",customerIds);
        }
        msgQuantityStatisticsDoQueryWrapper.groupBy("sendTime,accountDictCode");

        List<Map<String, Object>> maps = msgQuantityStatisticDao.selectMaps(msgQuantityStatisticsDoQueryWrapper);
        if(CollectionUtils.isNotEmpty(maps)){
            for(Map<String, Object> item : maps){
                if(item != null){
                    SendTrendsStatisticDto sendTrendsStatisticDto = new SendTrendsStatisticDto();
                    sendTrendsStatisticDto.setSendTime(item.get("sendTime")+"");
                    sendTrendsStatisticDto.setAccountDictCode(item.get("accountDictCode")+"");
                    sendTrendsStatisticDto.setSendSum(Long.valueOf(item.get("sum")+""));
                    sendTrendsStatisticDtos.add(sendTrendsStatisticDto);
                }
            }
        }
        return sendTrendsStatisticDtos;
    }

    private List<SendTrendsStatisticDto> querySendTrendsByAccountId(Date startDate, Date endDate, String timeType, String accountId, String accountType,List<Integer> messageResource) {
        List<SendTrendsStatisticDto> sendTrendsStatisticDtos = new ArrayList<>();
        QueryWrapper<MsgQuantityStatisticsDo> msgQuantityStatisticsDoQueryWrapper = new QueryWrapper<>();
        if(HOUR.equals(timeType)){
            msgQuantityStatisticsDoQueryWrapper.select("DATE_FORMAT(send_time,'%H:%i') AS sendTime," +
                    "sum(send_num) AS sum ");
        }else{
            msgQuantityStatisticsDoQueryWrapper.select("DATE_FORMAT(send_time,'%Y-%m-%d') AS sendTime," +
                    "sum(send_num) AS sum ");
        }
        msgQuantityStatisticsDoQueryWrapper.eq("account_type",accountType);
        msgQuantityStatisticsDoQueryWrapper.eq("account_id",accountId);
        msgQuantityStatisticsDoQueryWrapper.ge("send_time",startDate);
        if (endDate != null) {//结束时间
            msgQuantityStatisticsDoQueryWrapper.le("send_time", endDate);
        }
        msgQuantityStatisticsDoQueryWrapper.in("message_resource",messageResource);
        msgQuantityStatisticsDoQueryWrapper.groupBy("sendTime");
        List<Map<String, Object>> maps = msgQuantityStatisticDao.selectMaps(msgQuantityStatisticsDoQueryWrapper);
        if(CollectionUtils.isNotEmpty(maps)){
            for(Map<String, Object> item : maps){
                if(item !=null){
                    SendTrendsStatisticDto sendTrendsStatisticDto = new SendTrendsStatisticDto();
                    sendTrendsStatisticDto.setSendTime(item.get("activeTime")+"");
                    sendTrendsStatisticDto.setSendSum(Long.valueOf(item.get("sum")+""));
                    sendTrendsStatisticDtos.add(sendTrendsStatisticDto);
                }
            }
        }
        return sendTrendsStatisticDtos;
    }


    private YesterdayOverviewResp yesterdayOverviewForAccountType(String accountType) {
        YesterdayOverviewResp resp = new YesterdayOverviewResp();
        Date yesterday = DateUtils.obtainDayTime(END,DateUtils.obtainDate(DateUtils.getYesterday(new Date())));
        Date dayBeforeYesterday = DateUtils.obtainDayTime(START,DateUtils.obtainDate(DateUtils.getYesterday(yesterday)));

        //1、查询该用户下所有账号和用户
        List<String> accountList = queryAccountList(accountType);
        List<String> customerIdList = queryCustomerList(SessionContextUtil.getUser().getUserId());
        if(!isAdmin() && CollectionUtils.isEmpty(customerIdList))
            return resp;
        //活跃用户
        ActiveStatisticItem activeUserItem = obtainActiveStatisticUserOrAccount(true,dayBeforeYesterday,yesterday,accountType,customerIdList);
        //活跃账号
        ActiveStatisticItem activeAccountItem = obtainActiveStatisticUserOrAccount(false,dayBeforeYesterday,yesterday,accountType,accountList);
        //活跃发送量
        MsgSendAmountItem msgSendAmountItem = obtainMsgSendStatistic(dayBeforeYesterday,yesterday,accountType,customerIdList);

        resp.setActiveUserItem(activeUserItem);
        resp.setActiveAccountItem(activeAccountItem);
        resp.setMsgSendAmountItem(msgSendAmountItem);
        return resp;
    }



    private MsgSendAmountItem obtainMsgSendStatistic(Date dayBeforeYesterday, Date yesterday, String accountType, List<String> customerIdList) {
        MsgSendAmountItem msgSendAmountItem = new MsgSendAmountItem();
        //获取渠道消息
        Map<String,String> channelMap = MSG_5G.equals(accountType)? obtainOperatorMap(null):obtainAccountDictInfoMap(null);
        //查询昨天和前天的发送时间
        List<SendTrendsStatisticDto> sendTrendsStatisticDtos = (!isAdmin() && CollectionUtils.isEmpty(customerIdList)) ? new ArrayList<>() : queryYesterdayChannelMsgSendData(dayBeforeYesterday,yesterday,accountType,customerIdList);//msgQuantityStatisticDao.queryYesterdayChannelMsgSendData(DateUtils.obtainDate(dayBeforeYesterday +" 00:00:00"),DateUtils.obtainDate(yesterday +" 23:59:59"),accountList);
        //Map<"通道ID",Map<"时间"，“数量”>>
        Map<String,Map<String,Long>> chanelMap = obtainMap(sendTrendsStatisticDtos);
        List<ChannelStatisticsItem> channelStatistics = new ArrayList<>();
        ChannelStatisticsItem channelStatisticsInfo;
        long yesterdayDataAll = 0;
        long dayBeforeYesterdayDataAll = 0;
        for(Map.Entry<String,Map<String,Long>> entry : chanelMap.entrySet()){
            if(channelMap.containsKey(entry.getKey())){
                channelStatisticsInfo = new ChannelStatisticsItem();
                channelStatisticsInfo.setChannelName(channelMap.get(entry.getKey()));
                Map<String,Long> dataMap = entry.getValue();
                long yesterdaySendData = dataMap.getOrDefault(DateUtils.obtainDateStr(yesterday,"yyyy-MM-dd"),0L);
                long dayBeforeYesterdaySendData = dataMap.getOrDefault(DateUtils.obtainDateStr(dayBeforeYesterday,"yyyy-MM-dd"),0L);
                channelStatisticsInfo.setMsgSendAmount(yesterdaySendData);
                channelStatisticsInfo.setMsgSendAmountDiff(yesterdaySendData-dayBeforeYesterdaySendData);
                channelStatisticsInfo.setMsgSendAmountDiffPer(calculatePercentage(yesterdaySendData,dayBeforeYesterdaySendData));
                channelStatistics.add(channelStatisticsInfo);
                yesterdayDataAll += yesterdaySendData;
                dayBeforeYesterdayDataAll += dayBeforeYesterdaySendData;
            }
        }

        msgSendAmountItem.setMsgSendAmount(yesterdayDataAll);
        msgSendAmountItem.setMsgSendAmountDiff(yesterdayDataAll- dayBeforeYesterdayDataAll);
        msgSendAmountItem.setMsgSendAmountDiffPer(calculatePercentage(yesterdayDataAll,dayBeforeYesterdayDataAll));
        msgSendAmountItem.setChannelStatistics(channelStatistics);
        return msgSendAmountItem;
    }

    private ActiveStatisticItem obtainActiveStatisticUserOrAccount(boolean isUserStatistic,Date dayBeforeYesterday, Date yesterday, String accountType,List<String> statisticTypeList) {
        ActiveStatisticItem activeAccountItem = new ActiveStatisticItem();
        //查询昨天和前天的活跃用户数
        if(isAdmin() || CollectionUtils.isNotEmpty(statisticTypeList)){
            List<AccountStatisticsInfo> accountSumList = queryYesterdayActiveStatisticData(isUserStatistic,dayBeforeYesterday,yesterday,accountType,statisticTypeList);
            Map<String,Long> accountSumMap = new HashMap<>();
            accountSumList.forEach(i->accountSumMap.put(i.getActiveTime(),i.getSum()));
            long yesterdayData = accountSumMap.getOrDefault(DateUtils.obtainDateStr(yesterday,"yyyy-MM-dd"), 0L);
            long dayBeforeYesterdayData = accountSumMap.getOrDefault(DateUtils.obtainDateStr(dayBeforeYesterday,"yyyy-MM-dd"), 0L);
            activeAccountItem.setActiveAmount(yesterdayData);
            activeAccountItem.setActiveAmountDiff(yesterdayData-dayBeforeYesterdayData);
            activeAccountItem.setActiveAmountDiffPer(calculatePercentage(yesterdayData,dayBeforeYesterdayData));
        }else{
            activeAccountItem.setActiveAmount(0L);
            activeAccountItem.setActiveAmountDiff(0L);
            activeAccountItem.setActiveAmountDiffPer(new BigDecimal(0L));
        }
        return activeAccountItem;
    }

    private List<AccountStatisticsInfo> queryYesterdayActiveStatisticData(boolean isUserStatistic, Date dayBeforeYesterday, Date yesterday, String accountType, List<String> statisticTypeList) {
        List<AccountStatisticsInfo> accountStatisticsInfoList = new ArrayList<>();
        QueryWrapper<MsgQuantityStatisticsDo> msgQuantityStatisticsDoQueryWrapper = new QueryWrapper<>();
        if(isUserStatistic){
            msgQuantityStatisticsDoQueryWrapper.select("DATE_FORMAT(send_time,'%Y-%m-%d') AS activeTime," +
                    "            count(Distinct creator) AS sum");
            if (CollectionUtils.isNotEmpty(statisticTypeList)) {
                msgQuantityStatisticsDoQueryWrapper.in("creator", statisticTypeList);
            }
        }else{
            msgQuantityStatisticsDoQueryWrapper.select("DATE_FORMAT(send_time,'%Y-%m-%d') AS activeTime," +
                    "            count(Distinct account_id) AS sum");
            if(CollectionUtils.isNotEmpty(statisticTypeList)){
                msgQuantityStatisticsDoQueryWrapper.in("account_id",statisticTypeList);
            }
        }

        msgQuantityStatisticsDoQueryWrapper.ge("send_time", dayBeforeYesterday);
        msgQuantityStatisticsDoQueryWrapper.eq("account_type", accountType);
//        msgQuantityStatisticsDoQueryWrapper.eq("message_resource",MSG_SEND_PLAN);
        if (yesterday != null) {//结束时间
            msgQuantityStatisticsDoQueryWrapper.le("send_time", yesterday);
        }

        msgQuantityStatisticsDoQueryWrapper.groupBy("activeTime");
        List<Map<String, Object>> maps = msgQuantityStatisticDao.selectMaps(msgQuantityStatisticsDoQueryWrapper);
        if (CollectionUtils.isNotEmpty(maps)) {
            for (Map<String, Object> item : maps) {
                if(item != null){
                    AccountStatisticsInfo accountStatisticsInfo = new AccountStatisticsInfo();
                    accountStatisticsInfo.setActiveTime(item.get("activeTime") + "");
                    accountStatisticsInfo.setSum(Long.valueOf(item.get("sum")+""));
                    accountStatisticsInfoList.add(accountStatisticsInfo);
                }
            }
        }
        return accountStatisticsInfoList;
    }

    private List<SendTrendsStatisticDto> queryYesterdayChannelMsgSendData(Date startDate, Date endDate, String accountType, List<String> customerIdList) {
        List<SendTrendsStatisticDto> sendTrendsStatisticDtos = new ArrayList<>();
        QueryWrapper<MsgQuantityStatisticsDo> msgQuantityStatisticsDoQueryWrapper = new QueryWrapper<>();
        if(MSG_5G.equals(accountType)){
            msgQuantityStatisticsDoQueryWrapper.select("operator_code as accountDictCode," +
                    "            DATE_FORMAT(send_time,'%Y-%m-%d') AS sendTime," +
                    "            sum(send_num) AS sendSum ");
        }else{
            msgQuantityStatisticsDoQueryWrapper.select("account_dict_code as accountDictCode," +
                    "            DATE_FORMAT(send_time,'%Y-%m-%d') AS sendTime," +
                    "            sum(send_num) AS sendSum ");
        }
        msgQuantityStatisticsDoQueryWrapper.ge("send_time",startDate);
        msgQuantityStatisticsDoQueryWrapper.eq("account_type",accountType);
        msgQuantityStatisticsDoQueryWrapper.in("message_resource",Arrays.asList(MSG_SEND_PLAN,MSG_SEND_TEST,MSG_SEND_DEVELOPER,MSG_SEND_SUBSCRIBE,MSG_SEND_SIGN));
        if (endDate != null) {//结束时间
            msgQuantityStatisticsDoQueryWrapper.le("send_time", endDate);
        }
        if(CollectionUtils.isNotEmpty(customerIdList)){
            msgQuantityStatisticsDoQueryWrapper.in("creator",customerIdList);
        }
        msgQuantityStatisticsDoQueryWrapper.groupBy("accountDictCode,sendTime");
        List<Map<String, Object>> maps = msgQuantityStatisticDao.selectMaps(msgQuantityStatisticsDoQueryWrapper);
        if(CollectionUtils.isNotEmpty(maps)){
            for(Map<String, Object> item : maps){
                if(item != null){
                    SendTrendsStatisticDto sendTrendsStatisticDto = new SendTrendsStatisticDto();
                    sendTrendsStatisticDto.setAccountDictCode(item.get("accountDictCode")+"");
                    sendTrendsStatisticDto.setSendTime(item.get("sendTime")+"");
                    sendTrendsStatisticDto.setSendSum(Long.valueOf(item.get("sendSum")+""));
                    sendTrendsStatisticDtos.add(sendTrendsStatisticDto);
                }
            }
        }
        return sendTrendsStatisticDtos;


    }



    /**
     * 查询改用户下的指定账号
     * @return 结果
     */
    private List<String> queryAccountList(String accountType) {
        List<String> accountList = new ArrayList<>();
        if(!isAdmin()){//如果是管理员则查询所有账号，返回null，查询所有。
            String userId = SessionContextUtil.getUser().getUserId();
            if(MSG_5G.equals(accountType)){
                accountList =  csp5GSmsAccountApi.queryAccountIdListByCspIds(Arrays.asList(userId));
                if(!CollectionUtils.isNotEmpty(accountList)){//按照csp用户查询
                    accountList = csp5GSmsAccountApi.queryAccountIdListByCustomerIds(Arrays.asList(userId));
                }
            }else if(MSG_MEDIA.equals(accountType)){
                List<String> userIdList = new ArrayList<>();
                userIdList.add(userId);
                CspVideoSmsAccountQueryAccountIdReq req = new CspVideoSmsAccountQueryAccountIdReq();
                req.setUserIds(userIdList);
                accountList =  cspVideoSmsAccountApi.queryAccountIdListByCspUserId(req);
                if(!CollectionUtils.isNotEmpty(accountList)){//按照csp用户查询
                    CspVideoSmsAccountQueryAccountIdReq queryAccountIdReq = new CspVideoSmsAccountQueryAccountIdReq();
                    queryAccountIdReq.setUserIds(userIdList);
                    accountList = cspVideoSmsAccountApi.queryAccountIdListByUserList(queryAccountIdReq);
                }
            }else{
                accountList =  cspShortSmsAccountApi.queryAccountIdListByCspIds(Arrays.asList(userId));
                if(!CollectionUtils.isNotEmpty(accountList)){//按照csp用户查询
                    accountList = cspShortSmsAccountApi.queryAccountIdListByCustomerIds(Arrays.asList(userId));
                }
            }
        }

        return accountList;
    }


    /**
     * 计算日环比
     * @param yesterdayData 昨天时间
     * @param dayBeforeYesterdayData 前天时间
     * @return 日环比
     */
    private BigDecimal calculatePercentage(Long yesterdayData, Long dayBeforeYesterdayData) {
        BigDecimal percent = BigDecimal.valueOf(yesterdayData-dayBeforeYesterdayData).divide((dayBeforeYesterdayData == 0) ? BigDecimal.valueOf(1) : BigDecimal.valueOf(dayBeforeYesterdayData),4,BigDecimal.ROUND_HALF_UP );
        return percent.multiply(BigDecimal.valueOf(100)).setScale(2,BigDecimal.ROUND_HALF_UP);

    }

    private Map<String, String> obtainAccountDictInfoMap(String accountDictCode) {
        Map<String, String> AccountDictMap = new HashMap<>();
        CspDictReq req = new CspDictReq();
        req.setDictType(1);
        req.setPageNo(1);
        req.setPageSize(100000);
        PageResult<CspDictResp> channelPage = cspDictApi.queryList(req);
        List<CspDictResp> cspDicts = channelPage.getList();
        cspDicts.forEach(i-> AccountDictMap.put(i.getDictCode(),i.getDictValue()));
        if(CollectionUtils.isNotEmpty(cspDicts) && (!Strings.isNullOrEmpty(accountDictCode))){
                if(AccountDictMap.containsKey(accountDictCode)){
                    Map<String, String> onlyMap = new HashMap<>();
                    onlyMap.put(accountDictCode,AccountDictMap.get(accountDictCode));
                    return onlyMap;
                }else{
                    return new HashMap<>();
                }

        }
        return AccountDictMap;
    }

    private Map<String, String> obtainOperatorMap(String operatorCode) {
        Map<String, String> operatorMap = new HashMap<>();
        operatorMap.put("2","移动");
        operatorMap.put("1","联通");
        operatorMap.put("3","电信");
        operatorMap.put("0","硬核桃");
        if(StringUtils.isNotEmpty(operatorCode)){
            if(operatorMap.containsKey(operatorCode)){
                Map<String, String> onlyMap = new HashMap<>();
                onlyMap.put(operatorCode,operatorMap.get(operatorCode));
                return onlyMap;
            }else{
                return new HashMap<>();
            }
        }
        return operatorMap;
    }


    private Map<String, Map<String, Long>> obtainMap(List<SendTrendsStatisticDto> sendTrendsStatisticDtos) {
        Map<String,Map<String,Long>> chanelMap = new HashMap<>();
        if(CollectionUtils.isNotEmpty(sendTrendsStatisticDtos)){
            for(SendTrendsStatisticDto item : sendTrendsStatisticDtos){
                if(chanelMap.containsKey(item.getAccountDictCode())){
                    Map<String,Long> dateMap = chanelMap.get(item.getAccountDictCode());
                    dateMap.put(item.getSendTime(),item.getSendSum());
                }else{
                    Map<String,Long> dateMap = new HashMap<>();
                    dateMap.put(item.getSendTime(),item.getSendSum());
                    chanelMap.put(item.getAccountDictCode(),dateMap);
                }
            }
        }
        return chanelMap;

    }

    private boolean isAdmin(){
        String userId = SessionContextUtil.getUser().getUserId();
        if(StringUtils.isNotEmpty(userId)){
            if(userId.length() == 15){//用户ID
                return false;
            }else{
                CspInfoResp cspInfo = cspCustomerApi.getCspInfo(userId);
                return Strings.isNullOrEmpty(cspInfo.getCspId());
            }
        }
        return false;
    }

    private List<String> queryCustomerList(String userId) {
        List<String> customerIds = new ArrayList<>();
        if(StringUtils.isNotEmpty(userId)){
            if(userId.length() == 15){//用户ID
                customerIds.add(userId);
            }else{
                CspInfoResp cspInfo = cspCustomerApi.getCspInfo(userId);
                String cspId = cspInfo.getCspId();
                if(!Strings.isNullOrEmpty(cspId)){//CSP
                    customerIds = cspCustomerApi.queryCustomerIdsByCspId(cspId);
                }
            }
        }
        return customerIds;
    }
}
