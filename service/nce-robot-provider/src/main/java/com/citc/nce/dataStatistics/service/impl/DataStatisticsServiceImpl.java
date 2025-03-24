package com.citc.nce.dataStatistics.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.citc.nce.auth.accountmanagement.AccountManagementApi;
import com.citc.nce.auth.accountmanagement.vo.AccountManagementResp;
import com.citc.nce.authcenter.csp.CspCustomerApi;
import com.citc.nce.authcenter.csp.vo.resp.CspInfoResp;
import com.citc.nce.common.core.pojo.PageResult;
import com.citc.nce.common.util.JsonUtils;
import com.citc.nce.common.util.SessionContextUtil;
import com.citc.nce.customcommand.service.ICustomCommandService;
import com.citc.nce.dataStatistics.dao.*;
import com.citc.nce.dataStatistics.entity.*;
import com.citc.nce.dataStatistics.service.DataStatisticsService;
import com.citc.nce.dataStatistics.vo.DataStatisticItem;
import com.citc.nce.dataStatistics.vo.ProcessBasicInfo;
import com.citc.nce.dataStatistics.vo.ProcessTopInfo;
import com.citc.nce.dataStatistics.vo.req.*;
import com.citc.nce.dataStatistics.vo.resp.*;
import com.citc.nce.mybatis.core.query.LambdaQueryWrapperX;
import com.citc.nce.robot.dao.*;
import com.citc.nce.robot.entity.*;
import com.citc.nce.robot.util.DateUtil;
import com.citc.nce.utils.DateUtils;
import com.google.common.base.Strings;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class DataStatisticsServiceImpl implements DataStatisticsService {

    @Resource
    private ConversationalInteractionDaysDao conversationalInteractionDaysDao;
    @Resource
    private CspCustomerApi cspCustomerApi;

    @Resource
    private ProcessQuantityStatisticDao processQuantityStatisticDao;

    @Resource
    private ConversationalQuantityStatisticDao conversationalQuantityStatisticDao;

    @Resource
    private RobotSceneNodeDao robotSceneNodeDao;

    @Resource
    private RobotProcessSettingNodeDao robotProcessSettingNodeDao;

    @Resource
    private RobotProcessDesDao robotProcessDesDao;

    @Resource
    private RobotOrderDao robotOrderDao;

    @Resource
    private RobotVariableDao robotVariableDao;

    @Resource
    RobotAccountDao robotAccountDao;
    @Resource
    RobotRecordDao robotRecordDao;

    @Resource
    AccountManagementApi accountManagementApi;

    @Autowired
    private ICustomCommandService customCommandService;


    private static final String HOUR = "hour";
    private static final String DAY = "day";
    private static final String START = "start";
    private static final String END = "end";

    /**
     * 场景流程分析总统计
     *
     * @return
     */
    @Override
    public ScenarioFlowCountResp scenarioFlowCount() {
        String userId = SessionContextUtil.getUser().getUserId();
        QueryWrapper wrapper = new QueryWrapper();
        wrapper.eq("deleted", 0);
        wrapper.eq("creator", userId);
        //场景统计
        Long scenariosNum = robotSceneNodeDao.selectCount(wrapper);
        //流程统计
        Long processesNum = robotProcessSettingNodeDao.selectCount(wrapper);
        //指令统计

        LambdaQueryWrapper<RobotOrderDo> robotOrderDoLambdaQueryWrapper = new LambdaQueryWrapper<>();
        robotOrderDoLambdaQueryWrapper.eq(RobotOrderDo::getDeleted, 0);
        robotOrderDoLambdaQueryWrapper.eq(RobotOrderDo::getCreator, userId);
        robotOrderDoLambdaQueryWrapper.eq(RobotOrderDo::getOrderType, 0);
        Long orderNum = robotOrderDao.selectCount(robotOrderDoLambdaQueryWrapper) + customCommandService.countMyCommand();
        //变量统计
        Long variablesNum = robotVariableDao.selectCount(wrapper);
        ScenarioFlowCountResp scenarioFlowCountResp = robotProcessDesDao.selectRobotProcessDesCount(userId);
        if (scenarioFlowCountResp == null) {
            scenarioFlowCountResp = new ScenarioFlowCountResp();
            scenarioFlowCountResp.setQuestionsNum(0L);
            scenarioFlowCountResp.setBranchNum(0L);
            scenarioFlowCountResp.setSentMessagesNum(0L);
            scenarioFlowCountResp.setInstructionNodesNum(0L);
            scenarioFlowCountResp.setVariableOperationsNum(0L);
            scenarioFlowCountResp.setSubprocessesNum(0L);
            scenarioFlowCountResp.setContactOperationsNum(0L);
            /**
             * 如果解决报系统异常
             * 2023/2/23
             */
            return scenarioFlowCountResp;
        }
        scenarioFlowCountResp.setScenariosNum(scenariosNum);
        scenarioFlowCountResp.setProcessesNum(processesNum);
        scenarioFlowCountResp.setOrderNum(orderNum);
        scenarioFlowCountResp.setVariablesNum(variablesNum);
        return scenarioFlowCountResp;
    }

    @Override
    public List<SceneResp> getSceneList() {
        List<RobotSceneNodeDo> list = robotSceneNodeDao.selectList(RobotSceneNodeDo::getCreator, SessionContextUtil.getUser().getUserId());
        List<SceneResp> collect = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(list)) {
            collect = list.stream().map(e -> {
                return new SceneResp().setRobotSceneNodeId(e.getId()).setRobotSceneNodeName(e.getSceneName());
            }).collect(Collectors.toList());
        }
        return collect;
    }

    @Override
    public List<ProcessResp> getProcessList(SceneIdReq req) {
        QueryWrapper wrapper = new QueryWrapper();
        wrapper.eq("deleted", 0);
        String userId = SessionContextUtil.getUser().getUserId();
        wrapper.eq("creator", userId);
        List<RobotProcessSettingNodeDo> list = new ArrayList<>();
        if (null == req.getRobotSceneNodeId()) {
            list = robotProcessSettingNodeDao.selectList(wrapper);
        } else {
            wrapper.eq("scene_id", req.getRobotSceneNodeId());
            list = robotProcessSettingNodeDao.selectList(wrapper);
        }
        List<ProcessResp> collect = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(list)) {
            collect = list.stream().map(e -> {
                return new ProcessResp().setRobotProcessSettingNodeId(e.getId()).setRobotProcessSettingNodeName(e.getProcessName());
            }).collect(Collectors.toList());
        }
        return collect;
    }


    /**
     * 首页topo5
     *
     * @param req 请求信息
     * @return 响应报文
     */
    @Override
    public ProcessQuantityTopResp processTopFive(TopFiveReq req) {
        ProcessQuantityTopResp processQuantityTopResp = new ProcessQuantityTopResp();
        Date startDate = DateUtils.obtainTime(DAY, START, DateUtils.obtainDate(req.getStartTime()));
        Date endDate = DateUtils.obtainTime(DAY, END, DateUtils.obtainDate(req.getEndTime()));
        String userId = !isAdmin() ? SessionContextUtil.getUser().getUserId() : null;
        /**
         * 2023-5-9
         * 机器人运营情况折线图
         * 如果用户选中了某个chatbot，则只统计该chatbot下的数据
         */
        List<ProcessQuantityStatisticDo> processQuantityDos = obtainProcessQuantityByTimeOrUserId(userId, startDate, endDate, req.getChatBotId());
        /**
         * 如果获取不到流程，则直接返回
         * 2023/2/23
         */
        if (CollectionUtils.isEmpty(processQuantityDos)) {
            return processQuantityTopResp;
        }
        //拆分数据
        ProcessTopInfo processTopInfo = obtainProcessBasicData(processQuantityDos);
        Map<Long, Long> processMap = processTopInfo.getProcessMap();
        Map<Long, Long> sceneMap = processTopInfo.getSceneMap();
        Map<Long, Long> processToSceneMap = processTopInfo.getProcessToSceneMap();
        Set<Long> sceneIds = processTopInfo.getSceneIds();
        Set<Long> processIds = processTopInfo.getProcessIds();
        /**
         * 如果获取不到流程，则直接返回
         * 2023/2/24
         */
        if (CollectionUtils.isNotEmpty(sceneIds)) {
            //获取名称
            Map<Long, String> sceneNameMap = obtainSceneNameMapByIdS(new ArrayList<>(sceneIds));
            List<ProcessQuantityScenceTopResp> scenceList = obtainScenceList(sceneMap, sceneNameMap);
            //获取场景
            List<ProcessQuantityScenceTopResp> processQuantityScenceAsc = scenceList.stream().sorted(Comparator.comparing(ProcessQuantityScenceTopResp::getProcessTriggersSumNum)).limit(5).collect(Collectors.toList());
            List<ProcessQuantityScenceTopResp> processQuantityScenceDesc = scenceList.stream().sorted(Comparator.comparing(ProcessQuantityScenceTopResp::getProcessTriggersSumNum).reversed()).limit(5).collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(processIds)) {
                Map<Long, String> processNameMap = obtainProcessNameMapByIdS(new ArrayList<>(processIds));
                List<ProcessQuantityProcessTopResp> processList = obtainProcessList(processMap, processToSceneMap, sceneNameMap, processNameMap);
                //获取流程
                List<ProcessQuantityProcessTopResp> processQuantityProcessAsc = processList.stream().sorted(Comparator.comparing(ProcessQuantityProcessTopResp::getProcessTriggersSumNum)).limit(5).collect(Collectors.toList());
                List<ProcessQuantityProcessTopResp> processQuantityProcessDesc = processList.stream().sorted(Comparator.comparing(ProcessQuantityProcessTopResp::getProcessTriggersSumNum).reversed()).limit(5).collect(Collectors.toList());
                processQuantityTopResp.setScenceListAsc(processQuantityScenceAsc);
                processQuantityTopResp.setScenceListDesc(processQuantityScenceDesc);
                processQuantityTopResp.setProcessListAsc(processQuantityProcessAsc);
                processQuantityTopResp.setProcessListDesc(processQuantityProcessDesc);
            }
        }


        return processQuantityTopResp;
    }

    private List<ProcessQuantityProcessTopResp> obtainProcessList(Map<Long, Long> processMap, Map<Long, Long> processToSceneMap, Map<Long, String> sceneNameMap, Map<Long, String> processNameMap) {
        List<ProcessQuantityProcessTopResp> processList = new ArrayList<>();
        ProcessQuantityProcessTopResp processResp;
        Long id;
        for (Map.Entry<Long, Long> entry : processMap.entrySet()) {
            id = entry.getKey();
            processResp = new ProcessQuantityProcessTopResp();
            processResp.setRobotSceneNodeId(processToSceneMap.get(id));
            processResp.setRobotProcessSettingNodeId(id);
            processResp.setProcessTriggersSumNum(entry.getValue());
            processResp.setSceneName(sceneNameMap.get(processToSceneMap.get(id)));
            processResp.setProcessName(processNameMap.get(id));
            processList.add(processResp);
        }
        return processList;
    }

    private List<ProcessQuantityScenceTopResp> obtainScenceList(Map<Long, Long> sceneMap, Map<Long, String> sceneNameMap) {
        List<ProcessQuantityScenceTopResp> scenceList = new ArrayList<>();
        ProcessQuantityScenceTopResp scenceResp;
        Long id;
        for (Map.Entry<Long, Long> entry : sceneMap.entrySet()) {
            id = entry.getKey();
            scenceResp = new ProcessQuantityScenceTopResp();
            scenceResp.setRobotSceneNodeId(id);
            scenceResp.setProcessTriggersSumNum(entry.getValue());
            scenceResp.setSceneName(sceneNameMap.get(id));
            scenceList.add(scenceResp);
        }
        return scenceList;
    }

    private ProcessTopInfo obtainProcessBasicData(List<ProcessQuantityStatisticDo> processQuantityDaysDos) {
        ProcessTopInfo processTopInfo = new ProcessTopInfo();
        Map<Long, Long> processMap = new HashMap<>();
        Map<Long, Long> sceneMap = new HashMap<>();
        Map<Long, Long> processToSceneMap = new HashMap<>();
        Set<Long> sceneIds = new HashSet<>();
        Set<Long> processIds = new HashSet<>();

        Long processId;
        Long sceneId;
        Long processTriggersNum;
        for (ProcessQuantityStatisticDo item : processQuantityDaysDos) {
            processTriggersNum = item.getProcessTriggersNum();
            processId = item.getRobotProcessSettingNodeId();
            sceneId = item.getRobotSceneNodeId();
            if (processId != null && sceneId != null) {
                if (processMap.containsKey(processId)) {
                    processMap.put(processId, processMap.get(processId) + processTriggersNum);
                } else {
                    processMap.put(processId, processTriggersNum);
                }
                if (sceneMap.containsKey(sceneId)) {
                    sceneMap.put(sceneId, sceneMap.get(sceneId) + processTriggersNum);
                } else {
                    sceneMap.put(sceneId, processTriggersNum);
                }
                sceneIds.add(sceneId);
                processIds.add(processId);
                processToSceneMap.put(processId, sceneId);
            }
        }
        processTopInfo.setProcessIds(processIds);
        processTopInfo.setSceneIds(sceneIds);
        processTopInfo.setProcessMap(processMap);
        processTopInfo.setSceneMap(sceneMap);
        processTopInfo.setProcessToSceneMap(processToSceneMap);
        return processTopInfo;
    }

    private Map<Long, String> obtainProcessNameMapByIdS(List<Long> processIds) {
        Map<Long, String> processNameMap = new HashMap<>();
        List<RobotProcessSettingNodeDo> robotProcessSettingNodeDos = robotProcessSettingNodeDao.selectByIds(processIds);
        if (robotProcessSettingNodeDos != null) {
            for (RobotProcessSettingNodeDo item : robotProcessSettingNodeDos) {
                processNameMap.put(item.getId(), item.getProcessName());
            }
        }
        return processNameMap;
    }

    private Map<Long, String> obtainSceneNameMapByIdS(List<Long> sceneIds) {
        Map<Long, String> sceneNameMap = new HashMap<>();
        List<RobotSceneNodeDo> robotSceneNodeDos = robotSceneNodeDao.selectByIds(sceneIds);
        if (robotSceneNodeDos != null) {
            for (RobotSceneNodeDo item : robotSceneNodeDos) {
                sceneNameMap.put(item.getId(), item.getSceneName());
            }
        }
        return sceneNameMap;
    }


    @Override
    public ProcessQuantityResp processYesterdayLineChart(SceneAndProcessReq req) {
        ProcessQuantityResp resp = new ProcessQuantityResp();
        Long robotSceneNodeId = req.getRobotSceneNodeId();
        Long robotProcessSettingNodeId = req.getRobotProcessSettingNodeId();
        String userId = SessionContextUtil.getUser().getUserId();
        boolean admin = isAdmin();
        userId = admin ? null : userId;
        List<String> times = new ArrayList<>();
        Date startDate = DateUtils.obtainTime(DAY, START, DateUtils.obtainDate(req.getStartTime()));
        Date endDate = DateUtils.obtainTime(DAY, END, DateUtils.obtainDate(req.getEndTime()));
        String timeType = DateUtils.obtainTimeType(startDate, endDate);
        /**
         * 2023-5-9
         * 机器人运营情况折线图
         * 如果用户选中了某个chatbot，则只统计该chatbot下的数据
         */
        //获取数据


        Map<String, DataStatisticItem> dataStatistics = obtainDataStatistics(userId, startDate, endDate, times, robotSceneNodeId, robotProcessSettingNodeId, "P", null, req.getChatBotId(), req.getOperatorType());
        //整理数据
        DataStatisticItem dataStatisticItem;
        List<DataStatisticItem> dataStatisticItems = new ArrayList<>();
        long processTriggersSumNumAll = 0L;
        long processCompletedSumNumAll = 0L;
        long bottomReturnSumNumAll = 0L;
        for (String item : times) {
            if (dataStatistics.containsKey(item)) {
                dataStatisticItem = dataStatistics.get(item);
                processTriggersSumNumAll = processTriggersSumNumAll + dataStatisticItem.getProcessTriggersSumNum();
                processCompletedSumNumAll = processCompletedSumNumAll + dataStatisticItem.getProcessCompletedSumNum();
                bottomReturnSumNumAll = bottomReturnSumNumAll + dataStatisticItem.getBottomReturnSumNum();
            } else {
                dataStatisticItem = new DataStatisticItem();
                dataStatisticItem.setProcessTriggersSumNum(0L);
                dataStatisticItem.setProcessCompletedSumNum(0L);
                dataStatisticItem.setBottomReturnSumNum(0L);
                dataStatisticItem.setCreateTime(item);
            }
            dataStatisticItem.setShowTime(DateUtils.obtainSpecificFormatTime(item, timeType, "SHOW"));
            dataStatisticItem.setHoverTime(DateUtils.obtainSpecificFormatTime(item, timeType, "HOVER"));
            dataStatisticItems.add(dataStatisticItem);
        }
        resp.setList(dataStatisticItems);
        resp.setProcessTriggersSumNum(processTriggersSumNumAll);
        resp.setProcessCompletedSumNum(processCompletedSumNumAll);
        resp.setBottomReturnSumNum(bottomReturnSumNumAll);
        return resp;
    }

    @Override
    public PageResult processYesterdayPage(SceneAndProcessPageReq req) {
        PageResult pageResult = new PageResult<>();
        Long robotSceneNodeId = req.getRobotSceneNodeId();
        Long robotProcessSettingNodeId = req.getRobotProcessSettingNodeId();
        String userId = SessionContextUtil.getUser().getUserId();
        List<String> times = new ArrayList<>();
        Date startDate = DateUtils.obtainTime(DAY, START, DateUtils.obtainDate(req.getStartTime()));
        Date endDate = DateUtils.obtainTime(DAY, END, DateUtils.obtainDate(req.getEndTime()));

        //获取数据
        Map<String, DataStatisticItem> dataStatistics = obtainDataStatistics(userId, startDate, endDate, times, robotSceneNodeId, robotProcessSettingNodeId, "P", "ID", null, req.getOperatorType());
        log.info("req:{}", JsonUtils.obj2String(req));
        log.info("processIds:{}", JsonUtils.obj2String(dataStatistics.keySet()));
        //整理数据
        List<DataStatisticItem> dataStatisticItems = new ArrayList<>();
        if (!dataStatistics.isEmpty()) {
            Map<Long, ProcessBasicInfo> processInfoMap = obtainProcessInfoMap(new ArrayList<>(dataStatistics.keySet()));
            log.info("processInfoMap:{}", JsonUtils.obj2String(processInfoMap));
            String idStr;
            long id;
            Long processTriggerSum;
            Long processCompletedSum;
            ProcessBasicInfo processBasicInfo;
            DataStatisticItem dataStatisticItem;
            for (Map.Entry<String, DataStatisticItem> entry : dataStatistics.entrySet()) {
                idStr = entry.getKey();
                dataStatisticItem = entry.getValue();
                processTriggerSum = dataStatisticItem.getProcessTriggersSumNum();
                processCompletedSum = dataStatisticItem.getProcessCompletedSumNum();
                if (processTriggerSum > 0 || processCompletedSum > 0) {
                    if ((!"null".equals(idStr))) {
                        id = Long.parseLong(idStr);
                        processBasicInfo = processInfoMap.get(id);
                        dataStatisticItem.setRobotProcessSettingNodeId(processBasicInfo.getProcessId());
                        dataStatisticItem.setRobotSceneNodeId(processBasicInfo.getSceneId());
                        dataStatisticItem.setProcessName(processBasicInfo.getProcessName());
                        dataStatisticItem.setSceneName(processBasicInfo.getSceneName());
                        dataStatisticItem.setDerail(processBasicInfo.getDerail());
                    }
                    dataStatisticItem.setCompletedRate(obtainCompleteRate(processTriggerSum, processCompletedSum));
                    dataStatisticItems.add(dataStatisticItem);
                }
            }
        }
        List<DataStatisticItem> dataStatisticItemsResult = dataStatisticItems.stream().skip((long) (req.getPageNo() - 1) * req.getPageSize())
                .limit(req.getPageSize()).collect(Collectors.toList());
        pageResult.setList(dataStatisticItemsResult);
        pageResult.setTotal((long) dataStatisticItems.size());
        return pageResult;
    }

    private Integer obtainCompleteRate(Long processTriggerSum, Long processCompletedSum) {
        if (processCompletedSum == 0L) {
            return 0;
        } else {
            if (processTriggerSum == 0L) {
                return 0;
            } else {
                Double processTriggerSumInt = Double.parseDouble(processTriggerSum + "");
                Double processCompletedSumInt = Double.parseDouble(processCompletedSum + "");
                BigDecimal bigDecimal = BigDecimal.valueOf(processCompletedSumInt / processTriggerSumInt);
                return bigDecimal.setScale(2, BigDecimal.ROUND_HALF_UP).multiply(BigDecimal.valueOf(100)).intValue();
            }
        }
    }

    /**
     * 获取流程的基本信息
     *
     * @param processIds 流程Id列表
     * @return 流程基本信息
     */
    private Map<Long, ProcessBasicInfo> obtainProcessInfoMap(List<String> processIds) {
        Map<Long, ProcessBasicInfo> processBasicInfoMap = new HashMap<>();
        if (CollectionUtils.isNotEmpty(processIds)) {
            List<Long> processIdList = new ArrayList<>();
            List<Long> sceneIdList = new ArrayList<>();
            for (String item : processIds) {
                if (!"null".equals(item)) {
                    processIdList.add(Long.valueOf(item));
                }
            }
            if (CollectionUtils.isNotEmpty(processIdList)) {
                List<RobotProcessSettingNodeDo> robotProcessSettingNodeDos = robotProcessSettingNodeDao.selectByIds(processIdList);
                robotProcessSettingNodeDos.forEach(i -> sceneIdList.add(i.getSceneId()));
                Map<Long, String> sceneIdMap = obtainSceneNameMapByIdS(sceneIdList);
                if (CollectionUtils.isNotEmpty(robotProcessSettingNodeDos)) {
                    ProcessBasicInfo processBasicInfo;
                    for (RobotProcessSettingNodeDo item : robotProcessSettingNodeDos) {
                        processBasicInfo = new ProcessBasicInfo();
                        processBasicInfo.setProcessId(item.getId());
                        processBasicInfo.setProcessName(item.getProcessName());
                        processBasicInfo.setSceneId(item.getSceneId());
                        processBasicInfo.setSceneName(sceneIdMap.get(item.getSceneId()));
                        processBasicInfo.setDerail(item.getDerail());
                        processBasicInfoMap.put(item.getId(), processBasicInfo);
                    }
                }
            }
        }
        return processBasicInfoMap;
    }


    @Override
    public SessionQuantityResp sessionYesterdayLineChart(StartAndEndTimeReq req) {
        SessionQuantityResp resp = new SessionQuantityResp();
        String userId = SessionContextUtil.getUser().getUserId();
        Date startDate = DateUtils.obtainTime(DAY, START, DateUtils.obtainDate(req.getStartTime()));
        Date endDate = DateUtils.obtainTime(DAY, END, DateUtils.obtainDate(req.getEndTime()));
        String timeType = DateUtils.obtainTimeType(startDate, endDate);
        //获取用户统计信息
        UserStatisticsResp userStatisticsResp = obtainUserStatistics(userId, startDate, endDate);
        List<String> times = new ArrayList<>();
        //获取数据
        Map<String, DataStatisticItem> dataStatistics = obtainDataStatistics(userId, startDate, endDate, times, null);
        //整理数据
        DataStatisticItem dataStatisticItem;
        List<DataStatisticItem> dataStatisticItems = new ArrayList<>();
        long effectiveSessionSumNumAll = 0L;
        long sessionSumNumAll = 0L;
        Map<String, Long> activeUserDateMap = userStatisticsResp.getActiveUserDateMap();
        Map<String, Long> newUserDateMap = userStatisticsResp.getNewUserDateMap();
        for (String item : times) {
            if (dataStatistics.containsKey(item)) {
                dataStatisticItem = dataStatistics.get(item);
                effectiveSessionSumNumAll = effectiveSessionSumNumAll + dataStatisticItem.getEffectiveSessionSumNum();
                sessionSumNumAll = sessionSumNumAll + dataStatisticItem.getSessionSumNum();
            } else {
                dataStatisticItem = new DataStatisticItem();
                dataStatisticItem.setEffectiveSessionSumNum(0L);
                dataStatisticItem.setSessionSumNum(0L);
                dataStatisticItem.setSendSumNum(0L);
                dataStatisticItem.setUpsideSumNum(0L);
                dataStatisticItem.setCreateTime(item);
            }
            dataStatisticItem.setActiveUsersSumNum(activeUserDateMap.getOrDefault(item, 0L));
            dataStatisticItem.setNewUsersSumNum(newUserDateMap.getOrDefault(item, 0L));
            dataStatisticItem.setShowTime(DateUtils.obtainSpecificFormatTime(item, timeType, "SHOW"));
            dataStatisticItem.setHoverTime(DateUtils.obtainSpecificFormatTime(item, timeType, "HOVER"));
            dataStatisticItems.add(dataStatisticItem);

        }
        resp.setList(dataStatisticItems);
        resp.setUserList(userStatisticsResp.getUserList());
        resp.setNewUsersSumNum(userStatisticsResp.getNewUsersSumNum());
        resp.setActiveUsersSumNum(userStatisticsResp.getActiveUsersSumNum());
        resp.setSessionSumNum(sessionSumNumAll);
        resp.setEffectiveSessionSumNum(effectiveSessionSumNumAll);
        return resp;


    }

    private Map<String, DataStatisticItem> obtainDataStatistics(String userId, Date startTime, Date endTime, List<String> times, Integer operatorType) {
        return obtainDataStatistics(userId, startTime, endTime, times, null, null, null, null, null, operatorType);
    }

    private Map<String, DataStatisticItem> obtainDataStatistics(String userId, Date startTime, Date endTime, List<String> times, Long robotSceneNodeId, Long robotProcessSettingNodeId, String queryType, String type, String chatbotId, Integer operatorType) {
        //获取时间
        String timeType = DateUtils.obtainTimeType(startTime, endTime);
        Map<String, DataStatisticItem> dataStatistics;
        if (HOUR.equals(timeType)) {
            dataStatistics = obtainDataStatisticHourByTimeOrUserId(userId, startTime, endTime, robotSceneNodeId, robotProcessSettingNodeId, queryType, type, chatbotId, operatorType);
            times.addAll(DateUtils.obtainHourList(startTime));
        } else {
            dataStatistics = obtainDataStatisticDaysByTimeOrUserId(userId, startTime, endTime, robotSceneNodeId, robotProcessSettingNodeId, queryType, type, chatbotId, operatorType);
            times.addAll(DateUtils.obtainDayList(startTime, endTime));
        }

        return dataStatistics;
    }

    private Map<String, DataStatisticItem> obtainDataStatisticDaysByTimeOrUserId(String userId, Date startDate, Date endDate, Long robotSceneNodeId, Long robotProcessSettingNodeId, String queryType, String type, String chatbotId, Integer operatorType) {
        Map<String, DataStatisticItem> dataStatisticItemMap = new HashMap<>();
        if (!Strings.isNullOrEmpty(queryType) && "P".equals(queryType)) {
            List<ProcessQuantityStatisticDo> processQuantityDaysDos = obtainProcessQuantityDaysByTimeOrUserId(userId, startDate, endDate, robotSceneNodeId, robotProcessSettingNodeId, chatbotId, operatorType);
            if (processQuantityDaysDos != null) {
                String time;
                long processTriggersSumNum;
                long processCompletedSumNum;
                long bottomReturnSumNum;
                for (ProcessQuantityStatisticDo item : processQuantityDaysDos) {
                    processTriggersSumNum = item.getProcessTriggersNum();
                    processCompletedSumNum = item.getProcessCompletedNum();
                    bottomReturnSumNum = item.getBottomReturnNum();
                    if (!Strings.isNullOrEmpty(type) && type.equals("ID")) {
                        time = item.getRobotProcessSettingNodeId() + "";
                    } else {
                        time = DateUtils.obtainDateStr(item.getHours(), "yyyy-MM-dd");
                    }
                    makeupStatisticsData(dataStatisticItemMap, time, processTriggersSumNum, processCompletedSumNum, bottomReturnSumNum);
                }
            }
        } else {
            List<ConversationalQuantityStatisticDo> conversationalInteractionDaysDos = obtainConversationalInteractionDaysByTimeOrUserId(userId, startDate, endDate, operatorType);
            if (conversationalInteractionDaysDos != null) {
                String time;
                long sessionSumNum;
                long effectiveSessionSumNum;
                long sendSumNum;
                long upsideSumNum;
                for (ConversationalQuantityStatisticDo item : conversationalInteractionDaysDos) {
                    effectiveSessionSumNum = item.getEffectiveSessionNum();
                    sessionSumNum = item.getSessionNum();
                    sendSumNum = item.getSendNum();
                    upsideSumNum = item.getUpsideNum();
                    time = DateUtils.obtainDateStr(item.getHours(), "yyyy-MM-dd");
                    makeupStatisticsData(dataStatisticItemMap, time, effectiveSessionSumNum, sessionSumNum, sendSumNum, upsideSumNum);
                }
            }
        }

        return dataStatisticItemMap;
    }

    private Map<String, DataStatisticItem> obtainDataStatisticHourByTimeOrUserId(String userId, Date startDate, Date endDate, Long robotSceneNodeId, Long robotProcessSettingNodeId, String queryType, String type, String chatbotId, Integer operatorType) {
        Map<String, DataStatisticItem> dataStatisticItemMap = new HashMap<>();
        if (!Strings.isNullOrEmpty(queryType) && "P".equals(queryType)) {
            List<ProcessQuantityStatisticDo> processQuantityYesterdayDos = obtainProcessQuantityHoursByTimeOrUserId(userId, startDate, endDate, robotSceneNodeId, robotProcessSettingNodeId, chatbotId, operatorType);
            if (processQuantityYesterdayDos != null) {
                String time;
                long processTriggersSumNum;
                long processCompletedSumNum;
                long bottomReturnSumNum;
                for (ProcessQuantityStatisticDo item : processQuantityYesterdayDos) {
                    processTriggersSumNum = item.getProcessTriggersNum();
                    processCompletedSumNum = item.getProcessCompletedNum();
                    bottomReturnSumNum = item.getBottomReturnNum();
                    if (!Strings.isNullOrEmpty(type) && type.equals("ID")) {
                        time = item.getRobotProcessSettingNodeId() + "";
                    } else {
                        time = DateUtils.obtainDateStr(DateUtils.obtainTimesHoursKey(item.getHours()));
                    }
                    makeupStatisticsData(dataStatisticItemMap, time, processTriggersSumNum, processCompletedSumNum, bottomReturnSumNum);
                }
            }
        } else {
            List<ConversationalQuantityStatisticDo> conversationalInteractionHourDos = obtainConversationalInteractionHoursByTimeOrUserId(userId, startDate, endDate, operatorType);
            if (conversationalInteractionHourDos != null) {
                String time;
                long sessionSumNum;
                long effectiveSessionSumNum;
                long sendSumNum;
                long upsideSumNum;
                for (ConversationalQuantityStatisticDo item : conversationalInteractionHourDos) {
                    effectiveSessionSumNum = item.getEffectiveSessionNum();
                    sessionSumNum = item.getSessionNum();
                    sendSumNum = item.getSendNum();
                    upsideSumNum = item.getUpsideNum();
                    time = DateUtils.obtainDateStr(DateUtils.obtainTimesHoursKey(item.getHours()));
                    makeupStatisticsData(dataStatisticItemMap, time, effectiveSessionSumNum, sessionSumNum, sendSumNum, upsideSumNum);
                }
            }
        }
        return dataStatisticItemMap;
    }


    private void makeupStatisticsData(Map<String, DataStatisticItem> dataStatisticItemMap, String time, long processTriggersSumNum, long processCompletedSumNum, long bottomReturnSumNum) {
        DataStatisticItem dataStatisticItem;
        if (dataStatisticItemMap.containsKey(time)) {
            dataStatisticItem = dataStatisticItemMap.get(time);
            dataStatisticItem.setProcessTriggersSumNum(dataStatisticItem.getProcessTriggersSumNum() + processTriggersSumNum);
            dataStatisticItem.setProcessCompletedSumNum(dataStatisticItem.getProcessCompletedSumNum() + processCompletedSumNum);
            dataStatisticItem.setBottomReturnSumNum(dataStatisticItem.getBottomReturnSumNum() + bottomReturnSumNum);
        } else {
            dataStatisticItem = new DataStatisticItem();
            dataStatisticItem.setCreateTime(time);
            dataStatisticItem.setProcessTriggersSumNum(processTriggersSumNum);
            dataStatisticItem.setProcessCompletedSumNum(processCompletedSumNum);
            dataStatisticItem.setBottomReturnSumNum(bottomReturnSumNum);
            dataStatisticItemMap.put(time, dataStatisticItem);
        }
    }

    private void makeupStatisticsData(Map<String, DataStatisticItem> dataStatisticItemMap, String time, long effectiveSessionSumNum, long sessionSumNum, long sendSumNum, long upsideSumNum) {
        DataStatisticItem dataStatisticItem;
        if (dataStatisticItemMap.containsKey(time)) {
            dataStatisticItem = dataStatisticItemMap.get(time);
            dataStatisticItem.setSessionSumNum(dataStatisticItem.getSessionSumNum() + sessionSumNum);
            dataStatisticItem.setEffectiveSessionSumNum(dataStatisticItem.getEffectiveSessionSumNum() + effectiveSessionSumNum);
            dataStatisticItem.setSendSumNum(dataStatisticItem.getSendSumNum() + sendSumNum);
            dataStatisticItem.setUpsideSumNum(dataStatisticItem.getUpsideSumNum() + upsideSumNum);
        } else {
            dataStatisticItem = new DataStatisticItem();
            dataStatisticItem.setCreateTime(time);
            dataStatisticItem.setSessionSumNum(sessionSumNum);
            dataStatisticItem.setEffectiveSessionSumNum(effectiveSessionSumNum);
            dataStatisticItem.setSendSumNum(sendSumNum);
            dataStatisticItem.setUpsideSumNum(upsideSumNum);
            dataStatisticItemMap.put(time, dataStatisticItem);
        }
    }

    private UserStatisticsResp obtainUserStatistics(String userId, Date startTime, Date endTime) {
        UserStatisticsReq userStatisticsReq = new UserStatisticsReq();
        userStatisticsReq.setUserId(userId);
        userStatisticsReq.setStartDate(DateUtils.obtainDateStr(startTime));
        userStatisticsReq.setEndDate(DateUtils.obtainDateStr(endTime));
        return obtainUserStatistics(userStatisticsReq);
    }

    /**
     * 获取用户数统计
     */
    @Override
    public UserStatisticsResp obtainUserStatistics(UserStatisticsReq req) {
        UserStatisticsResp resp = new UserStatisticsResp();
        String userId = req.getUserId();
        String startDateS = req.getStartDate();
        String endDateS = req.getEndDate();
        Date startDate = DateUtils.obtainTime(DAY, START, DateUtils.obtainDate(startDateS));
        Date endDate = DateUtils.obtainTime(DAY, END, DateUtils.obtainDate(endDateS));
        String timeType = DateUtils.obtainTimeType(startDate, endDate);

        //1、获取指定时间的新增用户数
        List<RobotAccountDo> newUserList = newUserStatistics(startDate, endDate, userId);

        //2、查询活跃用户数
        Long allActiveUserSum = obtainDistinctMobileSum(startDate, endDate, userId);

        //3、更具时间段进行分组查询活跃用户数
        Map<String, Long> activeUserByTimeMap = obtainActiveUserSumByTime(timeType, startDate, endDate, userId);

        //4、更具账号进行分组查询活跃用户数
        Map<String, Long> activeUserByChatbotAccount = obtainActiveUserSumByChatbotAccount(startDate, endDate, userId);

        Map<String, UserStatisticsByChatbotIdItem> userMap = new HashMap<>();

        //新增用户信息组装
        Map<String, Long> newUserTimeMap = makeupStatisticsUseData(userMap, timeType, newUserList, activeUserByChatbotAccount);


        resp.setNewUsersSumNum(Long.parseLong(String.valueOf(newUserList.size())));
        resp.setActiveUsersSumNum(allActiveUserSum);
        resp.setUserList(new ArrayList<>(userMap.values()));
        resp.setActiveUserDateMap(activeUserByTimeMap);
        resp.setNewUserDateMap(newUserTimeMap);
        return resp;
    }

    @Override
    public UserStatisticsResp obtainUserStatisticsForConversation(UserStatisticsReq req) {
        UserStatisticsResp resp = new UserStatisticsResp();
        String userId = SessionContextUtil.getUser().getUserId();
        String startDateS = req.getStartDate();
        String endDateS = req.getEndDate();
        Date startDate = DateUtils.obtainTime(DAY, START, DateUtils.obtainDate(startDateS));
        Date endDate = DateUtils.obtainTime(DAY, END, DateUtils.obtainDate(endDateS));

        //1、查询改用户的所有从chatbot账号
        Map<String, String> chatbotInfoMap = obtainChatbotInfoMap(userId);

        //2、在聊天记录中查询指定时间段中的活跃用户信息
        Map<String, String> activeUserMap = getActiveUserByCustomerId(userId, startDate, endDate, chatbotInfoMap.keySet());
        if (CollectionUtils.isNotEmpty(activeUserMap)) {
            //3、查询活跃用户是否在历史时间存在
            List<String> checkoutPhoneIsOld = checkoutPhoneIsExist(userId, startDate, activeUserMap.keySet());
            UserStatisticsByChatbotIdItem item;
            long activeUsersSumNum = 0L;
            long newUsersSumNum = 0L;

            Map<String, UserStatisticsByChatbotIdItem> userStatisticsByChatbotIdItemMap = new HashMap<>();
            for (Map.Entry<String, String> entry : activeUserMap.entrySet()) {
                String phone = entry.getKey();
                String chatbotAccount = entry.getValue();
                activeUsersSumNum++;
                if (userStatisticsByChatbotIdItemMap.containsKey(chatbotAccount)) {
                    item = userStatisticsByChatbotIdItemMap.get(chatbotAccount);
                    item.setActiveUsersSumNum(item.getActiveUsersSumNum() + 1);
                    if (!checkoutPhoneIsOld.contains(phone)) {//是新增用户
                        item.setNewUsersSumNum(item.getNewUsersSumNum() + 1);
                        newUsersSumNum++;
                    }
                } else {
                    item = new UserStatisticsByChatbotIdItem();
                    item.setChatbotId(chatbotAccount);
                    item.setChatbotName(chatbotInfoMap.getOrDefault(chatbotAccount, null));
                    item.setActiveUsersSumNum(1L);
                    if (!checkoutPhoneIsOld.contains(phone)) {//是新增用户
                        item.setNewUsersSumNum(1L);
                        newUsersSumNum++;
                    } else {
                        item.setNewUsersSumNum(0L);
                    }
                    userStatisticsByChatbotIdItemMap.put(chatbotAccount, item);
                }
            }
            resp.setActiveUsersSumNum(activeUsersSumNum);
            resp.setNewUsersSumNum(newUsersSumNum);
            resp.setUserList(new ArrayList<>(userStatisticsByChatbotIdItemMap.values()));
        }
        return resp;
    }

    private List<String> checkoutPhoneIsExist(String customerId, Date startDate, Set<String> phones) {
        List<String> isExistPhones = new ArrayList<>();
        QueryWrapper<RobotRecordDo> robotRecordQueryWrapper = new QueryWrapper<>();
        robotRecordQueryWrapper.select("mobile_num as phone ");
        if (!Strings.isNullOrEmpty(customerId)) {
            robotRecordQueryWrapper.eq("creator", customerId);
        }
        if (startDate != null) {//结束时间
            robotRecordQueryWrapper.le("create_time", startDate);
        }
        robotRecordQueryWrapper.isNotNull("account");
        robotRecordQueryWrapper.in("mobile_num", phones);
        robotRecordQueryWrapper.groupBy("phone");
        List<Map<String, Object>> maps = robotRecordDao.selectMaps(robotRecordQueryWrapper);
        for (Map<String, Object> item : maps) {
            isExistPhones.add(item.get("phone").toString());
        }
        return isExistPhones;
    }

    private Map<String, String> getActiveUserByCustomerId(String customerId, Date startDate, Date endDate, Set<String> chatbotAccounts) {
        Map<String, String> activeUserByCustomerId = new HashMap<>();
        if (CollectionUtils.isNotEmpty(chatbotAccounts)) {
            QueryWrapper<RobotRecordDo> robotRecordQueryWrapper = new QueryWrapper<>();
            robotRecordQueryWrapper.select("mobile_num as phone," +
                    " account ");
            robotRecordQueryWrapper.ge("create_time", startDate);
            if (!Strings.isNullOrEmpty(customerId)) {
                robotRecordQueryWrapper.eq("creator", customerId);
            }
            if (endDate != null) {//结束时间
                robotRecordQueryWrapper.le("create_time", endDate);
            }
            robotRecordQueryWrapper.in("account", chatbotAccounts);
            robotRecordQueryWrapper.groupBy("phone,account");
            List<Map<String, Object>> maps = robotRecordDao.selectMaps(robotRecordQueryWrapper);
            for (Map<String, Object> item : maps) {
                activeUserByCustomerId.put(item.get("phone").toString(), item.get("account").toString());
            }
        }
        return activeUserByCustomerId;

    }

    /**
     * 获取指定用户的chatbot信息
     *
     * @param userId 用户ID
     * @return 返回信息
     */
    private Map<String, String> obtainChatbotInfoMap(String userId) {
        Map<String, String> chatbotInfoMap = new HashMap<>();
        if (!Strings.isNullOrEmpty(userId)) {
            List<AccountManagementResp> accountManagementResps = accountManagementApi.getChatbotAccountInfoByCustomerId(userId);
            if (!org.springframework.util.CollectionUtils.isEmpty(accountManagementResps)) {
                for (AccountManagementResp item : accountManagementResps) {
                    chatbotInfoMap.put(item.getChatbotAccount(), item.getAccountName() + "/" + item.getAccountType());
                }
            }
        }
        return chatbotInfoMap;
    }

    private Map<String, Long> obtainActiveUserSumByChatbotAccount(Date startDate, Date endDate, String customerId) {
        Map<String, Long> activeUserByChatbotAccount = new HashMap<>();
        QueryWrapper<RobotRecordDo> robotRecordQueryWrapper = new QueryWrapper<>();
        robotRecordQueryWrapper.select("account ," +
                " COUNT(DISTINCT(mobile_num)) AS sum ");
        robotRecordQueryWrapper.ge("create_time", startDate);
        if (!Strings.isNullOrEmpty(customerId)) {
            robotRecordQueryWrapper.eq("creator", customerId);
        }
        if (endDate != null) {//结束时间
            robotRecordQueryWrapper.le("create_time", endDate);
        }
        robotRecordQueryWrapper.groupBy("account");
        List<Map<String, Object>> maps = robotRecordDao.selectMaps(robotRecordQueryWrapper);
        log.info("maps {}", maps);
        for (Map<String, Object> item : maps) {
            activeUserByChatbotAccount.put(item.get("account").toString(), Long.valueOf(item.get("sum").toString()));
        }
        return activeUserByChatbotAccount;
    }


    private Map<String, Long> obtainActiveUserSumByTime(String timeType, Date startDate, Date endDate, String customerId) {
        Map<String, Long> activeUserByTimeMap = new HashMap<>();
        QueryWrapper<RobotRecordDo> robotRecordQueryWrapper = new QueryWrapper<>();
        if (HOUR.equals(timeType)) {
            robotRecordQueryWrapper.select("DATE_FORMAT(create_time,'%Y-%m-%d %H:00:00') AS hours," +
                    " COUNT(DISTINCT(mobile_num)) AS sum ");
        } else {
            robotRecordQueryWrapper.select("DATE_FORMAT(create_time,'%Y-%m-%d') AS hours," +
                    " COUNT(DISTINCT(mobile_num)) AS sum ");
        }

        robotRecordQueryWrapper.ge("create_time", startDate);
        if (!Strings.isNullOrEmpty(customerId)) {
            robotRecordQueryWrapper.eq("creator", customerId);
        }
        if (endDate != null) {//结束时间
            robotRecordQueryWrapper.le("create_time", endDate);
        }
        robotRecordQueryWrapper.groupBy("hours");
        List<Map<String, Object>> maps = robotRecordDao.selectMaps(robotRecordQueryWrapper);
        for (Map<String, Object> item : maps) {
            activeUserByTimeMap.put(item.get("hours").toString(), Long.valueOf(item.get("sum").toString()));
        }
        return activeUserByTimeMap;
    }

    private Long obtainDistinctMobileSum(Date startDate, Date endDate, String customerId) {
        QueryWrapper<RobotRecordDo> robotRecordQueryWrapper = new QueryWrapper<>();
        robotRecordQueryWrapper.select("distinct(mobile_num)");
        robotRecordQueryWrapper.ge("create_time", startDate);
        if (!Strings.isNullOrEmpty(customerId)) {
            robotRecordQueryWrapper.eq("creator", customerId);
        }
        if (endDate != null) {//结束时间
            robotRecordQueryWrapper.le("create_time", endDate);
        }
        return robotRecordDao.selectCount(robotRecordQueryWrapper);

    }


    private Map<String, Long> makeupStatisticsUseData(Map<String, UserStatisticsByChatbotIdItem> userMap,
                                                      String timeType, List<RobotAccountDo> newUserList, Map<String, Long> activeUserByChatbotAccount) {
        UserStatisticsByChatbotIdItem userStatisticsByChatbotIdItem;
        String account;
        Date createTime;
        Map<String, Long> userTimeMap = new HashMap<>();
        String timeMapKey;
        for (RobotAccountDo item : newUserList) {
            account = item.getAccount();
            createTime = item.getCreateTime();
            if (userMap.containsKey(account)) {
                userStatisticsByChatbotIdItem = userMap.get(account);
                userStatisticsByChatbotIdItem.setNewUsersSumNum(userStatisticsByChatbotIdItem.getNewUsersSumNum() + 1);
            } else {
                userStatisticsByChatbotIdItem = new UserStatisticsByChatbotIdItem();
                userStatisticsByChatbotIdItem.setChatbotId(item.getAccount());
                userStatisticsByChatbotIdItem.setActiveUsersSumNum(0L);
                userStatisticsByChatbotIdItem.setNewUsersSumNum(1L);
                userMap.put(account, userStatisticsByChatbotIdItem);
            }
            timeMapKey = DateUtils.obtainTimeMapKey(timeType, createTime);
            if (userTimeMap.containsKey(timeMapKey)) {
                Long count = userTimeMap.get(timeMapKey);
                userTimeMap.put(timeMapKey, count + 1L);
            } else {
                userTimeMap.put(timeMapKey, 1L);
            }
        }

        for (Map.Entry<String, Long> entry : activeUserByChatbotAccount.entrySet()) {
            account = entry.getKey();
            if (userMap.containsKey(account)) {
                userStatisticsByChatbotIdItem = userMap.get(account);
                userStatisticsByChatbotIdItem.setActiveUsersSumNum(entry.getValue());
            } else {
                userStatisticsByChatbotIdItem = new UserStatisticsByChatbotIdItem();
                userStatisticsByChatbotIdItem.setChatbotId(account);
                userStatisticsByChatbotIdItem.setActiveUsersSumNum(entry.getValue());
                userStatisticsByChatbotIdItem.setNewUsersSumNum(0L);
            }
        }
        return userTimeMap;
    }


    /**
     * 获取新增用户数 --6
     *
     * @param startDate 开始时间
     * @param endDate   结束时间
     * @param userId    用户id
     */
    private List<RobotAccountDo> newUserStatistics(Date startDate, Date endDate, String userId) {
        LambdaQueryWrapperX<RobotAccountDo> queryWrapper = new LambdaQueryWrapperX<>();
        queryWrapper.ge(RobotAccountDo::getCreateTime, startDate);
        if (endDate != null) {//结束时间
            queryWrapper.le(RobotAccountDo::getCreateTime, endDate);
        }
        if (org.apache.commons.lang.StringUtils.isNotEmpty(userId)) {
            queryWrapper.eq(RobotAccountDo::getCreator, userId);
        }
        return robotAccountDao.selectList(queryWrapper);
    }

    @Override
    public RobotServiceAnalysisResp chatbotServiceAnalysis() {
        QueryWrapper wrapper = new QueryWrapper();
        wrapper.eq("deleted", 0);

        //场景统计
        Long scenariosNum = robotSceneNodeDao.selectCount(wrapper);
        //流程统计
        Long processesNum = robotProcessSettingNodeDao.selectCount(wrapper);

        //终端用户量
        QueryWrapper<RobotRecordDo> robotRecordQueryWrapper = new QueryWrapper<>();
        robotRecordQueryWrapper.select("distinct(mobile_num)");
        Long terminalUsersNum = robotRecordDao.selectCount(robotRecordQueryWrapper);

        //获取所有5G 账户
        long accounts = accountManagementApi.selectCountAll();

        RobotServiceAnalysisResp robotServiceAnalysisResp = new RobotServiceAnalysisResp();
        robotServiceAnalysisResp.setProcessesNum(processesNum);
        robotServiceAnalysisResp.setScenariosNum(scenariosNum);
        robotServiceAnalysisResp.setTerminalUsersNum(terminalUsersNum);
        robotServiceAnalysisResp.setAssociatedMessageAccounts(accounts);
        return robotServiceAnalysisResp;

    }


    @Override
    public ConversationalInteractionResp converInteractYesterdayLineChart(OperatorTypeReq req) {
        ConversationalInteractionResp resp = new ConversationalInteractionResp();
        String userId = !isAdmin() ? SessionContextUtil.getUser().getUserId() : null;
        List<String> times = new ArrayList<>();
        Date startDate = DateUtils.obtainTime(DAY, START, DateUtils.obtainDate(req.getStartTime()));
        Date endDate = DateUtils.obtainTime(DAY, END, DateUtils.obtainDate(req.getEndTime()));
        //获取数据
        Map<String, DataStatisticItem> dataStatistics = obtainDataStatistics(userId, startDate, endDate, times, req.getOperatorType());
        String timeType = DateUtils.obtainTimeType(startDate, endDate);
        //整理数据
        DataStatisticItem dataStatisticItem;
        List<DataStatisticItem> dataStatisticItems = new ArrayList<>();
        long effectiveSessionSumNumAll = 0L;
        long sessionSumNumAll = 0L;
        long sendSumNumAll = 0L;
        long upsideSumNumAll = 0L;
        for (String item : times) {
            if (dataStatistics.containsKey(item)) {
                dataStatisticItem = dataStatistics.get(item);
                effectiveSessionSumNumAll = effectiveSessionSumNumAll + dataStatisticItem.getEffectiveSessionSumNum();
                sessionSumNumAll = sessionSumNumAll + dataStatisticItem.getSessionSumNum();
                sendSumNumAll = sendSumNumAll + dataStatisticItem.getSendSumNum();
                upsideSumNumAll = upsideSumNumAll + dataStatisticItem.getUpsideSumNum();
            } else {
                dataStatisticItem = new DataStatisticItem();
                dataStatisticItem.setEffectiveSessionSumNum(0L);
                dataStatisticItem.setSessionSumNum(0L);
                dataStatisticItem.setSendSumNum(0L);
                dataStatisticItem.setUpsideSumNum(0L);
                dataStatisticItem.setCreateTime(item);
            }
            dataStatisticItem.setShowTime(DateUtils.obtainSpecificFormatTime(item, timeType, "SHOW"));
            dataStatisticItem.setHoverTime(DateUtils.obtainSpecificFormatTime(item, timeType, "HOVER"));
            dataStatisticItems.add(dataStatisticItem);
        }
        resp.setList(dataStatisticItems);
        resp.setEffectiveSessionSumNum(effectiveSessionSumNumAll);
        resp.setSendSumNum(sendSumNumAll);
        resp.setSessionSumNum(sessionSumNumAll);
        resp.setUpsideSumNum(upsideSumNumAll);
        return resp;
    }


    @Override
    public PageResult converInteractYesterdayPage(OperatorTypePageReq req) {
        PageResult pageResult = new PageResult<>();
        String userId = null;
        List<String> times = new ArrayList<>();
        Date startDate = DateUtils.obtainTime(DAY, START, DateUtils.obtainDate(req.getStartTime()));
        Date endDate = DateUtils.obtainTime(DAY, END, DateUtils.obtainDate(req.getEndTime()));
        String timeType = DateUtils.obtainTimeType(startDate, endDate);
        //获取数据
        Map<String, DataStatisticItem> dataStatistics = obtainDataStatistics(userId, startDate, endDate, times, req.getOperatorType());
        //整理数据
        DataStatisticItem dataStatisticItem;
        List<DataStatisticItem> dataStatisticItems = new ArrayList<>();
        for (String item : times) {
            if (dataStatistics.containsKey(item)) {
                dataStatisticItem = dataStatistics.get(item);
            } else {
                dataStatisticItem = new DataStatisticItem();
                dataStatisticItem.setEffectiveSessionSumNum(0L);
                dataStatisticItem.setSessionSumNum(0L);
                dataStatisticItem.setSendSumNum(0L);
                dataStatisticItem.setUpsideSumNum(0L);
                dataStatisticItem.setCreateTime(item);
            }
            dataStatisticItem.setShowTime(DateUtils.obtainSpecificFormatTime(item, timeType, "SHOW"));
            dataStatisticItem.setHoverTime(DateUtils.obtainSpecificFormatTime(item, timeType, "HOVER"));
            dataStatisticItems.add(dataStatisticItem);
        }
        Collections.reverse(dataStatisticItems);

        List<DataStatisticItem> dataStatisticItemsResult = dataStatisticItems.stream().skip((long) (req.getPageNo() - 1) * req.getPageSize())
                .limit(req.getPageSize()).collect(Collectors.toList());
        pageResult.setList(dataStatisticItemsResult);
        pageResult.setTotal((long) dataStatisticItems.size());
        return pageResult;
    }

    @Override
    public ProcessQuantitySumResp processScenceNum(SortReq req) {
        ProcessQuantitySumResp processQuantitySumResp = new ProcessQuantitySumResp();
        HashMap<String, Object> map = new HashMap<>();
        if (StringUtils.isNotEmpty(req.getChatBotId())) {
            map.put("chatBotId", req.getChatBotId());
        }
        map.put("creator", SessionContextUtil.getUser().getUserId());
        Long sceneNum = robotSceneNodeDao.getSceneNum(map);
        Long processNum = robotSceneNodeDao.getProcessNum(map);
        processQuantitySumResp.setScenceNum(sceneNum);
        processQuantitySumResp.setProcessNum(processNum);
        return processQuantitySumResp;
    }

    @Override
    public List<ConversationalHeadUpChartResp> converInteractHeadUpChart(OperatorTypeReq req) {
        List<String> times;
        int timeFlag;

        List<ConversationalChartResp> conversationalInteractionvResps = obtainChartCount(req.getStartTime(), req.getEndTime());
        if (req.getStartTime().equals(req.getEndTime())) {
            times = DateUtil.findHours(req.getStartTime() + " 0:00:00", req.getEndTime() + " 23:00:00");
            timeFlag = 1;
        } else {
            times = DateUtil.findEveryDay(req.getStartTime() + " 0:00:00", req.getEndTime() + " 0:00:00");
            timeFlag = 2;
        }

        HashMap<String, Long> allMap = new HashMap<>();
        if (CollectionUtils.isNotEmpty(conversationalInteractionvResps)) {
            for (ConversationalChartResp conversationalChartResp : conversationalInteractionvResps) {
                allMap.put(conversationalChartResp.getCreateTime() + conversationalChartResp.getOperatorType(), conversationalChartResp.getUpsideSumNum());
            }
        }
        ArrayList<ConversationalHeadUpChartResp> objects = new ArrayList<>();

        for (int i = 0; i < times.size(); i++) {
            //0 硬核桃 1联通 2移动 3电信
            Long moveSum = 0L;//移动总数
            Long unicomSum = 0L;//联通总数
            Long walnutSum = 0L;//硬核桃总数
            Long telecomSum = 0L;//电信总数
            ConversationalHeadUpChartResp conversationalHeadUpChartResp = new ConversationalHeadUpChartResp();
            conversationalHeadUpChartResp.setCreateTime(times.get(i));
            if (allMap.get(times.get(i) + 0) != null) {
                walnutSum = allMap.get(times.get(i) + 0);
            }
            if (allMap.get(times.get(i) + 1) != null) {
                unicomSum = allMap.get(times.get(i) + 1);
            }
            if (allMap.get(times.get(i) + 2) != null) {
                moveSum = allMap.get(times.get(i) + 2);
            }
            if (allMap.get(times.get(i) + 3) != null) {
                telecomSum = allMap.get(times.get(i) + 3);
            }
            conversationalHeadUpChartResp.setMoveSumNum(moveSum);
            conversationalHeadUpChartResp.setTelecomSumNum(telecomSum);
            conversationalHeadUpChartResp.setUpsideSumNum(moveSum + telecomSum + unicomSum + walnutSum);
            conversationalHeadUpChartResp.setUnicomSumNum(unicomSum);
            conversationalHeadUpChartResp.setWalnutSumNum(walnutSum);
            conversationalHeadUpChartResp.setShowTime(DateUtil.getTimeString(times.get(i), timeFlag));
            objects.add(conversationalHeadUpChartResp);
        }
        return objects;
    }

    private List<ConversationalChartResp> obtainChartCount(String startTime, String endTime) {
        List<ConversationalChartResp> conversationalChartResps = new ArrayList<>();
        ConversationalChartResp conversationalChartResp;
        boolean isHour = startTime.equals(endTime);
        startTime = startTime + " 00:00:00";
        endTime = endTime + " 23:59:59";
        QueryWrapper<ConversationalQuantityStatisticDo> queryWrapper = new QueryWrapper<>();
        if (isHour) {
            queryWrapper.select("DATE_FORMAT(hours,'%Y-%m-%d %H:00:00') AS period," +
                    "SUM(upside_num) AS upsideSumNum," +
                    "operator_type AS operatorType ");
        } else {
            queryWrapper.select("DATE_FORMAT(hours,'%Y-%m-%d 00:00:00') AS period," +
                    "SUM(upside_num) AS upsideSumNum," +
                    "operator_type AS operatorType ");
        }

        queryWrapper.ge("hours", startTime);
        queryWrapper.le("hours", endTime);
        queryWrapper.groupBy("operatorType,period");
        List<Map<String, Object>> maps = conversationalQuantityStatisticDao.selectMaps(queryWrapper);
        for (Map<String, Object> item : maps) {
            conversationalChartResp = new ConversationalChartResp();
            conversationalChartResp.setCreateTime(item.get("period").toString());
            conversationalChartResp.setOperatorType((Integer) item.get("operatorType"));
            conversationalChartResp.setUpsideSumNum(Long.valueOf(item.get("upsideSumNum").toString()));
            conversationalChartResps.add(conversationalChartResp);
        }

        return conversationalChartResps;
    }

    public List<ProcessQuantityStatisticDo> obtainProcessQuantityHoursByTimeOrUserId(String userId, Date startDate, Date endDate, Long robotSceneNodeId, Long robotProcessSettingNodeId, String chatbotId, Integer operatorType) {
        LambdaQueryWrapperX<ProcessQuantityStatisticDo> queryWrapper = new LambdaQueryWrapperX<>();
        queryWrapper.ge(ProcessQuantityStatisticDo::getHours, startDate);
        if (endDate != null) {//结束时间
            queryWrapper.le(ProcessQuantityStatisticDo::getHours, endDate);
        }
        if (StringUtils.isNotEmpty(userId)) {//userID
            queryWrapper.eq(ProcessQuantityStatisticDo::getCreator, userId);
        }
        if (StringUtils.isNotEmpty(chatbotId)) {//chatbotId
            queryWrapper.eq(ProcessQuantityStatisticDo::getChatbotAccountId, chatbotId);
        }
        if (robotSceneNodeId != null && robotSceneNodeId > 0) {
            queryWrapper.eq(ProcessQuantityStatisticDo::getRobotSceneNodeId, robotSceneNodeId);
        }
        if (operatorType != null && operatorType >= 0) {
            queryWrapper.eq(ProcessQuantityStatisticDo::getOperatorType, operatorType);
        }
        if (robotProcessSettingNodeId != null && robotProcessSettingNodeId > 0) {
            queryWrapper.eq(ProcessQuantityStatisticDo::getRobotProcessSettingNodeId, robotProcessSettingNodeId);
        }
        return processQuantityStatisticDao.selectList(queryWrapper);
    }

    private List<ConversationalQuantityStatisticDo> obtainConversationalInteractionHoursByTimeOrUserId(String userId, Date startDate, Date endDate, Integer operatorType) {
        LambdaQueryWrapperX<ConversationalQuantityStatisticDo> queryWrapper = new LambdaQueryWrapperX<>();
        queryWrapper.ge(ConversationalQuantityStatisticDo::getHours, startDate);
        if (endDate != null) {//结束时间
            queryWrapper.le(ConversationalQuantityStatisticDo::getHours, endDate);
        }
        if (StringUtils.isNotEmpty(userId)) {//userID
            queryWrapper.eq(ConversationalQuantityStatisticDo::getCreator, userId);
        }
        if (operatorType != null && operatorType >= 0) {
            queryWrapper.eq(ConversationalQuantityStatisticDo::getOperatorType, operatorType);
        }
        return conversationalQuantityStatisticDao.selectList(queryWrapper);
    }

    @Override
    public List<ProcessQuantityStatisticDo> obtainProcessQuantityByTimeOrUserId(String userId, Date startDate, Date endDate, String chatbotId) {
        return obtainProcessQuantityDaysByTimeOrUserId(userId, startDate, endDate, null, null, chatbotId, null);
    }

    private List<ProcessQuantityStatisticDo> obtainProcessQuantityDaysByTimeOrUserId(String userId, Date startDate, Date endDate, Long robotSceneNodeId, Long robotProcessSettingNodeId, String chatbotId, Integer operatorType) {
        LambdaQueryWrapperX<ProcessQuantityStatisticDo> queryWrapper1 = new LambdaQueryWrapperX<>();
        queryWrapper1.ge(ProcessQuantityStatisticDo::getHours, startDate);
        queryWrapper1.eq(ProcessQuantityStatisticDo::getDeleted, 0);
        if (endDate != null) {//结束时间
            queryWrapper1.le(ProcessQuantityStatisticDo::getHours, endDate);
        }
        if (StringUtils.isNotEmpty(userId)) {//结束时间
            queryWrapper1.eq(ProcessQuantityStatisticDo::getCreator, userId);
        }
        if (StringUtils.isNotEmpty(chatbotId)) {//结束时间
            queryWrapper1.eq(ProcessQuantityStatisticDo::getChatbotAccountId, chatbotId);
        }
        if (operatorType != null && operatorType >= 0) {
            queryWrapper1.eq(ProcessQuantityStatisticDo::getOperatorType, operatorType);
        }
        if (robotSceneNodeId != null && robotSceneNodeId > 0) {
            queryWrapper1.eq(ProcessQuantityStatisticDo::getRobotSceneNodeId, robotSceneNodeId);
        }
        if (robotProcessSettingNodeId != null && robotProcessSettingNodeId > 0) {
            queryWrapper1.eq(ProcessQuantityStatisticDo::getRobotProcessSettingNodeId, robotProcessSettingNodeId);
        }
        return processQuantityStatisticDao.selectList(queryWrapper1);
    }

    private List<ConversationalQuantityStatisticDo> obtainConversationalInteractionDaysByTimeOrUserId(String userId, Date startDate, Date endDate, Integer operatorType) {
        LambdaQueryWrapperX<ConversationalQuantityStatisticDo> queryWrapper1 = new LambdaQueryWrapperX<>();
        queryWrapper1.ge(ConversationalQuantityStatisticDo::getHours, startDate);
        queryWrapper1.eq(ConversationalQuantityStatisticDo::getDeleted, 0);
        if (endDate != null) {//结束时间
            queryWrapper1.le(ConversationalQuantityStatisticDo::getHours, endDate);
        }
        if (StringUtils.isNotEmpty(userId)) {//结束时间
            queryWrapper1.eq(ConversationalQuantityStatisticDo::getCreator, userId);
        }
        if (operatorType != null && operatorType >= 0) {
            queryWrapper1.eq(ConversationalQuantityStatisticDo::getOperatorType, operatorType);
        }
        return conversationalQuantityStatisticDao.selectList(queryWrapper1);
    }

    private boolean isAdmin() {
        String userId = SessionContextUtil.getUser().getUserId();
        if (org.apache.commons.lang3.StringUtils.isNotEmpty(userId)) {
            if (userId.length() == 15) {//用户ID
                return false;
            } else {
                CspInfoResp cspInfo = cspCustomerApi.getCspInfo(userId);
                return Strings.isNullOrEmpty(cspInfo.getCspId());
            }
        }
        return false;
    }


}
