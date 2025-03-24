package com.citc.nce.tenant.service.impl;

import com.citc.nce.common.core.exception.BizException;
import com.citc.nce.developer.dao.DeveloperSendMapper;
import com.citc.nce.developer.dao.DeveloperSendStatisticsMapper;
import com.citc.nce.seata.ShardingJdbcNodesAutoConfigurator;
import com.citc.nce.tenant.exception.TenantErrorCode;
import com.citc.nce.tenant.robot.dao.*;
import com.citc.nce.tenant.service.CspTableManageService;
import com.citc.nce.tenant.vo.req.CreateCspTableReq;
import com.citc.nce.tenant.vo.req.RefreshActualNodesReq;
import com.google.common.base.Strings;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
@Slf4j
public class CspTableManageServiceImpl implements CspTableManageService {

    private static final String DB_NAME = "robot.";
    private static final String AIM_SENT_DATA = "aim_sent_data_";
    private static final String CONVERSATIONAL_QUANTITY_STATISTIC = "conversational_quantity_statistic_";
    private static final String PROCESS_QUANTITY_STATISTIC = "process_quantity_statistic_";
    private static final String ROBOT_RECORD = "robot_record_";
    private static final String MSG_RECORD = "msg_record_";
    private static final String MSG_QUANTITY_STATISTICS = "msg_quantity_statistics_";
    private static final String DEVELOPER_SEND = "developer_send_";
    private static final String DEVELOPER_SEND_STATISTICS = "developer_send_statistics";


    @Resource
    private ShardingJdbcNodesAutoConfigurator shardingJdbcNodesAutoConfigurator;

    @Resource
    private ProcessQuantityStatistic1Dao processQuantityStatistic1Dao;
    @Resource
    private RobotRecord1Dao robotRecord1Dao;
    @Resource
    private ConversationalQuantityStatistic1Dao conversationalQuantityStatistic1Dao;
    @Resource
    private AimSentData1Dao aimSentData1Dao;
    @Resource
    private MsgRecordDao msgRecordDao;
    @Resource
    private MsgQuantityStatisticDao msgQuantityStatisticDao;
    @Resource
    private DeveloperSendMapper developerSendMapper;
    @Resource
    private DeveloperSendStatisticsMapper developerSendStatisticsMapper;

    @Override
    public String createTable(CreateCspTableReq req) {
        String cspId = req.getCspId();
        try {
            if(!Strings.isNullOrEmpty(cspId) && cspId.length() == 10 ){
                createTableByCspId(cspId);
            }else{
                throw new BizException(TenantErrorCode.CSP_ID_EXCEPTION);
            }
            return "success";
        } catch (Exception e) {
            dropTableByCspId(cspId);
            log.error("创建csp业务表失败:{}",e.getMessage());
            return "fail";
        }
    }

    @Override
    public void refreshActualNodes(RefreshActualNodesReq req) {
        Set<String> cspIdSet = req.getCspIdSet();
        List<String> msgRecordDataActualNodes = new ArrayList<>();
        List<String> msgQuantityStatisticsDataActualNodes = new ArrayList<>();
        List<String> conversationalQuantityStatisticActualNodes = new ArrayList<>();
        List<String> processQuantityStatisticActualNodes = new ArrayList<>();
        List<String> robotRecordActualNodes = new ArrayList<>();
        for(String cspId : cspIdSet){
            processQuantityStatisticActualNodes.add(DB_NAME+PROCESS_QUANTITY_STATISTIC+cspId);
            robotRecordActualNodes.add(DB_NAME+ROBOT_RECORD+cspId);
            conversationalQuantityStatisticActualNodes.add(DB_NAME+CONVERSATIONAL_QUANTITY_STATISTIC+cspId);
            msgRecordDataActualNodes.add(DB_NAME+MSG_RECORD+cspId);
            msgQuantityStatisticsDataActualNodes.add(DB_NAME+MSG_QUANTITY_STATISTICS+cspId);
        }
        shardingJdbcNodesAutoConfigurator.generateActualDataNodes("conversational_quantity_statistic",conversationalQuantityStatisticActualNodes);
        shardingJdbcNodesAutoConfigurator.generateActualDataNodes("process_quantity_statistic",processQuantityStatisticActualNodes);
        shardingJdbcNodesAutoConfigurator.generateActualDataNodes("robot_record",robotRecordActualNodes);
        shardingJdbcNodesAutoConfigurator.generateActualDataNodes("msg_record",msgRecordDataActualNodes);
        shardingJdbcNodesAutoConfigurator.generateActualDataNodes("msg_quantity_statistics",msgQuantityStatisticsDataActualNodes);
    }

    @Override
    public void dropTableByCspId(String cspId) {
        processQuantityStatistic1Dao.dropTable(PROCESS_QUANTITY_STATISTIC+cspId);
        robotRecord1Dao.dropTable(ROBOT_RECORD+cspId);
        conversationalQuantityStatistic1Dao.dropTable(CONVERSATIONAL_QUANTITY_STATISTIC+cspId);
        msgRecordDao.dropTable(MSG_RECORD+cspId);
        msgQuantityStatisticDao.dropTable(MSG_QUANTITY_STATISTICS+cspId);
        developerSendMapper.dropTable(DEVELOPER_SEND+cspId);
        developerSendStatisticsMapper.dropTable(DEVELOPER_SEND_STATISTICS+cspId);
    }

    @Override
    public void createTableByCspId(String cspId) {
        //如果存在就不删除
        msgRecordDao.dropTable(MSG_RECORD+cspId);
        msgRecordDao.createTable(MSG_RECORD+cspId);

        robotRecord1Dao.dropTable(ROBOT_RECORD+cspId);
        robotRecord1Dao.createTable(ROBOT_RECORD+cspId);

        //删除历史表
        processQuantityStatistic1Dao.createTable(PROCESS_QUANTITY_STATISTIC+cspId);//删除历史表
        conversationalQuantityStatistic1Dao.createTable(CONVERSATIONAL_QUANTITY_STATISTIC+cspId);//删除历史表
        msgQuantityStatisticDao.createTable(MSG_QUANTITY_STATISTICS+cspId);//删除历史表

    }

    @Override
    public void cleanTableByCspId(String cspId) {
        processQuantityStatistic1Dao.clearTable(PROCESS_QUANTITY_STATISTIC+cspId);
        robotRecord1Dao.clearTable(ROBOT_RECORD+cspId);
        conversationalQuantityStatistic1Dao.clearTable(CONVERSATIONAL_QUANTITY_STATISTIC+cspId);
        msgRecordDao.clearTable(MSG_RECORD+cspId);
        msgQuantityStatisticDao.clearTable(MSG_QUANTITY_STATISTICS+cspId);
    }

    @Override
    public void cleanTableByCspIdAndTableName(String cspId, String tableName) {
        if(MSG_QUANTITY_STATISTICS.startsWith(tableName)){
            msgQuantityStatisticDao.clearTable(MSG_QUANTITY_STATISTICS+cspId);
        }else if(PROCESS_QUANTITY_STATISTIC.startsWith(tableName)){
            processQuantityStatistic1Dao.clearTable(PROCESS_QUANTITY_STATISTIC+cspId);
        }else if(CONVERSATIONAL_QUANTITY_STATISTIC.startsWith(tableName)){
            conversationalQuantityStatistic1Dao.clearTable(CONVERSATIONAL_QUANTITY_STATISTIC+cspId);
        }else if(MSG_RECORD.startsWith(tableName)){
            msgRecordDao.clearTable(MSG_RECORD+cspId);
        }else if(ROBOT_RECORD.startsWith(tableName)){
            robotRecord1Dao.clearTable(ROBOT_RECORD+cspId);
        }

    }

    @Override
    public void addSign(String cspId, String tableName) {
        if(MSG_RECORD.startsWith(tableName)) {
            msgRecordDao.addSign(MSG_RECORD + cspId);
        }
    }

    @Override
    public void dropTableByCspIdAndTableName(String cspId, String tableName) {
        if(MSG_QUANTITY_STATISTICS.startsWith(tableName)){
            msgQuantityStatisticDao.dropTable(MSG_QUANTITY_STATISTICS+cspId);
        }else if(PROCESS_QUANTITY_STATISTIC.startsWith(tableName)){
            processQuantityStatistic1Dao.dropTable(PROCESS_QUANTITY_STATISTIC+cspId);
        }else if(CONVERSATIONAL_QUANTITY_STATISTIC.startsWith(tableName)){
            conversationalQuantityStatistic1Dao.dropTable(CONVERSATIONAL_QUANTITY_STATISTIC+cspId);
        }else if(MSG_RECORD.startsWith(tableName)){
            msgRecordDao.dropTable(MSG_RECORD+cspId);
        }else if(ROBOT_RECORD.startsWith(tableName)){
            robotRecord1Dao.dropTable(ROBOT_RECORD+cspId);
        }else if(AIM_SENT_DATA.startsWith(tableName)){
            aimSentData1Dao.dropTable(AIM_SENT_DATA+cspId);
        }
    }

    @Override
    public void updateAccountId(String cspId, String tableName) {
        if(MSG_QUANTITY_STATISTICS.startsWith(tableName)){
            msgQuantityStatisticDao.updateAccountId(MSG_QUANTITY_STATISTICS+cspId);
        }else if(MSG_RECORD.startsWith(tableName)){
            msgRecordDao.updateAccountId(MSG_RECORD+cspId);
        }
    }

    @Override
    public void addMsgRecordFailedReason(String cspId) {
        try {
            msgRecordDao.addMsgRecordFailedReason("msg_record_"+cspId);
        } catch (Exception e) {
            log.error("添加[失败原因]字段失败:{}", e.getMessage());
        }
    }
}
