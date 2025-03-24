package com.citc.nce.auth.unicomAndTelecom.controller;

import com.citc.nce.auth.mobile.req.ChatBotSyncInfo;
import com.citc.nce.auth.unicomAndTelecom.CspNotifyApi;
import com.citc.nce.auth.unicomAndTelecom.req.ChatBotStatus;
import com.citc.nce.auth.unicomAndTelecom.req.CspAuditReq;
import com.citc.nce.auth.unicomAndTelecom.service.CspNotifyService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@Slf4j
public class CspNotifyController implements CspNotifyApi {
    @Resource
    CspNotifyService cspNotifyService;

    @PostMapping("/unicomAndTelecom/editOrAddCustomer")
    @Override
    public void editOrAddCustomer(ChatBotSyncInfo chatBotSyncInfo) {

    }

    @PostMapping("/unicomAndTelecom/cspAudit")
    @Override
    public void cspAudit(CspAuditReq cspAuditReq) {
        log.info("接收到联通/电信审核回调：{}",cspAuditReq);
        cspNotifyService.cspAudit(cspAuditReq);
    }


    @PostMapping("/unicomAndTelecom/isOnlineUpdate")
    @Override
    public void isOnlineUpdate(ChatBotStatus chatBotStatus) {

    }



}
