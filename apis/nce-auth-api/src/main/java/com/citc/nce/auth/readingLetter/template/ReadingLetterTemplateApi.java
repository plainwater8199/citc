package com.citc.nce.auth.readingLetter.template;

import com.citc.nce.auth.readingLetter.template.vo.ReadingLetterAuditDeleteReq;
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
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.Valid;
import java.util.List;

/**
 * @author ping chen
 */
@FeignClient(value = "auth-service", contextId = "ReadingLetter", url = "${auth:}")
public interface ReadingLetterTemplateApi {

    @PostMapping("/readingLetter/template/save")
    Long addTemplate(@RequestBody @Valid ReadingLetterTemplateAddReq readingLetterTemplateAddReq);

    @PostMapping("/readingLetter/template/update")
    void updateTemplate(@RequestBody @Valid ReadingLetterTemplateUpdateReq readingLetterTemplateUpdateVo);

    @PostMapping("/readingLetter/template/list")
    PageResult<ReadingLetterTemplateSimpleVo> list(@RequestBody @Valid ReadingLetterTemplateSearchReq readingLetterTemplateSearchReq);

    @PostMapping("{templateId}")
    ReadingLetterTemplateVo getTemplateInfo(@PathVariable("templateId") Long templateId);

    @PostMapping("/readingLetter/getTemplateInfoWithoutLogin/{templateId}")
    ReadingLetterTemplateVo getTemplateInfoWithoutLogin(@PathVariable("templateId") Long templateId);

    @PostMapping("delete")
    @ApiOperation("删除模板")
    void deleteTemplate(@RequestBody @Valid ReadingLetterDeleteReq readingLetterDeleteReq);

    @PostMapping("/readingLetter/audit/delete")
    @ApiOperation("删除模板")
    void deleteAuditAndProvedByAccount(@RequestBody @Valid ReadingLetterAuditDeleteReq req);

    @PostMapping("audit")
    @ApiOperation("送审模板")
    void auditTemplate(@RequestBody @Valid ReadingLetterTemplateAuditReq readingLetterTemplateAuditReq);

    @PostMapping("getApprovedTemplate")
    @ApiOperation("查询已过审的阅信+模板")
    List<ReadingLetterPlusTemplateProvedVo> getReadingLetterPlusApprovedTemplate(@RequestParam("accountId") String accountId);


    @PostMapping("getApprovedTemplateOfFifth")
    @ApiOperation("查询已过审的阅信模板(5G阅信)")
    List<ReadingLetterTemplateSimpleVo> getApprovedTemplateOfFifth(@RequestBody ReadingLetterTemplateOfFifthReq req);

    @GetMapping("getOneApprovedTemplate")
    @ApiOperation("查询单个已过审的阅信模板(5G阅信)")
    ReadingLetterTemplateSimpleVo getOneApprovedTemplate(@RequestParam("id") Long id, @RequestParam("smsType") Integer smsType);

    @PostMapping("deleteWarn")
    @ApiOperation("删除前进行判断预警")
    boolean deleteWarn(@RequestBody @Valid ReadingLetterDeleteReq req);

    @PostMapping("/readingLetter/template/checkForRepeat")
    @ApiOperation("检查模板名是否重复")
    boolean checkForRepeat(@RequestBody @Valid ReadingLetterTemplateNameRepeatReq req);

    @GetMapping("/readingLetter/template/getPlatformTemplateId")
    @ApiOperation("通过帐号Id和模板Id获取平台模板Id")
    String getPlatformTemplateIdByAccountIdAndTemplateId(@RequestParam("chatbotAccountId") String chatbotAccountId, @RequestParam("templateId") Long fallbackReadingLetterTemplateId);
}
