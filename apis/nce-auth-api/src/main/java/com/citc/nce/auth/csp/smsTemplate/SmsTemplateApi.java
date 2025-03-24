package com.citc.nce.auth.csp.smsTemplate;

import com.citc.nce.auth.csp.smsTemplate.vo.*;
import com.citc.nce.common.core.pojo.PageResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * @author ping chen
 */
@FeignClient(value = "auth-service", contextId = "SmsTemplate", url = "${auth:}")
public interface SmsTemplateApi {

    @PostMapping("/sms/template")
    void addTemplate(@RequestBody @Valid SmsTemplateAddVo smsTemplateAddVo);

    @PostMapping("/sms/template/update")
    void updateTemplate(@RequestBody @Valid SmsTemplateUpdateVo smsTemplateUpdateVo);

    @PostMapping("/sms/template/delete")
    void deleteTemplate(@RequestBody SmsTemplateCommonVo smsTemplateCommonVo);

    @PostMapping("/sms/template/delete/check")
    List<SmsTemplateCheckVo> templateDeleteCheck(@RequestBody SmsTemplateCommonVo smsTemplateCommonVo);

    @PostMapping("/sms/template/report/{templateId}")
    void reportTemplate(@PathVariable("templateId") Long templateId);

    @PostMapping("/sms/template/search")
    PageResult<SmsTemplateSimpleVo> searchTemplate(@RequestBody @Valid SmsTemplateSearchVo smsTemplateSearchVo);

    @PostMapping("/sms/template/getTemplateInfo/{templateId}/{delete}")
    SmsTemplateDetailVo getTemplateInfo(@PathVariable("templateId") Long templateId,@PathVariable("delete") Boolean delete);

    @PostMapping("/sms/template/getTemplateInfoInner/{templateId}")
    SmsTemplateDetailVo getTemplateInfoInner(@PathVariable("templateId") Long templateId, @RequestParam("delete") Boolean delete);

    @PostMapping("/sms/templateByPlatformId/{platformTemplateId}")
    SmsTemplateDetailVo getTemplateInfoByPlatformTemplateId(@PathVariable("platformTemplateId") String platformTemplateId);

    @PostMapping("/sms/template/audit")
    void updateAuditStatus(@RequestBody SmsTemplateAuditUpdateVo smsTemplateAuditUpdateVo);

    @PostMapping("/sms/template/accounts")
    List<SmsHaveTemplateAccountVo> getHaveTemplateAccountsByUserId();

    @PostMapping("/sms/template/refresh/auditStatus")
    void refreshAuditStatus(@RequestBody SmsTemplateCommonVo smsTemplateCommonVo);

    @PostMapping("/sms/template/testSending")
    Boolean testSending(@RequestBody @Valid SmsTemplateTestSendVo smsTemplateTestSendVo);

    @PostMapping("/sms/template/sending")
    List<SmsTemplateVariable> sending(@RequestBody @Valid SmsTemplateSendVo smsTemplateSendVo);

    @PostMapping("/sms/template/effective")
    List<SmsTemplateSimpleVo> findEffectiveTemplate(@RequestBody @Valid SmsTemplateEffectiveVo smsTemplateEffectiveVo);

    @PostMapping("/sms/template/effective/test")
    void test(@RequestParam("message") String message);

    @GetMapping("/sms/template/getPlatformTemplateIdById")
    String getPlatformTemplateIdById(@RequestParam("id") Long id);

    @PostMapping("/sms/template/existShortUrl")
    List<Long> existShortUrl(@RequestBody List<String> shortUrls);
}
