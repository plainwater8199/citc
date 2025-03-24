package com.citc.nce.auth.messageplan;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.citc.nce.auth.messageplan.vo.FifthMessagePlanAddVo;
import com.citc.nce.auth.messageplan.vo.FifthMessagePlanListVo;
import com.citc.nce.auth.messageplan.vo.MessagePlanSelectReq;
import com.citc.nce.auth.messageplan.vo.FifthMessagePlanVo;
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
@FeignClient(value = "auth-service", contextId = "FifthMessagePlanApi", url = "${auth:}")
public interface FifthMessagePlanApi {

    @PostMapping("/message/5g/plan/add")
    void addPlan(@RequestBody FifthMessagePlanAddVo addVo);

    @PostMapping("/message/5g/plan/search")
    Page<FifthMessagePlanListVo> selectPlan(@RequestBody MessagePlanSelectReq selectReq);

    @PostMapping("/message/5g/plan/updateState")
    void updateState(@RequestParam("id") Long id, @RequestParam("status") Integer status);

    @PostMapping("/message/5g/plan/delete")
    void deletePlan(@RequestParam("id") Long id);

    @GetMapping("/message/5g/plan")
    FifthMessagePlanVo getPlanDetail(@RequestParam("id") Long id);


}
