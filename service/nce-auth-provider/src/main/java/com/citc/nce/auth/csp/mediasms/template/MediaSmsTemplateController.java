package com.citc.nce.auth.csp.mediasms.template;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.citc.nce.auth.csp.mediasms.template.entity.MediaSmsTemplateDo;
import com.citc.nce.auth.csp.mediasms.template.enums.ContentMediaType;
import com.citc.nce.auth.csp.mediasms.template.service.MediaSmsTemplateService;
import com.citc.nce.auth.csp.mediasms.template.vo.*;
import com.citc.nce.common.core.exception.BizException;
import com.citc.nce.common.core.pojo.PageResult;
import com.citc.nce.common.util.SessionContextUtil;
import com.citc.nce.mybatis.core.entity.BaseDo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

/**
 * @author jiancheng
 */
@RestController
@Slf4j
@RequiredArgsConstructor
public class MediaSmsTemplateController implements MediaSmsTemplateApi {
    private final MediaSmsTemplateService templateService;

    @Override
    public void addTemplate(@RequestBody @Valid MediaSmsTemplateAddVo addVo) {
        List<MediaSmsTemplateContentAddVo> contents = addVo.getContents();
        contents.stream()
                .filter(c -> c.getMediaType() != ContentMediaType.TEXT && StringUtils.isEmpty(c.getName()))
                .findAny()
                .ifPresent(c -> {
                    if (c.getMediaType() == null)
                        throw new BizException(500, "资源类型不能为空");
                    throw new BizException(500, c.getMediaType().getAlias() + "媒体资源名称不能为空");
                });
        templateService.addTemplate(addVo);
    }

    @Override
    public void updateTemplate(@RequestBody @Valid MediaSmsTemplateUpdateVo updateVo) {
        List<MediaSmsTemplateContentAddVo> contents = updateVo.getContents();
        contents.stream()
                .filter(c -> c.getMediaType() != ContentMediaType.TEXT && StringUtils.isEmpty(c.getName()))
                .findAny()
                .ifPresent(c -> {
                    throw new BizException(500, c.getMediaType().getAlias() + "媒体资源名称不能为空");
                });
        templateService.updateTemplate(updateVo);
    }

    @Override
    public void deleteTemplate(MediaSmsTemplateCommonVo mediaSmsTemplateCommonVo) {
        templateService.deleteTemplate(mediaSmsTemplateCommonVo);
    }

    @Override
    public void reportTemplate(Long templateId) {
        templateService.reportTemplate(templateId);
    }

    @Override
    public PageResult<MediaSmsTemplateSimpleVo> searchTemplate(MediaSmsTemplateSearchVo searchVo) {
        return templateService.searchTemplate(
                searchVo.getTemplateName(),
                searchVo.getAccountId(),
                searchVo.getOperator(),
                searchVo.getAuditStatus(),
                searchVo.getTemplateType(),
                searchVo.getPageNo(),
                searchVo.getPageSize()
        );
    }

    @Override
    public MediaSmsTemplatePreviewVo getContents(Long templateId, Boolean inner) {
        return templateService.getContents(templateId, inner);
    }

    @Override
    public MediaSmsTemplateDetailVo getTemplateInfo(Long templateId) {
        return templateService.getTemplateInfo(templateId);
    }

    @Override
    public MediaSmsTemplateDetailVo getTemplateInfoByPlatformTemplateId(String platformTemplateId) {
        return templateService.getTemplateInfoByPlatformTemplateId(platformTemplateId);
    }


    @Override
    public void updateAuditStatus(MediaSmsTemplateAuditUpdateVo auditUpdateVo) {
        templateService.updateAuditStatus(auditUpdateVo);
    }

    @Override
    public void setPlatformTemplateId(Long id, String platformTemplateId) {
        templateService.setPlatformTemplateId(id, platformTemplateId);
    }

    @Override
    public List<MediaSmsTemplateSimpleVo> findEffectiveTemplate(List<String> accountIds, String templateName, Integer templateType) {
        return templateService.findEffectiveTemplate(accountIds, templateName, templateType);
    }

    @Override
    public List<MediaSmsHaveTemplateAccountVo> getHaveTemplateAccountsByUserId() {
        return templateService.getHaveTemplateAccountsByUserId(SessionContextUtil.getUser().getUserId());
    }

    @Override
    public List<String> getTemplateAccountIds(List<Long> templateIds) {
        return templateService.getTemplateAccountIds(templateIds);
    }

    @Override
    public List<MediaSmsTemplateCheckVo> templateDeleteCheck(MediaSmsTemplateCommonVo mediaSmsTemplateCommonVo) {
        return templateService.templateDeleteCheck(mediaSmsTemplateCommonVo);
    }

    @Override
    public MediaSmsTemplateVo getTemplateById(Long id) {
        MediaSmsTemplateDo templateDo = templateService.getById(id);
        if (templateDo == null)
            return null;
        MediaSmsTemplateVo vo = new MediaSmsTemplateVo();
        vo.setId(templateDo.getId());
        vo.setTemplateName(templateDo.getTemplateName());
        vo.setCustomerId(templateDo.getCustomerId());
        return vo;
    }

    @Override
    public String getPlatformTemplateIdById(Long id) {
        @SuppressWarnings("unchecked") LambdaQueryWrapper<MediaSmsTemplateDo> qw = new LambdaQueryWrapper<MediaSmsTemplateDo>()
                .eq(BaseDo::getId, id)
                .select(MediaSmsTemplateDo::getPlatformTemplateId);
        return templateService.getOneOpt(qw).map(MediaSmsTemplateDo::getPlatformTemplateId).orElse(null);
    }
}
