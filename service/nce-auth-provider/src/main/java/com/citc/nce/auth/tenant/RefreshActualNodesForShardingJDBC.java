package com.citc.nce.auth.tenant;

import cn.hutool.core.util.ObjectUtil;
import com.citc.nce.auth.tenant.req.AuthRefreshActualNodesReq;
import com.citc.nce.auth.tenant.service.CspTableManageService;
import com.citc.nce.authcenter.csp.CspApi;
import com.citc.nce.authcenter.csp.vo.QueryAllCspIdResp;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;

@Component
@Slf4j
public class RefreshActualNodesForShardingJDBC implements ApplicationRunner {
    @Autowired
    private CspApi cspApi;
    @Autowired
    private CspTableManageService cspTableManageService;



    @Override
    public void run(ApplicationArguments args) throws Exception {
        log.info("----------------启动后刷新sharding的真实结点----------------");
        try {
            QueryAllCspIdResp resp = cspApi.queryAllCspId();
            List<String> cspIds = resp.getCspIds();
            if (ObjectUtil.isEmpty(cspIds)) {
                log.info("启动后刷新sharding的真实结点,cspIds为空，刷新结束");
                return;
            }
//        List<String> cspIds = Arrays.asList("6307167392","7479264746","7514153566","9947815343");
            AuthRefreshActualNodesReq req = new AuthRefreshActualNodesReq();
            req.setCspIdSet(new HashSet<>(cspIds));
            cspTableManageService.refreshActualNodes(req);
            log.info("----------------启动后刷新sharding的真实结点------------完成----");
        }catch (Exception exception)
        {
            log.error("启动后刷新sharding的真实结点异常(tenant.RefreshActualNodesForShardingJDBC)",exception);
            throw  exception;
        }
    }
}
