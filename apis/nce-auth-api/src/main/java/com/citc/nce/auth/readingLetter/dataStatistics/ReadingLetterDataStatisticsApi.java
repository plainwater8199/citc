package com.citc.nce.auth.readingLetter.dataStatistics;

import com.citc.nce.auth.readingLetter.dataStatistics.dto.DataStatisticsReq;
import com.citc.nce.auth.readingLetter.dataStatistics.dto.FifthDataStatisticsReq;
import com.citc.nce.auth.readingLetter.dataStatistics.vo.FifthReadingLetterDailyReportSelectVo;
import com.citc.nce.auth.readingLetter.dataStatistics.vo.ReadingLetterDailyReportSelectVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;
import java.util.List;

/**
 * @author zjy
 */
@FeignClient(value = "auth-service", contextId = "ReadingLetterDataStatisticsApi", url = "${auth:}")
public interface ReadingLetterDataStatisticsApi {

    @PostMapping("/dataStatistics")
    List<ReadingLetterDailyReportSelectVo> dataStatistics(@RequestBody DataStatisticsReq req);

    @PostMapping("/fifthDataStatistics")
    public List<FifthReadingLetterDailyReportSelectVo> fifthDataStatistics(@RequestBody @Valid FifthDataStatisticsReq req);
}