package com.citc.nce.auth.readingLetter.dataStatistics;

import com.citc.nce.auth.readingLetter.dataStatistics.dto.DataStatisticsReq;
import com.citc.nce.auth.readingLetter.dataStatistics.dto.FifthDataStatisticsReq;
import com.citc.nce.auth.readingLetter.dataStatistics.service.ReadingLetterDataStatisticsService;
import com.citc.nce.auth.readingLetter.dataStatistics.vo.FifthReadingLetterDailyReportSelectVo;
import com.citc.nce.auth.readingLetter.dataStatistics.vo.ReadingLetterDailyReportSelectVo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * 文件名:ReadingLetterDataStatisticsController
 * 创建者:zhujinyu
 * 创建时间:2024/7/19 16:54
 * 描述:
 */
@RestController
@Slf4j
@RequiredArgsConstructor
public class ReadingLetterDataStatisticsController implements ReadingLetterDataStatisticsApi{

    @Resource
    private ReadingLetterDataStatisticsService dataStatisticsService;
    @Override
    public List<ReadingLetterDailyReportSelectVo> dataStatistics(DataStatisticsReq req) {
        return dataStatisticsService.dataStatistics(req);
    }

    @Override
    public List<FifthReadingLetterDailyReportSelectVo> fifthDataStatistics(FifthDataStatisticsReq req) {
        return dataStatisticsService.fifthDataStatistics(req);
    }
}
