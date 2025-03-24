package com.citc.nce.auth.readingLetter.template.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.citc.nce.auth.csp.smsTemplate.entity.SmsTemplateDo;
import com.citc.nce.auth.csp.smsTemplate.vo.SmsHaveTemplateAccountVo;
import com.citc.nce.auth.csp.smsTemplate.vo.SmsTemplateSimpleVo;
import com.citc.nce.auth.readingLetter.template.entity.ReadingLetterTemplateDo;
import com.citc.nce.auth.readingLetter.template.vo.ReadingLetterTemplateSimpleVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author zjy
 */
public interface ReadingLetterTemplateDao extends BaseMapper<ReadingLetterTemplateDo> {

    Page<ReadingLetterTemplateSimpleVo> selectReadingLetterTemplates(@Param("customerId")String customerId,
                                                                     @Param("templateName") String templateName,
                                                                     @Param("operatorCode") Integer operatorCode,
                                                                     @Param("smsType") Integer smsType,
                                                                     @Param("status") Integer status,
                                                                     Page<ReadingLetterTemplateSimpleVo> page);

    List<ReadingLetterTemplateSimpleVo> getApprovedTemplateOfFifth(@Param("customerId")String customerId, @Param("templateName")String templateName, @Param("accounts")List<String> accounts);
    ReadingLetterTemplateSimpleVo getOneApprovedTemplate(@Param("customerId")String customerId, @Param("id")Long id, @Param("smsType")Integer smsType);
}
