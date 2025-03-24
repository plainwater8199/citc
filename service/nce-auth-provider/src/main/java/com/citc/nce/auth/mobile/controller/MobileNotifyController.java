package com.citc.nce.auth.mobile.controller;


import com.citc.nce.auth.csp.chatbot.service.ChatbotManageService;
import com.citc.nce.auth.messagetemplate.MessageTemplateApi;
import com.citc.nce.auth.mobile.MobileNotifyApi;
import com.citc.nce.auth.mobile.req.AgentServiceCode;
import com.citc.nce.auth.mobile.req.AuthInfo;
import com.citc.nce.auth.mobile.req.ChatBotConfigInfo;
import com.citc.nce.auth.mobile.req.ChatBotExamine;
import com.citc.nce.auth.mobile.req.ChatBotInfo;
import com.citc.nce.auth.mobile.req.ChatBotSyncInfo;
import com.citc.nce.auth.mobile.req.CustomerStatus;
import com.citc.nce.auth.mobile.req.ServiceCode;
import com.citc.nce.auth.mobile.req.SignedCustomer;
import com.citc.nce.auth.mobile.req.SignedCustomerExamine;
import com.citc.nce.auth.mobile.service.ChatBotService;
import com.citc.nce.common.core.exception.BizException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

@RestController
@Slf4j
public class MobileNotifyController implements MobileNotifyApi {
    @Autowired
    private ChatBotService chatBotService;
    @Autowired
    private ChatbotManageService chatbotManageService;
    @Resource
    private MessageTemplateApi messageTemplateApi;
    /**
     * ！！！！！！！！！！！！重要 重要 重要 ！！！！！！！！！！！！！！！
     * 为移动回调controller单独处理业务异常，捕获biz异常，将错误码和提示信息转换成移动能识别的方式
     */
    @ExceptionHandler(BizException.class)
    public Map<String, String> bizExceptionHandler(BizException bizException) {
        log.error("处理移动CSP回调失败,code:{},msg:{}", bizException.getCode(), bizException.getMsg());
        HashMap<String, String> map = new HashMap<>();
        map.put("resultCode", String.valueOf(bizException.getCode()));
        map.put("resultDesc", bizException.getMsg());
        return map;
    }

    @PostMapping("/oc/syncchatbot")
    @Override
    public Map<String, String> syncChatBot(@RequestBody ChatBotSyncInfo chatBotSyncInfo) {
        log.info("忽略移动同步chatbot信息：{}", chatBotSyncInfo);
        return ChatBotService.syncReturnSuccess();
    }

    @PostMapping("/oc/syncproduct")
    @Override
    public Map<String, String> syncProduct(@RequestBody ServiceCode serviceCode) {
        log.info("接收移动同步订购信息：{}", serviceCode);
        return chatBotService.syncProduct(serviceCode);
    }

    @PostMapping("/oc/cancel")
    @Override
    public Map<String, String> chatBotCancel(@RequestBody ChatBotInfo chatBotInfo) {
        log.info("接收移动同步chatbot注销：{}", chatBotInfo);
        String[] split = chatBotInfo.getChatbotId().split("@");
        String chatbotId = split[0];
        //取消该chatbot的审核记录
        messageTemplateApi.cancelAudit(chatbotId);

        chatbotManageService.cancelCMCCChatbot(chatbotId);
        return ChatBotService.syncReturnSuccess();
    }

    @PostMapping("/oc/v1/cancel")
    @Override
    public Map<String, String> chatBotV1Cancel(@RequestBody ChatBotInfo chatBotInfo) {
        log.info("接收移动同步chatbot注销 v1 ：{}", chatBotInfo);
        String[] split = chatBotInfo.getChatbotId().split("@");
        String chatbotAccount = split[0];
        //取消该chatbot的审核记录
        messageTemplateApi.cancelAudit(chatbotAccount);

        chatbotManageService.realCancelCMCCChatbot(chatbotAccount);
        return ChatBotService.syncReturnSuccess();
    }

    @Override
    public Map<String, String> chatBotStatus(@RequestBody ChatBotInfo chatBotInfo) {
        log.info("接收移动同步chatbot状态：{}", chatBotInfo);
        return chatBotService.chatBotStatus(chatBotInfo);
    }

    /**
     * 固定菜单审核结果通知
     *
     * @param authInfo 审核信息
     */
    @PostMapping("/oc/authnotification")
    @Override
    public Map<String, String> authNotification(@RequestBody AuthInfo authInfo) {
        log.info("接收移动同步固定菜单审核结果：{}", authInfo);
        return chatBotService.authNotification(authInfo);
    }

    @PostMapping("/oc/audit")
    @Override
    public Map<String, String> chatBotAudit(@RequestBody ChatBotExamine chatBotExamine) {
        log.info("接收移动同步chatbot审核结果：{}", chatBotExamine);
        return chatBotService.chatBotAudit(chatBotExamine);
    }

    @PostMapping("/oc/syncconfigchatbot")
    @Override
    public Map<String, String> syncConfigChatBot(@RequestBody ChatBotConfigInfo chatBotConfigInfo) {
        log.info("接收移动同步chatbot配置：{}", chatBotConfigInfo);
        return chatBotService.syncConfigChatBot(chatBotConfigInfo);
    }


    @PostMapping("/oc/client/new")
    @Override
    public Map<String, String> clientNew(@RequestBody SignedCustomer signedCustomer) {
        return chatBotService.platClientNew(signedCustomer);
    }


    @PostMapping("/oc/client/change")
    @Override
    public Map<String, String> clientChange(@RequestBody SignedCustomer signedCustomer) {
        return chatBotService.platClientChange(signedCustomer);
    }

    @PostMapping("/oc/client/audit")
    @Override
    public Map<String, String> clientAudit(@RequestBody SignedCustomerExamine signedCustomerExamine) {
        log.info("非直签客户审核结果接口接收回调：{}", signedCustomerExamine);
        return chatBotService.platClientAudit(signedCustomerExamine);
    }


    @PostMapping("/oc/client/status")
    @Override
    public Map<String, String> clientStatus(@RequestBody CustomerStatus customerStatus) {
        log.info("非直签客户状态接口接收平台回调信息：{}", customerStatus);
        return chatBotService.platClientStatus(customerStatus);
    }

    @PostMapping("/oc/client/allotServiceCode")
    @Override
    public Map<String, String> allotServiceCode(@RequestBody AgentServiceCode agentServiceCode) {
        return chatBotService.allotServiceCode(agentServiceCode);
    }
}
