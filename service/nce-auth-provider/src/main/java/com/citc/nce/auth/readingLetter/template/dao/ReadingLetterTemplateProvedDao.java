package com.citc.nce.auth.readingLetter.template.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.citc.nce.auth.readingLetter.template.entity.ReadingLetterTemplateProvedDo;
import com.citc.nce.auth.readingLetter.template.vo.ReadingLetterPlusTemplateProvedVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author zjy
 */
public interface ReadingLetterTemplateProvedDao extends BaseMapper<ReadingLetterTemplateProvedDo> {

    List<ReadingLetterPlusTemplateProvedVo> getReadingLetterPlusApprovedTemplate(@Param("customerId")String customerId, @Param("accountId")String accountId);
}
