package com.citc.nce.readingLetter.config;

import cn.hutool.core.util.ObjectUtil;
import com.citc.nce.authcenter.csp.CspApi;
import com.citc.nce.authcenter.csp.vo.QueryAllCspIdResp;
import com.citc.nce.tenant.vo.req.RefreshActualNodesReq;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.HashSet;
import java.util.List;


@Component
@Slf4j
public class RefreshActualNodesForShardingJDBC implements ApplicationRunner {

    @Resource
    private CspTableManageService cspTableManageService;

    @Resource
    private CspApi cspApi;

    @Override
    public void run(ApplicationArguments args) {
        log.info("----------------启动后刷新sharding的真实结点----------------");
        QueryAllCspIdResp resp = cspApi.queryAllCspId();
        List<String> cspIds = resp.getCspIds();
        if (ObjectUtil.isEmpty(cspIds)) {
            return;
        }
        RefreshActualNodesReq req = new RefreshActualNodesReq();
        req.setCspIdSet(new HashSet<>(cspIds));
        cspTableManageService.refreshActualNodes(req);
        log.info("----------------启动后刷新sharding的真实结点------------完成----");
    }
}
