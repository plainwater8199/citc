package com.citc.nce.auth.csp.smsTemplate.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.citc.nce.auth.csp.smsTemplate.entity.SmsTemplateDo;
import com.citc.nce.auth.csp.smsTemplate.vo.*;
import com.citc.nce.common.core.pojo.PageResult;

import java.util.List;

/**
 * @author ping chen
 */
public interface SmsTemplateService extends IService<SmsTemplateDo> {
    /**
     * 新增模板
     *
     * @param templateAddVo
     */
    void addTemplate(SmsTemplateAddVo templateAddVo);

    /**
     * 修改模板
     *
     * @param smsTemplateUpdateVo
     */
    void updateTemplate(SmsTemplateUpdateVo smsTemplateUpdateVo);

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
    PageResult<SmsTemplateSimpleVo> searchTemplate(SmsTemplateSearchVo smsTemplateSearchVo);

    /**
     * 查询模板详情
     *
     * @param templateId 模板ID
     */
    SmsTemplateDetailVo getTemplateInfo(Long templateId, Boolean delete);

    /**
     * 查询模板详情
     * 系统内部查询模板（定时任务、群发计划执行等）没有用户，不需要越权判断
     *
     * @param templateId 模板ID
     */
    SmsTemplateDetailVo getTemplateInfoInner(Long templateId, Boolean delete);

    SmsTemplateDetailVo getTemplateInfoByPlatformTemplateId(String platformTemplateId);


    /**
     * 修改模板审核状态
     *
     * @param smsTemplateAuditUpdateVo
     */
    void updateAuditStatus(SmsTemplateAuditUpdateVo smsTemplateAuditUpdateVo);

    String getSignatureById(Long signatureId);

    void reportTemplate(Long templateId, String signature, String accountId, String content, Integer templateType);

    /**
     * 送审模板
     *
     * @param id 模板ID
     */
    void reportTemplate(Long id);

    /**
     * 删除模板
     *
     * @param smsTemplateCommonVo
     */
    void deleteTemplate(SmsTemplateCommonVo smsTemplateCommonVo);

    /**
     * 检查模板是否能删除
     *
     * @param smsTemplateCommonVo
     * @return
     */
    List<SmsTemplateCheckVo> templateDeleteCheck(SmsTemplateCommonVo smsTemplateCommonVo);

    /**
     * 返回用户有模板的账号列表
     *
     * @param userId
     * @return
     */
    List<SmsHaveTemplateAccountVo> getHaveTemplateAccountsByUserId(String userId);

    /**
     * 刷新模板审核状态
     *
     * @param smsTemplateCommonVo
     */
    void refreshAuditStatus(SmsTemplateCommonVo smsTemplateCommonVo);

    /**
     * 模板测试发送短信
     *
     * @param smsTemplateTestSendVo
     */
    Boolean testSending(SmsTemplateTestSendVo smsTemplateTestSendVo);

    /**
     * 群发短信
     *
     * @param smsTemplateSendVo
     * @return
     */
    List<SmsTemplateVariable> sending(SmsTemplateSendVo smsTemplateSendVo);

    /**
     * 返回用户可用的模板列表
     *
     * @param smsTemplateEffectiveVo
     */
    List<SmsTemplateSimpleVo> findEffectiveTemplate(SmsTemplateEffectiveVo smsTemplateEffectiveVo);

    List<Long> existShortUrl(List<String> shortUrls);
}
