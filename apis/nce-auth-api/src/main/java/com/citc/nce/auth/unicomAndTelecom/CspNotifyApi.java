package com.citc.nce.auth.unicomAndTelecom;

import com.citc.nce.auth.mobile.req.ChatBotSyncInfo;
import com.citc.nce.auth.unicomAndTelecom.req.ChatBotStatus;
import com.citc.nce.auth.unicomAndTelecom.req.CspAuditReq;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@FeignClient(value = "auth-service", contextId = "CspNotifyApi", url = "${auth:}")
public interface CspNotifyApi {

    /**
     * 平台修改信息
     * @param chatBotSyncInfo 信息实体类
     */
    @PostMapping("/unicomAndTelecom/editOrAddCustomer")
    void editOrAddCustomer(@RequestBody ChatBotSyncInfo chatBotSyncInfo);

    /**
     * csp状态审核
     * @param cspAuditReq 信息实体类
     */
    @PostMapping("/unicomAndTelecom/cspAudit")
    void cspAudit(@RequestBody CspAuditReq cspAuditReq);

    /**
     * chatBot状态审核
     * @param chatBotStatus 信息实体类
     */
    @PostMapping("/unicomAndTelecom/isOnlineUpdate")
    void isOnlineUpdate(@RequestBody ChatBotStatus chatBotStatus);


}
