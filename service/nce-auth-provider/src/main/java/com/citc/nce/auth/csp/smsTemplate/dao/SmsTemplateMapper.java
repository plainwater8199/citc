package com.citc.nce.auth.csp.smsTemplate.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.citc.nce.auth.csp.mediasms.template.vo.MediaSmsHaveTemplateAccountVo;
import com.citc.nce.auth.csp.smsTemplate.entity.SmsTemplateDo;
import com.citc.nce.auth.csp.smsTemplate.vo.SmsHaveTemplateAccountVo;
import com.citc.nce.auth.csp.smsTemplate.vo.SmsTemplateSimpleVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author ping chen
 */
public interface SmsTemplateMapper extends BaseMapper<SmsTemplateDo> {

    Page<SmsTemplateSimpleVo> searchTemplate(
            @Param("userId") String userId,
            @Param("templateName") String templateName,
            @Param("templateType") Integer templateType,
            @Param("accountId") String accountId,
            @Param("status") Integer status,
            Page<SmsTemplateSimpleVo> page
    );

    Page<SmsTemplateSimpleVo> searchTemplateOther(
            @Param("userId") String userId,
            @Param("templateName") String templateName,
            @Param("templateType") Integer templateType,
            @Param("accountId") String accountId,
            @Param("status") Integer status,
            Page<SmsTemplateSimpleVo> page
    );

    List<SmsHaveTemplateAccountVo> getHaveTemplateAccountsByUserId(@Param("customerId")String customerId);
    SmsTemplateDo querySmsTemplate(Long id);

    boolean exists(@Param("templateId") Long templateId, @Param("userId") String userId);
}
