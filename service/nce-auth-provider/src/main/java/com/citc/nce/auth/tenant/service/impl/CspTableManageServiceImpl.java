package com.citc.nce.auth.tenant.service.impl;

import com.citc.nce.auth.csp.recharge.dao.ChargeConsumeRecordDao;
import com.citc.nce.auth.tenant.req.AuthCreateCspTableReq;
import com.citc.nce.auth.tenant.req.AuthRefreshActualNodesReq;
import com.citc.nce.auth.tenant.service.CspTableManageService;
import com.citc.nce.common.core.exception.BizException;
import com.citc.nce.seata.ShardingJdbcNodesAutoConfigurator;
import com.citc.nce.tenant.exception.TenantErrorCode;
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

    private static final String DB_NAME = "user.";
    private static final String Charge_Consume_Record = "charge_consume_record_";
    private static final String Charge_Consume_Record_tableName = "charge_consume_record";
    @Resource
    ChargeConsumeRecordDao chargeConsumeRecordDao;
    @Resource
    private ShardingJdbcNodesAutoConfigurator shardingJdbcNodesAutoConfigurator;


    @Override
    public String createTable(AuthCreateCspTableReq req) {
        String cspId = req.getCspId();
        try {
            if (!Strings.isNullOrEmpty(cspId) && cspId.length() == 10) {
                createTableByCspId(cspId);
            } else {
                throw new BizException(TenantErrorCode.CSP_ID_EXCEPTION);
            }
            return "success";
        } catch (Exception e) {
            dropTableByCspId(cspId);
            log.error("创建csp业务表失败:{}", e.getMessage());
            return "fail";
        }
    }

    @Override
    public void refreshActualNodes(AuthRefreshActualNodesReq req) {
        Set<String> cspIdSet = req.getCspIdSet();
        List<String> chargeConsumeRecordActualNodes = new ArrayList<>();
        for (String cspId : cspIdSet) {
            chargeConsumeRecordActualNodes.add(DB_NAME + Charge_Consume_Record + cspId);
        }
        shardingJdbcNodesAutoConfigurator.generateActualDataNodes(Charge_Consume_Record_tableName, chargeConsumeRecordActualNodes);
    }

    @Override
    public void dropTableByCspId(String cspId) {
        chargeConsumeRecordDao.dropTable(Charge_Consume_Record + cspId);
    }

    @Override
    public void createTableByCspId(String cspId) {
        //如果存在就不删除
        chargeConsumeRecordDao.createTableIfNotExist(Charge_Consume_Record + cspId);
    }

    @Override
    public void cleanTableByCspId(String cspId) {
        chargeConsumeRecordDao.clearTable(Charge_Consume_Record + cspId);
    }

    @Override
    public void dropTableByCspIdAndTableName(String cspId, String tableName) {
        if (Charge_Consume_Record.startsWith(tableName)) {
            chargeConsumeRecordDao.dropTable(Charge_Consume_Record + cspId);
        }
    }
}
