package com.citc.nce.messageplan;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.citc.nce.auth.messageplan.VideoSmsPlanApi;
import com.citc.nce.auth.messageplan.vo.MessagePlanSelectReq;
import com.citc.nce.auth.messageplan.vo.VideoSmsPlanAddVo;
import com.citc.nce.auth.messageplan.vo.VideoSmsPlanListVo;
import com.citc.nce.auth.messageplan.vo.VideoSmsPlanVo;
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
@Api(tags = "视频短信消息套餐")
public class VideoSmsPlanController {
    private final VideoSmsPlanApi videoSmsPlanApi;

    @PostMapping("/message/videoSms/plan/add")
    @ApiOperation("新增套餐")
    public void addPlan(@RequestBody @Valid VideoSmsPlanAddVo addVo) {
        videoSmsPlanApi.addPlan(addVo);
    }

    @ApiOperation("分页查询套餐")
    @PostMapping("/message/videoSms/plan/search")
    public PageResult<VideoSmsPlanListVo> selectPlan(@RequestBody @Valid MessagePlanSelectReq selectReq) {
        Page<VideoSmsPlanListVo> page = videoSmsPlanApi.selectPlan(selectReq);
        return new PageResult<>(page.getRecords(), page.getTotal());
    }

    @ApiOperation("修改套餐状态")
    @PostMapping("/message/videoSms/plan/updateState")
    public void updateState(@RequestParam("id") Long id, @RequestParam("status") Integer status) {
        videoSmsPlanApi.updateState(id, status);
    }

    @ApiOperation("删除套餐")
    @PostMapping("/message/videoSms/plan/delete")
    public void deletePlan(@RequestParam("id") Long id) {
        videoSmsPlanApi.deletePlan(id);
    }

    @ApiOperation("套餐详情")
    @GetMapping("/message/videoSms/plan")
    public VideoSmsPlanVo getPlanDetail(@RequestParam("id") Long id) {
        return videoSmsPlanApi.getPlanDetail(id);
    }

}
