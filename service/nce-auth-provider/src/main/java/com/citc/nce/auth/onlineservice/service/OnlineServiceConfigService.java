package com.citc.nce.auth.onlineservice.service;

import com.citc.nce.auth.onlineservice.vo.req.OnlineServiceConfigReq;
import com.citc.nce.auth.onlineservice.vo.resp.OnlineServiceConfigResp;

/**
 * @BelongsPackage: com.citc.nce.auth.onlineservice.service
 * @Author: litao
 * @CreateTime: 2023-01-04  14:48
 
 * @Version: 1.0
 */
public interface OnlineServiceConfigService {
    OnlineServiceConfigResp findConfigByUserId(OnlineServiceConfigReq req);

    void updateConfigById(OnlineServiceConfigReq req);
}
