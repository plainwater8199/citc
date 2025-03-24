package com.citc.nce.adminPlatform;

import com.citc.nce.annotation.BossAuth;
import com.citc.nce.authcenter.auth.AdminAuthApi;
import com.citc.nce.authcenter.largeModel.LargeModelApi;
import com.citc.nce.authcenter.largeModel.vo.*;
import com.citc.nce.common.core.pojo.PageResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;

/**
 * @author ljh
 * @since 2024/4/29
 */

@BossAuth("/chatbot-view/function/largeModel")
@Api(tags = "大模型管理")
@RestController
@RequestMapping("/admin/model")
public class largeModelController {

    @Resource
    LargeModelApi largeModelApi;

    @Resource
    AdminAuthApi adminAuthApi;

    /**
     * 获取大模型列表
     */
    @ApiOperation("管理平台--获取大模型列表")
    @PostMapping("/largeModel/list")
    public PageResult<LargeModelResp> getLargeModelList(@RequestBody @Valid LargeModelReq req) {
        return largeModelApi.getLargeModelList(req);
    }

    /**
     * 获取大模型详情
     */
    @ApiOperation("管理平台--获取大模型详情")
    @GetMapping("/largeModel/detail/{id}")
    public LargeModelResp getLargeModeDetail(@PathVariable("id") Long id) {
        return largeModelApi.getLargeModeDetail(id);
    }

    /**
     * 创建大模型
     */
    @ApiOperation("管理平台--创建大模型")
    @PostMapping("/largeModel/create")
    public void createLargeModel(@RequestBody @Valid LargeModelCreateReq req) {
        largeModelApi.createLargeModel(req);
    }

    /**
     * 更新大模型
     */
    @ApiOperation("管理平台--更新大模型")
    @PostMapping("/largeModel/update")
    public void updateLargeModel(@RequestBody @Valid LargeModelUpdateReq req) {
        largeModelApi.updateLargeModel(req);
    }

    /**
     * 删除大模型
     */
    @ApiOperation("管理平台--删除大模型")
    @PostMapping("/largeModel/delete")
    public void deleteLargeModel(@RequestBody @Valid LargeModelDetailReq req) {
        largeModelApi.deleteLargeModel(req);
    }

    /**
     * 获取Prompt列表
     */
    @ApiOperation("管理平台--获取Prompt列表")
    @PostMapping("/prompt/list")
    public PageResult<PromptResp> getPromptList(@RequestBody @Valid PromptReq req) {
        return largeModelApi.getPromptList(req);
    }

    /**
     * 获取Prompt详情
     */
    @ApiOperation("管理平台--获取Prompt详情")
    @GetMapping("/prompt/detail/{id}")
    public PromptDetailResp getPromptDetail(@PathVariable("id") Long id) {
        return largeModelApi.getPromptDetail(id);
    }

    /**
     * 编辑Prompt
     */
    @ApiOperation("管理平台--编辑Prompt")
    @PostMapping("/prompt/update")
    public void updatePrompt(@RequestBody @Valid PromptUpdateReq req) {
        largeModelApi.updatePrompt(req);
    }

    /**
     * 启用/禁用Prompt
     */
    @ApiOperation("管理平台--启用/禁用Prompt")
    @PostMapping("/prompt/updateStatus")
    public void updatePromptStatus(@RequestBody @Valid PromptUpdateStatusReq req) {
        largeModelApi.updatePromptStatus(req);
    }
}
