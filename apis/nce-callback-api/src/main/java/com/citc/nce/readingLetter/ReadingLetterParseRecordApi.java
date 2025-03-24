package com.citc.nce.readingLetter;

import com.citc.nce.auth.readingLetter.dataStatistics.vo.FifthReadingLetterDailyReportSelectVo;
import com.citc.nce.auth.readingLetter.dataStatistics.vo.ReadingLetterDailyReportSelectVo;
import com.citc.nce.readingLetter.req.FifthReadingLetterDailyReportSelectReq;
import com.citc.nce.readingLetter.req.ReadingLetterDailyReportCreateReq;
import com.citc.nce.readingLetter.req.ReadingLetterDailyReportSelectReq;
import com.citc.nce.readingLetter.req.ReadingLetterMsgSendTotalResp;
import com.citc.nce.readingLetter.vo.ReadingLetterSendTotalResp;
import com.citc.nce.tenant.vo.req.RefreshActualNodesReq;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * <p>挂短-回调</p>
 *
 * @Author zjy
 * @CreatedTime 202/7/18 17:35
 */
@FeignClient(value = "callback-service", contextId = "ReadingLetterParseRecordApi", url = "${callback:}")
public interface ReadingLetterParseRecordApi {

    @PostMapping("/createTable/{cspId}")
    void createTable(@PathVariable("cspId") String cspId);

    @PostMapping("/selectRecords")
    List<ReadingLetterDailyReportSelectVo> selectRecords(@RequestBody ReadingLetterDailyReportSelectReq req);

    @PostMapping("/selectFifthRecords")
    List<FifthReadingLetterDailyReportSelectVo> selectRecords(@RequestBody FifthReadingLetterDailyReportSelectReq req);

    @ApiOperation(value = "查询昨日 5G阅信/阅信+ 的解析数量")
    @PostMapping("/queryReadingLetterTotal")
    ReadingLetterMsgSendTotalResp queryReadingLetterTotal();

    @ApiOperation("根据账号类型查询所有阅信发送")
    @PostMapping("/msg/analysis/queryReadingLetterSendTotal")
    ReadingLetterSendTotalResp queryReadingLetterCspTotal();

    @ApiOperation(value = "日报制作接口")
    @PostMapping("/dailyReport")
    void dailyReport(@RequestBody ReadingLetterDailyReportCreateReq req);

    @ApiOperation(value = "创建解析表")
    @PostMapping("/refreshActualNodes")
    void refreshActualNodes(@RequestBody RefreshActualNodesReq req);
}
