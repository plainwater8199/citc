package com.citc.nce.tenant.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.citc.nce.common.thread.ThreadTaskUtils;
import com.citc.nce.dataStatistics.service.DataStatisticsScheduleService;
import com.citc.nce.tenant.robot.dao.*;
import com.citc.nce.tenant.robot.entity.*;
import com.citc.nce.tenant.service.CspTableManageService;
import com.citc.nce.tenant.service.StatisticSyncService;
import com.citc.nce.tenant.vo.req.MsgRecordDataSynReq;
import com.citc.nce.tenant.vo.req.StatisticSyncReq;
import com.citc.nce.utils.DateUtils;
import com.google.common.base.Strings;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.*;

@Service
@Slf4j
public class StatisticSyncServiceImpl implements StatisticSyncService {

    private static final Integer LIMIT = 10000;

    @Resource
    private RobotRecordOldDao robotRecordOldDao;
    @Resource
    private RobotRecord1Dao robotRecord1Dao;
    @Resource
    private TemporaryStatistics1Dao temporaryStatistics1Dao;

    @Resource
    private RobotMediaPhoneResult1Dao robotMediaPhoneResult1Dao;
    @Resource
    private ShortMsgPhoneResult1Dao shortMsgPhoneResult1Dao;
    @Resource
    private RobotPhoneResult1Dao robotPhoneResult1Dao;
    @Resource
    private MsgRecordDao msgRecordDao;


    @Resource
    private CspTableManageService cspTableManageService;

    @Resource
    private DataStatisticsScheduleService dataStatisticsScheduleService;
    @Override
    public void statisticSync(StatisticSyncReq req) {

        Set<String> cspIdSet = req.getCspIdSet();
        Map<Integer,Map<Long,String>> templateSignMap = req.getTemplateSignMap();
        Map<Integer,Map<String,Long>> templateMap = req.getTemplateMap();
        Map<Integer,Map<Long,String>> templateByIdMap = req.getTemplateByIdMap();
        Map<String, Map<Integer, String>> chatbotMap = req.getChatbotMap();
        Map<String,Integer> operatorCode = new HashMap<>();//0：缺省(硬核桃)，1：联通，2：移动，3：电信
        operatorCode.put("联通",1);
        operatorCode.put("移动",2);
        operatorCode.put("电信",3);
        operatorCode.put("硬核桃",0);

        //Map<userId,customerId>
        Map<String, String> idMap = req.getIdMap();

        for(String cspId : cspIdSet){
            cspTableManageService.createTableByCspId(cspId);
            //删除aim_sent_data表
//            cspTableManageService.dropTableByCspIdAndTableName(cspId,"aim_sent_data");
        }


        List<MsgRecordDo> msgRecordDos = new ArrayList<>();
        Map<Long, String> signMap = templateSignMap.get(2);
        Map<String, Long> templateTypeMap = templateMap.get(2);
        Long templateId;
        LambdaQueryWrapper<RobotMediaPhoneResultDo> robotMediaPhoneResultDoLambdaQueryWrapper = new LambdaQueryWrapper<>();
        robotMediaPhoneResultDoLambdaQueryWrapper.isNotNull(RobotMediaPhoneResultDo::getMessageId);
        List<RobotMediaPhoneResultDo> robotMediaPhoneResultDos = robotMediaPhoneResult1Dao.selectList(robotMediaPhoneResultDoLambdaQueryWrapper);
        MsgRecordDo msgRecordDo;


        for(RobotMediaPhoneResultDo robotMediaPhoneResultDo : robotMediaPhoneResultDos ){
            if(idMap.containsKey(robotMediaPhoneResultDo.getCreator())){
                templateId = templateTypeMap.get(robotMediaPhoneResultDo.getTemplateName());
                msgRecordDo = new MsgRecordDo();
                msgRecordDo.setPhoneNum(robotMediaPhoneResultDo.getPhoneNum());
                msgRecordDo.setSendResult(robotMediaPhoneResultDo.getSendResult());
                msgRecordDo.setMessageId(robotMediaPhoneResultDo.getMessageId());
                msgRecordDo.setCallerAccount(robotMediaPhoneResultDo.getCallerAccount());
                msgRecordDo.setMessageResource(robotMediaPhoneResultDo.getMessageResource());
                msgRecordDo.setOperatorCode(0);
                msgRecordDo.setMessageType(robotMediaPhoneResultDo.getMessageType());
                msgRecordDo.setMessageContent(robotMediaPhoneResultDo.getMessageContent());
                msgRecordDo.setButtonContent(null);
                msgRecordDo.setTemplateId(templateId);
                msgRecordDo.setTemplateName(robotMediaPhoneResultDo.getTemplateName());
                msgRecordDo.setPlanDetailId(robotMediaPhoneResultDo.getPlanDetailId());
                msgRecordDo.setPlanId(robotMediaPhoneResultDo.getPlanId());
                msgRecordDo.setReceiptTime(robotMediaPhoneResultDo.getReceiptTime());
                msgRecordDo.setSendTime(robotMediaPhoneResultDo.getSendTime());
                msgRecordDo.setFinalResult(robotMediaPhoneResultDo.getFinalResult());
                msgRecordDo.setReadTime(null);
                msgRecordDo.setSign(signMap.get(templateId));
                msgRecordDo.setAccountType(2);
                msgRecordDo.setAccountId(robotMediaPhoneResultDo.getMediaAccountId());
                msgRecordDo.setAccountName(null);
                msgRecordDo.setAccountDictCode(robotMediaPhoneResultDo.getMediaOperatorCode());
                msgRecordDo.setId(null);
                msgRecordDo.setCreateTime(robotMediaPhoneResultDo.getCreateTime());
                msgRecordDo.setUpdateTime(robotMediaPhoneResultDo.getUpdateTime());
                msgRecordDo.setCreator(idMap.get(robotMediaPhoneResultDo.getCreator()));
                msgRecordDo.setUpdater("water");
                msgRecordDos.add(msgRecordDo);
            }

        }
        if (!CollectionUtils.isEmpty(msgRecordDos)) {
            msgRecordDao.insertBatch(msgRecordDos);
        }


        List<MsgRecordDo>  msgRecordDos1 = new ArrayList<>();
        MsgRecordDo msgRecordDo1;
        Map<Long, String> signMap1 = templateSignMap.get(3);
        Map<Long, String> smsTemplateByIdMap = templateByIdMap.get(3);
        LambdaQueryWrapper<ShortMsgPhoneResultDo> shortMsgPhoneResultDoLambdaQueryWrapper = new LambdaQueryWrapper<>();
        shortMsgPhoneResultDoLambdaQueryWrapper.isNotNull(ShortMsgPhoneResultDo::getMessageId);
        List<ShortMsgPhoneResultDo> shortMsgPhoneResultDos = shortMsgPhoneResult1Dao.selectList(shortMsgPhoneResultDoLambdaQueryWrapper);
        for(ShortMsgPhoneResultDo item : shortMsgPhoneResultDos ){
            if(idMap.containsKey(item.getCreator())){
                Long smsTemplateId = Long.valueOf(item.getTemplateName());
                msgRecordDo1 = new MsgRecordDo();
                msgRecordDo1.setPhoneNum(item.getPhoneNum());
                msgRecordDo1.setSendResult(item.getSendResult());
                msgRecordDo1.setMessageId(item.getMessageId());
                msgRecordDo1.setCallerAccount(item.getCallerAccount());
                msgRecordDo1.setMessageResource(item.getMessageResource());
                msgRecordDo1.setMessageType(item.getMessageType());
                msgRecordDo1.setOperatorCode(0);
                msgRecordDo1.setMessageContent(item.getMessageContent());
                msgRecordDo1.setButtonContent(null);
                msgRecordDo1.setTemplateId(smsTemplateId);
                msgRecordDo1.setTemplateName(smsTemplateByIdMap.getOrDefault(smsTemplateId,""));
                msgRecordDo1.setPlanDetailId(item.getPlanDetailId());
                msgRecordDo1.setPlanId(item.getPlanId());
                msgRecordDo1.setReceiptTime(item.getReceiptTime());
                msgRecordDo1.setSendTime(item.getSendTime());
                msgRecordDo1.setFinalResult(item.getFinalResult());
                msgRecordDo1.setReadTime(null);
                msgRecordDo1.setAccountType(3);
                msgRecordDo1.setSign(signMap1.get(Long.valueOf(item.getTemplateName())));
                msgRecordDo1.setAccountId(item.getShortMsgAccountId());
                msgRecordDo1.setAccountName(null);
                msgRecordDo1.setAccountDictCode(item.getShortMsgOperatorCode());
                msgRecordDo1.setId(null);
                msgRecordDo1.setCreateTime(item.getCreateTime());
                msgRecordDo1.setUpdateTime(item.getUpdateTime());
                msgRecordDo1.setCreator(idMap.get(item.getCreator()));
                msgRecordDo1.setUpdater("water");
                msgRecordDos1.add(msgRecordDo1);
            }

        }
        if (!CollectionUtils.isEmpty(msgRecordDos1)) {
            msgRecordDao.insertBatch(msgRecordDos1);
        }


        List<MsgRecordDo> msgRecordDos2 = new ArrayList<>();
        MsgRecordDo msgRecordDo2;
        LambdaQueryWrapper<RobotPhoneResult> robotPhoneResultLambdaQueryWrapper = new LambdaQueryWrapper<>();
        robotPhoneResultLambdaQueryWrapper.isNotNull(RobotPhoneResult::getMessageId);
        List<RobotPhoneResult>  robotPhoneResults = robotPhoneResult1Dao.selectList(robotPhoneResultLambdaQueryWrapper);
        for(RobotPhoneResult item : robotPhoneResults ){
            if(idMap.containsKey(item.getCreator())){
                msgRecordDo2 = new MsgRecordDo();
                msgRecordDo2.setPhoneNum(item.getPhoneNum());
                msgRecordDo2.setSendResult(item.getSendResult());
                msgRecordDo2.setMessageId(item.getMessageId());
                msgRecordDo2.setCallerAccount(item.getCallerAccount());
                msgRecordDo2.setOperatorCode(operatorCode.getOrDefault(item.getCallerAccount(),0));
                msgRecordDo2.setMessageResource(item.getMessageResource());
                msgRecordDo2.setMessageType(item.getMessageType());
                msgRecordDo2.setMessageContent(item.getMessageContent());
                msgRecordDo2.setButtonContent(item.getButtonContent());
                msgRecordDo2.setTemplateId(item.getTemplateId());
                msgRecordDo2.setTemplateName(item.getTemplateName());
                msgRecordDo2.setPlanDetailId(item.getPlanDetailId());
                msgRecordDo2.setPlanId(item.getPlanId());
                msgRecordDo2.setReceiptTime(item.getReceiptTime());
                msgRecordDo2.setSendTime(item.getSendTime());
                msgRecordDo2.setFinalResult(item.getFinalResult());
                msgRecordDo2.setReadTime(item.getReadTime());
                msgRecordDo2.setAccountType(1);
                msgRecordDo2.setAccountId(chatbotMap.containsKey(idMap.get(item.getCreator())) ? chatbotMap.get(idMap.get(item.getCreator())).get(operatorCode.getOrDefault(item.getCallerAccount(),0)):null);
                msgRecordDo2.setAccountName(null);
                msgRecordDo2.setId(null);
                msgRecordDo2.setCreateTime(item.getCreateTime());
                msgRecordDo2.setUpdateTime(item.getUpdateTime());
                msgRecordDo2.setCreator(idMap.get(item.getCreator()));
                msgRecordDo2.setUpdater("water");
                msgRecordDos2.add(msgRecordDo2);
            }

        }
        if (!CollectionUtils.isEmpty(msgRecordDos2)) {
            msgRecordDao.insertBatch(msgRecordDos2);
        }
        /*
        List<MsgRecordDo> msgRecordDos2 ;
        LambdaQueryWrapper<RobotPhoneResult> robotPhoneResultLambdaQueryWrapper = new LambdaQueryWrapper<>();
        robotPhoneResultLambdaQueryWrapper.isNotNull(RobotPhoneResult::getMessageId);
        int count = robotPhoneResult1Dao.selectCount(robotPhoneResultLambdaQueryWrapper).intValue();
        int groupSize = 1000; // 定义每组的大小
        int totalGroups = count / groupSize + ((count % groupSize == 0) ? 0 : 1); // 计算需要分多少组
        for (int i = 1; i < totalGroups+1 ; i++) {
            msgRecordDos2 = new ArrayList<>();
            MsgRecordDo msgRecordDo2;
            if (i * groupSize >= count) {
                break; // 如果已经超过了列表长度则结束循环
            }
            Page<RobotPhoneResult> page = new Page<>(i, groupSize);
            IPage<RobotPhoneResult> userPage =  robotPhoneResult1Dao.selectPage(page, robotPhoneResultLambdaQueryWrapper);
            List<RobotPhoneResult> robotPhoneResults = userPage.getRecords();
            for(RobotPhoneResult item : robotPhoneResults ){
                if(idMap.containsKey(item.getCreator())){
                    msgRecordDo2 = new MsgRecordDo();
                    msgRecordDo2.setPhoneNum(item.getPhoneNum());
                    msgRecordDo2.setSendResult(item.getSendResult());
                    msgRecordDo2.setMessageId(item.getMessageId());
                    msgRecordDo2.setCallerAccount(item.getCallerAccount());
                    msgRecordDo2.setOperatorCode(operatorCode.getOrDefault(item.getCallerAccount(),0));
                    msgRecordDo2.setMessageResource(item.getMessageResource());
                    msgRecordDo2.setMessageType(item.getMessageType());
                    msgRecordDo2.setMessageContent(item.getMessageContent());
                    msgRecordDo2.setButtonContent(item.getButtonContent());
                    msgRecordDo2.setTemplateId(item.getTemplateId());
                    msgRecordDo2.setTemplateName(item.getTemplateName());
                    msgRecordDo2.setPlanDetailId(item.getPlanDetailId());
                    msgRecordDo2.setPlanId(item.getPlanId());
                    msgRecordDo2.setReceiptTime(item.getReceiptTime());
                    msgRecordDo2.setSendTime(item.getSendTime());
                    msgRecordDo2.setFinalResult(item.getFinalResult());
                    msgRecordDo2.setReadTime(item.getReadTime());
                    msgRecordDo2.setAccountType(1);
                    msgRecordDo2.setAccountId(chatbotMap.containsKey(idMap.get(item.getCreator())) ? chatbotMap.get(idMap.get(item.getCreator())).get(operatorCode.getOrDefault(item.getCallerAccount(),0)):null);
                    msgRecordDo2.setAccountName(null);
                    msgRecordDo2.setId(null);
                    msgRecordDo2.setCreateTime(item.getCreateTime());
                    msgRecordDo2.setUpdateTime(item.getUpdateTime());
                    msgRecordDo2.setCreator(idMap.get(item.getCreator()));
                    msgRecordDo2.setUpdater("water");
                    msgRecordDos2.add(msgRecordDo2);
                }

            }
            if (!CollectionUtils.isEmpty(msgRecordDos2)) {
                msgRecordDao.insertBatch(msgRecordDos2);
            }
            log.info("msg_record表，第"+i+"批次，开始位置为："+i * groupSize);
        }
*/

        int resultSum = LIMIT;
        long id = 0L;
        while (resultSum == LIMIT){
            List<RobotRecordDo> robotRecordDos = new ArrayList<>();
            log.info("-----RobotRecordDo------:"+id);
            LambdaQueryWrapper<RobotRecordOldDo> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.gt(RobotRecordOldDo::getId,id);
            queryWrapper.last("LIMIT "+LIMIT);
            List<RobotRecordOldDo> oldData = robotRecordOldDao.selectList(queryWrapper);
            resultSum = oldData.size();
            for(RobotRecordOldDo item : oldData){
                if(idMap.containsKey(item.getCreator())){
                    RobotRecordDo robotRecordDo = new RobotRecordDo();
                    BeanUtils.copyProperties(item,robotRecordDo);
                    robotRecordDo.setCreator(idMap.get(item.getCreator()));
                    robotRecordDo.setId(null);
                    robotRecordDos.add(robotRecordDo);
                }
                id = item.getId();
            }
            if(!robotRecordDos.isEmpty()){
                robotRecord1Dao.insertBatch(robotRecordDos);
                log.info("-----RobotRecordDo------update----:"+robotRecordDos.size());
            }
        }
        log.info("-------------------------RobotRecordDo----------执行完成---------------");



        int resultSum1 = LIMIT;
        long id1 = 0L;
        while (resultSum1 == LIMIT){
            List<TemporaryStatisticsDo> temporaryStatisticsDos = new ArrayList<>();
            log.info("-----TemporaryStatisticsDo------:"+id1);
            LambdaQueryWrapper<TemporaryStatisticsDo> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.gt(TemporaryStatisticsDo::getId,id1);
            queryWrapper.last("LIMIT "+LIMIT);
            List<TemporaryStatisticsDo> oldData = temporaryStatistics1Dao.selectList(queryWrapper);
            resultSum1 = oldData.size();
            for(TemporaryStatisticsDo item : oldData){
                if(idMap.containsKey(item.getCreator())){
                    TemporaryStatisticsDo temporaryStatisticsDo = new TemporaryStatisticsDo();
                    BeanUtils.copyProperties(item,temporaryStatisticsDo);
                    temporaryStatisticsDo.setCreator(idMap.get(item.getCreator()));
                    temporaryStatisticsDos.add(temporaryStatisticsDo);
                }
                id1 = item.getId();
            }
            if(!temporaryStatisticsDos.isEmpty()){
                temporaryStatistics1Dao.updateBatch(temporaryStatisticsDos);
                log.info("-----TemporaryStatisticsDo------update----:"+temporaryStatisticsDos.size());
            }
        }
        log.info("-------------------------TemporaryStatisticsDo----------执行完成---------------");


        log.info("---------------------------------------------------------------基础数据同步完成---------------------------------------------------------");

    }

    private void insertMsgRecord(List<MsgRecordDo> msgRecordDos) {
        List<MsgRecordDo> updateList;
        int groupSize = 1000; // 定义每组的大小
        int totalGroups = msgRecordDos.size() / groupSize + ((msgRecordDos.size() % groupSize == 0) ? 0 : 1); // 计算需要分多少组
        for (int i = 0; i < totalGroups; i++) {
            if (i * groupSize >= msgRecordDos.size()) {
                break; // 如果已经超过了列表长度则结束循环
            }
            updateList = msgRecordDos.subList(i * groupSize,Math.min(msgRecordDos.size(),(i+1)*groupSize));
            msgRecordDao.insertBatch(updateList);
            log.info("msg_record表，第"+i+"批次，开始位置为："+i * groupSize);
        }

    }




    private static final String AIM_SENT_DATA = "aim_sent_data";
    private static final String CONVERSATIONAL_QUANTITY_STATISTIC = "conversational_quantity_statistic";
    private static final String PROCESS_QUANTITY_STATISTIC = "process_quantity_statistic";
    private static final String ROBOT_RECORD = "robot_record";
    private static final String MSG_RECORD = "msg_record";
    private static final String MSG_QUANTITY_STATISTICS = "msg_quantity_statistics";

    /**
     * 统计数据更新
     * @param cspIdSet csp列表
     */
    @Override
    public void updateStatistic(Set<String> cspIdSet) {
        //1、将统计表都清空
//        for(String cspId : cspIdSet){
//            cspTableManageService.cleanTableByCspIdAndTableName(cspId,MSG_QUANTITY_STATISTICS);
//            cspTableManageService.cleanTableByCspIdAndTableName(cspId,CONVERSATIONAL_QUANTITY_STATISTIC);
//            cspTableManageService.cleanTableByCspIdAndTableName(cspId,PROCESS_QUANTITY_STATISTIC);
//        }
//        log.info("----------------------------------------表清空完毕----------------------------------------");
//        dataStatisticsScheduleService.statisticPerHour("",DateUtils.obtainDate("2022-10-17 00:00:00"),new Date());


        Calendar startTime = Calendar.getInstance();
        startTime.set(Calendar.YEAR, 2023);
        startTime.set(Calendar.MONTH, Calendar.FEBRUARY);
        startTime.set(Calendar.DAY_OF_MONTH,1);

        Calendar endTime = Calendar.getInstance();
        endTime.setTime(new Date());
        while (startTime.before(endTime)) {
//            Date start1Time = startTime.getTime();
//            System.out.println("开始："+DateUtils.obtainDateStr(DateUtils.obtainDayTime("start",start1Time)));
//            System.out.println("结束："+DateUtils.obtainDateStr(DateUtils.obtainDayTime("end",start1Time))+"\n");
//            dataStatisticsScheduleService.statisticPerHour("",DateUtils.obtainDayTime("start",start1Time),DateUtils.obtainDayTime("end",start1Time));
//            startTime.add(Calendar.DAY_OF_YEAR, 1);

            Date start1Time = startTime.getTime();
            // 计算本月最后一天的时间
            int lastDayOfMonth = startTime.getActualMaximum(Calendar.DAY_OF_MONTH);
            startTime.set(Calendar.DAY_OF_MONTH, lastDayOfMonth);
            startTime.add(Calendar.SECOND, 59); // 将时间调到上一秒钟，确保在同一天
            startTime.add(Calendar.MILLISECOND, 999);
            Date end1Time = startTime.getTime();


            System.out.println("开始："+DateUtils.obtainDateStr(DateUtils.obtainDayTime("start",start1Time)));
            System.out.println("结束："+DateUtils.obtainDateStr(DateUtils.obtainDayTime("end",end1Time))+"\n");
            dataStatisticsScheduleService.statisticPerHour(null,DateUtils.obtainDayTime("start",start1Time),DateUtils.obtainDayTime("end",end1Time));

            // 移动到下一个月的第一天
            startTime.add(Calendar.MONTH, 1);
            startTime.set(Calendar.DAY_OF_MONTH, 1);

        }
        log.info("---------------------------------------------------------------基础数据统计入表完成---------------------------------------------------------");

    }


//    @Override
//    public void msgRecordDataSyn(MsgRecordDataSynReq req) {
//        Set<String> cspIdSet = req.getCspIdSet();
//        for(String cspId : cspIdSet){
//            try {
//                cspTableManageService.addSign(cspId,MSG_RECORD);
//            }catch (Exception e){
//                log.info("执行异常");
//            }
//
//        }
//        Map<Integer,Map<Long,String>> templateSignMap = req.getTemplateSignMap();
//        //查询所有记录
//        LambdaQueryWrapper<MsgRecordDo> queryWrapper = new LambdaQueryWrapper<>();
//        queryWrapper.isNotNull(MsgRecordDo::getTemplateId);
//        queryWrapper.in(MsgRecordDo::getAccountType,Arrays.asList(2,3));
//        List<MsgRecordDo> msgRecordDos = msgRecordDao.selectList(queryWrapper);
//
//        Long templateId;
//        Integer accountType;
//        for(MsgRecordDo item : msgRecordDos){
//            templateId = item.getTemplateId();
//            accountType = item.getAccountType();
//            if(templateSignMap.containsKey(accountType)){
//                Map<Long, String> signMap = templateSignMap.get(accountType);
//                if(signMap.containsKey(templateId)){
//                    String sign = signMap.get(templateId);
//                    if(!Strings.isNullOrEmpty(sign) && !sign.equals(item.getSign())){
//                        item.setSign(sign);
//                        LambdaUpdateWrapper<MsgRecordDo> updateWrapper = new LambdaUpdateWrapper<>();
//                        updateWrapper.eq(MsgRecordDo::getId,item.getId());
//                        updateWrapper.eq(MsgRecordDo::getCreator,item.getCreator());
//                        msgRecordDao.update(item,updateWrapper);
//                    }
//                }
//            }
//        }
//    }
//

}
