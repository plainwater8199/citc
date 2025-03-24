package com.citc.nce.auth.csp.home.service.impl;

import com.citc.nce.auth.accountmanagement.service.AccountManagementService;
import com.citc.nce.auth.accountmanagement.vo.AccountManagementResp;
import com.citc.nce.auth.constant.csp.common.CSPOperatorCodeEnum;
import com.citc.nce.auth.csp.csp.service.CspService;
import com.citc.nce.auth.csp.customer.dao.CspCustomerDao;
import com.citc.nce.auth.csp.customer.entity.CspCustomerDo;
import com.citc.nce.auth.csp.home.service.HomePageService;
import com.citc.nce.auth.csp.home.vo.*;
import com.citc.nce.auth.csp.videoSms.account.service.CspVideoSmsAccountService;
import com.citc.nce.auth.utils.DateUtil;
import com.citc.nce.authcenter.csp.CspCustomerApi;
import com.citc.nce.authcenter.userLoginRecord.UserLoginRecordApi;
import com.citc.nce.authcenter.userLoginRecord.vo.QueryUserLoginRecordReq;
import com.citc.nce.authcenter.userLoginRecord.vo.QueryUserLoginRecordResp;
import com.citc.nce.common.core.pojo.PageResult;
import com.citc.nce.common.util.SessionContextUtil;
import com.citc.nce.mybatis.core.query.LambdaQueryWrapperX;
import com.citc.nce.robot.RobotRecordApi;
import com.citc.nce.robot.vo.RobotRecordPageReq;
import com.citc.nce.robot.vo.RobotRecordStatisticResp;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>csp-首页</p>
 *
 * @Author zy.qiu
 * @CreatedTime 2023/2/7 14:51
 */
@Service
@Slf4j
public class HomePageServiceImpl implements HomePageService {


    @Autowired
    private CspCustomerDao cspCustomerDao;

    @Autowired
    private UserLoginRecordApi userLoginRecordApi;

    @Autowired
    private RobotRecordApi robotRecordApi;

    @Autowired
    private AccountManagementService accountManagementService;

    @Autowired
    private CspCustomerApi cspCustomerApi;

    @Autowired
    private CspService cspService;

    @Autowired
    private CspVideoSmsAccountService videoSmsAccountService;


    private static final String START_TIME_FORMAT = "yyyy-MM-dd 00:00:00";
    private static final String END_TIME_FORMAT = "yyyy-MM-dd 23:59:59";
    private static final String TIME_FORMAT = "yyyy-MM-dd";
    private static final String HOUR_TIME_FORMAT = "yyyy-MM-dd HH:00:00";

    private static final String ADD_ZERO_TIME = " 00:00:00";
    private static final String ADD_END_TIME = " 23:59:59";

    @Override
    public HomeTotalOverviewResp getTotalOverview() {
        HomeTotalOverviewResp total = new HomeTotalOverviewResp();
        // 客户总量
        String sTime = getsTime(0);
        String endTime = sTime + ADD_END_TIME;
        String cspId = cspService.obtainCspId(SessionContextUtil.getUser().getUserId());
        total.setActiveUserCount(cspCustomerApi.countCustomer(cspId, endTime).intValue());
        // chatbot总量
        List<AccountManagementResp> listByCreators = accountManagementService.getListByCreators(Collections.singletonList(cspId));
        // 硬核桃不纳入统计
        if (CollectionUtils.isNotEmpty(listByCreators)) {
            // 去除运营商是硬核桃的
            List<AccountManagementResp> collect = listByCreators.stream().filter(
                    amr -> !Objects.equals(CSPOperatorCodeEnum.DEFAULT.getCode(), amr.getAccountTypeCode())).collect(Collectors.toList());
            total.setActiveChatbotCount(collect.size());
        }
        total.setMediasSmsAccountCount(videoSmsAccountService.countVideoSmsAccount(cspId).intValue());
        return total;
    }

    @Override
    public HomeYesterdayOverviewResp getYesterDayOverview() {
        // 活跃客户
        HomeYesterdayOverviewResp resp = dealActiveUser(false);
        // 活跃chatbot
        List<String> userIdList = getUserIdList();
        if (CollectionUtils.isNotEmpty(userIdList)) {
            List<RobotRecordStatisticResp> yesterday = getRobotRecord(-1, userIdList, false);
            if (CollectionUtils.isNotEmpty(yesterday)) {
                List<RobotRecordStatisticResp> beforeYesterday = getRobotRecord(-2, userIdList, false);
                distinct(resp, yesterday, beforeYesterday);
                // 发送量
                sendCount(resp, yesterday, beforeYesterday);
            }
        }
        return resp;
    }

    private static void distinct(HomeYesterdayOverviewResp resp, List<RobotRecordStatisticResp> yesterday, List<RobotRecordStatisticResp> beforeYesterday) {
        // 根据accountId去重
        // 一天用户的活跃只算一次
        ArrayList<RobotRecordStatisticResp> yesterdayCollect = yesterday.stream().collect(Collectors.collectingAndThen(
                Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(RobotRecordStatisticResp::getAccount))), ArrayList::new
        ));
        ArrayList<RobotRecordStatisticResp> beforeYesterdayCollect;
        if (CollectionUtils.isNotEmpty(beforeYesterday)) {
            beforeYesterdayCollect = beforeYesterday.stream().collect(Collectors.collectingAndThen(
                    Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(RobotRecordStatisticResp::getAccount))), ArrayList::new
            ));
            int differ = yesterdayCollect.size() - beforeYesterdayCollect.size();
            resp.setActiveChatbotDifferences(new BigDecimal(differ));
            BigDecimal percent;
            if (beforeYesterdayCollect.size() > 0) {
                percent = BigDecimal.valueOf(differ).divide(BigDecimal.valueOf(beforeYesterdayCollect.size()), 4, BigDecimal.ROUND_HALF_UP);
            } else {
                percent = BigDecimal.valueOf(differ).divide(BigDecimal.valueOf(1), 4, BigDecimal.ROUND_HALF_UP);
            }
            resp.setActiveChatbotDifferencesPercent(percent.multiply(BigDecimal.valueOf(100)));
        } else {
            int differ = yesterdayCollect.size();
            resp.setActiveChatbotDifferences(new BigDecimal(differ));
            BigDecimal percent;
            percent = BigDecimal.valueOf(differ).divide(BigDecimal.valueOf(1), 4, BigDecimal.ROUND_HALF_UP);
            resp.setActiveChatbotDifferencesPercent(percent.multiply(BigDecimal.valueOf(100)));
        }
        resp.setActiveChatbotCount(new BigDecimal(yesterdayCollect.size()));
    }

    @Nullable
    private List<String> getUserIdList() {
        // 获取当前CSP的账号创建的用户

        LambdaQueryWrapperX<CspCustomerDo> queryWrapperX = new LambdaQueryWrapperX<>();
        String userId = SessionContextUtil.getUser().getUserId();
        queryWrapperX.eq(CspCustomerDo::getCspId, userId);
        List<CspCustomerDo> manageDos = cspCustomerDao.selectList(queryWrapperX);
        // 获取CustomerId
        List<String> userIdList = null;
        if (CollectionUtils.isNotEmpty(manageDos)) {
            userIdList = new ArrayList<>();
            for (CspCustomerDo identificationDo :
                    manageDos) {
                userIdList.add(identificationDo.getCustomerId());
            }
        }
        return userIdList;
    }

    private static void sendCount(HomeYesterdayOverviewResp resp, List<RobotRecordStatisticResp> yesterday, List<RobotRecordStatisticResp> beforeYesterday) {
        // 根据运营商分组 0：缺省(硬核桃)，1：联通，2：移动，3：电信
        Map<Integer, Long> yesterdayMap = yesterday.stream().collect(Collectors.groupingBy(RobotRecordStatisticResp::getChannelType, Collectors.counting()));
        Long allDiffer;
        Long totalSendCount = 0L;
        Long beforeSendCount = 0L;
        Long cmccDiffer;
        Long cuncDiffer;
        Long ctDiffer;
        BigDecimal allPercent;
        BigDecimal cmccPercent;
        BigDecimal cuncPercent;
        BigDecimal ctPercent;
        Map<Integer, Long> yesterdayCountMap = getCountMap(yesterdayMap, totalSendCount);
        resp.setSendCount(getCountValue(yesterdayCountMap, CSPOperatorCodeEnum.ALL.getCode()));
        resp.setCuncSendCount(getCountValue(yesterdayCountMap, CSPOperatorCodeEnum.CUNC.getCode()));
        resp.setCmccSendCount(getCountValue(yesterdayCountMap, CSPOperatorCodeEnum.CMCC.getCode()));
        resp.setCtSendCount(getCountValue(yesterdayCountMap, CSPOperatorCodeEnum.CT.getCode()));
        if (CollectionUtils.isNotEmpty(beforeYesterday)) {
            Map<Integer, Long> beforeYesterdayMap = beforeYesterday.stream().collect(Collectors.groupingBy(RobotRecordStatisticResp::getChannelType, Collectors.counting()));
            Map<Integer, Long> beforeCountMap = getCountMap(beforeYesterdayMap, beforeSendCount);
            allDiffer = yesterdayCountMap.get(CSPOperatorCodeEnum.ALL.getCode()) - beforeCountMap.get(CSPOperatorCodeEnum.ALL.getCode());
            cuncDiffer = yesterdayCountMap.get(CSPOperatorCodeEnum.CUNC.getCode()) - beforeCountMap.get(CSPOperatorCodeEnum.CUNC.getCode());
            cmccDiffer = yesterdayCountMap.get(CSPOperatorCodeEnum.CMCC.getCode()) - beforeCountMap.get(CSPOperatorCodeEnum.CMCC.getCode());
            ctDiffer = yesterdayCountMap.get(CSPOperatorCodeEnum.CT.getCode()) - beforeCountMap.get(CSPOperatorCodeEnum.CT.getCode());
            allPercent = getPercent(allDiffer, beforeCountMap.get(CSPOperatorCodeEnum.ALL.getCode()));
            cuncPercent = getPercent(cuncDiffer, beforeCountMap.get(CSPOperatorCodeEnum.CUNC.getCode()));
            cmccPercent = getPercent(cmccDiffer, beforeCountMap.get(CSPOperatorCodeEnum.CMCC.getCode()));
            ctPercent = getPercent(ctDiffer, beforeCountMap.get(CSPOperatorCodeEnum.CT.getCode()));
        } else {
            allDiffer = yesterdayCountMap.get(CSPOperatorCodeEnum.ALL.getCode());
            cuncDiffer = yesterdayCountMap.get(CSPOperatorCodeEnum.CUNC.getCode());
            cmccDiffer = yesterdayCountMap.get(CSPOperatorCodeEnum.CMCC.getCode());
            ctDiffer = yesterdayCountMap.get(CSPOperatorCodeEnum.CT.getCode());
            allPercent = getPercent(allDiffer, 0L);
            cuncPercent = getPercent(cuncDiffer, 0L);
            cmccPercent = getPercent(cmccDiffer, 0L);
            ctPercent = getPercent(ctDiffer, 0L);
        }
        resp.setSendDifferences(new BigDecimal(allDiffer));
        resp.setCuncSendDifferences(new BigDecimal(cuncDiffer));
        resp.setCmccSendDifferences(new BigDecimal(cmccDiffer));
        resp.setCtSendDifferences(new BigDecimal(ctDiffer));
        resp.setSendDifferencesPercent(allPercent);
        resp.setCuncSendDifferencesPercent(cuncPercent);
        resp.setCmccSendDifferencesPercent(cmccPercent);
        resp.setCtSendDifferencesPercent(ctPercent);
    }

    @NotNull
    private static BigDecimal getCountValue(Map<Integer, Long> map, Integer code) {
        BigDecimal res = new BigDecimal(0);
        if (null != map.get(code)) {
            res = new BigDecimal(map.get(code));
        }
        return res;
    }

    private static BigDecimal getPercent(Long allDiffer, Long count) {
        BigDecimal percent = new BigDecimal(0);
        if (count > 0) {
            percent = BigDecimal.valueOf(allDiffer).divide(BigDecimal.valueOf(count), 4, BigDecimal.ROUND_HALF_UP);
        } else {
            percent = BigDecimal.valueOf(allDiffer).divide(BigDecimal.valueOf(1), 4, BigDecimal.ROUND_HALF_UP);
        }
        percent = percent.multiply(BigDecimal.valueOf(100));
        return percent;
    }

    private static Map<Integer, Long> getCountMap(Map<Integer, Long> map, Long totalSendCount) {
        Map<Integer, Long> resMap = new HashMap<>();
        resMap.put(CSPOperatorCodeEnum.CUNC.getCode(), 0L);
        resMap.put(CSPOperatorCodeEnum.CMCC.getCode(), 0L);
        resMap.put(CSPOperatorCodeEnum.CT.getCode(), 0L);
        for (Map.Entry<Integer, Long> entry : map.entrySet()
        ) {
            // 总数
            totalSendCount += entry.getValue();
            switch (entry.getKey()) {
                case 1:
                    // 联通
                    resMap.put(CSPOperatorCodeEnum.CUNC.getCode(), (null != entry.getValue() && entry.getValue() > 0) ? entry.getValue() : 0L);
                    break;
                case 2:
                    // 移动
                    resMap.put(CSPOperatorCodeEnum.CMCC.getCode(), (null != entry.getValue() && entry.getValue() > 0) ? entry.getValue() : 0L);
                    break;
                case 3:
                    // 电信
                    resMap.put(CSPOperatorCodeEnum.CT.getCode(), (null != entry.getValue() && entry.getValue() > 0) ? entry.getValue() : 0L);
                    break;
                default:
            }
        }
        resMap.put(CSPOperatorCodeEnum.ALL.getCode(), totalSendCount);
        return resMap;
    }

    List<RobotRecordStatisticResp> getRobotRecord(int dayModify, List<String> userIdList, boolean isTotal) {
        RobotRecordPageReq req = new RobotRecordPageReq();
        req.setPageSize(-1);
        req.setUserIdList(userIdList);
        String sTime = getsTime(dayModify);
        String startTime = sTime + ADD_ZERO_TIME;
        String endTime = sTime + ADD_END_TIME;
        req.setEndTime(endTime);
        if (!isTotal) {
            req.setStartTime(startTime);
        }
        PageResult<RobotRecordStatisticResp> recordByTime = robotRecordApi.getRobotRecordStatisticByTime(req);
        if (CollectionUtils.isNotEmpty(recordByTime.getList())) {
            return recordByTime.getList();
        }
        return null;
    }

    private HomeYesterdayOverviewResp dealActiveUser(boolean isTotal) {
        HomeYesterdayOverviewResp resp = new HomeYesterdayOverviewResp();
        List<QueryUserLoginRecordResp> yesterday = getUserLoginRecord(-1, isTotal);
        List<QueryUserLoginRecordResp> beforeYesterday = new ArrayList<>();
        if (!isTotal) {
            beforeYesterday = getUserLoginRecord(-2, isTotal);
        }
        // 根据 userId进行数据去重
        // 一天用户的登录只算一次
        ArrayList<QueryUserLoginRecordResp> yesterdayCollect = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(yesterday)) {
            yesterdayCollect = yesterday.stream().collect(Collectors.collectingAndThen(
                    Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(QueryUserLoginRecordResp::getUserId))), ArrayList::new
            ));
        }
        if (!isTotal) {
            ArrayList<QueryUserLoginRecordResp> beforeYesterdayCollect = new ArrayList<>();
            if (CollectionUtils.isNotEmpty(beforeYesterday)) {
                beforeYesterdayCollect = beforeYesterday.stream().collect(Collectors.collectingAndThen(
                        Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(QueryUserLoginRecordResp::getUserId))), ArrayList::new
                ));
            }
            int differ = yesterdayCollect.size() - beforeYesterdayCollect.size();

            resp.setActiveUserDifferences(new BigDecimal(differ));
            BigDecimal percent;
            if (beforeYesterdayCollect.size() > 0) {
                percent = BigDecimal.valueOf(differ).divide(BigDecimal.valueOf(beforeYesterdayCollect.size()), 4, BigDecimal.ROUND_HALF_UP);
            } else {
                percent = BigDecimal.valueOf(differ).divide(BigDecimal.valueOf(1), 4, BigDecimal.ROUND_HALF_UP);
            }
            resp.setActiveUserDifferencesPercent(percent.multiply(BigDecimal.valueOf(100)));
        }
        resp.setActiveUserCount(new BigDecimal(yesterdayCollect.size()));
        return resp;
    }

    List<QueryUserLoginRecordResp> getUserLoginRecord(int dayModify, boolean isTotal) {
        QueryUserLoginRecordReq req = new QueryUserLoginRecordReq();
        String cspAccount = SessionContextUtil.getUser().getUserId();
        req.setCspAccount(cspAccount);
        req.setPageSize(-1);
        // 数据总览时不需要日期，查所有
        if (!isTotal) {
            String sTime = getsTime(dayModify);
            String startTime = sTime + ADD_ZERO_TIME;
            String endTime = sTime + ADD_END_TIME;
            req.setStartTime(startTime);
            req.setEndTime(endTime);
        }
        PageResult<QueryUserLoginRecordResp> recordRespPageResult = userLoginRecordApi.queryList(req);
        if (CollectionUtils.isNotEmpty(recordRespPageResult.getList())) {
            return recordRespPageResult.getList();
        }
        return null;
    }

    private static String getsTime(int dayModify) {
        SimpleDateFormat sdf = new SimpleDateFormat(TIME_FORMAT);
        String format = sdf.format(DateUtils.addDays(new Date(), dayModify));
        return format;
    }

    @Override
    public HomeLineChartResp activeLineChart(HomeLineChartReq req) {
        if (StringUtils.isEmpty(req.getCreator())) {
            req.setCreator(SessionContextUtil.getUser().getUserId());
        }
        HomeLineChartResp resp = new HomeLineChartResp();

        List<String> times = null;
        int timeFlag = 1;
        // 活跃客户
        QueryUserLoginRecordReq queryUserLoginRecordReq = new QueryUserLoginRecordReq();
        queryUserLoginRecordReq.setCspAccount(req.getCreator());
        queryUserLoginRecordReq.setStartTime(req.getStartTime() + ADD_ZERO_TIME);
        queryUserLoginRecordReq.setEndTime(req.getEndTime() + ADD_END_TIME);
        queryUserLoginRecordReq.setPageSize(-1);
        List<QueryUserLoginRecordResp> recordList = new ArrayList<>();
        recordList = getLoginRecordResps(queryUserLoginRecordReq, recordList);
        // 活跃chatbot
        PageResult<RobotRecordStatisticResp> recordByTime = getRecordStatisticRespPageResult(req);
        List<RobotRecordStatisticResp> sendRecordList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(recordByTime.getList())) {
            sendRecordList = recordByTime.getList();
        }

        // 查一天
        if (req.getStartTime().equals(req.getEndTime())) {
            setRecordList(recordList, HOUR_TIME_FORMAT);
            setSendList(sendRecordList, HOUR_TIME_FORMAT);
            times = DateUtil.findHours(req.getStartTime() + " 0:00:00", req.getEndTime() + " 23:00:00");
            timeFlag = 1;
        } else {
            // 小于一个月
            int betweenDays = DateUtil.getBetweenDays(req.getStartTime(), req.getEndTime());
            setRecordList(recordList, START_TIME_FORMAT);
            setSendList(sendRecordList, START_TIME_FORMAT);
            if (betweenDays <= 30) {
                times = DateUtil.findEveryDay(req.getStartTime() + ADD_ZERO_TIME, req.getEndTime() + ADD_ZERO_TIME);
                timeFlag = 2;
                // 一个月以上
            } else {
                times = DateUtil.findWeeks(req.getStartTime() + ADD_ZERO_TIME, req.getEndTime() + ADD_ZERO_TIME);
                setWeekRecordList(recordList, START_TIME_FORMAT);
                setWeekSendList(sendRecordList, START_TIME_FORMAT);
                timeFlag = 3;
            }
        }
        List<QueryUserLoginRecordResp> noRepeatRecordList = recordList.stream().distinct().collect(Collectors.toList());
        HashMap<String, Integer> loginMap = putValueInMap(times, null, noRepeatRecordList);
        List<RobotRecordStatisticResp> noRepeatSendRecordList = sendRecordList.stream().distinct().collect(Collectors.toList());
        HashMap<String, Integer> recordMap = putValueInMap(times, noRepeatSendRecordList, null);
        ArrayList<LineChart> objects = getLineCharts(times, timeFlag, loginMap);
        ArrayList<LineChart> chatbots = getLineCharts(times, timeFlag, recordMap);
        resp.setUserLineChart(objects);
        resp.setChatbotLineChart(chatbots);
        resp.setDataType(Long.valueOf(timeFlag));
        return resp;
    }

    private static void setWeekSendList(List<RobotRecordStatisticResp> sendRecordList, String format) {
        for (RobotRecordStatisticResp record :
                sendRecordList) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(record.getCreateTime());
            calendar.add(Calendar.DAY_OF_WEEK, 7);
            String startTime = DateUtil.dateToString(record.getCreateTime(), format);
            String endTime = DateUtil.calendarToString(calendar, format);
            List<String> weeks = DateUtil.findWeeks(startTime, endTime);
            if (CollectionUtils.isNotEmpty(weeks)) {
                record.setCreateTime(DateUtil.stringToDate(weeks.get(0), format));
            }
            record.setId(null);
            record.setUpdater(null);
            record.setSerialNum(null);
            record.setSendPerson(null);
            record.setMessageType(0);
            record.setConversationId(null);
            record.setUpdateTime(null);
        }
    }

    private static void setWeekRecordList(List<QueryUserLoginRecordResp> recordList, String format) {
        for (QueryUserLoginRecordResp login :
                recordList) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(login.getDailyFirstLoginTime());
            calendar.add(Calendar.DAY_OF_WEEK, 7);
            String startTime = DateUtil.dateToString(login.getDailyFirstLoginTime(), format);
            String endTime = DateUtil.calendarToString(calendar, format);
            List<String> weeks = DateUtil.findWeeks(startTime, endTime);
            if (CollectionUtils.isNotEmpty(weeks)) {
                login.setDailyFirstLoginTime(DateUtil.stringToDate(weeks.get(0), format));
            }
            login.setId(null);
            login.setCreateTime(null);
        }
    }

    @NotNull
    private static ArrayList<LineChart> getLineCharts(List<String> times, int timeFlag, HashMap<String, Integer> loginMap) {
        ArrayList<LineChart> objects = new ArrayList<>();
        for (int i = 0; i < times.size(); i++) {
            LineChart lineChart = new LineChart();
            lineChart.setTime(DateUtil.getTimeString(times.get(i), timeFlag));
            lineChart.setNum(null != loginMap.get(times.get(i)) ? loginMap.get(times.get(i)).toString() : "0");
            objects.add(lineChart);
        }
        return objects;
    }

    private static void setSendList(List<RobotRecordStatisticResp> sendRecordList, String format) {
        for (RobotRecordStatisticResp record :
                sendRecordList) {
            String date = DateUtil.dateToString(record.getCreateTime(), format);
            record.setCreateTime(DateUtil.stringToDate(date, format));
            record.setId(null);
            record.setUpdater(null);
            record.setSerialNum(null);
            record.setSendPerson(null);
            record.setMessageType(0);
            record.setConversationId(null);
            record.setUpdateTime(null);
        }
    }

    private static void setRecordList(List<QueryUserLoginRecordResp> recordList, String format) {
        for (QueryUserLoginRecordResp login :
                recordList) {
            String date = DateUtil.dateToString(login.getDailyFirstLoginTime(), format);
            login.setDailyFirstLoginTime(DateUtil.stringToDate(date, format));
            login.setId(null);
            login.setCreateTime(null);
        }
    }

    private static HashMap<String, Integer> putValueInMap(List<String> times, List<RobotRecordStatisticResp> sendRecordList, List<QueryUserLoginRecordResp> recordList) {
        HashMap<String, Integer> map = new HashMap<>();
        if (CollectionUtils.isNotEmpty(sendRecordList)) {
            // 可能有多条数据是同一时间节点
            for (RobotRecordStatisticResp record : sendRecordList) {
                int num = 0;
                for (String time : times) {
                    // 没有值的时候给 0
                    if (null == map.get(time)) {
                        map.put(time, 0);
                    }
                    if (0 == DateUtil.stringToCalendar(time).compareTo(DateUtil.longToCalendar(record.getCreateTime().getTime()))) {
                        num++;
                        // 累加同一时点节点的数量
                        int inc = map.put(time, num);
                        map.put(time, num + inc);
                    }
                }
            }
        }
        if (CollectionUtils.isNotEmpty(recordList)) {
            for (QueryUserLoginRecordResp record : recordList) {
                int num = 0;
                for (String time : times) {
                    if (null == map.get(time)) {
                        map.put(time, 0);
                    }
                    if (0 == DateUtil.stringToCalendar(time).compareTo(DateUtil.longToCalendar(record.getDailyFirstLoginTime().getTime()))) {
                        num++;
                        int inc = map.put(time, num);
                        map.put(time, num + inc);
                    }
                }
            }
        }
        return map;
    }

    private List<QueryUserLoginRecordResp> getLoginRecordResps(QueryUserLoginRecordReq queryUserLoginRecordReq, List<QueryUserLoginRecordResp> recordList) {
        PageResult<QueryUserLoginRecordResp> recordRespPageResult = userLoginRecordApi.queryList(queryUserLoginRecordReq);
        if (CollectionUtils.isNotEmpty(recordRespPageResult.getList())) {
            recordList = recordRespPageResult.getList();
        }
        return recordList;
    }

    @Override
    public HomeSendLineChartResp sendLineChart(HomeLineChartReq req) {
        HomeSendLineChartResp resp = new HomeSendLineChartResp();
        PageResult<RobotRecordStatisticResp> recordByTime = getRecordStatisticRespPageResult(req);
        List<RobotRecordStatisticResp> sendRecordList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(recordByTime.getList())) {
            sendRecordList = recordByTime.getList();
        }

        List<String> times = null;
        int timeFlag = 1;

        // 查一天
        if (req.getStartTime().equals(req.getEndTime())) {
            for (RobotRecordStatisticResp record :
                    sendRecordList) {
                String date = DateUtil.dateToString(record.getCreateTime(), HOUR_TIME_FORMAT);
                record.setCreateTime(DateUtil.stringToDate(date, HOUR_TIME_FORMAT));
            }
            times = DateUtil.findHours(req.getStartTime() + " 0:00:00", req.getEndTime() + " 23:00:00");
            timeFlag = 1;
        } else {
            // 小于一个月
            int betweenDays = DateUtil.getBetweenDays(req.getStartTime(), req.getEndTime());
            for (RobotRecordStatisticResp record :
                    sendRecordList) {
                String date = DateUtil.dateToString(record.getCreateTime(), START_TIME_FORMAT);
                record.setCreateTime(DateUtil.stringToDate(date, START_TIME_FORMAT));
            }
            if (betweenDays <= 30) {
                times = DateUtil.findEveryDay(req.getStartTime() + ADD_ZERO_TIME, req.getEndTime() + ADD_ZERO_TIME);
                timeFlag = 2;
                // 一个月以上
            } else {
                times = DateUtil.findWeeks(req.getStartTime() + ADD_ZERO_TIME, req.getEndTime() + ADD_ZERO_TIME);
                timeFlag = 3;
            }
        }
        HashMap<Calendar, HashMap> cuncMap = new HashMap<>();
        HashMap<Calendar, HashMap> cmccMap = new HashMap<>();
        HashMap<Calendar, HashMap> ctMap = new HashMap<>();
        // 发送量处理
        if (CollectionUtils.isNotEmpty(sendRecordList)) {
            // 所有的发送记录
            // 可能有多条数据是同一时间节点
            for (RobotRecordStatisticResp record : sendRecordList) {
                int cmcc = 0;
                int cunc = 0;
                int ct = 0;
                // 以时间节点作为循环
                for (String time : times) {
                    // 如果数据类型是周
                    if (3 == timeFlag) {
                        int timeWeek = DateUtil.getWeek(DateUtil.stringToCalendar(time));
                        int recordWeek = DateUtil.getWeek(DateUtil.longToCalendar(record.getCreateTime().getTime()));
                        // 把数据的时间改成周的
                        if (timeWeek == recordWeek) {
                            record.setCreateTime(DateUtil.stringToCalendar(time).getTime());
                        }
                    }
                    if (0 == DateUtil.stringToCalendar(time).compareTo(DateUtil.longToCalendar(record.getCreateTime().getTime()))) {
                        // 运营商一致则进行计数
                        if (CSPOperatorCodeEnum.CUNC.getCode() == record.getChannelType()) {
                            cunc++;
                            putCountValue(cuncMap, record, cunc, CSPOperatorCodeEnum.CUNC.getCode());
                        } else if (CSPOperatorCodeEnum.CMCC.getCode() == record.getChannelType()) {
                            cmcc++;
                            putCountValue(cmccMap, record, cmcc, CSPOperatorCodeEnum.CMCC.getCode());
                        } else if (CSPOperatorCodeEnum.CT.getCode() == record.getChannelType()) {
                            ct++;
                            putCountValue(ctMap, record, ct, CSPOperatorCodeEnum.CT.getCode());
                        }
                    }
                }
            }
        }
        List<LineChart> cuncList = getSendLineChartList(times, timeFlag, cuncMap, CSPOperatorCodeEnum.CUNC.getCode());
        List<LineChart> cmccList = getSendLineChartList(times, timeFlag, cmccMap, CSPOperatorCodeEnum.CMCC.getCode());
        List<LineChart> ctList = getSendLineChartList(times, timeFlag, ctMap, CSPOperatorCodeEnum.CT.getCode());
        resp.setCuncLineChart(cuncList);
        resp.setCmccLineChart(cmccList);
        resp.setCtLineChart(ctList);
        resp.setDataType(Long.valueOf(timeFlag));
        return resp;
    }

    private static void putCountValue(HashMap<Calendar, HashMap> recordMap, RobotRecordStatisticResp record, int index, int code) {
        HashMap<Integer, Integer> numMap = new HashMap<>();
        Calendar calendarKey = DateUtil.longToCalendar(record.getCreateTime().getTime());
        int inc = 0;
        Object key = code;
        // 如果有上一次的时间节点的数据
        if (null != recordMap.get(calendarKey)) {
            // 先取出上一次的数据，再进行累加
            HashMap<Integer, Integer> preMap = recordMap.get(calendarKey);
            if (null != preMap.get(key)) {
                inc = preMap.get(key);
            }
        }

        numMap.put(code, index + inc);
        recordMap.put(calendarKey, numMap);
    }

    private static List<LineChart> getSendLineChartList(List<String> times, int timeFlag, HashMap<Calendar, HashMap> recordMap, int code) {
        List<LineChart> list = new ArrayList<>();
        for (int i = 0; i < times.size(); i++) {
            LineChart lineChart = new LineChart();
            lineChart.setTime(DateUtil.getTimeString(times.get(i), timeFlag));
            lineChart.setNum("0");
            if (null != recordMap.get(DateUtil.stringToCalendar(times.get(i)))) {
                Object o = recordMap.get(DateUtil.stringToCalendar(times.get(i))).get(code);
                lineChart.setNum(null != o ? o.toString() : "0");
            }
            list.add(lineChart);
        }
        return list;
    }

    private PageResult<RobotRecordStatisticResp> getRecordStatisticRespPageResult(HomeLineChartReq req) {
        RobotRecordPageReq robotRecordPageReq = new RobotRecordPageReq();
        robotRecordPageReq.setPageSize(-1);
        String userId = req.getCreator();
        if (userId == null)
            userId = SessionContextUtil.getUser().getUserId();
        robotRecordPageReq.setUserIdList(cspCustomerApi.queryCustomerIdsByCspId(cspService.obtainCspId(userId)));
        robotRecordPageReq.setStartTime(req.getStartTime() + ADD_ZERO_TIME);
        robotRecordPageReq.setEndTime(req.getEndTime() + ADD_END_TIME);

        PageResult<RobotRecordStatisticResp> recordByTime = robotRecordApi.getRobotRecordStatisticByTime(robotRecordPageReq);
        return recordByTime;
    }
}
