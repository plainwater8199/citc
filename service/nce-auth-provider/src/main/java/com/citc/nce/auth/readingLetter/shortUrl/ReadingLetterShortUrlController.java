package com.citc.nce.auth.readingLetter.shortUrl;

import com.citc.nce.auth.readingLetter.shortUrl.service.ReadingLetterShortUrlService;
import com.citc.nce.auth.readingLetter.shortUrl.vo.QueryShortUrlResultVo;
import com.citc.nce.auth.readingLetter.shortUrl.vo.ReadingLetterAvailableShortUrlsReq;
import com.citc.nce.auth.readingLetter.shortUrl.vo.ReadingLetterShortUrlAddReq;
import com.citc.nce.auth.readingLetter.shortUrl.vo.ReadingLetterShortUrlDetailVo;
import com.citc.nce.auth.readingLetter.shortUrl.vo.ReadingLetterShortUrlListReq;
import com.citc.nce.auth.readingLetter.shortUrl.vo.ReadingLetterShortUrlReApplyReq;
import com.citc.nce.auth.readingLetter.shortUrl.vo.ReadingLetterShortUrlSearchReq;
import com.citc.nce.auth.readingLetter.shortUrl.vo.ReadingLetterShortUrlVo;
import com.citc.nce.common.core.pojo.PageResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * 短信模板管理
 *
 * @author zjy
 */
@RestController
@Slf4j
@RequiredArgsConstructor
public class ReadingLetterShortUrlController implements ReadingLetterShortUrlApi {

    @Resource
    private ReadingLetterShortUrlService readingLetterShortUrlService;

    @Override
    public void apply(ReadingLetterShortUrlAddReq readingLetterShortUrlAddReq) {
        readingLetterShortUrlService.apply(readingLetterShortUrlAddReq);
    }

    @Override
    public PageResult<ReadingLetterShortUrlVo> list(ReadingLetterShortUrlListReq readingLetterShortUrlListReq) {
        return readingLetterShortUrlService.list(readingLetterShortUrlListReq);
    }
    @Override
    public void handleParseRecord(@PathVariable("id")Long id) {
        //处理解析记录
        readingLetterShortUrlService.handleParseRecord(id);
    }

    @Override
    public ReadingLetterShortUrlDetailVo search(ReadingLetterShortUrlSearchReq readingLetterShortUrlSearchReq) {
        return readingLetterShortUrlService.search(readingLetterShortUrlSearchReq);
    }

    @Override
    public void reApply(ReadingLetterShortUrlReApplyReq readingLetterShortUrlReApplyReq) {
        readingLetterShortUrlService.reApply(readingLetterShortUrlReApplyReq);
    }

    @Override
    public ReadingLetterShortUrlVo findShortUrl(String shortUrl) {
        return readingLetterShortUrlService.findShortUrl(shortUrl);
    }

    @Override
    public Map<Long, ReadingLetterShortUrlVo> findShortUrlsByUrlIds(List<Long> urlIds) {
        return readingLetterShortUrlService.findShortUrls(urlIds);
    }

    @Override
    public List<ReadingLetterShortUrlVo> findAvailableShortUrls(ReadingLetterAvailableShortUrlsReq req) {
        return readingLetterShortUrlService.findAvailableShortUrls(req.getNameOrUrl());
    }

    @Override
    public QueryShortUrlResultVo findShortUrlFromPlatform(Long id) {
        return readingLetterShortUrlService.findShortUrlFromPlatform(id);
    }

}
