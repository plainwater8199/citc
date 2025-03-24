package com.citc.nce.csp;

import com.citc.nce.auth.csp.mediasms.template.MediaSmsTemplateApi;
import com.citc.nce.auth.csp.mediasms.template.vo.*;
import com.citc.nce.common.core.exception.BizException;
import com.citc.nce.common.core.pojo.PageResult;
import com.citc.nce.robot.api.RichMediaNotifyApi;
import com.citc.nce.robot.req.RichMediaResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

/**
 * @author jiancheng
 */
@RestController
@Slf4j
@RequiredArgsConstructor
@Validated
@Api(tags = "富媒体短信模板")
@RequestMapping("/mediaSms/template")
public class MediaSmsTemplateController {
    private final MediaSmsTemplateApi templateApi;
    private final RichMediaNotifyApi mediaNotifyApi;

    @PostMapping
    @ApiOperation("新增模板")
    public void addTemplate(@RequestBody @Valid MediaSmsTemplateAddVo addVo) {
        templateApi.addTemplate(addVo);
    }

    @PostMapping("update")
    @ApiOperation("修改模板")
    public void updateTemplate(@RequestBody @Valid MediaSmsTemplateUpdateVo updateVo) {
        templateApi.updateTemplate(updateVo);
    }

    @PostMapping("search")
    @ApiOperation("搜索模板")
    public PageResult<MediaSmsTemplateSimpleVo> searchTemplate(@RequestBody @Valid MediaSmsTemplateSearchVo searchVo) {
        return templateApi.searchTemplate(searchVo);
    }

    @PostMapping("contents/{templateId}")
    @ApiOperation("获取模板内容")
    public MediaSmsTemplatePreviewVo getContents(@PathVariable Long templateId) {
        return templateApi.getContents(templateId,false);
    }

    @PostMapping("{templateId}")
    @ApiOperation("获取模板详情")
    public MediaSmsTemplateDetailVo getTemplateInfo(@PathVariable Long templateId) {
        return templateApi.getTemplateInfo(templateId);
    }

    @PostMapping("delete")
    @ApiOperation("删除模板")
    public void deleteTemplate(@RequestBody @Valid MediaSmsTemplateCommonVo mediaSmsTemplateCommonVo) {
        templateApi.deleteTemplate(mediaSmsTemplateCommonVo);
    }

    @PostMapping("report/{templateId}")
    @ApiOperation("送审模板")
    public void reportTemplate(@PathVariable("templateId") Long templateId) {
        templateApi.reportTemplate(templateId);
    }

    @PostMapping("effective")
    @ApiOperation("查询可用模板")
    public List<MediaSmsTemplateSimpleVo> findEffectiveTemplate(
            @RequestParam("accountIds") List<String> accountIds,
            @RequestParam(value = "accountId", required = false) String templateName,
            @RequestParam(value = "templateType", required = false) Integer templateType
    ) {
        return templateApi.findEffectiveTemplate(accountIds, templateName,templateType);
    }

    @PostMapping("notify")
    @ApiOperation("模板状态回调")
    public void notify(@RequestBody RichMediaResult richMediaResult, HttpServletResponse response) {
        String result = mediaNotifyApi.richMediaTemplateNotify(richMediaResult);
        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=utf-8");
        try (PrintWriter writer = response.getWriter()) {
            IOUtils.write(result, writer);
        } catch (IOException e) {
            throw new BizException(500, "内部错误");
        }
    }

    @GetMapping("accounts")
    @ApiOperation("返回用户有模板的所有账号")
    public List<MediaSmsHaveTemplateAccountVo> accounts() {
        return templateApi.getHaveTemplateAccountsByUserId();
    }

    @PostMapping("/delete/check")
    @ApiOperation("模版删除检查,校验通过：0 有执行计划正在使用：1")
    public List<MediaSmsTemplateCheckVo> deleteCheck(@RequestBody @Valid MediaSmsTemplateCommonVo mediaSmsTemplateCommonVo) {
        return templateApi.templateDeleteCheck(mediaSmsTemplateCommonVo);
    }
}
