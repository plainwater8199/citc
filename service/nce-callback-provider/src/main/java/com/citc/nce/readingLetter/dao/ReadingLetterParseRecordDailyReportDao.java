package com.citc.nce.readingLetter.dao;

import com.citc.nce.auth.readingLetter.dataStatistics.vo.FifthReadingLetterDailyReportSelectVo;
import com.citc.nce.auth.readingLetter.dataStatistics.vo.ReadingLetterDailyReportSelectVo;
import com.citc.nce.mybatis.core.mapper.BaseMapperX;
import com.citc.nce.readingLetter.req.FifthReadingLetterDailyReportSelectReq;
import com.citc.nce.readingLetter.req.ReadingLetterDailyReportSelectByCspIdReq;
import com.citc.nce.readingLetter.req.ReadingLetterDailyReportSelectReq;
import com.citc.nce.readingLetter.entity.ReadingLetterParseRecordDailyReportDo;
import com.citc.nce.readingLetter.vo.CspReadingLetterNumVo;

import java.util.List;

/**
 * 文件名:ReadingLetterParseRecordDao
 * 创建者:zhujinyu
 * 创建时间:2024/7/17 17:57
 * 描述:
 */
public interface ReadingLetterParseRecordDailyReportDao extends BaseMapperX<ReadingLetterParseRecordDailyReportDo> {

    List<ReadingLetterDailyReportSelectVo> selectRecords(ReadingLetterDailyReportSelectReq req);

    List<FifthReadingLetterDailyReportSelectVo>  selectFifthtRecords(FifthReadingLetterDailyReportSelectReq req);

    List<CspReadingLetterNumVo> selectAllByCspId(ReadingLetterDailyReportSelectByCspIdReq req);
}