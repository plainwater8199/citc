package com.citc.nce.auth.csp.mediasms.template;

import com.citc.nce.auth.csp.mediasms.template.vo.*;
import com.citc.nce.common.core.pojo.PageResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * @author jiancheng
 */
@FeignClient(value = "auth-service", contextId = "MediaSmsTemplate", url = "${auth:}")
public interface MediaSmsTemplateApi {

    @PostMapping("/mediaSms/template")
    void addTemplate(@RequestBody @Valid MediaSmsTemplateAddVo addVo);

    @PostMapping("/mediaSms/template/update")
    void updateTemplate(@RequestBody @Valid MediaSmsTemplateUpdateVo updateVo);

    @PostMapping("/mediaSms/template/delete")
    void deleteTemplate(@RequestBody @Valid MediaSmsTemplateCommonVo mediaSmsTemplateCommonVo);

    @PostMapping("/mediaSms/template/report/{templateId}")
    void reportTemplate(@PathVariable("templateId") Long templateId);

    @PostMapping("/mediaSms/template/search")
    PageResult<MediaSmsTemplateSimpleVo> searchTemplate(@RequestBody @Valid MediaSmsTemplateSearchVo searchVo);

    @PostMapping("/mediaSms/template/contents/{templateId}")
    MediaSmsTemplatePreviewVo getContents(@PathVariable("templateId") Long templateId, @RequestParam(value = "inner")Boolean inner);

    @PostMapping("/mediaSms/template/{templateId}")
    MediaSmsTemplateDetailVo getTemplateInfo(@PathVariable("templateId") Long templateId);

    @PostMapping("/mediaSms/template/by/{platformTemplateId}")
    MediaSmsTemplateDetailVo getTemplateInfoByPlatformTemplateId(@PathVariable("platformTemplateId") String platformTemplateId);

    @PostMapping("/mediaSms/template/audit")
    void updateAuditStatus(@RequestBody MediaSmsTemplateAuditUpdateVo auditUpdateVo);

    @PostMapping("/mediaSms/template/platformTemplateId")
    void setPlatformTemplateId(@RequestParam("id") Long id, @RequestParam("platformTemplateId") String platformTemplateId);

    @PostMapping("/mediaSms/template/effective")
    List<MediaSmsTemplateSimpleVo> findEffectiveTemplate(
            @RequestParam("accountIds") List<String> accountIds,
            @RequestParam(value = "accountId", required = false) String templateName,
            @RequestParam(value = "templateType", required = false) Integer templateType
    );

    @PostMapping("/mediaSms/template/accounts")
    List<MediaSmsHaveTemplateAccountVo> getHaveTemplateAccountsByUserId();

    @PostMapping("/mediaSms/template/accountIds")
    List<String> getTemplateAccountIds(@RequestBody @Valid List<Long> templateIds);

    @PostMapping("/mediaSms/template/delete/check")
    List<MediaSmsTemplateCheckVo> templateDeleteCheck(@RequestBody @Valid MediaSmsTemplateCommonVo mediaSmsTemplateCommonVo);

    @GetMapping("/mediaSms/template/queryById")
    MediaSmsTemplateVo getTemplateById(@RequestParam("id")Long id);

    @GetMapping("/mediaSms/template/getPlatformTemplateIdById")
    String getPlatformTemplateIdById(@RequestParam("id") Long id);
}
