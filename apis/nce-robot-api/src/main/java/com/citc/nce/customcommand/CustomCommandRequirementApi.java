package com.citc.nce.customcommand;

import com.citc.nce.common.core.pojo.PageParam;
import com.citc.nce.common.core.pojo.PageResult;
import com.citc.nce.customcommand.vo.*;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @author jiancheng
 */
@FeignClient(value = "rebot-service", contextId = "CustomCommandRequirementApi", url = "${robot:}")
public interface CustomCommandRequirementApi {
    @PostMapping("/customCommand/requirement/add")
    void add(@RequestBody CustomCommandRequirementAddReq addReq);

    @PostMapping("/customCommand/requirement/wait/count")
    Long countWaitRequirement();

    @PostMapping("/customCommand/requirement/search")
    PageResult<CustomCommandRequirementSimpleVo> searchCustomCommandRequirement(@RequestBody CustomCommandRequirementSearchReq searchReq);

    @GetMapping("/customCommand/requirement/{requirementId}")
    CustomCommandRequirementDetailVo getRequirementDetail(@PathVariable("requirementId") Long requirementId);

    @PostMapping("/customCommand/requirement/updateNote")
    void updateRequirementNote(@RequestBody CustomCommandRequirementUpdateNoteReq updateNoteReq);

    @PostMapping("/customCommand/requirement/process")
    void processRequirement(@RequestBody CustomCommandRequirementProcessReq processReq);

    @PostMapping("/customCommand/requirement/mine")
    PageResult<CustomCommandRequirementSimpleVo> getMyRequirements(@RequestBody PageParam pageParam);

}
