package com.citc.nce.readingLetter;

import com.citc.nce.auth.readingLetter.shortUrl.ReadingLetterShortUrlApi;
import com.citc.nce.auth.readingLetter.shortUrl.vo.*;
import com.citc.nce.common.core.pojo.PageResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * 短链管理
 * @author ping chen
 */
@RestController
@Slf4j
@RequiredArgsConstructor
@Validated
@Api(tags = "阅信+短链管理")
@RequestMapping("/readingLetter/shortUrl")
public class ReadingLetterShortUrlController {
    private final ReadingLetterShortUrlApi readingLetterShortUrlApi;

    @PostMapping("apply")
    @ApiOperation("申请短链")
    public void apply(@RequestBody @Valid ReadingLetterShortUrlAddReq readingLetterShortUrlAddReq) {
        readingLetterShortUrlApi.apply(readingLetterShortUrlAddReq);
    }

    @PostMapping("list")
    @ApiOperation("搜索模板")
    public PageResult<ReadingLetterShortUrlVo> list(@RequestBody @Valid ReadingLetterShortUrlListReq readingLetterShortUrlListReq) {
        return readingLetterShortUrlApi.list(readingLetterShortUrlListReq);
    }

    @PostMapping("search")
    @ApiOperation("获取短链申请内容")
    public ReadingLetterShortUrlDetailVo search(@RequestBody @Valid ReadingLetterShortUrlSearchReq readingLetterShortUrlSearchReq) {
        return readingLetterShortUrlApi.search(readingLetterShortUrlSearchReq);
    }

    @PostMapping("reApply")
    @ApiOperation("重新申请短链")
    public void reApply(@RequestBody @Valid ReadingLetterShortUrlReApplyReq readingLetterShortUrlReApplyReq) {
        readingLetterShortUrlApi.reApply(readingLetterShortUrlReApplyReq);
    }

    @PostMapping("findAvailableShortUrls")
    @ApiOperation("查询可用的阅信+短链")
    List<ReadingLetterShortUrlVo> findAvailableShortUrls(@RequestBody ReadingLetterAvailableShortUrlsReq req) {
        return readingLetterShortUrlApi.findAvailableShortUrls(req);
    }

    @PostMapping("findShortUrlFromPlatform/{id}")
    @ApiOperation("查询阅信+短链详细信息")
    QueryShortUrlResultVo findShortUrlFromPlatform(@PathVariable("id") Long id) {
        return readingLetterShortUrlApi.findShortUrlFromPlatform(id);
    }

}
