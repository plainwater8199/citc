package com.citc.nce.readingLetter.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.citc.nce.readingLetter.req.SelectParseRecordToDailyReportReq;
import com.citc.nce.readingLetter.req.TodayDataSelectReq;
import com.citc.nce.readingLetter.entity.ReadingLetterParseRecordDo;
import com.citc.nce.readingLetter.vo.CspReadingLetterNumVo;
import com.citc.nce.readingLetter.vo.DailyReportListVo;
import com.citc.nce.readingLetter.vo.ShortUrlIdListVo;

import java.util.List;

/**
 * 文件名:ReadingLetterParseRecordDao
 * 创建者:zhujinyu
 * 创建时间:2024/7/17 17:57
 * 描述:
 */
public interface ReadingLetterParseRecordDao extends BaseMapper<ReadingLetterParseRecordDo> {
    //查询该CSP下  昨天 的各阅信+短链的解析数据
    List<ShortUrlIdListVo> selectShortUrlIdList(SelectParseRecordToDailyReportReq day);
    //查询该CSP下  今天 的各阅信+短链的解析数据
    List<ShortUrlIdListVo> selectTodayDailyReport(TodayDataSelectReq day);

    //查询该CSP下  昨天 的各5G阅信的解析数据
    List<DailyReportListVo> selectFifthReadingLetterToDailyReport(SelectParseRecordToDailyReportReq day);
    //查询该CSP下  今天 的各5G阅信的解析数据
    List<DailyReportListVo> selectTodayFifthDailyReport(TodayDataSelectReq day);

    void createTableIfNotExist(String tableName);

    List<CspReadingLetterNumVo> selectTodayDailyReportOfCsp(TodayDataSelectReq selectShortUrlIdListReq);

}