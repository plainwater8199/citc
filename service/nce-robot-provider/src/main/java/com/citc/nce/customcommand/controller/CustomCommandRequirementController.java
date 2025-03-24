package com.citc.nce.customcommand.controller;


import com.citc.nce.common.core.pojo.PageParam;
import com.citc.nce.common.core.pojo.PageResult;
import com.citc.nce.customcommand.CustomCommandRequirementApi;
import com.citc.nce.customcommand.service.ICustomCommandRequirementService;
import com.citc.nce.customcommand.vo.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 定制需求管理(自定义指令) 前端控制器
 * </p>
 *
 * @author jcrenc
 * @since 2023-11-09 02:53:48
 */
@RestController
@RequiredArgsConstructor
@Slf4j
public class CustomCommandRequirementController implements CustomCommandRequirementApi {
    private final ICustomCommandRequirementService customCommandRequirementService;

    @PostMapping("/customCommand/requirement/add")
    @Override
    public void add(CustomCommandRequirementAddReq addReq) {
        customCommandRequirementService.add(addReq);
    }

    @PostMapping("/customCommand/requirement/wait/count")
    @Override
    public Long countWaitRequirement() {
        return customCommandRequirementService.countWaitRequirement();
    }

    @PostMapping("/customCommand/requirement/search")
    @Override
    public PageResult<CustomCommandRequirementSimpleVo> searchCustomCommandRequirement(@RequestBody CustomCommandRequirementSearchReq searchReq) {
        return customCommandRequirementService.searchCustomCommandRequirement(searchReq);
    }

    @GetMapping("/customCommand/requirement/{requirementId}")
    @Override
    public CustomCommandRequirementDetailVo getRequirementDetail(@PathVariable Long requirementId) {
        return customCommandRequirementService.getRequirementDetail(requirementId);
    }

    @PostMapping("/customCommand/requirement/updateNote")
    @Override
    public void updateRequirementNote(@RequestBody CustomCommandRequirementUpdateNoteReq updateNoteReq) {
        customCommandRequirementService.updateRequirementNote(updateNoteReq.getId(), updateNoteReq.getNote());
    }

    @PostMapping("/customCommand/requirement/process")
    @Override
    public void processRequirement(@RequestBody CustomCommandRequirementProcessReq processReq) {
        customCommandRequirementService.processRequirement(processReq);
    }

    @PostMapping("/customCommand/requirement/mine")
    @Override
    public PageResult<CustomCommandRequirementSimpleVo> getMyRequirements(PageParam pageParam) {
        return customCommandRequirementService.getMyRequirements(pageParam);
    }
}

