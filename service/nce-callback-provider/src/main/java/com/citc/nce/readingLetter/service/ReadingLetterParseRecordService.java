package com.citc.nce.readingLetter.service;

import com.citc.nce.auth.readingLetter.dataStatistics.vo.FifthReadingLetterDailyReportSelectVo;
import com.citc.nce.auth.readingLetter.dataStatistics.vo.ReadingLetterDailyReportSelectVo;
import com.citc.nce.readingLetter.req.FifthReadingLetterDailyReportSelectReq;
import com.citc.nce.readingLetter.req.ReadingLetterDailyReportSelectReq;
import com.citc.nce.readingLetter.req.ReadingLetterMsgSendTotalResp;
import com.citc.nce.readingLetter.req.ReadingLetterParseRecordReq;
import com.citc.nce.readingLetter.vo.ReadingLetterSendTotalResp;

import java.util.List;

/**
 * 文件名:ReadingLetterParseRecordService
 * 创建者:zhujinyu
 * 创建时间:2024/7/17 17:10
 * 描述:
 */
public interface ReadingLetterParseRecordService {

    void receive(ReadingLetterParseRecordReq req);

    void dailyReport(int offsetDay, String selectedCspId);

    void createTable(String cspId);

    List<ReadingLetterDailyReportSelectVo> selectRecords(ReadingLetterDailyReportSelectReq req);

    List<FifthReadingLetterDailyReportSelectVo> selectRecords(FifthReadingLetterDailyReportSelectReq req);

    ReadingLetterMsgSendTotalResp queryReadingLetterTotal();

    ReadingLetterSendTotalResp queryReadingLetterCspTotal();
}
