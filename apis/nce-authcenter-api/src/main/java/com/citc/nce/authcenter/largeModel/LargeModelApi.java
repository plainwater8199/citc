package com.citc.nce.authcenter.largeModel;

import com.citc.nce.authcenter.largeModel.vo.*;
import com.citc.nce.common.core.pojo.PageResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;

@Api(tags = "管理平台--大模型模块")
@FeignClient(value = "authcenter-service", contextId = "large-model", url = "${authCenter:}")
public interface LargeModelApi {
    @ApiOperation("管理平台--获取大模型列表")
    @PostMapping("/admin/model/largeModel/list")
    PageResult<LargeModelResp> getLargeModelList(@RequestBody @Valid LargeModelReq req);

    @ApiOperation("管理平台--获取大模型详情")
    @GetMapping("/admin/model/largeModel/detail/{id}")
    LargeModelResp getLargeModeDetail(@PathVariable("id") Long id);

    @ApiOperation("管理平台--创建大模型")
    @PostMapping("/admin/model/largeModel/create")
    void createLargeModel(@RequestBody @Valid LargeModelCreateReq req);

    @ApiOperation("管理平台--更新大模型")
    @PostMapping("/admin/model/largeModel/update")
    void updateLargeModel(@RequestBody @Valid LargeModelUpdateReq req);

    @ApiOperation("管理平台--删除大模型")
    @PostMapping("/admin/model/largeModel/delete")
    void deleteLargeModel(@RequestBody @Valid LargeModelDetailReq req);

    @ApiOperation("管理平台--获取Prompt列表")
    @PostMapping("/admin/model/prompt/list")
    PageResult<PromptResp> getPromptList(@RequestBody @Valid PromptReq req);

    @ApiOperation("管理平台--获取Prompt详情")
    @GetMapping("/admin/model/prompt/{id}")
    PromptDetailResp getPromptDetail(@PathVariable("id") Long id);

    @ApiOperation("管理平台--编辑Prompt")
    @PostMapping("/admin/model/prompt/update")
    void updatePrompt(@RequestBody @Valid PromptUpdateReq req);

    @ApiOperation("管理平台--启用/禁用Prompt")
    @PostMapping("/admin/model/prompt/updateStatus")
    void updatePromptStatus(@RequestBody @Valid PromptUpdateStatusReq req);

    @ApiOperation("管理平台--根据chatbotAccountId查询大模型设置")
    @PostMapping("/admin/model/largeModel/searchByChatbotAccountId")
    LargeModelPromptSettingResp getLargeModelPromptSettingByChatbotAccountId();

    @ApiOperation("用户端--查询prompt状态")
    @GetMapping("/model/prompt/status")
    PromptStatusResp getPromptStatus();
}
