package com.citc.nce.auth.messageplan;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.citc.nce.auth.messageplan.vo.MessagePlanSelectReq;
import com.citc.nce.auth.messageplan.vo.VideoSmsPlanAddVo;
import com.citc.nce.auth.messageplan.vo.VideoSmsPlanListVo;
import com.citc.nce.auth.messageplan.vo.VideoSmsPlanVo;
import io.swagger.annotations.Api;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author jiancheng
 */
@Api(tags = "5g消息套餐api")
@FeignClient(value = "auth-service", contextId = "VideoSmsPlanApi", url = "${auth:}")
public interface VideoSmsPlanApi {

    @PostMapping("/message/videoSms/plan/add")
    void addPlan(@RequestBody VideoSmsPlanAddVo addVo);

    @PostMapping("/message/videoSms/plan/search")
    Page<VideoSmsPlanListVo> selectPlan(@RequestBody MessagePlanSelectReq selectReq);

    @PostMapping("/message/videoSms/plan/updateState")
    void updateState(@RequestParam("id") Long id, @RequestParam("status") Integer status);

    @PostMapping("/message/videoSms/plan/delete")
    void deletePlan(@RequestParam("id") Long id);

    @GetMapping("/message/videoSms/plan")
    VideoSmsPlanVo getPlanDetail(@RequestParam("id") Long id);
}
