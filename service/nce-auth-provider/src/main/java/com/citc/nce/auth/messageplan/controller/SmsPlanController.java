package com.citc.nce.auth.messageplan.controller;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.citc.nce.auth.messageplan.SmsPlanApi;
import com.citc.nce.auth.messageplan.service.ISmsPlanService;
import com.citc.nce.auth.messageplan.vo.MessagePlanSelectReq;
import com.citc.nce.auth.messageplan.vo.SmsPlanAddVo;
import com.citc.nce.auth.messageplan.vo.SmsPlanListVo;
import com.citc.nce.auth.messageplan.vo.SmsPlanVo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 短信套餐表 前端控制器
 * </p>
 *
 * @author jcrenc
 * @since 2024-01-23 03:01:42
 */
@RestController
@Slf4j
@RequiredArgsConstructor
public class SmsPlanController implements SmsPlanApi {

    private final ISmsPlanService smsPlanService;

    @Override
    public void addPlan(SmsPlanAddVo addVo) {
        smsPlanService.addPlan(addVo);
    }

    @Override
    public Page<SmsPlanListVo> selectPlan(MessagePlanSelectReq selectReq) {
        return smsPlanService.selectPlan(selectReq);
    }

    @Override
    public void updateState(Long id, Integer status) {
        smsPlanService.updateState(id, status);
    }

    @Override
    public void deletePlan(Long id) {
        smsPlanService.deletePlan(id);
    }

    @Override
    public SmsPlanVo getPlanDetail(Long id) {
        return smsPlanService.getPlanDetail(id);
    }
}

