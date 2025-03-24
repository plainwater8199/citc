package com.citc.nce.csp;

import com.citc.nce.auth.mobile.MobileNotifyApi;
import com.citc.nce.auth.mobile.req.*;
import com.citc.nce.common.facadeserver.annotations.SkipToken;
import com.citc.nce.common.facadeserver.annotations.UnWrapResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Map;

/**
 * 移动对接接口  移动运营商返回信息，同步状态
 */
@RestController
@Slf4j
@UnWrapResponse
public class MobileNotifyController {

    @Resource
    private MobileNotifyApi mobileNotifyApi;

    @SkipToken
    @PostMapping({"/oc/{apiVersion}/syncchatbot"})
    public Map<String, String> syncChatBot(@RequestBody ChatBotSyncInfo var1) {
        log.info("接收移动同步chatbot信息：{}", var1);
        return mobileNotifyApi.syncChatBot(var1);
    }

    @SkipToken
    @PostMapping({"/oc/{apiVersion}/syncproduct"})
    public Map<String, String> syncProduct(@RequestBody ServiceCode var1) {
        log.info("接收移动同步订购信息：{}", var1);
        return mobileNotifyApi.syncProduct(var1);
    }

    @SkipToken
    @PostMapping({"/oc/{apiVersion}/cancel"})
    public Map<String, String> chatBotCancel(@RequestBody ChatBotInfo var1) {
        log.info("接收移动同步chatbot注销：{}", var1);
        return mobileNotifyApi.chatBotCancel(var1);
    }

    @SkipToken
    @PostMapping({"/oc/{apiVersion}/status"})
    public Map<String, String> chatBotStatus(@RequestBody ChatBotInfo var1) {
        log.info("接收移动同步chatbot状态：{}", var1);
        return mobileNotifyApi.chatBotStatus(var1);
    }

    @SkipToken
    @PostMapping({"/oc/{apiVersion}/authnotification"})
    public Map<String, String> authNotification(@RequestBody AuthInfo var1) {
        log.info("接收移动同步固定菜单审核结果：{}", var1);
        return mobileNotifyApi.authNotification(var1);
    }

    @SkipToken
    @PostMapping({"/oc/{apiVersion}/audit"})
    public Map<String, String> chatBotAudit(@RequestBody ChatBotExamine var1) {
        log.info("接收移动chatbot审核回调信息：{}", var1);
        return mobileNotifyApi.chatBotAudit(var1);
    }

    @SkipToken
    @PostMapping({"/oc/{apiVersion}/syncconfigchatbot"})
    public Map<String, String> syncConfigChatBot(@RequestBody ChatBotConfigInfo var1) {
        log.info("接收移动chatbot变更配置信息：{}", var1);
        return mobileNotifyApi.syncConfigChatBot(var1);
    }

    @SkipToken
    @PostMapping({"/oc/{apiVersion}/client/new"})
    public Map<String, String> clientNew(@RequestBody SignedCustomer var1) {
        log.info("接收移动回调信息：{}", var1);
        return mobileNotifyApi.clientNew(var1);
    }

    @SkipToken
    @PostMapping({"/oc/{apiVersion}/client/change"})
    public Map<String, String> clientChange(@RequestBody SignedCustomer var1) {
        log.info("接收移动回调信息：{}", var1);
        return mobileNotifyApi.clientChange(var1);
    }

    @SkipToken
    @PostMapping({"/oc/{apiVersion}/client/audit"})
    public Map<String, String> clientAudit(@RequestBody SignedCustomerExamine var1) {
        log.info("接收移动非直签客户回调信息：{}", var1);
        return mobileNotifyApi.clientAudit(var1);
    }

    @SkipToken
    @PostMapping({"/oc/{apiVersion}/client/status"})
    public Map<String, String> clientStatus(@RequestBody CustomerStatus var1) {
        log.info("接收移动回调信息：{}", var1);
        return mobileNotifyApi.clientStatus(var1);
    }

    @SkipToken
    @PostMapping({"/oc/{apiVersion}/client/allotServiceCode"})
    public Map<String, String> allotServiceCode(@RequestBody AgentServiceCode var1) {
        log.info("接收移动回调信息：{}", var1);
        return mobileNotifyApi.allotServiceCode(var1);
    }

}
