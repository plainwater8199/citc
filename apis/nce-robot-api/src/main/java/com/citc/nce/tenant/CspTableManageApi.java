package com.citc.nce.tenant;

import com.citc.nce.tenant.vo.req.CreateCspTableReq;
import com.citc.nce.tenant.vo.req.RefreshActualNodesReq;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.Valid;
import java.util.Set;

@FeignClient(value = "rebot-service", contextId = "AuthCspTableManageApi", url = "${robot:}")
public interface CspTableManageApi {
    @ApiOperation("多租户批量建表")
    @PostMapping("/csp/robot/tenant/createTable")
    String createTable(@RequestBody @Valid CreateCspTableReq req);
    @ApiOperation("刷新真实结点信息")
    @PostMapping("/csp/robot/tenant/refreshActualNodes")
    void refreshActualNodes(@RequestBody @Valid RefreshActualNodesReq req);
    @ApiOperation("多租户批量删表")
    @PostMapping("/csp/robot/tenant/dropTable")
    void dropTable(@RequestParam("cspId") String cspId);
    @ApiOperation("多租户批量删表")
    @PostMapping("/csp/robot/tenant/dropTables")
    void dropTables(@RequestBody Set<String> cspIdSet);
}
