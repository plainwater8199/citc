package com.citc.nce.module;

import com.citc.nce.common.core.pojo.PageResult;
import com.citc.nce.module.vo.SubscribeContentInfo;
import com.citc.nce.module.vo.req.SubscribeContentDeleteReq;
import com.citc.nce.module.vo.req.SubscribeContentQueryListReq;
import com.citc.nce.module.vo.req.SubscribeContentSaveReq;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;

@FeignClient(value = "rebot-service", contextId = "SubscribeContentApi", url = "${robot:}")
public interface SubscribeContentApi {

    @PostMapping("/csp/subscribeContent/save")
    String saveSubscribeContent(@RequestBody @Valid SubscribeContentSaveReq req);

    @PostMapping("/csp/subscribeContent/delete")
    int deleteSubscribeContent(@RequestBody @Valid SubscribeContentDeleteReq req);

    @PostMapping("/csp/subscribeContent/update")
    int updateSubscribeContent(@RequestBody SubscribeContentSaveReq req);

    @PostMapping("/csp/subscribeContent/getSubscribeContentList")
    PageResult<SubscribeContentInfo> getSubscribeContentList(@RequestBody @Valid SubscribeContentQueryListReq req);

}
