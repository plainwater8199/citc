package com.citc.nce.csp;

import com.citc.nce.auth.csp.smsTemplate.SmsTemplateApi;
import com.citc.nce.auth.csp.smsTemplate.vo.*;
import com.citc.nce.common.core.pojo.PageResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * @author ping chen
 */
@RestController
@Slf4j
@RequiredArgsConstructor
@Validated
@Api(tags = "短信模板")
@RequestMapping("/sms/template")
public class SmsTemplateController {
    private final SmsTemplateApi smsTemplateApi;

    @PostMapping
    @ApiOperation("新增模板")
    public void addTemplate(@RequestBody @Valid SmsTemplateAddVo smsTemplateAddVo) {
        smsTemplateApi.addTemplate(smsTemplateAddVo);
    }

    @PostMapping("update")
    @ApiOperation("修改模板")
    public void updateTemplate(@RequestBody @Valid SmsTemplateUpdateVo smsTemplateUpdateVo) {
        smsTemplateApi.updateTemplate(smsTemplateUpdateVo);
    }

    @PostMapping("search")
    @ApiOperation("搜索模板")
    public PageResult<SmsTemplateSimpleVo> searchTemplate(@RequestBody @Valid SmsTemplateSearchVo smsTemplateSearchVo) {
        return smsTemplateApi.searchTemplate(smsTemplateSearchVo);
    }

    @PostMapping("{templateId}")
    @ApiOperation("获取模板详情")
    public SmsTemplateDetailVo getTemplateInfo(@PathVariable Long templateId) {
        return smsTemplateApi.getTemplateInfo(templateId,false);
    }

    @PostMapping("delete")
    @ApiOperation("删除模板")
    public void deleteTemplate(@RequestBody @Valid SmsTemplateCommonVo smsTemplateCommonVo) {
        smsTemplateApi.deleteTemplate(smsTemplateCommonVo);
    }

    @PostMapping("/delete/check")
    @ApiOperation("模版删除检查")
    public List<SmsTemplateCheckVo> deleteCheck(@RequestBody @Valid SmsTemplateCommonVo smsTemplateCommonVo) {
        return smsTemplateApi.templateDeleteCheck(smsTemplateCommonVo);
    }

    @PostMapping("report/{templateId}")
    @ApiOperation("送审模板")
    public void reportTemplate(@PathVariable("templateId") Long templateId) {
        smsTemplateApi.reportTemplate(templateId);
    }

    @GetMapping("accounts")
    @ApiOperation("返回用户有模板的所有账号")
    public List<SmsHaveTemplateAccountVo> accounts() {
        return smsTemplateApi.getHaveTemplateAccountsByUserId();
    }

    @PostMapping("refresh/auditStatus")
    @ApiOperation("刷新模板审核状态")
    public void refreshAuditStatus(@RequestBody @Valid SmsTemplateCommonVo smsTemplateCommonVo) {
        smsTemplateApi.refreshAuditStatus(smsTemplateCommonVo);
    }

    @PostMapping("testSending")
    @ApiOperation("模板测试发送短信")
    public Boolean testSending(@RequestBody @Valid SmsTemplateTestSendVo smsTemplateTestSendVo) {
        return smsTemplateApi.testSending(smsTemplateTestSendVo);
    }

    @PostMapping("effective")
    @ApiOperation("查询可用模板")
    public List<SmsTemplateSimpleVo> findEffectiveTemplate(@RequestBody @Valid SmsTemplateEffectiveVo smsTemplateEffectiveVo) {
        return smsTemplateApi.findEffectiveTemplate(smsTemplateEffectiveVo);
    }
}
