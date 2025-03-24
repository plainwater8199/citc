package com.citc.nce.tenant;

import com.citc.nce.dataStatistics.service.DataStatisticsScheduleService;
import com.citc.nce.tenant.service.CreateTenantTableService;
import com.citc.nce.tenant.service.StatisticSyncService;
import com.citc.nce.tenant.vo.req.CreateTableReq;
import com.citc.nce.tenant.vo.req.MsgRecordDataSynReq;
import com.citc.nce.tenant.vo.req.StatisticSyncReq;
import com.citc.nce.utils.DateUtils;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Date;
import java.util.Set;

@RestController()
@Slf4j
public class CreateTenantTableController implements CreateTenantTableApi {

    @Resource
    private CreateTenantTableService createTenantTableService;

    @Resource
    private StatisticSyncService statisticSyncService;

    @Resource
    private DataStatisticsScheduleService dataStatisticsScheduleService;


    @Override
    @ApiOperation("多租户批量更新数据")
    @PostMapping("/csp/robot/tenant/updateDate")
    public void updateDate(CreateTableReq req) {
        createTenantTableService.updateDate(req);
    }



    @Override
    @ApiOperation("多租户统计数据批量同步")
    @PostMapping("/csp/robot/tenant/statisticSync")
    public void statisticSync(@RequestBody StatisticSyncReq req) {
        statisticSyncService.statisticSync(req);
    }

    @Override
    @ApiOperation("多租户统计数据更新")
    @PostMapping("/csp/robot/tenant/updateStatistic")
    public void updateStatistic(@RequestBody Set<String> cspIdSet) {
        statisticSyncService.updateStatistic(cspIdSet);
    }


    @ApiOperation("多租户统计数据批量同步")
    @PostMapping("/csp/robot/tenant/statisticSyncTest")
    public void statisticSyncTest(@RequestBody StatisticSyncReq req) {
        Date startDate = DateUtils.obtainAddDay(new Date(),-10);
        startDate = DateUtils.obtainEarliestTime(startDate);
        dataStatisticsScheduleService.statisticPerHour(null,startDate,new Date());
//        dataStatisticsScheduleService.statisticPerHour(null, DateUtils.obtainDate("2024-02-01 00:00:00"),DateUtils.obtainDate("2024-03-18 23:59:59"));
    }

//    @ApiOperation("多租户统计-sign字段统计")
//    @PostMapping("/csp/robot/tenant/msgRecordDataSyn")
//    public void msgRecordDataSyn(@RequestBody MsgRecordDataSynReq req) {
//        statisticSyncService.msgRecordDataSyn(req);
//    }


}
