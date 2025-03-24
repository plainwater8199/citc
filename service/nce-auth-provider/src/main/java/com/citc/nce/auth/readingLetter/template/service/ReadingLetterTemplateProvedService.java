package com.citc.nce.auth.readingLetter.template.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.citc.nce.auth.readingLetter.template.entity.ReadingLetterTemplateProvedDo;
import com.citc.nce.auth.readingLetter.template.vo.ReadingLetterPlusTemplateProvedVo;

import java.util.List;

/**
 * @author zjy
 */
public interface ReadingLetterTemplateProvedService extends IService<ReadingLetterTemplateProvedDo> {

    List<ReadingLetterPlusTemplateProvedVo> getReadingLetterPlusApprovedTemplate(String accountId);

}
