package com.citc.nce.readingLetter;

import com.citc.nce.auth.readingLetter.template.ReadingLetterTemplateApi;
import com.citc.nce.auth.readingLetter.template.vo.ReadingLetterDeleteReq;
import com.citc.nce.auth.readingLetter.template.vo.ReadingLetterPlusTemplateProvedVo;
import com.citc.nce.auth.readingLetter.template.vo.ReadingLetterTemplateAddReq;
import com.citc.nce.auth.readingLetter.template.vo.ReadingLetterTemplateAuditReq;
import com.citc.nce.auth.readingLetter.template.vo.ReadingLetterTemplateNameRepeatReq;
import com.citc.nce.auth.readingLetter.template.vo.ReadingLetterTemplateOfFifthReq;
import com.citc.nce.auth.readingLetter.template.vo.ReadingLetterTemplateSearchReq;
import com.citc.nce.auth.readingLetter.template.vo.ReadingLetterTemplateSimpleVo;
import com.citc.nce.auth.readingLetter.template.vo.ReadingLetterTemplateUpdateReq;
import com.citc.nce.auth.readingLetter.template.vo.ReadingLetterTemplateVo;
import com.citc.nce.common.core.pojo.PageResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 阅信模板管理
 * @author ping chen
 */
@RestController
@Slf4j
@RequiredArgsConstructor
@Validated
@Api(tags = "阅信模板")
@RequestMapping("/readingLetter/template")
public class ReadingLetterTemplateController {
    private final ReadingLetterTemplateApi readingLetterTemplateApi;

    @PostMapping("add")
    @ApiOperation("新增模板")
    public Long addTemplate(@RequestBody @Valid ReadingLetterTemplateAddReq readingLetterTemplateAddReq) {
        return readingLetterTemplateApi.addTemplate(readingLetterTemplateAddReq);
    }

    @PostMapping("checkForRepeat")
    @ApiOperation("检查模板名是否重复")
    public boolean checkForRepeat(@RequestBody @Valid ReadingLetterTemplateNameRepeatReq req) {
        return readingLetterTemplateApi.checkForRepeat(req);
    }

    @PostMapping("update")
    @ApiOperation("修改模板")
    public void updateTemplate(@RequestBody @Valid ReadingLetterTemplateUpdateReq readingLetterTemplateUpdateReq) {
        readingLetterTemplateApi.updateTemplate(readingLetterTemplateUpdateReq);
    }

    @PostMapping("list")
    @ApiOperation("搜索模板列表")
    public PageResult<ReadingLetterTemplateSimpleVo> list(@RequestBody @Valid ReadingLetterTemplateSearchReq readingLetterTemplateSearchReq) {
        log.info("开始搜索阅信模板列表");
        PageResult<ReadingLetterTemplateSimpleVo> list = readingLetterTemplateApi.list(readingLetterTemplateSearchReq);
        log.info("搜索模板阅信列表结束");
        return list;
    }

    @PostMapping("{templateId}")
    @ApiOperation("获取模板详情")
    public ReadingLetterTemplateVo getTemplateInfo(@PathVariable("templateId") Long templateId) {
        return readingLetterTemplateApi.getTemplateInfo(templateId);
    }

    @PostMapping("deleteWarn")
    @ApiOperation("删除前进行判断预警")
    public boolean deleteWarn(@RequestBody @Valid ReadingLetterDeleteReq req) {
        return readingLetterTemplateApi.deleteWarn(req);
    }

    @PostMapping("delete")
    @ApiOperation("删除模板")
    public void deleteTemplate(@RequestBody @Valid ReadingLetterDeleteReq readingLetterDeleteReq) {
        readingLetterTemplateApi.deleteTemplate(readingLetterDeleteReq);
    }

    @PostMapping("audit")
    @ApiOperation("送审模板")
    public void auditTemplate(@RequestBody @Valid ReadingLetterTemplateAuditReq readingLetterTemplateAuditReq) {
        readingLetterTemplateApi.auditTemplate(readingLetterTemplateAuditReq);
    }

    @PostMapping("getReadingLetterPlusApprovedTemplate")
    @ApiOperation("查询已过审的阅信+模板")
    public List<ReadingLetterPlusTemplateProvedVo> getReadingLetterPlusApprovedTemplate(@RequestParam("accountId") String accountId) {
        return readingLetterTemplateApi.getReadingLetterPlusApprovedTemplate(accountId);
    }

    @PostMapping("getApprovedTemplateOfFifth")
    @ApiOperation("查询已过审的阅信模板(5G阅信)")
    public List<ReadingLetterTemplateSimpleVo> getApprovedTemplateOfFifth(@RequestBody ReadingLetterTemplateOfFifthReq req) {
        return readingLetterTemplateApi.getApprovedTemplateOfFifth(req);
    }
    @GetMapping("getOneApprovedTemplate")
    @ApiOperation("查询单个已过审的阅信模板(5G阅信)")
    public ReadingLetterTemplateSimpleVo getOneApprovedTemplate(@NotNull @RequestParam("id") Long id,@NotNull @RequestParam("smsType") Integer smsType)
    {
        return readingLetterTemplateApi.getOneApprovedTemplate(id,smsType);
    }
}
