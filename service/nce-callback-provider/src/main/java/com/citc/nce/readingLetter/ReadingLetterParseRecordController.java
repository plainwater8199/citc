package com.citc.nce.readingLetter;

import com.citc.nce.auth.readingLetter.dataStatistics.vo.FifthReadingLetterDailyReportSelectVo;
import com.citc.nce.auth.readingLetter.dataStatistics.vo.ReadingLetterDailyReportSelectVo;
import com.citc.nce.common.core.exception.BizException;
import com.citc.nce.common.facadeserver.annotations.UnWrapResponse;
import com.citc.nce.misc.schedule.ScheduleApi;
import com.citc.nce.readingLetter.config.CspTableManageService;
import com.citc.nce.readingLetter.req.FifthReadingLetterDailyReportSelectReq;
import com.citc.nce.readingLetter.req.ReadingLetterDailyReportCreateReq;
import com.citc.nce.readingLetter.req.ReadingLetterDailyReportSelectReq;
import com.citc.nce.readingLetter.req.ReadingLetterMsgSendTotalResp;
import com.citc.nce.readingLetter.service.ReadingLetterParseRecordService;
import com.citc.nce.readingLetter.vo.ReadingLetterSendTotalResp;
import com.citc.nce.tenant.vo.req.RefreshActualNodesReq;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@RestController
@Api(value = "ReadingLetterParseRecordController", tags = "阅信+解析回执接收")
@Slf4j
public class ReadingLetterParseRecordController implements ReadingLetterParseRecordApi {

    @Resource
    private ReadingLetterParseRecordService readingLetterParseRecordService;
    @Resource
    private CspTableManageService cspTableManageService;
    @Resource
    ScheduleApi scheduleApi;

    @Override
    @UnWrapResponse
    public ReadingLetterMsgSendTotalResp queryReadingLetterTotal() {
        return readingLetterParseRecordService.queryReadingLetterTotal();
    }

    @Override
    @UnWrapResponse
    //api类的请求必须加上该注解
    public ReadingLetterSendTotalResp queryReadingLetterCspTotal() {
        return readingLetterParseRecordService.queryReadingLetterCspTotal();
    }

    @Override
    public void dailyReport(ReadingLetterDailyReportCreateReq req) {
        log.info("开始生成每日报表");
        if (req.getOffsetDay() != null && req.getOffsetDay() > -1) {
            throw new BizException("offsetDay必须小于0");
        }
        readingLetterParseRecordService.dailyReport(req.getOffsetDay() == null ? -1 : req.getOffsetDay(), req.getCspId());
    }


    @Scheduled(cron = "0 1 0 * * ?")
    public void dailyReport() {
        boolean canExec = scheduleApi.addRecord("readingLetterDailyReport", "H");
        if (!canExec) {
            return;
        }
        log.info("开始生成每日报表");
        readingLetterParseRecordService.dailyReport(-1, null);
    }

    @Override
    public void createTable(String cspId) {
        readingLetterParseRecordService.createTable(cspId);
    }

    @Override
    public void refreshActualNodes(RefreshActualNodesReq req) {
        cspTableManageService.refreshActualNodes(req);
    }

    @Override
    @UnWrapResponse
    public List<ReadingLetterDailyReportSelectVo> selectRecords(ReadingLetterDailyReportSelectReq req) {
        return readingLetterParseRecordService.selectRecords(req);
    }

    @Override
    @UnWrapResponse
    public List<FifthReadingLetterDailyReportSelectVo> selectRecords(FifthReadingLetterDailyReportSelectReq req) {
        return readingLetterParseRecordService.selectRecords(req);
    }
}
