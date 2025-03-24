package com.citc.nce.module;

import com.citc.nce.module.vo.SubscribeModuleInfo;
import com.citc.nce.module.vo.req.SubscribeModuleReq;
import com.citc.nce.common.core.pojo.PageResult;
import com.citc.nce.module.vo.req.SubscribeModuleSaveReq;
import com.citc.nce.module.vo.resp.SubscribeModuleQueryResp;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;
import java.util.List;

@FeignClient(value = "rebot-service", contextId = "SubscribeModuleApi", url = "${robot:}")
public interface SubscribeModuleApi {

    /**
     * 保存订阅组件
     * @param req 请求信息
     * @return 组件信息
     */
    @PostMapping("/csp/subscribe/save")
    int saveSubscribeModule(@RequestBody @Valid SubscribeModuleSaveReq req);

    /**
     * 删除订阅组件
     * @param req 请求信息
     * @return 组件信息
     */
    @PostMapping("/csp/subscribe/delete")
    int deleteSubscribeModule(@RequestBody @Valid SubscribeModuleReq req);

    @PostMapping("/csp/subscribe/update")
    int updateSubscribeModule(@RequestBody @Valid SubscribeModuleSaveReq req);

    @PostMapping("/csp/subscribe/getSubscribeModuleList")
    PageResult<SubscribeModuleInfo> getSubscribeModuleList(@RequestBody SubscribeModuleReq req);

    /**
     * 查询订阅组件详情
     * @param req 请求信息
     * @return 组件信息
     */
    @PostMapping("/csp/subscribe/getSubscribeModule")
    SubscribeModuleQueryResp getSubscribeModule(@RequestBody @Valid SubscribeModuleReq req);

    @PostMapping("/csp/subscribe/getSubscribeModules")
    List<SubscribeModuleReq> getSubscribeModules();

}
