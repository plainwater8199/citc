package com.citc.nce.auth.csp.smsTemplate;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.citc.nce.auth.csp.mediasms.template.entity.MediaSmsTemplateDo;
import com.citc.nce.auth.csp.smsTemplate.entity.SmsTemplateDo;
import com.citc.nce.auth.csp.smsTemplate.service.SmsTemplateService;
import com.citc.nce.auth.csp.smsTemplate.vo.*;
import com.citc.nce.common.core.pojo.PageResult;
import com.citc.nce.common.util.SessionContextUtil;
import com.citc.nce.developer.DeveloperSendApi;
import com.citc.nce.mybatis.core.entity.BaseDo;
import com.citc.nce.robot.vo.SmsTemplateSendVariable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

/**
 * 短信模板管理
 * @author ping chen
 */
@RestController
@Slf4j
@RequiredArgsConstructor
public class SmsTemplateController implements SmsTemplateApi {
    private final SmsTemplateService smsTemplateService;
    private final DeveloperSendApi developerSendApi;

    @Override
    public void addTemplate(@RequestBody @Valid SmsTemplateAddVo smsTemplateAddVo) {
        smsTemplateService.addTemplate(smsTemplateAddVo);
    }

    @Override
    public void updateTemplate(@RequestBody @Valid SmsTemplateUpdateVo smsTemplateUpdateVo) {
        smsTemplateService.updateTemplate(smsTemplateUpdateVo);
    }

    @Override
    public void deleteTemplate(SmsTemplateCommonVo smsTemplateCommonVo) {
        smsTemplateService.deleteTemplate(smsTemplateCommonVo);
    }

    @Override
    public List<SmsTemplateCheckVo> templateDeleteCheck(SmsTemplateCommonVo smsTemplateCommonVo) {
        return smsTemplateService.templateDeleteCheck(smsTemplateCommonVo);
    }


    @Override
    public void reportTemplate(Long templateId) {
        smsTemplateService.reportTemplate(templateId);
    }

    @Override
    public PageResult<SmsTemplateSimpleVo> searchTemplate(SmsTemplateSearchVo smsTemplateSearchVo) {
        return smsTemplateService.searchTemplate(smsTemplateSearchVo);
    }

    @Override
    public SmsTemplateDetailVo getTemplateInfo(Long templateId,Boolean delete) {
        return smsTemplateService.getTemplateInfo(templateId,delete);
    }

    @Override
    public SmsTemplateDetailVo getTemplateInfoByPlatformTemplateId(String platformTemplateId) {
        return smsTemplateService.getTemplateInfoByPlatformTemplateId(platformTemplateId);
    }

    @Override
    public SmsTemplateDetailVo getTemplateInfoInner(Long templateId, Boolean delete) {
        return smsTemplateService.getTemplateInfoInner(templateId, delete);
    }

    @Override
    public void updateAuditStatus(SmsTemplateAuditUpdateVo smsTemplateAuditUpdateVo) {
        smsTemplateService.updateAuditStatus(smsTemplateAuditUpdateVo);
    }

    @Override
    public List<SmsHaveTemplateAccountVo> getHaveTemplateAccountsByUserId() {
        return smsTemplateService.getHaveTemplateAccountsByUserId(SessionContextUtil.getUser().getUserId());
    }

    @Override
    public void refreshAuditStatus(SmsTemplateCommonVo smsTemplateCommonVo) {
        smsTemplateService.refreshAuditStatus(smsTemplateCommonVo);
    }

    public Boolean testSending(SmsTemplateTestSendVo smsTemplateTestSendVo) {
        return smsTemplateService.testSending(smsTemplateTestSendVo);
    }

    public List<SmsTemplateVariable> sending(SmsTemplateSendVo smsTemplateSendVo) {
        return smsTemplateService.sending(smsTemplateSendVo);
    }

    public List<SmsTemplateSimpleVo> findEffectiveTemplate(SmsTemplateEffectiveVo smsTemplateEffectiveVo) {
        return smsTemplateService.findEffectiveTemplate(smsTemplateEffectiveVo);
    }

    @Override
    public void test(String message) {
        developerSendApi.test(message);
    }

    @Override
    public String getPlatformTemplateIdById(Long id) {
        @SuppressWarnings("unchecked") LambdaQueryWrapper<SmsTemplateDo> qw = new LambdaQueryWrapper<SmsTemplateDo>()
                .eq(BaseDo::getId, id)
                .select(SmsTemplateDo::getPlatformTemplateId);
        return smsTemplateService.getOneOpt(qw).map(SmsTemplateDo::getPlatformTemplateId).orElse(null);
    }

    @Override
    public List<Long> existShortUrl(List<String> shortUrls) {
       return smsTemplateService.existShortUrl(shortUrls);
    }
}
