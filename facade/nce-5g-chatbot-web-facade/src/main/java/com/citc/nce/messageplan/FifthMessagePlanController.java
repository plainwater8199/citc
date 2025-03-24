package com.citc.nce.messageplan;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.citc.nce.auth.messageplan.FifthMessagePlanApi;
import com.citc.nce.auth.messageplan.vo.FifthMessagePlanAddVo;
import com.citc.nce.auth.messageplan.vo.FifthMessagePlanListVo;
import com.citc.nce.auth.messageplan.vo.MessagePlanSelectReq;
import com.citc.nce.auth.messageplan.vo.FifthMessagePlanVo;
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
@Api(tags = "5g消息套餐")
public class FifthMessagePlanController {
    private final FifthMessagePlanApi fifthMessagePlanApi;

    @PostMapping("/message/5g/plan/add")
    @ApiOperation("新增套餐")
    public void addPlan(@RequestBody @Valid FifthMessagePlanAddVo addVo) {
        fifthMessagePlanApi.addPlan(addVo);
    }

    @ApiOperation("分页查询套餐")
    @PostMapping("/message/5g/plan/search")
    public PageResult<FifthMessagePlanListVo> selectPlan(@RequestBody @Valid MessagePlanSelectReq selectReq) {
        Page<FifthMessagePlanListVo> page = fifthMessagePlanApi.selectPlan(selectReq);
        return new PageResult<>(page.getRecords(), page.getTotal());
    }

    @ApiOperation("修改套餐状态")
    @PostMapping("/message/5g/plan/updateState")
    public void updateState(@RequestParam("id") Long id, @RequestParam("status") Integer status) {
        fifthMessagePlanApi.updateState(id, status);
    }

    @ApiOperation("删除套餐")
    @PostMapping("/message/5g/plan/delete")
    public void deletePlan(@RequestParam("id") Long id) {
        fifthMessagePlanApi.deletePlan(id);
    }

    @ApiOperation("套餐详情")
    @GetMapping("/message/5g/plan")
    public FifthMessagePlanVo getPlanDetail(@RequestParam("id") Long id) {
        return fifthMessagePlanApi.getPlanDetail(id);
    }

}
