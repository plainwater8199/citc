package com.citc.nce.tenant;

import com.citc.nce.tenant.vo.req.CreateTableReq;
import com.citc.nce.tenant.vo.req.MsgRecordDataSynReq;
import com.citc.nce.tenant.vo.req.StatisticSyncReq;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.Valid;
import java.util.Set;

@FeignClient(value = "rebot-service", contextId = "CreateTenantTableApi", url = "${robot:}")
public interface CreateTenantTableApi {


    @ApiOperation("多租户批量更新数据")
    @PostMapping("/csp/robot/tenant/updateDate")
    void updateDate(@RequestBody @Valid CreateTableReq req2);

    @ApiOperation("多租户统计数据批量同步")
    @PostMapping("/csp/robot/tenant/statisticSync")
    void statisticSync(@RequestBody @Valid StatisticSyncReq req);

    @ApiOperation("多租户统计数据更新")
    @PostMapping("/csp/robot/tenant/updateStatistic")
    void updateStatistic(@RequestBody Set<String> cspIdSet);

//    @ApiOperation("多租户统计-sign字段统计")
//    @PostMapping("/csp/robot/tenant/msgRecordDataSyn")
//    void msgRecordDataSyn(@RequestBody MsgRecordDataSynReq req);

}
