package com.citc.nce.auth.onlineservice;

import com.citc.nce.auth.onlineservice.service.OnlineServiceConfigService;
import com.citc.nce.auth.onlineservice.vo.req.OnlineServiceConfigReq;
import com.citc.nce.auth.onlineservice.vo.resp.OnlineServiceConfigResp;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;

/**
 * @BelongsPackage: com.citc.nce.auth.onlineservice
 * @Author: litao
 * @CreateTime: 2023-01-04  15:39
 
 * @Version: 1.0
 */
@RestController()
@Slf4j
public class OnlineServiceConfigController implements OnlineServiceConfigApi{
    @Resource
    private OnlineServiceConfigService onlineServiceConfigService;

    @Override
    public OnlineServiceConfigResp findConfigByUserId(@RequestBody @Valid OnlineServiceConfigReq req) {
        return onlineServiceConfigService.findConfigByUserId(req);
    }

    @Override
    public void updateConfigById(@RequestBody @Valid OnlineServiceConfigReq req) {
        onlineServiceConfigService.updateConfigById(req);
    }
}
