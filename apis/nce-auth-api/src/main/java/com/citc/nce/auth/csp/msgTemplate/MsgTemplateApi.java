package com.citc.nce.auth.csp.msgTemplate;

import com.citc.nce.common.core.enums.MsgTypeEnum;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;


@FeignClient(value = "auth-service", contextId = "MsgTemplateApi", url = "${auth:}")
public interface MsgTemplateApi {

    /**
     * 消息模版详情查询接口
     * @param msgType 消息类型
     * @param templateId 模版ID
     * @return 模版内容
     */
    @PostMapping("/message/templateContent/query")
    String templateContentQuery(@RequestParam("msgType") MsgTypeEnum msgType, @RequestParam("templateId") Long templateId,@RequestParam("customerId") String customerId);


}

