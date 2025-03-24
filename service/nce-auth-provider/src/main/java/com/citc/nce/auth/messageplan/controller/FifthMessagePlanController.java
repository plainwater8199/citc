package com.citc.nce.auth.messageplan.controller;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.citc.nce.auth.messageplan.FifthMessagePlanApi;
import com.citc.nce.auth.messageplan.service.IFifthMessagePlanService;
import com.citc.nce.auth.messageplan.vo.FifthMessagePlanAddVo;
import com.citc.nce.auth.messageplan.vo.FifthMessagePlanListVo;
import com.citc.nce.auth.messageplan.vo.MessagePlanSelectReq;
import com.citc.nce.auth.messageplan.vo.FifthMessagePlanVo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 5G消息套餐表 前端控制器
 * </p>
 *
 * @author jcrenc
 * @since 2024-01-23 03:01:41
 */
@RestController
@Slf4j
@RequiredArgsConstructor
public class FifthMessagePlanController implements FifthMessagePlanApi {
    private final IFifthMessagePlanService messagePlanService;

    @Override
    public void addPlan(FifthMessagePlanAddVo addVo) {
        messagePlanService.addPlan(addVo);
    }

    @Override
    public Page<FifthMessagePlanListVo> selectPlan(MessagePlanSelectReq selectReq) {
        return messagePlanService.selectPlan(selectReq);
    }

    @Override
    public void updateState(Long id, Integer status) {
        messagePlanService.updateState(id,status);
    }

    @Override
    public void deletePlan(Long id) {
        messagePlanService.deletePlan(id);
    }

    @Override
    public FifthMessagePlanVo getPlanDetail(Long id) {
        return messagePlanService.getPlanDetail(id);
    }
}

