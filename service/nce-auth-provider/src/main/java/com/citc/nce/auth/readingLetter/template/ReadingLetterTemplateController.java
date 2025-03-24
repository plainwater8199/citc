package com.citc.nce.auth.readingLetter.template;

import com.citc.nce.auth.readingLetter.template.service.ReadingLetterTemplateAuditService;
import com.citc.nce.auth.readingLetter.template.service.ReadingLetterTemplateProvedService;
import com.citc.nce.auth.readingLetter.template.service.ReadingLetterTemplateService;
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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * 短信模板管理
 *
 * @author zjy
 */
@RestController
@Slf4j
@RequiredArgsConstructor
public class ReadingLetterTemplateController implements ReadingLetterTemplateApi {

    @Resource
    private ReadingLetterTemplateService readingLetterTemplateService;

    @Resource
    private ReadingLetterTemplateAuditService readingLetterTemplateAuditService;

    @Resource
    private ReadingLetterTemplateProvedService templateProvedService;

    @Override
    public Long addTemplate(ReadingLetterTemplateAddReq readingLetterTemplateAddReq) {
        log.info("addTemplate:{}", readingLetterTemplateAddReq);
        return readingLetterTemplateService.addTemplate(readingLetterTemplateAddReq);
    }

    @Override
    public void updateTemplate(ReadingLetterTemplateUpdateReq readingLetterTemplateUpdateReq) {
        readingLetterTemplateService.updateTemplate(readingLetterTemplateUpdateReq);
    }

    @Override
    public PageResult<ReadingLetterTemplateSimpleVo> list(ReadingLetterTemplateSearchReq readingLetterTemplateSearchReq) {
        return readingLetterTemplateService.list(readingLetterTemplateSearchReq);
    }

    @Override
    public ReadingLetterTemplateVo getTemplateInfo(@PathVariable Long templateId) {
        return readingLetterTemplateService.getTemplateInfo(templateId);
    }

    @Override
    public ReadingLetterTemplateVo getTemplateInfoWithoutLogin(@PathVariable Long templateId) {
        return readingLetterTemplateService.getTemplateInfoWithoutLogin(templateId);
    }

    @Override
    public void deleteTemplate(ReadingLetterDeleteReq readingLetterDeleteReq) {
        readingLetterTemplateService.deleteTemplate(readingLetterDeleteReq);
    }

    @Override
    public void deleteAuditAndProvedByAccount(ReadingLetterAuditDeleteReq req) {
        readingLetterTemplateAuditService.deleteAuditAndProvedByAccount(req);
    }

    @Override
    public void auditTemplate(ReadingLetterTemplateAuditReq readingLetterTemplateAuditReq) {
        readingLetterTemplateService.auditTemplate(readingLetterTemplateAuditReq);
    }

    @Override
    public List<ReadingLetterPlusTemplateProvedVo> getReadingLetterPlusApprovedTemplate(String accountId) {
        return templateProvedService.getReadingLetterPlusApprovedTemplate(accountId);
    }

    @Override
    public List<ReadingLetterTemplateSimpleVo> getApprovedTemplateOfFifth(ReadingLetterTemplateOfFifthReq req) {
        return readingLetterTemplateService.getApprovedTemplateOfFifth(req);
    }

    @Override
    public ReadingLetterTemplateSimpleVo getOneApprovedTemplate(Long id, Integer smsType) {
        return readingLetterTemplateService.getOneApprovedTemplate(id, smsType);
    }

    @Override
    public boolean deleteWarn(ReadingLetterDeleteReq req) {
        return readingLetterTemplateService.deleteWarn(req);
    }

    @Override
    public boolean checkForRepeat(ReadingLetterTemplateNameRepeatReq req) {
        return readingLetterTemplateService.checkForRepeat(req);
    }

    @Override
    public String getPlatformTemplateIdByAccountIdAndTemplateId(String chatbotAccountId, Long fallbackReadingLetterTemplateId) {
        return readingLetterTemplateAuditService.getPlatformTemplateIdByAccountIdAndTemplateId(chatbotAccountId, fallbackReadingLetterTemplateId);
    }
}
