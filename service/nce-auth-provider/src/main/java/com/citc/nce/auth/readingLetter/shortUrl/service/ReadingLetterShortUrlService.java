package com.citc.nce.auth.readingLetter.shortUrl.service;


import com.citc.nce.auth.readingLetter.shortUrl.vo.QueryShortUrlResultVo;
import com.citc.nce.auth.readingLetter.shortUrl.vo.ReadingLetterShortUrlAddReq;
import com.citc.nce.auth.readingLetter.shortUrl.vo.ReadingLetterShortUrlDetailVo;
import com.citc.nce.auth.readingLetter.shortUrl.vo.ReadingLetterShortUrlListReq;
import com.citc.nce.auth.readingLetter.shortUrl.vo.ReadingLetterShortUrlReApplyReq;
import com.citc.nce.auth.readingLetter.shortUrl.vo.ReadingLetterShortUrlSearchReq;
import com.citc.nce.auth.readingLetter.shortUrl.vo.ReadingLetterShortUrlVo;
import com.citc.nce.common.core.pojo.PageResult;

import java.util.List;
import java.util.Map;

/**
 * @author zjy
 */

public interface ReadingLetterShortUrlService {
    //申请短链
    void apply(ReadingLetterShortUrlAddReq readingLetterShortUrlAddReq);

    //列表查询
    PageResult<ReadingLetterShortUrlVo> list(ReadingLetterShortUrlListReq readingLetterShortUrlListReq);

    //获取某个短链申请内容
    ReadingLetterShortUrlDetailVo search(ReadingLetterShortUrlSearchReq readingLetterShortUrlSearchReq);

    //重新申请短链
    void reApply(ReadingLetterShortUrlReApplyReq readingLetterShortUrlReApplyReq);

    //根绝shortUrl在数据库中查找短链信息
    ReadingLetterShortUrlVo findShortUrl(String shortUrl);

    //根绝shortUrlIds在数据库中查找短链信息
    Map<Long, ReadingLetterShortUrlVo> findShortUrls(List<Long> urlIds);

    //处理解析记录
    void handleParseRecord(Long shortUrlId);

    //查询该用户可用的锻炼信息
    List<ReadingLetterShortUrlVo> findAvailableShortUrls(String nameOrUrl);

    //通过网关向平台查询某条短链的具体信息
    QueryShortUrlResultVo findShortUrlFromPlatform(Long id);

    //通过网关向平台查询某条短链的具体信息
    List<ReadingLetterShortUrlVo> findShortUrlByTemplateId(Long templateId);

    //找到已过期的并且未使用完的并且未处理的短链
    List<ReadingLetterShortUrlVo> findExpiredUnprocessedShortUrl();
}