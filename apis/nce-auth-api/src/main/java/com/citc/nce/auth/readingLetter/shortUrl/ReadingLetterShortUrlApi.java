package com.citc.nce.auth.readingLetter.shortUrl;

import com.citc.nce.auth.readingLetter.shortUrl.vo.QueryShortUrlResultVo;
import com.citc.nce.auth.readingLetter.shortUrl.vo.ReadingLetterAvailableShortUrlsReq;
import com.citc.nce.auth.readingLetter.shortUrl.vo.ReadingLetterShortUrlAddReq;
import com.citc.nce.auth.readingLetter.shortUrl.vo.ReadingLetterShortUrlDetailVo;
import com.citc.nce.auth.readingLetter.shortUrl.vo.ReadingLetterShortUrlListReq;
import com.citc.nce.auth.readingLetter.shortUrl.vo.ReadingLetterShortUrlReApplyReq;
import com.citc.nce.auth.readingLetter.shortUrl.vo.ReadingLetterShortUrlSearchReq;
import com.citc.nce.auth.readingLetter.shortUrl.vo.ReadingLetterShortUrlVo;
import com.citc.nce.common.core.pojo.PageResult;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

/**
 * @author zjy
 */
@FeignClient(value = "auth-service", contextId = "ReadingLetterShortUrl", url = "${auth:}")
public interface ReadingLetterShortUrlApi {

    @PostMapping("apply")
    @ApiOperation("申请短链")
    void apply(@RequestBody @Valid ReadingLetterShortUrlAddReq readingLetterShortUrlAddReq);

    @PostMapping("list")
    @ApiOperation("搜索模板")
    PageResult<ReadingLetterShortUrlVo> list(@RequestBody @Valid ReadingLetterShortUrlListReq readingLetterShortUrlListReq);

    @PostMapping("handleParseRecord/{id}")
    @ApiOperation("处理解析记录")
    void handleParseRecord(@PathVariable("id")Long id);

    @PostMapping("search")
    @ApiOperation("获取短链申请内容")
    ReadingLetterShortUrlDetailVo search(@RequestBody @Valid ReadingLetterShortUrlSearchReq readingLetterShortUrlSearchReq);

    @PostMapping("reApply")
    @ApiOperation("重新申请短链")
    void reApply(@RequestBody @Valid ReadingLetterShortUrlReApplyReq readingLetterShortUrlReApplyReq);

    @PostMapping("findShortUrl")
    @ApiOperation("寻找短链")
    ReadingLetterShortUrlVo findShortUrl(@RequestParam("shortUrl") String shortUrl);

    @PostMapping("findShortUrls")
    @ApiOperation("批量查询短链信息")
    Map<Long, ReadingLetterShortUrlVo> findShortUrlsByUrlIds(@RequestBody List<Long> urlIds);

    @PostMapping("findAvailableShortUrls")
    @ApiOperation("查询可用的阅信+短链")
    List<ReadingLetterShortUrlVo> findAvailableShortUrls(@RequestBody ReadingLetterAvailableShortUrlsReq re);

    @PostMapping("findShortUrlFromPlatform/{id}")
    @ApiOperation("查询可用的阅信+短链")
    QueryShortUrlResultVo findShortUrlFromPlatform(@PathVariable("id") Long id);
}