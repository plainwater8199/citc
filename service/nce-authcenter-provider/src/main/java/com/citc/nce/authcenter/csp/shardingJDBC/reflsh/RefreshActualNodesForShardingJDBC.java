package com.citc.nce.authcenter.csp.shardingJDBC.reflsh;

import com.citc.nce.authcenter.csp.multitenant.service.CspCreateTableService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;


@Component
@Slf4j
@Order(2)
public class RefreshActualNodesForShardingJDBC implements ApplicationRunner {

    @Resource
    private CspCreateTableService cspCreateTable;

    @Override
    public void run(ApplicationArguments args) {
        log.info("----------------启动后刷新sharding的真实结点----------开始------");
        try {
            cspCreateTable.refreshActualNodesForShardingJDBC();
            log.info("----------------启动后刷新sharding的真实结点-----------结束-----");
        }catch (Exception exception)
        {
            log.error("启动后刷新sharding的真实结点异常(reflsh.RefreshActualNodesForShardingJDBC)",exception);
            throw  exception;
        }
    }
}
