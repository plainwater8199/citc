package com.citc.nce.auth.messageplan;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.citc.nce.auth.messageplan.vo.*;
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
@FeignClient(value = "auth-service", contextId = "SmsPlanApi", url = "${auth:}")
public interface SmsPlanApi {

    @PostMapping("/message/sms/plan/add")
    void addPlan(@RequestBody SmsPlanAddVo addVo);

    @PostMapping("/message/sms/plan/search")
    Page<SmsPlanListVo> selectPlan(@RequestBody MessagePlanSelectReq selectReq);

    @PostMapping("/message/sms/plan/updateState")
    void updateState(@RequestParam("id") Long id, @RequestParam("status") Integer status);

    @PostMapping("/message/sms/plan/delete")
    void deletePlan(@RequestParam("id") Long id);

    @GetMapping("/message/sms/plan")
    SmsPlanVo getPlanDetail(@RequestParam("id") Long id);
}
