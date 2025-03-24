package com.citc.nce.authcenter.tenantdata.user.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.citc.nce.auth.csp.mediasms.template.enums.AuditStatus;
import com.citc.nce.auth.csp.mediasms.template.enums.OperatorPlatform;
import com.citc.nce.auth.csp.mediasms.template.vo.MediaSmsHaveTemplateAccountVo;
import com.citc.nce.auth.csp.mediasms.template.vo.MediaSmsTemplateSimpleVo;
import com.citc.nce.authcenter.tenantdata.user.entity.CspVideoSmsTemplateDo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author jiancheng
 */
public interface MediaSmsTemplate1Dao extends BaseMapper<CspVideoSmsTemplateDo> {

    Page<MediaSmsTemplateSimpleVo> searchTemplate(
            @Param("userId") String userId,
            @Param("templateName") String templateName,
            @Param("accountId") String accountId,
            @Param("operator") OperatorPlatform operator,
            @Param("auditStatus") AuditStatus auditStatus,
            @Param("templateType") Integer templateType,
            Page<MediaSmsTemplateSimpleVo> page
    );

    List<MediaSmsHaveTemplateAccountVo> getHaveTemplateAccountsByUserId(@Param("userId")String userId);
}
