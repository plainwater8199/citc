package com.citc.nce.auth.readingLetter.template.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.citc.nce.auth.readingLetter.template.dao.ReadingLetterTemplateProvedDao;
import com.citc.nce.auth.readingLetter.template.entity.ReadingLetterTemplateProvedDo;
import com.citc.nce.auth.readingLetter.template.service.ReadingLetterTemplateProvedService;
import com.citc.nce.auth.readingLetter.template.vo.ReadingLetterPlusTemplateProvedVo;
import com.citc.nce.auth.readingLetter.template.vo.ReadingLetterTemplateOfFifthReq;
import com.citc.nce.auth.readingLetter.template.vo.ReadingLetterTemplateSimpleVo;
import com.citc.nce.common.util.SessionContextUtil;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author zjy
 */
@Service
public class ReadingLetterTemplateProvedServiceImpl extends ServiceImpl<ReadingLetterTemplateProvedDao, ReadingLetterTemplateProvedDo> implements ReadingLetterTemplateProvedService {
    @Resource
    private ReadingLetterTemplateProvedDao templateProvedDao;
    @Override
    public List<ReadingLetterPlusTemplateProvedVo> getReadingLetterPlusApprovedTemplate(String accountId) {
        String customerId = SessionContextUtil.getUser().getUserId();
        List<ReadingLetterPlusTemplateProvedVo> readingLetterTemplateDos = templateProvedDao.getReadingLetterPlusApprovedTemplate(customerId,accountId);
        return readingLetterTemplateDos;
    }
}
