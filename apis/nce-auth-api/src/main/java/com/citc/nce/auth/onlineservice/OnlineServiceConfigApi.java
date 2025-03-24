package com.citc.nce.auth.onlineservice;

import com.citc.nce.auth.onlineservice.vo.req.OnlineServiceConfigReq;
import com.citc.nce.auth.onlineservice.vo.resp.OnlineServiceConfigResp;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;

/**
 * @BelongsPackage: com.citc.nce.auth.onlineservice
 * @Author: litao
 * @CreateTime: 2023-01-04  14:54
 
 * @Version: 1.0
 */
@Api(tags = "在线客服模块")
@FeignClient(value = "auth-service", contextId = "OnlineServiceConfigApi", url = "${auth:}")
public interface OnlineServiceConfigApi {
    @ApiOperation("根据用户id查询在线客服配置信息")
    @PostMapping("/onlineService/findConfigByUserId")
    OnlineServiceConfigResp findConfigByUserId(@RequestBody @Valid OnlineServiceConfigReq req);

    @ApiOperation("根据id修改在线客服配置信息")
    @PostMapping("/onlineService/updateConfigById")
    void updateConfigById(@RequestBody @Valid OnlineServiceConfigReq req);
}
