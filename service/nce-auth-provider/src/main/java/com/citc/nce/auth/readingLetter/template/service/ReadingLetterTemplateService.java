package com.citc.nce.auth.readingLetter.template.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.citc.nce.auth.readingLetter.template.entity.ReadingLetterTemplateDo;
import com.citc.nce.auth.readingLetter.template.vo.ReadingLetterDeleteReq;
import com.citc.nce.auth.readingLetter.template.vo.ReadingLetterTemplateAddReq;
import com.citc.nce.auth.readingLetter.template.vo.ReadingLetterTemplateAuditReq;
import com.citc.nce.auth.readingLetter.template.vo.ReadingLetterTemplateNameRepeatReq;
import com.citc.nce.auth.readingLetter.template.vo.ReadingLetterTemplateOfFifthReq;
import com.citc.nce.auth.readingLetter.template.vo.ReadingLetterTemplateSearchReq;
import com.citc.nce.auth.readingLetter.template.vo.ReadingLetterTemplateSimpleVo;
import com.citc.nce.auth.readingLetter.template.vo.ReadingLetterTemplateUpdateReq;
import com.citc.nce.auth.readingLetter.template.vo.ReadingLetterTemplateVo;
import com.citc.nce.common.core.pojo.PageResult;

import java.util.List;

/**
 * @author zjy
 */
public interface ReadingLetterTemplateService extends IService<ReadingLetterTemplateDo> {
    /**
     * 新增模板
     *
     * @param readingLetterTemplateAddReq
     */
    Long addTemplate(ReadingLetterTemplateAddReq readingLetterTemplateAddReq);

    /**
     * 修改模板
     *
     * @param readingLetterTemplateUpdateReq
     */
    void updateTemplate(ReadingLetterTemplateUpdateReq readingLetterTemplateUpdateReq);

    /**
     * 搜索模板列表
     *
     * @param readingLetterTemplateSearchReq
     */
    PageResult<ReadingLetterTemplateSimpleVo> list(ReadingLetterTemplateSearchReq readingLetterTemplateSearchReq);

    ReadingLetterTemplateVo getTemplateInfo(Long templateId);

    ReadingLetterTemplateVo getTemplateInfoWithoutLogin(Long templateId);

    void deleteTemplate(ReadingLetterDeleteReq readingLetterDeleteReq);

    void auditTemplate(ReadingLetterTemplateAuditReq readingLetterTemplateAuditReq);

    List<ReadingLetterTemplateSimpleVo> getApprovedTemplateOfFifth(ReadingLetterTemplateOfFifthReq req);
    ReadingLetterTemplateSimpleVo getOneApprovedTemplate(Long id,Integer smsType);
    boolean deleteWarn(ReadingLetterDeleteReq req);

    boolean checkForRepeat(ReadingLetterTemplateNameRepeatReq req);
}
