package com.citc.nce.auth.readingLetter.template.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.citc.nce.auth.accountmanagement.vo.AccountManagementResp;
import com.citc.nce.auth.csp.readingLetter.vo.CspReadingLetterDetailResp;
import com.citc.nce.auth.readingLetter.template.entity.ReadingLetterTemplateAuditDo;
import com.citc.nce.auth.readingLetter.template.entity.ReadingLetterTemplateDo;
import com.citc.nce.auth.readingLetter.template.vo.ReadingLetterAuditDeleteReq;
import com.citc.nce.auth.readingLetter.template.vo.ReadingLetterTemplateNameVo;

import java.util.List;

/**
 * @author zjy
 */
public interface ReadingLetterTemplateAuditService extends IService<ReadingLetterTemplateAuditDo> {

    void audit(ReadingLetterTemplateDo readingLetterTemplateDo, List<CspReadingLetterDetailResp> readingLetterAccounts, List<AccountManagementResp> fontdo);

    void reAudit(ReadingLetterTemplateDo readingLetterTemplateDo, List<CspReadingLetterDetailResp> readingLetterAccounts, List<AccountManagementResp> fontdoAccount);

    //删除阅信模板审核记录
    void deleteAuditAndProved(Long id);

    //删除阅信模板审核记录
    void deleteAuditAndProvedByAccount(ReadingLetterAuditDeleteReq req);

    String getPlatformTemplateIdByAccountIdAndTemplateId(String accountId, Long TemplateId);

    List<ReadingLetterTemplateNameVo> getTemplatesByPlatformTemplateIdList(List<String> platformTemplateIdList);
}
