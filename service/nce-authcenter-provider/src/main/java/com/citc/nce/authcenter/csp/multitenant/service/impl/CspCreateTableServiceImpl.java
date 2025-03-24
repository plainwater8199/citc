package com.citc.nce.authcenter.csp.multitenant.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.citc.nce.auth.csp.recharge.RechargeApi;
import com.citc.nce.auth.tenant.AuthCspTableManageApi;
import com.citc.nce.auth.tenant.req.AuthCreateCspTableReq;
import com.citc.nce.auth.tenant.req.AuthRefreshActualNodesReq;
import com.citc.nce.authcenter.constant.AuthCenterError;
import com.citc.nce.authcenter.csp.multitenant.service.CspCreateTableService;
import com.citc.nce.authcenter.csp.multitenant.service.CspService;
import com.citc.nce.authcenter.tableInfo.entity.CreateTableInfo;
import com.citc.nce.authcenter.tableInfo.service.TableInfoService;
import com.citc.nce.authcenter.tenantdata.user.dao.Csp1Dao;
import com.citc.nce.authcenter.tenantdata.user.dao.CspCustomer1Dao;
import com.citc.nce.authcenter.tenantdata.user.entity.CspDo;
import com.citc.nce.common.core.exception.BizException;
import com.citc.nce.seata.ShardingJdbcNodesAutoConfigurator;
import com.citc.nce.tenant.CspTableManageApi;
import com.citc.nce.tenant.vo.req.CreateCspTableReq;
import com.citc.nce.tenant.vo.req.RefreshActualNodesReq;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

@Service
@Slf4j
public class CspCreateTableServiceImpl implements CspCreateTableService {

    @Resource
    private CspService cspService;
    @Resource
    private Csp1Dao csp1Dao;
    @Resource
    private CspTableManageApi cspTableManageApi;
    @Resource
    private ShardingJdbcNodesAutoConfigurator shardingJdbcNodesAutoConfigurator;
    @Resource
    private TableInfoService tableInfoService;
    @Resource
    RechargeApi rechargeApi;
    @Resource
    AuthCspTableManageApi authCspTableManageApi;
    @Override
    public void createCspTable(String cspId) {
        LambdaQueryWrapper<CspDo> cspQueryWrapper = new LambdaQueryWrapper<>();
        cspQueryWrapper.eq(CspDo::getCspId, cspId);
        CspDo cspDo = csp1Dao.selectOne(cspQueryWrapper);
        if (Objects.isNull(cspDo)) {
            throw new BizException(AuthCenterError.CSP_ID_ERROR);
        }
        if (0 == cspDo.getIsSplite()) {
            try {
                //要创建的表名称
                createTableExecuteSql("csp_customer", cspId);
               //刷新别的表
                String result = cspTableManageApi.createTable(new CreateCspTableReq().setCspId(cspId));
                authCspTableManageApi.createTable(new AuthCreateCspTableReq().setCspId(cspId));
                if ("fail".equals(result)) {
                    throw new BizException(AuthCenterError.CSP_SPLITE_TABLE_EXCEPTION);
                } else {
                    cspDo.setIsSplite(1);
                    csp1Dao.updateById(cspDo);
                    //刷新真实表结点
                    refreshActualNodesForShardingJDBC();
                }
            } catch (Exception e) {
                log.error("分表失败", e);
                throw new BizException(AuthCenterError.CSP_SPLITE_TABLE_EXCEPTION);
            }
        }

    }

    void createTableExecuteSql(String tableName, String cspId) {
        String table = String.format("%s_%s", tableName, cspId);
        //查询模板表ddl-sql
        CreateTableInfo tempTable = tableInfoService.getCreateTableDDl(tableName);
        String ddl = tempTable.getSql().replace("CREATE TABLE `" + tempTable.getTableName(), "CREATE TABLE IF NOT EXISTS `" + table) + ";";
        log.info("创建表{} DDL :{}", table, ddl);
        tableInfoService.executeDDlSql(ddl);
    }

    @Override
    public void refreshActualNodesForShardingJDBC() {
        List<String> cspIds = cspService.queryAllCspId();
        List<String> cspCustomerNameList = new ArrayList<>();
        List<String> chargeConsumeRecordList = new ArrayList<>();
        for (String cspId : cspIds) {
            cspCustomerNameList.add("user.csp_customer_" + cspId);
            }

        shardingJdbcNodesAutoConfigurator.generateActualDataNodes("csp_customer", cspCustomerNameList);
        //刷新robot库
        try {
            RefreshActualNodesReq req = new RefreshActualNodesReq();
            req.setCspIdSet(new HashSet<>(cspIds));
            cspTableManageApi.refreshActualNodes(req);
            } catch (Exception e) {
            log.info("shardingjdbc启动刷新robot库失败！");
        }
        //刷新robot库
        try {
             AuthRefreshActualNodesReq authRefreshActualNodesReq = new AuthRefreshActualNodesReq();
            authRefreshActualNodesReq.setCspIdSet(new HashSet<>(cspIds));
            authCspTableManageApi.refreshActualNodes(authRefreshActualNodesReq);
        } catch (Exception e) {
            log.info("shardingjdbc启动刷新auth库失败！");
        }
    }

    @Override
    public void refreshChargeConsumeRecordTable() {
        List<String> cspIds = cspService.queryAllCspId();
        for (String cspId : cspIds
        ) {
          rechargeApi.InitChargeConsumeRecordTable(cspId);
        }
        //刷新真实表结点
        refreshActualNodesForShardingJDBC();
    }

    @Override
    public void dropTable(Set<String> cspIdSet) {
        cspTableManageApi.dropTables(cspIdSet);
        authCspTableManageApi.dropTables(cspIdSet);
    }

}
