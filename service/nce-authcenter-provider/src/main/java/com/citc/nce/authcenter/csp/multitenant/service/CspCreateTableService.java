package com.citc.nce.authcenter.csp.multitenant.service;

import java.util.Set;

public interface CspCreateTableService {

    void createCspTable(String cspId);


    void refreshActualNodesForShardingJDBC();

    void refreshChargeConsumeRecordTable();
    void dropTable(Set<String> cspIdSet);
}
