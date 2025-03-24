package com.citc.nce.messageplan;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.citc.nce.auth.messageplan.SmsPlanApi;
import com.citc.nce.auth.messageplan.vo.*;
import com.citc.nce.common.core.pojo.PageResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * @author jiancheng
 */
@RestController
@Slf4j
@RequiredArgsConstructor
@Validated
@Api(tags = "短信消息套餐")
public class SmsPlanController {
    private final SmsPlanApi smsPlanApi;

    @PostMapping("/message/sms/plan/add")
    @ApiOperation("新增套餐")
    public void addPlan(@RequestBody @Valid SmsPlanAddVo addVo) {
        smsPlanApi.addPlan(addVo);
    }

    @ApiOperation("分页查询套餐")
    @PostMapping("/message/sms/plan/search")
    public PageResult<SmsPlanListVo> selectPlan(@RequestBody @Valid MessagePlanSelectReq selectReq) {
        Page<SmsPlanListVo> page = smsPlanApi.selectPlan(selectReq);
        return new PageResult<>(page.getRecords(),page.getTotal());
    }

    @ApiOperation("修改套餐状态")
    @PostMapping("/message/sms/plan/updateState")
    public void updateState(@RequestParam("id") Long id, @RequestParam("status") Integer status) {
        smsPlanApi.updateState(id, status);
    }

    @ApiOperation("删除套餐")
    @PostMapping("/message/sms/plan/delete")
    public void deletePlan(@RequestParam("id") Long id) {
        smsPlanApi.deletePlan(id);
    }

    @ApiOperation("套餐详情")
    @GetMapping("/message/sms/plan")
    public SmsPlanVo getPlanDetail(@RequestParam("id") Long id) {
        return smsPlanApi.getPlanDetail(id);
    }

}
