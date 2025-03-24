package com.citc.nce.module;

import com.citc.nce.module.vo.req.GetSubscribeInfoByUserReq;
import com.citc.nce.module.vo.req.SubscribeNamesReq;
import com.citc.nce.common.core.pojo.PageResult;
import com.citc.nce.module.vo.resp.GetSubscribeInfoByUserResp;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;

@FeignClient(value = "rebot-service", contextId = "SubscribeNamesApi", url = "${robot:}")
public interface SubscribeNamesApi {

    @PostMapping("/csp/subscribeNames/save")
    int saveSubscribeNames(@RequestBody @Valid SubscribeNamesReq req);

    @PostMapping("/csp/subscribeNames/cancel")
    int cancelSubscribeNames(@RequestBody SubscribeNamesReq req);

    @PostMapping("/csp/subscribeNames/update")
    int updateSubscribeNames(@RequestBody SubscribeNamesReq req);

    @PostMapping("/csp/subscribeNames/getSubscribeNamesList")
    PageResult<SubscribeNamesReq> getSubscribeNamesList(@RequestBody SubscribeNamesReq req);

    @PostMapping("/csp/subscribeNames/getSubscribeInfoByPhone")
    GetSubscribeInfoByUserResp getSubscribeInfoByPhone(@RequestBody @Valid GetSubscribeInfoByUserReq req);

}
