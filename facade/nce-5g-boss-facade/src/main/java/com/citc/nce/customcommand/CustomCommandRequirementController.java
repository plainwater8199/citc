package com.citc.nce.customcommand;

import com.citc.nce.annotation.BossAuth;
import com.citc.nce.common.core.pojo.PageParam;
import com.citc.nce.common.core.pojo.PageResult;
import com.citc.nce.customcommand.vo.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * @author jiancheng
 */
@RestController
@Api(value = "customCommandController", tags = "自定义指令需求管理")
@RequiredArgsConstructor
@RequestMapping("customCommand/requirement")
public class CustomCommandRequirementController {
    private final CustomCommandRequirementApi customCommandRequirementApi;

    @PostMapping("add")
    @ApiOperation("新增需求")
    public void add(@RequestBody @Valid CustomCommandRequirementAddReq addReq) {
        customCommandRequirementApi.add(addReq);
    }

    @BossAuth("/chatbot-view/index")
    @PostMapping("wait/count")
    @ApiOperation("查询待处理需求数")
    public Long countWaitRequirement() {
        return customCommandRequirementApi.countWaitRequirement();
    }

    @BossAuth("/chatbot-view/directive/demand")
    @PostMapping("search")
    @ApiOperation("搜索需求")
    public PageResult<CustomCommandRequirementSimpleVo> search(@RequestBody @Valid CustomCommandRequirementSearchReq searchReq) {
        return customCommandRequirementApi.searchCustomCommandRequirement(searchReq);
    }

    @BossAuth("/chatbot-view/directive/demand")
    @GetMapping("{requirementId}")
    @ApiOperation("查询需求详情")
    public CustomCommandRequirementDetailVo getRequirementDetail(@PathVariable Long requirementId) {
        return customCommandRequirementApi.getRequirementDetail(requirementId);
    }

    @PostMapping("updateNote")
    @ApiOperation("保存沟通记录")
    public void updateRequirementNote(@RequestBody @Valid CustomCommandRequirementUpdateNoteReq updateNoteReq) {
        customCommandRequirementApi.updateRequirementNote(updateNoteReq);
    }

    @PostMapping("process")
    @ApiOperation("处理需求")
    public void processRequirement(@RequestBody @Valid CustomCommandRequirementProcessReq processReq) {
        customCommandRequirementApi.processRequirement(processReq);
    }

    @PostMapping("mine")
    @ApiOperation("查询我的需求列表")
    public PageResult<CustomCommandRequirementSimpleVo> getMyRequirements(@RequestBody @Valid PageParam pageParam) {
        return customCommandRequirementApi.getMyRequirements(pageParam);
    }

}
