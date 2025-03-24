package com.citc.nce.csp;

import com.citc.nce.auth.mobile.req.ChatBotSyncInfo;
import com.citc.nce.auth.unicomAndTelecom.CspNotifyApi;
import com.citc.nce.auth.unicomAndTelecom.req.ChatBotStatus;
import com.citc.nce.auth.unicomAndTelecom.req.CspAuditReq;
import com.citc.nce.common.facadeserver.annotations.SkipToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
public class CspNotifyController{

    @Resource
    CspNotifyApi cspNotifyApi;


    public void editOrAddCustomer(ChatBotSyncInfo chatBotSyncInfo) {

    }


    @SkipToken
    @PostMapping("/cspAudit")
    public void cspAudit(@RequestBody CspAuditReq cspAuditReq) {
        cspNotifyApi.cspAudit(cspAuditReq);
    }


    public void isOnlineUpdate(ChatBotStatus chatBotStatus) {

    }
}
