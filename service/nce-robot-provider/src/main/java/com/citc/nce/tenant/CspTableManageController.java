package com.citc.nce.tenant;


import com.citc.nce.tenant.service.CspTableManageService;
import com.citc.nce.tenant.vo.req.CreateCspTableReq;
import com.citc.nce.tenant.vo.req.RefreshActualNodesReq;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.Set;

@RestController()
@Slf4j
public class CspTableManageController implements CspTableManageApi {

    @Resource
    private CspTableManageService cspTableManageService;


    @Override
    @ApiOperation("多租户批量建表")
    @PostMapping("/csp/robot/tenant/createTable")
    public String createTable(@RequestBody @Valid CreateCspTableReq req) {
        return cspTableManageService.createTable(req);
    }

    @Override
    @ApiOperation("刷新真实结点信息")
    @PostMapping("/csp/robot/tenant/refreshActualNodes")
    public void refreshActualNodes(@RequestBody @Valid RefreshActualNodesReq req) {
        cspTableManageService.refreshActualNodes(req);
    }

    @Override
    @ApiOperation("多租户批量删表")
    @PostMapping("/csp/robot/tenant/dropTable")
    public void dropTable(@RequestParam String cspId) {
        cspTableManageService.dropTableByCspId(cspId);
    }

    @Override
    @ApiOperation("多租户批量删表")
    @PostMapping("/csp/robot/tenant/dropTables")
    public void dropTables(Set<String> cspIdSet) {
        for(String cspId : cspIdSet){
            cspTableManageService.dropTableByCspIdAndTableName(cspId,"msg_record");
            cspTableManageService.dropTableByCspIdAndTableName(cspId,"conversational_quantity_statistic");
            cspTableManageService.dropTableByCspIdAndTableName(cspId,"process_quantity_statistic");
            cspTableManageService.dropTableByCspIdAndTableName(cspId,"robot_record");
            cspTableManageService.dropTableByCspIdAndTableName(cspId,"msg_quantity_statistics");
        }

    }

}
