package com.citc.nce.auth.messageplan.controller;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.citc.nce.auth.messageplan.VideoSmsPlanApi;
import com.citc.nce.auth.messageplan.service.IVideoSmsPlanService;
import com.citc.nce.auth.messageplan.vo.MessagePlanSelectReq;
import com.citc.nce.auth.messageplan.vo.VideoSmsPlanAddVo;
import com.citc.nce.auth.messageplan.vo.VideoSmsPlanListVo;
import com.citc.nce.auth.messageplan.vo.VideoSmsPlanVo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 视频短信套餐表 前端控制器
 * </p>
 *
 * @author jcrenc
 * @since 2024-01-23 03:01:42
 */
@RestController
@RequiredArgsConstructor
@Slf4j
public class VideoSmsPlanController implements VideoSmsPlanApi {
    private final IVideoSmsPlanService videoSmsPlanService;

    @Override
    public void addPlan(VideoSmsPlanAddVo addVo) {
        videoSmsPlanService.addPlan(addVo);
    }

    @Override
    public Page<VideoSmsPlanListVo> selectPlan(MessagePlanSelectReq selectReq) {
        return videoSmsPlanService.selectPlan(selectReq);
    }

    @Override
    public void updateState(Long id, Integer status) {
        videoSmsPlanService.updateState(id, status);
    }

    @Override
    public void deletePlan(Long id) {
        videoSmsPlanService.deletePlan(id);
    }

    @Override
    public VideoSmsPlanVo getPlanDetail(Long id) {
        return videoSmsPlanService.getPlanDetail(id);
    }
}

