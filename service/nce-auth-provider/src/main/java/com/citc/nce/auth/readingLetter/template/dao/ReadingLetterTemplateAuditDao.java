package com.citc.nce.auth.readingLetter.template.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.citc.nce.auth.readingLetter.template.entity.ReadingLetterTemplateAuditDo;
import com.citc.nce.auth.readingLetter.template.entity.ReadingLetterTemplateDo;
import com.citc.nce.auth.readingLetter.template.vo.ReadingLetterTemplateNameVo;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @author zjy
 */
public interface ReadingLetterTemplateAuditDao extends BaseMapper<ReadingLetterTemplateAuditDo> {
    @Select("select distinct * from reading_letter_template t left join reading_letter_template_audit a on t.id=a.template_id where a.platform_template_id = #{platform_id}")
    ReadingLetterTemplateDo getTemplateByPlatformTemplateId(String id);

    List<ReadingLetterTemplateNameVo> getTemplatesByPlatformTemplateIdList(@Param("platformTemplateIdList") List<String> platformTemplateIdList);
}
