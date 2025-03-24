package com.citc.nce.auth.readingLetter.dataStatistics.service;

import com.citc.nce.auth.readingLetter.dataStatistics.dto.DataStatisticsReq;
import com.citc.nce.auth.readingLetter.dataStatistics.dto.FifthDataStatisticsReq;
import com.citc.nce.auth.readingLetter.dataStatistics.vo.FifthReadingLetterDailyReportSelectVo;
import com.citc.nce.auth.readingLetter.dataStatistics.vo.ReadingLetterDailyReportSelectVo;

import java.util.List;

/**
 * @author zjy
 */
public interface ReadingLetterDataStatisticsService {

    List<ReadingLetterDailyReportSelectVo> dataStatistics(DataStatisticsReq req);

    List<FifthReadingLetterDailyReportSelectVo> fifthDataStatistics(FifthDataStatisticsReq req);
}
