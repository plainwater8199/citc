package com.citc.nce.auth.readingLetter.shortUrl.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.citc.nce.auth.readingLetter.dataStatistics.vo.DetailInfoVo;
import com.citc.nce.auth.readingLetter.shortUrl.entity.ReadingLetterShortUrlDo;
import com.citc.nce.auth.readingLetter.shortUrl.vo.ReadingLetterShortUrlVo;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;


/**
 * @author zjy
 */
public interface ReadingLetterShortUrlDao extends BaseMapper<ReadingLetterShortUrlDo> {

    Page<ReadingLetterShortUrlVo> selectShortUrl(@Param("customerId") String customerId, @Param("templateIdList") List<Long> templateIdList, @Param("operatorCode") Integer operatorCode, @Param("auditStatus") Integer auditStatus, @Param("taskStatus") Integer taskStatus, Page<ReadingLetterShortUrlVo> page);

    Page<ReadingLetterShortUrlVo> selectShortUrlWithoutTemplateId(@Param("customerId") String customerId, @Param("operatorCode") Integer operatorCode, @Param("auditStatus") Integer auditStatus, @Param("taskStatus") Integer taskStatus, Page<ReadingLetterShortUrlVo> page);

    List<ReadingLetterShortUrlVo> selectListByIds(@Param("ids") List<Long> urlIds);

    DetailInfoVo getDetailInfo(Long shortUrlId);

    List<Long> getShortUrlIds(@Param("accountIdList") List<String> accountIdList, @Param("shortUrl") String shortUrl);

    int addResolvedNumber(@Param("shortUrlId") Long shortUrlId);

    List<ReadingLetterShortUrlDo> findAvailableShortUrls(@Param("customerId") String customerId, @Param("nameOrUrl") String nameOrUrl);

    List<ReadingLetterShortUrlVo> findExpiredUnprocessedShortUrl(@Param("time") Date time);
}
