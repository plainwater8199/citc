package com.citc.nce.dataStatistics.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.citc.nce.authcenter.csp.customer.account.CustomerAccountApi;
import com.citc.nce.authcenter.csp.customer.account.vo.Query5GAccountListReq;
import com.citc.nce.authcenter.csp.customer.account.vo.Query5GAccountListResp;
import com.citc.nce.dataStatistics.dao.ConversationalQuantityStatisticDao;
import com.citc.nce.dataStatistics.dao.ProcessQuantityStatisticDao;
import com.citc.nce.dataStatistics.dto.ConversationalTemporaryStatisticDto;
import com.citc.nce.dataStatistics.dto.MsgStatisticsDto;
import com.citc.nce.dataStatistics.dto.ProcessTemporaryStatisticDto;
import com.citc.nce.dataStatistics.entity.ConversationalQuantityStatisticDo;
import com.citc.nce.dataStatistics.entity.ProcessQuantityStatisticDo;
import com.citc.nce.dataStatistics.service.DataStatisticsScheduleService;
import com.citc.nce.dataStatistics.vo.req.StatisticScheduleReq;
import com.citc.nce.dataStatistics.vo.resp.StatisticScheduleResp;
import com.citc.nce.mybatis.core.query.LambdaQueryWrapperX;
import com.citc.nce.robot.dao.TemporaryStatisticsDao;
import com.citc.nce.tenant.robot.dao.MsgQuantityStatisticDao;
import com.citc.nce.tenant.robot.dao.MsgRecordDao;
import com.citc.nce.tenant.robot.entity.MsgQuantityStatisticsDo;
import com.citc.nce.tenant.robot.entity.MsgRecordDo;
import com.citc.nce.utils.DateUtils;
import com.google.common.base.Strings;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class DataStatisticsScheduleServiceImpl implements DataStatisticsScheduleService {

    @Resource
    private TemporaryStatisticsDao temporaryStatisticsDao;
    @Resource
    private ProcessQuantityStatisticDao processQuantityStatisticDao;
    @Resource
    private ConversationalQuantityStatisticDao conversationalQuantityStatisticDao;

    @Resource
    private MsgRecordDao msgRecordDao;

    @Resource
    private MsgQuantityStatisticDao msgQuantityStatisticDao;

    @Resource
    private CustomerAccountApi customerAccountApi;


    private static final String START = "start";
    private static final String END = "end";
    private static final String UNKNOW = "0";
    private static final String SUCCESS = "1";
    private static final String FAIL = "2";
    private static final String READ = "6";




    @Override
    public StatisticScheduleResp statisticDataReset(StatisticScheduleReq req) {
        StatisticScheduleResp resp = new StatisticScheduleResp();
        String userId = req.getUserId();
        try {
            Date startDate = DateUtils.obtainDate(req.getStartDate());
            Date endDate = DateUtils.obtainDate(req.getEndDate());
            statisticPerHour(userId,startDate,endDate);//统计每小时
            resp.setResult("执行成功！");
        } catch (Exception e) {
            e.printStackTrace();
            resp.setResult("执行异常！");
        }
        return resp;
    }

    @Override
    public void  statisticPerHour(String customerId, Date startDate, Date endDate) {
//        //机器人统计
        robotStatistic(customerId,startDate,endDate);
        //消息统计
        msgStatistic(customerId,startDate,endDate);

    }


    /**
     * 从消息记录中将统计结果同步到统计表中
     * @param customerId 客户形象
     * @param startDate 开始时间
     * @param endDate 结束时间
     */
    private void msgStatistic(String customerId, Date startDate, Date endDate) {
        startDate = DateUtils.obtainDayTime(START,startDate);
        endDate = DateUtils.obtainDayTime(END,endDate);

        //更具时间查询所有内容
        List<MsgStatisticsDto> queryList = queryStatistic(customerId,startDate,endDate);
        //统计查询后的结果集
        Map<String, MsgQuantityStatisticsDo> msgQuantityStatisticsDoHashMap = dealWithQueryResult(queryList);
        //已经统计的结果集
        Map<String,MsgQuantityStatisticsDo> isHaveMsgQuantityStatisticsDoHashMap = queryIsHaveStatisticsResult(customerId,startDate,endDate);
        //保存的结果集
        List<MsgQuantityStatisticsDo> saveList = new ArrayList<>();
        //更新的结果集
        List<MsgQuantityStatisticsDo> updateList = new ArrayList<>();

        //检查是更新用户数量，还是保存用户，如果是更新用户，直接将历史ID和历史的创建时间更改为最新的ID和最新的时间
        if(!CollectionUtils.isEmpty(msgQuantityStatisticsDoHashMap)){
            for(Map.Entry<String,MsgQuantityStatisticsDo> Item : msgQuantityStatisticsDoHashMap.entrySet()){
                MsgQuantityStatisticsDo queryDo = Item.getValue();
                if(isHaveMsgQuantityStatisticsDoHashMap.containsKey(Item.getKey())){
                    MsgQuantityStatisticsDo dbDo = isHaveMsgQuantityStatisticsDoHashMap.get(Item.getKey());
                    queryDo.setId(dbDo.getId());//传入ID
                    queryDo.setCreateTime(dbDo.getCreateTime());//更新最新时间
                    updateList.add(queryDo);
                }else{
                    saveList.add(queryDo);
                }
            }
            //3、更新数据
            if(!saveList.isEmpty()){
                msgQuantityStatisticDao.insertBatch(saveList);
            }
            if(!updateList.isEmpty()){
                for (MsgQuantityStatisticsDo statisticsDo : updateList) {
                    String creator = statisticsDo.getCreator();
                    statisticsDo.setCreator(null);
                    msgQuantityStatisticDao.update(
                            statisticsDo,
                            Wrappers.<MsgQuantityStatisticsDo>lambdaUpdate()
                                    .eq(MsgQuantityStatisticsDo::getCreator,creator)
                                    .eq(MsgQuantityStatisticsDo::getId,statisticsDo.getId())
                    );
                }
            }
        }

    }
    /**
     * 查询已经统计好的结果集
     * @param customerId 客户形象
     * @param startDate 开始时间
     * @param endDate 结束时间
     */
    private Map<String, MsgQuantityStatisticsDo> queryIsHaveStatisticsResult(String customerId, Date startDate, Date endDate) {
        Map<String,MsgQuantityStatisticsDo> isHaveMsgQuantityStatisticsDoHashMap = new HashMap<>();
        String statisticKey;
        //2、读取旧数据
        LambdaQueryWrapperX<MsgQuantityStatisticsDo> msgQuantityStatisticsDoLambdaQueryWrapperX = new LambdaQueryWrapperX<>();
        msgQuantityStatisticsDoLambdaQueryWrapperX.ge(MsgQuantityStatisticsDo::getSendTime,startDate);
        if (endDate != null) {//结束时间
            msgQuantityStatisticsDoLambdaQueryWrapperX.le(MsgQuantityStatisticsDo::getSendTime, endDate);
        }
        if (!Strings.isNullOrEmpty(customerId)) {
            msgQuantityStatisticsDoLambdaQueryWrapperX.eq(MsgQuantityStatisticsDo::getCreator, customerId);
        }
        List<MsgQuantityStatisticsDo> msgQuantityStatisticsDos = msgQuantityStatisticDao.selectList(msgQuantityStatisticsDoLambdaQueryWrapperX);

        for(MsgQuantityStatisticsDo item : msgQuantityStatisticsDos){
            statisticKey = DateUtils.obtainDateStr(item.getSendTime())+"|"+item.getOperatorCode()+"|"+item.getPlanId()
                    +"|"+item.getPlanDetailId()+"|"+item.getCreator()+"|"+item.getAccountType()+"|"+item.getAccountId()+"|"+item.getMessageResource()+"|"+item.getAccountDictCode();
            isHaveMsgQuantityStatisticsDoHashMap.put(statisticKey,item);
        }
        return isHaveMsgQuantityStatisticsDoHashMap;

    }

    /**
     * 对统计的结果进行整理
     * @param queryList 查询的统计结果
     * @return 结果
     */
    private Map<String, MsgQuantityStatisticsDo> dealWithQueryResult(List<MsgStatisticsDto> queryList) {
        //查询所有的5G消息账号
//        Map<String, Map<String, String>> accountMap = get5GAccountMap();

        Map<String, MsgQuantityStatisticsDo> msgQuantityStatisticsDoHashMap = new HashMap<>();
        MsgQuantityStatisticsDo msgQuantityStatisticsDo;
        String statisticKey;
        //1、生成最新的数据
        for(MsgStatisticsDto item : queryList){
//            String account = getAccountId(item, accountMap);
            statisticKey = DateUtils.obtainDateStr(item.getSendTime())+"|"+item.getOperatorCode()+"|"+item.getPlanId()
                    +"|"+item.getPlanDetailId()+"|"+item.getCreator()+"|"+item.getAccountType()+"|"+item.getAccountId()+"|"+item.getMessageResource()+"|"+item.getAccountDictCode();

            if(msgQuantityStatisticsDoHashMap.containsKey(statisticKey)){
                msgQuantityStatisticsDo = msgQuantityStatisticsDoHashMap.get(statisticKey);
                msgQuantityStatisticsDo.setSendNum(msgQuantityStatisticsDo.getSendNum() + item.getNum());
                switch (item.getSendResult()){
                    case UNKNOW :
                        msgQuantityStatisticsDo.setUnknowNum(item.getNum());
                        continue;
                    case SUCCESS : //包含了已读
                        msgQuantityStatisticsDo.setSuccessNum(msgQuantityStatisticsDo.getSuccessNum() + item.getNum());
                        continue;
                    case FAIL :
                        msgQuantityStatisticsDo.setFailedNum(item.getNum());
                        continue;
                    case READ :
                        msgQuantityStatisticsDo.setReadNum(item.getNum());
                        msgQuantityStatisticsDo.setSuccessNum(msgQuantityStatisticsDo.getSuccessNum() + item.getNum());
                        continue;
                    default:
                }

            }else{
                msgQuantityStatisticsDo = new MsgQuantityStatisticsDo();
                msgQuantityStatisticsDo.setUnknowNum(UNKNOW.equals(item.getSendResult()) ? item.getNum() : 0L);
                msgQuantityStatisticsDo.setSuccessNum(Arrays.asList(SUCCESS,READ).contains(item.getSendResult())? item.getNum() : 0L);
                msgQuantityStatisticsDo.setFailedNum(FAIL.equals(item.getSendResult()) ? item.getNum() : 0L);
                msgQuantityStatisticsDo.setReadNum(READ.equals(item.getSendResult()) ? item.getNum() : 0L);
                msgQuantityStatisticsDo.setSendNum(item.getNum());//排除机器人发送
                msgQuantityStatisticsDo.setOperatorCode(item.getOperatorCode());
                msgQuantityStatisticsDo.setPlanId(item.getPlanId());
                msgQuantityStatisticsDo.setPlanDetailId(item.getPlanDetailId());
                msgQuantityStatisticsDo.setMessageResource(item.getMessageResource());
                msgQuantityStatisticsDo.setSendTime(item.getSendTime());
                msgQuantityStatisticsDo.setAccountType(item.getAccountType());
                msgQuantityStatisticsDo.setAccountId(item.getAccountId());
                msgQuantityStatisticsDo.setAccountDictCode(item.getAccountDictCode());
                msgQuantityStatisticsDo.setCreateTime(new Date());
                msgQuantityStatisticsDo.setUpdateTime(new Date());
                msgQuantityStatisticsDo.setCreator(item.getCreator());
                msgQuantityStatisticsDo.setUpdater(item.getCreator());
                msgQuantityStatisticsDoHashMap.put(statisticKey,msgQuantityStatisticsDo);
            }
        }
        return msgQuantityStatisticsDoHashMap;
    }

    /**
     * 获取具体的5G账号
     * @param item 统计信息
     * @param accountMap  5G账号map
     * @return 结果信息
     */
    private String getAccountId(MsgStatisticsDto item, Map<String, Map<String, String>> accountMap) {
        if(item!= null){
            if(item.getAccountType() != null && item.getAccountType() == 1 && accountMap.containsKey(item.getCreator())){
                Map<String, String> accountOperatorMap = accountMap.get(item.getCreator());
                if(!CollectionUtils.isEmpty(accountOperatorMap)){
                    return accountOperatorMap.getOrDefault(item.getOperatorCode(),null);
                }else{
                    return item.getAccountId();
                }
            }else{
                return item.getAccountId();
            }
        }
        return null;
    }

    /**
     * 获取5G账号列表
     * @return 5G账号列表
     */
    private Map<String, Map<String, String>> get5GAccountMap() {
        Query5GAccountListReq req = new Query5GAccountListReq();
        Query5GAccountListResp resp = customerAccountApi.query5GAccountList(req);
        if(resp != null){
            return resp.getAccountMap();
        }
        return new HashMap<>();
    }


    private List<MsgStatisticsDto> queryStatistic(String customerId, Date startDate, Date endDate) {
        QueryWrapper<MsgRecordDo> msgRecordDoQueryWrapper = new QueryWrapper<>();
        msgRecordDoQueryWrapper.select("COUNT(1) num," +
                "DATE_FORMAT(send_time,'%Y-%m-%d %H:00:00') AS sendTime,"+
                "operator_code AS operatorCode,"+
                "message_resource AS messageResource,"+
                "send_result AS sendResult,"+
                "plan_id AS planId,"+
                "plan_detail_id AS planDetailId,"+
                "creator AS creator,"+
                "account_type AS accountType,"+
                "account_dict_code AS accountDictCode,"+
                "account_id AS accountId ");
        msgRecordDoQueryWrapper.ge("send_time",startDate);
        if (endDate != null) {//结束时间
            msgRecordDoQueryWrapper.le("send_time", endDate);
        }
        if (!Strings.isNullOrEmpty(customerId)) {
            msgRecordDoQueryWrapper.eq("creator", customerId);
        }
        msgRecordDoQueryWrapper.groupBy("sendTime,operatorCode,messageResource,sendResult,planId,planDetailId,creator,accountType,accountDictCode,accountId");
        List<Map<String, Object>> maps = msgRecordDao.selectMaps(msgRecordDoQueryWrapper);
        return getStatisticQueryResult(maps);
    }

    /**
     * 处理统计查询结果
     * @param maps 统计查询结果
     * @return 处理后结果
     */
    private List<MsgStatisticsDto> getStatisticQueryResult(List<Map<String, Object>> maps) {
        List<MsgStatisticsDto> msgStatisticsDtos = new ArrayList<>();
        if(!CollectionUtils.isEmpty(maps)){
            MsgStatisticsDto msgStatisticsDto;
            for(Map<String, Object> item : maps){
                msgStatisticsDto = new MsgStatisticsDto();
                msgStatisticsDto.setNum((Long) item.get("num"));
                msgStatisticsDto.setSendTime(DateUtils.obtainDate(item.get("sendTime").toString()));
                msgStatisticsDto.setOperatorCode(item.get("operatorCode") == null ? null : item.get("operatorCode")+"");
                msgStatisticsDto.setSendResult(item.get("sendResult") == null ? null : item.get("sendResult")+"");
                msgStatisticsDto.setPlanId(item.get("planId") == null ? null : (Long) item.get("planId"));
                msgStatisticsDto.setPlanDetailId(item.get("planDetailId") == null ? null : (Long) item.get("planDetailId"));
                msgStatisticsDto.setMessageResource(item.get("messageResource") != null ? (Integer) item.get("messageResource") : null );
                msgStatisticsDto.setCreator((String) item.get("creator"));
                msgStatisticsDto.setAccountDictCode(item.getOrDefault("accountDictCode","")+"");
                msgStatisticsDto.setAccountType(item.get("accountType") == null ? null : (Integer) item.get("accountType"));
                msgStatisticsDto.setAccountId(item.get("accountId") == null ? null : (String) item.get("accountId"));
                msgStatisticsDtos.add(msgStatisticsDto);
            }
        }
        return msgStatisticsDtos;
    }


    private void robotStatistic(String customerId, Date startDate, Date endDate) {
        // 1-流程触发，2-流程完成，3-兜底回复，4-会话量，5-有效会话，8-发行量，9-上行量
        //1、查询基础数据
        //流程统计
        List<Integer> typePList = Arrays.asList(1,2,3);
        //会话统计
        List<Integer> typeCList = Arrays.asList(5,4,6,7,8,9);

        //1、查询所有数据
        List<ProcessTemporaryStatisticDto> processTemporaryStatisticDtos = temporaryStatisticsDao.findAllProcessDateByTime(customerId,typePList,startDate,endDate);
        if(!CollectionUtils.isEmpty(processTemporaryStatisticDtos)){
            processTemporaryStatisticDtos = processTemporaryStatisticDtos.stream().filter(i -> i.getCustomerId().length() == 15).collect(Collectors.toList());
            if(!CollectionUtils.isEmpty(processTemporaryStatisticDtos)){
                List<ProcessQuantityStatisticDo> processQuantityStatisticDos = obtainOldProcessQuantityStatistic(customerId,startDate,endDate);
                updateProcessData(processTemporaryStatisticDtos,processQuantityStatisticDos);
            }
        }
        List<ConversationalTemporaryStatisticDto> conversationalTemporaryStatisticDtos = temporaryStatisticsDao.findAllConversationalDateByTime(customerId,typeCList,startDate,endDate);
        if(!CollectionUtils.isEmpty(conversationalTemporaryStatisticDtos)){
            conversationalTemporaryStatisticDtos = conversationalTemporaryStatisticDtos.stream().filter(i -> i.getCustomerId().length() == 15).collect(Collectors.toList());
            if(!CollectionUtils.isEmpty(conversationalTemporaryStatisticDtos)){
                List<ConversationalQuantityStatisticDo> conversationalQuantityStatisticDos = obtainOldConversationalQuantityStatistic(customerId,startDate,endDate);
                updateConversationalData(conversationalTemporaryStatisticDtos,conversationalQuantityStatisticDos);
            }
        }

    }

    private void updateConversationalData(List<ConversationalTemporaryStatisticDto> conversationalTemporaryStatisticDtos, List<ConversationalQuantityStatisticDo> conversationalQuantityStatisticDos) {

        Map<String,ConversationalQuantityStatisticDo> saveMap = new HashMap<>();
        ConversationalQuantityStatisticDo conversationalQuantityStatisticDo;

        for(ConversationalTemporaryStatisticDto item : conversationalTemporaryStatisticDtos){
            String hours = item.getHours();
            Integer operatorType = item.getOperatorType();
            String chatbotId = item.getChatbotAccountId();
            String customerIdN = item.getCustomerId();
            String key = ""+customerIdN+hours+operatorType+chatbotId;
            if(saveMap.containsKey(key)){
                conversationalQuantityStatisticDo = saveMap.get(key);
                if(4 == item.getType()){//会话量
                    conversationalQuantityStatisticDo.setSessionNum(conversationalQuantityStatisticDo.getSessionNum() + item.getNum());
                }else if(5 == item.getType()){//有效会话
                    conversationalQuantityStatisticDo.setEffectiveSessionNum(item.getNum());
                }else if(6 == item.getType()){//
                    conversationalQuantityStatisticDo.setNewUsersNum(item.getNum());
                }else if(7 == item.getType()){//
                    conversationalQuantityStatisticDo.setActiveUsersNum(item.getNum());
                }else if(8 == item.getType()){//发行量
                    conversationalQuantityStatisticDo.setSendNum(item.getNum());
                }else if(9 == item.getType()){//上行量
                    conversationalQuantityStatisticDo.setUpsideNum(item.getNum());
                }
            }else{
                conversationalQuantityStatisticDo = new ConversationalQuantityStatisticDo();
                conversationalQuantityStatisticDo.setOperatorType(item.getOperatorType());
                conversationalQuantityStatisticDo.setChatbotId(item.getChatbotAccountId());
                conversationalQuantityStatisticDo.setSessionNum(0L);
                conversationalQuantityStatisticDo.setEffectiveSessionNum(0L);
                conversationalQuantityStatisticDo.setNewUsersNum(0L);
                conversationalQuantityStatisticDo.setActiveUsersNum(0L);
                conversationalQuantityStatisticDo.setSendNum(0L);
                conversationalQuantityStatisticDo.setUpsideNum(0L);
                if(4 == item.getType()){//会话量
                    conversationalQuantityStatisticDo.setSessionNum(item.getNum());
                }else if(5 == item.getType()){//有效会话
                    conversationalQuantityStatisticDo.setEffectiveSessionNum(item.getNum());
                    conversationalQuantityStatisticDo.setSessionNum(item.getNum());
                }else if(6 == item.getType()){//
                    conversationalQuantityStatisticDo.setNewUsersNum(item.getNum());
                }else if(7 == item.getType()){//
                    conversationalQuantityStatisticDo.setActiveUsersNum(item.getNum());
                }else if(8 == item.getType()){//发行量
                    conversationalQuantityStatisticDo.setSendNum(item.getNum());
                }else if(9 == item.getType()){//上行量
                    conversationalQuantityStatisticDo.setUpsideNum(item.getNum());
                }
                conversationalQuantityStatisticDo.setHours(DateUtils.obtainDate(hours,"yyyy-MM-dd HH"));
                conversationalQuantityStatisticDo.setCreator(item.getCustomerId());
                saveMap.put(key,conversationalQuantityStatisticDo);
            }
        }
        Map<String,ConversationalQuantityStatisticDo> updateMap = new HashMap<>();
        for(ConversationalQuantityStatisticDo item : conversationalQuantityStatisticDos){
            String hours = DateUtils.obtainDateStr(item.getHours(),"yyyy-MM-dd HH");
            Integer operatorType = item.getOperatorType();
            String chatbotId = item.getChatbotId();
            String customerIdO = item.getCreator();
            String key = ""+customerIdO+hours+operatorType+chatbotId;
            updateMap.put(key,item);
        }

        List<ConversationalQuantityStatisticDo> saveList = new ArrayList<>();
        List<ConversationalQuantityStatisticDo> updateList = new ArrayList<>();
        for(Map.Entry<String,ConversationalQuantityStatisticDo> entry : saveMap.entrySet()){
            String key = entry.getKey();
            ConversationalQuantityStatisticDo value = entry.getValue();
            if(updateMap.containsKey(key)){
                ConversationalQuantityStatisticDo oldValue = updateMap.get(key);
                if(!value.getSessionNum().equals(oldValue.getSessionNum())
                        || !value.getEffectiveSessionNum().equals(oldValue.getEffectiveSessionNum())
                        || !value.getActiveUsersNum().equals(oldValue.getActiveUsersNum())
                        || !value.getSendNum().equals(oldValue.getSendNum())
                        || !value.getUpsideNum().equals(oldValue.getUpsideNum())
                        || !value.getNewUsersNum().equals(oldValue.getNewUsersNum())){
                    oldValue.setSessionNum(value.getSessionNum());
                    oldValue.setEffectiveSessionNum(value.getEffectiveSessionNum());
                    oldValue.setNewUsersNum(value.getNewUsersNum());
                    oldValue.setActiveUsersNum(value.getActiveUsersNum());
                    oldValue.setSendNum(value.getSendNum());
                    oldValue.setUpsideNum(value.getUpsideNum());
                    updateList.add(oldValue);
                }
                System.out.println(key+"========="+
                        value.getSessionNum()+"、"+value.getEffectiveSessionNum()+"、"+value.getNewUsersNum()+"、"+value.getActiveUsersNum()+"、"+value.getSendNum()+"、"+value.getUpsideNum()+"|||||"+
                        oldValue.getSessionNum()+"、"+oldValue.getEffectiveSessionNum()+"、"+oldValue.getNewUsersNum()+"、"+oldValue.getActiveUsersNum()+"、"+oldValue.getSendNum()+"、"+oldValue.getUpsideNum());
            }else{
                saveList.add(value);
            }

        }
        if(!saveList.isEmpty()){
            conversationalQuantityStatisticDao.insertBatch(saveList);
        }
        if(!updateList.isEmpty()) {
            for (ConversationalQuantityStatisticDo item : updateList) {
                String creator = item.getCreator();
                item.setCreator(null);
                conversationalQuantityStatisticDao.update(
                        item,
                        Wrappers.<ConversationalQuantityStatisticDo>lambdaUpdate()
                                .eq(ConversationalQuantityStatisticDo::getCreator, creator)
                                .eq(ConversationalQuantityStatisticDo::getId, item.getId())
                );
            }
        }
    }

    private void updateProcessData(List<ProcessTemporaryStatisticDto> processTemporaryStatisticDtos, List<ProcessQuantityStatisticDo> processQuantityStatisticDos) {
        Map<String,ProcessQuantityStatisticDo> saveMap = new HashMap<>();
        ProcessQuantityStatisticDo processQuantityStatisticDo;

        for(ProcessTemporaryStatisticDto item : processTemporaryStatisticDtos){
            String hours = item.getHours();
            Integer operatorType = item.getOperatorType();
            String chatbotId = item.getChatbotAccountId();
            Long robotSceneNodeId = item.getRobotSceneNodeId();
            Long robotProcessSettingNodeId = item.getRobotProcessSettingNodeId();
            String customerIdN = item.getCustomerId();
            String key = ""+customerIdN+hours+operatorType+chatbotId+robotSceneNodeId+robotProcessSettingNodeId;
            if(saveMap.containsKey(key)){
                processQuantityStatisticDo = saveMap.get(key);
                if(1 == item.getType()){//流程触发
                    processQuantityStatisticDo.setProcessTriggersNum(item.getNum());
                }else if(2 == item.getType()){//流程完成
                    processQuantityStatisticDo.setProcessCompletedNum(item.getNum());
                }else if(3 == item.getType()){//兜底回复
                    processQuantityStatisticDo.setBottomReturnNum(item.getNum());
                }
            }else{
                processQuantityStatisticDo = new ProcessQuantityStatisticDo();
                processQuantityStatisticDo.setOperatorType(item.getOperatorType());
                processQuantityStatisticDo.setChatbotAccountId(item.getChatbotAccountId() == null ? "" : item.getChatbotAccountId());
                processQuantityStatisticDo.setRobotSceneNodeId(item.getRobotSceneNodeId());
                processQuantityStatisticDo.setRobotProcessSettingNodeId(item.getRobotProcessSettingNodeId());
                processQuantityStatisticDo.setProcessTriggersNum(0L);
                processQuantityStatisticDo.setProcessCompletedNum(0L);
                processQuantityStatisticDo.setBottomReturnNum(0L);
                if(1 == item.getType()){//流程触发
                    processQuantityStatisticDo.setProcessTriggersNum(item.getNum());
                }else if(2 == item.getType()){//流程完成
                    processQuantityStatisticDo.setProcessCompletedNum(item.getNum());
                }else if(3 == item.getType()){//兜底回复
                    processQuantityStatisticDo.setBottomReturnNum(item.getNum());
                }
                processQuantityStatisticDo.setProcessCompletionRate(BigDecimal.ZERO);
                processQuantityStatisticDo.setHours(DateUtils.obtainDate(hours,"yyyy-MM-dd HH"));
                processQuantityStatisticDo.setCreator(item.getCustomerId());
                saveMap.put(key,processQuantityStatisticDo);
            }
        }
        Map<String,ProcessQuantityStatisticDo> updateMap = new HashMap<>();
        for(ProcessQuantityStatisticDo item : processQuantityStatisticDos){
            String hours = DateUtils.obtainDateStr(item.getHours(),"yyyy-MM-dd HH");
            Integer operatorType = item.getOperatorType();
            String chatbotId = item.getChatbotAccountId();
            Long robotSceneNodeId = item.getRobotSceneNodeId();
            Long robotProcessSettingNodeId = item.getRobotProcessSettingNodeId();
            String customerIdO = item.getCreator();
            String key = ""+customerIdO+hours+operatorType+chatbotId+robotSceneNodeId+robotProcessSettingNodeId;
            updateMap.put(key,item);
        }

        List<ProcessQuantityStatisticDo> saveList = new ArrayList<>();
        List<ProcessQuantityStatisticDo> updateList = new ArrayList<>();
        for(Map.Entry<String,ProcessQuantityStatisticDo> entry : saveMap.entrySet()){
            String key = entry.getKey();
            ProcessQuantityStatisticDo value = entry.getValue();
            if(updateMap.containsKey(key)){
                ProcessQuantityStatisticDo oldValue = updateMap.get(key);
                if(!value.getProcessTriggersNum().equals(oldValue.getProcessTriggersNum()) || !value.getProcessCompletedNum().equals(oldValue.getProcessCompletedNum()) || !value.getBottomReturnNum().equals(oldValue.getBottomReturnNum())){
                    oldValue.setProcessTriggersNum(value.getProcessTriggersNum());
                    oldValue.setProcessCompletedNum(value.getProcessCompletedNum());
                    oldValue.setBottomReturnNum(value.getBottomReturnNum());
                    updateList.add(oldValue);
                }
                System.out.println(key+"========="+value.getProcessTriggersNum()+"、"+value.getProcessCompletedNum()+"、"+value.getBottomReturnNum()+"|||||"+oldValue.getProcessTriggersNum()+"、"+oldValue.getProcessCompletedNum()+"、"+oldValue.getBottomReturnNum());
            }else{
                saveList.add(value);
            }

        }
        if(!saveList.isEmpty()){
            processQuantityStatisticDao.insertBatch(saveList);
        }
        if(!updateList.isEmpty()){
            for (ProcessQuantityStatisticDo item : updateList) {
                String creator = item.getCreator();
                item.setCreator(null);
                processQuantityStatisticDao.update(
                        item,
                        Wrappers.<ProcessQuantityStatisticDo>lambdaUpdate()
                                .eq(ProcessQuantityStatisticDo::getCreator, creator)
                                .eq(ProcessQuantityStatisticDo::getId, item.getId())
                );
            }
        }

    }

    private List<ConversationalQuantityStatisticDo> obtainOldConversationalQuantityStatistic(String customerId, Date startDate, Date endDate) {
        LambdaQueryWrapperX<ConversationalQuantityStatisticDo> conversationalQuantityStatisticQueryWrapperX = new LambdaQueryWrapperX<>();
        conversationalQuantityStatisticQueryWrapperX.ge(ConversationalQuantityStatisticDo::getHours,startDate);
        if(endDate != null){//结束时间
            conversationalQuantityStatisticQueryWrapperX.le(ConversationalQuantityStatisticDo::getHours,endDate);
        }
        if(customerId != null){//结束时间
            conversationalQuantityStatisticQueryWrapperX.le(ConversationalQuantityStatisticDo::getCreator,customerId);
        }
        return conversationalQuantityStatisticDao.selectList(conversationalQuantityStatisticQueryWrapperX);
    }

    private List<ProcessQuantityStatisticDo> obtainOldProcessQuantityStatistic(String customerId, Date startDate, Date endDate) {
        LambdaQueryWrapperX<ProcessQuantityStatisticDo> processQuantityStatisticQueryWrapperX = new LambdaQueryWrapperX<>();
        processQuantityStatisticQueryWrapperX.ge(ProcessQuantityStatisticDo::getHours,startDate);
        if(endDate != null){//结束时间
            processQuantityStatisticQueryWrapperX.le(ProcessQuantityStatisticDo::getHours,endDate);
        }
        if(customerId != null){//结束时间
            processQuantityStatisticQueryWrapperX.le(ProcessQuantityStatisticDo::getCreator,customerId);
        }
        return processQuantityStatisticDao.selectList(processQuantityStatisticQueryWrapperX);
    }

}
