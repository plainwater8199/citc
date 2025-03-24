package com.citc.nce.readingLetter;

import com.citc.nce.auth.readingLetter.dataStatistics.vo.FifthReadingLetterDailyReportSelectVo;
import com.citc.nce.auth.readingLetter.dataStatistics.vo.ReadingLetterDailyReportSelectVo;
import com.citc.nce.common.core.exception.BizException;
import com.citc.nce.common.facadeserver.annotations.SkipToken;
import com.citc.nce.readingLetter.req.ReadingLetterParseRecordReq;
import com.citc.nce.readingLetter.service.ReadingLetterParseRecordService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/readingLetterCallback")
@Api(value = "ReadingLetterParseRecordReceiveController", tags = "阅信+解析回执接收")
@Slf4j
public class ReadingLetterParseRecordReceiveController{

    @Resource
    private ReadingLetterParseRecordService readingLetterParseRecordService;

    @SkipToken
    @ApiOperation(value = "阅信+解析回执接收接口")
    @PostMapping("/shortUrl/report")
    public void receive(@RequestBody ReadingLetterParseRecordReq req) {
        log.info("聚合层收到网关回调信息：{}", req);
        readingLetterParseRecordService.receive(req);
    }
}
