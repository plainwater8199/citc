package com.citc.nce.tenant.service;

import com.citc.nce.tenant.vo.req.CreateCspTableReq;
import com.citc.nce.tenant.vo.req.RefreshActualNodesReq;

public interface CspTableManageService {

    String createTable(CreateCspTableReq req);

    void refreshActualNodes(RefreshActualNodesReq req);

    void dropTableByCspId(String cspId);

    void createTableByCspId(String cspId);

    void cleanTableByCspId(String cspId);

    void cleanTableByCspIdAndTableName(String cspId, String tableName);

    void addSign(String cspId, String msgRecord);

    void dropTableByCspIdAndTableName(String cspId, String msg_record);

    void updateAccountId(String cspId, String msgRecord);

    void addMsgRecordFailedReason(String cspId);
}
