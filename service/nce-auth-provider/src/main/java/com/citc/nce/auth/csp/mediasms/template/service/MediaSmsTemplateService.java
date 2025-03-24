package com.citc.nce.auth.csp.mediasms.template.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.citc.nce.auth.csp.mediasms.template.entity.MediaSmsTemplateContentDo;
import com.citc.nce.auth.csp.mediasms.template.entity.MediaSmsTemplateDo;
import com.citc.nce.auth.csp.mediasms.template.enums.AuditStatus;
import com.citc.nce.auth.csp.mediasms.template.enums.OperatorPlatform;
import com.citc.nce.auth.csp.mediasms.template.vo.*;
import com.citc.nce.common.core.pojo.PageResult;

import java.util.List;

/**
 * @author jiancheng
 */
public interface MediaSmsTemplateService extends IService<MediaSmsTemplateDo> {
    /**
     * 新增模板
     *
     * @param templateAddVo
     */
    void addTemplate(MediaSmsTemplateAddVo templateAddVo);

    /**
     * 修改模板
     *
     * @param templateUpdateVo
     */
    void updateTemplate(MediaSmsTemplateUpdateVo templateUpdateVo);

    /**
     * 校验模板名是否重复
     *
     * @param accountId    账号
     * @param templateName 模板名
     * @return 如果有重复的模板名返回true
     */
    boolean isRepeatTemplateName(String accountId, String templateName);

    /**
     * 搜索模板
     *
     * @return
     */
    PageResult<MediaSmsTemplateSimpleVo> searchTemplate(
            String templateName,
            String accountId,
            OperatorPlatform operator,
            AuditStatus auditStatus,
            Integer templateType,
            Long pageNum,
            Long pageSize
    );

    /**
     * 查询模板内容
     *
     * @param templateId 模板ID
     */
    MediaSmsTemplatePreviewVo getContents(Long templateId,Boolean inner);

    /**
     * 查询模板详情
     *
     * @param templateId 模板ID
     */
    MediaSmsTemplateDetailVo getTemplateInfo(Long templateId);

    MediaSmsTemplateDetailVo getTemplateInfoByPlatformTemplateId(String platformTemplateId);

    /**
     * 修改模板审核状态
     *
     * @param auditUpdateVo
     */
    void updateAuditStatus(MediaSmsTemplateAuditUpdateVo auditUpdateVo);

    /**
     * 送审模板
     * @param id 模板ID
     */
    void reportTemplate(Long id);

    /**
     * 回写平台模板ID
     *
     * @param id                 模板ID
     * @param platformTemplateId 平台模板ID
     */
    void setPlatformTemplateId(Long id, String platformTemplateId);

    /**
     * 删除模板
     *
     * @param mediaSmsTemplateCommonVo 模板ID
     */
    void deleteTemplate(MediaSmsTemplateCommonVo mediaSmsTemplateCommonVo);


    /**
     * 返回用户可用的模板列表(有任意一家运营商通过审核并没有任何一家还在审核中)已经其审核信息
     *
     * @param accountIds    账号ID
     * @param templateName 模板名称，模糊查询
     */
    List<MediaSmsTemplateSimpleVo> findEffectiveTemplate(List<String> accountIds, String templateName,Integer templateType);

    /**
     * 返回用户有模板的账号列表
     * @param userId
     * @return
     */
    List<MediaSmsHaveTemplateAccountVo> getHaveTemplateAccountsByUserId(String userId);

    /**
     * 根据模板ID查询对应的账号ID
     * @param templateIds
     * @return
     */
    List<String> getTemplateAccountIds(List<Long> templateIds);

    /**
     * 模板删除前检查
     * @param mediaSmsTemplateCommonVo
     * @return 校验通过：0 有执行计划正在使用：1
     */
    List<MediaSmsTemplateCheckVo> templateDeleteCheck(MediaSmsTemplateCommonVo mediaSmsTemplateCommonVo);

    void reportTemplate(Long templateId, String accountId, String templateName, String sign, List<MediaSmsTemplateContentDo> contents, Integer templateType);
}
